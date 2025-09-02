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

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import jakarta.ws.rs.core.Response;
import se.uu.ub.cora.therest.AnnotationTestHelper;
import se.uu.ub.cora.therest.dependency.TheRestInstanceFactorySpy;
import se.uu.ub.cora.therest.dependency.TheRestInstanceProvider;

public class RecordEndpointReadDecoratedTest {
	private static final String APPLICATION_VND_CORA_RECORD_DECORATED_XML_QS09 = "application/vnd.cora.record-decorated+xml;qs=0.9";
	private static final String APPLICATION_VND_CORA_RECORD_DECORATED_XML = "application/vnd.cora.record-decorated+xml";
	private static final String APPLICATION_VND_CORA_RECORD_DECORATED_JSON_QS09 = "application/vnd.cora.record-decorated+json;qs=0.9";
	private static final String APPLICATION_VND_CORA_RECORD_DECORATED_JSON = "application/vnd.cora.record-decorated+json";

	private RecordEndpointReadDecorated recordEndpoint;
	private HttpServletRequestOldSpy requestSpy;
	private TheRestInstanceFactorySpy factory;

	@BeforeMethod
	public void beforeMethod() {

		factory = new TheRestInstanceFactorySpy();
		TheRestInstanceProvider.onlyForTestSetTheRestInstanceFactory(factory);

		requestSpy = new HttpServletRequestOldSpy();
		recordEndpoint = new RecordEndpointReadDecorated(requestSpy);
	}

	@AfterMethod
	public void afterMethod() {
		TheRestInstanceProvider.onlyForTestResetTheRestInstanceFactory();
	}

	@Test
	public void testInit() {
		recordEndpoint = new RecordEndpointReadDecorated(requestSpy);
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
	public void testReadDecoratedRecordXml() {
		Response response = recordEndpoint.readDecoratedRecordXml("someAuthToken", "someType",
				"someId");
		assertCallDecorateReader(APPLICATION_VND_CORA_RECORD_DECORATED_XML, response);
	}

	@Test
	public void testReadDecoratedRecordJson() {
		Response response = recordEndpoint.readDecoratedRecordJson("someAuthToken", "someType",
				"someId");
		assertCallDecorateReader(APPLICATION_VND_CORA_RECORD_DECORATED_JSON, response);
	}

	private void assertCallDecorateReader(String accept, Response response) {
		var decoratedReader = (EndpointDecoratedReaderSpy) factory.MCR
				.assertCalledParametersReturn("createDecoratedReader");

		decoratedReader.MCR.assertParameters("readAndDecorateRecord", 0, requestSpy, accept,
				"someAuthToken", "someType", "someId");
		decoratedReader.MCR.assertReturn("readAndDecorateRecord", 0, response);
	}

}