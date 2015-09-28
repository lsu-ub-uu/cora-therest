package epc.therest.record;

import epc.spider.dependency.SpiderInstanceProvider;
import epc.therest.initialize.DependencyProviderForTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import static org.testng.Assert.*;

public class RecordEndpointTest {
	private String jsonToCreateFrom = "{\"name\":\"authority\",\"children\":[{\"name\":\"datePeriod\",\"attributes\":{\"eventType\":\"existence\"},\"children\":[{\"name\":\"date\",\"attributes\":{\"datePointEventType\":\"start\"},\"children\":[{\"year\":\"1976\"},{\"month\":\"07\"},{\"day\":\"22\"}]},{\"name\":\"date\",\"attributes\":{\"datePointEventType\":\"end\"},\"children\":[{\"year\":\"2076\"},{\"month\":\"12\"},{\"day\":\"31\"}]},{\"description\":\"76 - 76\"}]},{\"name\":\"name\",\"attributes\":{\"type\":\"person\",\"nameform\":\"authorized\"},\"children\":[{\"name\":\"namepart\",\"attributes\":{\"type\":\"givenname\"},\"children\":[{\"name\":\"Olov\"}]},{\"name\":\"namepart\",\"attributes\":{\"type\":\"familyname\"},\"children\":[{\"name\":\"McKie\"}]},{\"name\":\"namepart\",\"attributes\":{\"type\":\"number\"},\"children\":[{\"name\":\"II\"}]},{\"name\":\"namepart\",\"attributes\":{\"type\":\"addition\"},\"children\":[{\"name\":\"Ett till�gg\"}]},{\"name\":\"datePeriod\",\"attributes\":{\"eventType\":\"valid\"},\"children\":[{\"name\":\"date\",\"attributes\":{\"datePointEventType\":\"start\"},\"children\":[{\"year\":\"2008\"},{\"month\":\"06\"},{\"day\":\"28\"}]},{\"description\":\"Namn som gift\"}]}]},{\"name\":\"name\",\"attributes\":{\"type\":\"person\",\"nameform\":\"alternative\"},\"children\":[{\"name\":\"namepart\",\"attributes\":{\"type\":\"givenname\"},\"children\":[{\"name\":\"Olle\"}]},{\"name\":\"namepart\",\"attributes\":{\"type\":\"familyname\"},\"children\":[{\"name\":\"Nilsson\"}]}]},{\"name\":\"name\",\"attributes\":{\"type\":\"person\",\"nameform\":\"alternative\"},\"children\":[{\"name\":\"namepart\",\"attributes\":{\"type\":\"givenname\"},\"children\":[{\"name\":\"Olle2\"}]},{\"name\":\"namepart\",\"attributes\":{\"type\":\"familyname\"},\"children\":[{\"name\":\"Nilsson2\"}]}]},{\"other\":\"some other stuff\"},{\"other\":\"second other stuff\"},{\"other\":\"third other stuff\"},{\"othercol\":\"yes\"}],\"attributes\":{\"type\":\"place\"}}";
	private String jsonToUpdateWith = "{\"name\":\"authority\",\"children\":[{\"name\":\"recordInfo\",\"children\":[{\"id\":\"place:0001\"},{\"type\":\"place\"},{\"createdBy\":\"userId\"}]},{\"name\":\"datePeriod\",\"attributes\":{\"eventType\":\"existence\"},\"children\":[{\"name\":\"date\",\"attributes\":{\"datePointEventType\":\"start\"},\"children\":[{\"year\":\"1976\"},{\"month\":\"07\"},{\"day\":\"22\"}]},{\"name\":\"date\",\"attributes\":{\"datePointEventType\":\"end\"},\"children\":[{\"year\":\"2076\"},{\"month\":\"12\"},{\"day\":\"31\"}]},{\"description\":\"76 - 76\"}]},{\"name\":\"name\",\"attributes\":{\"type\":\"person\",\"nameform\":\"authorized\"},\"children\":[{\"name\":\"namepart\",\"attributes\":{\"type\":\"givenname\"},\"children\":[{\"name\":\"Olov\"}]},{\"name\":\"namepart\",\"attributes\":{\"type\":\"familyname\"},\"children\":[{\"name\":\"McKie\"}]},{\"name\":\"namepart\",\"attributes\":{\"type\":\"number\"},\"children\":[{\"name\":\"II\"}]},{\"name\":\"namepart\",\"attributes\":{\"type\":\"addition\"},\"children\":[{\"name\":\"Ett tillägg\"}]},{\"name\":\"datePeriod\",\"attributes\":{\"eventType\":\"valid\"},\"children\":[{\"name\":\"date\",\"attributes\":{\"datePointEventType\":\"start\"},\"children\":[{\"year\":\"2008\"},{\"month\":\"06\"},{\"day\":\"28\"}]},{\"description\":\"Namn som gift\"}]}]},{\"name\":\"name\",\"attributes\":{\"type\":\"person\",\"nameform\":\"alternative\"},\"children\":[{\"name\":\"namepart\",\"attributes\":{\"type\":\"givenname\"},\"children\":[{\"name\":\"Olle\"}]},{\"name\":\"namepart\",\"attributes\":{\"type\":\"familyname\"},\"children\":[{\"name\":\"Nilsson\"}]}]},{\"name\":\"name\",\"attributes\":{\"type\":\"person\",\"nameform\":\"alternative\"},\"children\":[{\"name\":\"namepart\",\"attributes\":{\"type\":\"givenname\"},\"children\":[{\"name\":\"Olle2\"}]},{\"name\":\"namepart\",\"attributes\":{\"type\":\"familyname\"},\"children\":[{\"name\":\"Nilsson2\"}]}]},{\"other\":\"some other stuff\"},{\"other\":\"second other stuff\"},{\"other\":\"third other stuff\"},{\"othercol\":\"yes\"}],\"attributes\":{\"type\":\"place\"}}";

