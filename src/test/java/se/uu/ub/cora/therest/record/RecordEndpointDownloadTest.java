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
import static org.testng.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import se.uu.ub.cora.converter.ConverterProvider;
import se.uu.ub.cora.data.DataProvider;
import se.uu.ub.cora.data.converter.DataToJsonConverterProvider;
import se.uu.ub.cora.data.converter.JsonToDataConverterProvider;
import se.uu.ub.cora.data.spies.DataFactorySpy;
import se.uu.ub.cora.initialize.SettingsProvider;
import se.uu.ub.cora.logger.LoggerProvider;
import se.uu.ub.cora.logger.spies.LoggerFactorySpy;
import se.uu.ub.cora.spider.authorization.AuthorizationException;
import se.uu.ub.cora.spider.binary.ResourceInputStream;
import se.uu.ub.cora.spider.data.DataMissingException;
import se.uu.ub.cora.spider.dependency.SpiderInstanceProvider;
import se.uu.ub.cora.spider.record.MisuseException;
import se.uu.ub.cora.spider.record.RecordNotFoundException;
import se.uu.ub.cora.spider.record.ResourceNotFoundException;
import se.uu.ub.cora.spider.spies.DownloaderSpy;
import se.uu.ub.cora.spider.spies.SpiderInstanceFactorySpy;
import se.uu.ub.cora.therest.AnnotationTestHelper;

public class RecordEndpointDownloadTest {
	private static final String TEXT_PLAIN = "text/plain; charset=utf-8";
	private static final String DUMMY_NON_AUTHORIZED_TOKEN = "dummyNonAuthorizedToken";
	private static final String AUTH_TOKEN = "authToken";
	private static final String SOME_RESOURCE_TYPE = "someResourceType";
	private static final String SOME_ID = "someId";
	private static final String SOME_TYPE = "someType";

	private JsonToDataConverterFactorySpy jsonToDataConverterFactorySpy = new JsonToDataConverterFactorySpy();

	private RecordEndpointDownload recordEndpoint;
	private OldSpiderInstanceFactorySpy spiderInstanceFactorySpy;
	private Response response;
	private HttpServletRequestSpy requestSpy;
	private LoggerFactorySpy loggerFactorySpy;
	private DataFactorySpy dataFactorySpy;

	private DataToJsonConverterFactoryCreatorSpy converterFactoryCreatorSpy;
	private ConverterFactorySpy converterFactorySpy;
	private DownloaderSpy downloaderSpy;
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

		downloaderSpy = new DownloaderSpy();

		Map<String, String> settings = new HashMap<>();
		settings.put("theRestPublicPathToSystem", "/systemone/rest/");
		settings.put("iiifPublicPathToSystem", "/systemone/iiif/");
		SettingsProvider.setSettings(settings);

		requestSpy = new HttpServletRequestSpy();
		recordEndpoint = new RecordEndpointDownload(requestSpy);
	}

	@Test
	public void testInit() {
		recordEndpoint = new RecordEndpointDownload(requestSpy);
	}

	@Test
	public void testClassAnnotation() throws Exception {
		AnnotationTestHelper annotationHelper = AnnotationTestHelper
				.createAnnotationTestHelperForClass(RecordEndpointUpdate.class);

		annotationHelper.assertPathAnnotationForClass("/");
	}

	private void assertResponseStatusIs(Status responseStatus) {
		assertEquals(response.getStatusInfo(), responseStatus);
	}

	private void assertResponseContentTypeIs(String expectedContentType) {
		assertEquals(response.getHeaders().getFirst(HttpHeaders.CONTENT_TYPE), expectedContentType);
	}

	@Test
	public void testPreferredTokenForDownload_1() {
		expectTokenForDownloadToPreferablyBeHeaderThanQuery(AUTH_TOKEN, "authToken2", AUTH_TOKEN);
	}

	@Test
	public void testPreferredTokenForDownload_2() {
		expectTokenForDownloadToPreferablyBeHeaderThanQuery(null, AUTH_TOKEN, AUTH_TOKEN);
	}

	@Test
	public void testPreferredTokenForDownload_3() {
		expectTokenForDownloadToPreferablyBeHeaderThanQuery(AUTH_TOKEN, null, AUTH_TOKEN);
	}

	@Test
	public void testPreferredTokenForDownload_4() {
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
	public void testDownloadUnauthorized() {
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
	public void testDownloadWhenResourceNotFound() {
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
	public void testDownloadWhenRecordNotFound() {
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
	public void testDownloadNotAChildOfBinary() {
		downloaderSpy.MRV.setAlwaysThrowException("download", new MisuseException(
				"It is only possible to download files to recordTypes that are children of binary"));
		setUpSpiderInstanceProvider("factorDownloader", downloaderSpy);

		response = recordEndpoint.downloadResource(AUTH_TOKEN, AUTH_TOKEN, SOME_RESOURCE_TYPE,
				SOME_ID, "master");
		assertResponseStatusIs(Response.Status.METHOD_NOT_ALLOWED);
	}

	@Test
	public void testDownloadBadRequest() {
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

}