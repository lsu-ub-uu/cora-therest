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

package se.ub.uu.cora.therest.deployment;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import se.uu.ub.cora.therest.AnnotationTestHelper;
import se.uu.ub.cora.therest.dependency.TheRestInstanceFactorySpy;
import se.uu.ub.cora.therest.dependency.TheRestInstanceProvider;
import se.uu.ub.cora.therest.deployment.RecordEndpointDeployment;
import se.uu.ub.cora.therest.record.RecordEndpointRead;
import se.uu.ub.cora.therest.url.HttpServletRequestSpy;
import se.uu.ub.cora.therest.url.UrlHandlerSpy;

public class RecordEndpointDeploymentTest {
	private static final String HTTP_BASE_REST_URL = "http://base.rest.url/rest/";

	private static final String APPLICATION_VND_CORA_DEPLOYMENT_INFO_JSON = ""
			+ "application/vnd.cora.deploymentInfo+json";

	private RecordEndpointDeployment recordEndpoint;
	private Response response;
	private HttpServletRequestSpy requestSpy;

	private TheRestInstanceFactorySpy instanceFactory;

	@BeforeMethod
	public void beforeMethod() {
		setupUrlHandler();

		requestSpy = new HttpServletRequestSpy();
		recordEndpoint = new RecordEndpointDeployment(requestSpy);
	}

	private void setupUrlHandler() {
		UrlHandlerSpy urlHandler = new UrlHandlerSpy();
		urlHandler.MRV.setDefaultReturnValuesSupplier("getRestUrl", () -> HTTP_BASE_REST_URL);

		instanceFactory = new TheRestInstanceFactorySpy();
		instanceFactory.MRV.setDefaultReturnValuesSupplier("factorUrlHandler", () -> urlHandler);

		TheRestInstanceProvider.onlyForTestSetTheRestInstanceFactory(instanceFactory);
	}

	@Test
	public void testClassAnnotation() {
		AnnotationTestHelper annotationHelper = AnnotationTestHelper
				.createAnnotationTestHelperForClass(RecordEndpointRead.class);

		annotationHelper.assertPathAnnotationForClass("/");
	}

	// @Test
	// public void testUrlsHandledByUrlHandler() {
	// recordEndpoint = new RecordEndpointDeployment(requestSpy);
	//
	// response = recordEndpoint.readRecordJson(AUTH_TOKEN, AUTH_TOKEN, PLACE, PLACE_0001);
	//
	// UrlHandlerSpy urlHandler = (UrlHandlerSpy) instanceFactory.MCR
	// .getReturnValue("factorUrlHandler", 0);
	//
	// var restUrl = urlHandler.MCR.assertCalledParametersReturn("getRestUrl", requestSpy);
	// var iiifUrl = urlHandler.MCR.assertCalledParametersReturn("getIiifUrl", requestSpy);
	//
	// assertEquals(restUrl, getRestUrlFromFactorUsingConvertibleAndExternalUrls());
	// assertEquals(iiifUrl, getIiifUrlFromFactorUsingConvertibleAndExternalUrls());
	// }

	// private String getRestUrlFromFactorUsingConvertibleAndExternalUrls() {
	// DataToJsonConverterFactorySpy converterFactory = (DataToJsonConverterFactorySpy)
	// converterFactoryCreatorSpy.MCR
	// .getReturnValue("createFactory", 0);
	// se.uu.ub.cora.data.converter.ExternalUrls externalUrls =
	// (se.uu.ub.cora.data.converter.ExternalUrls) converterFactory.MCR
	// .getParameterForMethodAndCallNumberAndParameter(
	// "factorUsingConvertibleAndExternalUrls", 0, "externalUrls");
	// return externalUrls.getBaseUrl();
	// }
	//
	// private String getIiifUrlFromFactorUsingConvertibleAndExternalUrls() {
	// DataToJsonConverterFactorySpy converterFactory = (DataToJsonConverterFactorySpy)
	// converterFactoryCreatorSpy.MCR
	// .getReturnValue("createFactory", 0);
	// se.uu.ub.cora.data.converter.ExternalUrls externalUrls =
	// (se.uu.ub.cora.data.converter.ExternalUrls) converterFactory.MCR
	// .getParameterForMethodAndCallNumberAndParameter(
	// "factorUsingConvertibleAndExternalUrls", 0, "externalUrls");
	// return externalUrls.getIfffUrl();
	// }

	@Test
	public void testAnnotationsForReadIndexHtml() throws Exception {
		AnnotationTestHelper annotationHelper = AnnotationTestHelper
				.createAnnotationTestHelperForClassMethodNameAndNumOfParameters(
						recordEndpoint.getClass(), "forwardToHtmlDocumentation", 0);

		annotationHelper.assertHttpMethodAndPathAnnotation("GET", "");
		annotationHelper.assertProducesAnnotation(MediaType.TEXT_HTML);
	}

	@Test
	public void testAnnotationsForDeploymentInfo() throws Exception {
		AnnotationTestHelper annotationHelper = AnnotationTestHelper
				.createAnnotationTestHelperForClassMethodNameAndNumOfParameters(
						recordEndpoint.getClass(), "getDeploymentInfo", 0);

		annotationHelper.assertHttpMethodAndPathAnnotation("GET", "");
		annotationHelper
				.assertProducesAnnotation(APPLICATION_VND_CORA_DEPLOYMENT_INFO_JSON + ";qs=0.1");
	}

	@Test
	public void testForwardToHtmlDocumentation() {
		response = recordEndpoint.forwardToHtmlDocumentation();
		assertResponseStatusIs(Response.Status.TEMPORARY_REDIRECT);
		String locationString = response.getHeaders().getFirst(HttpHeaders.LOCATION).toString();
		assertEquals(locationString, HTTP_BASE_REST_URL + "index.html");
	}

	@Test
	public void testReadDeploymentInfo() {
		response = recordEndpoint.getDeploymentInfo();
		assertEntityExists();
		assertResponseStatusIs(Response.Status.OK);
		assertResponseContentTypeIs(APPLICATION_VND_CORA_DEPLOYMENT_INFO_JSON);
		assertEquals(response.getEntity(), """
				{
					"name": "SystemOne dev",
					"urls": {
						"REST": "http://base.rest.url/rest/",
						"appToken": "appToken",
						"password": "password",
						"records":"?",
						"iiifUrl":"someIiifUrl"
					},
					"demoUsers":[
						{
							"name": "systemoneAdmin",
							"text": "appToken for systemoneAdmin",
							"type": "appTokenLogin",
							"loginId": "systemoneAdmin@system.cora.uu.se",
							"appToken": "5d3f3ed4-4931-4924-9faa-8eaf5ac6457e"
						}
					]
				}
				""");
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

}