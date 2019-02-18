/*
 * Copyright 2015, 2016, 2018 Uppsala University Library
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
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition.FormDataContentDispositionBuilder;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.spider.data.SpiderDataGroup;
import se.uu.ub.cora.spider.dependency.SpiderInstanceProvider;

public class RecordEndpointTest {
	private static final String DUMMY_NON_AUTHORIZED_TOKEN = "dummyNonAuthorizedToken";
	private static final String PLACE_0001 = "place:0001";
	private static final String PLACE = "place";
	private static final String AUTH_TOKEN = "authToken";
	private String jsonToCreateFrom = "{\"name\":\"authority\",\"children\":[{\"name\":\"recordInfo\",\"children\":[{\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"system\"},{\"name\":\"linkedRecordId\",\"value\":\"cora\"}],\"name\":\"dataDivider\"}]},{\"name\":\"datePeriod\",\"attributes\":{\"eventType\":\"existence\"},\"children\":[{\"name\":\"date\",\"attributes\":{\"datePointEventType\":\"start\"},\"children\":[{\"name\":\"year\",\"value\":\"1976\"},{\"name\":\"month\",\"value\":\"07\"},{\"name\":\"day\",\"value\":\"22\"}]},{\"name\":\"date\",\"attributes\":{\"datePointEventType\":\"end\"},\"children\":[{\"name\":\"year\",\"value\":\"2076\"},{\"name\":\"month\",\"value\":\"12\"},{\"name\":\"day\",\"value\":\"31\"}]},{\"name\":\"description\",\"value\":\"76 - 76\"}]},{\"name\":\"name\",\"attributes\":{\"type\":\"person\",\"nameform\":\"authorized\"},\"children\":[{\"name\":\"namepart\",\"attributes\":{\"type\":\"givenname\"},\"children\":[{\"name\":\"name\",\"value\":\"Olov\"}]},{\"name\":\"namepart\",\"attributes\":{\"type\":\"familyname\"},\"children\":[{\"name\":\"name\",\"value\":\"McKie\"}]},{\"name\":\"namepart\",\"attributes\":{\"type\":\"number\"},\"children\":[{\"name\":\"name\",\"value\":\"II\"}]},{\"name\":\"namepart\",\"attributes\":{\"type\":\"addition\"},\"children\":[{\"name\":\"name\",\"value\":\"Ett tillägg\"}]},{\"name\":\"datePeriod\",\"attributes\":{\"eventType\":\"valid\"},\"children\":[{\"name\":\"date\",\"attributes\":{\"datePointEventType\":\"start\"},\"children\":[{\"name\":\"year\",\"value\":\"2008\"},{\"name\":\"month\",\"value\":\"06\"},{\"name\":\"day\",\"value\":\"28\"}]},{\"name\":\"description\",\"value\":\"Namn som gift\"}]}]},{\"name\":\"name\",\"attributes\":{\"type\":\"person\",\"nameform\":\"alternative\"},\"children\":[{\"name\":\"namepart\",\"attributes\":{\"type\":\"givenname\"},\"children\":[{\"name\":\"name\",\"value\":\"Olle\"}]},{\"name\":\"namepart\",\"attributes\":{\"type\":\"familyname\"},\"children\":[{\"name\":\"name\",\"value\":\"Nilsson\"}]}]},{\"name\":\"name\",\"attributes\":{\"type\":\"person\",\"nameform\":\"alternative\"},\"children\":[{\"name\":\"namepart\",\"attributes\":{\"type\":\"givenname\"},\"children\":[{\"name\":\"name\",\"value\":\"Olle2\"}]},{\"name\":\"namepart\",\"attributes\":{\"type\":\"familyname\"},\"children\":[{\"name\":\"name\",\"value\":\"Nilsson2\"}]}]},{\"name\":\"other\",\"value\":\"some other stuff\"},{\"name\":\"other\",\"value\":\"second other stuff\"},{\"name\":\"other\",\"value\":\"third other stuff\"},{\"name\":\"othercol\",\"value\":\"yes\"}],\"attributes\":{\"type\":\"place\"}}";
	private String jsonToCreateFromConversionException = "{\"name\":\"authority\",\"children\":[{\"type\":\"NOT_CORRECT\"},{\"name\":\"recordInfo\",\"children\":[{\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"system\"},{\"name\":\"linkedRecordId\",\"value\":\"cora\"}],\"name\":\"dataDivider\"}]},{\"name\":\"datePeriod\",\"attributes\":{\"eventType\":\"existence\"},\"children\":[{\"name\":\"date\",\"attributes\":{\"datePointEventType\":\"start\"},\"children\":[{\"name\":\"year\",\"value\":\"1976\"},{\"name\":\"month\",\"value\":\"07\"},{\"name\":\"day\",\"value\":\"22\"}]},{\"name\":\"date\",\"attributes\":{\"datePointEventType\":\"end\"},\"children\":[{\"name\":\"year\",\"value\":\"2076\"},{\"name\":\"month\",\"value\":\"12\"},{\"name\":\"day\",\"value\":\"31\"}]},{\"name\":\"description\",\"value\":\"76 - 76\"}]},{\"name\":\"name\",\"attributes\":{\"type\":\"person\",\"nameform\":\"authorized\"},\"children\":[{\"name\":\"namepart\",\"attributes\":{\"type\":\"givenname\"},\"children\":[{\"name\":\"name\",\"value\":\"Olov\"}]},{\"name\":\"namepart\",\"attributes\":{\"type\":\"familyname\"},\"children\":[{\"name\":\"name\",\"value\":\"McKie\"}]},{\"name\":\"namepart\",\"attributes\":{\"type\":\"number\"},\"children\":[{\"name\":\"name\",\"value\":\"II\"}]},{\"name\":\"namepart\",\"attributes\":{\"type\":\"addition\"},\"children\":[{\"name\":\"name\",\"value\":\"Ett tillägg\"}]},{\"name\":\"datePeriod\",\"attributes\":{\"eventType\":\"valid\"},\"children\":[{\"name\":\"date\",\"attributes\":{\"datePointEventType\":\"start\"},\"children\":[{\"name\":\"year\",\"value\":\"2008\"},{\"name\":\"month\",\"value\":\"06\"},{\"name\":\"day\",\"value\":\"28\"}]},{\"name\":\"description\",\"value\":\"Namn som gift\"}]}]},{\"name\":\"name\",\"attributes\":{\"type\":\"person\",\"nameform\":\"alternative\"},\"children\":[{\"name\":\"namepart\",\"attributes\":{\"type\":\"givenname\"},\"children\":[{\"name\":\"name\",\"value\":\"Olle\"}]},{\"name\":\"namepart\",\"attributes\":{\"type\":\"familyname\"},\"children\":[{\"name\":\"name\",\"value\":\"Nilsson\"}]}]},{\"name\":\"name\",\"attributes\":{\"type\":\"person\",\"nameform\":\"alternative\"},\"children\":[{\"name\":\"namepart\",\"attributes\":{\"type\":\"givenname\"},\"children\":[{\"name\":\"name\",\"value\":\"Olle2\"}]},{\"name\":\"namepart\",\"attributes\":{\"type\":\"familyname\"},\"children\":[{\"name\":\"name\",\"value\":\"Nilsson2\"}]}]},{\"name\":\"other\",\"value\":\"some other stuff\"},{\"name\":\"other\",\"value\":\"second other stuff\"},{\"name\":\"other\",\"value\":\"third other stuff\"},{\"name\":\"othercol\",\"value\":\"yes\"}],\"attributes\":{\"type\":\"place\"}}";
	private String jsonToCreateFromAttributeAsChild = "{\"name\":\"authority\",\"children\":[{\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"system\"},{\"name\":\"linkedRecordId\",\"value\":\"cora\"}],\"name\":\"dataDivider\"}]},{\"name\":\"datePeriod\",\"attributes\":{\"eventType\":\"existence\"},\"children\":[{\"name\":\"date\",\"attributes\":{\"datePointEventType\":\"start\"},\"children\":[{\"year\":\"1976\"},{\"name\":\"month\",\"value\":\"07\"},{\"name\":\"day\",\"value\":\"22\"}]},{\"name\":\"date\",\"attributes\":{\"datePointEventType\":\"end\"},\"children\":[{\"name\":\"year\",\"value\":\"2076\"},{\"name\":\"month\",\"value\":\"12\"},{\"name\":\"day\",\"value\":\"31\"}]},{\"name\":\"description\",\"value\":\"76 - 76\"}]},{\"name\":\"name\",\"attributes\":{\"type\":\"person\",\"nameform\":\"authorized\"},\"children\":[{\"name\":\"namepart\",\"attributes\":{\"type\":\"givenname\"},\"children\":[{\"name\":\"name\",\"value\":\"Olov\"}]},{\"name\":\"namepart\",\"attributes\":{\"type\":\"familyname\"},\"children\":[{\"name\":\"name\",\"value\":\"McKie\"}]},{\"name\":\"namepart\",\"attributes\":{\"type\":\"number\"},\"children\":[{\"name\":\"name\",\"value\":\"II\"}]},{\"name\":\"namepart\",\"attributes\":{\"type\":\"addition\"},\"children\":[{\"name\":\"name\",\"value\":\"Ett tillägg\"}]},{\"name\":\"datePeriod\",\"attributes\":{\"eventType\":\"valid\"},\"children\":[{\"name\":\"date\",\"attributes\":{\"datePointEventType\":\"start\"},\"children\":[{\"name\":\"year\",\"value\":\"2008\"},{\"name\":\"month\",\"value\":\"06\"},{\"name\":\"day\",\"value\":\"28\"}]},{\"name\":\"description\",\"value\":\"Namn som gift\"}]}]},{\"name\":\"name\",\"attributes\":{\"type\":\"person\",\"nameform\":\"alternative\"},\"children\":[{\"name\":\"namepart\",\"attributes\":{\"type\":\"givenname\"},\"children\":[{\"name\":\"name\",\"value\":\"Olle\"}]},{\"name\":\"namepart\",\"attributes\":{\"type\":\"familyname\"},\"children\":[{\"name\":\"name\",\"value\":\"Nilsson\"}]}]},{\"name\":\"name\",\"attributes\":{\"type\":\"person\",\"nameform\":\"alternative\"},\"children\":[{\"name\":\"namepart\",\"attributes\":{\"type\":\"givenname\"},\"children\":[{\"name\":\"name\",\"value\":\"Olle2\"}]},{\"name\":\"namepart\",\"attributes\":{\"type\":\"familyname\"},\"children\":[{\"name\":\"name\",\"value\":\"Nilsson2\"}]}]},{\"name\":\"other\",\"value\":\"some other stuff\"},{\"name\":\"other\",\"value\":\"second other stuff\"},{\"name\":\"other\",\"value\":\"third other stuff\"},{\"name\":\"othercol\",\"value\":\"yes\"}],\"attributes\":{\"type\":\"place\"}}";
	private String jsonToUpdateWith = "{\"name\":\"authority\",\"children\":[{\"name\":\"recordInfo\",\"children\":[{\"name\":\"id\",\"value\":\"place:0001\"},{\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"recordType\"},{\"name\":\"linkedRecordId\",\"value\":\"place\"}],\"name\":\"type\"},{\"name\":\"createdBy\",\"value\":\"userId\"},{\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"system\"},{\"name\":\"linkedRecordId\",\"value\":\"cora\"}],\"name\":\"dataDivider\"}]},{\"name\":\"datePeriod\",\"attributes\":{\"eventType\":\"existence\"},\"children\":[{\"name\":\"date\",\"attributes\":{\"datePointEventType\":\"start\"},\"children\":[{\"name\":\"year\",\"value\":\"1976\"},{\"name\":\"month\",\"value\":\"07\"},{\"name\":\"day\",\"value\":\"22\"}]},{\"name\":\"date\",\"attributes\":{\"datePointEventType\":\"end\"},\"children\":[{\"name\":\"year\",\"value\":\"2076\"},{\"name\":\"month\",\"value\":\"12\"},{\"name\":\"day\",\"value\":\"31\"}]},{\"name\":\"description\",\"value\":\"76 - 76\"}]},{\"name\":\"name\",\"attributes\":{\"type\":\"person\",\"nameform\":\"authorized\"},\"children\":[{\"name\":\"namepart\",\"attributes\":{\"type\":\"givenname\"},\"children\":[{\"name\":\"name\",\"value\":\"Olov\"}]},{\"name\":\"namepart\",\"attributes\":{\"type\":\"familyname\"},\"children\":[{\"name\":\"name\",\"value\":\"McKie\"}]},{\"name\":\"namepart\",\"attributes\":{\"type\":\"number\"},\"children\":[{\"name\":\"name\",\"value\":\"II\"}]},{\"name\":\"namepart\",\"attributes\":{\"type\":\"addition\"},\"children\":[{\"name\":\"name\",\"value\":\"Ett tillägg\"}]},{\"name\":\"datePeriod\",\"attributes\":{\"eventType\":\"valid\"},\"children\":[{\"name\":\"date\",\"attributes\":{\"datePointEventType\":\"start\"},\"children\":[{\"name\":\"year\",\"value\":\"2008\"},{\"name\":\"month\",\"value\":\"06\"},{\"name\":\"day\",\"value\":\"28\"}]},{\"name\":\"description\",\"value\":\"Namn som gift\"}]}]},{\"name\":\"name\",\"attributes\":{\"type\":\"person\",\"nameform\":\"alternative\"},\"children\":[{\"name\":\"namepart\",\"attributes\":{\"type\":\"givenname\"},\"children\":[{\"name\":\"name\",\"value\":\"Olle\"}]},{\"name\":\"namepart\",\"attributes\":{\"type\":\"familyname\"},\"children\":[{\"name\":\"name\",\"value\":\"Nilsson\"}]}]},{\"name\":\"name\",\"attributes\":{\"type\":\"person\",\"nameform\":\"alternative\"},\"children\":[{\"name\":\"namepart\",\"attributes\":{\"type\":\"givenname\"},\"children\":[{\"name\":\"name\",\"value\":\"Olle2\"}]},{\"name\":\"namepart\",\"attributes\":{\"type\":\"familyname\"},\"children\":[{\"name\":\"name\",\"value\":\"Nilsson2\"}]}]},{\"name\":\"other\",\"value\":\"some other stuff\"},{\"name\":\"other\",\"value\":\"second other stuff\"},{\"name\":\"other\",\"value\":\"third other stuff\"},{\"name\":\"othercol\",\"value\":\"yes\"}],\"attributes\":{\"type\":\"place\"}}";
	private String jsonToUpdateWithAttributeAsChild = "{\"name\":\"authority\",\"children\":[{\"name\":\"recordInfo\",\"children\":[{\"id\":\"place:0001\"},{\"name\":\"type\",\"value\":\"place\"},{\"name\":\"createdBy\",\"value\":\"userId\"}]},{\"name\":\"datePeriod\",\"attributes\":{\"eventType\":\"existence\"},\"children\":[{\"name\":\"date\",\"attributes\":{\"datePointEventType\":\"start\"},\"children\":[{\"name\":\"year\",\"value\":\"1976\"},{\"name\":\"month\",\"value\":\"07\"},{\"name\":\"day\",\"value\":\"22\"}]},{\"name\":\"date\",\"attributes\":{\"datePointEventType\":\"end\"},\"children\":[{\"name\":\"year\",\"value\":\"2076\"},{\"name\":\"month\",\"value\":\"12\"},{\"name\":\"day\",\"value\":\"31\"}]},{\"name\":\"description\",\"value\":\"76 - 76\"}]},{\"name\":\"name\",\"attributes\":{\"type\":\"person\",\"nameform\":\"authorized\"},\"children\":[{\"name\":\"namepart\",\"attributes\":{\"type\":\"givenname\"},\"children\":[{\"name\":\"name\",\"value\":\"Olov\"}]},{\"name\":\"namepart\",\"attributes\":{\"type\":\"familyname\"},\"children\":[{\"name\":\"name\",\"value\":\"McKie\"}]},{\"name\":\"namepart\",\"attributes\":{\"type\":\"number\"},\"children\":[{\"name\":\"name\",\"value\":\"II\"}]},{\"name\":\"namepart\",\"attributes\":{\"type\":\"addition\"},\"children\":[{\"name\":\"name\",\"value\":\"Ett tillägg\"}]},{\"name\":\"datePeriod\",\"attributes\":{\"eventType\":\"valid\"},\"children\":[{\"name\":\"date\",\"attributes\":{\"datePointEventType\":\"start\"},\"children\":[{\"name\":\"year\",\"value\":\"2008\"},{\"name\":\"month\",\"value\":\"06\"},{\"name\":\"day\",\"value\":\"28\"}]},{\"name\":\"description\",\"value\":\"Namn som gift\"}]}]},{\"name\":\"name\",\"attributes\":{\"type\":\"person\",\"nameform\":\"alternative\"},\"children\":[{\"name\":\"namepart\",\"attributes\":{\"type\":\"givenname\"},\"children\":[{\"name\":\"name\",\"value\":\"Olle\"}]},{\"name\":\"namepart\",\"attributes\":{\"type\":\"familyname\"},\"children\":[{\"name\":\"name\",\"value\":\"Nilsson\"}]}]},{\"name\":\"name\",\"attributes\":{\"type\":\"person\",\"nameform\":\"alternative\"},\"children\":[{\"name\":\"namepart\",\"attributes\":{\"type\":\"givenname\"},\"children\":[{\"name\":\"name\",\"value\":\"Olle2\"}]},{\"name\":\"namepart\",\"attributes\":{\"type\":\"familyname\"},\"children\":[{\"name\":\"name\",\"value\":\"Nilsson2\"}]}]},{\"name\":\"other\",\"value\":\"some other stuff\"},{\"name\":\"other\",\"value\":\"second other stuff\"},{\"name\":\"other\",\"value\":\"third other stuff\"},{\"name\":\"othercol\",\"value\":\"yes\"}],\"attributes\":{\"type\":\"place\"}}";
	private String jsonToUpdateWithNotFound = "{\"name\":\"authority\",\"children\":[{\"name\":\"recordInfo\",\"children\":[{\"name\":\"id\",\"value\":\"place:0001_NOT_FOUND\"},{\"name\":\"type\",\"value\":\"place\"},{\"name\":\"createdBy\",\"value\":\"userId\"}]},{\"name\":\"datePeriod\",\"attributes\":{\"eventType\":\"existence\"},\"children\":[{\"name\":\"date\",\"attributes\":{\"datePointEventType\":\"start\"},\"children\":[{\"name\":\"year\",\"value\":\"1976\"},{\"name\":\"month\",\"value\":\"07\"},{\"name\":\"day\",\"value\":\"22\"}]},{\"name\":\"date\",\"attributes\":{\"datePointEventType\":\"end\"},\"children\":[{\"name\":\"year\",\"value\":\"2076\"},{\"name\":\"month\",\"value\":\"12\"},{\"name\":\"day\",\"value\":\"31\"}]},{\"name\":\"description\",\"value\":\"76 - 76\"}]},{\"name\":\"name\",\"attributes\":{\"type\":\"person\",\"nameform\":\"authorized\"},\"children\":[{\"name\":\"namepart\",\"attributes\":{\"type\":\"givenname\"},\"children\":[{\"name\":\"name\",\"value\":\"Olov\"}]},{\"name\":\"namepart\",\"attributes\":{\"type\":\"familyname\"},\"children\":[{\"name\":\"name\",\"value\":\"McKie\"}]},{\"name\":\"namepart\",\"attributes\":{\"type\":\"number\"},\"children\":[{\"name\":\"name\",\"value\":\"II\"}]},{\"name\":\"namepart\",\"attributes\":{\"type\":\"addition\"},\"children\":[{\"name\":\"name\",\"value\":\"Ett tillägg\"}]},{\"name\":\"datePeriod\",\"attributes\":{\"eventType\":\"valid\"},\"children\":[{\"name\":\"date\",\"attributes\":{\"datePointEventType\":\"start\"},\"children\":[{\"name\":\"year\",\"value\":\"2008\"},{\"name\":\"month\",\"value\":\"06\"},{\"name\":\"day\",\"value\":\"28\"}]},{\"name\":\"description\",\"value\":\"Namn som gift\"}]}]},{\"name\":\"name\",\"attributes\":{\"type\":\"person\",\"nameform\":\"alternative\"},\"children\":[{\"name\":\"namepart\",\"attributes\":{\"type\":\"givenname\"},\"children\":[{\"name\":\"name\",\"value\":\"Olle\"}]},{\"name\":\"namepart\",\"attributes\":{\"type\":\"familyname\"},\"children\":[{\"name\":\"name\",\"value\":\"Nilsson\"}]}]},{\"name\":\"name\",\"attributes\":{\"type\":\"person\",\"nameform\":\"alternative\"},\"children\":[{\"name\":\"namepart\",\"attributes\":{\"type\":\"givenname\"},\"children\":[{\"name\":\"name\",\"value\":\"Olle2\"}]},{\"name\":\"namepart\",\"attributes\":{\"type\":\"familyname\"},\"children\":[{\"name\":\"name\",\"value\":\"Nilsson2\"}]}]},{\"name\":\"other\",\"value\":\"some other stuff\"},{\"name\":\"other\",\"value\":\"second other stuff\"},{\"name\":\"other\",\"value\":\"third other stuff\"},{\"name\":\"othercol\",\"value\":\"yes\"}],\"attributes\":{\"type\":\"place\"}}";
	private String jsonWithBadContent = "{\"groupNameInData\":{\"children\":[{\"atomicNameInData\":\"atomicValue\""
			+ ",\"atomicNameInData2\":\"atomicValue2\"}]}}";
	private String jsonSearchData = "{\"name\":\"search\",\"children\":[{\"name\":\"include\",\""
			+ "children\":[{\"name\":\"includePart\",\"children\":[{\"name\":\"text\",\"value\":\"\"}]}]}]}";
	private String jsonFilterData = "{\"name\":\"filter\",\"children\":[{\"name\":\"part\",\"children\":[{\"name\":\"key\",\"value\":\"movieTitle\"},{\"name\":\"value\",\"value\":\"Some title\"}],\"repeatId\":\"0\"}]}";
	private String jsonToValidate = "{\"validationInfo\":{\"name\":\"validationOrder\",\"children\":[{\"name\":\"recordInfo\",\"children\":[{\"name\":\"dataDivider\",\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"system\"},{\"name\":\"linkedRecordId\",\"value\":\"testSystem\"}]}]},{\"name\":\"recordType\",\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"recordType\"},{\"name\":\"linkedRecordId\",\"value\":\"someRecordType\"}]},{\"name\":\"metadataToValidate\",\"value\":\"existing\"},{\"name\":\"validateLinks\",\"value\":\"false\"}]},\"record\":{\"name\":\"text\",\"children\":[{\"name\":\"recordInfo\",\"children\":[{\"name\":\"id\",\"value\":\"workOrderRecordIdTextVar2Text\"},{\"name\":\"dataDivider\",\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"system\"},{\"name\":\"linkedRecordId\",\"value\":\"cora\"}]}]},{\"name\":\"textPart\",\"children\":[{\"name\":\"text\",\"value\":\"Id på länkad post\"}],\"attributes\":{\"type\":\"default\",\"lang\":\"sv\"}},{\"name\":\"textPart\",\"children\":[{\"name\":\"text\",\"value\":\"Linked record id\"}],\"attributes\":{\"type\":\"alternative\",\"lang\":\"en\"}}]}}";
	private RecordEndpoint recordEndpoint;
	private SpiderInstanceFactorySpy factorySpy;
	private Response response;
	private TestHttpServletRequest request;
	private Map<String, String> initInfo = new HashMap<>();

	@BeforeMethod
	public void beforeMethod() {
		initInfo.put("theRestPublicPathToSystem", "/systemone/rest/");
		factorySpy = new SpiderInstanceFactorySpy();
		SpiderInstanceProvider.setSpiderInstanceFactory(factorySpy);
		SpiderInstanceProvider.setInitInfo(initInfo);

		request = new TestHttpServletRequest();
		recordEndpoint = new RecordEndpoint(request);
	}

	@Test
	public void testXForwardedProtoHttps() {
		request.headers.put("X-Forwarded-Proto", "https");
		recordEndpoint = new RecordEndpoint(request);

		response = recordEndpoint.readRecord(AUTH_TOKEN, AUTH_TOKEN, PLACE, PLACE_0001);
		String entityString = response.getEntity().toString();
		assertTrue(entityString.contains("\"url\":\"https:/"));
	}

	@Test
	public void testXForwardedProtoHttpsWhenAlreadyHttpsInRequestUrl() {
		request.headers.put("X-Forwarded-Proto", "https");
		request.requestURL = new StringBuffer(
				"https://cora.epc.ub.uu.se/systemone/rest/record/text/");
		recordEndpoint = new RecordEndpoint(request);

		response = recordEndpoint.readRecord(AUTH_TOKEN, AUTH_TOKEN, PLACE, PLACE_0001);
		String entityString = response.getEntity().toString();
		assertTrue(entityString.contains("\"url\":\"https:/"));
	}

	@Test
	public void testXForwardedProtoEmpty() {
		request.headers.put("X-Forwarded-Proto", "");
		recordEndpoint = new RecordEndpoint(request);

		response = recordEndpoint.readRecord(AUTH_TOKEN, AUTH_TOKEN, PLACE, PLACE_0001);
		String entityString = response.getEntity().toString();
		assertTrue(entityString.contains("\"url\":\"http:/"));
	}

	@Test
	public void testReturnedUrl() {
		request.headers.put("X-Forwarded-Proto", "https");
		recordEndpoint = new RecordEndpoint(request);

		response = recordEndpoint.readRecord(AUTH_TOKEN, AUTH_TOKEN, PLACE, PLACE_0001);
		String entityString = response.getEntity().toString();
		assertTrue(entityString
				.contains("\"url\":\"https://cora.epc.ub.uu.se/systemone/rest/record/place/"));
	}

	@Test
	public void testPreferredTokenForReadList() throws IOException {
		expectTokenForReadListToPrefereblyBeHeaderThanQuery(AUTH_TOKEN, "authToken2", AUTH_TOKEN);
		expectTokenForReadListToPrefereblyBeHeaderThanQuery(null, AUTH_TOKEN, AUTH_TOKEN);
		expectTokenForReadListToPrefereblyBeHeaderThanQuery(AUTH_TOKEN, null, AUTH_TOKEN);
		expectTokenForReadListToPrefereblyBeHeaderThanQuery(null, null, null);
	}

	private void expectTokenForReadListToPrefereblyBeHeaderThanQuery(String headerAuthToken,
			String queryAuthToken, String authTokenExpected) {
		String jsonFilter = "{\"name\":\"filter\",\"children\":[]}";
		response = recordEndpoint.readRecordList(headerAuthToken, queryAuthToken, PLACE,
				jsonFilter);

		SpiderRecordListReaderSpy spiderListReaderSpy = factorySpy.spiderRecordListReaderSpy;
		assertEquals(spiderListReaderSpy.authToken, authTokenExpected);
	}

	@Test
	public void testReadRecordListWithFilter() {
		response = recordEndpoint.readRecordList(AUTH_TOKEN, AUTH_TOKEN, PLACE, jsonFilterData);
		assertEntityExists();
		assertResponseStatusIs(Response.Status.OK);

		SpiderRecordListReaderSpy spiderListReaderSpy = factorySpy.spiderRecordListReaderSpy;

		SpiderDataGroup filterData = spiderListReaderSpy.filter;
		assertEquals(filterData.getNameInData(), "filter");
		SpiderDataGroup part = filterData.extractGroup("part");
		assertTrue(part.containsChildWithNameInData("key"));
		assertTrue(part.containsChildWithNameInData("value"));
	}

	@Test
	public void testReadRecordListWithNullAsFilter() {
		response = recordEndpoint.readRecordList(AUTH_TOKEN, AUTH_TOKEN, PLACE, null);
		assertEntityExists();
		assertResponseStatusIs(Response.Status.OK);

		SpiderRecordListReaderSpy spiderListReaderSpy = factorySpy.spiderRecordListReaderSpy;

		SpiderDataGroup filterData = spiderListReaderSpy.filter;
		assertEquals(filterData.getNameInData(), "filter");
		assertFalse(filterData.containsChildWithNameInData("part"));
	}

	private void assertEntityExists() {
		assertNotNull(response.getEntity(), "An entity in json format should be returned");
	}

	private void assertResponseStatusIs(Status responseStatus) {
		assertEquals(response.getStatusInfo(), responseStatus);
	}

	@Test
	public void testReadRecordListNotFound() {
		String jsonFilter = "{\"name\":\"filter\",\"children\":[]}";
		response = recordEndpoint.readRecordList(AUTH_TOKEN, AUTH_TOKEN, "place_NOT_FOUND",
				jsonFilter);
		assertResponseStatusIs(Response.Status.NOT_FOUND);
	}

	@Test
	public void testReadRecordListUnauthorized() {
		String jsonFilter = "{\"name\":\"filter\",\"children\":[]}";
		response = recordEndpoint.readRecordListUsingAuthTokenByType(DUMMY_NON_AUTHORIZED_TOKEN,
				PLACE, jsonFilter);
		assertResponseStatusIs(Response.Status.FORBIDDEN);
	}

	@Test
	public void testReadRecordListNoTokenAndUnauthorized() {
		String jsonFilter = "{\"name\":\"filter\",\"children\":[]}";
		response = recordEndpoint.readRecordListUsingAuthTokenByType(null, PLACE, jsonFilter);
		assertResponseStatusIs(Response.Status.UNAUTHORIZED);
	}

	@Test
	public void testPreferredTokenForRead() throws IOException {
		expectTokenForReadToPreferablyBeHeaderThanQuery(AUTH_TOKEN, "authToken2", AUTH_TOKEN);
		expectTokenForReadToPreferablyBeHeaderThanQuery(null, AUTH_TOKEN, AUTH_TOKEN);
		expectTokenForReadToPreferablyBeHeaderThanQuery(AUTH_TOKEN, null, AUTH_TOKEN);
		expectTokenForReadToPreferablyBeHeaderThanQuery(null, null, null);
	}

	private void expectTokenForReadToPreferablyBeHeaderThanQuery(String headerAuthToken,
			String queryAuthToken, String authTokenExpected) {

		response = recordEndpoint.readRecord(headerAuthToken, queryAuthToken, PLACE, PLACE_0001);

		SpiderRecordReaderSpy spiderReaderSpy = factorySpy.spiderRecordReaderSpy;
		assertEquals(spiderReaderSpy.authToken, authTokenExpected);
	}

	@Test
	public void testReadRecord() {
		response = recordEndpoint.readRecord(AUTH_TOKEN, AUTH_TOKEN, PLACE, PLACE_0001);
		assertEntityExists();
		assertResponseStatusIs(Response.Status.OK);
	}

	@Test
	public void testReadRecordUnauthenticated() {
		response = recordEndpoint.readRecordUsingAuthTokenByTypeAndId("dummyNonAuthenticatedToken",
				PLACE, PLACE_0001);
		assertResponseStatusIs(Response.Status.UNAUTHORIZED);
	}

	@Test
	public void testReadRecordUnauthorized() {
		response = recordEndpoint.readRecordUsingAuthTokenByTypeAndId(DUMMY_NON_AUTHORIZED_TOKEN,
				PLACE, PLACE_0001);
		assertResponseStatusIs(Response.Status.FORBIDDEN);
	}

	@Test
	public void testReadRecordNotFound() {
		response = recordEndpoint.readRecord(AUTH_TOKEN, AUTH_TOKEN, PLACE, "place:0001_NOT_FOUND");
		assertResponseStatusIs(Response.Status.NOT_FOUND);
	}

	@Test
	public void testReadRecordAbstractRecordType() {
		response = recordEndpoint.readRecord(AUTH_TOKEN, AUTH_TOKEN, "binary", "image:123456789");
		assertResponseStatusIs(Response.Status.OK);
	}

	@Test
	public void testPreferredTokenForReadIncomingLinks() throws IOException {
		expectTokenForReadIncomingLinksToPrefereblyBeHeaderThanQuery(AUTH_TOKEN, "authToken2",
				AUTH_TOKEN);
		expectTokenForReadIncomingLinksToPrefereblyBeHeaderThanQuery(null, AUTH_TOKEN, AUTH_TOKEN);
		expectTokenForReadIncomingLinksToPrefereblyBeHeaderThanQuery(AUTH_TOKEN, null, AUTH_TOKEN);
		expectTokenForReadIncomingLinksToPrefereblyBeHeaderThanQuery(null, null, null);
	}

	private void expectTokenForReadIncomingLinksToPrefereblyBeHeaderThanQuery(
			String headerAuthToken, String queryAuthToken, String authTokenExpected) {

		response = recordEndpoint.readIncomingRecordLinks(headerAuthToken, queryAuthToken, PLACE,
				PLACE_0001);

		SpiderRecordIncomingLinksReaderSpy spiderIncomingLinksReaderSpy = factorySpy.spiderRecordIncomingLinksReaderSpy;
		assertEquals(spiderIncomingLinksReaderSpy.authToken, authTokenExpected);
	}

	@Test
	public void testReadIncomingRecordLinks() {
		response = recordEndpoint.readIncomingRecordLinks(AUTH_TOKEN, AUTH_TOKEN, PLACE,
				PLACE_0001);

		assertResponseStatusIs(Response.Status.OK);
	}

	@Test
	public void testReadIncomingLinksUnauthorized() {
		response = recordEndpoint.readIncomingRecordLinksUsingAuthTokenByTypeAndId(
				DUMMY_NON_AUTHORIZED_TOKEN, PLACE, PLACE_0001);
		assertResponseStatusIs(Response.Status.FORBIDDEN);
	}

	@Test
	public void testReadIncomingLinksNotFound() {
		response = recordEndpoint.readIncomingRecordLinks(AUTH_TOKEN, AUTH_TOKEN, PLACE,
				"place:0001_NOT_FOUND");
		assertResponseStatusIs(Response.Status.NOT_FOUND);
	}

	@Test
	public void testReadIncomingLinksAbstractRecordType() {
		String type = "abstract";
		response = recordEndpoint.readIncomingRecordLinks(AUTH_TOKEN, AUTH_TOKEN, type,
				"canBeWhatEverIdTypeIsChecked");
		assertResponseStatusIs(Response.Status.METHOD_NOT_ALLOWED);
	}

	@Test
	public void testPreferredTokenForDelete() throws IOException {
		expectTokenForDeleteToPreferablyBeHeaderThanQuery(AUTH_TOKEN, "authToken2", AUTH_TOKEN);
		expectTokenForDeleteToPreferablyBeHeaderThanQuery(null, AUTH_TOKEN, AUTH_TOKEN);
		expectTokenForDeleteToPreferablyBeHeaderThanQuery(AUTH_TOKEN, null, AUTH_TOKEN);
		expectTokenForDeleteToPreferablyBeHeaderThanQuery(null, null, null);
	}

	private void expectTokenForDeleteToPreferablyBeHeaderThanQuery(String headerAuthToken,
			String queryAuthToken, String authTokenExpected) {

		response = recordEndpoint.deleteRecord(headerAuthToken, queryAuthToken, PLACE,
				"place:0002");

		SpiderRecordDeleterSpy spiderDeleterSpy = factorySpy.spiderRecordDeleterSpy;
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
		response = recordEndpoint.deleteRecordUsingAuthTokenByTypeAndId(DUMMY_NON_AUTHORIZED_TOKEN,
				PLACE, PLACE_0001);
		assertResponseStatusIs(Response.Status.FORBIDDEN);
	}

	@Test
	public void testDeleteRecordNotFound() {
		response = recordEndpoint.deleteRecordUsingAuthTokenByTypeAndId("someToken78678567", PLACE,
				"place:0001_NOT_FOUND");
		assertResponseStatusIs(Response.Status.NOT_FOUND);
	}

	@Test
	public void testPreferredTokenForUpdate() throws IOException {
		expectTokenForUpdateToPrefereblyBeHeaderThanQuery(AUTH_TOKEN, "authToken2", AUTH_TOKEN);
		expectTokenForUpdateToPrefereblyBeHeaderThanQuery(null, AUTH_TOKEN, AUTH_TOKEN);
		expectTokenForUpdateToPrefereblyBeHeaderThanQuery(AUTH_TOKEN, null, AUTH_TOKEN);
		expectTokenForUpdateToPrefereblyBeHeaderThanQuery(null, null, null);
	}

	private void expectTokenForUpdateToPrefereblyBeHeaderThanQuery(String headerAuthToken,
			String queryAuthToken, String authTokenExpected) {

		response = recordEndpoint.updateRecord(headerAuthToken, queryAuthToken, PLACE, PLACE_0001,
				jsonToUpdateWith);

		SpiderRecordUpdaterSpy spiderUpdaterSpy = factorySpy.spiderRecordUpdaterSpy;
		assertEquals(spiderUpdaterSpy.authToken, authTokenExpected);
	}

	@Test
	public void testUpdateRecord() {
		response = recordEndpoint.updateRecord(AUTH_TOKEN, AUTH_TOKEN, PLACE, PLACE_0001,
				jsonToUpdateWith);
		assertResponseStatusIs(Response.Status.OK);
	}

	@Test
	public void testUpdateRecordUnauthorized() {
		response = recordEndpoint.updateRecordUsingAuthTokenWithRecord(DUMMY_NON_AUTHORIZED_TOKEN,
				PLACE, PLACE_0001, jsonToUpdateWith);
		assertResponseStatusIs(Response.Status.FORBIDDEN);
	}

	@Test
	public void testUpdateRecordNotFound() {
		response = recordEndpoint.updateRecord(AUTH_TOKEN, AUTH_TOKEN, PLACE,
				PLACE_0001 + "_NOT_FOUND", jsonToUpdateWithNotFound);
		assertResponseStatusIs(Response.Status.NOT_FOUND);
	}

	@Test
	public void testUpdateRecordTypeNotFound() {
		response = recordEndpoint.updateRecord(AUTH_TOKEN, AUTH_TOKEN, PLACE + "_NOT_FOUND",
				PLACE_0001, jsonToUpdateWithNotFound);
		assertResponseStatusIs(Response.Status.NOT_FOUND);
	}

	@Test
	public void testUpdateRecordBadContentInJson() {
		response = recordEndpoint.updateRecord(AUTH_TOKEN, AUTH_TOKEN, PLACE, PLACE_0001,
				jsonWithBadContent);
		assertResponseStatusIs(Response.Status.BAD_REQUEST);
	}

	@Test
	public void testUpdateRecordWrongDataTypeInJson() {
		response = recordEndpoint.updateRecord(AUTH_TOKEN, AUTH_TOKEN, PLACE, PLACE_0001,
				jsonToUpdateWithAttributeAsChild);
		assertResponseStatusIs(Response.Status.BAD_REQUEST);
	}

	@Test
	public void testPreferredTokenForCreate() throws IOException {
		expectTokenForCreateToPreferablyBeHeaderThanQuery(AUTH_TOKEN, "authToken2", AUTH_TOKEN);
		expectTokenForCreateToPreferablyBeHeaderThanQuery(null, AUTH_TOKEN, AUTH_TOKEN);
		expectTokenForCreateToPreferablyBeHeaderThanQuery(AUTH_TOKEN, null, AUTH_TOKEN);
		expectTokenForCreateToPreferablyBeHeaderThanQuery(null, null, null);
	}

	private void expectTokenForCreateToPreferablyBeHeaderThanQuery(String headerAuthToken,
			String queryAuthToken, String authTokenExpected) {

		response = recordEndpoint.createRecord(headerAuthToken, queryAuthToken, PLACE,
				jsonToCreateFrom);

		SpiderCreatorSpy spiderCreatorSpy = factorySpy.spiderCreatorSpy;
		assertEquals(spiderCreatorSpy.authToken, authTokenExpected);
	}

	@Test
	public void testCreateRecord() {
		response = recordEndpoint.createRecord(AUTH_TOKEN, AUTH_TOKEN, PLACE, jsonToCreateFrom);
		assertResponseStatusIs(Response.Status.CREATED);
		assertTrue(response.getLocation().toString().startsWith("record/" + PLACE));
	}

	@Test
	public void testCreateRecordBadCreatedLocation() {
		String type = "place&& &&\\\\";
		response = recordEndpoint.createRecord(AUTH_TOKEN, AUTH_TOKEN, type, jsonToCreateFrom);
		assertResponseStatusIs(Response.Status.BAD_REQUEST);
	}

	@Test
	public void testCreateRecordBadContentInJson() {
		response = recordEndpoint.createRecord(AUTH_TOKEN, AUTH_TOKEN, PLACE, jsonWithBadContent);
		assertResponseStatusIs(Response.Status.BAD_REQUEST);
	}

	@Test
	public void testCreateRecordUnauthorized() {
		response = recordEndpoint.createRecordUsingAuthTokenWithRecord(DUMMY_NON_AUTHORIZED_TOKEN,
				PLACE, jsonToCreateFrom);
		assertResponseStatusIs(Response.Status.FORBIDDEN);
	}

	@Test
	public void testCreateNonExistingRecordType() {
		String type = "recordType_NON_EXISTING";
		response = recordEndpoint.createRecordUsingAuthTokenWithRecord(AUTH_TOKEN, type,
				jsonToCreateFrom);
		assertResponseStatusIs(Response.Status.NOT_FOUND);
	}

	@Test
	public void testCreateRecordNotValid() {
		response = recordEndpoint.createRecord(AUTH_TOKEN, AUTH_TOKEN, "place_NON_VALID",
				jsonToCreateFrom);
		assertResponseStatusIs(Response.Status.BAD_REQUEST);
	}

	@Test
	public void testCreateRecordConversionException() {
		response = recordEndpoint.createRecordUsingAuthTokenWithRecord("someToken78678567", PLACE,
				jsonToCreateFromConversionException);
		assertResponseStatusIs(Response.Status.BAD_REQUEST);
	}

	@Test
	public void testCreateRecordAttributeAsChild() {
		response = recordEndpoint.createRecord(AUTH_TOKEN, AUTH_TOKEN, PLACE,
				jsonToCreateFromAttributeAsChild);
		assertResponseStatusIs(Response.Status.BAD_REQUEST);
	}

	@Test
	public void testCreateRecordAbstractRecordType() {
		String type = "abstract";
		response = recordEndpoint.createRecord(AUTH_TOKEN, AUTH_TOKEN, type, jsonToCreateFrom);
		assertResponseStatusIs(Response.Status.METHOD_NOT_ALLOWED);
	}

	String duplicateTestJson = "{\"name\":\"place\",\"children\":["
			+ "{\"name\":\"recordInfo\",\"children\":["
			+ "{\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"system\"},"
			+ "{\"name\":\"linkedRecordId\",\"value\":\"cora\"}],\"name\":\"dataDivider\"},"
			+ "{\"name\":\"id\",\"value\":\"aPlace\"}]}"
			+ ",{\"name\":\"id\",\"value\":\"anythingGoes\"}]}";

	@Test
	public void testCreateRecordDuplicateUserSuppliedId() {
		response = recordEndpoint.createRecord(AUTH_TOKEN, AUTH_TOKEN, "place_duplicate",
				duplicateTestJson);
		assertResponseStatusIs(Response.Status.CONFLICT);

	}

	@Test
	public void testCreateRecordUnexpectedError() {
		response = recordEndpoint.createRecord(AUTH_TOKEN, AUTH_TOKEN, "place_unexpected_error",
				jsonToCreateFrom);
		assertResponseStatusIs(Response.Status.INTERNAL_SERVER_ERROR);
	}

	@Test
	public void testPreferredTokenForUpload() throws IOException {
		expectTokenForUploadToPrefereblyBeHeaderThanQuery(AUTH_TOKEN, "authToken2", AUTH_TOKEN);
		expectTokenForUploadToPrefereblyBeHeaderThanQuery(null, AUTH_TOKEN, AUTH_TOKEN);
		expectTokenForUploadToPrefereblyBeHeaderThanQuery(AUTH_TOKEN, null, AUTH_TOKEN);
		expectTokenForUploadToPrefereblyBeHeaderThanQuery(null, null, null);
	}

	private void expectTokenForUploadToPrefereblyBeHeaderThanQuery(String headerAuthToken,
			String queryAuthToken, String authTokenExpected) {

		FormDataContentDispositionBuilder builder = FormDataContentDisposition
				.name("multipart;form-data");
		builder.fileName("adele1.png");
		FormDataContentDisposition formDataContentDisposition = builder.build();
		InputStream stream = new ByteArrayInputStream("a string".getBytes(StandardCharsets.UTF_8));

		response = recordEndpoint.uploadFile(headerAuthToken, queryAuthToken, "image",
				"image:123456789", stream, formDataContentDisposition);

		SpiderUploaderSpy spiderUploaderSpy = factorySpy.spiderUploaderSpy;
		assertEquals(spiderUploaderSpy.authToken, authTokenExpected);
	}

	@Test
	public void testUpload() throws ParseException {
		InputStream stream = new ByteArrayInputStream("a string".getBytes(StandardCharsets.UTF_8));

		FormDataContentDispositionBuilder builder = FormDataContentDisposition
				.name("multipart;form-data");
		builder.fileName("adele1.png");
		FormDataContentDisposition formDataContentDisposition = builder.build();

		response = recordEndpoint.uploadFile(AUTH_TOKEN, AUTH_TOKEN, "image", "image:123456789",
				stream, formDataContentDisposition);

		assertResponseStatusIs(Response.Status.OK);
	}

	@Test
	public void testUploadUnauthorized() {
		InputStream stream = new ByteArrayInputStream("a string".getBytes(StandardCharsets.UTF_8));

		response = recordEndpoint.uploadFileUsingAuthTokenWithStream(DUMMY_NON_AUTHORIZED_TOKEN,
				"image", "image:123456789", stream, "someFile.tif");

		assertResponseStatusIs(Response.Status.FORBIDDEN);
	}

	@Test
	public void testUploadNotFound() {
		InputStream stream = new ByteArrayInputStream("a string".getBytes(StandardCharsets.UTF_8));

		FormDataContentDispositionBuilder builder = FormDataContentDisposition
				.name("multipart;form-data");
		builder.fileName("adele1.png");
		FormDataContentDisposition formDataContentDisposition = builder.build();

		response = recordEndpoint.uploadFile(AUTH_TOKEN, AUTH_TOKEN, "image",
				"image:123456789_NOT_FOUND", stream, formDataContentDisposition);

		assertResponseStatusIs(Response.Status.NOT_FOUND);
	}

	@Test
	public void testUploadNotAChildOfBinary() {
		InputStream stream = new ByteArrayInputStream("a string".getBytes(StandardCharsets.UTF_8));

		FormDataContentDispositionBuilder builder = FormDataContentDisposition
				.name("multipart;form-data");
		builder.fileName("adele1.png");
		FormDataContentDisposition formDataContentDisposition = builder.build();

		response = recordEndpoint.uploadFile(AUTH_TOKEN, AUTH_TOKEN, "not_child_of_binary_type",
				"image:123456789", stream, formDataContentDisposition);

		assertResponseStatusIs(Response.Status.METHOD_NOT_ALLOWED);
	}

	@Test
	public void testUploadStreamMissing() {
		FormDataContentDispositionBuilder builder = FormDataContentDisposition
				.name("multipart;form-data");
		builder.fileName("adele1.png");
		FormDataContentDisposition formDataContentDisposition = builder.build();

		response = recordEndpoint.uploadFile(AUTH_TOKEN, AUTH_TOKEN, "image", "image:123456789",
				null, formDataContentDisposition);

		assertResponseStatusIs(Response.Status.BAD_REQUEST);
	}

	@Test
	public void testPreferredTokenForDownload() throws IOException {
		expectTokenForDownloadToPrefereblyBeHeaderThanQuery(AUTH_TOKEN, "authToken2", AUTH_TOKEN);
		expectTokenForDownloadToPrefereblyBeHeaderThanQuery(null, AUTH_TOKEN, AUTH_TOKEN);
		expectTokenForDownloadToPrefereblyBeHeaderThanQuery(AUTH_TOKEN, null, AUTH_TOKEN);
		expectTokenForDownloadToPrefereblyBeHeaderThanQuery(null, null, null);
	}

	private void expectTokenForDownloadToPrefereblyBeHeaderThanQuery(String headerAuthToken,
			String queryAuthToken, String authTokenExpected) {

		response = recordEndpoint.downloadFile(headerAuthToken, queryAuthToken, "image",
				"image:123456789", "master");

		SpiderDownloaderSpy spiderDownloaderSpy = factorySpy.spiderDownloaderSpy;
		assertEquals(spiderDownloaderSpy.authToken, authTokenExpected);
	}

	@Test
	public void testDownloadUnauthorized() throws IOException {
		response = recordEndpoint.downloadFileUsingAuthTokenWithStream(DUMMY_NON_AUTHORIZED_TOKEN,
				"image", "image:123456789", "master");
		assertResponseStatusIs(Response.Status.FORBIDDEN);
	}

	@Test
	public void testDownload() throws IOException {
		response = recordEndpoint.downloadFile(AUTH_TOKEN, AUTH_TOKEN, "image", "image:123456789",
				"master");
		String contentType = response.getHeaderString("Content-Type");
		/*
		 * when we detect and store type of file in spider check it like this
		 * assertEquals(contentType, "application/octet-stream");
		 */
		assertEquals(contentType, "application/octet-stream");
		// assertEquals(contentType, null);
		InputStream stream = (InputStream) response.getEntity();
		assertNotNull(stream);

		ByteArrayOutputStream result = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int length;
		while ((length = stream.read(buffer)) != -1) {
			result.write(buffer, 0, length);
		}
		String stringFromStream = result.toString("UTF-8");

		assertEquals(stringFromStream, "a string out");
		assertResponseStatusIs(Response.Status.OK);

		String contentLength = response.getHeaderString("Content-Length");
		assertEquals(contentLength, "12");

		String contentDisposition = response.getHeaderString("Content-Disposition");
		assertEquals(contentDisposition, "attachment; filename=someFile");
	}

	@Test
	public void testDownloadNotFound() throws IOException {
		response = recordEndpoint.downloadFile(AUTH_TOKEN, AUTH_TOKEN, "image",
				"image:123456789_NOT_FOUND", "master");
		assertResponseStatusIs(Response.Status.NOT_FOUND);
	}

	@Test
	public void testDownloadNotAChildOfBinary() throws IOException {
		response = recordEndpoint.downloadFile(AUTH_TOKEN, AUTH_TOKEN, "not_child_of_binary_type",
				"image:123456789", "master");
		assertResponseStatusIs(Response.Status.METHOD_NOT_ALLOWED);
	}

	@Test
	public void testDownloadBadRequest() throws IOException {
		response = recordEndpoint.downloadFile(AUTH_TOKEN, AUTH_TOKEN, "image", "image:123456789",
				"");
		assertResponseStatusIs(Response.Status.BAD_REQUEST);
	}

	@Test
	public void testSearchRecordInputsReachSpider() {
		response = recordEndpoint.searchRecord(AUTH_TOKEN, AUTH_TOKEN, "aSearchId", jsonSearchData);
		SpiderRecordSearcherSpy spiderRecordSearcherSpy = factorySpy.spiderRecordSearcherSpy;
		assertEquals(spiderRecordSearcherSpy.authToken, AUTH_TOKEN);
		assertEquals(spiderRecordSearcherSpy.searchId, "aSearchId");
		SpiderDataGroup searchData = spiderRecordSearcherSpy.searchData;
		assertEquals(searchData.getNameInData(), "search");
		SpiderDataGroup include = (SpiderDataGroup) searchData
				.getFirstChildWithNameInData("include");
		SpiderDataGroup includePart = (SpiderDataGroup) include
				.getFirstChildWithNameInData("includePart");
		assertTrue(includePart.containsChildWithNameInData("text"));
	}

	@Test
	public void testSearchRecordRightTokenInputsReachSpider() {
		response = recordEndpoint.searchRecord(AUTH_TOKEN, null, "aSearchId", jsonSearchData);
		SpiderRecordSearcherSpy spiderRecordSearcherSpy = factorySpy.spiderRecordSearcherSpy;
		assertEquals(spiderRecordSearcherSpy.authToken, AUTH_TOKEN);
	}

	@Test
	public void testSearchRecordRightTokenInputsReachSpider2() {
		response = recordEndpoint.searchRecord(null, AUTH_TOKEN, "aSearchId", jsonSearchData);
		SpiderRecordSearcherSpy spiderRecordSearcherSpy = factorySpy.spiderRecordSearcherSpy;
		assertEquals(spiderRecordSearcherSpy.authToken, AUTH_TOKEN);
	}

	@Test
	public void testSearchRecordRightTokenInputsReachSpider3() {
		response = recordEndpoint.searchRecord(AUTH_TOKEN, "otherAuthToken", "aSearchId",
				jsonSearchData);
		SpiderRecordSearcherSpy spiderRecordSearcherSpy = factorySpy.spiderRecordSearcherSpy;
		assertEquals(spiderRecordSearcherSpy.authToken, AUTH_TOKEN);
	}

	@Test
	public void testSearchRecord() {
		response = recordEndpoint.searchRecord(AUTH_TOKEN, AUTH_TOKEN, "aSearchId", jsonSearchData);
		assertEntityExists();
		assertResponseStatusIs(Response.Status.OK);
		String expectedJson = "{\"dataList\":{\"fromNo\":\"0\",\"data\":[],\"totalNo\":\"1\","
				+ "\"containDataOfType\":\"mix\",\"toNo\":\"1\"}}";
		assertEquals(response.getEntity(), expectedJson);
	}

	@Test
	public void testSearchRecordSearchIdNotFound() {
		response = recordEndpoint.searchRecord(AUTH_TOKEN, AUTH_TOKEN, "aSearchId_NOT_FOUND",
				jsonSearchData);
		assertResponseStatusIs(Response.Status.NOT_FOUND);
	}

	@Test
	public void testSearchRecordInvalidSearchData() {
		response = recordEndpoint.searchRecord(AUTH_TOKEN, AUTH_TOKEN, "aSearchId_INVALID_DATA",
				jsonSearchData);
		assertResponseStatusIs(Response.Status.BAD_REQUEST);
	}

	@Test
	public void testSearchRecordUnauthorized() {
		response = recordEndpoint.searchRecord(DUMMY_NON_AUTHORIZED_TOKEN,
				DUMMY_NON_AUTHORIZED_TOKEN, "aSearchId", jsonSearchData);
		assertResponseStatusIs(Response.Status.FORBIDDEN);
	}

	@Test
	public void testSearchRecordUnauthenticated() {
		response = recordEndpoint.searchRecord("nonExistingToken", "nonExistingToken", "aSearchId",
				jsonSearchData);
		assertResponseStatusIs(Response.Status.UNAUTHORIZED);
	}

	@Test
	public void testPreferredTokenForValidate() throws IOException {
		expectTokenForValidateToPrefereblyBeHeaderThanQuery(AUTH_TOKEN, "authToken2", AUTH_TOKEN);
		expectTokenForValidateToPrefereblyBeHeaderThanQuery(null, AUTH_TOKEN, AUTH_TOKEN);
		expectTokenForValidateToPrefereblyBeHeaderThanQuery(AUTH_TOKEN, null, AUTH_TOKEN);
		expectTokenForValidateToPrefereblyBeHeaderThanQuery(null, null, null);
	}

	private void expectTokenForValidateToPrefereblyBeHeaderThanQuery(String headerAuthToken,
			String queryAuthToken, String authTokenExpected) {

		response = recordEndpoint.validateRecord(headerAuthToken, queryAuthToken, PLACE,
				jsonToValidate);

		SpiderRecordValidatorSpy spiderRecordValidatorSpy = factorySpy.spiderRecordValidatorSpy;

		assertEquals(spiderRecordValidatorSpy.authToken, authTokenExpected);
	}

	@Test
	public void testValidateRecord() {
		response = recordEndpoint.validateRecord(AUTH_TOKEN, AUTH_TOKEN, PLACE, jsonToValidate);

		SpiderRecordValidatorSpy spiderRecordValidatorSpy = factorySpy.spiderRecordValidatorSpy;
		SpiderDataGroup validationRecord = spiderRecordValidatorSpy.validationRecord;
		assertEquals(validationRecord.getNameInData(), "validationOrder");

		SpiderDataGroup recordToValidate = spiderRecordValidatorSpy.recordToValidate;
		assertEquals(recordToValidate.getNameInData(), "text");

		assertResponseStatusIs(Response.Status.OK);
	}

	@Test
	public void testValidateRecordUnauthorized() {
		response = recordEndpoint.validateRecordUsingAuthTokenWithRecord(DUMMY_NON_AUTHORIZED_TOKEN,
				PLACE, jsonToValidate);
		assertResponseStatusIs(Response.Status.FORBIDDEN);
	}

	@Test
	public void testValidateRecordTypeNotFound() {
		response = recordEndpoint.validateRecord(AUTH_TOKEN, AUTH_TOKEN, "recordType_NON_EXISTING",
				jsonToValidate);
		assertResponseStatusIs(Response.Status.NOT_FOUND);
	}

	@Test
	public void testValidateRecordBadContentInJson() {
		response = recordEndpoint.validateRecord(AUTH_TOKEN, AUTH_TOKEN, PLACE, jsonWithBadContent);
		assertResponseStatusIs(Response.Status.BAD_REQUEST);
	}

}