/*
 * Copyright 2015, 2016, 2018, 2021, 2022, 2024, 2025 Uppsala University Library
 * Copyright 2016 Olov McKie
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

package se.ub.uu.cora.therest.deployment;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import se.uu.ub.cora.data.Data;
import se.uu.ub.cora.data.DataList;
import se.uu.ub.cora.data.DataProvider;
import se.uu.ub.cora.data.DataRecord;
import se.uu.ub.cora.data.spies.DataFactorySpy;
import se.uu.ub.cora.data.spies.DataListSpy;
import se.uu.ub.cora.data.spies.DataRecordGroupSpy;
import se.uu.ub.cora.data.spies.DataRecordSpy;
import se.uu.ub.cora.initialize.SettingsProvider;
import se.uu.ub.cora.logger.LoggerProvider;
import se.uu.ub.cora.logger.spies.LoggerFactorySpy;
import se.uu.ub.cora.spider.dependency.SpiderInstanceProvider;
import se.uu.ub.cora.spider.spies.RecordListReaderSpy;
import se.uu.ub.cora.spider.spies.SpiderInstanceFactorySpy;
import se.uu.ub.cora.therest.AnnotationTestHelper;
import se.uu.ub.cora.therest.dependency.TheRestInstanceFactorySpy;
import se.uu.ub.cora.therest.dependency.TheRestInstanceProvider;
import se.uu.ub.cora.therest.deployment.RecordEndpointDeployment;
import se.uu.ub.cora.therest.record.RecordEndpointRead;
import se.uu.ub.cora.therest.url.HttpServletRequestSpy;
import se.uu.ub.cora.therest.url.UrlHandlerSpy;

public class RecordEndpointDeploymentTest {
	private static final String HTTP_BASE_REST_URL = "http://base.rest.url/rest/";
	private static final String HTTP_BASE_REST_RECORD_URL = "http://base.rest.url/rest/record/";
	private static final String HTTP_BASE_IIIF_URL = "http://base.rest.url/iiif/";
	private static final String HTTP_LOGIN_REST_URL = "http://login.rest.url/login/rest/";

	private static final String APPLICATION_VND_CORA_DEPLOYMENT_INFO_JSON = ""
			+ "application/vnd.cora.deploymentInfo+json";
	private static final String APPLICATION_VND_CORA_DEPLOYMENT_INFO_XML = ""
			+ "application/vnd.cora.deploymentInfo+xml";

	private RecordEndpointDeployment recordEndpoint;
	private Response response;
	private HttpServletRequestSpy requestSpy;

	private TheRestInstanceFactorySpy instanceFactory;
	private RecordListReaderSpy recordListReader;
	private DataFactorySpy dataFactory;

	record ExampleUser(String name, String text, String type, String loginId, String apptoken) {
	}

	@BeforeMethod
	public void beforeMethod() {
		setupUrlHandler();

		LoggerProvider.setLoggerFactory(new LoggerFactorySpy());
		setUpSettingsProvider();

		dataFactory = new DataFactorySpy();
		DataProvider.onlyForTestSetDataFactory(dataFactory);

		requestSpy = new HttpServletRequestSpy();
		recordEndpoint = new RecordEndpointDeployment(requestSpy);

		setUpSpiderInstanceProvider();
	}

	private void setUpSpiderInstanceProvider(ExampleUser... exampleUsers) {
		SpiderInstanceFactorySpy spiderInstancefactory = createSpiderInstanceFactory(exampleUsers);
		SpiderInstanceProvider.setSpiderInstanceFactory(spiderInstancefactory);
	}

	private SpiderInstanceFactorySpy createSpiderInstanceFactory(ExampleUser... exampleUsers) {
		SpiderInstanceFactorySpy spiderInstancefactory = new SpiderInstanceFactorySpy();
		spiderInstancefactory.MRV.setDefaultReturnValuesSupplier("factorRecordListReader",
				() -> createRecordListReader(exampleUsers));
		return spiderInstancefactory;
	}

	private RecordListReaderSpy createRecordListReader(ExampleUser... exampleUsers) {
		recordListReader = new RecordListReaderSpy();
		recordListReader.MRV.setDefaultReturnValuesSupplier("readRecordList",
				() -> createDataListWithExampleUsers(exampleUsers));
		return recordListReader;
	}

	private DataList createDataListWithExampleUsers(ExampleUser... exampleUsers) {
		List<Data> exampleUserRecordsList = new ArrayList<>();
		for (ExampleUser exampleUser : exampleUsers) {
			DataRecord exampleUserRecord = convertExampleUserToRecord(exampleUser);
			exampleUserRecordsList.add(exampleUserRecord);
		}
		DataListSpy dataList = new DataListSpy();
		dataList.MRV.setDefaultReturnValuesSupplier("getDataList", () -> exampleUserRecordsList);
		return dataList;
	}

	private DataRecord convertExampleUserToRecord(ExampleUser exampleUser) {
		DataRecordSpy exampleUserRecord = new DataRecordSpy();
		exampleUserRecord.MRV.setDefaultReturnValuesSupplier("getDataRecordGroup",
				() -> convertExampleUserToRecordGroup(exampleUser));
		return exampleUserRecord;
	}

	private DataRecordGroupSpy convertExampleUserToRecordGroup(ExampleUser exampleUser) {
		DataRecordGroupSpy exampleUseRecordGroup = new DataRecordGroupSpy();
		exampleUseRecordGroup.MRV.setSpecificReturnValuesSupplier(
				"getFirstAtomicValueWithNameInData", exampleUser::name, "name");
		exampleUseRecordGroup.MRV.setSpecificReturnValuesSupplier(
				"getFirstAtomicValueWithNameInData", exampleUser::text, "text");
		exampleUseRecordGroup.MRV.setSpecificReturnValuesSupplier(
				"getFirstAtomicValueWithNameInData", exampleUser::type, "type");
		exampleUseRecordGroup.MRV.setSpecificReturnValuesSupplier(
				"getFirstAtomicValueWithNameInData", exampleUser::loginId, "loginId");
		exampleUseRecordGroup.MRV.setSpecificReturnValuesSupplier(
				"getFirstAtomicValueWithNameInData", exampleUser::apptoken, "apptoken");
		return exampleUseRecordGroup;
	}

	private void setUpSettingsProvider() {
		SettingsProvider.setSettings(createSettings());
	}

	private Map<String, String> createSettings() {
		Map<String, String> settings = new HashMap<>();

		settings.put("deploymentInfoApplicationName", "someApplicationName");
		settings.put("deploymentInfoDeploymentName", "someDeploymentName");
		settings.put("deploymentInfoCoraVersion", "someCoraVersion");
		settings.put("deploymentInfoApplicationVersion", "someApplicationVersion");
		settings.put("deploymentInfoLoginRestUrl", HTTP_LOGIN_REST_URL);

		return settings;
	}

	private void setupUrlHandler() {
		UrlHandlerSpy urlHandler = new UrlHandlerSpy();
		urlHandler.MRV.setDefaultReturnValuesSupplier("getRestUrl", () -> HTTP_BASE_REST_URL);
		urlHandler.MRV.setDefaultReturnValuesSupplier("getRestRecordUrl",
				() -> HTTP_BASE_REST_RECORD_URL);
		urlHandler.MRV.setDefaultReturnValuesSupplier("getIiifUrl", () -> HTTP_BASE_IIIF_URL);

		instanceFactory = new TheRestInstanceFactorySpy();
		instanceFactory.MRV.setDefaultReturnValuesSupplier("factorUrlHandler", () -> urlHandler);

		TheRestInstanceProvider.onlyForTestSetTheRestInstanceFactory(instanceFactory);
	}

	@Test
	public void testClassAnnotation() {
		AnnotationTestHelper annotationHelper = AnnotationTestHelper
				.createAnnotationTestHelperForClass(RecordEndpointRead.class);

		annotationHelper.assertPathAnnotationForClass("/");
	}

	@Test
	public void testAnnotationsForGetDeploymentInfoJson() throws Exception {
		AnnotationTestHelper annotationHelper = AnnotationTestHelper
				.createAnnotationTestHelperForClassMethodNameAndNumOfParameters(
						recordEndpoint.getClass(), "getDeploymentInfoJson", 0);

		annotationHelper.assertHttpMethodAndPathAnnotation("GET", "");
		annotationHelper
				.assertProducesAnnotation(APPLICATION_VND_CORA_DEPLOYMENT_INFO_JSON + ";qs=0.1");
	}

	@Test
	public void testAnnotationsForReadIndexHtml() throws Exception {
		AnnotationTestHelper annotationHelper = AnnotationTestHelper
				.createAnnotationTestHelperForClassMethodNameAndNumOfParameters(
						recordEndpoint.getClass(), "forwardToHtmlDocumentation", 0);

		annotationHelper.assertHttpMethodAndPathAnnotation("GET", "");
		annotationHelper.assertProducesAnnotation(MediaType.TEXT_HTML);
	}

	@Test
	public void testAnnotationsForDeploymentInfoXml() throws Exception {
		AnnotationTestHelper annotationHelper = AnnotationTestHelper
				.createAnnotationTestHelperForClassMethodNameAndNumOfParameters(
						recordEndpoint.getClass(), "getDeploymentInfoXml", 0);

		annotationHelper.assertHttpMethodAndPathAnnotation("GET", "");
		annotationHelper
				.assertProducesAnnotation(APPLICATION_VND_CORA_DEPLOYMENT_INFO_XML + ";qs=0.1");
	}

	@Test
	public void testForwardToHtmlDocumentation() {
		response = recordEndpoint.forwardToHtmlDocumentation();
		assertResponseStatusIs(Response.Status.TEMPORARY_REDIRECT);
		String locationString = response.getHeaders().getFirst(HttpHeaders.LOCATION).toString();
		assertEquals(locationString, HTTP_BASE_REST_URL + "index.html");
	}

	@Test
	public void testGetDeploymentInfoJson_NoExampleUsers() {
		recordEndpoint = new RecordEndpointDeployment(requestSpy);

		response = recordEndpoint.getDeploymentInfoJson();

		assertResponseJSON();
		assertEquals(response.getEntity(), """
				{
					"applicationName": "someApplicationName",
					"deploymentName": "someDeploymentName",
					"coraVersion": "someCoraVersion",
					"applicationVersion": "someApplicationVersion",
					"urls": {
						"REST": "http://base.rest.url/rest/",
						"appTokenLogin": "http://login.rest.url/login/rest/apptoken",
						"passwordLogin": "http://login.rest.url/login/rest/password",
						"record": "http://base.rest.url/rest/record/",
						"iiif": "http://base.rest.url/iiif/"
					},
					"exampleUsers": []
				}""");

		assertCallRecordListForExampleUser();
	}

	private void assertCallRecordListForExampleUser() {
		var filter = dataFactory.MCR.assertCalledParametersReturn("factorGroupUsingNameInData",
				"filter");
		recordListReader.MCR.assertParameters("readRecordList", 0, null, "exampleUser", filter);
	}

	@Test
	public void testGetDeploymentInfoJson_WithExampleUsers() {
		ExampleUser exampleUser01 = new ExampleUser("systemoneAdmin", "appToken for systemoneAdmin",
				"appTokenLogin", "systemoneAdmin@system.cora.uu.se",
				"5d3f3ed4-4931-4924-9faa-8eaf5ac6457e");
		ExampleUser exampleUser02 = new ExampleUser("someName02", "someText02", "someType02",
				"someLoginId02", "someAppToken02");
		setUpSpiderInstanceProvider(exampleUser01, exampleUser02);

		response = recordEndpoint.getDeploymentInfoJson();

		assertResponseJSON();
		assertEqualsCompactedFormat((String) response.getEntity(), """
				{
					"applicationName": "someApplicationName",
					"deploymentName": "someDeploymentName",
					"coraVersion": "someCoraVersion",
					"applicationVersion": "someApplicationVersion",
					"urls": {
						"REST": "http://base.rest.url/rest/",
						"appTokenLogin": "http://login.rest.url/login/rest/apptoken",
						"passwordLogin": "http://login.rest.url/login/rest/password",
						"record": "http://base.rest.url/rest/record/",
						"iiif": "http://base.rest.url/iiif/"
					},
					"exampleUsers": [
						{
							"name": "systemoneAdmin",
							"text": "appToken for systemoneAdmin",
							"type": "appTokenLogin",
							"loginId": "systemoneAdmin@system.cora.uu.se",
							"appToken": "5d3f3ed4-4931-4924-9faa-8eaf5ac6457e"
						},{
							"name": "someName02",
							"text": "someText02",
							"type": "someType02",
							"loginId": "someLoginId02",
							"appToken": "someAppToken02"
						}

					]
				}""");
		assertCallRecordListForExampleUser();
	}

	private void assertResponseJSON() {
		assertResponsOk();
		assertResponseContentTypeIs(APPLICATION_VND_CORA_DEPLOYMENT_INFO_JSON);
	}

	private void assertResponsOk() {
		assertEntityExists();
		assertResponseStatusIs(Response.Status.OK);
	}

	@Test
	public void testGetDeploymentInfoXml_NoExampleUsers() {

		response = recordEndpoint.getDeploymentInfoXml();

		assertResponseXML();
		String expected = """
				<deploymentInfo>
					<applicationName>someApplicationName</applicationName>
					<deploymentName>someDeploymentName</deploymentName>
					<coraVersion>someCoraVersion</coraVersion>
					<applicationVersion>someApplicationVersion</applicationVersion>
					<urls>
						<REST>http://base.rest.url/rest/</REST>
						<appTokenLogin>http://login.rest.url/login/rest/apptoken</appTokenLogin>
						<passwordLogin>http://login.rest.url/login/rest/password</passwordLogin>
						<record>http://base.rest.url/rest/record/</record>
						<iiif>http://base.rest.url/iiif/</iiif>
					</urls>
					<exampleUsers/>
				</deploymentInfo>""";
		assertEquals(response.getEntity(), expected);

		assertCallRecordListForExampleUser();
	}

	private void assertResponseXML() {
		assertResponsOk();
		assertResponseContentTypeIs(APPLICATION_VND_CORA_DEPLOYMENT_INFO_XML);
	}

	@Test
	public void testGetDeploymentInfoXml_WithExampleUsers() {
		ExampleUser exampleUser01 = new ExampleUser("systemoneAdmin", "appToken for systemoneAdmin",
				"appTokenLogin", "systemoneAdmin@system.cora.uu.se",
				"5d3f3ed4-4931-4924-9faa-8eaf5ac6457e");
		ExampleUser exampleUser02 = new ExampleUser("someName02", "someText02", "someType02",
				"someLoginId02", "someAppToken02");
		setUpSpiderInstanceProvider(exampleUser01, exampleUser02);

		response = recordEndpoint.getDeploymentInfoXml();

		assertResponseXML();
		String expected = """
				<deploymentInfo>
					<applicationName>someApplicationName</applicationName>
					<deploymentName>someDeploymentName</deploymentName>
					<coraVersion>someCoraVersion</coraVersion>
					<applicationVersion>someApplicationVersion</applicationVersion>
					<urls>
						<REST>http://base.rest.url/rest/</REST>
						<appTokenLogin>http://login.rest.url/login/rest/apptoken</appTokenLogin>
						<passwordLogin>http://login.rest.url/login/rest/password</passwordLogin>
						<record>http://base.rest.url/rest/record/</record>
						<iiif>http://base.rest.url/iiif/</iiif>
					</urls>
					<exampleUsers>
						<exampleUser>
							<name>systemoneAdmin</name>
							<text>appToken for systemoneAdmin</text>
							<type>appTokenLogin</type>
							<loginId>systemoneAdmin@system.cora.uu.se</loginId>
							<appToken>5d3f3ed4-4931-4924-9faa-8eaf5ac6457e</appToken>
						</exampleUser>
						<exampleUser>
							<name>someName02</name>
							<text>someText02</text>
							<type>someType02</type>
							<loginId>someLoginId02</loginId>
							<appToken>someAppToken02</appToken>
						</exampleUser>
					</exampleUsers>
				</deploymentInfo>""";
		assertEqualsCompactedFormat((String) response.getEntity(), expected);

		assertCallRecordListForExampleUser();
	}

	private void assertEqualsCompactedFormat(String actual, String expected) {
		assertEquals(convertToCompactFormat(actual), convertToCompactFormat(expected));
	}

	private String convertToCompactFormat(String string) {
		return string.replace("\t", "").replace("\s", "").replace("\n", "");
	}

	private void assertEntityExists() {
		assertNotNull(response.getEntity(), "An entity should be returned");
	}

	private void assertResponseStatusIs(Status responseStatus) {
		assertEquals(response.getStatusInfo(), responseStatus);
	}

	private void assertResponseContentTypeIs(String expectedContentType) {
		assertEquals(response.getHeaders().getFirst(HttpHeaders.CONTENT_TYPE), expectedContentType);
	}

}
