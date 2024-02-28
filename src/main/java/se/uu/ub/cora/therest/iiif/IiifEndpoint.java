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

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Request;
import jakarta.ws.rs.core.Response;
import se.uu.ub.cora.spider.binary.iiif.IiifImageReader;
import se.uu.ub.cora.spider.dependency.SpiderInstanceProvider;

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

		IiifImageReader iiifReader = SpiderInstanceProvider.getIiifReader();
		iiifReader.readIiif(identifier, requestedUri, request.getMethod(), null);

		// System.out.println("id: " + identifier);
		// System.out.println("path: " + requestedUri);
		// System.out.println("headers: " + headers.getRequestHeaders());
		// System.out.println("Method: " + request.getMethod());
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

		// return Response.status(418).build(json);
		// return Response.status(Response.Status.OK).header(HttpHeaders.CONTENT_TYPE, accept)
		// .entity(convertedDataRecord).build();
		return Response.status(Response.Status.OK).build();
	}
}
