package epc.therest;

import org.testng.Assert;
import org.testng.annotations.Test;

import epc.spider.record.RecordHandler;
import epc.spider.record.RecordInputBoundary;
import epc.systemone.SystemHolder;

public class SystemBuilderForTestTest {
	@Test
	public void testSystemInit() {
		SystemBuilderForTest.createAllDependenciesInSystemHolder();
		
		RecordInputBoundary recordInputBoundary = SystemHolder
				.getRecordInputBoundary();

		Assert.assertNotNull(recordInputBoundary,
				"RecordInputBoundry should be instansiated");

		Assert.assertEquals(recordInputBoundary.getClass(),RecordHandler.class,
				"The returned recordInputBoundary should be the same");
	}
}
