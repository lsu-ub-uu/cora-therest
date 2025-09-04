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
import se.uu.ub.cora.data.DataProvider;
import se.uu.ub.cora.data.DataRecord;
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

public class RecordEndpointReadTest {
	private static final String APPLICATION_XML = "application/xml";
	private static final String APPLICATION_XML_QS01 = "application/xml;qs=0.1";
	private static final String APPLICATION_VND_CORA_RECORD_XML = "application/vnd.cora.record+xml";
	private static final String APPLICATION_VND_CORA_RECORD_JSON = "application/vnd.cora.record+json";
	private static final String APPLICATION_VND_CORA_RECORD_JSON_QS09 = "application/vnd.cora.record+json;qs=0.9";
	private static final String DUMMY_NON_AUTHORIZED_TOKEN = "dummyNonAuthorizedToken";
	private static final String PLACE_0001 = "place:0001";
	private static final String PLACE = "place";
	private static final String AUTH_TOKEN = "authToken";

	private JsonToDataConverterFactorySpy jsonToDataConverterFactorySpy = new JsonToDataConverterFactorySpy();

	private RecordEndpointRead recordEndpoint;
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
		recordEndpoint = new RecordEndpointRead(requestSpy);
	}

	private void setupUrlHandler() {
		instanceFactory = new TheRestInstanceFactorySpy();
		TheRestInstanceProvider.onlyForTestSetTheRestInstanceFactory(instanceFactory);
	}

	@Test
	public void testInit() {
		recordEndpoint = new RecordEndpointRead(requestSpy);
	}

	@Test
	public void testClassAnnotation() throws Exception {
		AnnotationTestHelper annotationHelper = AnnotationTestHelper
				.createAnnotationTestHelperForClass(RecordEndpointRead.class);

		annotationHelper.assertPathAnnotationForClass("/");
	}

	@Test
	public void testUrlsHandledByUrlHandler() {
		recordEndpoint = new RecordEndpointRead(requestSpy);

		response = recordEndpoint.readRecordJson(AUTH_TOKEN, AUTH_TOKEN, PLACE, PLACE_0001);

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

	@Test
	public void testAnnotationsForReadJson() throws Exception {
		AnnotationTestHelper annotationHelper = AnnotationTestHelper
				.createAnnotationTestHelperForClassMethodNameAndNumOfParameters(
						recordEndpoint.getClass(), "readRecordJson", 4);

		annotationHelper.assertHttpMethodAndPathAnnotation("GET", "{type}/{id}");
		annotationHelper.assertProducesAnnotation(APPLICATION_VND_CORA_RECORD_JSON_QS09);
		annotationHelper.assertAnnotationForAuthTokensAndTypeAndIdParameters();
	}

	@Test
	public void testAnnotationsForReadXml() throws Exception {
		AnnotationTestHelper annotationHelper = AnnotationTestHelper
				.createAnnotationTestHelperForClassMethodNameAndNumOfParameters(
						recordEndpoint.getClass(), "readRecordXml", 4);

		annotationHelper.assertHttpMethodAndPathAnnotation("GET", "{type}/{id}");
		annotationHelper.assertProducesAnnotation(APPLICATION_VND_CORA_RECORD_XML);
		annotationHelper.assertAnnotationForAuthTokensAndTypeAndIdParameters();
	}

	@Test
	public void testAnnotationsForReadDefaultApplicationXMLForBrowsers() throws Exception {
		AnnotationTestHelper annotationHelper = AnnotationTestHelper
				.createAnnotationTestHelperForClassMethodNameAndNumOfParameters(
						recordEndpoint.getClass(), "readRecordAsApplicationXmlForBrowsers", 4);

		annotationHelper.assertHttpMethodAndPathAnnotation("GET", "{type}/{id}");
		annotationHelper.assertProducesAnnotation(APPLICATION_XML_QS01);
		annotationHelper.assertAnnotationForAuthTokensAndTypeAndIdParameters();
	}

	@Test
	public void testPreferredTokenForRead() {
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
		assertResponseContentTypeIs(APPLICATION_VND_CORA_RECORD_JSON);
	}

	private void assertResponseContentTypeIs(String expectedContentType) {
		assertEquals(response.getHeaders().getFirst(HttpHeaders.CONTENT_TYPE), expectedContentType);
	}

	@Test
	public void testReadRecordXml() {
		response = recordEndpoint.readRecordXml(AUTH_TOKEN, AUTH_TOKEN, PLACE, PLACE_0001);
		assertEntityExists();
		assertResponseStatusIs(Response.Status.OK);
		assertResponseContentTypeIs(APPLICATION_VND_CORA_RECORD_XML);
	}

	@Test
	public void testReadRecordAsApplicationXmlForBrowsers() {
		response = recordEndpoint.readRecordAsApplicationXmlForBrowsers(AUTH_TOKEN, AUTH_TOKEN,
				PLACE, PLACE_0001);
		assertEntityExists();
		assertResponseStatusIs(Response.Status.OK);
		assertResponseContentTypeIs(APPLICATION_XML);
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

	@Test
	public void testReadRecordUsesXmlConverterForAcceptXml() {
		response = recordEndpoint.readRecordXml(AUTH_TOKEN, AUTH_TOKEN, PLACE, PLACE_0001);

		DataRecord recordReturnedFromReader = spiderInstanceFactorySpy.spiderRecordReaderSpy.dataRecord;
		assertXmlConvertionOfResponse(recordReturnedFromReader);
	}

	@Test
	public void testReadRecordUsesXmlConverterForAcceptXmlForBrowser() {
		response = recordEndpoint.readRecordAsApplicationXmlForBrowsers(AUTH_TOKEN, AUTH_TOKEN,
				PLACE, PLACE_0001);

		DataRecord recordReturnedFromReader = spiderInstanceFactorySpy.spiderRecordReaderSpy.dataRecord;
		assertXmlConvertionOfResponse(recordReturnedFromReader);
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

}