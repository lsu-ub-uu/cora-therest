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

package se.uu.ub.cora.therest.deployment;

import java.net.URI;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import se.uu.ub.cora.initialize.SettingsProvider;
import se.uu.ub.cora.therest.dependency.TheRestInstanceProvider;
import se.uu.ub.cora.therest.url.UrlHandler;

@Path("/")
public class RecordEndpointDeployment {
	private static final String APPLICATION_VND_CORA_DEPLOYMENT_INFO_JSON = ""
			+ "application/vnd.cora.deploymentInfo+json";
	HttpServletRequest request;
	private String restUrl;
	private String iiifUrl;

	public RecordEndpointDeployment(@Context HttpServletRequest req) {
		request = req;
		UrlHandler urlHandler = TheRestInstanceProvider.getUrlHandler();
		restUrl = urlHandler.getRestUrl(req);
		iiifUrl = urlHandler.getIiifUrl(req);
	}

	@GET
	@Path("")
	@Produces(MediaType.TEXT_HTML)
	public Response forwardToHtmlDocumentation() {
		return Response.temporaryRedirect(URI.create(restUrl + "index.html")).build();
	}

	@GET
	@Path("")
	@Produces({ APPLICATION_VND_CORA_DEPLOYMENT_INFO_JSON + ";qs=0.1" })
	public Response getDeploymentInfoJson() {
		String name = SettingsProvider.getSetting("deploymentName");
		String family = SettingsProvider.getSetting("deploymentFamily");
		String coraVersion = SettingsProvider.getSetting("deploymentCoraVersion");
		String version = SettingsProvider.getSetting("deploymentVersion");

		String deployment = """
				{
					"name": "%s",
					"family": "%s",
					"coraVersion": "%s",
					"version": "%s",
					"urls": {
						"REST": "%s",
						"loginAppToken": "login/apptoken",
						"loginPassword": "login/password",
						"idpLogin": "password",
						"records":"/record"
					},
					"exampleUsers":[
						{
							"name": "systemoneAdmin",
							"text": "appToken for systemoneAdmin",
							"type": "appTokenLogin",
							"loginId": "systemoneAdmin@system.cora.uu.se",
							"appToken": "5d3f3ed4-4931-4924-9faa-8eaf5ac6457e"
						}
					]
				}
				""";
		Object[] arguments = { name, family, coraVersion, version, restUrl, iiifUrl };

		return Response.status(Response.Status.OK)
				.header(HttpHeaders.CONTENT_TYPE, APPLICATION_VND_CORA_DEPLOYMENT_INFO_JSON)
				.entity(deployment.formatted(arguments)).build();
	}
}
