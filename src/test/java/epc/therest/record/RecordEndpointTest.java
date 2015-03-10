package epc.therest.record;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import epc.therest.SystemBuilderForTest;

public class RecordEndpointTest {
	@BeforeMethod
	public void beforeTest() {

		SystemBuilderForTest.createAllDependenciesInSystemHolder();
	}

	@Test
	public void testCreateNew() {
		RecordEndpoint recordEndpoint = new RecordEndpoint();
		// String result = recordEndpoint.createRecord();
		String recordAsJson = recordEndpoint.createRecord();
		// TODO: test something better...
		Assert.assertNotNull(recordAsJson, "A record should be returned");
	}

	@Test
	public void testReadRecord() {
		RecordEndpoint recordEndpoint = new RecordEndpoint();
		String recordAsJson = recordEndpoint.readRecord("place", "place:0001");
		Assert.assertNotNull(recordAsJson, "A record should be returned");
	}

}
