package epc.therest.record;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import epc.therest.SystemBuilderForTest;
import epc.therest.data.DataAtomicRest;
import epc.therest.data.DataGroupRest;

public class RecordEndpointTest {
	@BeforeMethod
	public void beforeTest() {

		SystemBuilderForTest.createAllDependenciesInSystemHolder();
	}

	@Test
	public void testCreateNew() {
		RecordEndpoint recordEndpoint = new RecordEndpoint();
		// String result = recordEndpoint.createRecord();
		DataGroupRest record = recordEndpoint.createRecord();
		// TODO: test something better...
		DataGroupRest recordInfo = (DataGroupRest) record.getChildren().stream()
				.filter(p -> p.getDataId().equals("recordInfo")).findFirst().get();
		DataAtomicRest recordId = (DataAtomicRest) recordInfo.getChildren().stream()
				.filter(p -> p.getDataId().equals("id")).findFirst().get();
		Assert.assertTrue(recordId.getValue().length() > 10, "Keylength should be longer than 10");
	}

	@Test
	public void testReadRecord() {
		RecordEndpoint recordEndpoint = new RecordEndpoint();
		DataGroupRest record = recordEndpoint.readRecord("place", "place:0001");
		Assert.assertNotNull(record, "A record should be returned");
	}

}
