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

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import se.uu.ub.cora.spider.dependency.SpiderInstanceProvider;
import se.uu.ub.cora.spider.spies.DecoratedRecordReaderSpy;
import se.uu.ub.cora.spider.spies.SpiderInstanceFactorySpy;
import se.uu.ub.cora.therest.AnnotationTestHelper;
import se.uu.ub.cora.therest.dependency.TheRestInstanceFactorySpy;
import se.uu.ub.cora.therest.dependency.TheRestInstanceProvider;
import se.uu.ub.cora.therest.spy.EndpointConverterSpy;
import se.uu.ub.cora.therest.spy.ErrorHandlerSpy;
import se.uu.ub.cora.therest.url.APIUrls;
import se.uu.ub.cora.therest.url.HttpServletRequestSpy;
import se.uu.ub.cora.therest.url.UrlHandlerSpy;

public class RecordEndpointReadDecoratedTest {
	private static final String APPLICATION_VND_CORA_RECORD_DECORATED_XML_QS09 = "application/vnd.cora.record-decorated+xml;qs=0.9";
	private static final String APPLICATION_VND_CORA_RECORD_DECORATED_XML = "application/vnd.cora.record-decorated+xml";
	private static final String APPLICATION_VND_CORA_RECORD_DECORATED_JSON_QS09 = "application/vnd.cora.record-decorated+json;qs=0.9";
	private static final String APPLICATION_VND_CORA_RECORD_DECORATED_JSON = "application/vnd.cora.record-decorated+json";

	private SpiderInstanceFactorySpy spiderInstanceFactorySpy;
	private RecordEndpointReadDecorated recordEndpoint;
	private HttpServletRequestSpy requestSpy;
	private TheRestInstanceFactorySpy instanceFactory;

	@BeforeMethod
	public void beforeMethod() {
		spiderInstanceFactorySpy = new SpiderInstanceFactorySpy();
		SpiderInstanceProvider.setSpiderInstanceFactory(spiderInstanceFactorySpy);

		instanceFactory = new TheRestInstanceFactorySpy();
		TheRestInstanceProvider.onlyForTestSetTheRestInstanceFactory(instanceFactory);

		requestSpy = new HttpServletRequestSpy();
		recordEndpoint = new RecordEndpointReadDecorated(requestSpy);
	}

	@AfterMethod
	public void afterMethod() {
		TheRestInstanceProvider.onlyForTestResetTheRestInstanceFactory();
	}

	@Test
	public void testClassAnnotation() throws Exception {
		AnnotationTestHelper annotationHelper = AnnotationTestHelper
				.createAnnotationTestHelperForClass(RecordEndpointReadDecorated.class);

		annotationHelper.assertPathAnnotationForClass("/");
	}

	@Test
	public void testAnnotationsForReadDecoratedXml() throws Exception {
		AnnotationTestHelper annotationHelper = AnnotationTestHelper
				.createAnnotationTestHelperForClassMethodNameAndNumOfParameters(
						recordEndpoint.getClass(), "readDecoratedRecordXml", 3);

		annotationHelper.assertHttpMethodAndPathAnnotation("GET", "{type}/{id}");
		annotationHelper.assertProducesAnnotation(APPLICATION_VND_CORA_RECORD_DECORATED_XML_QS09);
		annotationHelper.assertAnnotationForHeaderAuthToken();
	}

	@Test
	public void testAnnotationsForReadJson() throws Exception {
		AnnotationTestHelper annotationHelper = AnnotationTestHelper
				.createAnnotationTestHelperForClassMethodNameAndNumOfParameters(
						recordEndpoint.getClass(), "readDecoratedRecordJson", 3);

		annotationHelper.assertHttpMethodAndPathAnnotation("GET", "{type}/{id}");
		annotationHelper.assertProducesAnnotation(APPLICATION_VND_CORA_RECORD_DECORATED_JSON_QS09);
		annotationHelper.assertAnnotationForHeaderAuthToken();
	}

	@Test
	public void testReadAndDecorateRecord_goesWrong() {
		RuntimeException thrownError = new RuntimeException();
		spiderInstanceFactorySpy.MRV.setAlwaysThrowException("factorDecoratedRecordReader",
				thrownError);

		Response response = recordEndpoint.readDecoratedRecordXml("someAuthToken", "someType",
				"someId");

		ErrorHandlerSpy errorHandler = (ErrorHandlerSpy) instanceFactory.MCR
				.getReturnValue("factorErrorHandler", 0);

		errorHandler.MCR.assertParameters("handleError", 0, "someAuthToken", thrownError,
				"Error reading decorated record with recordType: someType and "
						+ "recordId: someId.");
		errorHandler.MCR.assertReturn("handleError", 0, response);
	}

	@Test
	public void testReadDecoratedRecordXml() {
		String contentType = APPLICATION_VND_CORA_RECORD_DECORATED_XML;

		Response response = recordEndpoint.readDecoratedRecordXml("someAuthToken", "someType",
				"someId");

		assertComponentsCalledCorrectlyForContentType(contentType, response);
	}

	private void assertComponentsCalledCorrectlyForContentType(String contentType,
			Response response) {
		var decoratedRecordReader = (DecoratedRecordReaderSpy) spiderInstanceFactorySpy.MCR
				.assertCalledParametersReturn("factorDecoratedRecordReader");
		var dataRecord = decoratedRecordReader.MCR.assertCalledParametersReturn(
				"readDecoratedRecord", "someAuthToken", "someType", "someId");

		var endpointConverter = (EndpointConverterSpy) instanceFactory.MCR
				.getReturnValue("factorEndpointConverter", 0);

		var urlHandler = (UrlHandlerSpy) instanceFactory.MCR.getReturnValue("factorUrlHandler", 0);
		APIUrls apiUrls = (APIUrls) urlHandler.MCR.assertCalledParametersReturn("getAPIUrls",
				requestSpy);

		endpointConverter.MCR.assertParameters("convertConvertibleToString", 0, apiUrls,
				contentType, dataRecord);
		endpointConverter.MCR.assertReturn("convertConvertibleToString", 0, response.getEntity());

		assertEquals(response.getStatusInfo(), Response.Status.OK);
		assertEquals(response.getHeaderString(HttpHeaders.CONTENT_TYPE), contentType);
	}

	@Test
	public void testReadDecoratedRecordJson() {
		String contentType = APPLICATION_VND_CORA_RECORD_DECORATED_JSON;

		Response response = recordEndpoint.readDecoratedRecordJson("someAuthToken", "someType",
				"someId");

		assertComponentsCalledCorrectlyForContentType(contentType, response);
	}
}