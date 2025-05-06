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

import java.io.InputStream;
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
import se.uu.ub.cora.data.DataProvider;
import se.uu.ub.cora.data.DataRecord;
import se.uu.ub.cora.data.converter.DataToJsonConverterProvider;
import se.uu.ub.cora.data.converter.JsonToDataConverterProvider;
import se.uu.ub.cora.data.spies.DataFactorySpy;
import se.uu.ub.cora.initialize.SettingsProvider;
import se.uu.ub.cora.logger.LoggerProvider;
import se.uu.ub.cora.logger.spies.LoggerFactorySpy;
import se.uu.ub.cora.spider.authorization.AuthorizationException;
import se.uu.ub.cora.spider.binary.ArchiveDataIntergrityException;
import se.uu.ub.cora.spider.data.DataMissingException;
import se.uu.ub.cora.spider.dependency.SpiderInstanceProvider;
import se.uu.ub.cora.spider.record.MisuseException;
import se.uu.ub.cora.spider.record.RecordNotFoundException;
import se.uu.ub.cora.spider.spies.SpiderInstanceFactorySpy;
import se.uu.ub.cora.spider.spies.UploaderSpy;
import se.uu.ub.cora.therest.AnnotationTestHelper;
import se.uu.ub.cora.therest.spy.InputStreamSpy;

public class RecordEndpointUploadTest {
	private static final String APPLICATION_VND_CORA_RECORD_XML = "application/vnd.cora.record+xml";
	private static final String APPLICATION_VND_CORA_RECORD_JSON = "application/vnd.cora.record+json";
	private static final String APPLICATION_VND_CORA_RECORD_JSON_QS09 = "application/vnd.cora.record+json;qs=0.9";
	private static final String MULTIPART_FORM_DATA = "multipart/form-data";
	private static final String TEXT_PLAIN = "text/plain; charset=utf-8";
	private static final String DUMMY_NON_AUTHORIZED_TOKEN = "dummyNonAuthorizedToken";
	private static final String AUTH_TOKEN = "authToken";
	private static final String SOME_RESOURCE_TYPE = "someResourceType";
	private static final String SOME_ID = "someId";
	private static final String SOME_TYPE = "someType";

	private JsonToDataConverterFactorySpy jsonToDataConverterFactorySpy = new JsonToDataConverterFactorySpy();

	private RecordEndpointUpload recordEndpoint;
	private OldSpiderInstanceFactorySpy spiderInstanceFactorySpy;
	private Response response;
	private HttpServletRequestSpy requestSpy;
	private LoggerFactorySpy loggerFactorySpy;
	private DataFactorySpy dataFactorySpy;

	private DataToJsonConverterFactoryCreatorSpy converterFactoryCreatorSpy;
	private ConverterFactorySpy converterFactorySpy;
	private String standardBaseUrlHttp = "http://cora.epc.ub.uu.se/systemone/rest/record/";
	private String standardIffUrlHttp = "http://cora.epc.ub.uu.se/systemone/iiif/";
	private InputStreamSpy inputStreamSpy;
	private UploaderSpy uploaderSpy;
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

		inputStreamSpy = new InputStreamSpy();
		uploaderSpy = new UploaderSpy();

		Map<String, String> settings = new HashMap<>();
		settings.put("theRestPublicPathToSystem", "/systemone/rest/");
		settings.put("iiifPublicPathToSystem", "/systemone/iiif/");
		SettingsProvider.setSettings(settings);

		requestSpy = new HttpServletRequestSpy();
		recordEndpoint = new RecordEndpointUpload(requestSpy);
	}

	@Test
	public void testInit() {
		recordEndpoint = new RecordEndpointUpload(requestSpy);
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

	@Test
	public void testPreferredTokenForUpload_1() {
		expectTokenForUploadToPreferablyBeHeaderThanQuery(AUTH_TOKEN, "authToken2", AUTH_TOKEN);
	}

	@Test
	public void testPreferredTokenForUpload_2() {
		expectTokenForUploadToPreferablyBeHeaderThanQuery(null, AUTH_TOKEN, AUTH_TOKEN);
	}

	@Test
	public void testPreferredTokenForUpload_3() {
		expectTokenForUploadToPreferablyBeHeaderThanQuery(AUTH_TOKEN, null, AUTH_TOKEN);
	}

	@Test
	public void testPreferredTokenForUpload_4() {
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
	public void testUploadFileForJson() {
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
		assertResponseContentTypeIs(APPLICATION_VND_CORA_RECORD_XML);
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
		annotationHelper.assertProducesAnnotation(APPLICATION_VND_CORA_RECORD_JSON_QS09);
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
		annotationHelper.assertProducesAnnotation(APPLICATION_VND_CORA_RECORD_XML);
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
				APPLICATION_VND_CORA_RECORD_JSON, DUMMY_NON_AUTHORIZED_TOKEN, SOME_TYPE, SOME_ID,
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
	public void testUploadHandleArchiveDataIntegrityException() {
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

	private void setUpSpiderInstanceProvider(String method, Object supplier) {
		SpiderInstanceFactorySpy spiderInstanceFactorySpy = new SpiderInstanceFactorySpy();
		spiderInstanceFactorySpy.MRV.setDefaultReturnValuesSupplier(method, () -> supplier);
		SpiderInstanceProvider.setSpiderInstanceFactory(spiderInstanceFactorySpy);
	}

}