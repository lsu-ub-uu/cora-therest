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

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import se.uu.ub.cora.converter.ConverterException;
import se.uu.ub.cora.converter.ConverterProvider;
import se.uu.ub.cora.converter.ExternalUrls;
import se.uu.ub.cora.data.Convertible;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataProvider;
import se.uu.ub.cora.data.DataRecord;
import se.uu.ub.cora.data.converter.DataToJsonConverterProvider;
import se.uu.ub.cora.data.converter.JsonToDataConverterProvider;
import se.uu.ub.cora.data.spies.DataFactorySpy;
import se.uu.ub.cora.data.spies.DataRecordSpy;
import se.uu.ub.cora.json.parser.org.OrgJsonParser;
import se.uu.ub.cora.logger.LoggerProvider;
import se.uu.ub.cora.logger.spies.LoggerFactorySpy;
import se.uu.ub.cora.spider.dependency.SpiderInstanceProvider;
import se.uu.ub.cora.therest.AnnotationTestHelper;
import se.uu.ub.cora.therest.dependency.TheRestInstanceFactorySpy;
import se.uu.ub.cora.therest.dependency.TheRestInstanceProvider;
import se.uu.ub.cora.therest.url.UrlHandlerSpy;

public class RecordEndpointBatchIndexTest {
	private static final String APPLICATION_VND_CORA_RECORD_XML = "application/vnd.cora.record+xml";
	private static final String APPLICATION_VND_CORA_RECORD_JSON = "application/vnd.cora.record+json";
	private static final String APPLICATION_VND_CORA_RECORD_JSON_QS09 = "application/vnd.cora.record+json;qs=0.9";
	private static final String TEXT_PLAIN = "text/plain; charset=utf-8";
	private static final String DUMMY_NON_AUTHORIZED_TOKEN = "dummyNonAuthorizedToken";
	private static final String PLACE = "place";
	private static final String AUTH_TOKEN = "authToken";
	private JsonParserSpy jsonParser;

	private JsonToDataConverterFactorySpy jsonToDataConverterFactorySpy = new JsonToDataConverterFactorySpy();

	private RecordEndpointBatchIndex recordEndpoint;
	private OldSpiderInstanceFactorySpy spiderInstanceFactorySpy;
	private Response response;
	private HttpServletRequestOldSpy requestSpy;
	private LoggerFactorySpy loggerFactorySpy;
	private DataFactorySpy dataFactorySpy;

	private String defaultXml = "<someXml></someXml>";
	private String jsonFilterData = "{\"name\":\"filter\",\"children\":[{\"name\":\"part\",\"children\":[{\"name\":\"key\",\"value\":\"movieTitle\"},{\"name\":\"value\",\"value\":\"Some title\"}],\"repeatId\":\"0\"}]}";
	private String jsonIndexData = "{\\\"name\\\":\\\"indexSettings\\\",\\\"children\\\":[{\"name\":\"filter\",\"children\":[{\"name\":\"part\",\"children\":[{\"name\":\"key\",\"value\":\"movieTitle\"},{\"name\":\"value\",\"value\":\"Some title\"}],\"repeatId\":\"0\"}]}]}";
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
		spiderInstanceFactorySpy = new OldSpiderInstanceFactorySpy();
		SpiderInstanceProvider.setSpiderInstanceFactory(spiderInstanceFactorySpy);

