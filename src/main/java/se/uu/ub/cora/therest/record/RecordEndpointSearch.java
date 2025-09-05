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
import se.uu.ub.cora.converter.ConverterProvider;
import se.uu.ub.cora.converter.StringToExternallyConvertibleConverter;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataList;
import se.uu.ub.cora.data.ExternallyConvertible;
import se.uu.ub.cora.data.converter.JsonToDataConverter;
import se.uu.ub.cora.data.converter.JsonToDataConverterProvider;
import se.uu.ub.cora.json.parser.JsonParser;
import se.uu.ub.cora.json.parser.JsonValue;
import se.uu.ub.cora.json.parser.org.OrgJsonParser;
import se.uu.ub.cora.spider.dependency.SpiderInstanceProvider;
import se.uu.ub.cora.spider.record.RecordSearcher;
import se.uu.ub.cora.therest.dependency.TheRestInstanceProvider;
import se.uu.ub.cora.therest.error.ErrorHandler;
import se.uu.ub.cora.therest.url.APIUrls;

@Path("/")
public class RecordEndpointSearch {
	private static final String APPLICATION_VND_CORA_RECORD_LIST_XML = "application/vnd.cora.recordList+xml";
	private static final String APPLICATION_VND_CORA_RECORD_LIST_JSON = "application/vnd.cora.recordList+json";
	private static final String APPLICATION_VND_CORA_RECORD_LIST_JSON_QS09 = "application/vnd.cora.recordList+json;qs=0.9";
	HttpServletRequest request;

	private JsonParser jsonParser = new OrgJsonParser();

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

		RecordSearcher recordSearcher = SpiderInstanceProvider.getRecordSearcher();
		DataList searchRecordList = recordSearcher.search(authToken, searchId, searchData);

		String outputRecord = convertRecordListToString(request, accept, searchRecordList);

		return Response.status(Response.Status.OK).header(HttpHeaders.CONTENT_TYPE, accept)
				.entity(outputRecord).build();
	}

	private DataGroup convertSearchStringToData(String searchDataAsString) {
		String searchDataType = calculateSearchDataType(searchDataAsString);
		return convertStringToDataGroup(searchDataType, searchDataAsString);
	}

	private String calculateSearchDataType(String searchDataAsString) {
		if (searchDataAsString.startsWith("<")) {
			return "+xml";
		}
		return "+json";
	}

	private DataGroup convertStringToDataGroup(String accept, String input) {
		if (accept.endsWith("+xml")) {
			return convertXmlToDataElement(input);
		} else {
			return convertJsonStringToDataGroup(input);
		}
	}

	private DataGroup convertXmlToDataElement(String input) {
		StringToExternallyConvertibleConverter xmlToConvertibleConverter = ConverterProvider
				.getStringToExternallyConvertibleConverter("xml");
		return (DataGroup) xmlToConvertibleConverter.convert(input);
	}

	private DataGroup convertJsonStringToDataGroup(String jsonRecord) {
		JsonValue jsonValue = jsonParser.parseString(jsonRecord);
		JsonToDataConverter jsonToDataConverter = JsonToDataConverterProvider
				.getConverterUsingJsonObject(jsonValue);
		return (DataGroup) jsonToDataConverter.toInstance();
	}

	private String convertRecordListToString(HttpServletRequest request, String accept,
			ExternallyConvertible exConvertible) {
		var urlHandler = TheRestInstanceProvider.getUrlHandler();
		APIUrls apiUrls = urlHandler.getAPIUrls(request);

		var endpointConverter = TheRestInstanceProvider.getEndpointConverter();
		return endpointConverter.convertConvertibleToString(apiUrls, accept, exConvertible);
	}

	JsonParser getJsonParser() {
		return jsonParser;
	}

	void setJsonParser(JsonParser jsonParser) {
		this.jsonParser = jsonParser;
	}
}
