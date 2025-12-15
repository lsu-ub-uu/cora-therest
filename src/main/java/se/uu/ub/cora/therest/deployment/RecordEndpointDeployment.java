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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import se.uu.ub.cora.data.Data;
import se.uu.ub.cora.data.DataList;
import se.uu.ub.cora.data.DataProvider;
import se.uu.ub.cora.data.DataRecord;
import se.uu.ub.cora.data.DataRecordGroup;
import se.uu.ub.cora.initialize.SettingsProvider;
import se.uu.ub.cora.spider.dependency.SpiderInstanceProvider;
import se.uu.ub.cora.therest.dependency.TheRestInstanceProvider;
import se.uu.ub.cora.therest.url.UrlHandler;

@Path("/")
public class RecordEndpointDeployment {
	private static final String APPLICATION_VND_CORA_DEPLOYMENT_INFO_JSON = ""
			+ "application/vnd.cora.deploymentInfo+json";
	private static final String APPLICATION_VND_CORA_DEPLOYMENT_INFO_XML = ""
			+ "application/vnd.cora.deploymentInfo+xml";
	HttpServletRequest request;
	private String restUrl;

	public RecordEndpointDeployment(@Context HttpServletRequest req) {
		request = req;
		UrlHandler urlHandler = TheRestInstanceProvider.getUrlHandler();
		restUrl = urlHandler.getRestUrl(req);
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
		DeploymentInfo deploymentInfo = new DeploymentInfo(request);

		return Response.status(Response.Status.OK)
				.header(HttpHeaders.CONTENT_TYPE, APPLICATION_VND_CORA_DEPLOYMENT_INFO_JSON)
				.entity(deploymentInfo.toJson()).build();
	}

	@GET
	@Path("")
	@Produces({ APPLICATION_VND_CORA_DEPLOYMENT_INFO_XML + ";qs=0.1" })
	public Response getDeploymentInfoXml() {
		DeploymentInfo deploymentInfo = new DeploymentInfo(request);

		return Response.status(Response.Status.OK)
				.header(HttpHeaders.CONTENT_TYPE, APPLICATION_VND_CORA_DEPLOYMENT_INFO_XML)
				.entity(deploymentInfo.toXml()).build();
	}

	private static class DeploymentInfo {
		private final String applicationName;
		private final String deploymentName;
		private final String coraVersion;
		private final String applicationVersion;
		private final String loginRestApptokenUrl;
		private final String loginRestPasswordUrl;
		private final String restUrl;
		private final String restRecordUrl;
		private final String restRecordTypeUrl;
		private final String iiifUrl;

		record ExampleUser(String name, String text, String type, String loginId, String apptoken) {
		}

		private final Set<ExampleUser> exampleUserList;
		private String exampleUsers;

		DeploymentInfo(@Context HttpServletRequest req) {
			UrlHandler urlHandler = TheRestInstanceProvider.getUrlHandler();
			this.restUrl = urlHandler.getRestUrl(req);
			this.restRecordUrl = urlHandler.getRestRecordUrl(req);
			this.restRecordTypeUrl = restRecordUrl + "recordType";
			this.iiifUrl = urlHandler.getIiifUrl(req);

			this.applicationName = SettingsProvider.getSetting("deploymentInfoApplicationName");
			this.deploymentName = SettingsProvider.getSetting("deploymentInfoDeploymentName");
			this.coraVersion = SettingsProvider.getSetting("deploymentInfoCoraVersion");
			this.applicationVersion = SettingsProvider
					.getSetting("deploymentInfoApplicationVersion");
			String loginRestUrl = SettingsProvider.getSetting("deploymentInfoLoginRestUrl");
			this.loginRestApptokenUrl = loginRestUrl + "apptoken";
			this.loginRestPasswordUrl = loginRestUrl + "password";

			exampleUserList = readAllExampleUsersFromStorage();
		}

		private Set<ExampleUser> readAllExampleUsersFromStorage() {
			Set<ExampleUser> set = new HashSet<>();
			for (Data exampleUser : readExampleUsersFromStorage()) {
				set.add(readExtractAndConvertToRecord(exampleUser));
			}
			return set;
		}

