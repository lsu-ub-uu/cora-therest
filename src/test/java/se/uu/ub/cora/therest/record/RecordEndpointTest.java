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
import se.uu.ub.cora.therest.coradata.DataGroupSpy;
import se.uu.ub.cora.therest.coradata.DataListSpy;
import se.uu.ub.cora.therest.coradata.DataRecordSpy;
import se.uu.ub.cora.therest.log.LoggerFactorySpy;

public class RecordEndpointTest {
	private static final String TEXT_PLAIN = "text/plain; charset=UTF-8";
	private static final String APPLICATION_VND_UUB_RECORD_LIST_XML = "application/vnd.uub.recordList+xml";
	private static final String APPLICATION_VND_UUB_RECORD_LIST_XML_QS09 = "application/vnd.uub.recordList+xml;qs=0.9";
	private static final String APPLICATION_VND_UUB_RECORD_LIST_JSON = "application/vnd.uub.recordList+json";
	private static final String APPLICATION_VND_UUB_RECORD_XML = "application/vnd.uub.record+xml";
	private static final String APPLICATION_VND_UUB_RECORD_XML_QS09 = "application/vnd.uub.record+xml;qs=0.9";
	private static final String APPLICATION_VND_UUB_RECORD_JSON = "application/vnd.uub.record+json";
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

		response = recordEndpoint.readRecordJson(AUTH_TOKEN, AUTH_TOKEN, PLACE, PLACE_0001);
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

		response = recordEndpoint.readRecordJson(AUTH_TOKEN, AUTH_TOKEN, PLACE, PLACE_0001);
		DataToJsonConverterFactorySpy converterFactory = (DataToJsonConverterFactorySpy) converterFactoryCreatorSpy.MCR
				.getReturnValue("createFactory", 0);

