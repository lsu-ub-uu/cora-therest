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
		Response response = recordEndpoint.readRecordAsUserIdByTypeAndId("userId", "place",
				"place:0001");
		String entity = (String) response.getEntity();

		assertNotNull(entity, "An entity in json format should be returned");
	}

	@Test
	public void testReadRecordUnAuthorized() {
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
		Response response = recordEndpoint.deleteRecordAsUserIdByTypeAndId("userId", "place",
				"place:0001");
		assertEquals(response.getStatusInfo(), Response.Status.OK);
	}

	@Test
	public void testDeleteRecordUnAuthorized() {
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
}
