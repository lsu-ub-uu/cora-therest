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
import se.uu.ub.cora.therest.url.HttpServletRequestSpy;

public class RecordEndpointSearchTest {
	private static final String APPLICATION_XML = "application/xml";
	private static final String APPLICATION_XML_QS01 = "application/xml;qs=0.1";
	private static final String APPLICATION_VND_CORA_RECORD_LIST_XML = "application/vnd.cora.recordList+xml";
	private static final String APPLICATION_VND_CORA_RECORD_LIST_JSON = "application/vnd.cora.recordList+json";
	private static final String APPLICATION_VND_CORA_RECORD_LIST_JSON_QS09 = "application/vnd.cora.recordList+json;qs=0.9";

	private static final String APPLICATION_VND_CORA_RECORD_LIST_DECORATED_XML_QS09 = "application/vnd.cora.recordList-decorated+xml;qs=0.9";
	private static final String APPLICATION_VND_CORA_RECORD_LIST_DECORATED_JSON_QS09 = "application/vnd.cora.recordList-decorated+json;qs=0.9";
	private static final String APPLICATION_VND_CORA_RECORD_LIST_DECORATED_XML = "application/vnd.cora.recordList-decorated+xml";
	private static final String APPLICATION_VND_CORA_RECORD_LIST_DECORATED_JSON = "application/vnd.cora.recordList-decorated+json";

	private static final String QUERY_AUTH_TOKEN = "queryAuthToken";
	private static final String HEADER_AUTH_TOKEN = "headerAuthToken";
	private static final String SEARCH_ID = "aSearchId";
	private static final String SEARCH_DATA = "someSearchData";

	private RecordEndpointSearch recordEndpoint;
	private Response response;
	private HttpServletRequestSpy requestSpy;

	private TheRestInstanceFactorySpy instanceFactory;

	@BeforeMethod
	public void beforeMethod() {
		instanceFactory = new TheRestInstanceFactorySpy();
		TheRestInstanceProvider.onlyForTestSetTheRestInstanceFactory(instanceFactory);

		requestSpy = new HttpServletRequestSpy();
		recordEndpoint = new RecordEndpointSearch(requestSpy);
	}

	@AfterMethod
	public void afterMethod() {
		TheRestInstanceProvider.onlyForTestResetTheRestInstanceFactory();
	}

	@Test
	public void testClassAnnotation() throws Exception {
		AnnotationTestHelper annotationHelper = AnnotationTestHelper
				.createAnnotationTestHelperForClass(RecordEndpointUpdate.class);

		annotationHelper.assertPathAnnotationForClass("/");
	}

	@Test
	public void testAnnotationsForSearchRecordJson() throws Exception {
		assertSearchAnnotations("searchRecordJson", APPLICATION_VND_CORA_RECORD_LIST_JSON_QS09);
	}

	@Test
	public void testAnnotationsForSearchRecordXml() throws Exception {
		assertSearchAnnotations("searchRecordXml", APPLICATION_VND_CORA_RECORD_LIST_XML);
	}

	@Test
	public void testAnnotationsForSearchRecordDecoratedJson() throws Exception {
		assertSearchAnnotations("searchRecordDecoratedJson",
				APPLICATION_VND_CORA_RECORD_LIST_DECORATED_JSON_QS09);
	}

	@Test
	public void testAnnotationsForSearchRecordDecoratedXml() throws Exception {
		assertSearchAnnotations("searchRecordDecoratedXml",
				APPLICATION_VND_CORA_RECORD_LIST_DECORATED_XML_QS09);
	}

	@Test
	public void testAnnotationsForSearchAApplicationXmlForBrowsers() throws Exception {
		assertSearchAnnotations("searchRecordAsApplicationXmlForBrowsers", APPLICATION_XML_QS01);
	}

	private void assertSearchAnnotations(String methodName, String accept)
			throws NoSuchMethodException {
		AnnotationTestHelper annotationHelper = AnnotationTestHelper
				.createAnnotationTestHelperForClassMethodNameAndNumOfParameters(
						recordEndpoint.getClass(), methodName, 4);

		annotationHelper.assertHttpMethodAndPathAnnotation("GET", "searchResult/{searchId}");
		annotationHelper.assertProducesAnnotation(accept);
		annotationHelper.assertAnnotationForAuthTokenParameters();
		annotationHelper.assertPathParamAnnotationByNameAndPosition("searchId", 2);
		annotationHelper.assertQueryParamAnnotationByNameAndPosition("searchData", 3);
	}

	@Test
	public void testSearchRecordForJsonOut() {
		response = recordEndpoint.searchRecordJson(HEADER_AUTH_TOKEN, QUERY_AUTH_TOKEN, SEARCH_ID,
				SEARCH_DATA);
		assertCallMethod("factorEndpointSearch", APPLICATION_VND_CORA_RECORD_LIST_JSON);
	}

	@Test
	public void testSearchRecordForXmlOut() {
		response = recordEndpoint.searchRecordXml(HEADER_AUTH_TOKEN, QUERY_AUTH_TOKEN, SEARCH_ID,
				SEARCH_DATA);

		assertCallMethod("factorEndpointSearch", APPLICATION_VND_CORA_RECORD_LIST_XML);
	}

	@Test
	public void testSearchRecordDecoratedForJsonOut() {
		response = recordEndpoint.searchRecordDecoratedJson(HEADER_AUTH_TOKEN, QUERY_AUTH_TOKEN,
				SEARCH_ID, SEARCH_DATA);

		assertCallMethod("factorEndpointSearchDecorated",
				APPLICATION_VND_CORA_RECORD_LIST_DECORATED_JSON);
	}

	@Test
	public void testSearchRecordDecoratedForXmlOut() {
		response = recordEndpoint.searchRecordDecoratedXml(HEADER_AUTH_TOKEN, QUERY_AUTH_TOKEN,
				SEARCH_ID, SEARCH_DATA);

		assertCallMethod("factorEndpointSearchDecorated",
				APPLICATION_VND_CORA_RECORD_LIST_DECORATED_XML);
	}

	@Test
	public void testReadRecordAsApplicationXmlForBrowsers() {
		response = recordEndpoint.searchRecordAsApplicationXmlForBrowsers(HEADER_AUTH_TOKEN,
				QUERY_AUTH_TOKEN, SEARCH_ID, SEARCH_DATA);

		assertCallMethod("factorEndpointSearch", APPLICATION_XML);
	}

	private void assertCallMethod(String factorMethod, String contentTypeOut) {
		EndpointSearchSpy endPointSearch = (EndpointSearchSpy) instanceFactory.MCR
				.getReturnValue(factorMethod, 0);

		endPointSearch.MCR.assertParameters("searchRecord", 0, requestSpy, contentTypeOut,
				HEADER_AUTH_TOKEN, QUERY_AUTH_TOKEN, SEARCH_ID, SEARCH_DATA);
		endPointSearch.MCR.assertReturn("searchRecord", 0, response);
	}

}