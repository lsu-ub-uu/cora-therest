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
import se.uu.ub.cora.data.DataList;
import se.uu.ub.cora.data.DataProvider;
import se.uu.ub.cora.data.converter.DataToJsonConverterProvider;
import se.uu.ub.cora.data.converter.JsonToDataConverterProvider;
import se.uu.ub.cora.data.spies.DataFactorySpy;
import se.uu.ub.cora.initialize.SettingsProvider;
import se.uu.ub.cora.json.parser.org.OrgJsonParser;
import se.uu.ub.cora.logger.LoggerProvider;
import se.uu.ub.cora.logger.spies.LoggerFactorySpy;
import se.uu.ub.cora.spider.dependency.SpiderInstanceProvider;
import se.uu.ub.cora.therest.AnnotationTestHelper;

public class RecordEndpointSearchTest {
	private static final String APPLICATION_VND_UUB_RECORD_LIST_XML = "application/vnd.cora.recordList+xml";
	private static final String APPLICATION_VND_UUB_RECORD_LIST_JSON = "application/vnd.cora.recordList+json";
	private static final String APPLICATION_VND_UUB_RECORD_LIST_JSON_QS09 = "application/vnd.cora.recordList+json;qs=0.9";
	private static final String DUMMY_NON_AUTHORIZED_TOKEN = "dummyNonAuthorizedToken";
	private static final String AUTH_TOKEN = "authToken";
	private JsonParserSpy jsonParser;

	private JsonToDataConverterFactorySpy jsonToDataConverterFactorySpy = new JsonToDataConverterFactorySpy();

	private RecordEndpointSearch recordEndpoint;
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
		recordEndpoint = new RecordEndpointSearch(requestSpy);

		setUpSpiesInRecordEndpoint();
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

	private Object assertParametersAndGetConvertedXmlDataElement() {
		StringToExternallyConvertibleConverterSpy xmlToDataConverter = (StringToExternallyConvertibleConverterSpy) converterFactorySpy.MCR
				.getReturnValue("factorStringToExternallyConvertableConverter", 0);
		xmlToDataConverter.MCR.assertParameters("convert", 0, defaultXml);
		var dataElement = xmlToDataConverter.MCR.getReturnValue("convert", 0);
		return dataElement;
	}

	@Test
	public void testSearchRecordInputsReachSpider() {
		response = recordEndpoint.searchRecordJson(AUTH_TOKEN, AUTH_TOKEN, "aSearchId",
				defaultJson);

		SpiderRecordSearcherSpy spiderRecordSearcherSpy = spiderInstanceFactorySpy.spiderRecordSearcherSpy;
		assertEquals(spiderRecordSearcherSpy.authToken, AUTH_TOKEN);
		assertEquals(spiderRecordSearcherSpy.searchId, "aSearchId");

		DataGroup searchSentOnToSpider = spiderInstanceFactorySpy.spiderRecordSearcherSpy.searchData;
		assertJsonStringConvertedToGroupUsesCoraData(defaultJson, searchSentOnToSpider);
	}

	@Test
	public void testSearchRecordUsesFactoriesCorrectly() {
		response = recordEndpoint.searchRecordJson(AUTH_TOKEN, AUTH_TOKEN, "aSearchId",
				defaultJson);

		SpiderRecordSearcherSpy spiderRecordSearcherSpy = spiderInstanceFactorySpy.spiderRecordSearcherSpy;

		DataGroup searchSentOnToSpider = spiderInstanceFactorySpy.spiderRecordSearcherSpy.searchData;
		assertJsonStringConvertedToGroupUsesCoraData(defaultJson, searchSentOnToSpider);

		DataList returnedDataListFromReader = spiderRecordSearcherSpy.searchResult;

		assertDataFromSpiderConvertedToJsonUsingConvertersFromProvider(returnedDataListFromReader);
	}

	private void assertJsonStringConvertedToGroupUsesCoraData(String jsonSentToEndPoint,
			DataGroup recordSentOnToSpider) {
		assertSame(jsonParser.jsonString, jsonSentToEndPoint);
		assertSame(jsonToDataConverterFactorySpy.jsonValue, jsonParser.returnedJsonValue);
		JsonToDataConverterSpy jsonToDataConverterSpy = jsonToDataConverterFactorySpy.jsonToDataConverterSpy;
		DataGroup returnedDataPart = jsonToDataConverterSpy.dataPartToReturn;

		assertSame(recordSentOnToSpider, returnedDataPart);
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

		var dataElement = assertParametersAndGetConvertedXmlDataElement();

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

		var dataElement = assertParametersAndGetConvertedXmlDataElement();

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
		AnnotationTestHelper annotationHelper = AnnotationTestHelper
				.createAnnotationTestHelperForClassMethodNameAndNumOfParameters(
						recordEndpoint.getClass(), "searchRecordJson", 4);

		annotationHelper.assertHttpMethodAndPathAnnotation("GET", "searchResult/{searchId}");
		annotationHelper.assertProducesAnnotation(APPLICATION_VND_UUB_RECORD_LIST_JSON_QS09);
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
		annotationHelper.assertProducesAnnotation(APPLICATION_VND_UUB_RECORD_LIST_XML);
		annotationHelper.assertAnnotationForAuthTokenParameters();
		annotationHelper.assertPathParamAnnotationByNameAndPosition("searchId", 2);
		annotationHelper.assertQueryParamAnnotationByNameAndPosition("searchData", 3);
	}

	@Test
	public void testSearchRecordSearchIdNotFound() {
		response = recordEndpoint.searchRecordJson(AUTH_TOKEN, AUTH_TOKEN, "aSearchId_NOT_FOUND",
				defaultJson);
		assertResponseStatusIs(Response.Status.NOT_FOUND);
		assertEquals(response.getEntity(),
				"Error searching record with searchId: aSearchId_NOT_FOUND. Record does not exist");
	}

	@Test
	public void testSearchRecordInvalidSearchData() {
		response = recordEndpoint.searchRecordJson(AUTH_TOKEN, AUTH_TOKEN, "aSearchId_INVALID_DATA",
				defaultJson);
		assertResponseStatusIs(Response.Status.BAD_REQUEST);
		assertEquals(response.getEntity(),
				"Error searching record with searchId: aSearchId_INVALID_DATA. SearchData is invalid");
	}

	@Test
	public void testSearchRecordUnauthorized() {
		response = recordEndpoint.searchRecordJson(DUMMY_NON_AUTHORIZED_TOKEN,
				DUMMY_NON_AUTHORIZED_TOKEN, "aSearchId", defaultJson);
		assertResponseStatusIs(Response.Status.FORBIDDEN);
		assertEquals(response.getEntity(), null);
	}

	@Test
	public void testSearchRecordUnauthenticated() {
		response = recordEndpoint.searchRecordJson("nonExistingToken", "nonExistingToken",
				"aSearchId", defaultJson);
		assertResponseStatusIs(Response.Status.UNAUTHORIZED);
		assertEquals(response.getEntity(), null);
	}

}