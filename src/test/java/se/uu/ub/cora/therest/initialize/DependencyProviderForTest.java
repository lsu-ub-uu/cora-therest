package se.uu.ub.cora.therest.initialize;

import se.uu.ub.cora.beefeater.Authorizator;
import se.uu.ub.cora.beefeater.AuthorizatorImp;
import se.uu.ub.cora.bookkeeper.linkcollector.DataRecordLinkCollector;
import se.uu.ub.cora.bookkeeper.validator.DataValidator;
import se.uu.ub.cora.spider.dependency.SpiderDependencyProvider;
import se.uu.ub.cora.spider.record.PermissionKeyCalculator;
import se.uu.ub.cora.spider.record.RecordPermissionKeyCalculator;
import se.uu.ub.cora.spider.record.storage.RecordIdGenerator;
import se.uu.ub.cora.spider.record.storage.RecordStorage;
import se.uu.ub.cora.spider.record.storage.TimeStampIdGenerator;
import se.uu.ub.cora.therest.record.DataRecordLinkCollectorSpy;
import se.uu.ub.cora.therest.record.DataValidatorAlwaysValidSpy;
import se.uu.ub.cora.therest.testdata.TestDataRecordInMemoryStorage;

public class DependencyProviderForTest implements SpiderDependencyProvider {

	private RecordStorage recordStorage = TestDataRecordInMemoryStorage
			.createRecordStorageInMemoryWithTestData();
	private Authorizator authorizator = new AuthorizatorImp();
	private RecordIdGenerator idGenerator = new TimeStampIdGenerator();
	private PermissionKeyCalculator keyCalculator = new RecordPermissionKeyCalculator();
	private DataValidator dataValidator = new DataValidatorAlwaysValidSpy();
	private DataRecordLinkCollector linkCollector = new DataRecordLinkCollectorSpy();

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

	@Override
	public DataRecordLinkCollector getDataRecordLinkCollector() {
		return linkCollector;
	}

}
