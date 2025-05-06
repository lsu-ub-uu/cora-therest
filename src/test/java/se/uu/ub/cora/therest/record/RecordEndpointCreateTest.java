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
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

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
import se.uu.ub.cora.logger.spies.LoggerSpy;
import se.uu.ub.cora.spider.dependency.SpiderInstanceProvider;
import se.uu.ub.cora.therest.AnnotationTestHelper;

public class RecordEndpointCreateTest {
	private static final String APPLICATION_VND_CORA_RECORDGROUP_XML = "application/vnd.cora.recordgroup+xml";
	private static final String APPLICATION_VND_CORA_RECORDGROUP_JSON = "application/vnd.cora.recordgroup+json";
	private static final String APPLICATION_VND_CORA_RECORD_XML = "application/vnd.cora.record+xml";
	private static final String APPLICATION_VND_CORA_RECORD_JSON = "application/vnd.cora.record+json";
	private static final String APPLICATION_VND_CORA_RECORD_JSON_QS09 = "application/vnd.cora.record+json;qs=0.9";
	private static final String TEXT_PLAIN = "text/plain; charset=utf-8";
	private static final String DUMMY_NON_AUTHORIZED_TOKEN = "dummyNonAuthorizedToken";
	private static final String PLACE = "place";
	private static final String AUTH_TOKEN = "authToken";
	private JsonParserSpy jsonParser;

	private JsonToDataConverterFactorySpy jsonToDataConverterFactorySpy = new JsonToDataConverterFactorySpy();

	private RecordEndpointCreate recordEndpoint;
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
		recordEndpoint = new RecordEndpointCreate(requestSpy);

