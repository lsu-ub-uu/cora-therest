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

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Request;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.ResponseBuilder;
import se.uu.ub.cora.spider.authorization.AuthorizationException;
import se.uu.ub.cora.spider.binary.iiif.IiifReader;
import se.uu.ub.cora.spider.binary.iiif.IiifResponse;
import se.uu.ub.cora.spider.dependency.SpiderInstanceProvider;
import se.uu.ub.cora.spider.record.RecordNotFoundException;

@Path("/")
public class IiifEndpoint {

	// @OPTIONS
	// @Path("{identifier}/info.json")
	// public Response readOptions(@PathParam("identifier") String identifier) {
	// return Response.status(418).build();
	// }
	//
	// @GET
	// @Path("{identifier}/info.json")
	// public Response readInformation(@PathParam("identifier") String identifier) {
	// return Response.status(418).build();
	// }
	//
	// @GET
	// @Path("{identifier}/{region}/{size}/{rotation}/{quality}.{format}")
	// public Response readBinary(@PathParam("identifier") String identifier,
	// @PathParam("region") String region, @PathParam("size") String size,
	// @PathParam("rotation") String rotation, @PathParam("quality") String quality,
	// @PathParam("format") String format) {
	// IiifReader iiifReader = SpiderInstanceProvider.getIiifReader();
	// // iiifReader.readImage(identifier, region, size, rotation, quality, format)
	// iiifReader.readImage(null, null, null, null, null, null);
	// return Response.status(418).build();
	// }

	@GET
	@Path("{identifier}/{requestedUri:.*}")
	public Response readIiif(@Context HttpHeaders headers, @Context Request request,
			@PathParam("identifier") String identifier,
			@PathParam("requestedUri") String requestedUri) {

		// System.out.println("id: " + identifier);
		// System.out.println("path: " + requestedUri);
		// System.out.println("headers: " + headers.getRequestHeaders());
		// System.out.println("Method: " + request.getMethod());

		try {
			return tryToReadIiif(headers, request, identifier, requestedUri);
		} catch (AuthorizationException e) {
			return Response.status(Response.Status.UNAUTHORIZED).build();
		} catch (RecordNotFoundException e) {
			return Response.status(Response.Status.NOT_FOUND).build();
		} catch (Exception e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}

		// return Response.status(418).build(json);
		// return Response.status(Response.Status.OK).header(HttpHeaders.CONTENT_TYPE, accept)
		// .entity(convertedDataRecord).build();
	}

	// String json = """
	// {
	// "@context" : "http://iiif.io/api/image/3/context.json",
	// "protocol" : "http://iiif.io/api/image",
	// "width" : 2653,
	// "height" : 3419,
	// "sizes" : [
	// { "width" : 165, "height" : 213 },
	// { "width" : 331, "height" : 427 },
	// { "width" : 663, "height" : 854 },
	// { "width" : 1326, "height" : 1709 }
	// ],
	// "tiles" : [
	// { "width" : 256, "height" : 256, "scaleFactors" : [ 1, 2, 4, 8, 16 ] }
	// ],
	// "id" : "http://localhost:39080Kalle/iiif/systemOne/binary:binary:3219013273077247",
	// "type": "ImageService3",
	// "profile" : "level2",
	// "maxWidth" : 5000,
	// "maxHeight" : 5000,
	// "extraQualities": ["color","gray","bitonal"],
	// "extraFormats": ["webp"],
	// "extraFeatures":
	// ["regionByPct","sizeByForcedWh","sizeByWh","sizeAboveFull","sizeUpscaling","rotationBy90s","mirroring"]
	// }
	// """;

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
		for (Entry<String, List<String>> entry : requestHeaders.entrySet()) {
			String compoundValues = String.join(", ", entry.getValue());
			headersMap.put(entry.getKey(), compoundValues);
		}
		return headersMap;
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
			Map<String, Object> headers) {
		for (Entry<String, Object> header : headers.entrySet()) {
			responseBuilder.header(header.getKey(), header.getValue());
		}
	}

	private ResponseBuilder addBodyToBuiderFromIiifBody(ResponseBuilder responseBuilder,
			Object body) {
		return responseBuilder.entity(body);
	}
}