	private String jsonToUpdateWithNotFound = "{\"name\":\"authority\",\"children\":[{\"name\":\"recordInfo\",\"children\":[{\"id\":\"place:0001_NOT_FOUND\"},{\"type\":\"place\"},{\"createdBy\":\"userId\"}]},{\"name\":\"datePeriod\",\"attributes\":{\"eventType\":\"existence\"},\"children\":[{\"name\":\"date\",\"attributes\":{\"datePointEventType\":\"start\"},\"children\":[{\"year\":\"1976\"},{\"month\":\"07\"},{\"day\":\"22\"}]},{\"name\":\"date\",\"attributes\":{\"datePointEventType\":\"end\"},\"children\":[{\"year\":\"2076\"},{\"month\":\"12\"},{\"day\":\"31\"}]},{\"description\":\"76 - 76\"}]},{\"name\":\"name\",\"attributes\":{\"type\":\"person\",\"nameform\":\"authorized\"},\"children\":[{\"name\":\"namepart\",\"attributes\":{\"type\":\"givenname\"},\"children\":[{\"name\":\"Olov\"}]},{\"name\":\"namepart\",\"attributes\":{\"type\":\"familyname\"},\"children\":[{\"name\":\"McKie\"}]},{\"name\":\"namepart\",\"attributes\":{\"type\":\"number\"},\"children\":[{\"name\":\"II\"}]},{\"name\":\"namepart\",\"attributes\":{\"type\":\"addition\"},\"children\":[{\"name\":\"Ett tillägg\"}]},{\"name\":\"datePeriod\",\"attributes\":{\"eventType\":\"valid\"},\"children\":[{\"name\":\"date\",\"attributes\":{\"datePointEventType\":\"start\"},\"children\":[{\"year\":\"2008\"},{\"month\":\"06\"},{\"day\":\"28\"}]},{\"description\":\"Namn som gift\"}]}]},{\"name\":\"name\",\"attributes\":{\"type\":\"person\",\"nameform\":\"alternative\"},\"children\":[{\"name\":\"namepart\",\"attributes\":{\"type\":\"givenname\"},\"children\":[{\"name\":\"Olle\"}]},{\"name\":\"namepart\",\"attributes\":{\"type\":\"familyname\"},\"children\":[{\"name\":\"Nilsson\"}]}]},{\"name\":\"name\",\"attributes\":{\"type\":\"person\",\"nameform\":\"alternative\"},\"children\":[{\"name\":\"namepart\",\"attributes\":{\"type\":\"givenname\"},\"children\":[{\"name\":\"Olle2\"}]},{\"name\":\"namepart\",\"attributes\":{\"type\":\"familyname\"},\"children\":[{\"name\":\"Nilsson2\"}]}]},{\"other\":\"some other stuff\"},{\"other\":\"second other stuff\"},{\"other\":\"third other stuff\"},{\"othercol\":\"yes\"}],\"attributes\":{\"type\":\"place\"}}";
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
	public void testReadRecordAbstractRecordType() {
		String type = "abstract";
		Response responseRead = recordEndpoint.readRecord(type, jsonToCreateFrom);
		assertEquals(responseRead.getStatusInfo(), Response.Status.METHOD_NOT_ALLOWED);
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
	public void testDeleteRecordAbstractRecordType() {
		String type = "abstract";
		Response responseDeleted = recordEndpoint.deleteRecord(type, jsonToCreateFrom);
		assertEquals(responseDeleted.getStatusInfo(), Response.Status.METHOD_NOT_ALLOWED);
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
		Response responseUpdate = recordEndpoint
				.updateRecordAsUserIdWithRecord("unauthorizedUserId", type, id, jsonToUpdateWith);
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
	public void testUpdateRecordBadContentInJson() {
		String type = "place";
		String id = "place:0001";
		String json = "{\"groupNameInData\":{\"children\":[{\"atomicNameInData\":\"atomicValue\""
				+ ",\"atomicNameInData2\":\"atomicValue2\"}]}}";
		Response responseUpdate = recordEndpoint.updateRecord(type, id, json);
		assertEquals(responseUpdate.getStatusInfo(), Response.Status.BAD_REQUEST);
	}

	@Test
	public void testUpdateRecordAbstractRecordType() {
		String type = "abstract";
		Response responseUpdated = recordEndpoint.updateRecord(type, "anIdNotImportant",
				jsonToCreateFrom);
		assertEquals(responseUpdated.getStatusInfo(), Response.Status.METHOD_NOT_ALLOWED);
	}

	@Test
	public void testCreateRecord() {
		String type = "place";
		Response responseCreated = recordEndpoint.createRecord(type, jsonToCreateFrom);
		assertEquals(responseCreated.getStatusInfo(), Response.Status.CREATED);
		assertTrue(responseCreated.getLocation().toString().startsWith("record/" + type));
	}

	@Test
	public void testCreateRecordBadCreatedLocation() {
		String type = "place&& &&\\\\";
		Response responseCreated = recordEndpoint.createRecord(type, jsonToCreateFrom);
		assertEquals(responseCreated.getStatusInfo(), Response.Status.BAD_REQUEST);
	}

	@Test
	public void testCreateRecordBadContentInJson() {
		String type = "place";
		String json = "{\"groupNameInData\":{\"children\":[{\"atomicNameInData\":\"atomicValue\""
				+ ",\"atomicNameInData2\":\"atomicValue2\"}]}}";
		Response responseCreated = recordEndpoint.createRecord(type, json);
		assertEquals(responseCreated.getStatusInfo(), Response.Status.BAD_REQUEST);
	}

	@Test
	public void testCreateRecordUnauthorized() {
		String type = "place";
		Response responseUpdate = recordEndpoint
				.createRecordAsUserIdWithRecord("unauthorizedUserId", type, jsonToCreateFrom);
		assertEquals(responseUpdate.getStatusInfo(), Response.Status.UNAUTHORIZED);
	}

	@Test
	public void testCreateNonExistingRecordType() {
		String type = "recordType_NON_EXCISTING";
		Response responseUpdate = recordEndpoint
				.createRecordAsUserIdWithRecord("unauthorizedUserId", type, jsonToCreateFrom);
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

	@Test
	public void testCreateRecordAbstractRecordType() {
		String type = "abstract";
		Response responseCreated = recordEndpoint.createRecord(type, jsonToCreateFrom);
		assertEquals(responseCreated.getStatusInfo(), Response.Status.METHOD_NOT_ALLOWED);
	}

	String duplicateTestJson="{\"name\":\"place\",\"children\":["
			+ "{\"name\":\"recordInfo\",\"children\":["
			+ "{\"id\":\"aPlace\"}]}"
			+ ",{\"id\":\"anythingGoes\"}]}";
	@Test
	public void testCreateRecordDuplicateUserSuppliedId() {
		String type = "place";
		Response responseCreated = recordEndpoint.createRecord(type, duplicateTestJson);
		assertEquals(responseCreated.getStatusInfo(), Response.Status.CREATED);
		
		Response responseCreated2 = recordEndpoint.createRecord(type, duplicateTestJson);
		assertEquals(responseCreated2.getStatusInfo(), Response.Status.CONFLICT);
		
	}
}
