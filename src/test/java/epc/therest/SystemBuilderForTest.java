package epc.therest;

import epc.beefeater.AuthorizationInputBoundary;
import epc.beefeater.Authorizator;
import epc.spider.record.PermissionKeyCalculator;
import epc.spider.record.RecordHandler;
import epc.spider.record.RecordInputBoundary;
import epc.spider.record.RecordPermissionKeyCalculator;
import epc.spider.record.storage.RecordIdGenerator;
import epc.spider.record.storage.RecordStorageGateway;
import epc.spider.record.storage.TimeStampIdGenerator;
import epc.systemone.SystemHolder;
import epc.therest.testdata.TestDataRecordInMemoryStorage;

public class SystemBuilderForTest {

	public static void createAllDependenciesInSystemHolder() {
		SystemHolder
				.setRecordInputBoundary(defineImplementingRecordInputBoundary());
	}

	private static RecordInputBoundary defineImplementingRecordInputBoundary() {
		RecordStorageGateway recordStorage = TestDataRecordInMemoryStorage
				.createRecordInMemoryStorageWithTestData();
		AuthorizationInputBoundary authorization = new Authorizator();
		RecordIdGenerator idGenerator = new TimeStampIdGenerator();
		PermissionKeyCalculator keyCalculator = new RecordPermissionKeyCalculator();
		return new RecordHandler(authorization, recordStorage, idGenerator,
				keyCalculator);
	}

}
