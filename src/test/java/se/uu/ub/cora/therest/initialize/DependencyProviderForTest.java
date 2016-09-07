/*
 * Copyright 2015 Uppsala University Library
 *
 * This file is part of Cora.
 *
 *     Cora is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Cora is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Cora.  If not, see <http://www.gnu.org/licenses/>.
 */

package se.uu.ub.cora.therest.initialize;

import se.uu.ub.cora.beefeater.Authorizator;
import se.uu.ub.cora.beefeater.AuthorizatorImp;
import se.uu.ub.cora.bookkeeper.linkcollector.DataRecordLinkCollector;
import se.uu.ub.cora.bookkeeper.validator.DataValidator;
import se.uu.ub.cora.spider.dependency.SpiderDependencyProvider;
import se.uu.ub.cora.spider.record.PermissionKeyCalculator;
import se.uu.ub.cora.spider.record.StreamStorageSpy;
import se.uu.ub.cora.spider.record.storage.RecordIdGenerator;
import se.uu.ub.cora.spider.record.storage.RecordStorage;
import se.uu.ub.cora.spider.spy.IdGeneratorSpy;
import se.uu.ub.cora.spider.stream.storage.StreamStorage;
import se.uu.ub.cora.systemone.record.RecordPermissionKeyCalculator;
import se.uu.ub.cora.therest.record.DataRecordLinkCollectorSpy;
import se.uu.ub.cora.therest.record.DataValidatorAlwaysValidSpy;
import se.uu.ub.cora.therest.testdata.TestDataRecordInMemoryStorage;

public class DependencyProviderForTest implements SpiderDependencyProvider {

	private RecordStorage recordStorage = TestDataRecordInMemoryStorage
			.createRecordStorageInMemoryWithTestData();
	private Authorizator authorizator = new AuthorizatorImp();
	// private RecordIdGenerator idGenerator = new TimeStampIdGenerator();
	private RecordIdGenerator idGenerator = new IdGeneratorSpy();
	private PermissionKeyCalculator keyCalculator = new RecordPermissionKeyCalculator();
	private DataValidator dataValidator = new DataValidatorAlwaysValidSpy();
	private DataRecordLinkCollector linkCollector = new DataRecordLinkCollectorSpy();
	private StreamStorage streamStorage = new StreamStorageSpy();

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

	@Override
	public StreamStorage getStreamStorage() {
		return streamStorage;
	}

}
