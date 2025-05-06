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
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import se.uu.ub.cora.converter.ConverterException;
import se.uu.ub.cora.converter.ConverterProvider;
import se.uu.ub.cora.converter.ExternalUrls;
import se.uu.ub.cora.data.Convertible;
import se.uu.ub.cora.data.DataChild;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataProvider;
import se.uu.ub.cora.data.DataRecord;
import se.uu.ub.cora.data.DataRecordGroup;
import se.uu.ub.cora.data.converter.DataToJsonConverterProvider;
import se.uu.ub.cora.data.converter.JsonToDataConverterProvider;
import se.uu.ub.cora.data.spies.DataFactorySpy;
import se.uu.ub.cora.data.spies.DataGroupSpy;
import se.uu.ub.cora.initialize.SettingsProvider;
import se.uu.ub.cora.json.parser.JsonValue;
import se.uu.ub.cora.json.parser.org.OrgJsonParser;
import se.uu.ub.cora.logger.LoggerProvider;
import se.uu.ub.cora.logger.spies.LoggerFactorySpy;
import se.uu.ub.cora.spider.dependency.SpiderInstanceProvider;
import se.uu.ub.cora.therest.AnnotationTestHelper;

public class RecordEndpointValidateTest {
	private static final String APPLICATION_VND_UUB_RECORD_XML = "application/vnd.cora.record+xml";
	private static final String APPLICATION_VND_UUB_RECORD_JSON = "application/vnd.cora.record+json";
	private static final String APPLICATION_VND_UUB_RECORD_JSON_QS09 = "application/vnd.cora.record+json;qs=0.9";
	private static final String APPLICATION_VND_UUB_WORKORDER_XML = "application/vnd.cora.workorder+xml";
	private static final String APPLICATION_VND_UUB_WORKORDER_JSON = "application/vnd.cora.workorder+json";
	private static final String TEXT_PLAIN = "text/plain; charset=utf-8";
	private static final String DUMMY_NON_AUTHORIZED_TOKEN = "dummyNonAuthorizedToken";
	private static final String PLACE = "place";
	private static final String AUTH_TOKEN = "authToken";
	private JsonParserSpy jsonParser;

	private JsonToDataConverterFactorySpy jsonToDataConverterFactorySpy = new JsonToDataConverterFactorySpy();

	private RecordEndpointValidate recordEndpoint;
	private OldSpiderInstanceFactorySpy spiderInstanceFactorySpy;
	private Response response;
	private HttpServletRequestSpy requestSpy;
	private LoggerFactorySpy loggerFactorySpy;
	private DataFactorySpy dataFactorySpy;

	private String jsonToValidate = "{\"order\":{\"name\":\"validationOrder\",\"children\":[{\"name\":\"recordInfo\",\"children\":[{\"name\":\"dataDivider\",\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"system\"},{\"name\":\"linkedRecordId\",\"value\":\"testSystem\"}]}]},{\"name\":\"recordType\",\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"recordType\"},{\"name\":\"linkedRecordId\",\"value\":\"someRecordType\"}]},{\"name\":\"metadataToValidate\",\"value\":\"existing\"},{\"name\":\"validateLinks\",\"value\":\"false\"}]},\"record\":{\"name\":\"text\",\"children\":[{\"name\":\"recordInfo\",\"children\":[{\"name\":\"id\",\"value\":\"workOrderRecordIdTextVar2Text\"},{\"name\":\"dataDivider\",\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"system\"},{\"name\":\"linkedRecordId\",\"value\":\"cora\"}]}]},{\"name\":\"textPart\",\"children\":[{\"name\":\"text\",\"value\":\"Id på länkad post\"}],\"attributes\":{\"type\":\"default\",\"lang\":\"sv\"}},{\"name\":\"textPart\",\"children\":[{\"name\":\"text\",\"value\":\"Linked record id\"}],\"attributes\":{\"type\":\"alternative\",\"lang\":\"en\"}}]}}";
	private String defaultJson = "{\"name\":\"someRecordType\",\"children\":[]}";
	private String defaultXml = "<someXml></someXml>";
	private DataToJsonConverterFactoryCreatorSpy converterFactoryCreatorSpy;
	private ConverterFactorySpy converterFactorySpy;
	private String standardBaseUrlHttp = "http://cora.epc.ub.uu.se/systemone/rest/record/";
	private String standardIffUrlHttp = "http://cora.epc.ub.uu.se/systemone/iiif/";
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
		recordEndpoint = new RecordEndpointValidate(requestSpy);

