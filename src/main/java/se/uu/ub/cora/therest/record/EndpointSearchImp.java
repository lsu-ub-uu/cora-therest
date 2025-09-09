/*
 * Copyright 2025 Uppsala University Library
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
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataList;
import se.uu.ub.cora.data.ExternallyConvertible;
import se.uu.ub.cora.spider.record.RecordSearcher;
import se.uu.ub.cora.therest.converter.EndpointIncomingConverter;
import se.uu.ub.cora.therest.dependency.TheRestInstanceProvider;
import se.uu.ub.cora.therest.error.ErrorHandler;
import se.uu.ub.cora.therest.url.APIUrls;

public class EndpointSearchImp implements EndpointSearch {

	private RecordSearcher recordSearcher;
	private HttpServletRequest request;
	private String contentTypeOut;
	private String searchId;
	private String authToken;

	public EndpointSearchImp(RecordSearcher recordSearcher) {
		this.recordSearcher = recordSearcher;
	}

	@Override
	public Response searchRecord(HttpServletRequest request, String contentTypeOut,
			String headerAuthToken, String queryAuthToken, String searchId,
			String searchDataAsString) {
		this.request = request;
		this.contentTypeOut = contentTypeOut;
		this.searchId = searchId;

		authToken = getExistingTokenPreferHeader(headerAuthToken, queryAuthToken);
		return trySearch(searchDataAsString);
	}

	private String getExistingTokenPreferHeader(String headerAuthToken, String queryAuthToken) {
		return headerAuthToken != null ? headerAuthToken : queryAuthToken;
	}

	private Response trySearch(String searchDataAsString) {
		try {
			return searchUsingSearchDataAndCreateResponse(searchDataAsString);
		} catch (Exception error) {
			return handleError(error);
		}
	}

	private Response searchUsingSearchDataAndCreateResponse(String searchDataAsString) {
		DataGroup searchData = convertSearchStringToData(searchDataAsString);
		DataList searchResult = recordSearcher.search(authToken, searchId, searchData);
		String outputRecord = convertRecordListToString(searchResult);
		return createResponse(outputRecord);
	}

	private Response handleError(Exception error) {
		String errorFromCaller = "Error searching record with searchId: {0}.";
		ErrorHandler errorHandler = TheRestInstanceProvider.getErrorHandler();
		return errorHandler.handleError(authToken, error,
				MessageFormat.format(errorFromCaller, searchId));
	}

	private DataGroup convertSearchStringToData(String searchDataAsString) {
		EndpointIncomingConverter endpointIncomingConverter = TheRestInstanceProvider
				.getEndpointIncomingConverter();
		return (DataGroup) endpointIncomingConverter.convertStringToConvertible(searchDataAsString);
	}

	private String convertRecordListToString(ExternallyConvertible exConvertible) {
		var urlHandler = TheRestInstanceProvider.getUrlHandler();
		APIUrls apiUrls = urlHandler.getAPIUrls(request);

		var endpointConverter = TheRestInstanceProvider.getEndpointOutgoingConverter();
		return endpointConverter.convertConvertibleToString(apiUrls, contentTypeOut, exConvertible);
	}

	private Response createResponse(String outputRecord) {
		return Response.status(Response.Status.OK).header(HttpHeaders.CONTENT_TYPE, contentTypeOut)
				.entity(outputRecord).build();
	}

	public RecordSearcher onlyForTestGetRecordSearcher() {
		return recordSearcher;
	}
}
