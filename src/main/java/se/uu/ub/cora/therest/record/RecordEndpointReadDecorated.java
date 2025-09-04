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
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import se.uu.ub.cora.therest.dependency.TheRestInstanceProvider;

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

	private Response callReadAndDecorateRecord(String accept, String authToken, String type,
			String id) {
		EndpointDecoratedReader decoratedReader = TheRestInstanceProvider.getDecoratedReader();
		return decoratedReader.readAndDecorateRecord(request, accept, authToken, type, id);
	}

	@GET
	@Path("{type}/{id}")
	@Produces({ APPLICATION_VND_CORA_RECORD_DECORATED_JSON_QS09 })
	public Response readDecoratedRecordJson(@HeaderParam("authToken") String authToken,
			@PathParam("type") String type, @PathParam("id") String id) {
		return callReadAndDecorateRecord(APPLICATION_VND_CORA_RECORD_DECORATED_JSON, authToken,
				type, id);
	}

}
