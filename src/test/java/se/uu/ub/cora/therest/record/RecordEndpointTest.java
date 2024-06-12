/*
 * Copyright 2015, 2016, 2018, 2021, 2022, 2024 Uppsala University Library
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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import se.uu.ub.cora.converter.ConverterException;
import se.uu.ub.cora.converter.ConverterProvider;
import se.uu.ub.cora.converter.ExternalUrls;
import se.uu.ub.cora.data.Convertible;
import se.uu.ub.cora.data.DataChild;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataList;
import se.uu.ub.cora.data.DataRecord;
import se.uu.ub.cora.data.converter.DataToJsonConverterProvider;
import se.uu.ub.cora.data.converter.JsonToDataConverterProvider;
import se.uu.ub.cora.data.spies.DataGroupSpy;
import se.uu.ub.cora.initialize.SettingsProvider;
import se.uu.ub.cora.json.parser.JsonValue;
import se.uu.ub.cora.json.parser.org.OrgJsonParser;
import se.uu.ub.cora.logger.LoggerProvider;
import se.uu.ub.cora.logger.spies.LoggerFactorySpy;
import se.uu.ub.cora.logger.spies.LoggerSpy;
import se.uu.ub.cora.spider.authorization.AuthorizationException;
import se.uu.ub.cora.spider.binary.ArchiveDataIntergrityException;
import se.uu.ub.cora.spider.binary.ResourceInputStream;
import se.uu.ub.cora.spider.data.DataMissingException;
import se.uu.ub.cora.spider.dependency.SpiderInstanceProvider;
import se.uu.ub.cora.spider.record.MisuseException;
import se.uu.ub.cora.spider.record.RecordNotFoundException;
import se.uu.ub.cora.spider.record.ResourceNotFoundException;
import se.uu.ub.cora.spider.spies.DownloaderSpy;
import se.uu.ub.cora.spider.spies.SpiderInstanceFactorySpy;
import se.uu.ub.cora.spider.spies.UploaderSpy;
import se.uu.ub.cora.therest.AnnotationTestHelper;
import se.uu.ub.cora.therest.coradata.DataListSpy;
import se.uu.ub.cora.therest.coradata.DataRecordSpy;
import se.uu.ub.cora.therest.spy.InputStreamSpy;

public class RecordEndpointTest {
	private static final String APPLICATION_VND_UUB_WORKORDER_XML_QS_0_9 = "application/vnd.uub.workorder+xml;qs=0.9";
	private static final String APPLICATION_VND_UUB_WORKORDER_JSON = "application/vnd.uub.workorder+json";
	private static final String MULTIPART_FORM_DATA = "multipart/form-data";
	private static final String TEXT_PLAIN = "text/plain; charset=utf-8";
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
	private static final String SOME_RESOURCE_TYPE = "someResourceType";
	private static final String SOME_ID = "someId";
	private static final String SOME_TYPE = "someType";
	private JsonParserSpy jsonParser;

	private JsonToDataConverterFactorySpy jsonToDataConverterFactorySpy = new JsonToDataConverterFactorySpy();

	private RecordEndpoint recordEndpoint;
	private OldSpiderInstanceFactorySpy spiderInstanceFactorySpy;
	private Response response;
	private HttpServletRequestSpy requestSpy;
	private LoggerFactorySpy loggerFactorySpy;

	private String jsonToValidate = "{\"order\":{\"name\":\"validationOrder\",\"children\":[{\"name\":\"recordInfo\",\"children\":[{\"name\":\"dataDivider\",\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"system\"},{\"name\":\"linkedRecordId\",\"value\":\"testSystem\"}]}]},{\"name\":\"recordType\",\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"recordType\"},{\"name\":\"linkedRecordId\",\"value\":\"someRecordType\"}]},{\"name\":\"metadataToValidate\",\"value\":\"existing\"},{\"name\":\"validateLinks\",\"value\":\"false\"}]},\"record\":{\"name\":\"text\",\"children\":[{\"name\":\"recordInfo\",\"children\":[{\"name\":\"id\",\"value\":\"workOrderRecordIdTextVar2Text\"},{\"name\":\"dataDivider\",\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"system\"},{\"name\":\"linkedRecordId\",\"value\":\"cora\"}]}]},{\"name\":\"textPart\",\"children\":[{\"name\":\"text\",\"value\":\"Id på länkad post\"}],\"attributes\":{\"type\":\"default\",\"lang\":\"sv\"}},{\"name\":\"textPart\",\"children\":[{\"name\":\"text\",\"value\":\"Linked record id\"}],\"attributes\":{\"type\":\"alternative\",\"lang\":\"en\"}}]}}";
	private String defaultJson = "{\"name\":\"someRecordType\",\"children\":[]}";
	private String defaultXml = "<someXml></someXml>";
	private String jsonFilterData = "{\"name\":\"filter\",\"children\":[{\"name\":\"part\",\"children\":[{\"name\":\"key\",\"value\":\"movieTitle\"},{\"name\":\"value\",\"value\":\"Some title\"}],\"repeatId\":\"0\"}]}";
	private String jsonIndexData = "{\\\"name\\\":\\\"indexSettings\\\",\\\"children\\\":[{\"name\":\"filter\",\"children\":[{\"name\":\"part\",\"children\":[{\"name\":\"key\",\"value\":\"movieTitle\"},{\"name\":\"value\",\"value\":\"Some title\"}],\"repeatId\":\"0\"}]}]}";
	private DataToJsonConverterFactoryCreatorSpy converterFactoryCreatorSpy;
	private ConverterFactorySpy converterFactorySpy;
	private String standardBaseUrlHttp = "http://cora.epc.ub.uu.se/systemone/rest/record/";
	private String standardBaseUrlHttps = "https://cora.epc.ub.uu.se/systemone/rest/record/";
	private String standardIffUrlHttp = "http://cora.epc.ub.uu.se/systemone/iiif/";
	private String standardIffUrlHttps = "https://cora.epc.ub.uu.se/systemone/iiif/";
	private InputStreamSpy inputStreamSpy;
	private DownloaderSpy downloaderSpy;
	private UploaderSpy uploaderSpy;
	private StringToExternallyConvertibleConverterSpy stringToExternallyConvertibleConverterSpy;

	@BeforeMethod
	public void beforeMethod() {
		loggerFactorySpy = new LoggerFactorySpy();
		LoggerProvider.setLoggerFactory(loggerFactorySpy);
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

		inputStreamSpy = new InputStreamSpy();
		downloaderSpy = new DownloaderSpy();
		uploaderSpy = new UploaderSpy();

		Map<String, String> settings = new HashMap<>();
		settings.put("theRestPublicPathToSystem", "/systemone/rest/");
		settings.put("iiifPublicPathToSystem", "/systemone/iiif/");
		SettingsProvider.setSettings(settings);

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
	public void testClassAnnotation() throws Exception {
		AnnotationTestHelper annotationHelper = AnnotationTestHelper
				.createAnnotationTestHelperForClass(RecordEndpoint.class);

		annotationHelper.assertPathAnnotationForClass("/");
	}

	@Test
	public void testXForwardedProtoHttps() {
		requestSpy.headers.put("X-Forwarded-Proto", "https");
		recordEndpoint = new RecordEndpoint(requestSpy);

		response = recordEndpoint.readRecordJson(AUTH_TOKEN, AUTH_TOKEN, PLACE, PLACE_0001);
		DataToJsonConverterFactorySpy converterFactory = (DataToJsonConverterFactorySpy) converterFactoryCreatorSpy.MCR
				.getReturnValue("createFactory", 0);

		assertEquals(getBaseUrlsFromFactorUsingConvertibleAndExternalUrls(converterFactory),
				standardBaseUrlHttps);
		assertEquals(getIiifUrlFromFactorUsingConvertibleAndExternalUrls(converterFactory),
				standardIffUrlHttps);
	}

	private String getBaseUrlsFromFactorUsingConvertibleAndExternalUrls(
			DataToJsonConverterFactorySpy converterFactory) {
		se.uu.ub.cora.data.converter.ExternalUrls externalUrls = (se.uu.ub.cora.data.converter.ExternalUrls) converterFactory.MCR
				.getValueForMethodNameAndCallNumberAndParameterName(
						"factorUsingConvertibleAndExternalUrls", 0, "externalUrls");
		return externalUrls.getBaseUrl();
	}

	private String getIiifUrlFromFactorUsingConvertibleAndExternalUrls(
			DataToJsonConverterFactorySpy converterFactory) {
		se.uu.ub.cora.data.converter.ExternalUrls externalUrls = (se.uu.ub.cora.data.converter.ExternalUrls) converterFactory.MCR
				.getValueForMethodNameAndCallNumberAndParameterName(
						"factorUsingConvertibleAndExternalUrls", 0, "externalUrls");
		return externalUrls.getIfffUrl();
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

		assertEquals(getBaseUrlsFromFactorUsingConvertibleAndExternalUrls(converterFactory),
				standardBaseUrlHttps);
		assertEquals(getIiifUrlFromFactorUsingConvertibleAndExternalUrls(converterFactory),
				standardIffUrlHttps);
	}

	@Test
	public void testXForwardedProtoEmpty() {
		requestSpy.headers.put("X-Forwarded-Proto", "");
		recordEndpoint = new RecordEndpoint(requestSpy);

		response = recordEndpoint.readRecordJson(AUTH_TOKEN, AUTH_TOKEN, PLACE, PLACE_0001);
		DataToJsonConverterFactorySpy converterFactory = (DataToJsonConverterFactorySpy) converterFactoryCreatorSpy.MCR
				.getReturnValue("createFactory", 0);

		assertEquals(getBaseUrlsFromFactorUsingConvertibleAndExternalUrls(converterFactory),
				standardBaseUrlHttp);
	}

	@Test
	public void testPreferredTokenForReadList() throws IOException {
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
		AnnotationTestHelper annotationHelper = AnnotationTestHelper
				.createAnnotationTestHelperForClassMethodNameAndNumOfParameters(
						recordEndpoint.getClass(), "readRecordListJson", 4);

		annotationHelper.assertHttpMethodAndPathAnnotation("GET", "{type}/");
		annotationHelper.assertProducesAnnotation(APPLICATION_VND_UUB_RECORD_LIST_JSON);
		annotationHelper.assertAnnotationForAuthTokensAndTypeParameters();
		annotationHelper.assertQueryParamAnnotationByNameAndPosition("filter", 3);
	}

	@Test
	public void testAnnotationsForReadRecordListXml() throws Exception {
		AnnotationTestHelper annotationHelper = AnnotationTestHelper
				.createAnnotationTestHelperForClassMethodNameAndNumOfParameters(
						recordEndpoint.getClass(), "readRecordListXml", 4);

		annotationHelper.assertHttpMethodAndPathAnnotation("GET", "{type}/");
		annotationHelper.assertProducesAnnotation(APPLICATION_VND_UUB_RECORD_LIST_XML_QS09);
		annotationHelper.assertAnnotationForAuthTokensAndTypeParameters();
		annotationHelper.assertQueryParamAnnotationByNameAndPosition("filter", 3);
	}

	@Test
	public void testReadRecordListNotFound() {
		String jsonFilter = "{\"name\":\"filter\",\"children\":[]}";
		response = recordEndpoint.readRecordListJson(AUTH_TOKEN, AUTH_TOKEN, "place_NOT_FOUND",
				jsonFilter);
		assertResponseStatusIs(Response.Status.NOT_FOUND);
		assertEquals(response.getEntity(),
				"Error reading records with recordType: place_NOT_FOUND. Record not found");
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
		AnnotationTestHelper annotationHelper = AnnotationTestHelper
				.createAnnotationTestHelperForClassMethodNameAndNumOfParameters(
						recordEndpoint.getClass(), "readRecordJson", 4);

		annotationHelper.assertHttpMethodAndPathAnnotation("GET", "{type}/{id}");
		annotationHelper.assertProducesAnnotation(APPLICATION_VND_UUB_RECORD_JSON);
		annotationHelper.assertAnnotationForAuthTokensAndTypeAndIdParameters();
	}

	@Test
	public void testAnnotationsForReadXml() throws Exception {
		AnnotationTestHelper annotationHelper = AnnotationTestHelper
				.createAnnotationTestHelperForClassMethodNameAndNumOfParameters(
						recordEndpoint.getClass(), "readRecordXml", 4);

		annotationHelper.assertHttpMethodAndPathAnnotation("GET", "{type}/{id}");
		annotationHelper.assertProducesAnnotation(APPLICATION_VND_UUB_RECORD_XML_QS09);
		annotationHelper.assertAnnotationForAuthTokensAndTypeAndIdParameters();
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

		converterFactory.MCR.assertParameters("factorUsingConvertibleAndExternalUrls", 0,
				convertible);
		se.uu.ub.cora.data.converter.ExternalUrls externalUrls = (se.uu.ub.cora.data.converter.ExternalUrls) converterFactory.MCR
				.getValueForMethodNameAndCallNumberAndParameterName(
						"factorUsingConvertibleAndExternalUrls", 0, "externalUrls");
		assertEquals(externalUrls.getBaseUrl(), standardBaseUrlHttp);
		// assertEquals(externalUrls.getIfffUrl(), iiifUrl);
		assertEquals(externalUrls.getIfffUrl(), standardIffUrlHttp);

		DataToJsonConverterSpy converterSpy = (DataToJsonConverterSpy) converterFactory.MCR
				.getReturnValue("factorUsingConvertibleAndExternalUrls", 0);

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

		dataToXmlConverter.MCR.assertParameters("convertWithLinks", 0, convertible);
		ExternalUrls externalUrls = (ExternalUrls) dataToXmlConverter.MCR
				.getValueForMethodNameAndCallNumberAndParameterName("convertWithLinks", 0,
						"externalUrls");
		assertEquals(externalUrls.getBaseUrl(), standardBaseUrlHttp);
		// assertEquals(externalUrls.getIfffUrl(), iiifUrl);
		assertEquals(externalUrls.getIfffUrl(), standardIffUrlHttp);

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
		assertEquals(response.getEntity(), "Error reading record with recordType: " + PLACE
				+ " and recordId: place:0001_NOT_FOUND. No record exist with id place:0001_NOT_FOUND");
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
		AnnotationTestHelper annotationHelper = AnnotationTestHelper
				.createAnnotationTestHelperForClassMethodNameAndNumOfParameters(
						recordEndpoint.getClass(), "readIncomingRecordLinksJson", 4);

		annotationHelper.assertHttpMethodAndPathAnnotation("GET", "{type}/{id}/incomingLinks");
		annotationHelper.assertProducesAnnotation(APPLICATION_VND_UUB_RECORD_LIST_JSON);
		annotationHelper.assertAnnotationForAuthTokensAndTypeAndIdParameters();
	}

	@Test
	public void testAnnotationsForReadIncomingRecordLinksXml() throws Exception {
		AnnotationTestHelper annotationHelper = AnnotationTestHelper
				.createAnnotationTestHelperForClassMethodNameAndNumOfParameters(
						recordEndpoint.getClass(), "readIncomingRecordLinksXml", 4);

		annotationHelper.assertHttpMethodAndPathAnnotation("GET", "{type}/{id}/incomingLinks");
		annotationHelper.assertProducesAnnotation(APPLICATION_VND_UUB_RECORD_LIST_XML_QS09);
		annotationHelper.assertAnnotationForAuthTokensAndTypeAndIdParameters();
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
		AnnotationTestHelper annotationHelper = AnnotationTestHelper
				.createAnnotationTestHelperForClassMethodNameAndNumOfParameters(
						recordEndpoint.getClass(), "deleteRecord", 4);

		annotationHelper.assertHttpMethodAndPathAnnotation("DELETE", "{type}/{id}");
		annotationHelper.assertAnnotationForAuthTokensAndTypeAndIdParameters();
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
		AnnotationTestHelper annotationHelper = AnnotationTestHelper
				.createAnnotationTestHelperForClassMethodNameAndNumOfParameters(
						recordEndpoint.getClass(), "updateRecordJsonJson", 5);

		annotationHelper.assertHttpMethodAndPathAnnotation("POST", "{type}/{id}");
		annotationHelper.assertConsumesAnnotation(APPLICATION_VND_UUB_RECORD_JSON);
		annotationHelper.assertProducesAnnotation(APPLICATION_VND_UUB_RECORD_JSON);
		annotationHelper.assertAnnotationForAuthTokensAndTypeAndIdParameters();
	}

	@Test
	public void testAnnotationsForUpdateRecordJsonXml() throws Exception {
		AnnotationTestHelper annotationHelper = AnnotationTestHelper
				.createAnnotationTestHelperForClassMethodNameAndNumOfParameters(
						recordEndpoint.getClass(), "updateRecordJsonXml", 5);

		annotationHelper.assertHttpMethodAndPathAnnotation("POST", "{type}/{id}");
		annotationHelper.assertConsumesAnnotation(APPLICATION_VND_UUB_RECORD_JSON);
		annotationHelper.assertProducesAnnotation(APPLICATION_VND_UUB_RECORD_XML_QS09);
		annotationHelper.assertAnnotationForAuthTokensAndTypeAndIdParameters();
	}

	@Test
	public void testAnnotationsForUpdateRecordXmlJson() throws Exception {
		AnnotationTestHelper annotationHelper = AnnotationTestHelper
				.createAnnotationTestHelperForClassMethodNameAndNumOfParameters(
						recordEndpoint.getClass(), "updateRecordXmlJson", 5);

		annotationHelper.assertHttpMethodAndPathAnnotation("POST", "{type}/{id}");
		annotationHelper.assertConsumesAnnotation(APPLICATION_VND_UUB_RECORD_XML_QS09);
		annotationHelper.assertProducesAnnotation(APPLICATION_VND_UUB_RECORD_JSON);
		annotationHelper.assertAnnotationForAuthTokensAndTypeAndIdParameters();
	}

	@Test
	public void testAnnotationsForUpdateRecordXmlXml() throws Exception {
		AnnotationTestHelper annotationHelper = AnnotationTestHelper
				.createAnnotationTestHelperForClassMethodNameAndNumOfParameters(
						recordEndpoint.getClass(), "updateRecordXmlXml", 5);

		annotationHelper.assertHttpMethodAndPathAnnotation("POST", "{type}/{id}");
		annotationHelper.assertConsumesAnnotation(APPLICATION_VND_UUB_RECORD_XML_QS09);
		annotationHelper.assertProducesAnnotation(APPLICATION_VND_UUB_RECORD_XML_QS09);
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
		AnnotationTestHelper annotationHelper = AnnotationTestHelper
				.createAnnotationTestHelperForClassMethodNameAndNumOfParameters(
						recordEndpoint.getClass(), "createRecordJsonJson", 4);

		annotationHelper.assertHttpMethodAndPathAnnotation("POST", "{type}");
		annotationHelper.assertConsumesAnnotation(APPLICATION_VND_UUB_RECORD_JSON);
		annotationHelper.assertProducesAnnotation(APPLICATION_VND_UUB_RECORD_JSON);
		annotationHelper.assertAnnotationForAuthTokensAndTypeParameters();
	}

	@Test
	public void testAnnotationsForCreateRecordJsonXml() throws Exception {
		AnnotationTestHelper annotationHelper = AnnotationTestHelper
				.createAnnotationTestHelperForClassMethodNameAndNumOfParameters(
						recordEndpoint.getClass(), "createRecordJsonXml", 4);

		annotationHelper.assertHttpMethodAndPathAnnotation("POST", "{type}");
		annotationHelper.assertConsumesAnnotation(APPLICATION_VND_UUB_RECORD_JSON);
		annotationHelper.assertProducesAnnotation(APPLICATION_VND_UUB_RECORD_XML_QS09);
		annotationHelper.assertAnnotationForAuthTokensAndTypeParameters();
	}

	@Test
	public void testAnnotationsForCreateRecordXmlJson() throws Exception {
		AnnotationTestHelper annotationHelper = AnnotationTestHelper
				.createAnnotationTestHelperForClassMethodNameAndNumOfParameters(
						recordEndpoint.getClass(), "createRecordXmlJson", 4);

		annotationHelper.assertHttpMethodAndPathAnnotation("POST", "{type}");
		annotationHelper.assertConsumesAnnotation(APPLICATION_VND_UUB_RECORD_XML_QS09);
		annotationHelper.assertProducesAnnotation(APPLICATION_VND_UUB_RECORD_JSON);
		annotationHelper.assertAnnotationForAuthTokensAndTypeParameters();
	}

	@Test
	public void testAnnotationsForCreateRecordXmlXml() throws Exception {
		AnnotationTestHelper annotationHelper = AnnotationTestHelper
				.createAnnotationTestHelperForClassMethodNameAndNumOfParameters(
						recordEndpoint.getClass(), "createRecordXmlXml", 4);

		annotationHelper.assertHttpMethodAndPathAnnotation("POST", "{type}");
		annotationHelper.assertConsumesAnnotation(APPLICATION_VND_UUB_RECORD_XML_QS09);
		annotationHelper.assertProducesAnnotation(APPLICATION_VND_UUB_RECORD_XML_QS09);
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
		var caughtException = loggerSpy.MCR.getValueForMethodNameAndCallNumberAndParameterName(
				"logErrorUsingMessageAndException", 0, "exception");
		assertTrue(caughtException instanceof NullPointerException);

	}

	@Test
	public void testPreferredTokenForUpload_1() throws IOException {
		expectTokenForUploadToPreferablyBeHeaderThanQuery(AUTH_TOKEN, "authToken2", AUTH_TOKEN);
	}

	@Test
	public void testPreferredTokenForUpload_2() throws IOException {
		expectTokenForUploadToPreferablyBeHeaderThanQuery(null, AUTH_TOKEN, AUTH_TOKEN);
	}

	@Test
	public void testPreferredTokenForUpload_3() throws IOException {
		expectTokenForUploadToPreferablyBeHeaderThanQuery(AUTH_TOKEN, null, AUTH_TOKEN);
	}

	@Test
	public void testPreferredTokenForUpload_4() throws IOException {
		expectTokenForUploadToPreferablyBeHeaderThanQuery(null, null, null);
	}

	private void expectTokenForUploadToPreferablyBeHeaderThanQuery(String headerAuthToken,
			String queryAuthToken, String authTokenExpected) {
		setUpSpiderInstanceProvider("factorUploader", uploaderSpy);

		response = recordEndpoint.uploadResourceJson(headerAuthToken, queryAuthToken, SOME_TYPE,
				SOME_ID, inputStreamSpy, SOME_RESOURCE_TYPE);

		uploaderSpy.MCR.assertParameter("upload", 0, "authToken", authTokenExpected);
	}

	@Test
	public void testUploadFileForJson() throws ParseException {
		setUpSpiderInstanceProvider("factorUploader", uploaderSpy);

		response = recordEndpoint.uploadResourceJson(AUTH_TOKEN, AUTH_TOKEN, SOME_TYPE, SOME_ID,
				inputStreamSpy, SOME_RESOURCE_TYPE);

		assertResponseStatusIs(Response.Status.OK);
		assertEquals(response.getEntity(), "Fake json compacted format");
	}

	@Test
	public void testUploadFileForXml() {
		setUpSpiderInstanceProvider("factorUploader", uploaderSpy);

		response = recordEndpoint.uploadResourceXml(AUTH_TOKEN, AUTH_TOKEN, SOME_TYPE, SOME_ID,
				inputStreamSpy, SOME_RESOURCE_TYPE);

		DataRecord uploadedFile = (DataRecord) uploaderSpy.MCR.getReturnValue("upload", 0);

		assertXmlConvertionOfResponse(uploadedFile);
		assertEntityExists();
		assertResponseStatusIs(Response.Status.OK);
		assertResponseContentTypeIs(APPLICATION_VND_UUB_RECORD_XML);
		assertEquals(response.getEntity(),
				"fake string from ExternallyConvertibleToStringConverterSpy");
	}

	@Test
	public void testAnnotationsForUploadResourceJson() throws Exception {
		Class<?>[] parameters = { String.class, String.class, String.class, String.class,
				InputStream.class, String.class };
		AnnotationTestHelper annotationHelper = AnnotationTestHelper
				.createAnnotationTestHelperForClassMethodNameAndParameters(
						recordEndpoint.getClass(), "uploadResourceJson", parameters);

		annotationHelper.assertHttpMethodAndPathAnnotation("POST", "{type}/{id}/{resourceType}");
		annotationHelper.assertConsumesAnnotation(MULTIPART_FORM_DATA);
		annotationHelper.assertProducesAnnotation(APPLICATION_VND_UUB_RECORD_JSON);
		annotationHelper.assertAnnotationForAuthTokensAndTypeAndIdParameters();
		annotationHelper.assertFormDataParamAnnotationByNameAndPositionAndType("file", 4);
		annotationHelper.assertPathParamAnnotationByNameAndPosition("resourceType", 5);
	}

	@Test
	public void testAnnotationsForUploadResourceXml() throws Exception {
		Class<?>[] parameters = { String.class, String.class, String.class, String.class,
				InputStream.class, String.class };
		AnnotationTestHelper annotationHelper = AnnotationTestHelper
				.createAnnotationTestHelperForClassMethodNameAndParameters(
						recordEndpoint.getClass(), "uploadResourceXml", parameters);

		annotationHelper.assertHttpMethodAndPathAnnotation("POST", "{type}/{id}/{resourceType}");
		annotationHelper.assertConsumesAnnotation(MULTIPART_FORM_DATA);
		annotationHelper.assertProducesAnnotation(APPLICATION_VND_UUB_RECORD_XML_QS09);
		annotationHelper.assertAnnotationForAuthTokensAndTypeAndIdParameters();
		annotationHelper.assertFormDataParamAnnotationByNameAndPositionAndType("file", 4);
		annotationHelper.assertPathParamAnnotationByNameAndPosition("resourceType", 5);
	}

	@Test
	public void testUploadUnauthorized() {
		uploaderSpy.MRV.setAlwaysThrowException("upload",
				new AuthorizationException("not authorized"));
		setUpSpiderInstanceProvider("factorUploader", uploaderSpy);

		response = recordEndpoint.uploadResourceUsingAuthTokenWithStream(
				APPLICATION_VND_UUB_RECORD_JSON, DUMMY_NON_AUTHORIZED_TOKEN, SOME_TYPE, SOME_ID,
				inputStreamSpy, SOME_RESOURCE_TYPE);

		assertResponseStatusIs(Response.Status.FORBIDDEN);
	}

	@Test
	public void testUploadNotFound() {
		String errorMessage = "No record exists with recordId: " + SOME_ID;
		uploaderSpy.MRV.setAlwaysThrowException("upload",
				RecordNotFoundException.withMessage(errorMessage));
		setUpSpiderInstanceProvider("factorUploader", uploaderSpy);

		response = recordEndpoint.uploadResourceJson(AUTH_TOKEN, AUTH_TOKEN, SOME_TYPE, SOME_ID,
				inputStreamSpy, SOME_RESOURCE_TYPE);

		assertResponseStatusIs(Response.Status.NOT_FOUND);
		assertEquals(response.getEntity(),
				"An error has ocurred while uploading a resource: " + errorMessage);
	}

	@Test
	public void testUploadNotAChildOfBinary() {
		String errorMessage = "It is only possible to upload files to recordTypes that are children of binary";
		uploaderSpy.MRV.setAlwaysThrowException("upload", new MisuseException(errorMessage));
		setUpSpiderInstanceProvider("factorUploader", uploaderSpy);

		response = recordEndpoint.uploadResourceJson(AUTH_TOKEN, AUTH_TOKEN, SOME_TYPE, SOME_ID,
				inputStreamSpy, SOME_RESOURCE_TYPE);

		assertResponseStatusIs(Response.Status.METHOD_NOT_ALLOWED);
		assertResponseContentTypeIs(TEXT_PLAIN);
		assertEquals(response.getEntity(), errorMessage);
	}

	@Test
	public void testUploadStreamMissing() {
		uploaderSpy.MRV.setAlwaysThrowException("upload",
				new DataMissingException("No stream to store"));
		setUpSpiderInstanceProvider("factorUploader", uploaderSpy);

		response = recordEndpoint.uploadResourceJson(AUTH_TOKEN, AUTH_TOKEN, SOME_RESOURCE_TYPE,
				SOME_ID, null, SOME_RESOURCE_TYPE);

		assertResponseStatusIs(Response.Status.BAD_REQUEST);
		assertResponseContentTypeIs(TEXT_PLAIN);
		assertEquals(response.getEntity(),
				"An error has ocurred while uploading a resource: No stream to store");
	}

	@Test
	public void testUploadHandleArchiveDataIntegrityException() throws Exception {
		uploaderSpy.MRV.setAlwaysThrowException("upload",
				ArchiveDataIntergrityException.withMessage("someException"));
		setUpSpiderInstanceProvider("factorUploader", uploaderSpy);

		response = recordEndpoint.uploadResourceJson(AUTH_TOKEN, AUTH_TOKEN, SOME_TYPE, SOME_ID,
				inputStreamSpy, "someRepresentation");
		assertResponseStatusIs(Response.Status.BAD_REQUEST);
		assertResponseContentTypeIs(TEXT_PLAIN);
		assertEquals(response.getEntity(),
				"An error has ocurred while uploading a resource: someException");
	}

	@Test
	public void testPreferredTokenForDownload_1() throws IOException {
		expectTokenForDownloadToPreferablyBeHeaderThanQuery(AUTH_TOKEN, "authToken2", AUTH_TOKEN);
	}

	@Test
	public void testPreferredTokenForDownload_2() throws IOException {
		expectTokenForDownloadToPreferablyBeHeaderThanQuery(null, AUTH_TOKEN, AUTH_TOKEN);
	}

	@Test
	public void testPreferredTokenForDownload_3() throws IOException {
		expectTokenForDownloadToPreferablyBeHeaderThanQuery(AUTH_TOKEN, null, AUTH_TOKEN);
	}

	@Test
	public void testPreferredTokenForDownload_4() throws IOException {
		expectTokenForDownloadToPreferablyBeHeaderThanQuery(null, null, null);
	}

	private void expectTokenForDownloadToPreferablyBeHeaderThanQuery(String headerAuthToken,
			String queryAuthToken, String authTokenExpected) {
		setUpSpiderInstanceProvider("factorDownloader", downloaderSpy);

		response = recordEndpoint.downloadResource(headerAuthToken, queryAuthToken, "someType",
				"someId", "someRepresentation");

		downloaderSpy.MCR.assertParameter("download", 0, "authToken", authTokenExpected);
	}

	@Test
	public void testDownloadUnauthorized() throws IOException {
		downloaderSpy.MRV.setAlwaysThrowException("download",
				new AuthorizationException("not authorized"));
		setUpSpiderInstanceProvider("factorDownloader", downloaderSpy);

		response = recordEndpoint.downloadResourceUsingAuthTokenWithStream(
				DUMMY_NON_AUTHORIZED_TOKEN, SOME_RESOURCE_TYPE, SOME_ID, SOME_RESOURCE_TYPE);
		assertResponseStatusIs(Response.Status.FORBIDDEN);
	}

	@Test
	public void testDownload() throws IOException {
		downloaderSpy.MRV.setDefaultReturnValuesSupplier("download", () -> createDownloadStream());

		setUpSpiderInstanceProvider("factorDownloader", downloaderSpy);

		response = recordEndpoint.downloadResource(AUTH_TOKEN, AUTH_TOKEN, SOME_RESOURCE_TYPE,
				SOME_ID, "master");
		assertDownloadStreamResponse();
		assertResponseStatusIs(Response.Status.OK);
		assertTrue(response.getEntity() instanceof InputStream);
	}

	private ResourceInputStream createDownloadStream() {
		ResourceInputStream downloadStream = ResourceInputStream.withNameSizeInputStream("someFile",
				12, "application/octet-stream",
				new ByteArrayInputStream("a string out".getBytes(StandardCharsets.UTF_8)));
		return downloadStream;
	}

	private void assertDownloadStreamResponse() throws IOException, UnsupportedEncodingException {
		String contentType = response.getHeaderString("Content-Type");
		assertEquals(contentType, "application/octet-stream");

		InputStream downloadStream = (InputStream) response.getEntity();
		assertNotNull(downloadStream);

		String stringFromStream = readStream(downloadStream);
		assertEquals(stringFromStream, "a string out");

		String contentLength = response.getHeaderString("Content-Length");
		assertEquals(contentLength, "12");

		String contentDisposition = response.getHeaderString("Content-Disposition");
		assertEquals(contentDisposition, "attachment; filename=someFile");
	}

	private String readStream(InputStream stream) throws IOException, UnsupportedEncodingException {
		return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
	}

	@Test
	public void testAnnotationsForDownloadResource() throws Exception {
		AnnotationTestHelper annotationHelper = AnnotationTestHelper
				.createAnnotationTestHelperForClassMethodNameAndNumOfParameters(
						recordEndpoint.getClass(), "downloadResource", 5);

		annotationHelper.assertHttpMethodAndPathAnnotation("GET", "{type}/{id}/{resourceType}");
		annotationHelper.assertAnnotationForAuthTokensAndTypeAndIdParameters();
		annotationHelper.assertPathParamAnnotationByNameAndPosition("resourceType", 4);
	}

	@Test
	public void testDownloadWhenResourceNotFound() throws IOException {
		String exceptionMessage = "someException";
		downloaderSpy.MRV.setAlwaysThrowException("download",
				ResourceNotFoundException.withMessage(exceptionMessage));
		setUpSpiderInstanceProvider("factorDownloader", downloaderSpy);

		response = recordEndpoint.downloadResource(AUTH_TOKEN, AUTH_TOKEN, SOME_TYPE, SOME_ID,
				"someRepresentation");
		assertResponseStatusIs(Response.Status.NOT_FOUND);
		assertEquals(response.getEntity(),
				"An error has ocurred while downloading a resource: " + exceptionMessage);

	}

	@Test
	public void testDownloadWhenRecordNotFound() throws IOException {
		downloaderSpy.MRV.setAlwaysThrowException("download",
				RecordNotFoundException.withMessage("someException"));
		setUpSpiderInstanceProvider("factorDownloader", downloaderSpy);

		response = recordEndpoint.downloadResource(AUTH_TOKEN, AUTH_TOKEN, SOME_TYPE, SOME_ID,
				"someRepresentation");
		assertResponseStatusIs(Response.Status.NOT_FOUND);
		assertEquals(response.getEntity(),
				"An error has ocurred while downloading a resource: someException");
	}

	private void setUpSpiderInstanceProvider(String method, Object supplier) {
		SpiderInstanceFactorySpy spiderInstanceFactorySpy = new SpiderInstanceFactorySpy();
		spiderInstanceFactorySpy.MRV.setDefaultReturnValuesSupplier(method, () -> supplier);
		SpiderInstanceProvider.setSpiderInstanceFactory(spiderInstanceFactorySpy);
	}

	@Test
	public void testDownloadNotAChildOfBinary() throws IOException {
		downloaderSpy.MRV.setAlwaysThrowException("download", new MisuseException(
				"It is only possible to download files to recordTypes that are children of binary"));
		setUpSpiderInstanceProvider("factorDownloader", downloaderSpy);

		response = recordEndpoint.downloadResource(AUTH_TOKEN, AUTH_TOKEN, SOME_RESOURCE_TYPE,
				SOME_ID, "master");
		assertResponseStatusIs(Response.Status.METHOD_NOT_ALLOWED);
	}

	@Test
	public void testDownloadBadRequest() throws IOException {
		String errorMessage = "No stream to store";
		downloaderSpy.MRV.setAlwaysThrowException("download",
				new DataMissingException(errorMessage));
		setUpSpiderInstanceProvider("factorDownloader", downloaderSpy);

		response = recordEndpoint.downloadResource(AUTH_TOKEN, AUTH_TOKEN, SOME_TYPE, SOME_ID, "");
		assertResponseStatusIs(Response.Status.BAD_REQUEST);
		assertResponseContentTypeIs(TEXT_PLAIN);
		assertEquals(response.getEntity(),
				"An error has ocurred while downloading a resource: " + errorMessage);
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
		AnnotationTestHelper annotationHelper = AnnotationTestHelper
				.createAnnotationTestHelperForClassMethodNameAndNumOfParameters(
						recordEndpoint.getClass(), "searchRecordJson", 4);

		annotationHelper.assertHttpMethodAndPathAnnotation("GET", "searchResult/{searchId}");
		annotationHelper.assertProducesAnnotation(APPLICATION_VND_UUB_RECORD_LIST_JSON);
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
		annotationHelper.assertProducesAnnotation(APPLICATION_VND_UUB_RECORD_LIST_XML_QS09);
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
	public void testValidateWorkOrderPartMissing() throws Exception {
		DataGroupSpy order = createDataGroupWithChildren();
		DataGroupSpy recordToValidate = new DataGroupSpy();
		DataGroupSpy record = createDataGroupWithChildren(recordToValidate);
		DataGroupSpy workOrder = createWorkOrderWithOrder(order, record);

		workOrder.MRV.setAlwaysThrowException("getFirstGroupWithNameInData",
				new se.uu.ub.cora.data.DataMissingException("someException"));

		response = recordEndpoint.validateRecordXmlXml(AUTH_TOKEN, AUTH_TOKEN, PLACE, defaultXml);

		assertEquals(response.getStatus(), 400);
		assertEquals(response.getEntity(),
				"Validation failed due to: WorkOrder part 'order' not found.");
	}

	@Test
	public void testValidateMultipleRecordToValidateExists() throws Exception {
		DataGroupSpy validationOrder = new DataGroupSpy();
		DataGroupSpy order = createDataGroupWithChildren(validationOrder);
		DataGroupSpy recordToValidate1 = new DataGroupSpy();
		DataGroupSpy recordToValidate2 = new DataGroupSpy();
		DataGroupSpy record = createDataGroupWithChildren(recordToValidate1, recordToValidate2);
		createWorkOrderWithOrder(order, record);

		response = recordEndpoint.validateRecordXmlXml(AUTH_TOKEN, AUTH_TOKEN, PLACE, defaultXml);

		assertEquals(response.getStatus(), 400);
		assertEquals(response.getEntity(),
				"Validation failed due to: Too many children in workOrder part.");
	}

	@Test
	public void testValidateMultipleValidateOrderExists() throws Exception {
		DataGroupSpy validationOrder1 = new DataGroupSpy();
		DataGroupSpy validationOrder2 = new DataGroupSpy();
		DataGroupSpy order = createDataGroupWithChildren(validationOrder1, validationOrder2);
		DataGroupSpy recordToValidate = new DataGroupSpy();
		DataGroupSpy record = createDataGroupWithChildren(recordToValidate);
		createWorkOrderWithOrder(order, record);

		response = recordEndpoint.validateRecordXmlXml(AUTH_TOKEN, AUTH_TOKEN, PLACE, defaultXml);

		assertEquals(response.getStatus(), 400);
		assertEquals(response.getEntity(),
				"Validation failed due to: Too many children in workOrder part.");
	}

	@Test
	public void testValidateRecordForXml() {
		DataGroupSpy validationOrder = new DataGroupSpy();
		DataGroupSpy order = createDataGroupWithChildren(validationOrder);
		DataGroupSpy recordToValidate = new DataGroupSpy();
		DataGroupSpy record = createDataGroupWithChildren(recordToValidate);
		DataGroupSpy workOrder = createWorkOrderWithOrder(order, record);

		response = recordEndpoint.validateRecordXmlXml(AUTH_TOKEN, AUTH_TOKEN, PLACE, defaultXml);

		workOrder.MCR.assertParameters("getFirstGroupWithNameInData", 0, "order");
		workOrder.MCR.assertParameters("getFirstGroupWithNameInData", 1, "record");
		order.MCR.assertParameters("getChildren", 0);
		record.MCR.assertParameters("getChildren", 0);

		spiderInstanceFactorySpy.spiderRecordValidatorSpy.MCR.assertParameters("validateRecord", 0,
				AUTH_TOKEN, "validationOrder", validationOrder, recordToValidate);

		assertValidationResult();
	}

	private DataGroupSpy createWorkOrderWithOrder(DataGroupSpy order, DataGroupSpy record) {
		DataGroupSpy workOrder = new DataGroupSpy();
		workOrder.MRV.setSpecificReturnValuesSupplier("getFirstGroupWithNameInData", () -> order,
				"order");
		workOrder.MRV.setSpecificReturnValuesSupplier("getFirstGroupWithNameInData", () -> record,
				"record");
		stringToExternallyConvertibleConverterSpy.MRV.setDefaultReturnValuesSupplier("convert",
				() -> workOrder);
		return workOrder;
	}

	private DataGroupSpy createDataGroupWithChildren(DataGroupSpy... validationOrders) {
		DataGroupSpy dataGroup = new DataGroupSpy();
		addChildrenToOrder(dataGroup, validationOrders);
		return dataGroup;
	}

	private void addChildrenToOrder(DataGroupSpy order, DataGroupSpy... validationOrders) {
		ArrayList<DataChild> orderChildren = new ArrayList<>();
		for (DataGroup validationOrder : validationOrders) {
			orderChildren.add(validationOrder);
		}
		order.MRV.setDefaultReturnValuesSupplier("getChildren", () -> orderChildren);
	}

	private void assertValidationResult() {
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

		assertValidationResult();
	}

	@Test
	public void testValidateRecordInputAsXMLResponseAsJson() {
		DataGroupSpy validationOrder = new DataGroupSpy();
		DataGroupSpy order = createDataGroupWithChildren(validationOrder);
		DataGroupSpy recordToValidate = new DataGroupSpy();
		DataGroupSpy record = createDataGroupWithChildren(recordToValidate);
		DataGroupSpy workOrder = createWorkOrderWithOrder(order, record);

		response = recordEndpoint.validateRecordXmlJson(AUTH_TOKEN, AUTH_TOKEN, PLACE, defaultXml);

		workOrder.MCR.assertParameters("getFirstGroupWithNameInData", 0, "order");
		workOrder.MCR.assertParameters("getFirstGroupWithNameInData", 1, "record");

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
		stringToExternallyConvertibleConverterSpy.MRV.setAlwaysThrowException("convert",
				new ConverterException("exception from spy"));
		response = recordEndpoint.validateRecordXmlJson(AUTH_TOKEN, AUTH_TOKEN, PLACE, defaultJson);
		assertResponseStatusIs(Response.Status.BAD_REQUEST);
		assertResponseContentTypeIs(TEXT_PLAIN);
	}

	@Test
	public void testAnnotationsForValidateRecordJsonJson() throws Exception {
		AnnotationTestHelper annotationHelper = AnnotationTestHelper
				.createAnnotationTestHelperForClassMethodNameAndNumOfParameters(
						recordEndpoint.getClass(), "validateRecordJsonJson", 4);

		annotationHelper.assertHttpMethodAndPathAnnotation("POST", "{type}");
		annotationHelper.assertConsumesAnnotation(APPLICATION_VND_UUB_WORKORDER_JSON);
		annotationHelper.assertProducesAnnotation(APPLICATION_VND_UUB_RECORD_JSON);
		annotationHelper.assertAnnotationForAuthTokensAndTypeParameters();
	}

	@Test
	public void testAnnotationsForValidateRecordJsonXml() throws Exception {
		AnnotationTestHelper annotationHelper = AnnotationTestHelper
				.createAnnotationTestHelperForClassMethodNameAndNumOfParameters(
						recordEndpoint.getClass(), "validateRecordJsonXml", 4);

		annotationHelper.assertHttpMethodAndPathAnnotation("POST", "{type}");
		annotationHelper.assertConsumesAnnotation(APPLICATION_VND_UUB_WORKORDER_JSON);
		annotationHelper.assertProducesAnnotation(APPLICATION_VND_UUB_RECORD_XML_QS09);
		annotationHelper.assertAnnotationForAuthTokensAndTypeParameters();
	}

	@Test
	public void testAnnotationsForValidateRecordXmlJson() throws Exception {
		AnnotationTestHelper annotationHelper = AnnotationTestHelper
				.createAnnotationTestHelperForClassMethodNameAndNumOfParameters(
						recordEndpoint.getClass(), "validateRecordXmlJson", 4);

		annotationHelper.assertHttpMethodAndPathAnnotation("POST", "{type}");
		annotationHelper.assertConsumesAnnotation(APPLICATION_VND_UUB_WORKORDER_XML_QS_0_9);
		annotationHelper.assertProducesAnnotation(APPLICATION_VND_UUB_RECORD_JSON);
		annotationHelper.assertAnnotationForAuthTokensAndTypeParameters();
	}

	@Test
	public void testAnnotationsForValidateRecordXmlXml() throws Exception {
		AnnotationTestHelper annotationHelper = AnnotationTestHelper
				.createAnnotationTestHelperForClassMethodNameAndNumOfParameters(
						recordEndpoint.getClass(), "validateRecordXmlXml", 4);

		annotationHelper.assertHttpMethodAndPathAnnotation("POST", "{type}");
		annotationHelper.assertConsumesAnnotation(APPLICATION_VND_UUB_WORKORDER_XML_QS_0_9);
		annotationHelper.assertProducesAnnotation(APPLICATION_VND_UUB_RECORD_XML_QS09);
		annotationHelper.assertAnnotationForAuthTokensAndTypeParameters();
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
		AnnotationTestHelper annotationHelper = AnnotationTestHelper
				.createAnnotationTestHelperForClassMethodNameAndNumOfParameters(
						recordEndpoint.getClass(), "batchIndexJsonJson", 4);

		annotationHelper.assertHttpMethodAndPathAnnotation("POST", "index/{type}");
		annotationHelper.assertConsumesAnnotation(APPLICATION_VND_UUB_RECORD_JSON);
		annotationHelper.assertProducesAnnotation(APPLICATION_VND_UUB_RECORD_JSON);
		annotationHelper.assertAnnotationForAuthTokensAndTypeParameters();
	}

	@Test
	public void testAnnotationsForIndexRecordListJsonXml() throws Exception {
		AnnotationTestHelper annotationHelper = AnnotationTestHelper
				.createAnnotationTestHelperForClassMethodNameAndNumOfParameters(
						recordEndpoint.getClass(), "batchIndexJsonXml", 4);

		annotationHelper.assertHttpMethodAndPathAnnotation("POST", "index/{type}");
		annotationHelper.assertConsumesAnnotation(APPLICATION_VND_UUB_RECORD_JSON);
		annotationHelper.assertProducesAnnotation(APPLICATION_VND_UUB_RECORD_XML_QS09);
		annotationHelper.assertAnnotationForAuthTokensAndTypeParameters();
	}

	@Test
	public void testAnnotationsForIndexRecordListXmlJson() throws Exception {
		AnnotationTestHelper annotationHelper = AnnotationTestHelper
				.createAnnotationTestHelperForClassMethodNameAndNumOfParameters(
						recordEndpoint.getClass(), "batchIndexXmlJson", 4);

		annotationHelper.assertHttpMethodAndPathAnnotation("POST", "index/{type}");
		annotationHelper.assertConsumesAnnotation(APPLICATION_VND_UUB_RECORD_XML_QS09);
		annotationHelper.assertProducesAnnotation(APPLICATION_VND_UUB_RECORD_JSON);
		annotationHelper.assertAnnotationForAuthTokensAndTypeParameters();
	}

	@Test
	public void testAnnotationsForIndexRecordListXmlXml() throws Exception {
		AnnotationTestHelper annotationHelper = AnnotationTestHelper
				.createAnnotationTestHelperForClassMethodNameAndNumOfParameters(
						recordEndpoint.getClass(), "batchIndexXmlXml", 4);

		annotationHelper.assertHttpMethodAndPathAnnotation("POST", "index/{type}");
		annotationHelper.assertConsumesAnnotation(APPLICATION_VND_UUB_RECORD_XML_QS09);
		annotationHelper.assertProducesAnnotation(APPLICATION_VND_UUB_RECORD_XML_QS09);
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