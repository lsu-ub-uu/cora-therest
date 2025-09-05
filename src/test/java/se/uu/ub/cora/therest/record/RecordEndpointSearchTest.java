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
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import se.uu.ub.cora.converter.ConverterProvider;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataList;
import se.uu.ub.cora.data.converter.JsonToDataConverterProvider;
import se.uu.ub.cora.json.parser.org.OrgJsonParser;
import se.uu.ub.cora.logger.LoggerProvider;
import se.uu.ub.cora.logger.spies.LoggerFactorySpy;
import se.uu.ub.cora.spider.dependency.SpiderInstanceProvider;
import se.uu.ub.cora.spider.spies.RecordSearcherSpy;
import se.uu.ub.cora.spider.spies.SpiderInstanceFactorySpy;
import se.uu.ub.cora.therest.AnnotationTestHelper;
import se.uu.ub.cora.therest.dependency.TheRestInstanceFactorySpy;
import se.uu.ub.cora.therest.dependency.TheRestInstanceProvider;
import se.uu.ub.cora.therest.spy.EndpointConverterSpy;
import se.uu.ub.cora.therest.spy.ErrorHandlerSpy;
import se.uu.ub.cora.therest.url.APIUrls;
import se.uu.ub.cora.therest.url.HttpServletRequestSpy;
import se.uu.ub.cora.therest.url.UrlHandlerSpy;

public class RecordEndpointSearchTest {
	private static final String APPLICATION_VND_CORA_RECORD_LIST_XML = "application/vnd.cora.recordList+xml";
	private static final String APPLICATION_VND_CORA_RECORD_LIST_JSON = "application/vnd.cora.recordList+json";
	private static final String APPLICATION_VND_CORA_RECORD_LIST_JSON_QS09 = "application/vnd.cora.recordList+json;qs=0.9";
	private static final String AUTH_TOKEN = "authToken";
	private JsonParserSpy jsonParser;

	private RecordEndpointSearch recordEndpoint;
	private SpiderInstanceFactorySpy spiderInstanceFactorySpy;
	private Response response;
	private HttpServletRequestSpy requestSpy;
	private LoggerFactorySpy loggerFactorySpy;

	private String defaultJson = "{\"name\":\"someRecordType\",\"children\":[]}";
	private String defaultXml = "<someXml></someXml>";
	private ConverterFactorySpy converterFactorySpy;
	private JsonToDataConverterFactorySpy jsonToDataConverterFactorySpy = new JsonToDataConverterFactorySpy();
	private StringToExternallyConvertibleConverterSpy stringToExternallyConvertibleConverterSpy;
	private TheRestInstanceFactorySpy instanceFactory;

	@BeforeMethod
	public void beforeMethod() {
		spiderInstanceFactorySpy = new SpiderInstanceFactorySpy();
		SpiderInstanceProvider.setSpiderInstanceFactory(spiderInstanceFactorySpy);

		instanceFactory = new TheRestInstanceFactorySpy();
		TheRestInstanceProvider.onlyForTestSetTheRestInstanceFactory(instanceFactory);

		loggerFactorySpy = new LoggerFactorySpy();
		LoggerProvider.setLoggerFactory(loggerFactorySpy);
		setupUrlHandler();

		stringToExternallyConvertibleConverterSpy = new StringToExternallyConvertibleConverterSpy();
		converterFactorySpy = new ConverterFactorySpy();
		converterFactorySpy.MRV.setDefaultReturnValuesSupplier(
				"factorStringToExternallyConvertableConverter",
				() -> stringToExternallyConvertibleConverterSpy);
		ConverterProvider.setConverterFactory("xml", converterFactorySpy);

		jsonToDataConverterFactorySpy = new JsonToDataConverterFactorySpy();
		JsonToDataConverterProvider.setJsonToDataConverterFactory(jsonToDataConverterFactorySpy);

		requestSpy = new HttpServletRequestSpy();
		recordEndpoint = new RecordEndpointSearch(requestSpy);

		setUpSpiesInRecordEndpoint();
	}

	@AfterMethod
	public void afterMethod() {
		TheRestInstanceProvider.onlyForTestResetTheRestInstanceFactory();
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
		recordEndpoint = new RecordEndpointSearch(requestSpy);
		assertTrue(recordEndpoint.getJsonParser() instanceof OrgJsonParser);
	}

