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

package se.uu.ub.cora.therest.record;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import se.uu.ub.cora.converter.ConverterProvider;
import se.uu.ub.cora.converter.ExternalUrls;
import se.uu.ub.cora.data.Convertible;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataProvider;
import se.uu.ub.cora.data.DataRecord;
import se.uu.ub.cora.data.DataRecordGroup;
import se.uu.ub.cora.data.converter.DataToJsonConverterProvider;
import se.uu.ub.cora.data.converter.JsonToDataConverterProvider;
import se.uu.ub.cora.data.spies.DataFactorySpy;
import se.uu.ub.cora.initialize.SettingsProvider;
import se.uu.ub.cora.json.parser.org.OrgJsonParser;
import se.uu.ub.cora.logger.LoggerProvider;
import se.uu.ub.cora.logger.spies.LoggerFactorySpy;
import se.uu.ub.cora.spider.dependency.SpiderInstanceProvider;
import se.uu.ub.cora.therest.AnnotationTestHelper;

public class RecordEndpointUpdateTest {
	private static final String APPLICATION_VND_CORA_RECORD_XML = "application/vnd.cora.record+xml";
	private static final String APPLICATION_VND_CORA_RECORD_JSON = "application/vnd.cora.record+json";
	private static final String APPLICATION_VND_CORA_RECORDGROUP_XML = "application/vnd.cora.recordgroup+xml";
	private static final String APPLICATION_VND_CORA_RECORDGROUP_JSON = "application/vnd.cora.recordgroup+json";
	private static final String APPLICATION_VND_CORA_RECORD_JSON_QS09 = "application/vnd.cora.record+json;qs=0.9";
	private static final String TEXT_PLAIN = "text/plain; charset=utf-8";
	private static final String DUMMY_NON_AUTHORIZED_TOKEN = "dummyNonAuthorizedToken";
	private static final String PLACE_0001 = "place:0001";
	private static final String PLACE = "place";
	private static final String AUTH_TOKEN = "authToken";
	private JsonParserSpy jsonParser;

	private JsonToDataConverterFactorySpy jsonToDataConverterFactorySpy = new JsonToDataConverterFactorySpy();

	private RecordEndpointUpdate recordEndpoint;
	private OldSpiderInstanceFactorySpy spiderInstanceFactorySpy;
	private Response response;
	private HttpServletRequestSpy requestSpy;
	private LoggerFactorySpy loggerFactorySpy;
	private DataFactorySpy dataFactorySpy;

	private String defaultJson = "{\"name\":\"someRecordType\",\"children\":[]}";
	private String defaultXml = "<someXml></someXml>";
	private DataToJsonConverterFactoryCreatorSpy converterFactoryCreatorSpy;
	private ConverterFactorySpy converterFactorySpy;
	private String standardBaseUrlHttp = "http://cora.epc.ub.uu.se/systemone/rest/record/";
	private String standardIffUrlHttp = "http://cora.epc.ub.uu.se/systemone/iiif/";
	private StringToExternallyConvertibleConverterSpy stringToExternallyConvertibleConverterSpy;

