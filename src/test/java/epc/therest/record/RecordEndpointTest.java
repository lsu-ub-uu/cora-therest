package epc.therest.record;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import epc.spider.dependency.SpiderInstanceProvider;
import epc.therest.initialize.DependencyProviderForTest;

public class RecordEndpointTest {
	private String jsonToCreateFrom = "{\"authority\":{\"children\":["
			+ "{\"datePeriod\":{\"attributes\":{\"eventType\":\"existence\"},"
			+ "\"children\":[{\"date\":{\"attributes\":{\"datePointEventType\":\"start\"},"
			+ "\"children\":[{\"year\":\"1976\"},{\"month\":\"07\"},{\"day\":\"22\"}]}},"
			+ "{\"date\":{\"attributes\":{\"datePointEventType\":\"end\"},"
			+ "\"children\":[{\"year\":\"2076\"},{\"month\":\"12\"},{\"day\":\"31\"}]}},"
			+ "{\"description\":\"76 - 76\"}]}},{\"name\":{"
			+ "\"attributes\":{\"type\":\"person\",\"nameform\":\"authorized\"},"
			+ "\"children\":[{\"namepart\":{\"attributes\":{\"type\":\"givenname\"},"
			+ "\"children\":[{\"name\":\"Olov\"}]}},{\"namepart\":{"
			+ "\"attributes\":{\"type\":\"familyname\"},\"children\":[{\"name\":\"McKie\"}]}},"
			+ "{\"namepart\":{\"attributes\":{\"type\":\"number\"},"
			+ "\"children\":[{\"name\":\"II\"}]}},{\"namepart\":{\"attributes\":{"
			+ "\"type\":\"addition\"},\"children\":[{\"name\":\"Ett tillägg\"}]}},"
			+ "{\"datePeriod\":{\"attributes\":{\"eventType\":\"valid\"},"
			+ "\"children\":[{\"date\":{\"attributes\":{\"datePointEventType\":\"start\"},"
			+ "\"children\":[{\"year\":\"2008\"},{\"month\":\"06\"},{\"day\":\"28\"}]}},"
			+ "{\"description\":\"Namn som gift\"}]}}]}},{\"name\":{"
			+ "\"attributes\":{\"type\":\"person\",\"nameform\":\"alternative\"},"
			+ "\"children\":[{\"namepart\":{\"attributes\":{\"type\":\"givenname\"},"
			+ "\"children\":[{\"name\":\"Olle\"}]}},{\"namepart\":{"
			+ "\"attributes\":{\"type\":\"familyname\"},"
			+ "\"children\":[{\"name\":\"Nilsson\"}]}}]}},{\"name\":{\"attributes\":{"
			+ "\"type\":\"person\",\"nameform\":\"alternative\"},\"children\":["
			+ "{\"namepart\":{\"attributes\":{\"type\":\"givenname\"},\"children\":["
			+ "{\"name\":\"Olle2\"}]}},{\"namepart\":{\"attributes\":{\"type\":\"familyname\"},"
			+ "\"children\":[{\"name\":\"Nilsson2\"}]}}]}},{\"other\":\"some other stuff\"},"
			+ "{\"other\":\"second other stuff\"},{\"other\":\"third other stuff\"},"
			+ "{\"othercol\":\"yes\"}],\"attributes\":{\"type\":\"place\"}}}";
	private String jsonToUpdateWith = "{\"authority\":{\"children\":["
			+ "{\"recordInfo\":{\"children\":[{\"id\":\"place:0001\"}"
			+ ",{\"type\":\"place\"},{\"createdBy\":\"userId\"}]}},"
			+ "{\"datePeriod\":{\"attributes\":{\"eventType\":\"existence\"},"
			+ "\"children\":[{\"date\":{\"attributes\":{\"datePointEventType\":\"start\"},"
			+ "\"children\":[{\"year\":\"1976\"},{\"month\":\"07\"},{\"day\":\"22\"}]}},"
			+ "{\"date\":{\"attributes\":{\"datePointEventType\":\"end\"},"
			+ "\"children\":[{\"year\":\"2076\"},{\"month\":\"12\"},{\"day\":\"31\"}]}},"
			+ "{\"description\":\"76 - 76\"}]}},{\"name\":{\"attributes\":{\"type\":\"person\","
			+ "\"nameform\":\"authorized\"},"
			+ "\"children\":[{\"namepart\":{\"attributes\":{\"type\":\"givenname\"},"
			+ "\"children\":[{\"name\":\"Olov\"}]}},"
			+ "{\"namepart\":{\"attributes\":{\"type\":\"familyname\"},"
			+ "\"children\":[{\"name\":\"McKie\"}]}},"
			+ "{\"namepart\":{\"attributes\":{\"type\":\"number\"},"
			+ "\"children\":[{\"name\":\"II\"}]}},"
			+ "{\"namepart\":{\"attributes\":{\"type\":\"addition\"},"
			+ "\"children\":[{\"name\":\"Ett tillägg\"}]}},"
			+ "{\"datePeriod\":{\"attributes\":{\"eventType\":\"valid\"},"
			+ "\"children\":[{\"date\":{\"attributes\":{\"datePointEventType\":\"start\"},"
			+ "\"children\":[{\"year\":\"2008\"},{\"month\":\"06\"},{\"day\":\"28\"}]}},"
			+ "{\"description\":\"Namn som gift\"}]}}]}},"
			+ "{\"name\":{\"attributes\":{\"type\":\"person\",\"nameform\":\"alternative\"},"
			+ "\"children\":[{\"namepart\":{\"attributes\":{\"type\":\"givenname\"},"
			+ "\"children\":[{\"name\":\"Olle\"}]}},"
			+ "{\"namepart\":{\"attributes\":{\"type\":\"familyname\"},"
			+ "\"children\":[{\"name\":\"Nilsson\"}]}}]}},"
			+ "{\"name\":{\"attributes\":{\"type\":\"person\"," + "\"nameform\":\"alternative\"},"
			+ "\"children\":[{\"namepart\":{\"attributes\":{\"type\":\"givenname\"},"
			+ "\"children\":[{\"name\":\"Olle2\"}]}},"
			+ "{\"namepart\":{\"attributes\":{\"type\":\"familyname\"},"
			+ "\"children\":[{\"name\":\"Nilsson2\"}]}}]}},{\"other\":\"some other stuff\"},"
			+ "{\"other\":\"second other stuff\"},{\"other\":\"third other stuff\"}," + ""
			+ "{\"othercol\":\"yes\"}],\"attributes\":{\"type\":\"place\"}}}";
	private String jsonToUpdateWithNotFound = "{\"authority\":{\"children\":["
			+ "{\"recordInfo\":{\"children\":[{\"id\":\"place:0001_NOT_FOUND\"}"
			+ ",{\"type\":\"place\"},{\"createdBy\":\"userId\"}]}},"
			+ "{\"datePeriod\":{\"attributes\":{\"eventType\":\"existence\"},"
			+ "\"children\":[{\"date\":{\"attributes\":{\"datePointEventType\":\"start\"},"
			+ "\"children\":[{\"year\":\"1976\"},{\"month\":\"07\"},{\"day\":\"22\"}]}},"
			+ "{\"date\":{\"attributes\":{\"datePointEventType\":\"end\"},"
			+ "\"children\":[{\"year\":\"2076\"},{\"month\":\"12\"},{\"day\":\"31\"}]}},"
			+ "{\"description\":\"76 - 76\"}]}},{\"name\":{\"attributes\":{\"type\":\"person\","
			+ "\"nameform\":\"authorized\"},"
			+ "\"children\":[{\"namepart\":{\"attributes\":{\"type\":\"givenname\"},"
			+ "\"children\":[{\"name\":\"Olov\"}]}},"
			+ "{\"namepart\":{\"attributes\":{\"type\":\"familyname\"},"
			+ "\"children\":[{\"name\":\"McKie\"}]}},"
			+ "{\"namepart\":{\"attributes\":{\"type\":\"number\"},"
			+ "\"children\":[{\"name\":\"II\"}]}},"
			+ "{\"namepart\":{\"attributes\":{\"type\":\"addition\"},"
			+ "\"children\":[{\"name\":\"Ett tillägg\"}]}},"
			+ "{\"datePeriod\":{\"attributes\":{\"eventType\":\"valid\"},"
			+ "\"children\":[{\"date\":{\"attributes\":{\"datePointEventType\":\"start\"},"
			+ "\"children\":[{\"year\":\"2008\"},{\"month\":\"06\"},{\"day\":\"28\"}]}},"
			+ "{\"description\":\"Namn som gift\"}]}}]}},"
			+ "{\"name\":{\"attributes\":{\"type\":\"person\",\"nameform\":\"alternative\"},"
			+ "\"children\":[{\"namepart\":{\"attributes\":{\"type\":\"givenname\"},"
			+ "\"children\":[{\"name\":\"Olle\"}]}},"
			+ "{\"namepart\":{\"attributes\":{\"type\":\"familyname\"},"
			+ "\"children\":[{\"name\":\"Nilsson\"}]}}]}},"
			+ "{\"name\":{\"attributes\":{\"type\":\"person\"," + "\"nameform\":\"alternative\"},"
			+ "\"children\":[{\"namepart\":{\"attributes\":{\"type\":\"givenname\"},"
			+ "\"children\":[{\"name\":\"Olle2\"}]}},"
			+ "{\"namepart\":{\"attributes\":{\"type\":\"familyname\"},"
			+ "\"children\":[{\"name\":\"Nilsson2\"}]}}]}},{\"other\":\"some other stuff\"},"
			+ "{\"other\":\"second other stuff\"},{\"other\":\"third other stuff\"}," + ""
			+ "{\"othercol\":\"yes\"}],\"attributes\":{\"type\":\"place\"}}}";
	private RecordEndpoint recordEndpoint;

