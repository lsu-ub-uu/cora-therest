package se.uu.ub.cora.therest.record;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import se.uu.ub.cora.spider.dependency.SpiderInstanceProvider;
import se.uu.ub.cora.therest.initialize.DependencyProviderForTest;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import static org.testng.Assert.*;

public class RecordEndpointTest {
	private String jsonToCreateFrom = "{\"name\":\"authority\",\"children\":[{\"name\":\"datePeriod\",\"attributes\":{\"eventType\":\"existence\"},\"children\":[{\"name\":\"date\",\"attributes\":{\"datePointEventType\":\"start\"},\"children\":[{\"name\":\"year\",\"value\":\"1976\"},{\"name\":\"month\",\"value\":\"07\"},{\"name\":\"day\",\"value\":\"22\"}]},{\"name\":\"date\",\"attributes\":{\"datePointEventType\":\"end\"},\"children\":[{\"name\":\"year\",\"value\":\"2076\"},{\"name\":\"month\",\"value\":\"12\"},{\"name\":\"day\",\"value\":\"31\"}]},{\"name\":\"description\",\"value\":\"76 - 76\"}]},{\"name\":\"name\",\"attributes\":{\"type\":\"person\",\"nameform\":\"authorized\"},\"children\":[{\"name\":\"namepart\",\"attributes\":{\"type\":\"givenname\"},\"children\":[{\"name\":\"name\",\"value\":\"Olov\"}]},{\"name\":\"namepart\",\"attributes\":{\"type\":\"familyname\"},\"children\":[{\"name\":\"name\",\"value\":\"McKie\"}]},{\"name\":\"namepart\",\"attributes\":{\"type\":\"number\"},\"children\":[{\"name\":\"name\",\"value\":\"II\"}]},{\"name\":\"namepart\",\"attributes\":{\"type\":\"addition\"},\"children\":[{\"name\":\"name\",\"value\":\"Ett tillägg\"}]},{\"name\":\"datePeriod\",\"attributes\":{\"eventType\":\"valid\"},\"children\":[{\"name\":\"date\",\"attributes\":{\"datePointEventType\":\"start\"},\"children\":[{\"name\":\"year\",\"value\":\"2008\"},{\"name\":\"month\",\"value\":\"06\"},{\"name\":\"day\",\"value\":\"28\"}]},{\"name\":\"description\",\"value\":\"Namn som gift\"}]}]},{\"name\":\"name\",\"attributes\":{\"type\":\"person\",\"nameform\":\"alternative\"},\"children\":[{\"name\":\"namepart\",\"attributes\":{\"type\":\"givenname\"},\"children\":[{\"name\":\"name\",\"value\":\"Olle\"}]},{\"name\":\"namepart\",\"attributes\":{\"type\":\"familyname\"},\"children\":[{\"name\":\"name\",\"value\":\"Nilsson\"}]}]},{\"name\":\"name\",\"attributes\":{\"type\":\"person\",\"nameform\":\"alternative\"},\"children\":[{\"name\":\"namepart\",\"attributes\":{\"type\":\"givenname\"},\"children\":[{\"name\":\"name\",\"value\":\"Olle2\"}]},{\"name\":\"namepart\",\"attributes\":{\"type\":\"familyname\"},\"children\":[{\"name\":\"name\",\"value\":\"Nilsson2\"}]}]},{\"name\":\"other\",\"value\":\"some other stuff\"},{\"name\":\"other\",\"value\":\"second other stuff\"},{\"name\":\"other\",\"value\":\"third other stuff\"},{\"name\":\"othercol\",\"value\":\"yes\"}],\"attributes\":{\"type\":\"place\"}}";
	private String jsonToCreateFromAttributeAsChild = "{\"name\":\"authority\",\"children\":[{\"name\":\"datePeriod\",\"attributes\":{\"eventType\":\"existence\"},\"children\":[{\"name\":\"date\",\"attributes\":{\"datePointEventType\":\"start\"},\"children\":[{\"year\":\"1976\"},{\"name\":\"month\",\"value\":\"07\"},{\"name\":\"day\",\"value\":\"22\"}]},{\"name\":\"date\",\"attributes\":{\"datePointEventType\":\"end\"},\"children\":[{\"name\":\"year\",\"value\":\"2076\"},{\"name\":\"month\",\"value\":\"12\"},{\"name\":\"day\",\"value\":\"31\"}]},{\"name\":\"description\",\"value\":\"76 - 76\"}]},{\"name\":\"name\",\"attributes\":{\"type\":\"person\",\"nameform\":\"authorized\"},\"children\":[{\"name\":\"namepart\",\"attributes\":{\"type\":\"givenname\"},\"children\":[{\"name\":\"name\",\"value\":\"Olov\"}]},{\"name\":\"namepart\",\"attributes\":{\"type\":\"familyname\"},\"children\":[{\"name\":\"name\",\"value\":\"McKie\"}]},{\"name\":\"namepart\",\"attributes\":{\"type\":\"number\"},\"children\":[{\"name\":\"name\",\"value\":\"II\"}]},{\"name\":\"namepart\",\"attributes\":{\"type\":\"addition\"},\"children\":[{\"name\":\"name\",\"value\":\"Ett tillägg\"}]},{\"name\":\"datePeriod\",\"attributes\":{\"eventType\":\"valid\"},\"children\":[{\"name\":\"date\",\"attributes\":{\"datePointEventType\":\"start\"},\"children\":[{\"name\":\"year\",\"value\":\"2008\"},{\"name\":\"month\",\"value\":\"06\"},{\"name\":\"day\",\"value\":\"28\"}]},{\"name\":\"description\",\"value\":\"Namn som gift\"}]}]},{\"name\":\"name\",\"attributes\":{\"type\":\"person\",\"nameform\":\"alternative\"},\"children\":[{\"name\":\"namepart\",\"attributes\":{\"type\":\"givenname\"},\"children\":[{\"name\":\"name\",\"value\":\"Olle\"}]},{\"name\":\"namepart\",\"attributes\":{\"type\":\"familyname\"},\"children\":[{\"name\":\"name\",\"value\":\"Nilsson\"}]}]},{\"name\":\"name\",\"attributes\":{\"type\":\"person\",\"nameform\":\"alternative\"},\"children\":[{\"name\":\"namepart\",\"attributes\":{\"type\":\"givenname\"},\"children\":[{\"name\":\"name\",\"value\":\"Olle2\"}]},{\"name\":\"namepart\",\"attributes\":{\"type\":\"familyname\"},\"children\":[{\"name\":\"name\",\"value\":\"Nilsson2\"}]}]},{\"name\":\"other\",\"value\":\"some other stuff\"},{\"name\":\"other\",\"value\":\"second other stuff\"},{\"name\":\"other\",\"value\":\"third other stuff\"},{\"name\":\"othercol\",\"value\":\"yes\"}],\"attributes\":{\"type\":\"place\"}}";
	private String jsonToUpdateWith = "{\"name\":\"authority\",\"children\":[{\"name\":\"recordInfo\",\"children\":[{\"name\":\"id\",\"value\":\"place:0001\"},{\"name\":\"type\",\"value\":\"place\"},{\"name\":\"createdBy\",\"value\":\"userId\"}]},{\"name\":\"datePeriod\",\"attributes\":{\"eventType\":\"existence\"},\"children\":[{\"name\":\"date\",\"attributes\":{\"datePointEventType\":\"start\"},\"children\":[{\"name\":\"year\",\"value\":\"1976\"},{\"name\":\"month\",\"value\":\"07\"},{\"name\":\"day\",\"value\":\"22\"}]},{\"name\":\"date\",\"attributes\":{\"datePointEventType\":\"end\"},\"children\":[{\"name\":\"year\",\"value\":\"2076\"},{\"name\":\"month\",\"value\":\"12\"},{\"name\":\"day\",\"value\":\"31\"}]},{\"name\":\"description\",\"value\":\"76 - 76\"}]},{\"name\":\"name\",\"attributes\":{\"type\":\"person\",\"nameform\":\"authorized\"},\"children\":[{\"name\":\"namepart\",\"attributes\":{\"type\":\"givenname\"},\"children\":[{\"name\":\"name\",\"value\":\"Olov\"}]},{\"name\":\"namepart\",\"attributes\":{\"type\":\"familyname\"},\"children\":[{\"name\":\"name\",\"value\":\"McKie\"}]},{\"name\":\"namepart\",\"attributes\":{\"type\":\"number\"},\"children\":[{\"name\":\"name\",\"value\":\"II\"}]},{\"name\":\"namepart\",\"attributes\":{\"type\":\"addition\"},\"children\":[{\"name\":\"name\",\"value\":\"Ett tillägg\"}]},{\"name\":\"datePeriod\",\"attributes\":{\"eventType\":\"valid\"},\"children\":[{\"name\":\"date\",\"attributes\":{\"datePointEventType\":\"start\"},\"children\":[{\"name\":\"year\",\"value\":\"2008\"},{\"name\":\"month\",\"value\":\"06\"},{\"name\":\"day\",\"value\":\"28\"}]},{\"name\":\"description\",\"value\":\"Namn som gift\"}]}]},{\"name\":\"name\",\"attributes\":{\"type\":\"person\",\"nameform\":\"alternative\"},\"children\":[{\"name\":\"namepart\",\"attributes\":{\"type\":\"givenname\"},\"children\":[{\"name\":\"name\",\"value\":\"Olle\"}]},{\"name\":\"namepart\",\"attributes\":{\"type\":\"familyname\"},\"children\":[{\"name\":\"name\",\"value\":\"Nilsson\"}]}]},{\"name\":\"name\",\"attributes\":{\"type\":\"person\",\"nameform\":\"alternative\"},\"children\":[{\"name\":\"namepart\",\"attributes\":{\"type\":\"givenname\"},\"children\":[{\"name\":\"name\",\"value\":\"Olle2\"}]},{\"name\":\"namepart\",\"attributes\":{\"type\":\"familyname\"},\"children\":[{\"name\":\"name\",\"value\":\"Nilsson2\"}]}]},{\"name\":\"other\",\"value\":\"some other stuff\"},{\"name\":\"other\",\"value\":\"second other stuff\"},{\"name\":\"other\",\"value\":\"third other stuff\"},{\"name\":\"othercol\",\"value\":\"yes\"}],\"attributes\":{\"type\":\"place\"}}";
	private String jsonToUpdateWithAttributeAsChild = "{\"name\":\"authority\",\"children\":[{\"name\":\"recordInfo\",\"children\":[{\"id\":\"place:0001\"},{\"name\":\"type\",\"value\":\"place\"},{\"name\":\"createdBy\",\"value\":\"userId\"}]},{\"name\":\"datePeriod\",\"attributes\":{\"eventType\":\"existence\"},\"children\":[{\"name\":\"date\",\"attributes\":{\"datePointEventType\":\"start\"},\"children\":[{\"name\":\"year\",\"value\":\"1976\"},{\"name\":\"month\",\"value\":\"07\"},{\"name\":\"day\",\"value\":\"22\"}]},{\"name\":\"date\",\"attributes\":{\"datePointEventType\":\"end\"},\"children\":[{\"name\":\"year\",\"value\":\"2076\"},{\"name\":\"month\",\"value\":\"12\"},{\"name\":\"day\",\"value\":\"31\"}]},{\"name\":\"description\",\"value\":\"76 - 76\"}]},{\"name\":\"name\",\"attributes\":{\"type\":\"person\",\"nameform\":\"authorized\"},\"children\":[{\"name\":\"namepart\",\"attributes\":{\"type\":\"givenname\"},\"children\":[{\"name\":\"name\",\"value\":\"Olov\"}]},{\"name\":\"namepart\",\"attributes\":{\"type\":\"familyname\"},\"children\":[{\"name\":\"name\",\"value\":\"McKie\"}]},{\"name\":\"namepart\",\"attributes\":{\"type\":\"number\"},\"children\":[{\"name\":\"name\",\"value\":\"II\"}]},{\"name\":\"namepart\",\"attributes\":{\"type\":\"addition\"},\"children\":[{\"name\":\"name\",\"value\":\"Ett tillägg\"}]},{\"name\":\"datePeriod\",\"attributes\":{\"eventType\":\"valid\"},\"children\":[{\"name\":\"date\",\"attributes\":{\"datePointEventType\":\"start\"},\"children\":[{\"name\":\"year\",\"value\":\"2008\"},{\"name\":\"month\",\"value\":\"06\"},{\"name\":\"day\",\"value\":\"28\"}]},{\"name\":\"description\",\"value\":\"Namn som gift\"}]}]},{\"name\":\"name\",\"attributes\":{\"type\":\"person\",\"nameform\":\"alternative\"},\"children\":[{\"name\":\"namepart\",\"attributes\":{\"type\":\"givenname\"},\"children\":[{\"name\":\"name\",\"value\":\"Olle\"}]},{\"name\":\"namepart\",\"attributes\":{\"type\":\"familyname\"},\"children\":[{\"name\":\"name\",\"value\":\"Nilsson\"}]}]},{\"name\":\"name\",\"attributes\":{\"type\":\"person\",\"nameform\":\"alternative\"},\"children\":[{\"name\":\"namepart\",\"attributes\":{\"type\":\"givenname\"},\"children\":[{\"name\":\"name\",\"value\":\"Olle2\"}]},{\"name\":\"namepart\",\"attributes\":{\"type\":\"familyname\"},\"children\":[{\"name\":\"name\",\"value\":\"Nilsson2\"}]}]},{\"name\":\"other\",\"value\":\"some other stuff\"},{\"name\":\"other\",\"value\":\"second other stuff\"},{\"name\":\"other\",\"value\":\"third other stuff\"},{\"name\":\"othercol\",\"value\":\"yes\"}],\"attributes\":{\"type\":\"place\"}}";
	private String jsonToUpdateWithNotFound = "{\"name\":\"authority\",\"children\":[{\"name\":\"recordInfo\",\"children\":[{\"name\":\"id\",\"value\":\"place:0001_NOT_FOUND\"},{\"name\":\"type\",\"value\":\"place\"},{\"name\":\"createdBy\",\"value\":\"userId\"}]},{\"name\":\"datePeriod\",\"attributes\":{\"eventType\":\"existence\"},\"children\":[{\"name\":\"date\",\"attributes\":{\"datePointEventType\":\"start\"},\"children\":[{\"name\":\"year\",\"value\":\"1976\"},{\"name\":\"month\",\"value\":\"07\"},{\"name\":\"day\",\"value\":\"22\"}]},{\"name\":\"date\",\"attributes\":{\"datePointEventType\":\"end\"},\"children\":[{\"name\":\"year\",\"value\":\"2076\"},{\"name\":\"month\",\"value\":\"12\"},{\"name\":\"day\",\"value\":\"31\"}]},{\"name\":\"description\",\"value\":\"76 - 76\"}]},{\"name\":\"name\",\"attributes\":{\"type\":\"person\",\"nameform\":\"authorized\"},\"children\":[{\"name\":\"namepart\",\"attributes\":{\"type\":\"givenname\"},\"children\":[{\"name\":\"name\",\"value\":\"Olov\"}]},{\"name\":\"namepart\",\"attributes\":{\"type\":\"familyname\"},\"children\":[{\"name\":\"name\",\"value\":\"McKie\"}]},{\"name\":\"namepart\",\"attributes\":{\"type\":\"number\"},\"children\":[{\"name\":\"name\",\"value\":\"II\"}]},{\"name\":\"namepart\",\"attributes\":{\"type\":\"addition\"},\"children\":[{\"name\":\"name\",\"value\":\"Ett tillägg\"}]},{\"name\":\"datePeriod\",\"attributes\":{\"eventType\":\"valid\"},\"children\":[{\"name\":\"date\",\"attributes\":{\"datePointEventType\":\"start\"},\"children\":[{\"name\":\"year\",\"value\":\"2008\"},{\"name\":\"month\",\"value\":\"06\"},{\"name\":\"day\",\"value\":\"28\"}]},{\"name\":\"description\",\"value\":\"Namn som gift\"}]}]},{\"name\":\"name\",\"attributes\":{\"type\":\"person\",\"nameform\":\"alternative\"},\"children\":[{\"name\":\"namepart\",\"attributes\":{\"type\":\"givenname\"},\"children\":[{\"name\":\"name\",\"value\":\"Olle\"}]},{\"name\":\"namepart\",\"attributes\":{\"type\":\"familyname\"},\"children\":[{\"name\":\"name\",\"value\":\"Nilsson\"}]}]},{\"name\":\"name\",\"attributes\":{\"type\":\"person\",\"nameform\":\"alternative\"},\"children\":[{\"name\":\"namepart\",\"attributes\":{\"type\":\"givenname\"},\"children\":[{\"name\":\"name\",\"value\":\"Olle2\"}]},{\"name\":\"namepart\",\"attributes\":{\"type\":\"familyname\"},\"children\":[{\"name\":\"name\",\"value\":\"Nilsson2\"}]}]},{\"name\":\"other\",\"value\":\"some other stuff\"},{\"name\":\"other\",\"value\":\"second other stuff\"},{\"name\":\"other\",\"value\":\"third other stuff\"},{\"name\":\"othercol\",\"value\":\"yes\"}],\"attributes\":{\"type\":\"place\"}}";
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
		Response response = recordEndpoint.readRecord("place", "place:0001_NOT_FOUND");
		assertEquals(response.getStatusInfo(), Response.Status.NOT_FOUND);
	}

	@Test
	public void testReadRecordAbstractRecordType() {
		String type = "abstract";
		Response responseRead = recordEndpoint.readRecord(type, "canBeWhatEverIdTypeIsChecked");
		assertEquals(responseRead.getStatusInfo(), Response.Status.METHOD_NOT_ALLOWED);
	}

	@Test
	public void testReadIncomingRecordLinks() {
		Response response = recordEndpoint.readIncomingRecordLinks("place", "place:0001");
		String entity = (String) response.getEntity();

		assertNotNull(entity, "An entity in json format should be returned");
		assertEquals(entity,
				"{\"children\":[{\"children\":["
						+ "{\"linkedRecordType\":\"place\",\"linkedRecordId\":\"place:0002\",\"name\":\"from\"},"
						+ "{\"linkedRecordType\":\"place\",\"linkedRecordId\":\"place:0001\",\"name\":\"to\"}],"
						+ "\"name\":\"recordToRecordLink\"}],\"name\":\"incomingRecordLinks\"}");
	}

	@Test
	public void testReadIncomingLinksUnauthorized() {
		Response response = recordEndpoint.readIncomingRecordLinksAsUserIdByTypeAndId(
				"unauthorizedUserId", "place", "place:0001");
		assertEquals(response.getStatusInfo(), Response.Status.UNAUTHORIZED);
	}

	@Test
	public void testReadIncomingLinksNotFound() {
		Response response = recordEndpoint.readIncomingRecordLinks("place", "place:0001_NOT_FOUND");
		assertEquals(response.getStatusInfo(), Response.Status.NOT_FOUND);
	}

	@Test
	public void testReadIncomingLinksAbstractRecordType() {
		String type = "abstract";
		Response responseRead = recordEndpoint.readIncomingRecordLinks(type,
				"canBeWhatEverIdTypeIsChecked");
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
		Response responseDeleted = recordEndpoint.deleteRecord(type,
				"canBeWhatEverIdTypeIsChecked");
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
	public void testUpdateRecordTypeNotFound() {
		String type = "place";
		String id = "place:0001";
		Response responseUpdate = recordEndpoint.updateRecord(type + "_NOT_FOUND", id,
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
	public void testUpdateRecordWrongDataTypeInJson() {
		String type = "place";
		String id = "place:0001";
		Response responseUpdate = recordEndpoint.updateRecord(type, id,
				jsonToUpdateWithAttributeAsChild);
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
		assertEquals(responseUpdate.getStatusInfo(), Response.Status.NOT_FOUND);
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
	public void testCreateRecordAttributeAsChild() {
		String type = "place";
		Response responseCreated = recordEndpoint.createRecord(type,
				jsonToCreateFromAttributeAsChild);
		assertEquals(responseCreated.getStatusInfo(), Response.Status.BAD_REQUEST);
	}

	@Test
	public void testCreateRecordAbstractRecordType() {
		String type = "abstract";
		Response responseCreated = recordEndpoint.createRecord(type, jsonToCreateFrom);
		assertEquals(responseCreated.getStatusInfo(), Response.Status.METHOD_NOT_ALLOWED);
	}

	String duplicateTestJson = "{\"name\":\"place\",\"children\":["
			+ "{\"name\":\"recordInfo\",\"children\":[" + "{\"name\":\"id\",\"value\":\"aPlace\"}]}"
			+ ",{\"name\":\"id\",\"value\":\"anythingGoes\"}]}";

	@Test
	public void testCreateRecordDuplicateUserSuppliedId() {
		String type = "place";
		Response responseCreated = recordEndpoint.createRecord(type, duplicateTestJson);
		assertEquals(responseCreated.getStatusInfo(), Response.Status.CREATED);

		Response responseCreated2 = recordEndpoint.createRecord(type, duplicateTestJson);
		assertEquals(responseCreated2.getStatusInfo(), Response.Status.CONFLICT);

	}

	@Test
	public void testCreateRecordUnexpectedError() {
		DependencyProviderForTest spiderDependencyProvider = new DependencyProviderForTest();
		spiderDependencyProvider.setDataValidator(new DataValidatorReturnNullPointer());
		SpiderInstanceProvider.setSpiderDependencyProvider(spiderDependencyProvider);

		String type = "place";
		Response responseCreated = recordEndpoint.createRecord(type, jsonToCreateFrom);
		assertEquals(responseCreated.getStatusInfo(), Response.Status.INTERNAL_SERVER_ERROR);
	}
}
