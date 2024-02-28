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

import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.Request;
import jakarta.ws.rs.core.Response;
import se.uu.ub.cora.spider.binary.iiif.IiifImageReader;
import se.uu.ub.cora.spider.dependency.SpiderInstanceProvider;
import se.uu.ub.cora.spider.spies.SpiderInstanceFactorySpy;
import se.uu.ub.cora.spider.spies.binary.iiif.IiifImageReaderSpy;
import se.uu.ub.cora.therest.AnnotationTestHelper;
import se.uu.ub.cora.therest.spy.HttpHeadersSpy;
import se.uu.ub.cora.therest.spy.RequestSpy;

public class IiifEndpointTest {
	IiifEndpoint endpoint;
	IiifImageReader iifBinaryReader = new IiifImageReaderSpy();
	private HttpHeadersSpy headers;
	private RequestSpy request;

	@BeforeMethod
	private void beforeMethod() {
		headers = new HttpHeadersSpy();
		request = new RequestSpy();

		endpoint = new IiifEndpoint();
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
		SpiderInstanceFactorySpy spiderInstanceFactorySpy = new SpiderInstanceFactorySpy();
		SpiderInstanceProvider.setSpiderInstanceFactory(spiderInstanceFactorySpy);
		request.MRV.setDefaultReturnValuesSupplier("getMethod", () -> "someMethod");
		MultivaluedHashMap<Object, Object> requestHeaders = new MultivaluedHashMap<>();
		requestHeaders.put("aHeaderKey", List.of("aHeaderValue"));
		headers.MRV.setDefaultReturnValuesSupplier("getRequestHeaders", () -> requestHeaders);

		Response readIiif = endpoint.readIiif(headers, request, "someIdentifier",
				"some/requested/Uri");

		spiderInstanceFactorySpy.MCR.assertMethodWasCalled("factorIiifReader");
		IiifImageReaderSpy iiifReader = (IiifImageReaderSpy) spiderInstanceFactorySpy.MCR
				.getReturnValue("factorIiifReader", 0);

		iiifReader.MCR.assertParameters("readIiif", 0, "someIdentifier", "some/requested/Uri",
				"someMethod");

		// assertEquals(response.getStatusInfo(), Response.Status.OK);

	}
}
