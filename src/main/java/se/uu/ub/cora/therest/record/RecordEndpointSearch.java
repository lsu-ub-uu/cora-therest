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

import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import se.uu.ub.cora.therest.dependency.TheRestInstanceProvider;

@Path("/")
public class RecordEndpointSearch {
	private static final String APPLICATION_VND_CORA_RECORD_LIST_XML = "application/vnd.cora.recordList+xml";
	private static final String APPLICATION_VND_CORA_RECORD_LIST_JSON = "application/vnd.cora.recordList+json";
	private static final String APPLICATION_VND_CORA_RECORD_LIST_JSON_QS09 = "application/vnd.cora.recordList+json;qs=0.9";

	private static final String APPLICATION_VND_CORA_RECORD_LIST_DECORATED_XML_QS09 = "application/vnd.cora.recordList-decorated+xml;qs=0.9";
	private static final String APPLICATION_VND_CORA_RECORD_LIST_DECORATED_JSON_QS09 = "application/vnd.cora.recordList-decorated+json;qs=0.9";
	private static final String APPLICATION_VND_CORA_RECORD_LIST_DECORATED_XML = "application/vnd.cora.recordList-decorated+xml";
	private static final String APPLICATION_VND_CORA_RECORD_LIST_DECORATED_JSON = "application/vnd.cora.recordList-decorated+json";

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

		EndpointSearch endpointSearch = TheRestInstanceProvider.getEndpointSearch();
		return endpointSearch.searchRecord(request, APPLICATION_VND_CORA_RECORD_LIST_JSON,
				headerAuthToken, queryAuthToken, searchId, searchDataAsString);
	}

	@GET
	@Path("searchResult/{searchId}")
	@Produces(APPLICATION_VND_CORA_RECORD_LIST_XML)
	public Response searchRecordXml(@HeaderParam("authToken") String headerAuthToken,
			@QueryParam("authToken") String queryAuthToken, @PathParam("searchId") String searchId,
			@QueryParam("searchData") String searchDataAsString) {

		EndpointSearch endpointSearch = TheRestInstanceProvider.getEndpointSearch();
		return endpointSearch.searchRecord(request, APPLICATION_VND_CORA_RECORD_LIST_XML,
				headerAuthToken, queryAuthToken, searchId, searchDataAsString);
	}

	@GET
	@Path("searchResult/{searchId}")
	@Produces({ APPLICATION_VND_CORA_RECORD_LIST_DECORATED_JSON_QS09 })
	public Response searchRecordDecoratedJson(@HeaderParam("authToken") String headerAuthToken,
			@QueryParam("authToken") String queryAuthToken, @PathParam("searchId") String searchId,
			@QueryParam("searchData") String searchDataAsString) {

		EndpointSearch endpointSearch = TheRestInstanceProvider.getEndpointSearchDecorated();
		return endpointSearch.searchRecord(request, APPLICATION_VND_CORA_RECORD_LIST_DECORATED_JSON,
				headerAuthToken, queryAuthToken, searchId, searchDataAsString);
	}

	@GET
	@Path("searchResult/{searchId}")
	@Produces({ APPLICATION_VND_CORA_RECORD_LIST_DECORATED_XML_QS09 })
	public Response searchRecordDecoratedXml(@HeaderParam("authToken") String headerAuthToken,
			@QueryParam("authToken") String queryAuthToken, @PathParam("searchId") String searchId,
			@QueryParam("searchData") String searchDataAsString) {

		EndpointSearch endpointSearch = TheRestInstanceProvider.getEndpointSearchDecorated();
		return endpointSearch.searchRecord(request, APPLICATION_VND_CORA_RECORD_LIST_DECORATED_XML,
				headerAuthToken, queryAuthToken, searchId, searchDataAsString);
	}
}
