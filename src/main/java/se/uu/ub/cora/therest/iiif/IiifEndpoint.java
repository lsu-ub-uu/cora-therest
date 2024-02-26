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
import jakarta.ws.rs.OPTIONS;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;
import se.uu.ub.cora.spider.binary.iiif.IiifReader;
import se.uu.ub.cora.spider.dependency.SpiderInstanceProvider;

@Path("/")
public class IiifEndpoint {

	@OPTIONS
	@Path("{identifier}/info.json")
	public Response readOptions(@PathParam("identifier") String identifier) {
		return Response.status(418).build();
	}

	@GET
	@Path("{identifier}/info.json")
	public Response readInformation(@PathParam("identifier") String identifier) {
		return Response.status(418).build();
	}

	@GET
	@Path("{identifier}/{region}/{size}/{rotation}/{quality}.{format}")
	public Response readBinary(@PathParam("identifier") String identifier,
			@PathParam("region") String region, @PathParam("size") String size,
			@PathParam("rotation") String rotation, @PathParam("quality") String quality,
			@PathParam("format") String format) {
		IiifReader iiifReader = SpiderInstanceProvider.getIiifReader();
		// iiifReader.readImage(identifier, region, size, rotation, quality, format)
		iiifReader.readImage(null, null, null, null, null, null);
		return Response.status(418).build();
	}

}
