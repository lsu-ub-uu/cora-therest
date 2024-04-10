/*
 * Copyright 2024 Uppsala University Library
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
package se.uu.ub.cora.therest.iiif;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.Request;
import jakarta.ws.rs.core.Response;
import se.uu.ub.cora.spider.authorization.AuthorizationException;
import se.uu.ub.cora.spider.binary.iiif.IiifResponse;
import se.uu.ub.cora.spider.dependency.SpiderInstanceProvider;
import se.uu.ub.cora.spider.record.RecordNotFoundException;
import se.uu.ub.cora.spider.spies.SpiderInstanceFactorySpy;
import se.uu.ub.cora.spider.spies.binary.iiif.IiifReaderSpy;
import se.uu.ub.cora.therest.AnnotationTestHelper;
import se.uu.ub.cora.therest.spy.HttpHeadersSpy;
import se.uu.ub.cora.therest.spy.RequestSpy;

public class IiifEndpointTest {
	private static final List<String> restrictedHeaders = List.of("access-control-request-headers",
			"access-control-request-method", "connection", "content-length",
			"content-transfer-encoding", "host", "keep-alive", "origin", "trailer",
			"transfer-encoding", "upgrade", "via");

	IiifEndpoint endpoint;
	private IiifReaderSpy iiifReader;
	private HttpHeadersSpy headers;
	private RequestSpy request;
	private SpiderInstanceFactorySpy spiderInstanceFactorySpy;

	@BeforeMethod
	private void beforeMethod() {
		headers = new HttpHeadersSpy();
		request = new RequestSpy();
		iiifReader = new IiifReaderSpy();

		spiderInstanceFactorySpy = new SpiderInstanceFactorySpy();
		spiderInstanceFactorySpy.MRV.setDefaultReturnValuesSupplier("factorIiifReader",
				() -> iiifReader);
		SpiderInstanceProvider.setSpiderInstanceFactory(spiderInstanceFactorySpy);

		request.MRV.setDefaultReturnValuesSupplier("getMethod", () -> "someMethod");
		headers.MRV.setDefaultReturnValuesSupplier("getRequestHeaders", () -> createHeaders());

		endpoint = new IiifEndpoint();
	}

	private MultivaluedHashMap<Object, Object> createHeaders() {
		MultivaluedHashMap<Object, Object> requestHeaders = new MultivaluedHashMap<>();
		requestHeaders.put("aHeaderKey", List.of("oneHeaderValue", "twoHeaderValue"));
		return requestHeaders;
	}

	@Test
	public void testReadRequestPathAndParameters() throws Exception {
		Class<?>[] parameters = { HttpHeaders.class, Request.class, String.class, String.class };
		AnnotationTestHelper annotationHelper = AnnotationTestHelper
				.createAnnotationTestHelperForClassMethodNameAndParameters(endpoint.getClass(),
						"readIiif", parameters);

		annotationHelper.assertHttpMethodAndPathAnnotation("GET", "{identifier}/{requestedUri:.*}");
		annotationHelper.assertContextAnnotationForPosition(0);
		annotationHelper.assertContextAnnotationForPosition(1);
		annotationHelper.assertPathParamAnnotationByNameAndPosition("identifier", 2);
		annotationHelper.assertPathParamAnnotationByNameAndPosition("requestedUri", 3);
	}

	@Test
	public void testIiifReaderFetchedFromInstanceProvider() throws Exception {
		endpoint.readIiif(headers, request, "someIdentifier", "some/requested/Uri");

		spiderInstanceFactorySpy.MCR.assertMethodWasCalled("factorIiifReader");
	}

	@Test
	public void testIiifReaderCalledWithParametersFromRequest() throws Exception {
		endpoint.readIiif(headers, request, "someIdentifier", "some/requested/Uri");

		iiifReader.MCR.assertParameters("readIiif", 0, "someIdentifier", "some/requested/Uri",
				"someMethod");
		assertHeadersSentToIiifReader(iiifReader);
	}

	private void assertHeadersSentToIiifReader(IiifReaderSpy iiifReader) {
		Map<String, String> headers = getHeadersFromCallToIiifReader(iiifReader);
		assertEquals(headers.size(), 1);
		assertEquals(headers.get("aHeaderKey"), "oneHeaderValue, twoHeaderValue");
	}

	private Map<String, String> getHeadersFromCallToIiifReader(IiifReaderSpy iiifReader) {
		return (Map<String, String>) iiifReader.MCR
				.getValueForMethodNameAndCallNumberAndParameterName("readIiif", 0, "headers");
	}

	@Test
	public void testIiiifReaderNotAuthorizedToRead() throws Exception {
		iiifReader.MRV.setAlwaysThrowException("readIiif", new AuthorizationException("someError"));

		Response response = endpoint.readIiif(headers, request, "someIdentifier",
				"some/requested/Uri");

		assertEquals(response.getStatus(), 401);
	}

	@Test
	public void testIiiifReaderNotAuthorizedReturnsForbiddenForKnownToken() throws Exception {
		iiifReader.MRV.setAlwaysThrowException("readIiif", new AuthorizationException("someError"));
		headers.MRV.setSpecificReturnValuesSupplier("getRequestHeader", () -> List.of("someToken"),
				"authtoken");

		Response response = endpoint.readIiif(headers, request, "someIdentifier",
				"some/requested/Uri");

		assertEquals(response.getStatus(), 403);
	}

	@Test
	public void testIiiifReaderNotFoundRecordToRead() throws Exception {
		iiifReader.MRV.setAlwaysThrowException("readIiif",
				RecordNotFoundException.withMessage("someError"));

		Response response = endpoint.readIiif(headers, request, "someIdentifier",
				"some/requested/Uri");

		assertEquals(response.getStatus(), 404);
	}

	@Test
	public void testIiiifReaderAnyExecptionToRead() throws Exception {
		iiifReader.MRV.setAlwaysThrowException("readIiif", new RuntimeException("someError"));

		Response response = endpoint.readIiif(headers, request, "someIdentifier",
				"some/requested/Uri");

		assertEquals(response.getStatus(), 500);
	}

	@Test
	public void testEndpointResponseFromIiifReader() throws Exception {
		iiifReader.MRV.setDefaultReturnValuesSupplier("readIiif", () -> new IiifResponse(418,
				Map.of("content-type", "plain/text", "someOtherHeader", "aHeaderValue"), "body"));

		Response response = endpoint.readIiif(headers, request, "someIdentifier",
				"some/requested/Uri");

		IiifResponse iiifResponse = (IiifResponse) iiifReader.MCR.getReturnValue("readIiif", 0);

		assertEquals(response.getStatus(), iiifResponse.status());
		assertEquals(response.getHeaders().size(), 2);
		assertEquals(response.getHeaders().get("content-type"), List.of("plain/text"));
		assertEquals(response.getHeaders().get("someOtherHeader"), List.of("aHeaderValue"));
		assertEquals(response.getEntity(), "body");
	}

	@Test
	public void testCallReadIfffUsingRestrictedHeaders_IN() throws Exception {
		headers.MRV.setDefaultReturnValuesSupplier("getRequestHeaders",
				() -> createInHeadersUsingRestrictedHeadersInUpperCase());

		endpoint.readIiif(headers, request, "someIdentifier", "some/requested/Uri");

		Map<String, String> nonRestrictedHeaders = (Map<String, String>) iiifReader.MCR
				.getValueForMethodNameAndCallNumberAndParameterName("readIiif", 0, "headers");

		assertEquals(nonRestrictedHeaders.size(), 1);
		assertTrue(nonRestrictedHeaders.containsKey("aHeaderKey"));
	}

	private MultivaluedHashMap<Object, Object> createInHeadersUsingRestrictedHeadersInUpperCase() {
		MultivaluedHashMap<Object, Object> requestHeaders = new MultivaluedHashMap<>();

		requestHeaders.put("aHeaderKey", List.of("oneHeaderValue"));

		for (String restrictedHeader : restrictedHeaders) {
			String headerInUpperCase = restrictedHeader.toUpperCase();
			requestHeaders.put(headerInUpperCase, List.of(restrictedHeader + "Value"));
		}
		return requestHeaders;
	}

	@Test
	public void testCallReadIfffUsingRestrictedHeaders_OUT() throws Exception {
		IiifResponse responseWithRestrictedHeaders = new IiifResponse(200,
				createOutHeadersUsingRestrictedHeadersInUpperCase(), "aBody");

		iiifReader.MRV.setDefaultReturnValuesSupplier("readIiif",
				() -> responseWithRestrictedHeaders);

		Response response = endpoint.readIiif(headers, request, "someIdentifier",
				"some/requested/Uri");

		assertEquals(response.getHeaders().size(), 1);
		assertTrue(response.getHeaders().containsKey("aHeaderKey"));

	}

	private Map<String, String> createOutHeadersUsingRestrictedHeadersInUpperCase() {
		Map<String, String> requestHeaders = new HashMap<>();

		requestHeaders.put("aHeaderKey", "oneHeaderValue");

		for (String restrictedHeader : restrictedHeaders) {
			String headerInUpperCase = restrictedHeader.toUpperCase();
			requestHeaders.put(headerInUpperCase, restrictedHeader + "Value");
		}
		return requestHeaders;
	}
}