	@BeforeMethod
	public void beforeMethod() {
		SpiderInstanceProvider.setSpiderDependencyProvider(new DependencyProviderForTest());
		UriInfo uriInfo = new TestUri();
		recordEndpoint = new RecordEndpoint(uriInfo);
	}

	@Test
	public void testReadRecordList() {
		String type = "place";
		Response response = recordEndpoint.readRecordList(type);
		String entity = (String) response.getEntity();
		assertNotNull(entity, "An entity in json format should be returned");
	}

	@Test
	public void testReadRecordListNotFound() {
		Response response = recordEndpoint.readRecordList("place_NOT_FOUND");
		assertEquals(response.getStatusInfo(), Response.Status.NOT_FOUND);
	}

	@Test
	public void testReadRecordListUnauthorized() {
		Response response = recordEndpoint.readRecordListAsUserIdByType("unauthorizedUserId",
				"place");
		assertEquals(response.getStatusInfo(), Response.Status.UNAUTHORIZED);
	}

	@Test
	public void testReadRecord() {
		Response response = recordEndpoint.readRecord("place", "place:0001");
		String entity = (String) response.getEntity();

		assertNotNull(entity, "An entity in json format should be returned");
	}

	@Test
	public void testReadRecordUnauthorized() {
		Response response = recordEndpoint.readRecordAsUserIdByTypeAndId("unauthorizedUserId",
				"place", "place:0001");
		assertEquals(response.getStatusInfo(), Response.Status.UNAUTHORIZED);
	}