		setUpSpiesInRecordEndpoint();
	}

	private void setUpSpiesInRecordEndpoint() {
		jsonParser = new JsonParserSpy();

		recordEndpoint.setJsonParser(jsonParser);
	}

	@Test
	public void testInit() {
		recordEndpoint = new RecordEndpointCreate(requestSpy);
		assertTrue(recordEndpoint.getJsonParser() instanceof OrgJsonParser);
	}

	@Test
	public void testClassAnnotation() throws Exception {
		AnnotationTestHelper annotationHelper = AnnotationTestHelper
				.createAnnotationTestHelperForClass(RecordEndpointCreate.class);

		annotationHelper.assertPathAnnotationForClass("/");
	}

	private void assertResponseStatusIs(Status responseStatus) {
		assertEquals(response.getStatusInfo(), responseStatus);
	}

	private void assertEntityExists() {
		assertNotNull(response.getEntity(), "An entity should be returned");
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

	private DataRecordGroup assertParametersAndGetConvertedXmlDataRecordGroup() {
		StringToExternallyConvertibleConverterSpy xmlToDataConverter = (StringToExternallyConvertibleConverterSpy) converterFactorySpy.MCR
				.getReturnValue("factorStringToExternallyConvertableConverter", 0);
		xmlToDataConverter.MCR.assertParameters("convert", 0, defaultXml);
		DataGroup dataGroup = (DataGroup) xmlToDataConverter.MCR.getReturnValue("convert", 0);

		DataRecordGroup convertedToRecord = (DataRecordGroup) dataFactorySpy.MCR
				.assertCalledParametersReturn("factorRecordGroupFromDataGroup", dataGroup);

		return convertedToRecord;
	}

	@Test
	public void testPreferredTokenForCreate() {
		expectTokenForCreateToPreferablyBeHeaderThanQuery(AUTH_TOKEN, "authToken2", AUTH_TOKEN);
		expectTokenForCreateToPreferablyBeHeaderThanQuery(null, AUTH_TOKEN, AUTH_TOKEN);
		expectTokenForCreateToPreferablyBeHeaderThanQuery(AUTH_TOKEN, null, AUTH_TOKEN);
		expectTokenForCreateToPreferablyBeHeaderThanQuery(null, null, null);
	}

	private void expectTokenForCreateToPreferablyBeHeaderThanQuery(String headerAuthToken,
			String queryAuthToken, String authTokenExpected) {
		response = recordEndpoint.createRecordJsonJson(headerAuthToken, queryAuthToken, PLACE,
				defaultJson);

		SpiderCreatorOldSpy spiderCreatorSpy = spiderInstanceFactorySpy.spiderCreatorSpy;
		assertEquals(spiderCreatorSpy.authToken, authTokenExpected);
	}

	@Test
	public void testCreateRecord() {
		response = recordEndpoint.createRecordJsonJson(AUTH_TOKEN, AUTH_TOKEN, PLACE, defaultJson);

		assertResponseStatusIs(Response.Status.CREATED);
		assertEquals(response.getLocation().toString(), PLACE + "/someCreatedId");
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
	public void testCreateRecordUsesFactories() {
		response = recordEndpoint.createRecordJsonJson(AUTH_TOKEN, AUTH_TOKEN, PLACE, defaultJson);

		DataRecordGroup recordSentOnToSpider = spiderInstanceFactorySpy.spiderCreatorSpy.record;
		assertJsonStringConvertedToRecordUsesConverter(defaultJson, recordSentOnToSpider);
		DataRecord createdRecord = (DataRecord) spiderInstanceFactorySpy.spiderCreatorSpy.MCR
				.getReturnValue("createAndStoreRecord", 0);

		assertDataFromSpiderConvertedToJsonUsingConvertersFromProvider(createdRecord);

		assertResponseStatusIs(Response.Status.CREATED);
		assertEquals(response.getLocation().toString(), PLACE + "/someCreatedId");
	}

	@Test
	public void testCreateRecordBodyInJsonWithReplyInXml() {
		response = recordEndpoint.createRecordJsonXml(AUTH_TOKEN, AUTH_TOKEN, PLACE, defaultJson);

		DataRecordGroup recordSentOnToSpider = spiderInstanceFactorySpy.spiderCreatorSpy.record;
		assertJsonStringConvertedToRecordUsesConverter(defaultJson, recordSentOnToSpider);

		DataRecord createdRecord = (DataRecord) spiderInstanceFactorySpy.spiderCreatorSpy.MCR
				.getReturnValue("createAndStoreRecord", 0);

		assertXmlConvertionOfResponse(createdRecord);
		assertEntityExists();
		assertResponseStatusIs(Response.Status.CREATED);
		assertResponseContentTypeIs(APPLICATION_VND_CORA_RECORD_XML);
	}

	@Test
	public void testCreateRecordBodyInXmlWithReplyInJson() {
		response = recordEndpoint.createRecordXmlJson(AUTH_TOKEN, AUTH_TOKEN, PLACE, defaultXml);

		DataRecordGroup dataRecord = assertParametersAndGetConvertedXmlDataRecordGroup();

		spiderInstanceFactorySpy.spiderCreatorSpy.MCR.assertParameters("createAndStoreRecord", 0,
				AUTH_TOKEN, PLACE, dataRecord);

		DataRecord createdRecord = (DataRecord) spiderInstanceFactorySpy.spiderCreatorSpy.MCR
				.getReturnValue("createAndStoreRecord", 0);

		assertDataFromSpiderConvertedToJsonUsingConvertersFromProvider(createdRecord);
		assertEntityExists();
		assertResponseStatusIs(Response.Status.CREATED);
		assertResponseContentTypeIs(APPLICATION_VND_CORA_RECORD_JSON);
	}

	@Test
	public void testCreateRecordForXmlXml() {
		response = recordEndpoint.createRecordXmlXml(AUTH_TOKEN, AUTH_TOKEN, PLACE, defaultXml);

		DataRecordGroup dataRecord = assertParametersAndGetConvertedXmlDataRecordGroup();

		spiderInstanceFactorySpy.spiderCreatorSpy.MCR.assertParameters("createAndStoreRecord", 0,
				AUTH_TOKEN, PLACE, dataRecord);

		DataRecord createdRecord = (DataRecord) spiderInstanceFactorySpy.spiderCreatorSpy.MCR
				.getReturnValue("createAndStoreRecord", 0);

		assertXmlConvertionOfResponse(createdRecord);
		assertEntityExists();
		assertResponseStatusIs(Response.Status.CREATED);
		assertResponseContentTypeIs(APPLICATION_VND_CORA_RECORD_XML);
	}

	@Test
	public void testAnnotationsForCreateRecordJsonJson() throws Exception {
		AnnotationTestHelper annotationHelper = AnnotationTestHelper
				.createAnnotationTestHelperForClassMethodNameAndNumOfParameters(
						recordEndpoint.getClass(), "createRecordJsonJson", 4);

		annotationHelper.assertHttpMethodAndPathAnnotation("POST", "{type}");
		annotationHelper.assertConsumesAnnotation(APPLICATION_VND_CORA_RECORDGROUP_JSON);
		annotationHelper.assertProducesAnnotation(APPLICATION_VND_CORA_RECORD_JSON_QS09);
		annotationHelper.assertAnnotationForAuthTokensAndTypeParameters();
	}

	@Test
	public void testAnnotationsForCreateRecordJsonXml() throws Exception {
		AnnotationTestHelper annotationHelper = AnnotationTestHelper
				.createAnnotationTestHelperForClassMethodNameAndNumOfParameters(
						recordEndpoint.getClass(), "createRecordJsonXml", 4);

		annotationHelper.assertHttpMethodAndPathAnnotation("POST", "{type}");
		annotationHelper.assertConsumesAnnotation(APPLICATION_VND_CORA_RECORDGROUP_JSON);
		annotationHelper.assertProducesAnnotation(APPLICATION_VND_CORA_RECORD_XML);
		annotationHelper.assertAnnotationForAuthTokensAndTypeParameters();
	}

	@Test
	public void testAnnotationsForCreateRecordXmlJson() throws Exception {
		AnnotationTestHelper annotationHelper = AnnotationTestHelper
				.createAnnotationTestHelperForClassMethodNameAndNumOfParameters(
						recordEndpoint.getClass(), "createRecordXmlJson", 4);

		annotationHelper.assertHttpMethodAndPathAnnotation("POST", "{type}");
		annotationHelper.assertConsumesAnnotation(APPLICATION_VND_CORA_RECORDGROUP_XML);
		annotationHelper.assertProducesAnnotation(APPLICATION_VND_CORA_RECORD_JSON_QS09);
		annotationHelper.assertAnnotationForAuthTokensAndTypeParameters();
	}

	@Test
	public void testAnnotationsForCreateRecordXmlXml() throws Exception {
		AnnotationTestHelper annotationHelper = AnnotationTestHelper
				.createAnnotationTestHelperForClassMethodNameAndNumOfParameters(
						recordEndpoint.getClass(), "createRecordXmlXml", 4);

		annotationHelper.assertHttpMethodAndPathAnnotation("POST", "{type}");
		annotationHelper.assertConsumesAnnotation(APPLICATION_VND_CORA_RECORDGROUP_XML);
		annotationHelper.assertProducesAnnotation(APPLICATION_VND_CORA_RECORD_XML);
		annotationHelper.assertAnnotationForAuthTokensAndTypeParameters();
	}

	@Test
	public void testCreateRecordBadCreatedLocation() {
		String type = "place&& &&\\\\";
		response = recordEndpoint.createRecordJsonJson(AUTH_TOKEN, AUTH_TOKEN, type, defaultJson);
		assertResponseStatusIs(Response.Status.BAD_REQUEST);
		assertNull(response.getEntity());
	}

	@Test
	public void testCreateRecordUnauthorized() {
		response = recordEndpoint.createRecordJsonJson(DUMMY_NON_AUTHORIZED_TOKEN, AUTH_TOKEN,
				PLACE, defaultJson);
		assertResponseStatusIs(Response.Status.FORBIDDEN);
	}

	@Test
	public void testCreateNonExistingRecordType() {
		String type = "recordType_NON_EXISTING";
		response = recordEndpoint.createRecordJsonJson(AUTH_TOKEN, AUTH_TOKEN, type, defaultJson);
		assertResponseStatusIs(Response.Status.NOT_FOUND);
	}

	@Test
	public void testCreateRecordNotValid() {
		response = recordEndpoint.createRecordJsonJson(AUTH_TOKEN, AUTH_TOKEN, "place_NON_VALID",
				defaultJson);
		assertResponseStatusIs(Response.Status.BAD_REQUEST);
		assertEquals(response.getEntity(),
				"Error creating new record for recordType: place_NON_VALID. Data is not valid");
	}

	@Test
	public void testCreateRecordConversionException() {
		jsonToDataConverterFactorySpy.throwError = true;
		response = recordEndpoint.createRecordJsonJson("someToken78678567", AUTH_TOKEN, PLACE,
				defaultJson);
		assertResponseStatusIs(Response.Status.BAD_REQUEST);
		assertEquals(response.getEntity(), "Error creating new record for recordType: " + PLACE
				+ ". Error from converter spy");
	}

	@Test
	public void testCreateRecordAbstractRecordType() {
		String type = "abstract";
		response = recordEndpoint.createRecordJsonJson(AUTH_TOKEN, AUTH_TOKEN, type, defaultJson);
		assertResponseStatusIs(Response.Status.METHOD_NOT_ALLOWED);
	}

	@Test
	public void testCreateRecordDuplicateFromSpider() {
		response = recordEndpoint.createRecordJsonJson(AUTH_TOKEN, AUTH_TOKEN,
				"place_duplicate_spider", defaultJson);
		assertResponseStatusIs(Response.Status.CONFLICT);
		assertResponseContentTypeIs(TEXT_PLAIN);
		assertEquals(response.getEntity(), "Record already exists in spider");
	}

	@Test
	public void testCreateRecordDuplicateUserSuppliedId() {
		response = recordEndpoint.createRecordJsonJson(AUTH_TOKEN, AUTH_TOKEN, "place_duplicate",
				defaultJson);
		assertResponseStatusIs(Response.Status.CONFLICT);
		assertResponseContentTypeIs(TEXT_PLAIN);
	}

	@Test
	public void testCreateRecordUnexpectedError() {
		response = recordEndpoint.createRecordJsonJson(AUTH_TOKEN, AUTH_TOKEN,
				"place_unexpected_error", defaultJson);

		assertResponseStatusIs(Response.Status.INTERNAL_SERVER_ERROR);

		LoggerSpy loggerSpy = (LoggerSpy) loggerFactorySpy.MCR.getReturnValue("factorForClass", 0);
		loggerSpy.MCR.assertNumberOfCallsToMethod("logErrorUsingMessageAndException", 1);
		loggerSpy.MCR.assertParameter("logErrorUsingMessageAndException", 0, "message",
				"Error handling request: Some error");
		var caughtException = loggerSpy.MCR.getParameterForMethodAndCallNumberAndParameter(
				"logErrorUsingMessageAndException", 0, "exception");
		assertTrue(caughtException instanceof NullPointerException);
	}

}