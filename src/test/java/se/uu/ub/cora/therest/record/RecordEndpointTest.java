/*
 * Copyright 2015, 2016, 2018, 2021 Uppsala University Library
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition.FormDataContentDispositionBuilder;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.data.Convertible;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataList;
import se.uu.ub.cora.data.DataRecord;
import se.uu.ub.cora.data.converter.DataToJsonConverterProvider;
import se.uu.ub.cora.data.converter.JsonToDataConverterProvider;
import se.uu.ub.cora.json.parser.JsonValue;
import se.uu.ub.cora.json.parser.org.OrgJsonParser;
import se.uu.ub.cora.logger.LoggerProvider;
import se.uu.ub.cora.spider.dependency.SpiderInstanceProvider;
import se.uu.ub.cora.therest.coradata.DataListSpy;
import se.uu.ub.cora.therest.coradata.DataRecordSpy;
import se.uu.ub.cora.therest.log.LoggerFactorySpy;

public class RecordEndpointTest {
	private static final String INDEX_BATCH_JOB = "indexBatchJob";
	private static final String DUMMY_NON_AUTHORIZED_TOKEN = "dummyNonAuthorizedToken";
	private static final String PLACE_0001 = "place:0001";
	private static final String PLACE = "place";
	private static final String AUTH_TOKEN = "authToken";
	private JsonParserSpy jsonParser;

	JsonToDataConverterFactorySpy jsonToDataConverterFactorySpy = new JsonToDataConverterFactorySpy();

	private RecordEndpoint recordEndpoint;
	private SpiderInstanceFactorySpy spiderInstanceFactorySpy;
	private Response response;
	private HttpServletRequestSpy requestSpy;
	private Map<String, String> initInfo = new HashMap<>();
	private LoggerFactorySpy loggerFactorySpy;
	private String testedClassName = "RecordEndpoint";

	private String jsonToValidate = "{\"order\":{\"name\":\"validationOrder\",\"children\":[{\"name\":\"recordInfo\",\"children\":[{\"name\":\"dataDivider\",\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"system\"},{\"name\":\"linkedRecordId\",\"value\":\"testSystem\"}]}]},{\"name\":\"recordType\",\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"recordType\"},{\"name\":\"linkedRecordId\",\"value\":\"someRecordType\"}]},{\"name\":\"metadataToValidate\",\"value\":\"existing\"},{\"name\":\"validateLinks\",\"value\":\"false\"}]},\"record\":{\"name\":\"text\",\"children\":[{\"name\":\"recordInfo\",\"children\":[{\"name\":\"id\",\"value\":\"workOrderRecordIdTextVar2Text\"},{\"name\":\"dataDivider\",\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"system\"},{\"name\":\"linkedRecordId\",\"value\":\"cora\"}]}]},{\"name\":\"textPart\",\"children\":[{\"name\":\"text\",\"value\":\"Id på länkad post\"}],\"attributes\":{\"type\":\"default\",\"lang\":\"sv\"}},{\"name\":\"textPart\",\"children\":[{\"name\":\"text\",\"value\":\"Linked record id\"}],\"attributes\":{\"type\":\"alternative\",\"lang\":\"en\"}}]}}";
	private String defaultJson = "{\"name\":\"someRecordType\",\"children\":[]}";
	private String jsonFilterData = "{\"name\":\"filter\",\"children\":[{\"name\":\"part\",\"children\":[{\"name\":\"key\",\"value\":\"movieTitle\"},{\"name\":\"value\",\"value\":\"Some title\"}],\"repeatId\":\"0\"}]}";
	private DataToJsonConverterFactoryCreatorSpy converterFactoryCreatorSpy;
	private String standardBaseUrlHttp = "http://cora.epc.ub.uu.se/systemone/rest/record/";
	private String standardBaseUrlHttps = "https://cora.epc.ub.uu.se/systemone/rest/record/";

	@BeforeMethod
	public void beforeMethod() {
		converterFactoryCreatorSpy = new DataToJsonConverterFactoryCreatorSpy();
		DataToJsonConverterProvider
				.setDataToJsonConverterFactoryCreator(converterFactoryCreatorSpy);

		jsonToDataConverterFactorySpy = new JsonToDataConverterFactorySpy();
		JsonToDataConverterProvider.setJsonToDataConverterFactory(jsonToDataConverterFactorySpy);
		loggerFactorySpy = new LoggerFactorySpy();
		LoggerProvider.setLoggerFactory(loggerFactorySpy);
		initInfo.put("theRestPublicPathToSystem", "/systemone/rest/");
		spiderInstanceFactorySpy = new SpiderInstanceFactorySpy();
		SpiderInstanceProvider.setSpiderInstanceFactory(spiderInstanceFactorySpy);
		SpiderInstanceProvider.setInitInfo(initInfo);

		requestSpy = new HttpServletRequestSpy();
		recordEndpoint = new RecordEndpoint(requestSpy);

		setUpSpiesInRecordEndpoint();

	}

	private void setUpSpiesInRecordEndpoint() {
		jsonParser = new JsonParserSpy();

		recordEndpoint.setJsonParser(jsonParser);
	}

	@Test
	public void testInit() {
		recordEndpoint = new RecordEndpoint(requestSpy);
		assertTrue(recordEndpoint.getJsonParser() instanceof OrgJsonParser);
	}

	@Test
	public void testXForwardedProtoHttps() {
		requestSpy.headers.put("X-Forwarded-Proto", "https");
		recordEndpoint = new RecordEndpoint(requestSpy);

		response = recordEndpoint.readRecord(AUTH_TOKEN, AUTH_TOKEN, PLACE, PLACE_0001);
		DataToJsonConverterFactorySpy converterFactory = (DataToJsonConverterFactorySpy) converterFactoryCreatorSpy.MCR
				.getReturnValue("createFactory", 0);

		converterFactory.MCR.assertParameters("factorUsingBaseUrlAndConvertible", 0,
				standardBaseUrlHttps);
	}

	@Test
	public void testXForwardedProtoHttpsWhenAlreadyHttpsInRequestUrl() {
		requestSpy.headers.put("X-Forwarded-Proto", "https");
		requestSpy.requestURL = new StringBuffer(
				"https://cora.epc.ub.uu.se/systemone/rest/record/text/");
		recordEndpoint = new RecordEndpoint(requestSpy);

		response = recordEndpoint.readRecord(AUTH_TOKEN, AUTH_TOKEN, PLACE, PLACE_0001);
		DataToJsonConverterFactorySpy converterFactory = (DataToJsonConverterFactorySpy) converterFactoryCreatorSpy.MCR
				.getReturnValue("createFactory", 0);

		converterFactory.MCR.assertParameters("factorUsingBaseUrlAndConvertible", 0,
				standardBaseUrlHttps);
	}

	@Test
	public void testXForwardedProtoEmpty() {
		requestSpy.headers.put("X-Forwarded-Proto", "");
		recordEndpoint = new RecordEndpoint(requestSpy);

		response = recordEndpoint.readRecord(AUTH_TOKEN, AUTH_TOKEN, PLACE, PLACE_0001);
		DataToJsonConverterFactorySpy converterFactory = (DataToJsonConverterFactorySpy) converterFactoryCreatorSpy.MCR
				.getReturnValue("createFactory", 0);

		converterFactory.MCR.assertParameters("factorUsingBaseUrlAndConvertible", 0,
				standardBaseUrlHttp);
	}

	@Test
	public void testPreferredTokenForReadList() throws IOException {
		expectTokenForReadListToPrefereblyBeHeaderThanQuery(AUTH_TOKEN, "authToken2", AUTH_TOKEN);
		expectTokenForReadListToPrefereblyBeHeaderThanQuery(null, AUTH_TOKEN, AUTH_TOKEN);
		expectTokenForReadListToPrefereblyBeHeaderThanQuery(AUTH_TOKEN, null, AUTH_TOKEN);
		expectTokenForReadListToPrefereblyBeHeaderThanQuery(null, null, null);
	}

	private void expectTokenForReadListToPrefereblyBeHeaderThanQuery(String headerAuthToken,
			String queryAuthToken, String authTokenExpected) {
		String jsonFilter = "{\"name\":\"filter\",\"children\":[]}";
		response = recordEndpoint.readRecordList(headerAuthToken, queryAuthToken, PLACE,
				jsonFilter);

		SpiderRecordListReaderSpy spiderListReaderSpy = spiderInstanceFactorySpy.spiderRecordListReaderSpy;
		assertEquals(spiderListReaderSpy.authToken, authTokenExpected);
	}

	@Test
	public void testReadRecordListWithFilter() {
		response = recordEndpoint.readRecordList(AUTH_TOKEN, AUTH_TOKEN, PLACE, jsonFilterData);

		SpiderRecordListReaderSpy spiderListReaderSpy = spiderInstanceFactorySpy.spiderRecordListReaderSpy;

		DataGroup filterSentOnToSpider = spiderInstanceFactorySpy.spiderRecordListReaderSpy.filter;
		assertJsonStringConvertedToDataUsesCoraData(jsonFilterData, filterSentOnToSpider);

		DataListSpy returnedDataListFromReader = spiderListReaderSpy.returnedDataList;

		assertDataFromSpiderConvertedToJsonUsingConvertersFromProvider(returnedDataListFromReader);

		assertResponseStatusIs(Response.Status.OK);
	}

	@Test
	public void testReadRecordListWithNullAsFilter() {
		response = recordEndpoint.readRecordList(AUTH_TOKEN, AUTH_TOKEN, PLACE, null);
		assertEntityExists();
		assertResponseStatusIs(Response.Status.OK);

		assertEquals(jsonParser.jsonString, "{\"name\":\"filter\",\"children\":[]}");

	}

	private void assertEntityExists() {
		assertNotNull(response.getEntity(), "An entity in json format should be returned");
	}

	private void assertResponseStatusIs(Status responseStatus) {
		assertEquals(response.getStatusInfo(), responseStatus);
	}

	@Test
	public void testReadRecordListNotFound() {
		String jsonFilter = "{\"name\":\"filter\",\"children\":[]}";
		response = recordEndpoint.readRecordList(AUTH_TOKEN, AUTH_TOKEN, "place_NOT_FOUND",
				jsonFilter);
		assertResponseStatusIs(Response.Status.NOT_FOUND);
	}

	@Test
	public void testReadRecordListUnauthorized() {
		String jsonFilter = "{\"name\":\"filter\",\"children\":[]}";
		response = recordEndpoint.readRecordListUsingAuthTokenByType(DUMMY_NON_AUTHORIZED_TOKEN,
				PLACE, jsonFilter);
		assertResponseStatusIs(Response.Status.FORBIDDEN);
	}

	@Test
	public void testReadRecordListNoTokenAndUnauthorized() {
		String jsonFilter = "{\"name\":\"filter\",\"children\":[]}";
		response = recordEndpoint.readRecordListUsingAuthTokenByType(null, PLACE, jsonFilter);
		assertResponseStatusIs(Response.Status.UNAUTHORIZED);
	}

	@Test
	public void testPreferredTokenForRead() throws IOException {
		expectTokenForReadToPreferablyBeHeaderThanQuery(AUTH_TOKEN, "authToken2", AUTH_TOKEN);
		expectTokenForReadToPreferablyBeHeaderThanQuery(null, AUTH_TOKEN, AUTH_TOKEN);
		expectTokenForReadToPreferablyBeHeaderThanQuery(AUTH_TOKEN, null, AUTH_TOKEN);
		expectTokenForReadToPreferablyBeHeaderThanQuery(null, null, null);
	}

	private void expectTokenForReadToPreferablyBeHeaderThanQuery(String headerAuthToken,
			String queryAuthToken, String authTokenExpected) {

		response = recordEndpoint.readRecord(headerAuthToken, queryAuthToken, PLACE, PLACE_0001);

		SpiderRecordReaderSpy spiderReaderSpy = spiderInstanceFactorySpy.spiderRecordReaderSpy;
		assertEquals(spiderReaderSpy.authToken, authTokenExpected);
	}

	@Test
	public void testReadRecord() {
		response = recordEndpoint.readRecord(AUTH_TOKEN, AUTH_TOKEN, PLACE, PLACE_0001);
		assertEntityExists();
		assertResponseStatusIs(Response.Status.OK);
	}

	@Test
	public void testReadRecordUsesToRestConverterFactory() {

		response = recordEndpoint.readRecord(AUTH_TOKEN, AUTH_TOKEN, PLACE, PLACE_0001);

		DataRecord recordReturnedFromReader = spiderInstanceFactorySpy.spiderRecordReaderSpy.dataRecord;

		assertDataFromSpiderConvertedToJsonUsingConvertersFromProvider(recordReturnedFromReader);
	}

	private void assertDataFromSpiderConvertedToJsonUsingConvertersFromProvider(
			Convertible convertible) {
		DataToJsonConverterFactorySpy converterFactory = (DataToJsonConverterFactorySpy) converterFactoryCreatorSpy.MCR
				.getReturnValue("createFactory", 0);

		converterFactory.MCR.assertParameters("factorUsingBaseUrlAndConvertible", 0,
				standardBaseUrlHttp, convertible);

		DataToJsonConverterSpy converterSpy = (DataToJsonConverterSpy) converterFactory.MCR
				.getReturnValue("factorUsingBaseUrlAndConvertible", 0);

		var entity = response.getEntity();
		converterSpy.MCR.assertReturn("toJsonCompactFormat", 0, entity);
	}

	@Test
	public void testReadRecordUnauthenticated() {
		response = recordEndpoint.readRecordUsingAuthTokenByTypeAndId("dummyNonAuthenticatedToken",
				PLACE, PLACE_0001);
		assertResponseStatusIs(Response.Status.UNAUTHORIZED);
	}

	@Test
	public void testReadRecordUnauthorized() {
		response = recordEndpoint.readRecordUsingAuthTokenByTypeAndId(DUMMY_NON_AUTHORIZED_TOKEN,
				PLACE, PLACE_0001);
		assertResponseStatusIs(Response.Status.FORBIDDEN);
	}

	@Test
	public void testReadRecordNotFound() {
		response = recordEndpoint.readRecord(AUTH_TOKEN, AUTH_TOKEN, PLACE, "place:0001_NOT_FOUND");
		assertResponseStatusIs(Response.Status.NOT_FOUND);
	}

	@Test
	public void testReadRecordAbstractRecordType() {
		response = recordEndpoint.readRecord(AUTH_TOKEN, AUTH_TOKEN, "binary", "image:123456789");
		assertResponseStatusIs(Response.Status.OK);
	}

	@Test
	public void testPreferredTokenForReadIncomingLinks() throws IOException {
		expectTokenForReadIncomingLinksToPrefereblyBeHeaderThanQuery(AUTH_TOKEN, "authToken2",
				AUTH_TOKEN);
		expectTokenForReadIncomingLinksToPrefereblyBeHeaderThanQuery(null, AUTH_TOKEN, AUTH_TOKEN);
		expectTokenForReadIncomingLinksToPrefereblyBeHeaderThanQuery(AUTH_TOKEN, null, AUTH_TOKEN);
		expectTokenForReadIncomingLinksToPrefereblyBeHeaderThanQuery(null, null, null);
	}

	private void expectTokenForReadIncomingLinksToPrefereblyBeHeaderThanQuery(
			String headerAuthToken, String queryAuthToken, String authTokenExpected) {

		response = recordEndpoint.readIncomingRecordLinks(headerAuthToken, queryAuthToken, PLACE,
				PLACE_0001);

		SpiderRecordIncomingLinksReaderSpy spiderIncomingLinksReaderSpy = spiderInstanceFactorySpy.spiderRecordIncomingLinksReaderSpy;
		assertEquals(spiderIncomingLinksReaderSpy.authToken, authTokenExpected);
	}

	@Test
	public void testReadIncomingRecordLinks() {
		response = recordEndpoint.readIncomingRecordLinks(AUTH_TOKEN, AUTH_TOKEN, PLACE,
				PLACE_0001);
		DataList dataList = (DataList) spiderInstanceFactorySpy.spiderRecordIncomingLinksReaderSpy.MCR
				.getReturnValue("readIncomingLinks", 0);

		assertDataFromSpiderConvertedToJsonUsingConvertersFromProvider(dataList);
		assertResponseStatusIs(Response.Status.OK);
	}

	@Test
	public void testReadIncomingLinksUnauthorized() {
		response = recordEndpoint.readIncomingRecordLinksUsingAuthTokenByTypeAndId(
				DUMMY_NON_AUTHORIZED_TOKEN, PLACE, PLACE_0001);
		assertResponseStatusIs(Response.Status.FORBIDDEN);
	}

	@Test
	public void testReadIncomingLinksNotFound() {
		response = recordEndpoint.readIncomingRecordLinks(AUTH_TOKEN, AUTH_TOKEN, PLACE,
				"place:0001_NOT_FOUND");
		assertResponseStatusIs(Response.Status.NOT_FOUND);
	}

	@Test
	public void testReadIncomingLinksAbstractRecordType() {
		String type = "abstract";
		response = recordEndpoint.readIncomingRecordLinks(AUTH_TOKEN, AUTH_TOKEN, type,
				"canBeWhatEverIdTypeIsChecked");
		assertResponseStatusIs(Response.Status.METHOD_NOT_ALLOWED);
	}

	@Test
	public void testPreferredTokenForDelete() throws IOException {
		expectTokenForDeleteToPreferablyBeHeaderThanQuery(AUTH_TOKEN, "authToken2", AUTH_TOKEN);
		expectTokenForDeleteToPreferablyBeHeaderThanQuery(null, AUTH_TOKEN, AUTH_TOKEN);
		expectTokenForDeleteToPreferablyBeHeaderThanQuery(AUTH_TOKEN, null, AUTH_TOKEN);
		expectTokenForDeleteToPreferablyBeHeaderThanQuery(null, null, null);
	}

	private void expectTokenForDeleteToPreferablyBeHeaderThanQuery(String headerAuthToken,
			String queryAuthToken, String authTokenExpected) {

		response = recordEndpoint.deleteRecord(headerAuthToken, queryAuthToken, PLACE,
				"place:0002");

		SpiderRecordDeleterSpy spiderDeleterSpy = spiderInstanceFactorySpy.spiderRecordDeleterSpy;
		assertEquals(spiderDeleterSpy.authToken, authTokenExpected);
	}

	@Test
	public void testDeleteRecordNoIncomingLinks() {
		response = recordEndpoint.deleteRecord(AUTH_TOKEN, AUTH_TOKEN, PLACE, "place:0002");
		assertResponseStatusIs(Response.Status.OK);
	}

	@Test
	public void testDeleteRecordIncomingLinks() {
		response = recordEndpoint.deleteRecord(AUTH_TOKEN, AUTH_TOKEN, PLACE, PLACE_0001);
		assertResponseStatusIs(Response.Status.METHOD_NOT_ALLOWED);
	}

	@Test
	public void testDeleteRecordUnauthorized() {
		response = recordEndpoint.deleteRecordUsingAuthTokenByTypeAndId(DUMMY_NON_AUTHORIZED_TOKEN,
				PLACE, PLACE_0001);
		assertResponseStatusIs(Response.Status.FORBIDDEN);
	}

	@Test
	public void testDeleteRecordNotFound() {
		response = recordEndpoint.deleteRecordUsingAuthTokenByTypeAndId("someToken78678567", PLACE,
				"place:0001_NOT_FOUND");
		assertResponseStatusIs(Response.Status.NOT_FOUND);
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

		response = recordEndpoint.updateRecord(headerAuthToken, queryAuthToken, PLACE, PLACE_0001,
				defaultJson);

		SpiderRecordUpdaterSpy spiderUpdaterSpy = spiderInstanceFactorySpy.spiderRecordUpdaterSpy;
		assertEquals(spiderUpdaterSpy.authToken, authTokenExpected);
	}

	@Test
	public void testUpdateRecord() {
		response = recordEndpoint.updateRecord(AUTH_TOKEN, AUTH_TOKEN, PLACE, PLACE_0001,
				defaultJson);
		assertResponseStatusIs(Response.Status.OK);
	}

	@Test
	public void testUpdateRecordUsesFactories() {
		response = recordEndpoint.updateRecord(AUTH_TOKEN, AUTH_TOKEN, PLACE, PLACE_0001,
				defaultJson);

		DataGroup recordSentOnToSpider = spiderInstanceFactorySpy.spiderRecordUpdaterSpy.record;
		assertJsonStringConvertedToDataUsesCoraData(defaultJson, recordSentOnToSpider);
	}

	private void assertJsonStringConvertedToDataUsesCoraData(String jsonSentToEndPoint,
			DataGroup recordSentOnToSpider) {
		assertSame(jsonParser.jsonString, jsonSentToEndPoint);
		assertSame(jsonToDataConverterFactorySpy.jsonValue, jsonParser.returnedJsonValue);
		JsonToDataConverterSpy jsonToDataConverterSpy = jsonToDataConverterFactorySpy.jsonToDataConverterSpy;
		DataGroup returnedDataPart = jsonToDataConverterSpy.dataPartToReturn;
		assertSame(recordSentOnToSpider, returnedDataPart);
	}

	@Test
	public void testUpdateRecordUnauthorized() {
		response = recordEndpoint.updateRecordUsingAuthTokenWithRecord(DUMMY_NON_AUTHORIZED_TOKEN,
				PLACE, PLACE_0001, defaultJson);
		assertResponseStatusIs(Response.Status.FORBIDDEN);
	}

	@Test
	public void testUpdateRecordNotFound() {
		response = recordEndpoint.updateRecord(AUTH_TOKEN, AUTH_TOKEN, PLACE,
				PLACE_0001 + "_NOT_FOUND", defaultJson);
		assertResponseStatusIs(Response.Status.NOT_FOUND);
	}

	@Test
	public void testUpdateRecordTypeNotFound() {
		response = recordEndpoint.updateRecord(AUTH_TOKEN, AUTH_TOKEN, PLACE + "_NOT_FOUND",
				PLACE_0001, defaultJson);
		assertResponseStatusIs(Response.Status.NOT_FOUND);
	}

	@Test
	public void testUpdateRecordBadContentInJson() {
		jsonParser.throwError = true;
		response = recordEndpoint.updateRecord(AUTH_TOKEN, AUTH_TOKEN, PLACE, PLACE_0001,
				defaultJson);
		assertResponseStatusIs(Response.Status.BAD_REQUEST);
	}

	@Test
	public void testUpdateRecordWrongDataTypeInJson() {
		spiderInstanceFactorySpy.throwDataException = true;
		response = recordEndpoint.updateRecord(AUTH_TOKEN, AUTH_TOKEN, PLACE, PLACE_0001,
				defaultJson);
		assertResponseStatusIs(Response.Status.BAD_REQUEST);
	}

	@Test
	public void testPreferredTokenForCreate() throws IOException {
		expectTokenForCreateToPreferablyBeHeaderThanQuery(AUTH_TOKEN, "authToken2", AUTH_TOKEN);
		expectTokenForCreateToPreferablyBeHeaderThanQuery(null, AUTH_TOKEN, AUTH_TOKEN);
		expectTokenForCreateToPreferablyBeHeaderThanQuery(AUTH_TOKEN, null, AUTH_TOKEN);
		expectTokenForCreateToPreferablyBeHeaderThanQuery(null, null, null);
	}

	private void expectTokenForCreateToPreferablyBeHeaderThanQuery(String headerAuthToken,
			String queryAuthToken, String authTokenExpected) {

		response = recordEndpoint.createRecord(headerAuthToken, queryAuthToken, PLACE, defaultJson);

		SpiderCreatorSpy spiderCreatorSpy = spiderInstanceFactorySpy.spiderCreatorSpy;
		assertEquals(spiderCreatorSpy.authToken, authTokenExpected);
	}

	@Test
	public void testCreateRecord() {
		response = recordEndpoint.createRecord(AUTH_TOKEN, AUTH_TOKEN, PLACE, defaultJson);

		assertResponseStatusIs(Response.Status.CREATED);
		assertTrue(response.getLocation().toString().startsWith("record/" + PLACE));
	}

	@Test
	public void testCreateRecordUsesFactories() {

		response = recordEndpoint.createRecord(AUTH_TOKEN, AUTH_TOKEN, PLACE, defaultJson);

		DataGroup recordSentOnToSpider = spiderInstanceFactorySpy.spiderCreatorSpy.record;
		assertJsonStringConvertedToDataUsesCoraData(defaultJson, recordSentOnToSpider);
		DataRecord createdRecord = (DataRecord) spiderInstanceFactorySpy.spiderCreatorSpy.MCR
				.getReturnValue("createAndStoreRecord", 0);

		assertDataFromSpiderConvertedToJsonUsingConvertersFromProvider(createdRecord);

		assertResponseStatusIs(Response.Status.CREATED);
		assertTrue(response.getLocation().toString().startsWith("record/" + PLACE));
	}

	@Test
	public void testCreateRecordBadCreatedLocation() {
		String type = "place&& &&\\\\";
		response = recordEndpoint.createRecord(AUTH_TOKEN, AUTH_TOKEN, type, defaultJson);
		assertResponseStatusIs(Response.Status.BAD_REQUEST);
	}

	@Test
	public void testCreateRecordUnauthorized() {
		response = recordEndpoint.createRecordUsingAuthTokenWithRecord(DUMMY_NON_AUTHORIZED_TOKEN,
				PLACE, defaultJson);
		assertResponseStatusIs(Response.Status.FORBIDDEN);
	}

	@Test
	public void testCreateNonExistingRecordType() {
		String type = "recordType_NON_EXISTING";
		response = recordEndpoint.createRecordUsingAuthTokenWithRecord(AUTH_TOKEN, type,
				defaultJson);
		assertResponseStatusIs(Response.Status.NOT_FOUND);
	}

	@Test
	public void testCreateRecordNotValid() {
		response = recordEndpoint.createRecord(AUTH_TOKEN, AUTH_TOKEN, "place_NON_VALID",
				defaultJson);
		assertResponseStatusIs(Response.Status.BAD_REQUEST);
	}

	@Test
	public void testCreateRecordConversionException() {
		jsonToDataConverterFactorySpy.throwError = true;
		response = recordEndpoint.createRecordUsingAuthTokenWithRecord("someToken78678567", PLACE,
				defaultJson);
		assertResponseStatusIs(Response.Status.BAD_REQUEST);
	}

	@Test
	public void testCreateRecordAbstractRecordType() {
		String type = "abstract";
		response = recordEndpoint.createRecord(AUTH_TOKEN, AUTH_TOKEN, type, defaultJson);
		assertResponseStatusIs(Response.Status.METHOD_NOT_ALLOWED);
	}

	@Test
	public void testCreateRecordDuplicateUserSuppliedId() {
		response = recordEndpoint.createRecord(AUTH_TOKEN, AUTH_TOKEN, "place_duplicate",
				defaultJson);
		assertResponseStatusIs(Response.Status.CONFLICT);

	}

	@Test
	public void testCreateRecordUnexpectedError() {
		assertEquals(loggerFactorySpy.getNoOfErrorExceptionsUsingClassName(testedClassName), 0);
		response = recordEndpoint.createRecord(AUTH_TOKEN, AUTH_TOKEN, "place_unexpected_error",
				defaultJson);
		assertResponseStatusIs(Response.Status.INTERNAL_SERVER_ERROR);
		assertEquals(loggerFactorySpy.getNoOfErrorExceptionsUsingClassName(testedClassName), 1);
		assertEquals(loggerFactorySpy.getErrorLogMessageUsingClassNameAndNo(testedClassName, 0),
				"Error handling request: Some error");
		Exception caughtException = loggerFactorySpy
				.getErrorExceptionUsingClassNameAndNo(testedClassName, 0);
		assertTrue(caughtException instanceof NullPointerException);
	}

	@Test
	public void testPreferredTokenForUpload() throws IOException {
		expectTokenForUploadToPreferablyBeHeaderThanQuery(AUTH_TOKEN, "authToken2", AUTH_TOKEN);
		expectTokenForUploadToPreferablyBeHeaderThanQuery(null, AUTH_TOKEN, AUTH_TOKEN);
		expectTokenForUploadToPreferablyBeHeaderThanQuery(AUTH_TOKEN, null, AUTH_TOKEN);
		expectTokenForUploadToPreferablyBeHeaderThanQuery(null, null, null);
	}

	private void expectTokenForUploadToPreferablyBeHeaderThanQuery(String headerAuthToken,
			String queryAuthToken, String authTokenExpected) {

		FormDataContentDispositionBuilder builder = FormDataContentDisposition
				.name("multipart;form-data");
		builder.fileName("adele1.png");
		FormDataContentDisposition formDataContentDisposition = builder.build();
		InputStream stream = new ByteArrayInputStream("a string".getBytes(StandardCharsets.UTF_8));

		response = recordEndpoint.uploadFile(headerAuthToken, queryAuthToken, "image",
				"image:123456789", stream, formDataContentDisposition);

		SpiderUploaderSpy spiderUploaderSpy = spiderInstanceFactorySpy.spiderUploaderSpy;
		assertEquals(spiderUploaderSpy.authToken, authTokenExpected);
	}

	@Test
	public void testUpload() throws ParseException {
		InputStream stream = new ByteArrayInputStream("a string".getBytes(StandardCharsets.UTF_8));

		FormDataContentDispositionBuilder builder = FormDataContentDisposition
				.name("multipart;form-data");
		builder.fileName("adele1.png");
		FormDataContentDisposition formDataContentDisposition = builder.build();

		response = recordEndpoint.uploadFile(AUTH_TOKEN, AUTH_TOKEN, "image", "image:123456789",
				stream, formDataContentDisposition);

		assertResponseStatusIs(Response.Status.OK);
	}

	@Test
	public void testUploadUnauthorized() {
		InputStream stream = new ByteArrayInputStream("a string".getBytes(StandardCharsets.UTF_8));

		response = recordEndpoint.uploadFileUsingAuthTokenWithStream(DUMMY_NON_AUTHORIZED_TOKEN,
				"image", "image:123456789", stream, "someFile.tif");

		assertResponseStatusIs(Response.Status.FORBIDDEN);
	}

	@Test
	public void testUploadNotFound() {
		InputStream stream = new ByteArrayInputStream("a string".getBytes(StandardCharsets.UTF_8));

		FormDataContentDispositionBuilder builder = FormDataContentDisposition
				.name("multipart;form-data");
		builder.fileName("adele1.png");
		FormDataContentDisposition formDataContentDisposition = builder.build();

		response = recordEndpoint.uploadFile(AUTH_TOKEN, AUTH_TOKEN, "image",
				"image:123456789_NOT_FOUND", stream, formDataContentDisposition);

		assertResponseStatusIs(Response.Status.NOT_FOUND);
	}

	@Test
	public void testUploadNotAChildOfBinary() {
		InputStream stream = new ByteArrayInputStream("a string".getBytes(StandardCharsets.UTF_8));

		FormDataContentDispositionBuilder builder = FormDataContentDisposition
				.name("multipart;form-data");
		builder.fileName("adele1.png");
		FormDataContentDisposition formDataContentDisposition = builder.build();

		response = recordEndpoint.uploadFile(AUTH_TOKEN, AUTH_TOKEN, "not_child_of_binary_type",
				"image:123456789", stream, formDataContentDisposition);

		assertResponseStatusIs(Response.Status.METHOD_NOT_ALLOWED);
	}

	@Test
	public void testUploadStreamMissing() {
		FormDataContentDispositionBuilder builder = FormDataContentDisposition
				.name("multipart;form-data");
		builder.fileName("adele1.png");
		FormDataContentDisposition formDataContentDisposition = builder.build();

		response = recordEndpoint.uploadFile(AUTH_TOKEN, AUTH_TOKEN, "image", "image:123456789",
				null, formDataContentDisposition);

		assertResponseStatusIs(Response.Status.BAD_REQUEST);
	}

	@Test
	public void testPreferredTokenForDownload() throws IOException {
		expectTokenForDownloadToPreferablyBeHeaderThanQuery(AUTH_TOKEN, "authToken2", AUTH_TOKEN);
		expectTokenForDownloadToPreferablyBeHeaderThanQuery(null, AUTH_TOKEN, AUTH_TOKEN);
		expectTokenForDownloadToPreferablyBeHeaderThanQuery(AUTH_TOKEN, null, AUTH_TOKEN);
		expectTokenForDownloadToPreferablyBeHeaderThanQuery(null, null, null);
	}

	private void expectTokenForDownloadToPreferablyBeHeaderThanQuery(String headerAuthToken,
			String queryAuthToken, String authTokenExpected) {

		response = recordEndpoint.downloadFile(headerAuthToken, queryAuthToken, "image",
				"image:123456789", "master");

		SpiderDownloaderSpy spiderDownloaderSpy = spiderInstanceFactorySpy.spiderDownloaderSpy;
		assertEquals(spiderDownloaderSpy.authToken, authTokenExpected);
	}

	@Test
	public void testDownloadUnauthorized() throws IOException {
		response = recordEndpoint.downloadFileUsingAuthTokenWithStream(DUMMY_NON_AUTHORIZED_TOKEN,
				"image", "image:123456789", "master");
		assertResponseStatusIs(Response.Status.FORBIDDEN);
	}

	@Test
	public void testDownload() throws IOException {
		response = recordEndpoint.downloadFile(AUTH_TOKEN, AUTH_TOKEN, "image", "image:123456789",
				"master");
		String contentType = response.getHeaderString("Content-Type");
		/*
		 * when we detect and store type of file in spider check it like this
		 * assertEquals(contentType, "application/octet-stream");
		 */
		assertEquals(contentType, "application/octet-stream");
		// assertEquals(contentType, null);
		InputStream stream = (InputStream) response.getEntity();
		assertNotNull(stream);

		ByteArrayOutputStream result = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int length;
		while ((length = stream.read(buffer)) != -1) {
			result.write(buffer, 0, length);
		}
		String stringFromStream = result.toString("UTF-8");

		assertEquals(stringFromStream, "a string out");
		assertResponseStatusIs(Response.Status.OK);

		String contentLength = response.getHeaderString("Content-Length");
		assertEquals(contentLength, "12");

		String contentDisposition = response.getHeaderString("Content-Disposition");
		assertEquals(contentDisposition, "attachment; filename=someFile");
	}

	@Test
	public void testDownloadNotFound() throws IOException {
		response = recordEndpoint.downloadFile(AUTH_TOKEN, AUTH_TOKEN, "image",
				"image:123456789_NOT_FOUND", "master");
		assertResponseStatusIs(Response.Status.NOT_FOUND);
	}

	@Test
	public void testDownloadNotAChildOfBinary() throws IOException {
		response = recordEndpoint.downloadFile(AUTH_TOKEN, AUTH_TOKEN, "not_child_of_binary_type",
				"image:123456789", "master");
		assertResponseStatusIs(Response.Status.METHOD_NOT_ALLOWED);
	}

	@Test
	public void testDownloadBadRequest() throws IOException {
		response = recordEndpoint.downloadFile(AUTH_TOKEN, AUTH_TOKEN, "image", "image:123456789",
				"");
		assertResponseStatusIs(Response.Status.BAD_REQUEST);
	}

	@Test
	public void testSearchRecordInputsReachSpider() {
		response = recordEndpoint.searchRecord(AUTH_TOKEN, AUTH_TOKEN, "aSearchId", defaultJson);

		SpiderRecordSearcherSpy spiderRecordSearcherSpy = spiderInstanceFactorySpy.spiderRecordSearcherSpy;
		assertEquals(spiderRecordSearcherSpy.authToken, AUTH_TOKEN);
		assertEquals(spiderRecordSearcherSpy.searchId, "aSearchId");

		DataGroup searchSentOnToSpider = spiderInstanceFactorySpy.spiderRecordSearcherSpy.searchData;
		assertJsonStringConvertedToDataUsesCoraData(defaultJson, searchSentOnToSpider);
	}

	@Test
	public void testSearchRecordUsesFactoriesCorrectly() {
		response = recordEndpoint.searchRecord(AUTH_TOKEN, AUTH_TOKEN, "aSearchId", defaultJson);

		SpiderRecordSearcherSpy spiderRecordSearcherSpy = spiderInstanceFactorySpy.spiderRecordSearcherSpy;

		DataGroup searchSentOnToSpider = spiderInstanceFactorySpy.spiderRecordSearcherSpy.searchData;
		assertJsonStringConvertedToDataUsesCoraData(defaultJson, searchSentOnToSpider);

		DataList returnedDataListFromReader = spiderRecordSearcherSpy.searchResult;

		assertDataFromSpiderConvertedToJsonUsingConvertersFromProvider(returnedDataListFromReader);
	}

	@Test
	public void testSearchRecordRightTokenInputsReachSpider() {
		response = recordEndpoint.searchRecord(AUTH_TOKEN, null, "aSearchId", defaultJson);
		SpiderRecordSearcherSpy spiderRecordSearcherSpy = spiderInstanceFactorySpy.spiderRecordSearcherSpy;
		assertEquals(spiderRecordSearcherSpy.authToken, AUTH_TOKEN);
	}

	@Test
	public void testSearchRecordRightTokenInputsReachSpider2() {
		response = recordEndpoint.searchRecord(null, AUTH_TOKEN, "aSearchId", defaultJson);
		SpiderRecordSearcherSpy spiderRecordSearcherSpy = spiderInstanceFactorySpy.spiderRecordSearcherSpy;
		assertEquals(spiderRecordSearcherSpy.authToken, AUTH_TOKEN);
	}

	@Test
	public void testSearchRecordRightTokenInputsReachSpider3() {
		response = recordEndpoint.searchRecord(AUTH_TOKEN, "otherAuthToken", "aSearchId",
				defaultJson);
		SpiderRecordSearcherSpy spiderRecordSearcherSpy = spiderInstanceFactorySpy.spiderRecordSearcherSpy;
		assertEquals(spiderRecordSearcherSpy.authToken, AUTH_TOKEN);
	}

	@Test
	public void testSearchRecord() {
		response = recordEndpoint.searchRecord(AUTH_TOKEN, AUTH_TOKEN, "aSearchId", defaultJson);
		assertEntityExists();
		assertResponseStatusIs(Response.Status.OK);
	}

	@Test
	public void testSearchRecordSearchIdNotFound() {
		response = recordEndpoint.searchRecord(AUTH_TOKEN, AUTH_TOKEN, "aSearchId_NOT_FOUND",
				defaultJson);
		assertResponseStatusIs(Response.Status.NOT_FOUND);
	}

	@Test
	public void testSearchRecordInvalidSearchData() {
		response = recordEndpoint.searchRecord(AUTH_TOKEN, AUTH_TOKEN, "aSearchId_INVALID_DATA",
				defaultJson);
		assertResponseStatusIs(Response.Status.BAD_REQUEST);
	}

	@Test
	public void testSearchRecordUnauthorized() {
		response = recordEndpoint.searchRecord(DUMMY_NON_AUTHORIZED_TOKEN,
				DUMMY_NON_AUTHORIZED_TOKEN, "aSearchId", defaultJson);
		assertResponseStatusIs(Response.Status.FORBIDDEN);
	}

	@Test
	public void testSearchRecordUnauthenticated() {
		response = recordEndpoint.searchRecord("nonExistingToken", "nonExistingToken", "aSearchId",
				defaultJson);
		assertResponseStatusIs(Response.Status.UNAUTHORIZED);
	}

	@Test
	public void testPreferredTokenForValidate() throws IOException {
		expectTokenForValidateToPrefereblyBeHeaderThanQuery(AUTH_TOKEN, "authToken2", AUTH_TOKEN);
		expectTokenForValidateToPrefereblyBeHeaderThanQuery(null, AUTH_TOKEN, AUTH_TOKEN);
		expectTokenForValidateToPrefereblyBeHeaderThanQuery(AUTH_TOKEN, null, AUTH_TOKEN);
		expectTokenForValidateToPrefereblyBeHeaderThanQuery(null, null, null);
	}

	private void expectTokenForValidateToPrefereblyBeHeaderThanQuery(String headerAuthToken,
			String queryAuthToken, String authTokenExpected) {

		response = recordEndpoint.validateRecord(headerAuthToken, queryAuthToken, PLACE,
				jsonToValidate);

		SpiderRecordValidatorSpy spiderRecordValidatorSpy = spiderInstanceFactorySpy.spiderRecordValidatorSpy;

		assertEquals(spiderRecordValidatorSpy.authToken, authTokenExpected);
	}

	@Test
	public void testValidateRecord() {
		response = recordEndpoint.validateRecord(AUTH_TOKEN, AUTH_TOKEN, "workOrder", defaultJson);

		assertEquals(jsonParser.jsonString, defaultJson);

		// parts requested from validationJson sent in
		JsonValueSpy topJsonObject = jsonParser.returnedJsonValue;
		assertEquals(topJsonObject.keys.get(0), "order");
		assertEquals(topJsonObject.keys.get(1), "record");

		DataGroup validationOrderSentOnToSpider = spiderInstanceFactorySpy.spiderRecordValidatorSpy.validationOrder;
		DataGroup recordToValidateSentOnToSpider = spiderInstanceFactorySpy.spiderRecordValidatorSpy.recordToValidate;

		JsonValue orderDataFromParser = jsonParser.returnedJsonValue.returnedJsonValues.get(0);
		JsonValue firstSentToConverterFactory = jsonToDataConverterFactorySpy.jsonValues.get(0);
		assertSame(firstSentToConverterFactory, orderDataFromParser);

		JsonValue recordDataFromParser = jsonParser.returnedJsonValue.returnedJsonValues.get(1);
		JsonValue secondSentToConverterFactory = jsonToDataConverterFactorySpy.jsonValues.get(1);
		assertSame(secondSentToConverterFactory, recordDataFromParser);

		var converterSpyForOrder = jsonToDataConverterFactorySpy.jsonToDataConverterSpies.get(0);
		DataGroup orderReturnedDataPart = converterSpyForOrder.dataPartToReturn;
		assertSame(validationOrderSentOnToSpider, orderReturnedDataPart);

		var converterSpyForRecord = jsonToDataConverterFactorySpy.jsonToDataConverterSpies.get(1);
		DataGroup recordReturnedDataPart = converterSpyForRecord.dataPartToReturn;
		assertSame(recordToValidateSentOnToSpider, recordReturnedDataPart);

		DataRecord validationResult = (DataRecord) spiderInstanceFactorySpy.spiderRecordValidatorSpy.MCR
				.getReturnValue("validateRecord", 0);
		assertDataFromSpiderConvertedToJsonUsingConvertersFromProvider(validationResult);

		assertResponseStatusIs(Response.Status.OK);
	}

	@Test
	public void testValidateRecordUnauthorized() {
		response = recordEndpoint.validateRecordUsingAuthTokenWithRecord(DUMMY_NON_AUTHORIZED_TOKEN,
				PLACE, jsonToValidate);
		assertResponseStatusIs(Response.Status.FORBIDDEN);
	}

	@Test
	public void testValidateRecordTypeNotFound() {
		spiderInstanceFactorySpy.throwRecordNotFoundException = true;
		response = recordEndpoint.validateRecord(AUTH_TOKEN, AUTH_TOKEN, "workOrder", defaultJson);
		assertResponseStatusIs(Response.Status.NOT_FOUND);
	}

	@Test
	public void testPreferredTokenForBatchIndex() throws IOException {
		expectTokenForBatchIndexToPrefereblyBeHeaderThanQuery(AUTH_TOKEN, "authToken2", AUTH_TOKEN);
		expectTokenForBatchIndexToPrefereblyBeHeaderThanQuery(null, AUTH_TOKEN, AUTH_TOKEN);
		expectTokenForBatchIndexToPrefereblyBeHeaderThanQuery(AUTH_TOKEN, null, AUTH_TOKEN);
		expectTokenForBatchIndexToPrefereblyBeHeaderThanQuery(null, null, null);
	}

	private void expectTokenForBatchIndexToPrefereblyBeHeaderThanQuery(String headerAuthToken,
			String queryAuthToken, String authTokenExpected) {
		String jsonFilter = "{\"name\":\"filter\",\"children\":[]}";
		response = recordEndpoint.indexRecordList(headerAuthToken, queryAuthToken, PLACE,
				jsonFilter);

		IndexBatchJobCreatorSpy indexBatchJobSpy = spiderInstanceFactorySpy.indexBatchJobCreator;

		assertEquals(indexBatchJobSpy.authToken, authTokenExpected);
	}

	@Test
	public void testBatchIndexWithFilter() {
		response = recordEndpoint.indexRecordList(AUTH_TOKEN, AUTH_TOKEN, PLACE, jsonFilterData);

		IndexBatchJobCreatorSpy indexBatchJobCreator = spiderInstanceFactorySpy.indexBatchJobCreator;

		DataGroup filterSentOnToSpider = spiderInstanceFactorySpy.indexBatchJobCreator.filter;
		assertJsonStringConvertedToDataUsesCoraData(jsonFilterData, filterSentOnToSpider);

		assertEquals(indexBatchJobCreator.type, PLACE);

		DataRecordSpy recordToReturn = spiderInstanceFactorySpy.indexBatchJobCreator.recordToReturn;
		assertDataFromSpiderConvertedToJsonUsingConvertersFromProvider(recordToReturn);

		assertEntityExists();
		assertResponseStatusIs(Response.Status.CREATED);
		assertTrue(response.getLocation().toString().startsWith("record/" + INDEX_BATCH_JOB));
	}

	@Test
	public void testIndexRecordListWithNullAsFilter() {
		response = recordEndpoint.indexRecordList(AUTH_TOKEN, AUTH_TOKEN, PLACE, null);
		assertEntityExists();
		assertResponseStatusIs(Response.Status.CREATED);
		assertTrue(response.getLocation().toString().startsWith("record/" + INDEX_BATCH_JOB));

		assertEquals(jsonParser.jsonString, "{\"name\":\"filter\",\"children\":[]}");

	}

	@Test
	public void testIndexRecordListWithEmptyFilter() {
		response = recordEndpoint.indexRecordList(AUTH_TOKEN, AUTH_TOKEN, PLACE, "");
		assertEntityExists();
		assertResponseStatusIs(Response.Status.CREATED);
		assertTrue(response.getLocation().toString().startsWith("record/" + INDEX_BATCH_JOB));

		assertEquals(jsonParser.jsonString, "{\"name\":\"filter\",\"children\":[]}");

	}

	@Test
	public void testIndexRecordListNotFound() {
		response = recordEndpoint.indexRecordList(AUTH_TOKEN, AUTH_TOKEN, "recordType_NON_EXISTING",
				jsonFilterData);
		assertResponseStatusIs(Response.Status.NOT_FOUND);
	}

	@Test
	public void testIndexRecordListUnauthorized() {
		response = recordEndpoint.indexRecordListUsingAuthTokenByType(DUMMY_NON_AUTHORIZED_TOKEN,
				PLACE, jsonFilterData);
		assertResponseStatusIs(Response.Status.FORBIDDEN);
	}

	@Test
	public void testIndexRecordListNoTokenAndUnauthorized() {
		response = recordEndpoint.indexRecordListUsingAuthTokenByType(null, PLACE, jsonFilterData);
		assertResponseStatusIs(Response.Status.UNAUTHORIZED);
	}

}