		converterFactory.MCR.assertParameters("factorUsingBaseUrlAndConvertible", 0,
				standardBaseUrlHttps);
	}

	@Test
	public void testXForwardedProtoEmpty() {
		requestSpy.headers.put("X-Forwarded-Proto", "");
		recordEndpoint = new RecordEndpoint(requestSpy);

		response = recordEndpoint.readRecordJson(AUTH_TOKEN, AUTH_TOKEN, PLACE, PLACE_0001);
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
		response = recordEndpoint.readRecordListJson(headerAuthToken, queryAuthToken, PLACE,
				jsonFilter);

		SpiderRecordListReaderSpy spiderListReaderSpy = spiderInstanceFactorySpy.spiderRecordListReaderSpy;
		assertEquals(spiderListReaderSpy.authToken, authTokenExpected);
	}

	@Test
	public void testReadRecordListWithFilter() {
		response = recordEndpoint.readRecordListJson(AUTH_TOKEN, AUTH_TOKEN, PLACE, jsonFilterData);

		SpiderRecordListReaderSpy spiderListReaderSpy = spiderInstanceFactorySpy.spiderRecordListReaderSpy;

		DataGroup filterSentOnToSpider = spiderInstanceFactorySpy.spiderRecordListReaderSpy.filter;
		assertJsonStringConvertedToDataUsesCoraData(jsonFilterData, filterSentOnToSpider);

		DataListSpy returnedDataListFromReader = spiderListReaderSpy.returnedDataList;

		assertDataFromSpiderConvertedToJsonUsingConvertersFromProvider(returnedDataListFromReader);

		assertResponseStatusIs(Response.Status.OK);
	}

	@Test
	public void testReadRecordListWithNullAsFilter() {
		response = recordEndpoint.readRecordListJson(AUTH_TOKEN, AUTH_TOKEN, PLACE, null);
		assertEntityExists();
		assertResponseStatusIs(Response.Status.OK);

		assertEquals(jsonParser.jsonString, "{\"name\":\"filter\",\"children\":[]}");
	}

	@Test
	public void testReadRecordListWithEmptyFilter() {
		response = recordEndpoint.readRecordListJson(AUTH_TOKEN, AUTH_TOKEN, PLACE, "");
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
	public void testReadRecordListForXml() {
		response = recordEndpoint.readRecordListXml(AUTH_TOKEN, AUTH_TOKEN, "place", defaultXml);

		var filterElement = getValueFromConvertAndAssertParameters();

		spiderInstanceFactorySpy.spiderRecordListReaderSpy.MCR.assertParameters("readRecordList", 0,
				AUTH_TOKEN, "place", filterElement);

		DataList dataList = (DataList) spiderInstanceFactorySpy.spiderRecordListReaderSpy.MCR
				.getReturnValue("readRecordList", 0);

		assertXmlConvertionOfResponse(dataList);
		assertEntityExists();
		assertResponseStatusIs(Response.Status.OK);
		assertResponseContentTypeIs(APPLICATION_VND_UUB_RECORD_LIST_XML);
	}

	@Test
	public void testReadRecordListWithNullFilterForXml() {
		response = recordEndpoint.readRecordListXml(AUTH_TOKEN, AUTH_TOKEN, "place", null);

		var filterSentOnToSpider = spiderInstanceFactorySpy.spiderRecordListReaderSpy.filter;

		String defaultFilter = "{\"name\":\"filter\",\"children\":[]}";
		assertJsonStringConvertedToDataUsesCoraData(defaultFilter, filterSentOnToSpider);

		spiderInstanceFactorySpy.spiderRecordListReaderSpy.MCR.assertParameters("readRecordList", 0,
				AUTH_TOKEN, "place", filterSentOnToSpider);

		DataList dataList = (DataList) spiderInstanceFactorySpy.spiderRecordListReaderSpy.MCR
				.getReturnValue("readRecordList", 0);

		assertXmlConvertionOfResponse(dataList);
		assertEntityExists();
		assertResponseStatusIs(Response.Status.OK);
		assertResponseContentTypeIs(APPLICATION_VND_UUB_RECORD_LIST_XML);
	}

	@Test
	public void testAnnotationsForReadRecordListJson() throws Exception {
		Method method = getMethodWithMethodName("readRecordListJson", 4);
		Annotation[][] parameterAnnotations = method.getParameterAnnotations();

		assertHttpMethodAndPathAnnotation(method, "GET", "{type}/");
		assertProducesAnnotation(method, APPLICATION_VND_UUB_RECORD_LIST_JSON);
		assertAuthTokenAnnotation(parameterAnnotations, 0);

		PathParam typeParameter = (PathParam) parameterAnnotations[2][0];
		assertEquals(typeParameter.value(), "type");

		QueryParam filterParameter = (QueryParam) parameterAnnotations[3][0];
		assertEquals(filterParameter.value(), "filter");
	}

	@Test
	public void testAnnotationsForReadRecordListXml() throws Exception {
		Method method = getMethodWithMethodName("readRecordListXml", 4);
		Annotation[][] parameterAnnotations = method.getParameterAnnotations();

		assertHttpMethodAndPathAnnotation(method, "GET", "{type}/");
		assertProducesAnnotation(method, APPLICATION_VND_UUB_RECORD_LIST_XML_QS09);
		assertAuthTokenAnnotation(parameterAnnotations, 0);

		PathParam typeParameter = (PathParam) parameterAnnotations[2][0];
		assertEquals(typeParameter.value(), "type");

		QueryParam filterParameter = (QueryParam) parameterAnnotations[3][0];
		assertEquals(filterParameter.value(), "filter");
	}

	@Test
	public void testReadRecordListNotFound() {
		String jsonFilter = "{\"name\":\"filter\",\"children\":[]}";
		response = recordEndpoint.readRecordListJson(AUTH_TOKEN, AUTH_TOKEN, "place_NOT_FOUND",
				jsonFilter);
		assertResponseStatusIs(Response.Status.NOT_FOUND);
	}

	@Test
	public void testReadRecordListUnauthorized() {
		String jsonFilter = "{\"name\":\"filter\",\"children\":[]}";
		response = recordEndpoint.readRecordListJson(DUMMY_NON_AUTHORIZED_TOKEN, AUTH_TOKEN, PLACE,
				jsonFilter);
		assertResponseStatusIs(Response.Status.FORBIDDEN);
	}

	@Test
	public void testReadRecordListNoTokenAndUnauthorized() {
		String jsonFilter = "{\"name\":\"filter\",\"children\":[]}";
		response = recordEndpoint.readRecordListJson(null, null, PLACE, jsonFilter);
		assertResponseStatusIs(Response.Status.UNAUTHORIZED);
	}

	@Test
	public void testAnnotationsForReadJson() throws Exception {
		Method method = getMethodWithMethodName("readRecordJson", 4);

		assertHttpMethodAndPathAnnotation(method, "GET", "{type}/{id}");
		assertProducesAnnotation(method, APPLICATION_VND_UUB_RECORD_JSON);
		assertAnnotationForReadRecordParameters(method);
	}

	@Test
	public void testAnnotationsForReadXml() throws Exception {
		Method method = getMethodWithMethodName("readRecordXml", 4);

		assertHttpMethodAndPathAnnotation(method, "GET", "{type}/{id}");
		assertProducesAnnotation(method, APPLICATION_VND_UUB_RECORD_XML_QS09);
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
		assertAuthTokenAnnotation(parameterAnnotations, 0);
		assertTypeAndIdAnnotation(parameterAnnotations, 2);
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

		response = recordEndpoint.readRecordJson(headerAuthToken, queryAuthToken, PLACE,
				PLACE_0001);

		SpiderRecordReaderSpy spiderReaderSpy = spiderInstanceFactorySpy.spiderRecordReaderSpy;
		assertEquals(spiderReaderSpy.authToken, authTokenExpected);
	}

	@Test
	public void testReadRecordJson() {
		response = recordEndpoint.readRecordJson(AUTH_TOKEN, AUTH_TOKEN, PLACE, PLACE_0001);
		assertEntityExists();
		assertResponseStatusIs(Response.Status.OK);
		assertResponseContentTypeIs(APPLICATION_VND_UUB_RECORD_JSON);
	}

	private void assertResponseContentTypeIs(String expectedContentType) {
		assertEquals(response.getHeaders().getFirst(HttpHeaders.CONTENT_TYPE), expectedContentType);
	}

	@Test
	public void testReadRecordXml() {
		response = recordEndpoint.readRecordXml(AUTH_TOKEN, AUTH_TOKEN, PLACE, PLACE_0001);
		assertEntityExists();
		assertResponseStatusIs(Response.Status.OK);
		assertResponseContentTypeIs(APPLICATION_VND_UUB_RECORD_XML);
	}

	@Test
	public void testReadRecordUsesToRestConverterFactoryForJson() {

		response = recordEndpoint.readRecordJson(AUTH_TOKEN, AUTH_TOKEN, PLACE, PLACE_0001);

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

		response = recordEndpoint.readRecordXml(AUTH_TOKEN, AUTH_TOKEN, PLACE, PLACE_0001);

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
		response = recordEndpoint.readRecordJson("dummyNonAuthenticatedToken", AUTH_TOKEN, PLACE,
				PLACE_0001);
		assertResponseStatusIs(Response.Status.UNAUTHORIZED);
	}

	@Test
	public void testReadRecordUnauthorized() {
		response = recordEndpoint.readRecordJson(DUMMY_NON_AUTHORIZED_TOKEN, AUTH_TOKEN, PLACE,
				PLACE_0001);
		assertResponseStatusIs(Response.Status.FORBIDDEN);
	}

	@Test
	public void testReadRecordNotFound() {
		response = recordEndpoint.readRecordJson(AUTH_TOKEN, AUTH_TOKEN, PLACE,
				"place:0001_NOT_FOUND");
		assertResponseStatusIs(Response.Status.NOT_FOUND);
	}

	@Test
	public void testReadRecordAbstractRecordType() {
		response = recordEndpoint.readRecordJson(AUTH_TOKEN, AUTH_TOKEN, "binary",
				"image:123456789");
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

		response = recordEndpoint.readIncomingRecordLinksJson(headerAuthToken, queryAuthToken,
				PLACE, PLACE_0001);

		SpiderRecordIncomingLinksReaderSpy spiderIncomingLinksReaderSpy = spiderInstanceFactorySpy.spiderRecordIncomingLinksReaderSpy;
		assertEquals(spiderIncomingLinksReaderSpy.authToken, authTokenExpected);
	}

	@Test
	public void testReadIncomingRecordLinksForJson() {
		response = recordEndpoint.readIncomingRecordLinksJson(AUTH_TOKEN, AUTH_TOKEN, PLACE,
				PLACE_0001);
		DataList dataList = (DataList) spiderInstanceFactorySpy.spiderRecordIncomingLinksReaderSpy.MCR
				.getReturnValue("readIncomingLinks", 0);

		assertDataFromSpiderConvertedToJsonUsingConvertersFromProvider(dataList);
		assertResponseStatusIs(Response.Status.OK);
	}

	@Test
	public void testReadIncomingRecordLinksForXml() {
		response = recordEndpoint.readIncomingRecordLinksXml(AUTH_TOKEN, AUTH_TOKEN, PLACE,
				PLACE_0001);
		DataList dataList = (DataList) spiderInstanceFactorySpy.spiderRecordIncomingLinksReaderSpy.MCR
				.getReturnValue("readIncomingLinks", 0);

		assertXmlConvertionOfResponse(dataList);
		assertEntityExists();
		assertResponseStatusIs(Response.Status.OK);
		assertResponseContentTypeIs(APPLICATION_VND_UUB_RECORD_LIST_XML);
	}

	@Test
	public void testAnnotationsForReadIncomingRecordLinksJson() throws Exception {
		String methodName = "readIncomingRecordLinksJson";
		Method method = getMethodWithMethodName(methodName, 4);

		assertHttpMethodAndPathAnnotation(method, "GET", "{type}/{id}/incomingLinks");
		assertProducesAnnotation(method, APPLICATION_VND_UUB_RECORD_LIST_JSON);
		Annotation[][] parameterAnnotations = method.getParameterAnnotations();
		assertAuthTokenAnnotation(parameterAnnotations, 0);
		assertTypeAndIdAnnotation(parameterAnnotations, 2);
	}

	@Test
	public void testAnnotationsForReadIncomingRecordLinksXml() throws Exception {
		String methodName = "readIncomingRecordLinksXml";
		Method method = getMethodWithMethodName(methodName, 4);

		assertHttpMethodAndPathAnnotation(method, "GET", "{type}/{id}/incomingLinks");
		assertProducesAnnotation(method, APPLICATION_VND_UUB_RECORD_LIST_XML_QS09);
		Annotation[][] parameterAnnotations = method.getParameterAnnotations();
		assertAuthTokenAnnotation(parameterAnnotations, 0);
		assertTypeAndIdAnnotation(parameterAnnotations, 2);
	}

	// private void assertAcceptAndAuthTokenAnnotation(Annotation[][] parameterAnnotations) {
	// HeaderParam acceptParameter = (HeaderParam) parameterAnnotations[0][0];
	// assertEquals(acceptParameter.value(), "Accept");
	// assertAuthTokenAnnotation(parameterAnnotations, 1);
	// }

	private void assertAuthTokenAnnotation(Annotation[][] parameterAnnotations, int startPosition) {
		HeaderParam headerAuthTokenParameter = (HeaderParam) parameterAnnotations[startPosition][0];
		assertEquals(headerAuthTokenParameter.value(), "authToken");
		QueryParam queryAuthTokenParameter = (QueryParam) parameterAnnotations[startPosition
				+ 1][0];
		assertEquals(queryAuthTokenParameter.value(), "authToken");
	}

	@Test
	public void testReadIncomingLinksUnauthorized() {
		response = recordEndpoint.readIncomingRecordLinksJson(DUMMY_NON_AUTHORIZED_TOKEN,
				AUTH_TOKEN, PLACE, PLACE_0001);
		assertResponseStatusIs(Response.Status.FORBIDDEN);
	}

	@Test
	public void testReadIncomingLinksNotFound() {
		response = recordEndpoint.readIncomingRecordLinksJson(AUTH_TOKEN, AUTH_TOKEN, PLACE,
				"place:0001_NOT_FOUND");
		assertResponseStatusIs(Response.Status.NOT_FOUND);
	}

	@Test
	public void testReadIncomingLinksAbstractRecordType() {
		String type = "abstract";
		response = recordEndpoint.readIncomingRecordLinksJson(AUTH_TOKEN, AUTH_TOKEN, type,
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
		response = recordEndpoint.deleteRecord(DUMMY_NON_AUTHORIZED_TOKEN, AUTH_TOKEN, PLACE,
				PLACE_0001);
		assertResponseStatusIs(Response.Status.FORBIDDEN);
	}

	@Test
	public void testDeleteRecordNotFound() {
		response = recordEndpoint.deleteRecord("someToken78678567", AUTH_TOKEN, PLACE,
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
	public void testUpdateRecordForJsonXml() {
		response = recordEndpoint.updateRecordJsonXml(AUTH_TOKEN, AUTH_TOKEN, PLACE, PLACE_0001,
				defaultJson);

		DataGroup recordSentOnToSpider = spiderInstanceFactorySpy.spiderRecordUpdaterSpy.record;
		assertJsonStringConvertedToDataUsesCoraData(defaultJson, recordSentOnToSpider);

		DataRecord updatedRecord = (DataRecord) spiderInstanceFactorySpy.spiderRecordUpdaterSpy.MCR
				.getReturnValue("updateRecord", 0);

		assertXmlConvertionOfResponse(updatedRecord);
		assertEntityExists();
		assertResponseStatusIs(Response.Status.OK);
		assertResponseContentTypeIs(APPLICATION_VND_UUB_RECORD_XML);
	}

	@Test
	public void testUpdateRecordBodyInXmlWithReplyInJson() {
		response = recordEndpoint.updateRecordXmlJson(AUTH_TOKEN, AUTH_TOKEN, PLACE, PLACE_0001,
				defaultXml);

		var dataElement = getValueFromConvertAndAssertParameters();

		spiderInstanceFactorySpy.spiderRecordUpdaterSpy.MCR.assertParameters("updateRecord", 0,
				AUTH_TOKEN, PLACE, PLACE_0001, dataElement);

		DataRecord updatedRecord = (DataRecord) spiderInstanceFactorySpy.spiderRecordUpdaterSpy.MCR
				.getReturnValue("updateRecord", 0);

		assertDataFromSpiderConvertedToJsonUsingConvertersFromProvider(updatedRecord);
		assertEntityExists();
		assertResponseStatusIs(Response.Status.OK);
		assertResponseContentTypeIs(APPLICATION_VND_UUB_RECORD_JSON);
	}

	@Test
	public void testUpdateRecordForXmlXml() {
		response = recordEndpoint.updateRecordXmlXml(AUTH_TOKEN, AUTH_TOKEN, PLACE, PLACE_0001,
				defaultXml);

		var dataElement = getValueFromConvertAndAssertParameters();

		spiderInstanceFactorySpy.spiderRecordUpdaterSpy.MCR.assertParameters("updateRecord", 0,
				AUTH_TOKEN, PLACE, PLACE_0001, dataElement);

		DataRecord updatedRecord = (DataRecord) spiderInstanceFactorySpy.spiderRecordUpdaterSpy.MCR
				.getReturnValue("updateRecord", 0);

		assertXmlConvertionOfResponse(updatedRecord);
		assertEntityExists();
		assertResponseStatusIs(Response.Status.OK);
		assertResponseContentTypeIs(APPLICATION_VND_UUB_RECORD_XML);
	}

	private Object getValueFromConvertAndAssertParameters() {
		StringToExternallyConvertibleConverterSpy xmlToDataConverter = (StringToExternallyConvertibleConverterSpy) converterFactorySpy.MCR
				.getReturnValue("factorStringToExternallyConvertableConverter", 0);
		xmlToDataConverter.MCR.assertParameters("convert", 0, defaultXml);
		var dataElement = xmlToDataConverter.MCR.getReturnValue("convert", 0);
		return dataElement;
	}

	@Test
	public void testAnnotationsForUpdateRecordJsonJson() throws Exception {
		Method method = getMethodWithMethodName("updateRecordJsonJson", 5);
		Annotation[][] parameterAnnotations = method.getParameterAnnotations();

		assertHttpMethodAndPathAnnotation(method, "POST", "{type}/{id}");
		assertConsumesAnnotation(method, APPLICATION_VND_UUB_RECORD_JSON);
		assertProducesAnnotation(method, APPLICATION_VND_UUB_RECORD_JSON);
		assertAuthTokenAnnotation(parameterAnnotations, 0);
		assertTypeAndIdAnnotation(parameterAnnotations, 2);
	}

	@Test
	public void testAnnotationsForUpdateRecordJsonXml() throws Exception {
		Method method = getMethodWithMethodName("updateRecordJsonXml", 5);
		Annotation[][] parameterAnnotations = method.getParameterAnnotations();

		assertHttpMethodAndPathAnnotation(method, "POST", "{type}/{id}");
		assertConsumesAnnotation(method, APPLICATION_VND_UUB_RECORD_JSON);
		assertProducesAnnotation(method, APPLICATION_VND_UUB_RECORD_XML_QS09);
		assertAuthTokenAnnotation(parameterAnnotations, 0);
		assertTypeAndIdAnnotation(parameterAnnotations, 2);
	}

	@Test
	public void testAnnotationsForUpdateRecordXmlJson() throws Exception {
		Method method = getMethodWithMethodName("updateRecordXmlJson", 5);
		Annotation[][] parameterAnnotations = method.getParameterAnnotations();

		assertHttpMethodAndPathAnnotation(method, "POST", "{type}/{id}");
		assertConsumesAnnotation(method, APPLICATION_VND_UUB_RECORD_XML_QS09);
		assertProducesAnnotation(method, APPLICATION_VND_UUB_RECORD_JSON);
		assertAuthTokenAnnotation(parameterAnnotations, 0);
		assertTypeAndIdAnnotation(parameterAnnotations, 2);
	}

	@Test
	public void testAnnotationsForUpdateRecordXmlXml() throws Exception {
		Method method = getMethodWithMethodName("updateRecordXmlXml", 5);
		Annotation[][] parameterAnnotations = method.getParameterAnnotations();

		assertHttpMethodAndPathAnnotation(method, "POST", "{type}/{id}");
		assertConsumesAnnotation(method, APPLICATION_VND_UUB_RECORD_XML_QS09);
		assertProducesAnnotation(method, APPLICATION_VND_UUB_RECORD_XML_QS09);
		assertAuthTokenAnnotation(parameterAnnotations, 0);
		assertTypeAndIdAnnotation(parameterAnnotations, 2);
	}

	@Test
	public void testUpdateRecordUnauthorized() {
		response = recordEndpoint.updateRecordJsonJson(DUMMY_NON_AUTHORIZED_TOKEN, AUTH_TOKEN,
				PLACE, PLACE_0001, defaultJson);
		assertResponseStatusIs(Response.Status.FORBIDDEN);
		assertResponseContentTypeIs(TEXT_PLAIN);
	}

	@Test
	public void testUpdateRecordNotFound() {
		response = recordEndpoint.updateRecordJsonJson(AUTH_TOKEN, AUTH_TOKEN, PLACE,
				PLACE_0001 + "_NOT_FOUND", defaultJson);
		assertResponseStatusIs(Response.Status.NOT_FOUND);
		assertResponseContentTypeIs(TEXT_PLAIN);
	}

	@Test
	public void testUpdateRecordTypeNotFound() {
		response = recordEndpoint.updateRecordJsonJson(AUTH_TOKEN, AUTH_TOKEN, PLACE + "_NOT_FOUND",
				PLACE_0001, defaultJson);
		assertResponseStatusIs(Response.Status.NOT_FOUND);
		assertResponseContentTypeIs(TEXT_PLAIN);
	}

	@Test
	public void testUpdateRecordBadContentInJson() {
		jsonParser.throwError = true;
		response = recordEndpoint.updateRecordJsonJson(AUTH_TOKEN, AUTH_TOKEN, PLACE, PLACE_0001,
				defaultJson);
		assertResponseStatusIs(Response.Status.BAD_REQUEST);
		assertResponseContentTypeIs(TEXT_PLAIN);
	}

	@Test
	public void testUpdateRecordWrongDataTypeInJson() {
		spiderInstanceFactorySpy.throwDataException = true;
		response = recordEndpoint.updateRecordJsonJson(AUTH_TOKEN, AUTH_TOKEN, PLACE, PLACE_0001,
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

		response = recordEndpoint.createRecordJsonJson(headerAuthToken, queryAuthToken, PLACE,
				defaultJson);

		SpiderCreatorSpy spiderCreatorSpy = spiderInstanceFactorySpy.spiderCreatorSpy;
		assertEquals(spiderCreatorSpy.authToken, authTokenExpected);
	}

	@Test
	public void testCreateRecord() {
		response = recordEndpoint.createRecordJsonJson(AUTH_TOKEN, AUTH_TOKEN, PLACE, defaultJson);

		assertResponseStatusIs(Response.Status.CREATED);
		assertEquals(response.getLocation().toString(), PLACE + "/idFromDataRecordSpy");
	}

	@Test
	public void testCreateRecordUsesFactories() {

		response = recordEndpoint.createRecordJsonJson(AUTH_TOKEN, AUTH_TOKEN, PLACE, defaultJson);

		DataGroup recordSentOnToSpider = spiderInstanceFactorySpy.spiderCreatorSpy.record;
		assertJsonStringConvertedToDataUsesCoraData(defaultJson, recordSentOnToSpider);
		DataRecord createdRecord = (DataRecord) spiderInstanceFactorySpy.spiderCreatorSpy.MCR
				.getReturnValue("createAndStoreRecord", 0);

		assertDataFromSpiderConvertedToJsonUsingConvertersFromProvider(createdRecord);

		assertResponseStatusIs(Response.Status.CREATED);
		assertEquals(response.getLocation().toString(), PLACE + "/idFromDataRecordSpy");
	}

	@Test
	public void testCreateRecordBodyInJsonWithReplyInXml() {
		response = recordEndpoint.createRecordJsonXml(AUTH_TOKEN, AUTH_TOKEN, PLACE, defaultJson);

		DataGroup recordSentOnToSpider = spiderInstanceFactorySpy.spiderCreatorSpy.record;
		assertJsonStringConvertedToDataUsesCoraData(defaultJson, recordSentOnToSpider);

		DataRecord createdRecord = (DataRecord) spiderInstanceFactorySpy.spiderCreatorSpy.MCR
				.getReturnValue("createAndStoreRecord", 0);

		assertXmlConvertionOfResponse(createdRecord);
		assertEntityExists();
		assertResponseStatusIs(Response.Status.CREATED);
		assertResponseContentTypeIs(APPLICATION_VND_UUB_RECORD_XML);
	}

	@Test
	public void testCreateRecordBodyInXmlWithReplyInJson() {
		response = recordEndpoint.createRecordXmlJson(AUTH_TOKEN, AUTH_TOKEN, PLACE, defaultXml);

		var dataElement = getValueFromConvertAndAssertParameters();
		spiderInstanceFactorySpy.spiderCreatorSpy.MCR.assertParameters("createAndStoreRecord", 0,
				AUTH_TOKEN, PLACE, dataElement);

		DataRecord createdRecord = (DataRecord) spiderInstanceFactorySpy.spiderCreatorSpy.MCR
				.getReturnValue("createAndStoreRecord", 0);

		assertDataFromSpiderConvertedToJsonUsingConvertersFromProvider(createdRecord);
		assertEntityExists();
		assertResponseStatusIs(Response.Status.CREATED);
		assertResponseContentTypeIs(APPLICATION_VND_UUB_RECORD_JSON);
	}

	@Test
	public void testCreateRecordForXmlXml() {
		response = recordEndpoint.createRecordXmlXml(AUTH_TOKEN, AUTH_TOKEN, PLACE, defaultXml);

		var dataElement = getValueFromConvertAndAssertParameters();
		spiderInstanceFactorySpy.spiderCreatorSpy.MCR.assertParameters("createAndStoreRecord", 0,
				AUTH_TOKEN, PLACE, dataElement);

		DataRecord createdRecord = (DataRecord) spiderInstanceFactorySpy.spiderCreatorSpy.MCR
				.getReturnValue("createAndStoreRecord", 0);

		assertXmlConvertionOfResponse(createdRecord);
		assertEntityExists();
		assertResponseStatusIs(Response.Status.CREATED);
		assertResponseContentTypeIs(APPLICATION_VND_UUB_RECORD_XML);
	}

	@Test
	public void testAnnotationsForCreateRecordJsonJson() throws Exception {
		Method method = getMethodWithMethodName("createRecordJsonJson", 4);
		Annotation[][] parameterAnnotations = method.getParameterAnnotations();

		assertHttpMethodAndPathAnnotation(method, "POST", "{type}");
		assertConsumesAnnotation(method, APPLICATION_VND_UUB_RECORD_JSON);
		assertProducesAnnotation(method, APPLICATION_VND_UUB_RECORD_JSON);
		assertAuthTokenAnnotation(parameterAnnotations, 0);
		PathParam typeParameter = (PathParam) parameterAnnotations[2][0];
		assertEquals(typeParameter.value(), "type");
	}

	@Test
	public void testAnnotationsForCreateRecordJsonXml() throws Exception {
		Method method = getMethodWithMethodName("createRecordJsonXml", 4);
		Annotation[][] parameterAnnotations = method.getParameterAnnotations();

		assertHttpMethodAndPathAnnotation(method, "POST", "{type}");
		assertConsumesAnnotation(method, APPLICATION_VND_UUB_RECORD_JSON);
		assertProducesAnnotation(method, APPLICATION_VND_UUB_RECORD_XML_QS09);
		assertAuthTokenAnnotation(parameterAnnotations, 0);
		PathParam typeParameter = (PathParam) parameterAnnotations[2][0];
		assertEquals(typeParameter.value(), "type");
	}

	@Test
	public void testAnnotationsForCreateRecordXmlJson() throws Exception {
		Method method = getMethodWithMethodName("createRecordXmlJson", 4);
		Annotation[][] parameterAnnotations = method.getParameterAnnotations();

		assertHttpMethodAndPathAnnotation(method, "POST", "{type}");
		assertConsumesAnnotation(method, APPLICATION_VND_UUB_RECORD_XML_QS09);
		assertProducesAnnotation(method, APPLICATION_VND_UUB_RECORD_JSON);
		assertAuthTokenAnnotation(parameterAnnotations, 0);
		PathParam typeParameter = (PathParam) parameterAnnotations[2][0];
		assertEquals(typeParameter.value(), "type");
	}

	@Test
	public void testAnnotationsForCreateRecordXmlXml() throws Exception {
		Method method = getMethodWithMethodName("createRecordXmlXml", 4);
		Annotation[][] parameterAnnotations = method.getParameterAnnotations();

		assertHttpMethodAndPathAnnotation(method, "POST", "{type}");
		assertConsumesAnnotation(method, APPLICATION_VND_UUB_RECORD_XML_QS09);
		assertProducesAnnotation(method, APPLICATION_VND_UUB_RECORD_XML_QS09);
		assertAuthTokenAnnotation(parameterAnnotations, 0);
		PathParam typeParameter = (PathParam) parameterAnnotations[2][0];
		assertEquals(typeParameter.value(), "type");
	}

	@Test
	public void testCreateRecordBadCreatedLocation() {
		String type = "place&& &&\\\\";
		response = recordEndpoint.createRecordJsonJson(AUTH_TOKEN, AUTH_TOKEN, type, defaultJson);
		assertResponseStatusIs(Response.Status.BAD_REQUEST);
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
	}

	@Test
	public void testCreateRecordConversionException() {
		jsonToDataConverterFactorySpy.throwError = true;
		response = recordEndpoint.createRecordJsonJson("someToken78678567", AUTH_TOKEN, PLACE,
				defaultJson);
		assertResponseStatusIs(Response.Status.BAD_REQUEST);
	}

	@Test
	public void testCreateRecordAbstractRecordType() {
		String type = "abstract";
		response = recordEndpoint.createRecordJsonJson(AUTH_TOKEN, AUTH_TOKEN, type, defaultJson);
		assertResponseStatusIs(Response.Status.METHOD_NOT_ALLOWED);
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
		assertEquals(loggerFactorySpy.getNoOfErrorExceptionsUsingClassName(testedClassName), 0);
		response = recordEndpoint.createRecordJsonJson(AUTH_TOKEN, AUTH_TOKEN,
				"place_unexpected_error", defaultJson);
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

		response = recordEndpoint.uploadFileJson(headerAuthToken, queryAuthToken, "image",
				"image:123456789", stream, formDataContentDisposition);

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

		response = recordEndpoint.uploadFileJson(AUTH_TOKEN, AUTH_TOKEN, "image", "image:123456789",
				stream, formDataContentDisposition);

		assertResponseStatusIs(Response.Status.OK);
	}

	@Test
	public void testUploadFileForXml() {
		InputStream stream = createUplodedInputStream();
		FormDataContentDisposition formDataContentDisposition = createformDataContent();

		response = recordEndpoint.uploadFileXml(AUTH_TOKEN, AUTH_TOKEN, "image", "image:123456789",
				stream, formDataContentDisposition);
		DataRecord uploadedFile = (DataRecord) spiderInstanceFactorySpy.spiderUploaderSpy.MCR
				.getReturnValue("upload", 0);

		assertXmlConvertionOfResponse(uploadedFile);
		assertEntityExists();
		assertResponseStatusIs(Response.Status.OK);
		assertResponseContentTypeIs(APPLICATION_VND_UUB_RECORD_XML);
	}

	@Test
	public void testAnnotationsForUploadFileJson() throws Exception {
		Class<?>[] parameters = { String.class, String.class, String.class, String.class,
				InputStream.class, FormDataContentDisposition.class };
		Method method = getMethodWithMethodName("uploadFileJson", parameters);
		Annotation[][] parameterAnnotations = method.getParameterAnnotations();

		assertHttpMethodAndPathAnnotation(method, "POST", "{type}/{id}/{streamId}");
		assertProducesAnnotation(method, APPLICATION_VND_UUB_RECORD_JSON);
		assertAuthTokenAnnotation(parameterAnnotations, 0);
		assertTypeAndIdAnnotation(parameterAnnotations, 2);

		FormDataParam uploadedInputStreamParameter = (FormDataParam) parameterAnnotations[4][0];
		assertEquals(uploadedInputStreamParameter.value(), "file");
		FormDataParam fileDetailParameter = (FormDataParam) parameterAnnotations[5][0];
		assertEquals(fileDetailParameter.value(), "file");
	}

	@Test
	public void testAnnotationsForUploadFileXml() throws Exception {
		Class<?>[] parameters = { String.class, String.class, String.class, String.class,
				InputStream.class, FormDataContentDisposition.class };
		Method method = getMethodWithMethodName("uploadFileXml", parameters);
		Annotation[][] parameterAnnotations = method.getParameterAnnotations();

		assertHttpMethodAndPathAnnotation(method, "POST", "{type}/{id}/{streamId}");
		assertProducesAnnotation(method, APPLICATION_VND_UUB_RECORD_XML_QS09);
		assertAuthTokenAnnotation(parameterAnnotations, 0);
		assertTypeAndIdAnnotation(parameterAnnotations, 2);

		FormDataParam uploadedInputStreamParameter = (FormDataParam) parameterAnnotations[4][0];
		assertEquals(uploadedInputStreamParameter.value(), "file");
		FormDataParam fileDetailParameter = (FormDataParam) parameterAnnotations[5][0];
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
				APPLICATION_VND_UUB_RECORD_JSON, DUMMY_NON_AUTHORIZED_TOKEN, "image",
				"image:123456789", stream, "someFile.tif");

		assertResponseStatusIs(Response.Status.FORBIDDEN);
	}

	@Test
	public void testUploadNotFound() {
		InputStream stream = createUplodedInputStream();

		FormDataContentDisposition formDataContentDisposition = createformDataContent();

		response = recordEndpoint.uploadFileJson(AUTH_TOKEN, AUTH_TOKEN, "image",
				"image:123456789_NOT_FOUND", stream, formDataContentDisposition);

		assertResponseStatusIs(Response.Status.NOT_FOUND);
	}

	@Test
	public void testUploadNotAChildOfBinary() {
		InputStream stream = createUplodedInputStream();

		FormDataContentDisposition formDataContentDisposition = createformDataContent();

		response = recordEndpoint.uploadFileJson(AUTH_TOKEN, AUTH_TOKEN, "not_child_of_binary_type",
				"image:123456789", stream, formDataContentDisposition);

		assertResponseStatusIs(Response.Status.METHOD_NOT_ALLOWED);
		assertResponseContentTypeIs(TEXT_PLAIN);
	}

	@Test
	public void testUploadStreamMissing() {
		FormDataContentDispositionBuilder builder = FormDataContentDisposition
				.name("multipart;form-data");
		builder.fileName("adele1.png");
		FormDataContentDisposition formDataContentDisposition = builder.build();

		response = recordEndpoint.uploadFileJson(AUTH_TOKEN, AUTH_TOKEN, "image", "image:123456789",
				null, formDataContentDisposition);

		assertResponseStatusIs(Response.Status.BAD_REQUEST);
		assertResponseContentTypeIs(TEXT_PLAIN);
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
		assertResponseContentTypeIs(TEXT_PLAIN);
	}

	@Test
	public void testSearchRecordInputsReachSpider() {
		response = recordEndpoint.searchRecordJson(AUTH_TOKEN, AUTH_TOKEN, "aSearchId",
				defaultJson);

		SpiderRecordSearcherSpy spiderRecordSearcherSpy = spiderInstanceFactorySpy.spiderRecordSearcherSpy;
		assertEquals(spiderRecordSearcherSpy.authToken, AUTH_TOKEN);
		assertEquals(spiderRecordSearcherSpy.searchId, "aSearchId");

		DataGroup searchSentOnToSpider = spiderInstanceFactorySpy.spiderRecordSearcherSpy.searchData;
		assertJsonStringConvertedToDataUsesCoraData(defaultJson, searchSentOnToSpider);
	}

	@Test
	public void testSearchRecordUsesFactoriesCorrectly() {
		response = recordEndpoint.searchRecordJson(AUTH_TOKEN, AUTH_TOKEN, "aSearchId",
				defaultJson);

		SpiderRecordSearcherSpy spiderRecordSearcherSpy = spiderInstanceFactorySpy.spiderRecordSearcherSpy;

		DataGroup searchSentOnToSpider = spiderInstanceFactorySpy.spiderRecordSearcherSpy.searchData;
		assertJsonStringConvertedToDataUsesCoraData(defaultJson, searchSentOnToSpider);

		DataList returnedDataListFromReader = spiderRecordSearcherSpy.searchResult;

		assertDataFromSpiderConvertedToJsonUsingConvertersFromProvider(returnedDataListFromReader);
	}

	@Test
	public void testSearchRecordRightTokenInputsReachSpider() {
		response = recordEndpoint.searchRecordJson(AUTH_TOKEN, null, "aSearchId", defaultJson);
		SpiderRecordSearcherSpy spiderRecordSearcherSpy = spiderInstanceFactorySpy.spiderRecordSearcherSpy;
		assertEquals(spiderRecordSearcherSpy.authToken, AUTH_TOKEN);
	}

	@Test
	public void testSearchRecordRightTokenInputsReachSpider2() {
		response = recordEndpoint.searchRecordJson(null, AUTH_TOKEN, "aSearchId", defaultJson);
		SpiderRecordSearcherSpy spiderRecordSearcherSpy = spiderInstanceFactorySpy.spiderRecordSearcherSpy;
		assertEquals(spiderRecordSearcherSpy.authToken, AUTH_TOKEN);
	}

	@Test
	public void testSearchRecordRightTokenInputsReachSpider3() {
		response = recordEndpoint.searchRecordJson(AUTH_TOKEN, "otherAuthToken", "aSearchId",
				defaultJson);
		SpiderRecordSearcherSpy spiderRecordSearcherSpy = spiderInstanceFactorySpy.spiderRecordSearcherSpy;
		assertEquals(spiderRecordSearcherSpy.authToken, AUTH_TOKEN);
	}

	@Test
	public void testSearchRecord() {
		response = recordEndpoint.searchRecordJson(AUTH_TOKEN, AUTH_TOKEN, "aSearchId",
				defaultJson);
		assertEntityExists();
		assertResponseStatusIs(Response.Status.OK);
	}

	@Test
	public void testSearchRecordForXml() {
		response = recordEndpoint.searchRecordXml(AUTH_TOKEN, AUTH_TOKEN, "aSearchId", defaultXml);

		var dataElement = getValueFromConvertAndAssertParameters();

		spiderInstanceFactorySpy.spiderRecordSearcherSpy.MCR.assertParameters("search", 0,
				AUTH_TOKEN, "aSearchId", dataElement);

		DataList searchRecord = (DataList) spiderInstanceFactorySpy.spiderRecordSearcherSpy.MCR
				.getReturnValue("search", 0);

		assertXmlConvertionOfResponse(searchRecord);
		assertEntityExists();
		assertResponseStatusIs(Response.Status.OK);
		assertResponseContentTypeIs(APPLICATION_VND_UUB_RECORD_LIST_XML);
	}

	@Test
	public void testSearchRecordSearchDataInXmlForJson() {
		response = recordEndpoint.searchRecordJson(AUTH_TOKEN, AUTH_TOKEN, "aSearchId", defaultXml);

		var dataElement = getValueFromConvertAndAssertParameters();

		spiderInstanceFactorySpy.spiderRecordSearcherSpy.MCR.assertParameters("search", 0,
				AUTH_TOKEN, "aSearchId", dataElement);

		DataList searchRecord = (DataList) spiderInstanceFactorySpy.spiderRecordSearcherSpy.MCR
				.getReturnValue("search", 0);

		assertDataFromSpiderConvertedToJsonUsingConvertersFromProvider(searchRecord);
		assertEntityExists();
		assertResponseStatusIs(Response.Status.OK);
		assertResponseContentTypeIs(APPLICATION_VND_UUB_RECORD_LIST_JSON);
	}

	@Test
	public void testAnnotationsForSearchRecordJson() throws Exception {
		Method method = getMethodWithMethodName("searchRecordJson", 4);
		Annotation[][] parameterAnnotations = method.getParameterAnnotations();

		assertHttpMethodAndPathAnnotation(method, "GET", "searchResult/{searchId}");
		assertProducesAnnotation(method, APPLICATION_VND_UUB_RECORD_LIST_JSON);
		assertAuthTokenAnnotation(parameterAnnotations, 0);

		PathParam searchIdParameter = (PathParam) parameterAnnotations[2][0];
		assertEquals(searchIdParameter.value(), "searchId");

		QueryParam searchDataAsQueryParameter = (QueryParam) parameterAnnotations[3][0];
		assertEquals(searchDataAsQueryParameter.value(), "searchData");
	}

	@Test
	public void testAnnotationsForSearchRecordXml() throws Exception {
		Method method = getMethodWithMethodName("searchRecordXml", 4);
		Annotation[][] parameterAnnotations = method.getParameterAnnotations();

		assertHttpMethodAndPathAnnotation(method, "GET", "searchResult/{searchId}");
		assertProducesAnnotation(method, APPLICATION_VND_UUB_RECORD_LIST_XML_QS09);
		assertAuthTokenAnnotation(parameterAnnotations, 0);

		PathParam searchIdParameter = (PathParam) parameterAnnotations[2][0];
		assertEquals(searchIdParameter.value(), "searchId");

		QueryParam searchDataAsQueryParameter = (QueryParam) parameterAnnotations[3][0];
		assertEquals(searchDataAsQueryParameter.value(), "searchData");
	}

	@Test
	public void testSearchRecordSearchIdNotFound() {
		response = recordEndpoint.searchRecordJson(AUTH_TOKEN, AUTH_TOKEN, "aSearchId_NOT_FOUND",
				defaultJson);
		assertResponseStatusIs(Response.Status.NOT_FOUND);
	}

	@Test
	public void testSearchRecordInvalidSearchData() {
		response = recordEndpoint.searchRecordJson(AUTH_TOKEN, AUTH_TOKEN, "aSearchId_INVALID_DATA",
				defaultJson);
		assertResponseStatusIs(Response.Status.BAD_REQUEST);
	}

	@Test
	public void testSearchRecordUnauthorized() {
		response = recordEndpoint.searchRecordJson(DUMMY_NON_AUTHORIZED_TOKEN,
				DUMMY_NON_AUTHORIZED_TOKEN, "aSearchId", defaultJson);
		assertResponseStatusIs(Response.Status.FORBIDDEN);
	}

	@Test
	public void testSearchRecordUnauthenticated() {
		response = recordEndpoint.searchRecordJson("nonExistingToken", "nonExistingToken",
				"aSearchId", defaultJson);
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

		response = recordEndpoint.validateRecordJsonJson(headerAuthToken, queryAuthToken, PLACE,
				jsonToValidate);

		SpiderRecordValidatorSpy spiderRecordValidatorSpy = spiderInstanceFactorySpy.spiderRecordValidatorSpy;

		assertEquals(spiderRecordValidatorSpy.authToken, authTokenExpected);
	}

	@Test
	public void testValidateRecord() {
		response = recordEndpoint.validateRecordJsonJson(AUTH_TOKEN, AUTH_TOKEN, "workOrder",
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
		response = recordEndpoint.validateRecordXmlXml(AUTH_TOKEN, AUTH_TOKEN, PLACE, defaultXml);

		DataGroupSpy container = (DataGroupSpy) getValueFromConvertAndAssertParameters();

		container.MCR.assertParameters("getFirstGroupWithNameInData", 0, "order");
		container.MCR.assertParameters("getFirstGroupWithNameInData", 1, "record");

		var validationOrder = container.MCR.getReturnValue("getFirstGroupWithNameInData", 0);
		var recordToValidate = container.MCR.getReturnValue("getFirstGroupWithNameInData", 1);

		spiderInstanceFactorySpy.spiderRecordValidatorSpy.MCR.assertParameters("validateRecord", 0,
				AUTH_TOKEN, "validationOrder", validationOrder, recordToValidate);

		DataRecord validationResult = (DataRecord) spiderInstanceFactorySpy.spiderRecordValidatorSpy.MCR
				.getReturnValue("validateRecord", 0);

		assertXmlConvertionOfResponse(validationResult);
		assertEntityExists();
		assertResponseStatusIs(Response.Status.OK);
		assertResponseContentTypeIs(APPLICATION_VND_UUB_RECORD_XML);
	}

	@Test
	public void testValidateRecordInputAsJsonResponseAsXml() {
		response = recordEndpoint.validateRecordJsonXml(AUTH_TOKEN, AUTH_TOKEN, PLACE, defaultJson);

		JsonValueSpy topJsonObject = jsonParser.returnedJsonValue;
		assertEquals(topJsonObject.keys.get(0), "order");
		assertEquals(topJsonObject.keys.get(1), "record");

		DataGroup validationOrderSentOnToSpider = spiderInstanceFactorySpy.spiderRecordValidatorSpy.validationOrder;
		DataGroup recordToValidateSentOnToSpider = spiderInstanceFactorySpy.spiderRecordValidatorSpy.recordToValidate;

		spiderInstanceFactorySpy.spiderRecordValidatorSpy.MCR.assertParameters("validateRecord", 0,
				AUTH_TOKEN, "validationOrder", validationOrderSentOnToSpider,
				recordToValidateSentOnToSpider);

		DataRecord validationResult = (DataRecord) spiderInstanceFactorySpy.spiderRecordValidatorSpy.MCR
				.getReturnValue("validateRecord", 0);

		assertXmlConvertionOfResponse(validationResult);
		assertEntityExists();
		assertResponseStatusIs(Response.Status.OK);
		assertResponseContentTypeIs(APPLICATION_VND_UUB_RECORD_XML);
	}

	@Test
	public void testValidateRecordInputAsXMLResponseAsJson() {
		response = recordEndpoint.validateRecordXmlJson(AUTH_TOKEN, AUTH_TOKEN, PLACE, defaultXml);

		DataGroupSpy container = (DataGroupSpy) getValueFromConvertAndAssertParameters();

		container.MCR.assertParameters("getFirstGroupWithNameInData", 0, "order");
		container.MCR.assertParameters("getFirstGroupWithNameInData", 1, "record");

		var validationOrder = container.MCR.getReturnValue("getFirstGroupWithNameInData", 0);
		var recordToValidate = container.MCR.getReturnValue("getFirstGroupWithNameInData", 1);

		spiderInstanceFactorySpy.spiderRecordValidatorSpy.MCR.assertParameters("validateRecord", 0,
				AUTH_TOKEN, "validationOrder", validationOrder, recordToValidate);

		DataRecord validationResult = (DataRecord) spiderInstanceFactorySpy.spiderRecordValidatorSpy.MCR
				.getReturnValue("validateRecord", 0);

		assertDataFromSpiderConvertedToJsonUsingConvertersFromProvider(validationResult);
		assertEntityExists();
		assertResponseStatusIs(Response.Status.OK);
		assertResponseContentTypeIs(APPLICATION_VND_UUB_RECORD_JSON);
	}

	@Test
	public void testValidateRecordInputJsonWrongContentType() {
		jsonParser.throwError = true;
		response = recordEndpoint.validateRecordJsonJson(AUTH_TOKEN, AUTH_TOKEN, PLACE, defaultXml);
		assertResponseStatusIs(Response.Status.BAD_REQUEST);
		assertResponseContentTypeIs(TEXT_PLAIN);
	}

	@Test
	public void testValidateRecordInputXmlWrongContentType() {
		converterFactorySpy.xmlToDataConverterThrowsException = true;
		response = recordEndpoint.validateRecordXmlJson(AUTH_TOKEN, AUTH_TOKEN, PLACE, defaultJson);
		assertResponseStatusIs(Response.Status.BAD_REQUEST);
		assertResponseContentTypeIs(TEXT_PLAIN);
	}

	@Test
	public void testAnnotationsForValidateRecordJsonJson() throws Exception {
		Method method = getMethodWithMethodName("validateRecordJsonJson", 4);
		Annotation[][] parameterAnnotations = method.getParameterAnnotations();

		assertHttpMethodAndPathAnnotation(method, "POST", "{type}");
		assertConsumesAnnotation(method, "application/vnd.uub.workorder+json");
		assertProducesAnnotation(method, APPLICATION_VND_UUB_RECORD_JSON);
		assertAuthTokenAnnotation(parameterAnnotations, 0);

		PathParam typeParameter = (PathParam) parameterAnnotations[2][0];
		assertEquals(typeParameter.value(), "type");
	}

	@Test
	public void testAnnotationsForValidateRecordJsonXml() throws Exception {
		Method method = getMethodWithMethodName("validateRecordJsonXml", 4);
		Annotation[][] parameterAnnotations = method.getParameterAnnotations();

		assertHttpMethodAndPathAnnotation(method, "POST", "{type}");
		assertConsumesAnnotation(method, "application/vnd.uub.workorder+json");
		assertProducesAnnotation(method, APPLICATION_VND_UUB_RECORD_XML_QS09);
		assertAuthTokenAnnotation(parameterAnnotations, 0);

		PathParam typeParameter = (PathParam) parameterAnnotations[2][0];
		assertEquals(typeParameter.value(), "type");
	}

	@Test
	public void testAnnotationsForValidateRecordXmlJson() throws Exception {
		Method method = getMethodWithMethodName("validateRecordXmlJson", 4);
		Annotation[][] parameterAnnotations = method.getParameterAnnotations();

		assertHttpMethodAndPathAnnotation(method, "POST", "{type}");
		assertConsumesAnnotation(method, "application/vnd.uub.workorder+xml" + ";qs=0.9");
		assertProducesAnnotation(method, APPLICATION_VND_UUB_RECORD_JSON);
		assertAuthTokenAnnotation(parameterAnnotations, 0);

		PathParam typeParameter = (PathParam) parameterAnnotations[2][0];
		assertEquals(typeParameter.value(), "type");
	}

	@Test
	public void testAnnotationsForValidateRecordXmlXml() throws Exception {
		Method method = getMethodWithMethodName("validateRecordXmlXml", 4);
		Annotation[][] parameterAnnotations = method.getParameterAnnotations();

		assertHttpMethodAndPathAnnotation(method, "POST", "{type}");
		assertConsumesAnnotation(method, "application/vnd.uub.workorder+xml" + ";qs=0.9");
		assertProducesAnnotation(method, APPLICATION_VND_UUB_RECORD_XML_QS09);
		assertAuthTokenAnnotation(parameterAnnotations, 0);

		PathParam typeParameter = (PathParam) parameterAnnotations[2][0];
		assertEquals(typeParameter.value(), "type");
	}

	@Test
	public void testValidateRecordUnauthorized() {
		response = recordEndpoint.validateRecordJsonJson(DUMMY_NON_AUTHORIZED_TOKEN, AUTH_TOKEN,
				"workOrder", jsonToValidate);
		assertResponseStatusIs(Response.Status.FORBIDDEN);
		assertResponseContentTypeIs(TEXT_PLAIN);
	}

	@Test
	public void testValidateRecordTypeNotFound() {
		spiderInstanceFactorySpy.throwRecordNotFoundException = true;
		response = recordEndpoint.validateRecordJsonJson(AUTH_TOKEN, AUTH_TOKEN, "workOrder",
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
		response = recordEndpoint.batchIndexJsonJson(headerAuthToken, queryAuthToken, PLACE,
				jsonFilter);

		IndexBatchJobCreatorSpy indexBatchJobSpy = spiderInstanceFactorySpy.spiderRecordListIndexerSpy;

		assertEquals(indexBatchJobSpy.authToken, authTokenExpected);
	}

	@Test
	public void testBatchIndexWithFilter() {
		response = recordEndpoint.batchIndexJsonJson(AUTH_TOKEN, AUTH_TOKEN, PLACE, jsonIndexData);

		IndexBatchJobCreatorSpy indexBatchJobCreator = spiderInstanceFactorySpy.spiderRecordListIndexerSpy;

		DataGroup filterSentOnToSpider = spiderInstanceFactorySpy.spiderRecordListIndexerSpy.filter;
		assertJsonStringConvertedToDataUsesCoraData(jsonIndexData, filterSentOnToSpider);

		assertEquals(indexBatchJobCreator.type, PLACE);

		DataRecordSpy recordToReturn = spiderInstanceFactorySpy.spiderRecordListIndexerSpy.recordToReturn;
		assertDataFromSpiderConvertedToJsonUsingConvertersFromProvider(recordToReturn);

		assertEntityExists();
		assertResponseStatusIs(Response.Status.CREATED);
		assertEquals(response.getLocation().toString(), "indexBatchJob/idFromDataRecordSpy");
	}

	@Test
	public void testIndexRecordListWithNullAsFilter() {
		response = recordEndpoint.batchIndexJsonJson(AUTH_TOKEN, AUTH_TOKEN, PLACE, null);
		assertEntityExists();
		assertResponseStatusIs(Response.Status.CREATED);
		assertEquals(response.getLocation().toString(), "indexBatchJob/idFromDataRecordSpy");

		assertEquals(jsonParser.jsonString, "{\"name\":\"indexSettings\",\"children\":[]}");
	}

	@Test
	public void testIndexRecordListWithEmptyFilter() {
		response = recordEndpoint.batchIndexJsonJson(AUTH_TOKEN, AUTH_TOKEN, PLACE, "");
		assertEntityExists();
		assertResponseStatusIs(Response.Status.CREATED);
		assertEquals(response.getLocation().toString(), "indexBatchJob/idFromDataRecordSpy");

		assertEquals(jsonParser.jsonString, "{\"name\":\"indexSettings\",\"children\":[]}");
	}

	@Test
	public void testIndexRecordListWithFilterAsXmlAndResponseXml() {
		response = recordEndpoint.batchIndexXmlXml(AUTH_TOKEN, AUTH_TOKEN, PLACE, defaultXml);

		var filterElement = getValueFromConvertAndAssertParameters();

		spiderInstanceFactorySpy.spiderRecordListIndexerSpy.MCR.assertParameters("indexRecordList",
				0, AUTH_TOKEN, "place", filterElement);

		DataRecord dataList = (DataRecord) spiderInstanceFactorySpy.spiderRecordListIndexerSpy.MCR
				.getReturnValue("indexRecordList", 0);

		assertXmlConvertionOfResponse(dataList);
		assertEntityExists();
		assertResponseStatusIs(Response.Status.CREATED);
		assertResponseContentTypeIs(APPLICATION_VND_UUB_RECORD_XML);
	}

	@Test
	public void testIndexRecordListWithFilterAsXmlndResponseJson() {
		response = recordEndpoint.batchIndexXmlJson(AUTH_TOKEN, AUTH_TOKEN, PLACE, defaultXml);

		var filterElement = getValueFromConvertAndAssertParameters();

		spiderInstanceFactorySpy.spiderRecordListIndexerSpy.MCR.assertParameters("indexRecordList",
				0, AUTH_TOKEN, "place", filterElement);

		DataRecord dataList = (DataRecord) spiderInstanceFactorySpy.spiderRecordListIndexerSpy.MCR
				.getReturnValue("indexRecordList", 0);

		assertDataFromSpiderConvertedToJsonUsingConvertersFromProvider(dataList);
		assertEntityExists();
		assertResponseStatusIs(Response.Status.CREATED);
		assertResponseContentTypeIs(APPLICATION_VND_UUB_RECORD_JSON);
	}

	@Test
	public void testIndexRecordListWithFilterAsJsonAndResponseXml() {
		response = recordEndpoint.batchIndexJsonXml(AUTH_TOKEN, AUTH_TOKEN, PLACE, jsonIndexData);

		DataGroup filterSentOnToSpider = spiderInstanceFactorySpy.spiderRecordListIndexerSpy.filter;
		assertJsonStringConvertedToDataUsesCoraData(jsonIndexData, filterSentOnToSpider);

		spiderInstanceFactorySpy.spiderRecordListIndexerSpy.MCR.assertParameters("indexRecordList",
				0, AUTH_TOKEN, "place", filterSentOnToSpider);

		DataRecord dataList = (DataRecord) spiderInstanceFactorySpy.spiderRecordListIndexerSpy.MCR
				.getReturnValue("indexRecordList", 0);

		assertXmlConvertionOfResponse(dataList);
		assertEntityExists();
		assertResponseStatusIs(Response.Status.CREATED);
		assertResponseContentTypeIs(APPLICATION_VND_UUB_RECORD_XML);
	}

	@Test
	public void testIndexRecordListWithJsonAnWrongContentTypeAndResponseXml() {
		converterFactorySpy.xmlToDataConverterThrowsException = true;

		response = recordEndpoint.batchIndexXmlXml(AUTH_TOKEN, AUTH_TOKEN, PLACE, jsonIndexData);

		StringToExternallyConvertibleConverterSpy xmlToDataConverter = (StringToExternallyConvertibleConverterSpy) converterFactorySpy.MCR
				.getReturnValue("factorStringToExternallyConvertableConverter", 0);

		xmlToDataConverter.MCR.assertParameters("convert", 0, jsonIndexData);

		assertEquals(response.getEntity(), "exception from spy");
		assertResponseStatusIs(Response.Status.BAD_REQUEST);
	}

	@Test
	public void testIndexRecordListWithNullFilterAsXmlAndResponseXml() {
		response = recordEndpoint.batchIndexXmlXml(AUTH_TOKEN, AUTH_TOKEN, PLACE, null);

		DataGroup filterSentOnToSpider = spiderInstanceFactorySpy.spiderRecordListIndexerSpy.filter;
		assertJsonStringConvertedToDataUsesCoraData("{\"name\":\"indexSettings\",\"children\":[]}",
				filterSentOnToSpider);

		spiderInstanceFactorySpy.spiderRecordListIndexerSpy.MCR.assertParameters("indexRecordList",
				0, AUTH_TOKEN, "place", filterSentOnToSpider);

		DataRecord dataList = (DataRecord) spiderInstanceFactorySpy.spiderRecordListIndexerSpy.MCR
				.getReturnValue("indexRecordList", 0);

		assertXmlConvertionOfResponse(dataList);
		assertEntityExists();
		assertResponseStatusIs(Response.Status.CREATED);
		assertResponseContentTypeIs(APPLICATION_VND_UUB_RECORD_XML);
	}

	@Test
	public void testAnnotationsForIndexRecordListJsonJson() throws Exception {
		Method method = getMethodWithMethodName("batchIndexJsonJson", 4);
		Annotation[][] parameterAnnotations = method.getParameterAnnotations();

		assertHttpMethodAndPathAnnotation(method, "POST", "index/{type}");
		assertConsumesAnnotation(method, APPLICATION_VND_UUB_RECORD_JSON);
		assertProducesAnnotation(method, APPLICATION_VND_UUB_RECORD_JSON);
		assertAuthTokenAnnotation(parameterAnnotations, 0);

		PathParam typeParameter = (PathParam) parameterAnnotations[2][0];
		assertEquals(typeParameter.value(), "type");
	}

	@Test
	public void testAnnotationsForIndexRecordListJsonXml() throws Exception {
		Method method = getMethodWithMethodName("batchIndexJsonXml", 4);
		Annotation[][] parameterAnnotations = method.getParameterAnnotations();

		assertHttpMethodAndPathAnnotation(method, "POST", "index/{type}");
		assertConsumesAnnotation(method, APPLICATION_VND_UUB_RECORD_JSON);
		assertProducesAnnotation(method, APPLICATION_VND_UUB_RECORD_XML_QS09);
		assertAuthTokenAnnotation(parameterAnnotations, 0);

		PathParam typeParameter = (PathParam) parameterAnnotations[2][0];
		assertEquals(typeParameter.value(), "type");
	}

	@Test
	public void testAnnotationsForIndexRecordListXmlJson() throws Exception {
		Method method = getMethodWithMethodName("batchIndexXmlJson", 4);
		Annotation[][] parameterAnnotations = method.getParameterAnnotations();

		assertHttpMethodAndPathAnnotation(method, "POST", "index/{type}");
		assertConsumesAnnotation(method, APPLICATION_VND_UUB_RECORD_XML_QS09);
		assertProducesAnnotation(method, APPLICATION_VND_UUB_RECORD_JSON);
		assertAuthTokenAnnotation(parameterAnnotations, 0);

		PathParam typeParameter = (PathParam) parameterAnnotations[2][0];
		assertEquals(typeParameter.value(), "type");
	}

	@Test
	public void testAnnotationsForIndexRecordListXmlXml() throws Exception {
		Method method = getMethodWithMethodName("batchIndexXmlXml", 4);
		Annotation[][] parameterAnnotations = method.getParameterAnnotations();

		assertHttpMethodAndPathAnnotation(method, "POST", "index/{type}");
		assertConsumesAnnotation(method, APPLICATION_VND_UUB_RECORD_XML_QS09);
		assertProducesAnnotation(method, APPLICATION_VND_UUB_RECORD_XML_QS09);
		assertAuthTokenAnnotation(parameterAnnotations, 0);

		PathParam typeParameter = (PathParam) parameterAnnotations[2][0];
		assertEquals(typeParameter.value(), "type");
	}

	@Test
	public void testIndexRecordListNotFound() {
		response = recordEndpoint.batchIndexJsonJson(AUTH_TOKEN, AUTH_TOKEN,
				"recordType_NON_EXISTING", jsonFilterData);
		assertResponseStatusIs(Response.Status.NOT_FOUND);
	}

	@Test
	public void testIndexRecordListUnauthorized() {
		response = recordEndpoint.batchIndexJsonJson(DUMMY_NON_AUTHORIZED_TOKEN, AUTH_TOKEN, PLACE,
				jsonFilterData);
		assertResponseStatusIs(Response.Status.FORBIDDEN);
	}

	@Test
	public void testIndexRecordListNoTokenAndUnauthorized() {
		response = recordEndpoint.batchIndexJsonJson(null, null, PLACE, jsonFilterData);
		assertResponseStatusIs(Response.Status.UNAUTHORIZED);
		assertResponseContentTypeIs(TEXT_PLAIN);
	}

}