	@BeforeMethod
	public void beforeMethod() {
		loggerFactorySpy = new LoggerFactorySpy();
		LoggerProvider.setLoggerFactory(loggerFactorySpy);
		dataFactorySpy = new DataFactorySpy();
		DataProvider.onlyForTestSetDataFactory(dataFactorySpy);

		converterFactoryCreatorSpy = new DataToJsonConverterFactoryCreatorSpy();
		DataToJsonConverterProvider
				.setDataToJsonConverterFactoryCreator(converterFactoryCreatorSpy);

		stringToExternallyConvertibleConverterSpy = new StringToExternallyConvertibleConverterSpy();
		converterFactorySpy = new ConverterFactorySpy();
		converterFactorySpy.MRV.setDefaultReturnValuesSupplier(
				"factorStringToExternallyConvertableConverter",
				() -> stringToExternallyConvertibleConverterSpy);
		ConverterProvider.setConverterFactory("xml", converterFactorySpy);

		jsonToDataConverterFactorySpy = new JsonToDataConverterFactorySpy();
		JsonToDataConverterProvider.setJsonToDataConverterFactory(jsonToDataConverterFactorySpy);
		spiderInstanceFactorySpy = new OldSpiderInstanceFactorySpy();
		SpiderInstanceProvider.setSpiderInstanceFactory(spiderInstanceFactorySpy);

		Map<String, String> settings = new HashMap<>();
		settings.put("theRestPublicPathToSystem", "/systemone/rest/");
		settings.put("iiifPublicPathToSystem", "/systemone/iiif/");
		SettingsProvider.setSettings(settings);

		requestSpy = new HttpServletRequestSpy();
		recordEndpoint = new RecordEndpointUpdate(requestSpy);

		setUpSpiesInRecordEndpoint();
	}

	private void setUpSpiesInRecordEndpoint() {
		jsonParser = new JsonParserSpy();

		recordEndpoint.setJsonParser(jsonParser);
	}

	@Test
	public void testInit() {
		recordEndpoint = new RecordEndpointUpdate(requestSpy);
		assertTrue(recordEndpoint.getJsonParser() instanceof OrgJsonParser);
	}

