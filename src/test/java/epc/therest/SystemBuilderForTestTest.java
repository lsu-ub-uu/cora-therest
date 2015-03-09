package epc.therest;

import org.testng.Assert;
import org.testng.annotations.Test;

import epc.spider.record.SpiderRecordHandlerImp;
import epc.spider.record.SpiderRecordHandler;
import epc.systemone.SystemHolder;

public class SystemBuilderForTestTest {
	@Test
	public void testSystemInit() {
		SystemBuilderForTest.createAllDependenciesInSystemHolder();
		
		SpiderRecordHandler spiderRecordHandler = SystemHolder
				.getSpiderRecordHandler();

		Assert.assertNotNull(spiderRecordHandler,
				"RecordInputBoundry should be instansiated");

		Assert.assertEquals(spiderRecordHandler.getClass(),SpiderRecordHandlerImp.class,
				"The returned recordInputBoundary should be the same");
	}
}