	@Test
	public void testReadRecordNotFound() {
		Response response = recordEndpoint.readRecordAsUserIdByTypeAndId("userId", "place",
				"place:0001_NOT_FOUND");
		assertEquals(response.getStatusInfo(), Response.Status.NOT_FOUND);
	}

	@Test
	public void testDeleteRecord() {
		Response response = recordEndpoint.deleteRecord("place", "place:0001");
		assertEquals(response.getStatusInfo(), Response.Status.OK);
	}

	@Test
	public void testDeleteRecordUnauthorized() {
		Response response = recordEndpoint.deleteRecordAsUserIdByTypeAndId("unauthorizedUserId",
				"place", "place:0001");
		assertEquals(response.getStatusInfo(), Response.Status.UNAUTHORIZED);
	}

	@Test
	public void testDeleteRecordNotFound() {
		Response response = recordEndpoint.deleteRecordAsUserIdByTypeAndId("userId", "place",
				"place:0001_NOT_FOUND");
		assertEquals(response.getStatusInfo(), Response.Status.NOT_FOUND);
	}

	@Test
	public void testUpdateRecord() {
		String type = "place";
		String id = "place:0001";
		Response responseUpdate = recordEndpoint.updateRecord(type, id, jsonToUpdateWith);
		assertEquals(responseUpdate.getStatusInfo(), Response.Status.OK);
	}

