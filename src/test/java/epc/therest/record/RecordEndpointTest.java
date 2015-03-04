package epc.therest.record;

import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import epc.systemone.SystemBuilderForProduction;

public class RecordEndpointTest {
	@BeforeTest
	public void beforeTest() {

		SystemBuilderForProduction.createAllDependenciesInSystemHolder();
	}

	@Test
	public void testCreateNew() {
		RecordEndpoint recordEndpoint = new RecordEndpoint();
		String result = recordEndpoint.createRecord();
		// TODO: test something better...
		Assert.assertEquals(result.length(), 21, "Keylength should be 16");
	}
	
}
