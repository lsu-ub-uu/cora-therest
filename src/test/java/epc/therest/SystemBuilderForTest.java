package epc.therest;

import epc.beefeater.Authorizator;
import epc.beefeater.AuthorizatorImp;
import epc.spider.record.PermissionKeyCalculator;
import epc.spider.record.SpiderRecordHandlerImp;
import epc.spider.record.SpiderRecordHandler;
import epc.spider.record.RecordPermissionKeyCalculator;
import epc.spider.record.storage.RecordIdGenerator;
import epc.spider.record.storage.RecordStorage;
import epc.spider.record.storage.TimeStampIdGenerator;
import epc.systemone.SystemHolder;
import epc.therest.testdata.TestDataRecordInMemoryStorage;

public class SystemBuilderForTest {

	public static void createAllDependenciesInSystemHolder() {
		SystemHolder
				.setSpiderRecordHandler(defineImplementingSpiderRecordHandler());
	}

	private static SpiderRecordHandler defineImplementingSpiderRecordHandler() {
		RecordStorage recordStorage = TestDataRecordInMemoryStorage
				.createRecordStorageInMemoryWithTestData();
		Authorizator authorization = new AuthorizatorImp();
		RecordIdGenerator idGenerator = new TimeStampIdGenerator();
		PermissionKeyCalculator keyCalculator = new RecordPermissionKeyCalculator();
		return SpiderRecordHandlerImp.usingAuthorizationAndRecordStorageAndIdGeneratorAndKeyCalculator(
				authorization, recordStorage, idGenerator, keyCalculator);
	}

}
