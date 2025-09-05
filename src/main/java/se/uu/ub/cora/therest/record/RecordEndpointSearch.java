/*
 * Copyright 2015, 2016, 2018, 2021, 2024, 2025 Uppsala University Library
 *
 * This file is part of Cora.
 *
 *     Cora is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Cora is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Cora.  If not, see <http://www.gnu.org/licenses/>.
 */

package se.uu.ub.cora.therest.record;

import java.text.MessageFormat;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataList;
import se.uu.ub.cora.data.ExternallyConvertible;
import se.uu.ub.cora.spider.dependency.SpiderInstanceProvider;
import se.uu.ub.cora.spider.record.RecordSearcher;
import se.uu.ub.cora.therest.converter.EndpointIncomingConverter;
import se.uu.ub.cora.therest.dependency.TheRestInstanceProvider;
import se.uu.ub.cora.therest.error.ErrorHandler;
import se.uu.ub.cora.therest.url.APIUrls;

@Path("/")
public class RecordEndpointSearch {
	private static final String APPLICATION_VND_CORA_RECORD_LIST_XML = "application/vnd.cora.recordList+xml";
	private static final String APPLICATION_VND_CORA_RECORD_LIST_JSON = "application/vnd.cora.recordList+json";
	private static final String APPLICATION_VND_CORA_RECORD_LIST_JSON_QS09 = "application/vnd.cora.recordList+json;qs=0.9";

	private static final String APPLICATION_VND_CORA_RECORD_LIST_DECORATED_XML = "application/vnd.cora.recordList-decorated+xml";
	private static final String APPLICATION_VND_CORA_RECORD_LIST_DECORATED_JSON_QS09 = "application/vnd.cora.recordList-decorated+json;qs=0.9";

	HttpServletRequest request;

	public RecordEndpointSearch(@Context HttpServletRequest req) {
		request = req;
	}

	@GET
	@Path("searchResult/{searchId}")
	@Produces({ APPLICATION_VND_CORA_RECORD_LIST_JSON_QS09 })
	public Response searchRecordJson(@HeaderParam("authToken") String headerAuthToken,
			@QueryParam("authToken") String queryAuthToken, @PathParam("searchId") String searchId,
			@QueryParam("searchData") String searchDataAsString) {
		return searchRecord(APPLICATION_VND_CORA_RECORD_LIST_JSON, headerAuthToken, queryAuthToken,
				searchId, searchDataAsString);
	}

	@GET
	@Path("searchResult/{searchId}")
	@Produces(APPLICATION_VND_CORA_RECORD_LIST_XML)
	public Response searchRecordXml(@HeaderParam("authToken") String headerAuthToken,
			@QueryParam("authToken") String queryAuthToken, @PathParam("searchId") String searchId,
			@QueryParam("searchData") String searchDataAsString) {
		return searchRecord(APPLICATION_VND_CORA_RECORD_LIST_XML, headerAuthToken, queryAuthToken,
				searchId, searchDataAsString);
	}

	@GET
	@Path("searchResult/{searchId}")
	@Produces({ APPLICATION_VND_CORA_RECORD_LIST_DECORATED_JSON_QS09 })
	public Response searchRecordDecoratedJson(@HeaderParam("authToken") String headerAuthToken,
			@QueryParam("authToken") String queryAuthToken, @PathParam("searchId") String searchId,
			@QueryParam("searchData") String searchDataAsString) {
		// return searchRecord(APPLICATION_VND_CORA_RECORD_LIST_JSON, headerAuthToken,
		// queryAuthToken,
		// searchId, searchDataAsString);
		return null;
	}

	@GET
	@Path("searchResult/{searchId}")
	@Produces({ APPLICATION_VND_CORA_RECORD_LIST_DECORATED_XML })
	public Response searchRecordDecoratedXml(@HeaderParam("authToken") String headerAuthToken,
			@QueryParam("authToken") String queryAuthToken, @PathParam("searchId") String searchId,
			@QueryParam("searchData") String searchDataAsString) {
		// return searchRecord(APPLICATION_VND_CORA_RECORD_LIST_JSON, headerAuthToken,
		// queryAuthToken,
		// searchId, searchDataAsString);
		return null;
	}

	private Response searchRecord(String accept, String headerAuthToken, String queryAuthToken,
			String searchId, String searchDataAsString) {
		String usedToken = getExistingTokenPreferHeader(headerAuthToken, queryAuthToken);
		return searchRecordUsingAuthTokenBySearchData(accept, usedToken, searchId,
				searchDataAsString);
	}

	private String getExistingTokenPreferHeader(String headerAuthToken, String queryAuthToken) {
		return headerAuthToken != null ? headerAuthToken : queryAuthToken;
	}

	private Response searchRecordUsingAuthTokenBySearchData(String accept, String authToken,
			String searchId, String searchDataAsString) {
		try {
			return trySearchRecord(accept, authToken, searchId, searchDataAsString);
		} catch (Exception error) {
			return handleError(authToken, searchId, error);
		}
	}

	private Response handleError(String authToken, String searchId, Exception error) {
		String errorFromCaller = "Error searching record with searchId: {0}.";
		ErrorHandler errorHandler = TheRestInstanceProvider.getErrorHandler();
		return errorHandler.handleError(authToken, error,
				MessageFormat.format(errorFromCaller, searchId));
	}

	private Response trySearchRecord(String accept, String authToken, String searchId,
			String searchDataAsString) {
		DataGroup searchData = convertSearchStringToData(searchDataAsString);
		DataList searchRecordList = search(authToken, searchId, searchData);
		String outputRecord = convertRecordListToString(request, accept, searchRecordList);
		return createResponse(accept, outputRecord);
	}

	private DataGroup convertSearchStringToData(String searchDataAsString) {
		EndpointIncomingConverter endpointIncomingConverter = TheRestInstanceProvider
				.getEndpointIncomingConverter();
		return (DataGroup) endpointIncomingConverter.convertStringToConvertible(searchDataAsString);
	}

	private DataList search(String authToken, String searchId, DataGroup searchData) {
		RecordSearcher recordSearcher = SpiderInstanceProvider.getRecordSearcher();
		return recordSearcher.search(authToken, searchId, searchData);
	}

	private String convertRecordListToString(HttpServletRequest request, String accept,
			ExternallyConvertible exConvertible) {
		var urlHandler = TheRestInstanceProvider.getUrlHandler();
		APIUrls apiUrls = urlHandler.getAPIUrls(request);

		var endpointConverter = TheRestInstanceProvider.getEndpointOutgoingConverter();
		return endpointConverter.convertConvertibleToString(apiUrls, accept, exConvertible);
	}

	private Response createResponse(String accept, String outputRecord) {
		return Response.status(Response.Status.OK).header(HttpHeaders.CONTENT_TYPE, accept)
				.entity(outputRecord).build();
	}
}