	@Test
	public void testClassAnnotation() throws Exception {
		AnnotationTestHelper annotationHelper = AnnotationTestHelper
				.createAnnotationTestHelperForClass(RecordEndpointUpdate.class);

		annotationHelper.assertPathAnnotationForClass("/");
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

	private void assertDataFromSpiderConvertedToJsonUsingConvertersFromProvider(
			Convertible convertible) {
		DataToJsonConverterFactorySpy converterFactory = (DataToJsonConverterFactorySpy) converterFactoryCreatorSpy.MCR
				.getReturnValue("createFactory", 0);

		converterFactory.MCR.assertParameters("factorUsingConvertibleAndExternalUrls", 0,
				convertible);
		se.uu.ub.cora.data.converter.ExternalUrls externalUrls = (se.uu.ub.cora.data.converter.ExternalUrls) converterFactory.MCR
				.getParameterForMethodAndCallNumberAndParameter(
						"factorUsingConvertibleAndExternalUrls", 0, "externalUrls");
		assertEquals(externalUrls.getBaseUrl(), standardBaseUrlHttp);
		assertEquals(externalUrls.getIfffUrl(), standardIffUrlHttp);

		DataToJsonConverterSpy converterSpy = (DataToJsonConverterSpy) converterFactory.MCR
				.getReturnValue("factorUsingConvertibleAndExternalUrls", 0);

		var entity = response.getEntity();
		converterSpy.MCR.assertReturn("toJsonCompactFormat", 0, entity);
	}

	private void assertXmlConvertionOfResponse(Convertible convertible) {
		ExternallyConvertibleToStringConverterSpy dataToXmlConverter = (ExternallyConvertibleToStringConverterSpy) converterFactorySpy.MCR
				.getReturnValue("factorExternallyConvertableToStringConverter", 0);

		dataToXmlConverter.MCR.assertParameters("convertWithLinks", 0, convertible);
		ExternalUrls externalUrls = (ExternalUrls) dataToXmlConverter.MCR
				.getParameterForMethodAndCallNumberAndParameter("convertWithLinks", 0,
						"externalUrls");
		assertEquals(externalUrls.getBaseUrl(), standardBaseUrlHttp);
		assertEquals(externalUrls.getIfffUrl(), standardIffUrlHttp);

		var entity = response.getEntity();
		dataToXmlConverter.MCR.assertReturn("convertWithLinks", 0, entity);
	}

	@Test
	public void testPreferredTokenForUpdate() throws IOException {
		expectTokenForUpdateToPrefereblyBeHeaderThanQuery(AUTH_TOKEN, "authToken2", AUTH_TOKEN);
		expectTokenForUpdateToPrefereblyBeHeaderThanQuery(null, AUTH_TOKEN, AUTH_TOKEN);
		expectTokenForUpdateToPrefereblyBeHeaderThanQuery(AUTH_TOKEN, null, AUTH_TOKEN);
		expectTokenForUpdateToPrefereblyBeHeaderThanQuery(null, null, null);
	}

	private void expectTokenForUpdateToPrefereblyBeHeaderThanQuery(String headerAuthToken,
			String queryAuthToken, String authTokenExpected) {
		response = recordEndpoint.updateRecordJsonJson(headerAuthToken, queryAuthToken, PLACE,
				PLACE_0001, defaultJson);

		SpiderRecordUpdaterSpy spiderUpdaterSpy = spiderInstanceFactorySpy.spiderRecordUpdaterSpy;
		assertEquals(spiderUpdaterSpy.authToken, authTokenExpected);
	}

	@Test
	public void testUpdateRecordForJson() {
		response = recordEndpoint.updateRecordJsonJson(AUTH_TOKEN, AUTH_TOKEN, PLACE, PLACE_0001,
				defaultJson);
		assertResponseStatusIs(Response.Status.OK);
	}

	@Test
	public void testUpdateRecordUsesFactories() {
		response = recordEndpoint.updateRecordJsonJson(AUTH_TOKEN, AUTH_TOKEN, PLACE, PLACE_0001,
				defaultJson);

		DataRecordGroup recordSentOnToSpider = spiderInstanceFactorySpy.spiderRecordUpdaterSpy.record;
		assertJsonStringConvertedToRecordUsesConverter(defaultJson, recordSentOnToSpider);
	}

	private void assertJsonStringConvertedToRecordUsesConverter(String jsonSentToEndPoint,
			DataRecordGroup recordSentOnToSpider) {
		assertSame(jsonParser.jsonString, jsonSentToEndPoint);
		assertSame(jsonToDataConverterFactorySpy.jsonValue, jsonParser.returnedJsonValue);
		JsonToDataConverterSpy jsonToDataConverterSpy = jsonToDataConverterFactorySpy.jsonToDataConverterSpy;
		DataGroup returnedDataPart = jsonToDataConverterSpy.dataPartToReturn;

		DataRecordGroup convertedToRecord = (DataRecordGroup) dataFactorySpy.MCR
				.assertCalledParametersReturn("factorRecordGroupFromDataGroup", returnedDataPart);
		assertSame(recordSentOnToSpider, convertedToRecord);
	}

	@Test
	public void testUpdateRecordForJsonXml() {
		response = recordEndpoint.updateRecordJsonXml(AUTH_TOKEN, AUTH_TOKEN, PLACE, PLACE_0001,
				defaultJson);

		DataRecordGroup recordSentOnToSpider = spiderInstanceFactorySpy.spiderRecordUpdaterSpy.record;
		assertJsonStringConvertedToRecordUsesConverter(defaultJson, recordSentOnToSpider);

		DataRecord updatedRecord = (DataRecord) spiderInstanceFactorySpy.spiderRecordUpdaterSpy.MCR
				.getReturnValue("updateRecord", 0);

		assertXmlConvertionOfResponse(updatedRecord);
		assertEntityExists();
		assertResponseStatusIs(Response.Status.OK);
		assertResponseContentTypeIs(APPLICATION_VND_CORA_RECORD_XML);
	}

	@Test
	public void testUpdateRecordBodyInXmlWithReplyInJson() {
		response = recordEndpoint.updateRecordXmlJson(AUTH_TOKEN, AUTH_TOKEN, PLACE, PLACE_0001,
				defaultXml);

		DataRecordGroup dataRecord = assertParametersAndGetConvertedXmlDataRecordGroup();

		spiderInstanceFactorySpy.spiderRecordUpdaterSpy.MCR.assertParameters("updateRecord", 0,
				AUTH_TOKEN, PLACE, PLACE_0001, dataRecord);

		DataRecord updatedRecord = (DataRecord) spiderInstanceFactorySpy.spiderRecordUpdaterSpy.MCR
				.getReturnValue("updateRecord", 0);

		assertDataFromSpiderConvertedToJsonUsingConvertersFromProvider(updatedRecord);
		assertEntityExists();
		assertResponseStatusIs(Response.Status.OK);
		assertResponseContentTypeIs(APPLICATION_VND_CORA_RECORD_JSON);
	}

	@Test
	public void testUpdateRecordForXmlXml() {
		response = recordEndpoint.updateRecordXmlXml(AUTH_TOKEN, AUTH_TOKEN, PLACE, PLACE_0001,
				defaultXml);

		DataRecordGroup dataRecord = assertParametersAndGetConvertedXmlDataRecordGroup();

		spiderInstanceFactorySpy.spiderRecordUpdaterSpy.MCR.assertParameters("updateRecord", 0,
				AUTH_TOKEN, PLACE, PLACE_0001, dataRecord);

		DataRecord updatedRecord = (DataRecord) spiderInstanceFactorySpy.spiderRecordUpdaterSpy.MCR
				.getReturnValue("updateRecord", 0);

		assertXmlConvertionOfResponse(updatedRecord);
		assertEntityExists();
		assertResponseStatusIs(Response.Status.OK);
		assertResponseContentTypeIs(APPLICATION_VND_CORA_RECORD_XML);
	}

	private DataRecordGroup assertParametersAndGetConvertedXmlDataRecordGroup() {
		StringToExternallyConvertibleConverterSpy xmlToDataConverter = (StringToExternallyConvertibleConverterSpy) converterFactorySpy.MCR
				.getReturnValue("factorStringToExternallyConvertableConverter", 0);
		xmlToDataConverter.MCR.assertParameters("convert", 0, defaultXml);
		DataGroup dataGroup = (DataGroup) xmlToDataConverter.MCR.getReturnValue("convert", 0);

		return (DataRecordGroup) dataFactorySpy.MCR
				.assertCalledParametersReturn("factorRecordGroupFromDataGroup", dataGroup);
	}

	@Test
	public void testAnnotationsForUpdateRecordJsonJson() throws Exception {
		AnnotationTestHelper annotationHelper = AnnotationTestHelper
				.createAnnotationTestHelperForClassMethodNameAndNumOfParameters(
						recordEndpoint.getClass(), "updateRecordJsonJson", 5);

		annotationHelper.assertHttpMethodAndPathAnnotation("POST", "{type}/{id}");
		annotationHelper.assertConsumesAnnotation(APPLICATION_VND_CORA_RECORDGROUP_JSON);
		annotationHelper.assertProducesAnnotation(APPLICATION_VND_CORA_RECORD_JSON_QS09);
		annotationHelper.assertAnnotationForAuthTokensAndTypeAndIdParameters();
	}

	@Test
	public void testAnnotationsForUpdateRecordJsonXml() throws Exception {
		AnnotationTestHelper annotationHelper = AnnotationTestHelper
				.createAnnotationTestHelperForClassMethodNameAndNumOfParameters(
						recordEndpoint.getClass(), "updateRecordJsonXml", 5);

		annotationHelper.assertHttpMethodAndPathAnnotation("POST", "{type}/{id}");
		annotationHelper.assertConsumesAnnotation(APPLICATION_VND_CORA_RECORDGROUP_JSON);
		annotationHelper.assertProducesAnnotation(APPLICATION_VND_CORA_RECORD_XML);
		annotationHelper.assertAnnotationForAuthTokensAndTypeAndIdParameters();
	}

	@Test
	public void testAnnotationsForUpdateRecordXmlJson() throws Exception {
		AnnotationTestHelper annotationHelper = AnnotationTestHelper
				.createAnnotationTestHelperForClassMethodNameAndNumOfParameters(
						recordEndpoint.getClass(), "updateRecordXmlJson", 5);

		annotationHelper.assertHttpMethodAndPathAnnotation("POST", "{type}/{id}");
		annotationHelper.assertConsumesAnnotation(APPLICATION_VND_CORA_RECORDGROUP_XML);
		annotationHelper.assertProducesAnnotation(APPLICATION_VND_CORA_RECORD_JSON_QS09);
		annotationHelper.assertAnnotationForAuthTokensAndTypeAndIdParameters();
	}

	@Test
	public void testAnnotationsForUpdateRecordXmlXml() throws Exception {
		AnnotationTestHelper annotationHelper = AnnotationTestHelper
				.createAnnotationTestHelperForClassMethodNameAndNumOfParameters(
						recordEndpoint.getClass(), "updateRecordXmlXml", 5);

		annotationHelper.assertHttpMethodAndPathAnnotation("POST", "{type}/{id}");
		annotationHelper.assertConsumesAnnotation(APPLICATION_VND_CORA_RECORDGROUP_XML);
		annotationHelper.assertProducesAnnotation(APPLICATION_VND_CORA_RECORD_XML);
		annotationHelper.assertAnnotationForAuthTokensAndTypeAndIdParameters();
	}

	@Test
	public void testUpdateRecordUnauthorized() {
		response = recordEndpoint.updateRecordJsonJson(DUMMY_NON_AUTHORIZED_TOKEN, AUTH_TOKEN,
				PLACE, PLACE_0001, defaultJson);
		assertResponseStatusIs(Response.Status.FORBIDDEN);
		assertResponseContentTypeIs(TEXT_PLAIN);
		assertEquals(response.getEntity(), null);
	}

	@Test
	public void testUpdateRecordNotFound() {
		response = recordEndpoint.updateRecordJsonJson(AUTH_TOKEN, AUTH_TOKEN, PLACE,
				PLACE_0001 + "_NOT_FOUND", defaultJson);
		assertResponseStatusIs(Response.Status.NOT_FOUND);
		assertResponseContentTypeIs(TEXT_PLAIN);
		assertEquals(response.getEntity(),
				"Error updating record with recordType: " + PLACE + " and recordId: " + PLACE_0001
						+ "_NOT_FOUND. No record exist with id place:0001_NOT_FOUND");
	}

	@Test
	public void testUpdateRecordTypeNotFound() {
		response = recordEndpoint.updateRecordJsonJson(AUTH_TOKEN, AUTH_TOKEN, PLACE + "_NOT_FOUND",
				PLACE_0001, defaultJson);
		assertResponseStatusIs(Response.Status.NOT_FOUND);
		assertResponseContentTypeIs(TEXT_PLAIN);
		assertEquals(response.getEntity(),
				"Error updating record with recordType: " + PLACE + "_NOT_FOUND and recordId: "
						+ PLACE_0001 + ". No record exist with type place_NOT_FOUND");
	}

	@Test
	public void testUpdateRecordBadContentInJson() {
		jsonParser.throwError = true;
		response = recordEndpoint.updateRecordJsonJson(AUTH_TOKEN, AUTH_TOKEN, PLACE, PLACE_0001,
				defaultJson);
		assertResponseStatusIs(Response.Status.BAD_REQUEST);
		assertResponseContentTypeIs(TEXT_PLAIN);
		assertEquals(response.getEntity(), "Error updating record with recordType: " + PLACE
				+ " and recordId: " + PLACE_0001 + ". some parse exception from spy");
	}

	@Test
	public void testUpdateRecordWrongDataTypeInJson() {
		spiderInstanceFactorySpy.throwDataException = true;
		response = recordEndpoint.updateRecordJsonJson(AUTH_TOKEN, AUTH_TOKEN, PLACE, PLACE_0001,
				defaultJson);
		assertResponseStatusIs(Response.Status.BAD_REQUEST);
		assertEquals(response.getEntity(), "Error updating record with recordType: " + PLACE
				+ " and recordId: " + PLACE_0001 + ". some data exception from update");
	}

}