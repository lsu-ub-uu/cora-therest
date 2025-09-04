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

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import se.uu.ub.cora.converter.ConverterProvider;
import se.uu.ub.cora.converter.ExternalUrls;
import se.uu.ub.cora.data.Convertible;
import se.uu.ub.cora.data.DataList;
import se.uu.ub.cora.data.DataProvider;
import se.uu.ub.cora.data.converter.DataToJsonConverterProvider;
import se.uu.ub.cora.data.converter.JsonToDataConverterProvider;
import se.uu.ub.cora.data.spies.DataFactorySpy;
import se.uu.ub.cora.logger.LoggerProvider;
import se.uu.ub.cora.logger.spies.LoggerFactorySpy;
import se.uu.ub.cora.spider.dependency.SpiderInstanceProvider;
import se.uu.ub.cora.therest.AnnotationTestHelper;
import se.uu.ub.cora.therest.dependency.TheRestInstanceFactorySpy;
import se.uu.ub.cora.therest.dependency.TheRestInstanceProvider;
import se.uu.ub.cora.therest.url.UrlHandlerSpy;

public class RecordEndpointReadIncomingLinksTest {
	private static final String APPLICATION_XML = "application/xml";
	private static final String APPLICATION_XML_QS01 = "application/xml;qs=0.1";
	private static final String APPLICATION_VND_CORA_RECORD_LIST_XML = "application/vnd.cora.recordList+xml";
	private static final String APPLICATION_VND_CORA_RECORD_LIST_JSON_QS09 = "application/vnd.cora.recordList+json;qs=0.9";
	private static final String DUMMY_NON_AUTHORIZED_TOKEN = "dummyNonAuthorizedToken";
	private static final String PLACE_0001 = "place:0001";
	private static final String PLACE = "place";
	private static final String AUTH_TOKEN = "authToken";

	private JsonToDataConverterFactorySpy jsonToDataConverterFactorySpy = new JsonToDataConverterFactorySpy();

	private RecordEndpointReadIncomingLinks recordEndpoint;
	private OldSpiderInstanceFactorySpy spiderInstanceFactorySpy;
	private Response response;
	private HttpServletRequestOldSpy requestSpy;
	private LoggerFactorySpy loggerFactorySpy;
	private DataFactorySpy dataFactorySpy;

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
		recordEndpoint = new RecordEndpointReadIncomingLinks(requestSpy);
	}

	private void setupUrlHandler() {
		instanceFactory = new TheRestInstanceFactorySpy();
		TheRestInstanceProvider.onlyForTestSetTheRestInstanceFactory(instanceFactory);
	}

	@Test
	public void testInit() {
		recordEndpoint = new RecordEndpointReadIncomingLinks(requestSpy);
	}

	@Test
	public void testClassAnnotation() throws Exception {
		AnnotationTestHelper annotationHelper = AnnotationTestHelper
				.createAnnotationTestHelperForClass(RecordEndpointUpdate.class);

		annotationHelper.assertPathAnnotationForClass("/");
	}

	@Test
	public void testUrlsHandledByUrlHandler() {
		response = recordEndpoint.readIncomingRecordLinksJson(AUTH_TOKEN, AUTH_TOKEN, PLACE,
				PLACE_0001);
		UrlHandlerSpy urlHandler = (UrlHandlerSpy) instanceFactory.MCR
				.getReturnValue("factorUrlHandler", 0);

		var restUrl = urlHandler.MCR.assertCalledParametersReturn("getRestUrl", requestSpy);
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

	@Test
	public void testPreferredTokenForReadIncomingLinks() {
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
		assertResponseContentTypeIs(APPLICATION_VND_CORA_RECORD_LIST_XML);
	}

	@Test
	public void testReadIncomingRecordLinksAsApplicationXmlForBrowsers() {
		response = recordEndpoint.readIncomingRecordLinksAsApplicationXmlForBrowsers(AUTH_TOKEN,
				AUTH_TOKEN, PLACE, PLACE_0001);
		DataList dataList = (DataList) spiderInstanceFactorySpy.spiderRecordIncomingLinksReaderSpy.MCR
				.getReturnValue("readIncomingLinks", 0);

		assertXmlConvertionOfResponse(dataList);
		assertEntityExists();
		assertResponseStatusIs(Response.Status.OK);
		assertResponseContentTypeIs(APPLICATION_XML);
	}

	@Test
	public void testAnnotationsForReadIncomingRecordLinksJson() throws Exception {
		AnnotationTestHelper annotationHelper = AnnotationTestHelper
				.createAnnotationTestHelperForClassMethodNameAndNumOfParameters(
						recordEndpoint.getClass(), "readIncomingRecordLinksJson", 4);

		annotationHelper.assertHttpMethodAndPathAnnotation("GET", "{type}/{id}/incomingLinks");
		annotationHelper.assertProducesAnnotation(APPLICATION_VND_CORA_RECORD_LIST_JSON_QS09);
		annotationHelper.assertAnnotationForAuthTokensAndTypeAndIdParameters();
	}

	@Test
	public void testAnnotationsForReadIncomingRecordLinksXml() throws Exception {
		AnnotationTestHelper annotationHelper = AnnotationTestHelper
				.createAnnotationTestHelperForClassMethodNameAndNumOfParameters(
						recordEndpoint.getClass(), "readIncomingRecordLinksXml", 4);

		annotationHelper.assertHttpMethodAndPathAnnotation("GET", "{type}/{id}/incomingLinks");
		annotationHelper.assertProducesAnnotation(APPLICATION_VND_CORA_RECORD_LIST_XML);
		annotationHelper.assertAnnotationForAuthTokensAndTypeAndIdParameters();
	}

	@Test
	public void testAnnotationsForReadIncomingRecordLinksAsApplicationXmlForBrowsers()
			throws Exception {
		AnnotationTestHelper annotationHelper = AnnotationTestHelper
				.createAnnotationTestHelperForClassMethodNameAndNumOfParameters(
						recordEndpoint.getClass(),
						"readIncomingRecordLinksAsApplicationXmlForBrowsers", 4);

		annotationHelper.assertHttpMethodAndPathAnnotation("GET", "{type}/{id}/incomingLinks");
		annotationHelper.assertProducesAnnotation(APPLICATION_XML_QS01);
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

}