	@Test
	public void testClassAnnotation() throws Exception {
		AnnotationTestHelper annotationHelper = AnnotationTestHelper
				.createAnnotationTestHelperForClass(RecordEndpointUpdate.class);

		annotationHelper.assertPathAnnotationForClass("/");
	}

	@Test
	public void testAnnotationsForSearchRecordJson() throws Exception {
		AnnotationTestHelper annotationHelper = AnnotationTestHelper
				.createAnnotationTestHelperForClassMethodNameAndNumOfParameters(
						recordEndpoint.getClass(), "searchRecordJson", 4);

		annotationHelper.assertHttpMethodAndPathAnnotation("GET", "searchResult/{searchId}");
		annotationHelper.assertProducesAnnotation(APPLICATION_VND_CORA_RECORD_LIST_JSON_QS09);
		annotationHelper.assertAnnotationForAuthTokenParameters();
		annotationHelper.assertPathParamAnnotationByNameAndPosition("searchId", 2);
		annotationHelper.assertQueryParamAnnotationByNameAndPosition("searchData", 3);
	}

	@Test
	public void testAnnotationsForSearchRecordXml() throws Exception {
		AnnotationTestHelper annotationHelper = AnnotationTestHelper
				.createAnnotationTestHelperForClassMethodNameAndNumOfParameters(
						recordEndpoint.getClass(), "searchRecordXml", 4);

		annotationHelper.assertHttpMethodAndPathAnnotation("GET", "searchResult/{searchId}");
		annotationHelper.assertProducesAnnotation(APPLICATION_VND_CORA_RECORD_LIST_XML);
		annotationHelper.assertAnnotationForAuthTokenParameters();
		annotationHelper.assertPathParamAnnotationByNameAndPosition("searchId", 2);
		annotationHelper.assertQueryParamAnnotationByNameAndPosition("searchData", 3);
	}

	@Test
	public void testSearchRecordRightTokenSentAsHeaderParam() {
		response = recordEndpoint.searchRecordJson(AUTH_TOKEN, null, "aSearchId", defaultJson);
		assertAuthTokenSentToRecordSearch(AUTH_TOKEN);
	}

	@Test
	public void testSearchRecordRightTokenSentAsQueryParam() {
		response = recordEndpoint.searchRecordJson(null, AUTH_TOKEN, "aSearchId", defaultJson);
		assertAuthTokenSentToRecordSearch(AUTH_TOKEN);
	}

	@Test
	public void testSearchRecordRightTokenSentAsBothHeaderAndQueryParam() {
		response = recordEndpoint.searchRecordJson(AUTH_TOKEN, "otherAuthToken", "aSearchId",
				defaultJson);
		assertAuthTokenSentToRecordSearch(AUTH_TOKEN);
	}

	private void assertAuthTokenSentToRecordSearch(String authToken) {
		RecordSearcherSpy recordSearcherSpy = (RecordSearcherSpy) spiderInstanceFactorySpy.MCR
				.getReturnValue("factorRecordSearcher", 0);
		recordSearcherSpy.MCR.assertParameter("search", 0, "authToken", authToken);
	}

	@Test
	public void testSearchRecordForJsonInJsonOut() {
		response = recordEndpoint.searchRecordJson(AUTH_TOKEN, AUTH_TOKEN, "aSearchId",
				defaultJson);

		DataGroup searchData = assertConversionOfAndReturnSearchDataAsElementForJSON();
		assertComponentsCalledCorrectlyForContentType(searchData,
				APPLICATION_VND_CORA_RECORD_LIST_JSON, response);
	}

	@Test
	public void testSearchRecordForJsonInXmlOut() {
		response = recordEndpoint.searchRecordXml(AUTH_TOKEN, AUTH_TOKEN, "aSearchId", defaultJson);

		DataGroup searchData = assertConversionOfAndReturnSearchDataAsElementForJSON();
		assertComponentsCalledCorrectlyForContentType(searchData,
				APPLICATION_VND_CORA_RECORD_LIST_XML, response);
	}

