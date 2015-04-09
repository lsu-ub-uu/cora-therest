package epc.therest.record;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import javax.ws.rs.core.Response;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import epc.therest.SystemBuilderForTest;

public class RecordEndpointTest {
	private RecordEndpoint recordEndpoint;

	@BeforeMethod
	public void beforeMethod() {
		SystemBuilderForTest.createAllDependenciesInSystemHolder();
		recordEndpoint = new RecordEndpoint();
	}

	@Test
	public void testCreateNew() {
		// String result = recordEndpoint.createRecord();
		String response = recordEndpoint.createRecord();
		// TODO: test something better...
		assertNotNull(response, "A response should be returned");
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
		Response response = recordEndpoint.readRecord(type, id);
		String jsonString = (String) response.getEntity();

		Response responseUpdate = recordEndpoint.updateRecord(type, id, jsonString);
		assertEquals(responseUpdate.getStatusInfo(), Response.Status.OK);
	}

	@Test
	public void testUpdateRecordUnauthorized() {
		String type = "place";
		String id = "place:0001";
		Response response = recordEndpoint.readRecord(type, id);
		String jsonString = (String) response.getEntity();

		Response responseUpdate = recordEndpoint.updateRecordAsUserIdWithRecord(
				"unauthorizedUserId", type, id, jsonString);
		assertEquals(responseUpdate.getStatusInfo(), Response.Status.UNAUTHORIZED);
	}

	@Test
	public void testUpdateRecordNotFound() {
		String type = "place";
		String id = "place:0001";
		Response response = recordEndpoint.readRecord(type, id);
		String jsonString = (String) response.getEntity();

		Response responseUpdate = recordEndpoint.updateRecordAsUserIdWithRecord(
				"unauthorizedUserId", type, id + "_NOT_FOUND", jsonString);
		assertEquals(responseUpdate.getStatusInfo(), Response.Status.NOT_FOUND);
	}

	@Test
	public void testUpdateRecordBadJson() {
		String type = "place";
		String id = "place:0001";
		Response response = recordEndpoint.readRecord(type, id);
		String jsonString = (String) response.getEntity();
		String json = "{\"groupDataId\":{\"children\":[{\"atomicDataId\":\"atomicValue\""
				+ ",\"atomicDataId2\":\"atomicValue2\"}]}}";
		Response responseUpdate = recordEndpoint.updateRecord(type, id, json);
		assertEquals(responseUpdate.getStatusInfo(), Response.Status.BAD_REQUEST);
	}
}
