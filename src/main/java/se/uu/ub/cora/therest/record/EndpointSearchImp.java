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
import java.util.function.Function;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataList;
import se.uu.ub.cora.data.ExternallyConvertible;
import se.uu.ub.cora.spider.dependency.SpiderInstanceProvider;
import se.uu.ub.cora.spider.record.RecordSearcher;
import se.uu.ub.cora.spider.record.RecordSearcherDecorated;
import se.uu.ub.cora.therest.converter.EndpointIncomingConverter;
import se.uu.ub.cora.therest.dependency.TheRestInstanceProvider;
import se.uu.ub.cora.therest.error.ErrorHandler;
import se.uu.ub.cora.therest.url.APIUrls;

public class EndpointSearchImp implements EndpointSearch {

	@Override
	public Response searchRecord(HttpServletRequest request, String contentTypeOut,
			String headerAuthToken, String queryAuthToken, String searchId,
			String searchDataAsString) {

		String authToken = getExistingTokenPreferHeader(headerAuthToken, queryAuthToken);
		return trySearch(request, contentTypeOut, authToken, searchId, searchDataAsString,
				callSearch(searchId, authToken));
	}

	private Function<DataGroup, DataList> callSearch(String searchId, String authToken) {
		return searchData -> {
			RecordSearcher recordSearcher = SpiderInstanceProvider.getRecordSearcher();
			return recordSearcher.search(authToken, searchId, searchData);
		};
	}

	@Override
	public Response searchRecordDecorated(HttpServletRequest request, String contentTypeOut,
			String headerAuthToken, String queryAuthToken, String searchId,
			String searchDataAsString) {

		String authToken = getExistingTokenPreferHeader(headerAuthToken, queryAuthToken);
		return trySearch(request, contentTypeOut, authToken, searchId, searchDataAsString,
				callSearchDecorated(searchId, authToken));
	}

	private Function<DataGroup, DataList> callSearchDecorated(String searchId, String authToken) {
		return searchData -> {
			RecordSearcherDecorated recordSearcher = SpiderInstanceProvider
					.getRecordSearcherDecorated();
			return recordSearcher.searchDecorated(authToken, searchId, searchData);
		};
	}

	private String getExistingTokenPreferHeader(String headerAuthToken, String queryAuthToken) {
		return headerAuthToken != null ? headerAuthToken : queryAuthToken;
	}

	private Response trySearch(HttpServletRequest request, String contentTypeOut, String authToken,
			String searchId, String searchDataAsString,
			Function<DataGroup, DataList> genericSearcher) {
		try {
			return searchUsingSearchDataAndCreateResponse(request, contentTypeOut,
					searchDataAsString, genericSearcher);
		} catch (Exception error) {
			return handleError(authToken, searchId, error);
		}
	}

	private Response searchUsingSearchDataAndCreateResponse(HttpServletRequest request,
			String contentTypeOut, String searchDataAsString,
			Function<DataGroup, DataList> generidSearcher) {
		DataGroup searchData = convertSearchStringToData(searchDataAsString);
		DataList searchResult = generidSearcher.apply(searchData);
		String outputRecord = convertRecordListToString(request, contentTypeOut, searchResult);
		return createResponse(contentTypeOut, outputRecord);
	}

	private Response handleError(String authToken, String searchId, Exception error) {
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

	private String convertRecordListToString(HttpServletRequest request, String accept,
			ExternallyConvertible exConvertible) {
		var urlHandler = TheRestInstanceProvider.getUrlHandler();
		APIUrls apiUrls = urlHandler.getAPIUrls(request);

		var endpointConverter = TheRestInstanceProvider.getEndpointOutgoingConverter();
		return endpointConverter.convertConvertibleToString(apiUrls, accept, exConvertible);
	}

	private Response createResponse(String contentType, String outputRecord) {
		return Response.status(Response.Status.OK).header(HttpHeaders.CONTENT_TYPE, contentType)
				.entity(outputRecord).build();
	}
}