	@Test
	public void testSearchRecordForXmlInXmlOut() {
		response = recordEndpoint.searchRecordXml(AUTH_TOKEN, AUTH_TOKEN, "aSearchId", defaultXml);

		DataGroup searchData = assertConversionOfAndReturnSearchDataAsElementForXML();
		assertComponentsCalledCorrectlyForContentType(searchData,
				APPLICATION_VND_CORA_RECORD_LIST_XML, response);
	}

	@Test
	public void testSearchRecordForXmlInJsonOut() {
		response = recordEndpoint.searchRecordJson(AUTH_TOKEN, AUTH_TOKEN, "aSearchId", defaultXml);

		DataGroup searchData = assertConversionOfAndReturnSearchDataAsElementForXML();
		assertComponentsCalledCorrectlyForContentType(searchData,
				APPLICATION_VND_CORA_RECORD_LIST_JSON, response);
	}

	private DataGroup assertConversionOfAndReturnSearchDataAsElementForJSON() {
		assertSame(jsonParser.jsonString, defaultJson);
		assertSame(jsonToDataConverterFactorySpy.jsonValue, jsonParser.returnedJsonValue);
		JsonToDataConverterSpy jsonToDataConverterSpy = jsonToDataConverterFactorySpy.jsonToDataConverterSpy;
		return jsonToDataConverterSpy.dataPartToReturn;
	}

	private DataGroup assertConversionOfAndReturnSearchDataAsElementForXML() {
		var xmlToDataConverter = (StringToExternallyConvertibleConverterSpy) converterFactorySpy.MCR
				.getReturnValue("factorStringToExternallyConvertableConverter", 0);

		return (DataGroup) xmlToDataConverter.MCR.assertCalledParametersReturn("convert",
				defaultXml);
	}

	private void assertComponentsCalledCorrectlyForContentType(DataGroup searchData,
			String contentType, Response response) {
		DataList dataRecordList = assertSpiderSearcherCalledAndReturnResult(searchData);
		APIUrls apiUrls = assertUrlsCalculatedFromRequestAndReturn();
		String result = assertConverterCalledAndReturn(contentType, dataRecordList, apiUrls);
		assertResponse(contentType, response, result);
	}

	private DataList assertSpiderSearcherCalledAndReturnResult(DataGroup searchData) {
		var recordSearcherSpy = (RecordSearcherSpy) spiderInstanceFactorySpy.MCR
				.getReturnValue("factorRecordSearcher", 0);
		return (DataList) recordSearcherSpy.MCR.assertCalledParametersReturn("search", AUTH_TOKEN,
				"aSearchId", searchData);
	}

	private APIUrls assertUrlsCalculatedFromRequestAndReturn() {
		var urlHandler = (UrlHandlerSpy) instanceFactory.MCR.getReturnValue("factorUrlHandler", 0);
		return (APIUrls) urlHandler.MCR.assertCalledParametersReturn("getAPIUrls", requestSpy);
	}

	private String assertConverterCalledAndReturn(String contentType, DataList dataRecordList,
			APIUrls apiUrls) {
		var endpointConverter = (EndpointConverterSpy) instanceFactory.MCR
				.getReturnValue("factorEndpointConverter", 0);
		endpointConverter.MCR.assertParameters("convertConvertibleToString", 0, apiUrls,
				contentType, dataRecordList);
		return (String) endpointConverter.MCR.getReturnValue("convertConvertibleToString", 0);
	}

	private void assertResponse(String contentType, Response response, String result) {
		assertEquals(response.getStatusInfo(), Response.Status.OK);
		assertEquals(response.getHeaderString(HttpHeaders.CONTENT_TYPE), contentType);
		assertEquals(response.getEntity(), result);
	}

	@Test
	public void testReadAndDecorateRecord_goesWrong() {
		RuntimeException thrownError = new RuntimeException();
		spiderInstanceFactorySpy.MRV.setAlwaysThrowException("factorRecordSearcher", thrownError);

		response = recordEndpoint.searchRecordJson(AUTH_TOKEN, AUTH_TOKEN, "aSearchId",
				defaultJson);

		ErrorHandlerSpy errorHandler = (ErrorHandlerSpy) instanceFactory.MCR
				.getReturnValue("factorErrorHandler", 0);

		errorHandler.MCR.assertParameters("handleError", 0, AUTH_TOKEN, thrownError,
				"Error searching record with searchId: aSearchId.");
		errorHandler.MCR.assertReturn("handleError", 0, response);
	}

}