	@Test
	public void testUpdateRecordUnauthorized() {
		String type = "place";
		String id = "place:0001";
		Response responseUpdate = recordEndpoint.updateRecordAsUserIdWithRecord(
				"unauthorizedUserId", type, id, jsonToUpdateWith);
		assertEquals(responseUpdate.getStatusInfo(), Response.Status.UNAUTHORIZED);
	}

	@Test
	public void testUpdateRecordNotFound() {
		String type = "place";
		String id = "place:0001";
		Response responseUpdate = recordEndpoint.updateRecord(type, id + "_NOT_FOUND",
				jsonToUpdateWithNotFound);
		assertEquals(responseUpdate.getStatusInfo(), Response.Status.NOT_FOUND);
	}

	@Test
	public void testUpdateRecordBadJson() {
		String type = "place";
		String id = "place:0001";
		String json = "{\"groupDataId\":{\"children\":[{\"atomicDataId\":\"atomicValue\""
				+ ",\"atomicDataId2\":\"atomicValue2\"}]}}";
		Response responseUpdate = recordEndpoint.updateRecord(type, id, json);
		assertEquals(responseUpdate.getStatusInfo(), Response.Status.BAD_REQUEST);
	}

	@Test
	public void testCreateRecord() {
		String type = "place";
		Response responseCreated = recordEndpoint.createRecord(type, jsonToCreateFrom);
		assertEquals(responseCreated.getStatusInfo(), Response.Status.CREATED);
		assertTrue(responseCreated.getLocation().toString().startsWith("record/" + type));
	}

	@Test()
	public void testCreateRecordBadCreatedLocation() {
		String type = "place&& &&\\\\";
		Response responseCreated = recordEndpoint.createRecord(type, jsonToCreateFrom);
		assertEquals(responseCreated.getStatusInfo(), Response.Status.BAD_REQUEST);
	}

	@Test
	public void testCreateRecordBadJson() {
		String type = "place";
		String json = "{\"groupDataId\":{\"children\":[{\"atomicDataId\":\"atomicValue\""
				+ ",\"atomicDataId2\":\"atomicValue2\"}]}}";
		Response responseCreated = recordEndpoint.createRecord(type, json);
		assertEquals(responseCreated.getStatusInfo(), Response.Status.BAD_REQUEST);
	}

	@Test
	public void testCreateRecordUnauthorized() {
		String type = "place";
		Response responseUpdate = recordEndpoint.createRecordAsUserIdWithRecord(
				"unauthorizedUserId", type, jsonToCreateFrom);
		assertEquals(responseUpdate.getStatusInfo(), Response.Status.UNAUTHORIZED);
	}

	@Test
	public void testCreateNonExcistingRecordType() {
		String type = "recordType_NON_EXCISTING";
		Response responseUpdate = recordEndpoint.createRecordAsUserIdWithRecord(
				"unauthorizedUserId", type, jsonToCreateFrom);
		assertEquals(responseUpdate.getStatusInfo(), Response.Status.BAD_REQUEST);
	}

	@Test
	public void testCreateRecordNotValid() {
		// uses always invalid validator
		DependencyProviderForTest spiderDependencyProvider = new DependencyProviderForTest();
		spiderDependencyProvider.setDataValidator(new DataValidatorAlwaysInvalidSpy());
		SpiderInstanceProvider.setSpiderDependencyProvider(spiderDependencyProvider);

		String type = "place";
		Response responseCreated = recordEndpoint.createRecord(type, jsonToCreateFrom);
		assertEquals(responseCreated.getStatusInfo(), Response.Status.BAD_REQUEST);
	}
}
