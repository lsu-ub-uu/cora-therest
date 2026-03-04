/*
 * Copyright 2015, 2016, 2018, 2021, 2022, 2024, 2025, 2026 Uppsala University Library
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

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import se.uu.ub.cora.converter.ConverterProvider;
import se.uu.ub.cora.converter.ExternalUrls;
import se.uu.ub.cora.data.Convertible;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataList;
import se.uu.ub.cora.data.DataProvider;
import se.uu.ub.cora.data.converter.DataToJsonConverterProvider;
import se.uu.ub.cora.data.converter.JsonToDataConverterProvider;
import se.uu.ub.cora.data.spies.DataFactorySpy;
import se.uu.ub.cora.json.parser.org.OrgJsonParser;
import se.uu.ub.cora.logger.LoggerProvider;
import se.uu.ub.cora.logger.spies.LoggerFactorySpy;
import se.uu.ub.cora.spider.authentication.AuthenticationException;
import se.uu.ub.cora.spider.authorization.AuthorizationException;
import se.uu.ub.cora.spider.dependency.SpiderInstanceProvider;
import se.uu.ub.cora.spider.record.RecordNotFoundException;
import se.uu.ub.cora.spider.spies.RecordListReaderSpy;
import se.uu.ub.cora.spider.spies.SpiderInstanceFactorySpy;
import se.uu.ub.cora.therest.AnnotationTestHelper;
import se.uu.ub.cora.therest.dependency.TheRestInstanceFactorySpy;
import se.uu.ub.cora.therest.dependency.TheRestInstanceProvider;
import se.uu.ub.cora.therest.url.UrlHandlerSpy;

public class RecordEndpointReadListTest {
	private static final String APPLICATION_XML = "application/xml";
	private static final String APPLICATION_XML_QS01 = "application/xml;qs=0.1";
	private static final String APPLICATION_VND_CORA_RECORD_LIST_XML = "application/vnd.cora.recordList+xml";
	private static final String APPLICATION_VND_CORA_RECORD_LIST_JSON_QS09 = "application/vnd.cora.recordList+json;qs=0.9";
	private static final String DUMMY_NON_AUTHORIZED_TOKEN = "dummyNonAuthorizedToken";
	private static final String PLACE = "place";
	private static final String AUTH_TOKEN = "authToken";
	private JsonParserSpy jsonParser;

	private JsonToDataConverterFactorySpy jsonToDataConverterFactorySpy = new JsonToDataConverterFactorySpy();

	private RecordEndpointReadList recordEndpoint;
	private SpiderInstanceFactorySpy spiderInstanceFactorySpy;
	private Response response;
	private HttpServletRequestOldSpy requestSpy;
	private LoggerFactorySpy loggerFactorySpy;
	private DataFactorySpy dataFactorySpy;

	private String defaultXml = "<someXml></someXml>";
	private String jsonFilterData = "{\"name\":\"filter\",\"children\":[{\"name\":\"part\",\"children\":[{\"name\":\"key\",\"value\":\"movieTitle\"},{\"name\":\"value\",\"value\":\"Some title\"}],\"repeatId\":\"0\"}]}";
	private DataToJsonConverterFactoryCreatorSpy converterFactoryCreatorSpy;
	private ConverterFactorySpy converterFactorySpy;
	private StringToExternallyConvertibleConverterSpy stringToExternallyConvertibleConverterSpy;
	private TheRestInstanceFactorySpy instanceFactory;

	@BeforeMethod
	public void beforeMethod() {
		loggerFactorySpy = new LoggerFactorySpy();
		LoggerProvider.setLoggerFactory(loggerFactorySpy);
		dataFactorySpy = new DataFactorySpy();
		DataProvider.onlyForTestSetDataFactory(dataFactorySpy);
		setupUrlHandler();

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
		spiderInstanceFactorySpy = new SpiderInstanceFactorySpy();
		SpiderInstanceProvider.setSpiderInstanceFactory(spiderInstanceFactorySpy);

		requestSpy = new HttpServletRequestOldSpy();
		recordEndpoint = new RecordEndpointReadList(requestSpy);

		setUpSpiesInRecordEndpoint();
	}

	private void setupUrlHandler() {
		instanceFactory = new TheRestInstanceFactorySpy();
		TheRestInstanceProvider.onlyForTestSetTheRestInstanceFactory(instanceFactory);
	}

	private void setUpSpiesInRecordEndpoint() {
		jsonParser = new JsonParserSpy();

		recordEndpoint.setJsonParser(jsonParser);
	}

	@Test
	public void testInit() {
		recordEndpoint = new RecordEndpointReadList(requestSpy);
		assertTrue(recordEndpoint.getJsonParser() instanceof OrgJsonParser);
	}

	@Test
	public void testClassAnnotation() throws Exception {
		AnnotationTestHelper annotationHelper = AnnotationTestHelper
				.createAnnotationTestHelperForClass(RecordEndpointReadList.class);

		annotationHelper.assertPathAnnotationForClass("/");
	}

	@Test
	public void testUrlsHandledByUrlHandler() {
		response = recordEndpoint.readRecordListJson(AUTH_TOKEN, AUTH_TOKEN, PLACE, jsonFilterData);

		UrlHandlerSpy urlHandler = (UrlHandlerSpy) instanceFactory.MCR
				.getReturnValue("factorUrlHandler", 0);

		var restUrl = urlHandler.MCR.assertCalledParametersReturn("getRestRecordUrl", requestSpy);
		var iiifUrl = urlHandler.MCR.assertCalledParametersReturn("getIiifUrl", requestSpy);

		assertEquals(restUrl, getRestUrlFromFactorUsingConvertibleAndExternalUrls());
		assertEquals(iiifUrl, getIiifUrlFromFactorUsingConvertibleAndExternalUrls());
	}

	private String getRestUrlFromFactorUsingConvertibleAndExternalUrls() {
		DataToJsonConverterFactorySpy converterFactory = (DataToJsonConverterFactorySpy) converterFactoryCreatorSpy.MCR
				.getReturnValue("createFactory", 0);
		se.uu.ub.cora.data.converter.ExternalUrls externalUrls = (se.uu.ub.cora.data.converter.ExternalUrls) converterFactory.MCR
				.getParameterForMethodAndCallNumberAndParameter(
						"factorUsingConvertibleAndExternalUrls", 0, "externalUrls");
		return externalUrls.getBaseUrl();
	}

	private String getIiifUrlFromFactorUsingConvertibleAndExternalUrls() {
		DataToJsonConverterFactorySpy converterFactory = (DataToJsonConverterFactorySpy) converterFactoryCreatorSpy.MCR
				.getReturnValue("createFactory", 0);
		se.uu.ub.cora.data.converter.ExternalUrls externalUrls = (se.uu.ub.cora.data.converter.ExternalUrls) converterFactory.MCR
				.getParameterForMethodAndCallNumberAndParameter(
						"factorUsingConvertibleAndExternalUrls", 0, "externalUrls");
		return externalUrls.getIfffUrl();
	}

	@Test
	public void testPreferredTokenForReadList() {
		expectTokenForReadListToPrefereblyBeHeaderThanQuery(0, null, AUTH_TOKEN, AUTH_TOKEN);
		expectTokenForReadListToPrefereblyBeHeaderThanQuery(1, AUTH_TOKEN, null, AUTH_TOKEN);
		expectTokenForReadListToPrefereblyBeHeaderThanQuery(2, null, null, null);
	}

	private void expectTokenForReadListToPrefereblyBeHeaderThanQuery(int callNo,
			String headerAuthToken, String queryAuthToken, String authTokenExpected) {

		response = recordEndpoint.readRecordListJson(headerAuthToken, queryAuthToken, PLACE,
				"someFilter");

		RecordListReaderSpy spiderListReaderSpy = (RecordListReaderSpy) spiderInstanceFactorySpy.MCR
				.getReturnValue("factorRecordListReader", callNo);
		spiderListReaderSpy.MCR.assertParameter("readRecordList", 0, "authToken",
				authTokenExpected);
	}

	@Test
	public void testReadRecordListWithFilter() {
		response = recordEndpoint.readRecordListJson(AUTH_TOKEN, AUTH_TOKEN, PLACE, jsonFilterData);

		RecordListReaderSpy spiderListReaderSpy = (RecordListReaderSpy) spiderInstanceFactorySpy.MCR
				.getReturnValue("factorRecordListReader", 0);
		var filterSentOnToSpider = (DataGroup) spiderListReaderSpy.MCR
				.getParameterForMethodAndCallNumberAndParameter("readRecordList", 0, "filter");
		assertJsonStringConvertedToGroupUsesCoraData(jsonFilterData, filterSentOnToSpider);

		DataList dataList = (DataList) spiderListReaderSpy.MCR.getReturnValue("readRecordList", 0);

		assertDataFromSpiderConvertedToJsonUsingConvertersFromProvider(dataList);
		assertResponseStatusIs(Response.Status.OK);
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

		var filterElement = assertParametersAndGetConvertedXmlDataElement();

		RecordListReaderSpy spiderListReaderSpy = (RecordListReaderSpy) spiderInstanceFactorySpy.MCR
				.getReturnValue("factorRecordListReader", 0);
		spiderListReaderSpy.MCR.assertParameters("readRecordList", 0, AUTH_TOKEN, "place",
				filterElement);

		DataList dataList = (DataList) spiderListReaderSpy.MCR.getReturnValue("readRecordList", 0);

		assertXmlConvertionOfResponse(dataList);
		assertEntityExists();
		assertResponseStatusIs(Response.Status.OK);
		assertResponseContentTypeIs(APPLICATION_VND_CORA_RECORD_LIST_XML);
	}

	@Test
	public void testReadRecordListAsApplicationXmlForBrowsers() {
		response = recordEndpoint.readRecordListAsApplicationXmlForBrowsers(AUTH_TOKEN, AUTH_TOKEN,
				"place", defaultXml);

		var filterElement = assertParametersAndGetConvertedXmlDataElement();

		RecordListReaderSpy spiderListReaderSpy = (RecordListReaderSpy) spiderInstanceFactorySpy.MCR
				.getReturnValue("factorRecordListReader", 0);
		spiderListReaderSpy.MCR.assertParameters("readRecordList", 0, AUTH_TOKEN, "place",
				filterElement);

		DataList dataList = (DataList) spiderListReaderSpy.MCR.getReturnValue("readRecordList", 0);

		assertXmlConvertionOfResponse(dataList);
		assertEntityExists();
		assertResponseStatusIs(Response.Status.OK);
		assertResponseContentTypeIs(APPLICATION_XML);
	}

	@Test
	public void testReadRecordListWithNullFilterForXml() {
		SpiderInstanceFactorySpy factory = new SpiderInstanceFactorySpy();
		SpiderInstanceProvider.setSpiderInstanceFactory(factory);

		response = recordEndpoint.readRecordListXml(AUTH_TOKEN, AUTH_TOKEN, "place", null);

		var emptyFilterGroup = dataFactorySpy.MCR
				.assertCalledParametersReturn("factorGroupUsingNameInData", "filter");

		var spiderRecordListReaderSpy = getRecordListReader(factory);

		DataList dataList = (DataList) spiderRecordListReaderSpy.MCR.assertCalledParametersReturn(
				"readRecordList", AUTH_TOKEN, "place", emptyFilterGroup);

		assertXmlConvertionOfResponse(dataList);
		assertEntityExists();
		assertResponseStatusIs(Response.Status.OK);
		assertResponseContentTypeIs(APPLICATION_VND_CORA_RECORD_LIST_XML);
	}

	@Test
	public void testReadRecordListWithNullFilterAsApplicationXmlForBrowsers() {
		SpiderInstanceFactorySpy factory = new SpiderInstanceFactorySpy();
		SpiderInstanceProvider.setSpiderInstanceFactory(factory);

		response = recordEndpoint.readRecordListAsApplicationXmlForBrowsers(AUTH_TOKEN, AUTH_TOKEN,
				"place", null);

		var emptyFilterGroup = dataFactorySpy.MCR
				.assertCalledParametersReturn("factorGroupUsingNameInData", "filter");

		var spiderRecordListReaderSpy = getRecordListReader(factory);

		DataList dataList = (DataList) spiderRecordListReaderSpy.MCR.assertCalledParametersReturn(
				"readRecordList", AUTH_TOKEN, "place", emptyFilterGroup);

		assertXmlConvertionOfResponse(dataList);
		assertEntityExists();
		assertResponseStatusIs(Response.Status.OK);
		assertResponseContentTypeIs(APPLICATION_XML);
	}

	private RecordListReaderSpy getRecordListReader(SpiderInstanceFactorySpy factory) {
		return (RecordListReaderSpy) factory.MCR
				.assertCalledParametersReturn("factorRecordListReader");
	}

	@Test
	public void testAnnotationsForReadRecordListJson() throws Exception {
		AnnotationTestHelper annotationHelper = AnnotationTestHelper
				.createAnnotationTestHelperForClassMethodNameAndNumOfParameters(
						recordEndpoint.getClass(), "readRecordListJson", 4);

		annotationHelper.assertHttpMethodAndPathAnnotation("GET", "{type}/");
		annotationHelper.assertProducesAnnotation(APPLICATION_VND_CORA_RECORD_LIST_JSON_QS09);
		annotationHelper.assertAnnotationForAuthTokensAndTypeParameters();
		annotationHelper.assertQueryParamAnnotationByNameAndPosition("filter", 3);
	}

	@Test
	public void testAnnotationsForReadRecordListXml() throws Exception {
		AnnotationTestHelper annotationHelper = AnnotationTestHelper
				.createAnnotationTestHelperForClassMethodNameAndNumOfParameters(
						recordEndpoint.getClass(), "readRecordListXml", 4);

		annotationHelper.assertHttpMethodAndPathAnnotation("GET", "{type}/");
		annotationHelper.assertProducesAnnotation(APPLICATION_VND_CORA_RECORD_LIST_XML);
		annotationHelper.assertAnnotationForAuthTokensAndTypeParameters();
		annotationHelper.assertQueryParamAnnotationByNameAndPosition("filter", 3);
	}

	@Test
	public void testAnnotationsForReadRecordListAsApplicationXmlForBrowsers() throws Exception {
		AnnotationTestHelper annotationHelper = AnnotationTestHelper
				.createAnnotationTestHelperForClassMethodNameAndNumOfParameters(
						recordEndpoint.getClass(), "readRecordListAsApplicationXmlForBrowsers", 4);

		annotationHelper.assertHttpMethodAndPathAnnotation("GET", "{type}/");
		annotationHelper.assertProducesAnnotation(APPLICATION_XML_QS01);
		annotationHelper.assertAnnotationForAuthTokensAndTypeParameters();
		annotationHelper.assertQueryParamAnnotationByNameAndPosition("filter", 3);
	}

	@Test
	public void testReadRecordListNotFound() {
		spiderInstanceFactorySpy.MRV.setAlwaysThrowException("factorRecordListReader",
				RecordNotFoundException.withMessage("SomeException"));

		response = recordEndpoint.readRecordListJson(AUTH_TOKEN, AUTH_TOKEN, "place_NOT_FOUND",
				"someFilter");

		assertResponseStatusIs(Response.Status.NOT_FOUND);
		assertEquals(response.getEntity(),
				"Error reading records with recordType: place_NOT_FOUND. SomeException");
	}

	@Test
	public void testReadRecordListUnauthorized() {
		spiderInstanceFactorySpy.MRV.setAlwaysThrowException("factorRecordListReader",
				new AuthorizationException("SomeException"));

		response = recordEndpoint.readRecordListJson(DUMMY_NON_AUTHORIZED_TOKEN, AUTH_TOKEN, PLACE,
				"someException");

		assertResponseStatusIs(Response.Status.FORBIDDEN);
	}

	@Test
	public void testReadRecordListNoTokenAndUnauthorized() {
		spiderInstanceFactorySpy.MRV.setAlwaysThrowException("factorRecordListReader",
				new AuthenticationException("SomeException"));

		response = recordEndpoint.readRecordListJson(null, null, PLACE, "someFilter");
		assertResponseStatusIs(Response.Status.UNAUTHORIZED);
	}

	private void assertResponseContentTypeIs(String expectedContentType) {
		assertEquals(response.getHeaders().getFirst(HttpHeaders.CONTENT_TYPE), expectedContentType);
	}

	private void assertDataFromSpiderConvertedToJsonUsingConvertersFromProvider(
			Convertible convertible) {
		DataToJsonConverterFactorySpy converterFactory = (DataToJsonConverterFactorySpy) converterFactoryCreatorSpy.MCR
				.getReturnValue("createFactory", 0);

		se.uu.ub.cora.data.converter.ExternalUrls externalUrls = (se.uu.ub.cora.data.converter.ExternalUrls) converterFactory.MCR
				.getParameterForMethodAndCallNumberAndParameter(
						"factorUsingConvertibleAndExternalUrls", 0, "externalUrls");
		converterFactory.MCR.assertParameters("factorUsingConvertibleAndExternalUrls", 0,
				convertible, externalUrls);

		DataToJsonConverterSpy converterSpy = (DataToJsonConverterSpy) converterFactory.MCR
				.getReturnValue("factorUsingConvertibleAndExternalUrls", 0);

		var entity = response.getEntity();
		converterSpy.MCR.assertReturn("toJsonCompactFormat", 0, entity);
	}

	private void assertXmlConvertionOfResponse(Convertible convertible) {
		ExternallyConvertibleToStringConverterSpy dataToXmlConverter = (ExternallyConvertibleToStringConverterSpy) converterFactorySpy.MCR
				.getReturnValue("factorExternallyConvertableToStringConverter", 0);

		ExternalUrls externalUrls = (ExternalUrls) dataToXmlConverter.MCR
				.getParameterForMethodAndCallNumberAndParameter("convertWithLinks", 0,
						"externalUrls");
		dataToXmlConverter.MCR.assertParameters("convertWithLinks", 0, convertible, externalUrls);

		var entity = response.getEntity();
		dataToXmlConverter.MCR.assertReturn("convertWithLinks", 0, entity);
	}

	private Object assertParametersAndGetConvertedXmlDataElement() {
		StringToExternallyConvertibleConverterSpy xmlToDataConverter = (StringToExternallyConvertibleConverterSpy) converterFactorySpy.MCR
				.getReturnValue("factorStringToExternallyConvertableConverter", 0);
		xmlToDataConverter.MCR.assertParameters("convert", 0, defaultXml);
		return xmlToDataConverter.MCR.getReturnValue("convert", 0);
	}

	private void assertJsonStringConvertedToGroupUsesCoraData(String jsonSentToEndPoint,
			DataGroup recordSentOnToSpider) {
		assertSame(jsonParser.jsonString, jsonSentToEndPoint);
		assertSame(jsonToDataConverterFactorySpy.jsonValue, jsonParser.returnedJsonValue);
		JsonToDataConverterSpy jsonToDataConverterSpy = jsonToDataConverterFactorySpy.jsonToDataConverterSpy;
		DataGroup returnedDataPart = jsonToDataConverterSpy.dataPartToReturn;

		assertSame(recordSentOnToSpider, returnedDataPart);
	}

}