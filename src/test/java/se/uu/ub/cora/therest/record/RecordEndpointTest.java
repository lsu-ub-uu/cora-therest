/*
 * Copyright 2015, 2016, 2018, 2021, 2022 Uppsala University Library
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
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition.FormDataContentDispositionBuilder;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import se.uu.ub.cora.converter.ConverterProvider;
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

	private JsonToDataConverterFactorySpy jsonToDataConverterFactorySpy = new JsonToDataConverterFactorySpy();

	private RecordEndpoint recordEndpoint;
	private SpiderInstanceFactorySpy spiderInstanceFactorySpy;
	private Response response;
	private HttpServletRequestSpy requestSpy;
	private Map<String, String> initInfo = new HashMap<>();
	private LoggerFactorySpy loggerFactorySpy;
	private String testedClassName = "RecordEndpoint";

	private String jsonToValidate = "{\"order\":{\"name\":\"validationOrder\",\"children\":[{\"name\":\"recordInfo\",\"children\":[{\"name\":\"dataDivider\",\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"system\"},{\"name\":\"linkedRecordId\",\"value\":\"testSystem\"}]}]},{\"name\":\"recordType\",\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"recordType\"},{\"name\":\"linkedRecordId\",\"value\":\"someRecordType\"}]},{\"name\":\"metadataToValidate\",\"value\":\"existing\"},{\"name\":\"validateLinks\",\"value\":\"false\"}]},\"record\":{\"name\":\"text\",\"children\":[{\"name\":\"recordInfo\",\"children\":[{\"name\":\"id\",\"value\":\"workOrderRecordIdTextVar2Text\"},{\"name\":\"dataDivider\",\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"system\"},{\"name\":\"linkedRecordId\",\"value\":\"cora\"}]}]},{\"name\":\"textPart\",\"children\":[{\"name\":\"text\",\"value\":\"Id på länkad post\"}],\"attributes\":{\"type\":\"default\",\"lang\":\"sv\"}},{\"name\":\"textPart\",\"children\":[{\"name\":\"text\",\"value\":\"Linked record id\"}],\"attributes\":{\"type\":\"alternative\",\"lang\":\"en\"}}]}}";
	private String defaultJson = "{\"name\":\"someRecordType\",\"children\":[]}";
	private String defaultXml = "<someXml></someXml>";
	private String jsonFilterData = "{\"name\":\"filter\",\"children\":[{\"name\":\"part\",\"children\":[{\"name\":\"key\",\"value\":\"movieTitle\"},{\"name\":\"value\",\"value\":\"Some title\"}],\"repeatId\":\"0\"}]}";
	private String jsonIndexData = "{\\\"name\\\":\\\"indexSettings\\\",\\\"children\\\":[{\"name\":\"filter\",\"children\":[{\"name\":\"part\",\"children\":[{\"name\":\"key\",\"value\":\"movieTitle\"},{\"name\":\"value\",\"value\":\"Some title\"}],\"repeatId\":\"0\"}]}]}";
	private DataToJsonConverterFactoryCreatorSpy converterFactoryCreatorSpy;
	private ConverterFactorySpy converterFactorySpy;
	private String standardBaseUrlHttp = "http://cora.epc.ub.uu.se/systemone/rest/record/";
	private String standardBaseUrlHttps = "https://cora.epc.ub.uu.se/systemone/rest/record/";

	@BeforeMethod
	public void beforeMethod() {
		loggerFactorySpy = new LoggerFactorySpy();
		LoggerProvider.setLoggerFactory(loggerFactorySpy);
		converterFactoryCreatorSpy = new DataToJsonConverterFactoryCreatorSpy();
		DataToJsonConverterProvider
				.setDataToJsonConverterFactoryCreator(converterFactoryCreatorSpy);

		converterFactorySpy = new ConverterFactorySpy();
		ConverterProvider.setConverterFactory("xml", converterFactorySpy);

		jsonToDataConverterFactorySpy = new JsonToDataConverterFactorySpy();
		JsonToDataConverterProvider.setJsonToDataConverterFactory(jsonToDataConverterFactorySpy);
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

		response = recordEndpoint.readRecord("application/vnd.uub.record+json", AUTH_TOKEN,
				AUTH_TOKEN, PLACE, PLACE_0001);
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

		response = recordEndpoint.readRecord("application/vnd.uub.record+json", AUTH_TOKEN,
				AUTH_TOKEN, PLACE, PLACE_0001);
		DataToJsonConverterFactorySpy converterFactory = (DataToJsonConverterFactorySpy) converterFactoryCreatorSpy.MCR
				.getReturnValue("createFactory", 0);

		converterFactory.MCR.assertParameters("factorUsingBaseUrlAndConvertible", 0,
				standardBaseUrlHttps);
	}

	@Test
	public void testXForwardedProtoEmpty() {
		requestSpy.headers.put("X-Forwarded-Proto", "");
		recordEndpoint = new RecordEndpoint(requestSpy);

		response = recordEndpoint.readRecord("application/vnd.uub.record+json", AUTH_TOKEN,
				AUTH_TOKEN, PLACE, PLACE_0001);
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
		assertNotNull(response.getEntity(), "An entity should be returned");
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
	public void testAnnotationsForRead() throws Exception {
		Method method = getMethodWithMethodName("readRecord", 5);

		assertHttpMethodAndPathAnnotation(method, "GET", "{type}/{id}");
		assertProducesAnnotation(method, "application/vnd.uub.record+json",
				"application/vnd.uub.record+xml");
		assertAnnotationForReadRecordParameters(method);
	}

	private void assertHttpMethodAndPathAnnotation(Method method, String httpMethod,
			String expectedPath) {
		assertHttpMethodAnnotation(method, httpMethod);
		assertPathAnnotation(method, expectedPath);
	}

	private void assertHttpMethodAnnotation(Method method, String httpMethod) {
		Annotation[] annotations = method.getAnnotations();
		Class<? extends Annotation> httpMethodAnnotation = annotations[0].annotationType();
		String httpMethodAnnotationClassName = httpMethodAnnotation.toString();
		assertTrue(httpMethodAnnotationClassName.endsWith(httpMethod));
	}

	private void assertPathAnnotation(Method method, String expectedPath) {
		Path pathAnnotation = method.getAnnotation(Path.class);
		assertNotNull(pathAnnotation);
		assertEquals(pathAnnotation.value(), expectedPath);
	}

	private void assertAnnotationForReadRecordParameters(Method method) {
		Annotation[][] parameterAnnotations = method.getParameterAnnotations();

		assertAcceptAndAuthTokenAnnotation(parameterAnnotations);
		assertTypeAndIdAnnotation(parameterAnnotations, 3);
	}

	private void assertTypeAndIdAnnotation(Annotation[][] parameterAnnotations, int startPosition) {
		PathParam typeParameter = (PathParam) parameterAnnotations[startPosition][0];
		assertEquals(typeParameter.value(), "type");

		PathParam idParameter = (PathParam) parameterAnnotations[startPosition + 1][0];
		assertEquals(idParameter.value(), "id");
	}

	private void assertProducesAnnotation(Method method, String... accept) {
		Produces producesAnnotation = method.getAnnotation(Produces.class);
		assertNotNull(producesAnnotation);
		assertProducesValues(producesAnnotation, accept);
		assertEquals(producesAnnotation.value().length, accept.length);
	}

	private void assertConsumesAnnotation(Method method, String... accept) {
		Consumes consumesAnnotation = method.getAnnotation(Consumes.class);
		assertNotNull(consumesAnnotation);
		assertConsumesValues(consumesAnnotation, accept);
		assertEquals(consumesAnnotation.value().length, accept.length);
	}

	private void assertConsumesValues(Consumes consumesAnnotation, String... accept) {
		for (int i = 0; i < accept.length; i++) {
			assertEquals(consumesAnnotation.value()[i], accept[i]);
		}
	}

	private void assertProducesValues(Produces producesAnnotation, String... accept) {
		for (int i = 0; i < accept.length; i++) {
			assertEquals(producesAnnotation.value()[i], accept[i]);
		}
	}

	private Method getMethodWithMethodName(String methodName, int numOfParameters)
			throws NoSuchMethodException {
		Class<? extends RecordEndpoint> endpointClass = recordEndpoint.getClass();

		var parameters = generateParameters(numOfParameters);

		return endpointClass.getMethod(methodName, parameters);
	}

	private Class<?>[] generateParameters(int numOfParameters) {
		Class<?>[] parameters = new Class<?>[numOfParameters];

		for (int i = 0; i < numOfParameters; i++) {
			parameters[i] = String.class;
		}
		return parameters;
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

		response = recordEndpoint.readRecord("application/vnd.uub.record+json", headerAuthToken,
				queryAuthToken, PLACE, PLACE_0001);

		SpiderRecordReaderSpy spiderReaderSpy = spiderInstanceFactorySpy.spiderRecordReaderSpy;
		assertEquals(spiderReaderSpy.authToken, authTokenExpected);
	}

	@Test
	public void testReadRecordJson() {
		response = recordEndpoint.readRecord("application/vnd.uub.record+json", AUTH_TOKEN,
				AUTH_TOKEN, PLACE, PLACE_0001);
		assertEntityExists();
		assertResponseStatusIs(Response.Status.OK);
		assertResponseContentTypeIs("application/vnd.uub.record+json");
	}

	private void assertResponseContentTypeIs(String expectedContentType) {
		assertEquals(response.getHeaders().getFirst(HttpHeaders.CONTENT_TYPE), expectedContentType);
	}

	@Test
	public void testReadRecordXml() {
		response = recordEndpoint.readRecord("application/vnd.uub.record+xml", AUTH_TOKEN,
				AUTH_TOKEN, PLACE, PLACE_0001);
		assertEntityExists();
		assertResponseStatusIs(Response.Status.OK);
		assertResponseContentTypeIs("application/vnd.uub.record+xml");
	}

	@Test
	public void testReadRecordUsesToRestConverterFactoryForJson() {

		response = recordEndpoint.readRecord("application/vnd.uub.record+json", AUTH_TOKEN,
				AUTH_TOKEN, PLACE, PLACE_0001);

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
	public void testReadRecordUsesXmlConverterForAcceptXml() {

		response = recordEndpoint.readRecord("application/vnd.uub.record+xml", AUTH_TOKEN,
				AUTH_TOKEN, PLACE, PLACE_0001);

		DataRecord recordReturnedFromReader = spiderInstanceFactorySpy.spiderRecordReaderSpy.dataRecord;
		assertXmlConvertionOfResponse(recordReturnedFromReader);
	}

	private void assertXmlConvertionOfResponse(Convertible convertible) {

		ExternallyConvertibleToStringConverterSpy dataToXmlConverter = (ExternallyConvertibleToStringConverterSpy) converterFactorySpy.MCR
				.getReturnValue("factorExternallyConvertableToStringConverter", 0);

		dataToXmlConverter.MCR.assertParameters("convertWithLinks", 0, convertible,
				standardBaseUrlHttp);

		var entity = response.getEntity();
		dataToXmlConverter.MCR.assertReturn("convertWithLinks", 0, entity);
	}

	@Test
	public void testReadRecordUnauthenticated() {
		response = recordEndpoint.readRecord("application/vnd.uub.record+json",
				"dummyNonAuthenticatedToken", AUTH_TOKEN, PLACE, PLACE_0001);
		assertResponseStatusIs(Response.Status.UNAUTHORIZED);
	}

	@Test
	public void testReadRecordUnauthorized() {
		response = recordEndpoint.readRecord("application/vnd.uub.record+json",
				DUMMY_NON_AUTHORIZED_TOKEN, AUTH_TOKEN, PLACE, PLACE_0001);
		assertResponseStatusIs(Response.Status.FORBIDDEN);
	}

	@Test
	public void testReadRecordNotFound() {
		response = recordEndpoint.readRecord("application/vnd.uub.record+json", AUTH_TOKEN,
				AUTH_TOKEN, PLACE, "place:0001_NOT_FOUND");
		assertResponseStatusIs(Response.Status.NOT_FOUND);
	}

	@Test
	public void testReadRecordAbstractRecordType() {
		response = recordEndpoint.readRecord("application/vnd.uub.record+json", AUTH_TOKEN,
				AUTH_TOKEN, "binary", "image:123456789");
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

		response = recordEndpoint.readIncomingRecordLinks("application/vnd.uub.record+json",
				headerAuthToken, queryAuthToken, PLACE, PLACE_0001);

		SpiderRecordIncomingLinksReaderSpy spiderIncomingLinksReaderSpy = spiderInstanceFactorySpy.spiderRecordIncomingLinksReaderSpy;
		assertEquals(spiderIncomingLinksReaderSpy.authToken, authTokenExpected);
	}

	@Test
	public void testReadIncomingRecordLinksForJson() {
		response = recordEndpoint.readIncomingRecordLinks("application/vnd.uub.record+json",
				AUTH_TOKEN, AUTH_TOKEN, PLACE, PLACE_0001);
		DataList dataList = (DataList) spiderInstanceFactorySpy.spiderRecordIncomingLinksReaderSpy.MCR
				.getReturnValue("readIncomingLinks", 0);

		assertDataFromSpiderConvertedToJsonUsingConvertersFromProvider(dataList);
		assertResponseStatusIs(Response.Status.OK);
	}

	@Test
	public void testReadIncomingRecordLinksForXml() {
		response = recordEndpoint.readIncomingRecordLinks("application/vnd.uub.recordList+xml",
				AUTH_TOKEN, AUTH_TOKEN, PLACE, PLACE_0001);
		DataList dataList = (DataList) spiderInstanceFactorySpy.spiderRecordIncomingLinksReaderSpy.MCR
				.getReturnValue("readIncomingLinks", 0);

		assertXmlConvertionOfResponse(dataList);
		assertEntityExists();
		assertResponseStatusIs(Response.Status.OK);
		assertResponseContentTypeIs("application/vnd.uub.recordList+xml");
	}

	@Test
	public void testAnnotationsForReadIncomingRecordLinks() throws Exception {
		String methodName = "readIncomingRecordLinks";
		Method method = getMethodWithMethodName(methodName, 5);

		assertHttpMethodAndPathAnnotation(method, "GET", "{type}/{id}/incomingLinks");
		assertProducesAnnotation(method, "application/vnd.uub.recordList+json",
				"application/vnd.uub.recordList+xml");
		assertAnnotationForReadIncomingRecordLinksParameters(method);
	}

	private void assertAnnotationForReadIncomingRecordLinksParameters(Method method) {
		Annotation[][] parameterAnnotations = method.getParameterAnnotations();
		assertAcceptAndAuthTokenAnnotation(parameterAnnotations);
		assertTypeAndIdAnnotation(parameterAnnotations, 3);
	}

	private void assertAcceptAndAuthTokenAnnotation(Annotation[][] parameterAnnotations) {
		HeaderParam acceptParameter = (HeaderParam) parameterAnnotations[0][0];
		assertEquals(acceptParameter.value(), "Accept");
		assertAuthTokenAnnotation(parameterAnnotations, 1);
	}

	private void assertAuthTokenAnnotation(Annotation[][] parameterAnnotations, int startPosition) {
		HeaderParam headerAuthTokenParameter = (HeaderParam) parameterAnnotations[startPosition][0];
		assertEquals(headerAuthTokenParameter.value(), "authToken");
		QueryParam queryAuthTokenParameter = (QueryParam) parameterAnnotations[startPosition
				+ 1][0];
		assertEquals(queryAuthTokenParameter.value(), "authToken");
	}

	@Test
	public void testReadIncomingLinksUnauthorized() {
		response = recordEndpoint.readIncomingRecordLinks("application/vnd.uub.record+json",
				DUMMY_NON_AUTHORIZED_TOKEN, AUTH_TOKEN, PLACE, PLACE_0001);
		assertResponseStatusIs(Response.Status.FORBIDDEN);
	}

	@Test
	public void testReadIncomingLinksNotFound() {
		response = recordEndpoint.readIncomingRecordLinks("application/vnd.uub.record+json",
				AUTH_TOKEN, AUTH_TOKEN, PLACE, "place:0001_NOT_FOUND");
		assertResponseStatusIs(Response.Status.NOT_FOUND);
	}

	@Test
	public void testReadIncomingLinksAbstractRecordType() {
		String type = "abstract";
		response = recordEndpoint.readIncomingRecordLinks("application/vnd.uub.record+json",
				AUTH_TOKEN, AUTH_TOKEN, type, "canBeWhatEverIdTypeIsChecked");
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
	public void testAnnotationsForDeletRecord() throws Exception {
		String methodName = "deleteRecord";
		Method method = getMethodWithMethodName(methodName, 4);

		assertHttpMethodAndPathAnnotation(method, "DELETE", "{type}/{id}");

		Annotation[][] parameterAnnotations = method.getParameterAnnotations();

		assertAuthTokenAnnotation(parameterAnnotations, 0);
		assertTypeAndIdAnnotation(parameterAnnotations, 2);
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

		response = recordEndpoint.updateRecord("application/vnd.uub.record+json",
				"application/vnd.uub.record+json", headerAuthToken, queryAuthToken, PLACE,
				PLACE_0001, defaultJson);

		SpiderRecordUpdaterSpy spiderUpdaterSpy = spiderInstanceFactorySpy.spiderRecordUpdaterSpy;
		assertEquals(spiderUpdaterSpy.authToken, authTokenExpected);
	}

	@Test
	public void testUpdateRecordForJson() {
		response = recordEndpoint.updateRecord("application/vnd.uub.record+json",
				"application/vnd.uub.record+json", AUTH_TOKEN, AUTH_TOKEN, PLACE, PLACE_0001,
				defaultJson);
		assertResponseStatusIs(Response.Status.OK);
	}

	@Test
	public void testUpdateRecordUsesFactories() {
		response = recordEndpoint.updateRecord("application/vnd.uub.record+json",
				"application/vnd.uub.record+json", AUTH_TOKEN, AUTH_TOKEN, PLACE, PLACE_0001,
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
	public void testUpdateRecordForXml() {
		response = recordEndpoint.updateRecord("application/vnd.uub.record+xml",
				"application/vnd.uub.record+xml", AUTH_TOKEN, AUTH_TOKEN, PLACE, PLACE_0001,
				defaultXml);

		var dataElement = getValueFromConvertAndAssertParameters();

		spiderInstanceFactorySpy.spiderRecordUpdaterSpy.MCR.assertParameters("updateRecord", 0,
				AUTH_TOKEN, PLACE, PLACE_0001, dataElement);

		DataRecord updatedRecord = (DataRecord) spiderInstanceFactorySpy.spiderRecordUpdaterSpy.MCR
				.getReturnValue("updateRecord", 0);

		assertXmlConvertionOfResponse(updatedRecord);
		assertEntityExists();
		assertResponseStatusIs(Response.Status.OK);
		assertResponseContentTypeIs("application/vnd.uub.record+xml");
	}

	@Test
	public void testUpdateRecordBodyInXmlWithReplyInJson() {
		response = recordEndpoint.updateRecord("application/vnd.uub.record+xml",
				"application/vnd.uub.record+json", AUTH_TOKEN, AUTH_TOKEN, PLACE, PLACE_0001,
				defaultXml);

		var dataElement = getValueFromConvertAndAssertParameters();

		spiderInstanceFactorySpy.spiderRecordUpdaterSpy.MCR.assertParameters("updateRecord", 0,
				AUTH_TOKEN, PLACE, PLACE_0001, dataElement);

		DataRecord updatedRecord = (DataRecord) spiderInstanceFactorySpy.spiderRecordUpdaterSpy.MCR
				.getReturnValue("updateRecord", 0);

		assertDataFromSpiderConvertedToJsonUsingConvertersFromProvider(updatedRecord);
		assertEntityExists();
		assertResponseStatusIs(Response.Status.OK);
		assertResponseContentTypeIs("application/vnd.uub.record+json");
	}

	private Object getValueFromConvertAndAssertParameters() {
		StringToExternallyConvertibleConverterSpy xmlToDataConverter = (StringToExternallyConvertibleConverterSpy) converterFactorySpy.MCR
				.getReturnValue("factorStringToExternallyConvertableConverter", 0);
		xmlToDataConverter.MCR.assertParameters("convert", 0, defaultXml);
		var dataElement = xmlToDataConverter.MCR.getReturnValue("convert", 0);
		return dataElement;
	}

	@Test
	public void testAnnotationsForUpdateRecord() throws Exception {
		Method method = getMethodWithMethodName("updateRecord", 7);
		Annotation[][] parameterAnnotations = method.getParameterAnnotations();

		assertHttpMethodAndPathAnnotation(method, "POST", "{type}/{id}");
		assertConsumesAnnotation(method, "application/vnd.uub.record+json",
				"application/vnd.uub.record+xml");
		assertProducesAnnotation(method, "application/vnd.uub.record+json",
				"application/vnd.uub.record+xml");
		assertContentTypeAndAcceptAndAuthTokenAnnotation(parameterAnnotations);
		assertTypeAndIdAnnotation(parameterAnnotations, 4);
	}

	private void assertContentTypeAndAcceptAndAuthTokenAnnotation(
			Annotation[][] parameterAnnotations) {
		HeaderParam contentTypeParameter = (HeaderParam) parameterAnnotations[0][0];
		assertEquals(contentTypeParameter.value(), "Content-Type");
		HeaderParam acceptParameter = (HeaderParam) parameterAnnotations[1][0];
		assertEquals(acceptParameter.value(), "Accept");
		assertAuthTokenAnnotation(parameterAnnotations, 2);
	}

	@Test
	public void testUpdateRecordUnauthorized() {
		response = recordEndpoint.updateRecord("application/vnd.uub.record+json",
				"application/vnd.uub.record+json", DUMMY_NON_AUTHORIZED_TOKEN, AUTH_TOKEN, PLACE,
				PLACE_0001, defaultJson);
		assertResponseStatusIs(Response.Status.FORBIDDEN);
	}

	@Test
	public void testUpdateRecordNotFound() {
		response = recordEndpoint.updateRecord("application/vnd.uub.record+json",
				"application/vnd.uub.record+json", AUTH_TOKEN, AUTH_TOKEN, PLACE,
				PLACE_0001 + "_NOT_FOUND", defaultJson);
		assertResponseStatusIs(Response.Status.NOT_FOUND);
	}

	@Test
	public void testUpdateRecordTypeNotFound() {
		response = recordEndpoint.updateRecord("application/vnd.uub.record+json",
				"application/vnd.uub.record+json", AUTH_TOKEN, AUTH_TOKEN, PLACE + "_NOT_FOUND",
				PLACE_0001, defaultJson);
		assertResponseStatusIs(Response.Status.NOT_FOUND);
	}

	@Test
	public void testUpdateRecordBadContentInJson() {
		jsonParser.throwError = true;
		response = recordEndpoint.updateRecord("application/vnd.uub.record+json",
				"application/vnd.uub.record+json", AUTH_TOKEN, AUTH_TOKEN, PLACE, PLACE_0001,
				defaultJson);
		assertResponseStatusIs(Response.Status.BAD_REQUEST);
	}

	@Test
	public void testUpdateRecordWrongDataTypeInJson() {
		spiderInstanceFactorySpy.throwDataException = true;
		response = recordEndpoint.updateRecord("application/vnd.uub.record+json",
				"application/vnd.uub.record+json", AUTH_TOKEN, AUTH_TOKEN, PLACE, PLACE_0001,
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

		response = recordEndpoint.createRecord("application/vnd.uub.record+json",
				"application/vnd.uub.record+json", headerAuthToken, queryAuthToken, PLACE,
				defaultJson);

		SpiderCreatorSpy spiderCreatorSpy = spiderInstanceFactorySpy.spiderCreatorSpy;
		assertEquals(spiderCreatorSpy.authToken, authTokenExpected);
	}

	@Test
	public void testCreateRecord() {
		response = recordEndpoint.createRecord("application/vnd.uub.record+json",
				"application/vnd.uub.record+json", AUTH_TOKEN, AUTH_TOKEN, PLACE, defaultJson);

		assertResponseStatusIs(Response.Status.CREATED);
		// assertTrue(response.getLocation().toString().startsWith("record/" + PLACE));
		assertTrue(response.getLocation().toString().startsWith(PLACE));
	}

	@Test
	public void testCreateRecordUsesFactories() {

		response = recordEndpoint.createRecord("application/vnd.uub.record+json",
				"application/vnd.uub.record+json", AUTH_TOKEN, AUTH_TOKEN, PLACE, defaultJson);

		DataGroup recordSentOnToSpider = spiderInstanceFactorySpy.spiderCreatorSpy.record;
		assertJsonStringConvertedToDataUsesCoraData(defaultJson, recordSentOnToSpider);
		DataRecord createdRecord = (DataRecord) spiderInstanceFactorySpy.spiderCreatorSpy.MCR
				.getReturnValue("createAndStoreRecord", 0);

		assertDataFromSpiderConvertedToJsonUsingConvertersFromProvider(createdRecord);

		assertResponseStatusIs(Response.Status.CREATED);
		// assertTrue(response.getLocation().toString().startsWith("record/" + PLACE));
		assertTrue(response.getLocation().toString().startsWith(PLACE));
	}

	@Test
	public void testCreateRecordForXml() {
		response = recordEndpoint.createRecord("application/vnd.uub.record+xml",
				"application/vnd.uub.record+xml", AUTH_TOKEN, AUTH_TOKEN, PLACE, defaultXml);

		var dataElement = getValueFromConvertAndAssertParameters();
		spiderInstanceFactorySpy.spiderCreatorSpy.MCR.assertParameters("createAndStoreRecord", 0,
				AUTH_TOKEN, PLACE, dataElement);

		DataRecord createdRecord = (DataRecord) spiderInstanceFactorySpy.spiderCreatorSpy.MCR
				.getReturnValue("createAndStoreRecord", 0);

		assertXmlConvertionOfResponse(createdRecord);
		assertEntityExists();
		assertResponseStatusIs(Response.Status.CREATED);
		assertResponseContentTypeIs("application/vnd.uub.record+xml");
	}

	@Test
	public void testCreateRecordBodyInXmlWithReplyInJson() {
		response = recordEndpoint.createRecord("application/vnd.uub.record+xml",
				"application/vnd.uub.record+json", AUTH_TOKEN, AUTH_TOKEN, PLACE, defaultXml);

		var dataElement = getValueFromConvertAndAssertParameters();
		spiderInstanceFactorySpy.spiderCreatorSpy.MCR.assertParameters("createAndStoreRecord", 0,
				AUTH_TOKEN, PLACE, dataElement);

		DataRecord createdRecord = (DataRecord) spiderInstanceFactorySpy.spiderCreatorSpy.MCR
				.getReturnValue("createAndStoreRecord", 0);

		assertDataFromSpiderConvertedToJsonUsingConvertersFromProvider(createdRecord);
		assertEntityExists();
		assertResponseStatusIs(Response.Status.CREATED);
		assertResponseContentTypeIs("application/vnd.uub.record+json");
	}

	@Test
	public void testAnnotationsForCreateRecord() throws Exception {
		Method method = getMethodWithMethodName("createRecord", 6);
		Annotation[][] parameterAnnotations = method.getParameterAnnotations();

		assertHttpMethodAndPathAnnotation(method, "POST", "{type}");
		assertConsumesAnnotation(method, "application/vnd.uub.record+json",
				"application/vnd.uub.record+xml");
		assertProducesAnnotation(method, "application/vnd.uub.record+json",
				"application/vnd.uub.record+xml");
		assertContentTypeAndAcceptAndAuthTokenAnnotation(parameterAnnotations);
		PathParam typeParameter = (PathParam) parameterAnnotations[4][0];
		assertEquals(typeParameter.value(), "type");
	}

	@Test
	public void testCreateRecordBadCreatedLocation() {
		String type = "place&& &&\\\\";
		response = recordEndpoint.createRecord("application/vnd.uub.record+json",
				"application/vnd.uub.record+json", AUTH_TOKEN, AUTH_TOKEN, type, defaultJson);
		assertResponseStatusIs(Response.Status.BAD_REQUEST);
	}

	@Test
	public void testCreateRecordUnauthorized() {
		response = recordEndpoint.createRecord("application/vnd.uub.record+json",
				"application/vnd.uub.record+json", DUMMY_NON_AUTHORIZED_TOKEN, AUTH_TOKEN, PLACE,
				defaultJson);
		assertResponseStatusIs(Response.Status.FORBIDDEN);
	}

	@Test
	public void testCreateNonExistingRecordType() {
		String type = "recordType_NON_EXISTING";
		response = recordEndpoint.createRecord("application/vnd.uub.record+json",
				"application/vnd.uub.record+json", AUTH_TOKEN, AUTH_TOKEN, type, defaultJson);
		assertResponseStatusIs(Response.Status.NOT_FOUND);
	}

	@Test
	public void testCreateRecordNotValid() {
		response = recordEndpoint.createRecord("application/vnd.uub.record+json",
				"application/vnd.uub.record+json", AUTH_TOKEN, AUTH_TOKEN, "place_NON_VALID",
				defaultJson);
		assertResponseStatusIs(Response.Status.BAD_REQUEST);
	}

	@Test
	public void testCreateRecordConversionException() {
		jsonToDataConverterFactorySpy.throwError = true;
		response = recordEndpoint.createRecord("application/vnd.uub.record+json",
				"application/vnd.uub.record+json", "someToken78678567", AUTH_TOKEN, PLACE,
				defaultJson);
		assertResponseStatusIs(Response.Status.BAD_REQUEST);
	}

	@Test
	public void testCreateRecordAbstractRecordType() {
		String type = "abstract";
		response = recordEndpoint.createRecord("application/vnd.uub.record+json",
				"application/vnd.uub.record+json", AUTH_TOKEN, AUTH_TOKEN, type, defaultJson);
		assertResponseStatusIs(Response.Status.METHOD_NOT_ALLOWED);
	}

	@Test
	public void testCreateRecordDuplicateUserSuppliedId() {
		response = recordEndpoint.createRecord("application/vnd.uub.record+json",
				"application/vnd.uub.record+json", AUTH_TOKEN, AUTH_TOKEN, "place_duplicate",
				defaultJson);
		assertResponseStatusIs(Response.Status.CONFLICT);

	}

	@Test
	public void testCreateRecordUnexpectedError() {
		assertEquals(loggerFactorySpy.getNoOfErrorExceptionsUsingClassName(testedClassName), 0);
		response = recordEndpoint.createRecord("application/vnd.uub.record+json",
				"application/vnd.uub.record+json", AUTH_TOKEN, AUTH_TOKEN, "place_unexpected_error",
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
		InputStream stream = createUplodedInputStream();
		FormDataContentDisposition formDataContentDisposition = createformDataContent();

		response = recordEndpoint.uploadFile("application/vnd.uub.recordList+json", headerAuthToken,
				queryAuthToken, "image", "image:123456789", stream, formDataContentDisposition);

		SpiderUploaderSpy spiderUploaderSpy = spiderInstanceFactorySpy.spiderUploaderSpy;
		assertEquals(spiderUploaderSpy.authToken, authTokenExpected);
	}

	private ByteArrayInputStream createUplodedInputStream() {
		return new ByteArrayInputStream("a string".getBytes(StandardCharsets.UTF_8));
	}

	private FormDataContentDisposition createformDataContent() {
		FormDataContentDispositionBuilder builder = FormDataContentDisposition
				.name("multipart;form-data");
		builder.fileName("adele1.png");
		FormDataContentDisposition formDataContentDisposition = builder.build();
		return formDataContentDisposition;
	}

	@Test
	public void testUploadFileForJson() throws ParseException {
		InputStream stream = createUplodedInputStream();
		FormDataContentDisposition formDataContentDisposition = createformDataContent();

		response = recordEndpoint.uploadFile("application/vnd.uub.recordList+json", AUTH_TOKEN,
				AUTH_TOKEN, "image", "image:123456789", stream, formDataContentDisposition);

		assertResponseStatusIs(Response.Status.OK);
	}

	@Test
	public void testUploadFileForXml() {
		InputStream stream = createUplodedInputStream();
		FormDataContentDisposition formDataContentDisposition = createformDataContent();

		response = recordEndpoint.uploadFile("application/vnd.uub.record+xml", AUTH_TOKEN,
				AUTH_TOKEN, "image", "image:123456789", stream, formDataContentDisposition);
		DataRecord uploadedFile = (DataRecord) spiderInstanceFactorySpy.spiderUploaderSpy.MCR
				.getReturnValue("upload", 0);

		assertXmlConvertionOfResponse(uploadedFile);
		assertEntityExists();
		assertResponseStatusIs(Response.Status.OK);
		assertResponseContentTypeIs("application/vnd.uub.record+xml");
	}

	@Test
	public void testAnnotationsForUploadFile() throws Exception {
		Class<?>[] parameters = { String.class, String.class, String.class, String.class,
				String.class, InputStream.class, FormDataContentDisposition.class };
		Method method = getMethodWithMethodName("uploadFile", parameters);
		Annotation[][] parameterAnnotations = method.getParameterAnnotations();

		assertHttpMethodAndPathAnnotation(method, "POST", "{type}/{id}/{streamId}");
		assertProducesAnnotation(method, "application/vnd.uub.record+json",
				"application/vnd.uub.record+xml");
		assertAcceptAndAuthTokenAnnotation(parameterAnnotations);
		assertTypeAndIdAnnotation(parameterAnnotations, 3);

		// TODO Ska vi verkligen ha två annotation parameters med samma namn File???
		FormDataParam uploadedInputStreamParameter = (FormDataParam) parameterAnnotations[5][0];
		assertEquals(uploadedInputStreamParameter.value(), "file");
		FormDataParam fileDetailParameter = (FormDataParam) parameterAnnotations[6][0];
		assertEquals(fileDetailParameter.value(), "file");
	}

	private Method getMethodWithMethodName(String methodName, Class<?>[] parameters)
			throws NoSuchMethodException {
		Class<? extends RecordEndpoint> endpointClass = recordEndpoint.getClass();
		Method method = endpointClass.getMethod(methodName, parameters);
		return method;
	}

	@Test
	public void testUploadUnauthorized() {
		InputStream stream = createUplodedInputStream();

		response = recordEndpoint.uploadFileUsingAuthTokenWithStream(
				"application/vnd.uub.record+json", DUMMY_NON_AUTHORIZED_TOKEN, "image",
				"image:123456789", stream, "someFile.tif");

		assertResponseStatusIs(Response.Status.FORBIDDEN);
	}

	@Test
	public void testUploadNotFound() {
		InputStream stream = createUplodedInputStream();

		FormDataContentDisposition formDataContentDisposition = createformDataContent();

		response = recordEndpoint.uploadFile("application/vnd.uub.recordList+json", AUTH_TOKEN,
				AUTH_TOKEN, "image", "image:123456789_NOT_FOUND", stream,
				formDataContentDisposition);

		assertResponseStatusIs(Response.Status.NOT_FOUND);
	}

	@Test
	public void testUploadNotAChildOfBinary() {
		InputStream stream = createUplodedInputStream();

		FormDataContentDisposition formDataContentDisposition = createformDataContent();

		response = recordEndpoint.uploadFile("application/vnd.uub.recordList+json", AUTH_TOKEN,
				AUTH_TOKEN, "not_child_of_binary_type", "image:123456789", stream,
				formDataContentDisposition);

		assertResponseStatusIs(Response.Status.METHOD_NOT_ALLOWED);
	}

	@Test
	public void testUploadStreamMissing() {
		FormDataContentDispositionBuilder builder = FormDataContentDisposition
				.name("multipart;form-data");
		builder.fileName("adele1.png");
		FormDataContentDisposition formDataContentDisposition = builder.build();

		response = recordEndpoint.uploadFile("application/vnd.uub.recordList+json", AUTH_TOKEN,
				AUTH_TOKEN, "image", "image:123456789", null, formDataContentDisposition);

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
	public void testAnnotationsForDownloadFile() throws Exception {
		Method method = getMethodWithMethodName("downloadFile", 5);
		Annotation[][] parameterAnnotations = method.getParameterAnnotations();

		assertHttpMethodAndPathAnnotation(method, "GET", "{type}/{id}/{streamId}");

		assertAuthTokenAnnotation(parameterAnnotations, 0);
		assertTypeAndIdAnnotation(parameterAnnotations, 2);
		assertStreamIdAnnotation(parameterAnnotations);

	}

	private void assertStreamIdAnnotation(Annotation[][] parameterAnnotations) {
		PathParam streamIdParameter = (PathParam) parameterAnnotations[4][0];
		assertEquals(streamIdParameter.value(), "streamId");
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
		response = recordEndpoint.searchRecord("application/vnd.uub.recordList+json", AUTH_TOKEN,
				AUTH_TOKEN, "aSearchId", defaultJson);

		SpiderRecordSearcherSpy spiderRecordSearcherSpy = spiderInstanceFactorySpy.spiderRecordSearcherSpy;
		assertEquals(spiderRecordSearcherSpy.authToken, AUTH_TOKEN);
		assertEquals(spiderRecordSearcherSpy.searchId, "aSearchId");

		DataGroup searchSentOnToSpider = spiderInstanceFactorySpy.spiderRecordSearcherSpy.searchData;
		assertJsonStringConvertedToDataUsesCoraData(defaultJson, searchSentOnToSpider);
	}

	@Test
	public void testSearchRecordUsesFactoriesCorrectly() {
		response = recordEndpoint.searchRecord("application/vnd.uub.recordList+json", AUTH_TOKEN,
				AUTH_TOKEN, "aSearchId", defaultJson);

		SpiderRecordSearcherSpy spiderRecordSearcherSpy = spiderInstanceFactorySpy.spiderRecordSearcherSpy;

		DataGroup searchSentOnToSpider = spiderInstanceFactorySpy.spiderRecordSearcherSpy.searchData;
		assertJsonStringConvertedToDataUsesCoraData(defaultJson, searchSentOnToSpider);

		DataList returnedDataListFromReader = spiderRecordSearcherSpy.searchResult;

		assertDataFromSpiderConvertedToJsonUsingConvertersFromProvider(returnedDataListFromReader);
	}

	@Test
	public void testSearchRecordRightTokenInputsReachSpider() {
		response = recordEndpoint.searchRecord("application/vnd.uub.recordList+json", AUTH_TOKEN,
				null, "aSearchId", defaultJson);
		SpiderRecordSearcherSpy spiderRecordSearcherSpy = spiderInstanceFactorySpy.spiderRecordSearcherSpy;
		assertEquals(spiderRecordSearcherSpy.authToken, AUTH_TOKEN);
	}

	@Test
	public void testSearchRecordRightTokenInputsReachSpider2() {
		response = recordEndpoint.searchRecord("application/vnd.uub.recordList+json", null,
				AUTH_TOKEN, "aSearchId", defaultJson);
		SpiderRecordSearcherSpy spiderRecordSearcherSpy = spiderInstanceFactorySpy.spiderRecordSearcherSpy;
		assertEquals(spiderRecordSearcherSpy.authToken, AUTH_TOKEN);
	}

	@Test
	public void testSearchRecordRightTokenInputsReachSpider3() {
		response = recordEndpoint.searchRecord("application/vnd.uub.recordList+json", AUTH_TOKEN,
				"otherAuthToken", "aSearchId", defaultJson);
		SpiderRecordSearcherSpy spiderRecordSearcherSpy = spiderInstanceFactorySpy.spiderRecordSearcherSpy;
		assertEquals(spiderRecordSearcherSpy.authToken, AUTH_TOKEN);
	}

	@Test
	public void testSearchRecord() {
		response = recordEndpoint.searchRecord("application/vnd.uub.recordList+json", AUTH_TOKEN,
				AUTH_TOKEN, "aSearchId", defaultJson);
		assertEntityExists();
		assertResponseStatusIs(Response.Status.OK);
	}

	@Test
	public void testSearchRecordForXml() {
		response = recordEndpoint.searchRecord("application/vnd.uub.recordList+xml", AUTH_TOKEN,
				AUTH_TOKEN, "aSearchId", defaultXml);

		var dataElement = getValueFromConvertAndAssertParameters();

		spiderInstanceFactorySpy.spiderRecordSearcherSpy.MCR.assertParameters("search", 0,
				AUTH_TOKEN, "aSearchId", dataElement);

		DataList searchRecord = (DataList) spiderInstanceFactorySpy.spiderRecordSearcherSpy.MCR
				.getReturnValue("search", 0);

		assertXmlConvertionOfResponse(searchRecord);
		assertEntityExists();
		assertResponseStatusIs(Response.Status.OK);
		assertResponseContentTypeIs("application/vnd.uub.recordList+xml");
	}

	@Test
	public void testSearchRecordSearchDataInXmlForJson() {
		response = recordEndpoint.searchRecord("application/vnd.uub.recordList+json", AUTH_TOKEN,
				AUTH_TOKEN, "aSearchId", defaultXml);

		var dataElement = getValueFromConvertAndAssertParameters();

		spiderInstanceFactorySpy.spiderRecordSearcherSpy.MCR.assertParameters("search", 0,
				AUTH_TOKEN, "aSearchId", dataElement);

		DataList searchRecord = (DataList) spiderInstanceFactorySpy.spiderRecordSearcherSpy.MCR
				.getReturnValue("search", 0);

		assertDataFromSpiderConvertedToJsonUsingConvertersFromProvider(searchRecord);
		assertEntityExists();
		assertResponseStatusIs(Response.Status.OK);
		assertResponseContentTypeIs("application/vnd.uub.recordList+json");
	}

	@Test
	public void testAnnotationsForSearchRecord() throws Exception {
		Method method = getMethodWithMethodName("searchRecord", 5);
		Annotation[][] parameterAnnotations = method.getParameterAnnotations();

		assertHttpMethodAndPathAnnotation(method, "GET", "searchResult/{searchId}");
		assertProducesAnnotation(method, "application/vnd.uub.recordList+json",
				"application/vnd.uub.recordList+xml");
		assertAcceptAndAuthTokenAnnotation(parameterAnnotations);

		PathParam searchIdParameter = (PathParam) parameterAnnotations[3][0];
		assertEquals(searchIdParameter.value(), "searchId");

		QueryParam searchDataAsQueryParameter = (QueryParam) parameterAnnotations[4][0];
		assertEquals(searchDataAsQueryParameter.value(), "searchData");
	}

	@Test
	public void testSearchRecordSearchIdNotFound() {
		response = recordEndpoint.searchRecord("application/vnd.uub.recordList+json", AUTH_TOKEN,
				AUTH_TOKEN, "aSearchId_NOT_FOUND", defaultJson);
		assertResponseStatusIs(Response.Status.NOT_FOUND);
	}

	@Test
	public void testSearchRecordInvalidSearchData() {
		response = recordEndpoint.searchRecord("application/vnd.uub.recordList+json", AUTH_TOKEN,
				AUTH_TOKEN, "aSearchId_INVALID_DATA", defaultJson);
		assertResponseStatusIs(Response.Status.BAD_REQUEST);
	}

	@Test
	public void testSearchRecordUnauthorized() {
		response = recordEndpoint.searchRecord("application/vnd.uub.recordList+json",
				DUMMY_NON_AUTHORIZED_TOKEN, DUMMY_NON_AUTHORIZED_TOKEN, "aSearchId", defaultJson);
		assertResponseStatusIs(Response.Status.FORBIDDEN);
	}

	@Test
	public void testSearchRecordUnauthenticated() {
		response = recordEndpoint.searchRecord("application/vnd.uub.recordList+json",
				"nonExistingToken", "nonExistingToken", "aSearchId", defaultJson);
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

		response = recordEndpoint.validateRecord("application/vnd.uub.workorder+json",
				"application/vnd.uub.record+json", headerAuthToken, queryAuthToken, PLACE,
				jsonToValidate);

		SpiderRecordValidatorSpy spiderRecordValidatorSpy = spiderInstanceFactorySpy.spiderRecordValidatorSpy;

		assertEquals(spiderRecordValidatorSpy.authToken, authTokenExpected);
	}

	@Test
	public void testValidateRecord() {
		response = recordEndpoint.validateRecord("application/vnd.uub.workorder+json",
				"application/vnd.uub.record+json", AUTH_TOKEN, AUTH_TOKEN, "workOrder",
				defaultJson);

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
	public void testValidateRecordForXml() {
		response = recordEndpoint.validateRecord("application/vnd.uub.workorder+xml",
				"application/vnd.uub.record+xml", AUTH_TOKEN, AUTH_TOKEN, "workOrder", defaultXml);

		var dataElement = getValueFromConvertAndAssertParameters();

		spiderInstanceFactorySpy.spiderRecordValidatorSpy.MCR.assertParameters("validateRecord", 0,
				AUTH_TOKEN, "workOrder", PLACE_0001, dataElement);

		DataRecord vaildatedRecord = (DataRecord) spiderInstanceFactorySpy.spiderRecordValidatorSpy.MCR
				.getReturnValue("validateRecord", 0);

		assertXmlConvertionOfResponse(vaildatedRecord);
		assertEntityExists();
		assertResponseStatusIs(Response.Status.OK);
		assertResponseContentTypeIs("application/vnd.uub.record+xml");
	}

	// @Test
	// public void testUpdateRecordBodyInXmlWithReplyInJson() {
	// response = recordEndpoint.updateRecord("application/vnd.uub.record+xml",
	// "application/vnd.uub.record+json", AUTH_TOKEN, AUTH_TOKEN, PLACE, PLACE_0001,
	// defaultXml);
	//
	// var dataElement = getValueFromConvertAndAssertParameters();
	//
	// spiderInstanceFactorySpy.spiderRecordUpdaterSpy.MCR.assertParameters("updateRecord", 0,
	// AUTH_TOKEN, PLACE, PLACE_0001, dataElement);
	//
	// DataRecord updatedRecord = (DataRecord) spiderInstanceFactorySpy.spiderRecordUpdaterSpy.MCR
	// .getReturnValue("updateRecord", 0);
	//
	// assertDataFromSpiderConvertedToJsonUsingConvertersFromProvider(updatedRecord);
	// assertEntityExists();
	// assertResponseStatusIs(Response.Status.OK);
	// assertResponseContentTypeIs("application/vnd.uub.record+json");
	// }

	@Test
	public void testAnnotationsForValidateRecord() throws Exception {
		Method method = getMethodWithMethodName("validateRecord", 6);
		Annotation[][] parameterAnnotations = method.getParameterAnnotations();

		assertHttpMethodAndPathAnnotation(method, "POST", "{type}");
		assertConsumesAnnotation(method, "application/vnd.uub.workorder+json",
				"application/vnd.uub.workorder+xml");
		assertProducesAnnotation(method, "application/vnd.uub.record+json",
				"application/vnd.uub.record+xml");
		assertContentTypeAndAcceptAndAuthTokenAnnotation(parameterAnnotations);

		PathParam typeParameter = (PathParam) parameterAnnotations[4][0];
		assertEquals(typeParameter.value(), "type");
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
		response = recordEndpoint.validateRecord("application/vnd.uub.workorder+json",
				"application/vnd.uub.record+json", AUTH_TOKEN, AUTH_TOKEN, "workOrder",
				defaultJson);
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
		response = recordEndpoint.indexRecordList(AUTH_TOKEN, AUTH_TOKEN, PLACE, jsonIndexData);

		IndexBatchJobCreatorSpy indexBatchJobCreator = spiderInstanceFactorySpy.indexBatchJobCreator;

		DataGroup filterSentOnToSpider = spiderInstanceFactorySpy.indexBatchJobCreator.filter;
		assertJsonStringConvertedToDataUsesCoraData(jsonIndexData, filterSentOnToSpider);

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

		assertEquals(jsonParser.jsonString, "{\"name\":\"indexSettings\",\"children\":[]}");

	}

	@Test
	public void testIndexRecordListWithEmptyFilter() {
		response = recordEndpoint.indexRecordList(AUTH_TOKEN, AUTH_TOKEN, PLACE, "");
		assertEntityExists();
		assertResponseStatusIs(Response.Status.CREATED);
		assertTrue(response.getLocation().toString().startsWith("record/" + INDEX_BATCH_JOB));

		assertEquals(jsonParser.jsonString, "{\"name\":\"indexSettings\",\"children\":[]}");

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