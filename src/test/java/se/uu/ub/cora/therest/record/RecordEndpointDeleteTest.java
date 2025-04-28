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

import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

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
import se.uu.ub.cora.spider.dependency.SpiderInstanceProvider;
import se.uu.ub.cora.therest.AnnotationTestHelper;

public class RecordEndpointDeleteTest {
	private static final String DUMMY_NON_AUTHORIZED_TOKEN = "dummyNonAuthorizedToken";
	private static final String PLACE_0001 = "place:0001";
	private static final String PLACE = "place";
	private static final String AUTH_TOKEN = "authToken";

	private JsonToDataConverterFactorySpy jsonToDataConverterFactorySpy = new JsonToDataConverterFactorySpy();

	private RecordEndpointDelete recordEndpoint;
	private OldSpiderInstanceFactorySpy spiderInstanceFactorySpy;
	private Response response;
	private HttpServletRequestSpy requestSpy;
	private LoggerFactorySpy loggerFactorySpy;
	private DataFactorySpy dataFactorySpy;

	private DataToJsonConverterFactoryCreatorSpy converterFactoryCreatorSpy;
	private ConverterFactorySpy converterFactorySpy;
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

		Map<String, String> settings = new HashMap<>();
		settings.put("theRestPublicPathToSystem", "/systemone/rest/");
		settings.put("iiifPublicPathToSystem", "/systemone/iiif/");
		SettingsProvider.setSettings(settings);

		requestSpy = new HttpServletRequestSpy();
		recordEndpoint = new RecordEndpointDelete(requestSpy);
	}

	@Test
	public void testInit() {
		recordEndpoint = new RecordEndpointDelete(requestSpy);
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

	@Test
	public void testPreferredTokenForDelete() {
		expectTokenForDeleteToPreferablyBeHeaderThanQuery(AUTH_TOKEN, "authToken2", AUTH_TOKEN);
		expectTokenForDeleteToPreferablyBeHeaderThanQuery(null, AUTH_TOKEN, AUTH_TOKEN);
		expectTokenForDeleteToPreferablyBeHeaderThanQuery(AUTH_TOKEN, null, AUTH_TOKEN);
		expectTokenForDeleteToPreferablyBeHeaderThanQuery(null, null, null);
	}

	private void expectTokenForDeleteToPreferablyBeHeaderThanQuery(String headerAuthToken,
			String queryAuthToken, String authTokenExpected) {
		response = recordEndpoint.deleteRecord(headerAuthToken, queryAuthToken, PLACE,
				"place:0002");

		SpiderRecordDeleterSpy spiderDeleterSpy = spiderInstanceFactorySpy.spiderRecordDeleterSpy;
		assertEquals(spiderDeleterSpy.authToken, authTokenExpected);
	}

	@Test
	public void testDeleteRecordNoIncomingLinks() {
		response = recordEndpoint.deleteRecord(AUTH_TOKEN, AUTH_TOKEN, PLACE, "place:0002");
		assertResponseStatusIs(Response.Status.OK);
	}

	@Test
	public void testDeleteRecordIncomingLinks() {
		response = recordEndpoint.deleteRecord(AUTH_TOKEN, AUTH_TOKEN, PLACE, PLACE_0001);
		assertResponseStatusIs(Response.Status.METHOD_NOT_ALLOWED);
	}

	@Test
	public void testDeleteRecordUnauthorized() {
		response = recordEndpoint.deleteRecord(DUMMY_NON_AUTHORIZED_TOKEN, AUTH_TOKEN, PLACE,
				PLACE_0001);
		assertResponseStatusIs(Response.Status.FORBIDDEN);
	}

	@Test
	public void testDeleteRecordNotFound() {
		response = recordEndpoint.deleteRecord("someToken78678567", AUTH_TOKEN, PLACE,
				"place:0001_NOT_FOUND");
		assertResponseStatusIs(Response.Status.NOT_FOUND);
	}

	@Test
	public void testAnnotationsForDeletRecord() throws Exception {
		AnnotationTestHelper annotationHelper = AnnotationTestHelper
				.createAnnotationTestHelperForClassMethodNameAndNumOfParameters(
						recordEndpoint.getClass(), "deleteRecord", 4);

		annotationHelper.assertHttpMethodAndPathAnnotation("DELETE", "{type}/{id}");
		annotationHelper.assertAnnotationForAuthTokensAndTypeAndIdParameters();
	}

}