/*
 * Copyright 2024 Uppsala University Library
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
package se.uu.ub.cora.therest.iiif;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Request;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.ResponseBuilder;
import se.uu.ub.cora.spider.authentication.AuthenticationException;
import se.uu.ub.cora.spider.authorization.AuthorizationException;
import se.uu.ub.cora.spider.binary.iiif.IiifReader;
import se.uu.ub.cora.spider.binary.iiif.IiifResponse;
import se.uu.ub.cora.spider.dependency.SpiderInstanceProvider;
import se.uu.ub.cora.spider.record.RecordNotFoundException;

@Path("/")
public class IiifEndpoint {
	private static final String AUTH_TOKEN = "authtoken";
	private static final Set<String> restrictedHeaders = Set.of("access-control-request-headers",
			"access-control-request-method", "connection", "content-length",
			"content-transfer-encoding", "host", "keep-alive", "origin", "trailer",
			"transfer-encoding", "upgrade", "via");

	@GET
	@Path("{identifier}/{requestedUri:.*}")
	public Response readIiif(@Context HttpHeaders headers, @Context Request request,
			@PathParam("identifier") String identifier,
			@PathParam("requestedUri") String requestedUri) {

		try {
			return tryToReadIiif(headers, request, identifier, requestedUri);
		} catch (AuthorizationException | AuthenticationException e) {
			// } catch (AuthorizationException e) {
			if (authTokenExistInHeaders(headers)) {
				return Response.status(Response.Status.FORBIDDEN).build();
			}
			return Response.status(Response.Status.UNAUTHORIZED).build();
		} catch (RecordNotFoundException e) {
			return Response.status(Response.Status.NOT_FOUND).build();
		} catch (Exception e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}
	}

	private boolean authTokenExistInHeaders(HttpHeaders headers) {
		return headers.getRequestHeader(AUTH_TOKEN) != null;
	}

	private Response tryToReadIiif(HttpHeaders headers, Request request, String identifier,
			String requestedUri) {
		IiifResponse iiifResponse = readResponseFromIiif(headers, request, identifier,
				requestedUri);
		return createJakartaResponseFromIiifResponse(iiifResponse);
	}

	private IiifResponse readResponseFromIiif(HttpHeaders headers, Request request,
			String identifier, String requestedUri) {
		Map<String, String> headersMap = getHeadersAsMapWithCombinedValues(headers);
		IiifReader iiifReader = SpiderInstanceProvider.getIiifReader();
		return iiifReader.readIiif(identifier, requestedUri, request.getMethod(), headersMap);
	}

	private Map<String, String> getHeadersAsMapWithCombinedValues(HttpHeaders headers) {
		MultivaluedMap<String, String> requestHeaders = headers.getRequestHeaders();
		Map<String, String> headersMap = new HashMap<>();
		for (Entry<String, List<String>> header : requestHeaders.entrySet()) {
			possiblyTransformAndAddHeaderToCompoundValuesIfHeaderIsAllowed(headersMap, header);
		}
		return headersMap;
	}

	private void possiblyTransformAndAddHeaderToCompoundValuesIfHeaderIsAllowed(
			Map<String, String> headersMap, Entry<String, List<String>> header) {
		if (headerAllowed(header.getKey())) {
			String compoundValues = String.join(", ", header.getValue());
			headersMap.put(header.getKey(), compoundValues);
		}
	}

	private boolean headerAllowed(String headerName) {
		return !(restrictedHeaders.contains(headerName.toLowerCase()));
	}

	private Response createJakartaResponseFromIiifResponse(IiifResponse iiifResponse) {
		ResponseBuilder responseBuilder = createResponseFromIiifStatus(iiifResponse.status());
		addHeadersToBuilderFromIiifHeaders(responseBuilder, iiifResponse.headers());
		addBodyToBuiderFromIiifBody(responseBuilder, iiifResponse.body());
		return responseBuilder.build();
	}

	private ResponseBuilder createResponseFromIiifStatus(int status) {
		return Response.status(status);
	}

	private void addHeadersToBuilderFromIiifHeaders(ResponseBuilder responseBuilder,
			Map<String, String> headers) {
		for (Entry<String, String> header : headers.entrySet()) {
			possiblyAddHeaderIfAllowedToResponseBuilder(responseBuilder, header);
		}
	}

	private void possiblyAddHeaderIfAllowedToResponseBuilder(ResponseBuilder responseBuilder,
			Entry<String, String> header) {
		if (headerAllowed(header.getKey())) {
			responseBuilder.header(header.getKey(), header.getValue());
		}
	}

	private ResponseBuilder addBodyToBuiderFromIiifBody(ResponseBuilder responseBuilder,
			Object body) {
		return responseBuilder.entity(body);
	}
}