		requestSpy = new HttpServletRequestOldSpy();
		recordEndpoint = new RecordEndpointBatchIndex(requestSpy);

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
		recordEndpoint = new RecordEndpointBatchIndex(requestSpy);
		assertTrue(recordEndpoint.getJsonParser() instanceof OrgJsonParser);
	}

	@Test
	public void testClassAnnotation() throws Exception {
		AnnotationTestHelper annotationHelper = AnnotationTestHelper
				.createAnnotationTestHelperForClass(RecordEndpointUpdate.class);

		annotationHelper.assertPathAnnotationForClass("/");
	}

	@Test
	public void testUrlsHandledByUrlHandler() {
		response = recordEndpoint.batchIndexJsonJson(AUTH_TOKEN, AUTH_TOKEN, PLACE, jsonIndexData);

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
		var dataElement = xmlToDataConverter.MCR.getReturnValue("convert", 0);
		return dataElement;
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
	public void testPreferredTokenForBatchIndex() {
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
		assertJsonStringConvertedToGroupUsesCoraData(jsonIndexData, filterSentOnToSpider);

		assertEquals(indexBatchJobCreator.type, PLACE);

		DataRecordSpy recordToReturn = spiderInstanceFactorySpy.spiderRecordListIndexerSpy.recordToReturn;
		assertDataFromSpiderConvertedToJsonUsingConvertersFromProvider(recordToReturn);

		assertEntityExists();
		assertResponseStatusIs(Response.Status.CREATED);
		assertEquals(response.getLocation().toString(), "indexBatchJob/someCreatedBatchJobId");
	}

	@Test
	public void testIndexRecordListWithNullAsFilter() {
		response = recordEndpoint.batchIndexJsonJson(AUTH_TOKEN, AUTH_TOKEN, PLACE, null);
		assertEntityExists();
		assertResponseStatusIs(Response.Status.CREATED);
		assertEquals(response.getLocation().toString(), "indexBatchJob/someCreatedBatchJobId");

		assertEquals(jsonParser.jsonString, "{\"name\":\"indexSettings\",\"children\":[]}");
	}

	@Test
	public void testIndexRecordListWithEmptyFilter() {
		response = recordEndpoint.batchIndexJsonJson(AUTH_TOKEN, AUTH_TOKEN, PLACE, "");
		assertEntityExists();
		assertResponseStatusIs(Response.Status.CREATED);
		assertEquals(response.getLocation().toString(), "indexBatchJob/someCreatedBatchJobId");

		assertEquals(jsonParser.jsonString, "{\"name\":\"indexSettings\",\"children\":[]}");
	}

	@Test
	public void testIndexRecordListWithFilterAsXmlAndResponseXml() {
		response = recordEndpoint.batchIndexXmlXml(AUTH_TOKEN, AUTH_TOKEN, PLACE, defaultXml);

		var filterElement = assertParametersAndGetConvertedXmlDataElement();

		spiderInstanceFactorySpy.spiderRecordListIndexerSpy.MCR.assertParameters("indexRecordList",
				0, AUTH_TOKEN, "place", filterElement);

		DataRecord dataList = (DataRecord) spiderInstanceFactorySpy.spiderRecordListIndexerSpy.MCR
				.getReturnValue("indexRecordList", 0);

		assertXmlConvertionOfResponse(dataList);
		assertEntityExists();
		assertResponseStatusIs(Response.Status.CREATED);
		assertResponseContentTypeIs(APPLICATION_VND_CORA_RECORD_XML);
	}

	@Test
	public void testIndexRecordListWithFilterAsXmlndResponseJson() {
		response = recordEndpoint.batchIndexXmlJson(AUTH_TOKEN, AUTH_TOKEN, PLACE, defaultXml);

		var filterElement = assertParametersAndGetConvertedXmlDataElement();

		spiderInstanceFactorySpy.spiderRecordListIndexerSpy.MCR.assertParameters("indexRecordList",
				0, AUTH_TOKEN, "place", filterElement);

		DataRecord dataList = (DataRecord) spiderInstanceFactorySpy.spiderRecordListIndexerSpy.MCR
				.getReturnValue("indexRecordList", 0);

		assertDataFromSpiderConvertedToJsonUsingConvertersFromProvider(dataList);
		assertEntityExists();
		assertResponseStatusIs(Response.Status.CREATED);
		assertResponseContentTypeIs(APPLICATION_VND_CORA_RECORD_JSON);
	}

	@Test
	public void testIndexRecordListWithFilterAsJsonAndResponseXml() {
		response = recordEndpoint.batchIndexJsonXml(AUTH_TOKEN, AUTH_TOKEN, PLACE, jsonIndexData);

		DataGroup filterSentOnToSpider = spiderInstanceFactorySpy.spiderRecordListIndexerSpy.filter;
		assertJsonStringConvertedToGroupUsesCoraData(jsonIndexData, filterSentOnToSpider);

		spiderInstanceFactorySpy.spiderRecordListIndexerSpy.MCR.assertParameters("indexRecordList",
				0, AUTH_TOKEN, "place", filterSentOnToSpider);

		DataRecord dataList = (DataRecord) spiderInstanceFactorySpy.spiderRecordListIndexerSpy.MCR
				.getReturnValue("indexRecordList", 0);

		assertXmlConvertionOfResponse(dataList);
		assertEntityExists();
		assertResponseStatusIs(Response.Status.CREATED);
		assertResponseContentTypeIs(APPLICATION_VND_CORA_RECORD_XML);
	}

	@Test
	public void testIndexRecordListWithJsonAnWrongContentTypeAndResponseXml() {
		stringToExternallyConvertibleConverterSpy.MRV.setAlwaysThrowException("convert",
				new ConverterException("exception from spy"));

		response = recordEndpoint.batchIndexXmlXml(AUTH_TOKEN, AUTH_TOKEN, PLACE, jsonIndexData);

		StringToExternallyConvertibleConverterSpy xmlToDataConverter = (StringToExternallyConvertibleConverterSpy) converterFactorySpy.MCR
				.getReturnValue("factorStringToExternallyConvertableConverter", 0);

		xmlToDataConverter.MCR.assertParameters("convert", 0, jsonIndexData);

		assertEquals(response.getEntity(),
				"Error indexing records with recordType: place. exception from spy");
		assertResponseStatusIs(Response.Status.BAD_REQUEST);
	}

	@Test
	public void testIndexRecordListWithNullFilterAsXmlAndResponseXml() {
		response = recordEndpoint.batchIndexXmlXml(AUTH_TOKEN, AUTH_TOKEN, PLACE, null);

		DataGroup filterSentOnToSpider = spiderInstanceFactorySpy.spiderRecordListIndexerSpy.filter;
		assertJsonStringConvertedToGroupUsesCoraData("{\"name\":\"indexSettings\",\"children\":[]}",
				filterSentOnToSpider);

		spiderInstanceFactorySpy.spiderRecordListIndexerSpy.MCR.assertParameters("indexRecordList",
				0, AUTH_TOKEN, "place", filterSentOnToSpider);

		DataRecord dataList = (DataRecord) spiderInstanceFactorySpy.spiderRecordListIndexerSpy.MCR
				.getReturnValue("indexRecordList", 0);

		assertXmlConvertionOfResponse(dataList);
		assertEntityExists();
		assertResponseStatusIs(Response.Status.CREATED);
		assertResponseContentTypeIs(APPLICATION_VND_CORA_RECORD_XML);
	}

	@Test
	public void testAnnotationsForIndexRecordListJsonJson() throws Exception {
		AnnotationTestHelper annotationHelper = AnnotationTestHelper
				.createAnnotationTestHelperForClassMethodNameAndNumOfParameters(
						recordEndpoint.getClass(), "batchIndexJsonJson", 4);

		annotationHelper.assertHttpMethodAndPathAnnotation("POST", "index/{type}");
		annotationHelper.assertConsumesAnnotation(APPLICATION_VND_CORA_RECORD_JSON);
		annotationHelper.assertProducesAnnotation(APPLICATION_VND_CORA_RECORD_JSON_QS09);
		annotationHelper.assertAnnotationForAuthTokensAndTypeParameters();
	}

	@Test
	public void testAnnotationsForIndexRecordListJsonXml() throws Exception {
		AnnotationTestHelper annotationHelper = AnnotationTestHelper
				.createAnnotationTestHelperForClassMethodNameAndNumOfParameters(
						recordEndpoint.getClass(), "batchIndexJsonXml", 4);

		annotationHelper.assertHttpMethodAndPathAnnotation("POST", "index/{type}");
		annotationHelper.assertConsumesAnnotation(APPLICATION_VND_CORA_RECORD_JSON);
		annotationHelper.assertProducesAnnotation(APPLICATION_VND_CORA_RECORD_XML);
		annotationHelper.assertAnnotationForAuthTokensAndTypeParameters();
	}

	@Test
	public void testAnnotationsForIndexRecordListXmlJson() throws Exception {
		AnnotationTestHelper annotationHelper = AnnotationTestHelper
				.createAnnotationTestHelperForClassMethodNameAndNumOfParameters(
						recordEndpoint.getClass(), "batchIndexXmlJson", 4);

		annotationHelper.assertHttpMethodAndPathAnnotation("POST", "index/{type}");
		annotationHelper.assertConsumesAnnotation(APPLICATION_VND_CORA_RECORD_XML);
		annotationHelper.assertProducesAnnotation(APPLICATION_VND_CORA_RECORD_JSON_QS09);
		annotationHelper.assertAnnotationForAuthTokensAndTypeParameters();
	}

	@Test
	public void testAnnotationsForIndexRecordListXmlXml() throws Exception {
		AnnotationTestHelper annotationHelper = AnnotationTestHelper
				.createAnnotationTestHelperForClassMethodNameAndNumOfParameters(
						recordEndpoint.getClass(), "batchIndexXmlXml", 4);

		annotationHelper.assertHttpMethodAndPathAnnotation("POST", "index/{type}");
		annotationHelper.assertConsumesAnnotation(APPLICATION_VND_CORA_RECORD_XML);
		annotationHelper.assertProducesAnnotation(APPLICATION_VND_CORA_RECORD_XML);
		annotationHelper.assertAnnotationForAuthTokensAndTypeParameters();
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