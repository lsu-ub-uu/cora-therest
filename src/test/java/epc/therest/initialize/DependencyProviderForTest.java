package epc.therest.initialize;

import epc.beefeater.Authorizator;
import epc.beefeater.AuthorizatorImp;
import epc.metadataformat.validator.DataValidator;
import epc.spider.dependency.SpiderDependencyProvider;
import epc.spider.record.PermissionKeyCalculator;
import epc.spider.record.RecordPermissionKeyCalculator;
import epc.spider.record.storage.RecordIdGenerator;
import epc.spider.record.storage.RecordStorage;
import epc.spider.record.storage.TimeStampIdGenerator;
import epc.therest.record.DataValidatorAlwaysValidSpy;
import epc.therest.testdata.TestDataRecordInMemoryStorage;

public class DependencyProviderForTest implements SpiderDependencyProvider {

	private RecordStorage recordStorage = TestDataRecordInMemoryStorage
			.createRecordStorageInMemoryWithTestData();
	private Authorizator authorizator = new AuthorizatorImp();
	private RecordIdGenerator idGenerator = new TimeStampIdGenerator();
	private PermissionKeyCalculator keyCalculator = new RecordPermissionKeyCalculator();
	private DataValidator dataValidator = new DataValidatorAlwaysValidSpy();

	@Override
	public Authorizator getAuthorizator() {
		return authorizator;
	}

	@Override
	public RecordStorage getRecordStorage() {
		return recordStorage;
	}

	@Override
	public RecordIdGenerator getIdGenerator() {
		return idGenerator;
	}

	@Override
	public PermissionKeyCalculator getPermissionKeyCalculator() {
		return keyCalculator;
	}

	@Override
	public DataValidator getDataValidator() {
		return dataValidator;
	}

	public void setDataValidator(DataValidator dataValidator) {
		this.dataValidator = dataValidator;
	}

}
