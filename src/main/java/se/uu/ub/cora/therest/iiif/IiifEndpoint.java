package se.uu.ub.cora.therest.iiif;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;

@Path("/")
public class IiifEndpoint {

	@GET
	@Path("{identifier}/{region}/{size}/{rotation}/{quality}.{format}")
	public Response readBinary(@PathParam("identifier") String identifier,
			@PathParam("region") String region, @PathParam("size") String size,
			@PathParam("rotation") String rotation, @PathParam("quality") String quality,
			@PathParam("format") String format) {
		return Response.status(418).build();
	}

}