		private ExampleUser readExtractAndConvertToRecord(Data exampleUser) {
			DataRecord exampleUserAsRecord = (DataRecord) exampleUser;
			DataRecordGroup dataRecordGroup = exampleUserAsRecord.getDataRecordGroup();
			String name = dataRecordGroup.getFirstAtomicValueWithNameInData("name");
			String text = dataRecordGroup.getFirstAtomicValueWithNameInData("text");
			String type = dataRecordGroup.getFirstAtomicValueWithNameInData("type");
			String loginId = dataRecordGroup.getFirstAtomicValueWithNameInData("loginId");
			String apptoken = dataRecordGroup.getFirstAtomicValueWithNameInData("apptoken");
			return new ExampleUser(name, text, type, loginId, apptoken);
		}

		private List<Data> readExampleUsersFromStorage() {
			DataList exampleUsersDataList = SpiderInstanceProvider.getRecordListReader()
					.readRecordList(null, "exampleUser",
							DataProvider.createGroupUsingNameInData("filter"));
			return exampleUsersDataList.getDataList();
		}

		String toJson() {
			String deploymentInfoJsonTemplate = """
					{
						"applicationName": "%s",
						"deploymentName": "%s",
						"coraVersion": "%s",
						"applicationVersion": "%s",
						"urls": {
							"REST": "%s",
							"appTokenLogin": "%s",
							"passwordLogin": "%s",
							"record": "%s",
							"recordType": "%s",
							"iiif": "%s"
						},
						"exampleUsers": [%s]
					}""";

			String exampleUsersJsonTemplate = """
					{
						"name": "%s",
						"text": "%s",
						"type": "%s",
						"loginId": "%s",
						"appToken": "%s"
					}""";

			formatExampleUserJson(exampleUsersJsonTemplate);
			return formatDeploymenInfoUsingTemplate(deploymentInfoJsonTemplate);
		}

		private void formatExampleUserJson(String template) {
			exampleUsers = String.join(",", formatExampleUserUsingTemplate(template));
		}

		private List<String> formatExampleUserUsingTemplate(String template) {
			List<String> exampleUsersStringList = new ArrayList<>();
			for (ExampleUser exampleUser : exampleUserList) {
				exampleUsersStringList.add(template.formatted(exampleUser.name, exampleUser.text,
						exampleUser.type, exampleUser.loginId, exampleUser.apptoken));

			}
			return exampleUsersStringList;
		}

		private String formatDeploymenInfoUsingTemplate(String xmlTemplate) {
			return xmlTemplate.formatted(applicationName, deploymentName, coraVersion,
					applicationVersion, restUrl, loginRestApptokenUrl, loginRestPasswordUrl,
					restRecordUrl, restRecordTypeUrl, iiifUrl, exampleUsers);
		}

		String toXml() {
			exampleUsers = getXmlExampleUser("""
					<exampleUser>
							<name>%s</name>
							<text>%s</text>
							<type>%s</type>
							<loginId>%s</loginId>
							<appToken>%s</appToken>
						</exampleUser>""");

			return formatDeploymenInfoUsingTemplate("""
					<deploymentInfo>
						<applicationName>%s</applicationName>
						<deploymentName>%s</deploymentName>
						<coraVersion>%s</coraVersion>
						<applicationVersion>%s</applicationVersion>
						<urls>
							<REST>%s</REST>
							<appTokenLogin>%s</appTokenLogin>
							<passwordLogin>%s</passwordLogin>
							<record>%s</record>
							<recordType>%s</recordType>
							<iiif>%s</iiif>
						</urls>
						%s
					</deploymentInfo>""");
		}

		private String getXmlExampleUser(String exampleUserXmlTemplate) {
			if (exampleUserList.isEmpty()) {
				return "<exampleUsers/>";
			}
			return "<exampleUsers>" + formatExampleUserXml(exampleUserXmlTemplate)
					+ "</exampleUsers>";
		}

		private String formatExampleUserXml(String template) {
			return String.join("", formatExampleUserUsingTemplate(template));
		}
	}
}
