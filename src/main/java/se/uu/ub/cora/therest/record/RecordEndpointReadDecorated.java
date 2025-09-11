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
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import se.uu.ub.cora.data.DataRecord;
import se.uu.ub.cora.spider.dependency.SpiderInstanceProvider;
import se.uu.ub.cora.therest.dependency.TheRestInstanceProvider;
import se.uu.ub.cora.therest.error.ErrorHandler;
import se.uu.ub.cora.therest.url.APIUrls;

@Path("/")
public class RecordEndpointReadDecorated {
	private static final String APPLICATION_VND_CORA_RECORD_DECORATED_XML_QS09 = "application/vnd.cora.record-decorated+xml;qs=0.9";
	private static final String APPLICATION_VND_CORA_RECORD_DECORATED_XML = "application/vnd.cora.record-decorated+xml";
	private static final String APPLICATION_VND_CORA_RECORD_DECORATED_JSON_QS09 = "application/vnd.cora.record-decorated+json;qs=0.9";
	private static final String APPLICATION_VND_CORA_RECORD_DECORATED_JSON = "application/vnd.cora.record-decorated+json";
	HttpServletRequest request;

	public RecordEndpointReadDecorated(@Context HttpServletRequest req) {
		request = req;
	}

	@GET
	@Path("{type}/{id}")
	@Produces(APPLICATION_VND_CORA_RECORD_DECORATED_XML_QS09)
	public Response readDecoratedRecordXml(@HeaderParam("authToken") String authToken,
			@PathParam("type") String type, @PathParam("id") String id) {
		return callReadAndDecorateRecord(APPLICATION_VND_CORA_RECORD_DECORATED_XML, authToken, type,
				id);
	}

	@GET
	@Path("{type}/{id}")
	@Produces({ APPLICATION_VND_CORA_RECORD_DECORATED_JSON_QS09 })
	public Response readDecoratedRecordJson(@HeaderParam("authToken") String authToken,
			@PathParam("type") String type, @PathParam("id") String id) {
		return callReadAndDecorateRecord(APPLICATION_VND_CORA_RECORD_DECORATED_JSON, authToken,
				type, id);
	}

	private Response callReadAndDecorateRecord(String accept, String authToken, String type,
			String id) {
		try {
			return tryReadRecord(request, accept, authToken, type, id);
		} catch (Exception error) {
			return handleError(authToken, type, id, error);
		}
	}

	private Response tryReadRecord(HttpServletRequest request, String accept, String authToken,
			String type, String id) {
		DataRecord dataRecord = readDecoratedRecordFromSpider(authToken, type, id);
		String convertedDataRecord = convertDecoratedRecordToString(request, accept, dataRecord);
		return createResponse(accept, convertedDataRecord);
	}

	private DataRecord readDecoratedRecordFromSpider(String authToken, String type, String id) {
		var decoratedRecordReader = SpiderInstanceProvider.getDecoratedRecordReader();
		return decoratedRecordReader.readDecoratedRecord(authToken, type, id);
	}

	private String convertDecoratedRecordToString(HttpServletRequest request, String accept,
			DataRecord dataRecord) {
		var urlHandler = TheRestInstanceProvider.getUrlHandler();
		APIUrls apiUrls = urlHandler.getAPIUrls(request);

		var endpointConverter = TheRestInstanceProvider.getEndpointOutgoingConverter();
		return endpointConverter.convertConvertibleToString(apiUrls, accept, dataRecord);
	}

	private Response createResponse(String accept, String convertedDataRecord) {
		return Response.status(Response.Status.OK).header(HttpHeaders.CONTENT_TYPE, accept)
				.entity(convertedDataRecord).build();
	}

	private Response handleError(String authToken, String type, String id, Exception error) {
		String errorFromCaller = "Error reading decorated record with recordType: {0} and "
				+ "recordId: {1}.";
		ErrorHandler errorHandler = TheRestInstanceProvider.getErrorHandler();
		return errorHandler.handleError(authToken, error,
				MessageFormat.format(errorFromCaller, type, id));
	}
}