		setUpSpiesInRecordEndpoint();
	}

	private void setUpSpiesInRecordEndpoint() {
		jsonParser = new JsonParserSpy();

		recordEndpoint.setJsonParser(jsonParser);
	}

	@Test
	public void testInit() {
		recordEndpoint = new RecordEndpointValidate(requestSpy);
		assertTrue(recordEndpoint.getJsonParser() instanceof OrgJsonParser);
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

	private void assertDataFromSpiderConvertedToJsonUsingConvertersFromProvider(
			Convertible convertible) {
		DataToJsonConverterFactorySpy converterFactory = (DataToJsonConverterFactorySpy) converterFactoryCreatorSpy.MCR
				.getReturnValue("createFactory", 0);

		converterFactory.MCR.assertParameters("factorUsingConvertibleAndExternalUrls", 0,
				convertible);
		se.uu.ub.cora.data.converter.ExternalUrls externalUrls = (se.uu.ub.cora.data.converter.ExternalUrls) converterFactory.MCR
				.getParameterForMethodAndCallNumberAndParameter(
						"factorUsingConvertibleAndExternalUrls", 0, "externalUrls");
		assertEquals(externalUrls.getBaseUrl(), standardBaseUrlHttp);
		assertEquals(externalUrls.getIfffUrl(), standardIffUrlHttp);

		DataToJsonConverterSpy converterSpy = (DataToJsonConverterSpy) converterFactory.MCR
				.getReturnValue("factorUsingConvertibleAndExternalUrls", 0);

		var entity = response.getEntity();
		converterSpy.MCR.assertReturn("toJsonCompactFormat", 0, entity);
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
	public void testPreferredTokenForValidate() {
		expectTokenForValidateToPrefereblyBeHeaderThanQuery(AUTH_TOKEN, "authToken2", AUTH_TOKEN);
		expectTokenForValidateToPrefereblyBeHeaderThanQuery(null, AUTH_TOKEN, AUTH_TOKEN);
		expectTokenForValidateToPrefereblyBeHeaderThanQuery(AUTH_TOKEN, null, AUTH_TOKEN);
		expectTokenForValidateToPrefereblyBeHeaderThanQuery(null, null, null);
	}

	private void expectTokenForValidateToPrefereblyBeHeaderThanQuery(String headerAuthToken,
			String queryAuthToken, String authTokenExpected) {
		response = recordEndpoint.validateRecordJsonJson(headerAuthToken, queryAuthToken, PLACE,
				jsonToValidate);

		SpiderRecordValidatorSpy spiderRecordValidatorSpy = spiderInstanceFactorySpy.spiderRecordValidatorSpy;

		assertEquals(spiderRecordValidatorSpy.authToken, authTokenExpected);
	}

	@Test
	public void testValidateRecord() {
		response = recordEndpoint.validateRecordJsonJson(AUTH_TOKEN, AUTH_TOKEN, "workOrder",
				defaultJson);

		assertEquals(jsonParser.jsonString, defaultJson);

		// parts requested from validationJson sent in
		JsonValueSpy topJsonObject = jsonParser.returnedJsonValue;
		assertEquals(topJsonObject.keys.get(0), "order");
		assertEquals(topJsonObject.keys.get(1), "record");

		DataGroup validationOrderSentOnToSpider = spiderInstanceFactorySpy.spiderRecordValidatorSpy.validationOrder;
		DataRecordGroup recordToValidateSentOnToSpider = spiderInstanceFactorySpy.spiderRecordValidatorSpy.recordToValidate;

		JsonValue orderDataFromParser = jsonParser.returnedJsonValue.returnedJsonValues.get(0);
		JsonValue firstSentToConverterFactory = jsonToDataConverterFactorySpy.jsonValues.get(0);
		assertSame(firstSentToConverterFactory, orderDataFromParser);

		JsonValue recordDataFromParser = jsonParser.returnedJsonValue.returnedJsonValues.get(1);
		JsonValue secondSentToConverterFactory = jsonToDataConverterFactorySpy.jsonValues.get(1);
		assertSame(secondSentToConverterFactory, recordDataFromParser);

		var converterSpyForOrder = jsonToDataConverterFactorySpy.jsonToDataConverterSpies.get(0);
		DataGroup orderReturnedDataPart = converterSpyForOrder.dataPartToReturn;
		assertSame(validationOrderSentOnToSpider, orderReturnedDataPart);

		var converterSpyForRecord = jsonToDataConverterFactorySpy.jsonToDataConverterSpies.get(1);
		DataGroup recordReturnedDataPart = converterSpyForRecord.dataPartToReturn;

		DataRecordGroup convertedToRecord = (DataRecordGroup) dataFactorySpy.MCR
				.assertCalledParametersReturn("factorRecordGroupFromDataGroup",
						recordReturnedDataPart);
		assertSame(recordToValidateSentOnToSpider, convertedToRecord);

		DataRecord validationResult = (DataRecord) spiderInstanceFactorySpy.spiderRecordValidatorSpy.MCR
				.getReturnValue("validateRecord", 0);
		assertDataFromSpiderConvertedToJsonUsingConvertersFromProvider(validationResult);

		assertResponseStatusIs(Response.Status.OK);
	}

	@Test
	public void testValidateWorkOrderPartMissing() {
		DataGroupSpy order = createDataGroupWithChildren();
		DataGroupSpy recordToValidate = new DataGroupSpy();
		DataGroupSpy recordAsDataGroup = createDataGroupWithChildren(recordToValidate);
		DataGroupSpy workOrder = createWorkOrderWithOrder(order, recordAsDataGroup);

		workOrder.MRV.setAlwaysThrowException("getFirstGroupWithNameInData",
				new se.uu.ub.cora.data.DataMissingException("someException"));

		response = recordEndpoint.validateRecordXmlXml(AUTH_TOKEN, AUTH_TOKEN, PLACE, defaultXml);

		assertEquals(response.getStatus(), 400);
		assertEquals(response.getEntity(),
				"Validation failed due to: WorkOrder part 'order' not found.");
	}

	@Test
	public void testValidateMultipleRecordToValidateExists() {
		DataGroupSpy validationOrder = new DataGroupSpy();
		DataGroupSpy order = createDataGroupWithChildren(validationOrder);
		DataGroupSpy recordToValidate1 = new DataGroupSpy();
		DataGroupSpy recordToValidate2 = new DataGroupSpy();
		DataGroupSpy recordAsDataGroup = createDataGroupWithChildren(recordToValidate1,
				recordToValidate2);
		createWorkOrderWithOrder(order, recordAsDataGroup);

		response = recordEndpoint.validateRecordXmlXml(AUTH_TOKEN, AUTH_TOKEN, PLACE, defaultXml);

		assertEquals(response.getStatus(), 400);
		assertEquals(response.getEntity(),
				"Validation failed due to: Too many children in workOrder part.");
	}

	@Test
	public void testValidateMultipleValidateOrderExists() {
		DataGroupSpy validationOrder1 = new DataGroupSpy();
		DataGroupSpy validationOrder2 = new DataGroupSpy();
		DataGroupSpy order = createDataGroupWithChildren(validationOrder1, validationOrder2);
		DataGroupSpy recordToValidate = new DataGroupSpy();
		DataGroupSpy recordAsDataGroup = createDataGroupWithChildren(recordToValidate);
		createWorkOrderWithOrder(order, recordAsDataGroup);

		response = recordEndpoint.validateRecordXmlXml(AUTH_TOKEN, AUTH_TOKEN, PLACE, defaultXml);

		assertEquals(response.getStatus(), 400);
		assertEquals(response.getEntity(),
				"Validation failed due to: Too many children in workOrder part.");
	}

	@Test
	public void testValidateRecordForXml() {
		DataGroupSpy validationOrder = new DataGroupSpy();
		DataGroupSpy order = createDataGroupWithChildren(validationOrder);
		DataGroupSpy recordToValidate = new DataGroupSpy();
		DataGroupSpy recordAsDataGroup = createDataGroupWithChildren(recordToValidate);
		DataGroupSpy workOrder = createWorkOrderWithOrder(order, recordAsDataGroup);

		response = recordEndpoint.validateRecordXmlXml(AUTH_TOKEN, AUTH_TOKEN, PLACE, defaultXml);

		workOrder.MCR.assertParameters("getFirstGroupWithNameInData", 0, "order");
		workOrder.MCR.assertParameters("getFirstGroupWithNameInData", 1, "record");
		order.MCR.assertParameters("getChildren", 0);
		recordAsDataGroup.MCR.assertParameters("getChildren", 0);

		DataRecordGroup convertedToRecord = (DataRecordGroup) dataFactorySpy.MCR
				.assertCalledParametersReturn("factorRecordGroupFromDataGroup", recordToValidate);
		spiderInstanceFactorySpy.spiderRecordValidatorSpy.MCR.assertParameters("validateRecord", 0,
				AUTH_TOKEN, "validationOrder", validationOrder, convertedToRecord);

		assertValidationResult();
	}

	private DataGroupSpy createWorkOrderWithOrder(DataGroupSpy order, DataGroupSpy dataGroup) {
		DataGroupSpy workOrder = new DataGroupSpy();
		workOrder.MRV.setSpecificReturnValuesSupplier("getFirstGroupWithNameInData", () -> order,
				"order");
		workOrder.MRV.setSpecificReturnValuesSupplier("getFirstGroupWithNameInData",
				() -> dataGroup, "record");
		stringToExternallyConvertibleConverterSpy.MRV.setDefaultReturnValuesSupplier("convert",
				() -> workOrder);
		return workOrder;
	}

	private DataGroupSpy createDataGroupWithChildren(DataGroupSpy... validationOrders) {
		DataGroupSpy dataGroup = new DataGroupSpy();
		addChildrenToOrder(dataGroup, validationOrders);
		return dataGroup;
	}

	private void addChildrenToOrder(DataGroupSpy order, DataGroupSpy... validationOrders) {
		ArrayList<DataChild> orderChildren = new ArrayList<>();
		for (DataGroup validationOrder : validationOrders) {
			orderChildren.add(validationOrder);
		}
		order.MRV.setDefaultReturnValuesSupplier("getChildren", () -> orderChildren);
	}

	private void assertValidationResult() {
		DataRecord validationResult = (DataRecord) spiderInstanceFactorySpy.spiderRecordValidatorSpy.MCR
				.getReturnValue("validateRecord", 0);

		assertXmlConvertionOfResponse(validationResult);
		assertEntityExists();
		assertResponseStatusIs(Response.Status.OK);
		assertResponseContentTypeIs(APPLICATION_VND_UUB_RECORD_XML);
	}

	@Test
	public void testValidateRecordInputAsJsonResponseAsXml() {
		response = recordEndpoint.validateRecordJsonXml(AUTH_TOKEN, AUTH_TOKEN, PLACE, defaultJson);

		JsonValueSpy topJsonObject = jsonParser.returnedJsonValue;
		assertEquals(topJsonObject.keys.get(0), "order");
		assertEquals(topJsonObject.keys.get(1), "record");

		DataGroup validationOrderSentOnToSpider = spiderInstanceFactorySpy.spiderRecordValidatorSpy.validationOrder;
		DataRecordGroup recordToValidateSentOnToSpider = spiderInstanceFactorySpy.spiderRecordValidatorSpy.recordToValidate;

		spiderInstanceFactorySpy.spiderRecordValidatorSpy.MCR.assertParameters("validateRecord", 0,
				AUTH_TOKEN, "validationOrder", validationOrderSentOnToSpider,
				recordToValidateSentOnToSpider);

		assertValidationResult();
	}

	@Test
	public void testValidateRecordInputAsXMLResponseAsJson() {
		DataGroupSpy validationOrder = new DataGroupSpy();
		DataGroupSpy order = createDataGroupWithChildren(validationOrder);
		DataGroupSpy recordToValidate = new DataGroupSpy();
		DataGroupSpy dataRecord = createDataGroupWithChildren(recordToValidate);
		DataGroupSpy workOrder = createWorkOrderWithOrder(order, dataRecord);

		response = recordEndpoint.validateRecordXmlJson(AUTH_TOKEN, AUTH_TOKEN, PLACE, defaultXml);

		workOrder.MCR.assertParameters("getFirstGroupWithNameInData", 0, "order");
		workOrder.MCR.assertParameters("getFirstGroupWithNameInData", 1, "record");

		DataRecordGroup convertedToRecord = (DataRecordGroup) dataFactorySpy.MCR
				.assertCalledParametersReturn("factorRecordGroupFromDataGroup", recordToValidate);
		spiderInstanceFactorySpy.spiderRecordValidatorSpy.MCR.assertParameters("validateRecord", 0,
				AUTH_TOKEN, "validationOrder", validationOrder, convertedToRecord);

		DataRecord validationResult = (DataRecord) spiderInstanceFactorySpy.spiderRecordValidatorSpy.MCR
				.getReturnValue("validateRecord", 0);

		assertDataFromSpiderConvertedToJsonUsingConvertersFromProvider(validationResult);
		assertEntityExists();
		assertResponseStatusIs(Response.Status.OK);
		assertResponseContentTypeIs(APPLICATION_VND_UUB_RECORD_JSON);
	}

	@Test
	public void testValidateRecordInputJsonWrongContentType() {
		jsonParser.throwError = true;
		response = recordEndpoint.validateRecordJsonJson(AUTH_TOKEN, AUTH_TOKEN, PLACE, defaultXml);
		assertResponseStatusIs(Response.Status.BAD_REQUEST);
		assertResponseContentTypeIs(TEXT_PLAIN);
	}

	@Test
	public void testValidateRecordInputXmlWrongContentType() {
		stringToExternallyConvertibleConverterSpy.MRV.setAlwaysThrowException("convert",
				new ConverterException("exception from spy"));
		response = recordEndpoint.validateRecordXmlJson(AUTH_TOKEN, AUTH_TOKEN, PLACE, defaultJson);
		assertResponseStatusIs(Response.Status.BAD_REQUEST);
		assertResponseContentTypeIs(TEXT_PLAIN);
	}

	@Test
	public void testAnnotationsForValidateRecordJsonJson() throws Exception {
		AnnotationTestHelper annotationHelper = AnnotationTestHelper
				.createAnnotationTestHelperForClassMethodNameAndNumOfParameters(
						recordEndpoint.getClass(), "validateRecordJsonJson", 4);

		annotationHelper.assertHttpMethodAndPathAnnotation("POST", "{type}");
		annotationHelper.assertConsumesAnnotation(APPLICATION_VND_UUB_WORKORDER_JSON);
		annotationHelper.assertProducesAnnotation(APPLICATION_VND_UUB_RECORD_JSON_QS09);
		annotationHelper.assertAnnotationForAuthTokensAndTypeParameters();
	}

	@Test
	public void testAnnotationsForValidateRecordJsonXml() throws Exception {
		AnnotationTestHelper annotationHelper = AnnotationTestHelper
				.createAnnotationTestHelperForClassMethodNameAndNumOfParameters(
						recordEndpoint.getClass(), "validateRecordJsonXml", 4);

		annotationHelper.assertHttpMethodAndPathAnnotation("POST", "{type}");
		annotationHelper.assertConsumesAnnotation(APPLICATION_VND_UUB_WORKORDER_JSON);
		annotationHelper.assertProducesAnnotation(APPLICATION_VND_UUB_RECORD_XML);
		annotationHelper.assertAnnotationForAuthTokensAndTypeParameters();
	}

	@Test
	public void testAnnotationsForValidateRecordXmlJson() throws Exception {
		AnnotationTestHelper annotationHelper = AnnotationTestHelper
				.createAnnotationTestHelperForClassMethodNameAndNumOfParameters(
						recordEndpoint.getClass(), "validateRecordXmlJson", 4);

		annotationHelper.assertHttpMethodAndPathAnnotation("POST", "{type}");
		annotationHelper.assertConsumesAnnotation(APPLICATION_VND_UUB_WORKORDER_XML);
		annotationHelper.assertProducesAnnotation(APPLICATION_VND_UUB_RECORD_JSON_QS09);
		annotationHelper.assertAnnotationForAuthTokensAndTypeParameters();
	}

	@Test
	public void testAnnotationsForValidateRecordXmlXml() throws Exception {
		AnnotationTestHelper annotationHelper = AnnotationTestHelper
				.createAnnotationTestHelperForClassMethodNameAndNumOfParameters(
						recordEndpoint.getClass(), "validateRecordXmlXml", 4);

		annotationHelper.assertHttpMethodAndPathAnnotation("POST", "{type}");
		annotationHelper.assertConsumesAnnotation(APPLICATION_VND_UUB_WORKORDER_XML);
		annotationHelper.assertProducesAnnotation(APPLICATION_VND_UUB_RECORD_XML);
		annotationHelper.assertAnnotationForAuthTokensAndTypeParameters();
	}

	@Test
	public void testValidateRecordUnauthorized() {
		response = recordEndpoint.validateRecordJsonJson(DUMMY_NON_AUTHORIZED_TOKEN, AUTH_TOKEN,
				"workOrder", jsonToValidate);
		assertResponseStatusIs(Response.Status.FORBIDDEN);
		assertResponseContentTypeIs(TEXT_PLAIN);
	}

	@Test
	public void testValidateRecordTypeNotFound() {
		spiderInstanceFactorySpy.throwRecordNotFoundException = true;
		response = recordEndpoint.validateRecordJsonJson(AUTH_TOKEN, AUTH_TOKEN, "workOrder",
				defaultJson);
		assertResponseStatusIs(Response.Status.NOT_FOUND);
	}

}