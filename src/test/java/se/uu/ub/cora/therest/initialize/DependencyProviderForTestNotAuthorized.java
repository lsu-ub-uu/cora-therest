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

import java.util.Map;

import se.uu.ub.cora.bookkeeper.linkcollector.DataRecordLinkCollector;
import se.uu.ub.cora.bookkeeper.searchtermcollector.DataGroupSearchTermCollector;
import se.uu.ub.cora.bookkeeper.validator.DataValidator;
import se.uu.ub.cora.spider.authentication.Authenticator;
import se.uu.ub.cora.spider.authorization.BasePermissionRuleCalculator;
import se.uu.ub.cora.spider.authorization.PermissionRuleCalculator;
import se.uu.ub.cora.spider.authorization.SpiderAuthorizator;
import se.uu.ub.cora.spider.dependency.SpiderDependencyProvider;
import se.uu.ub.cora.spider.extended.BaseExtendedFunctionalityProvider;
import se.uu.ub.cora.spider.extended.ExtendedFunctionalityProvider;
import se.uu.ub.cora.spider.record.RecordSearch;
import se.uu.ub.cora.spider.record.storage.RecordIdGenerator;
import se.uu.ub.cora.spider.record.storage.RecordStorage;
import se.uu.ub.cora.spider.stream.storage.StreamStorage;
import se.uu.ub.cora.therest.record.DataRecordLinkCollectorSpy;
import se.uu.ub.cora.therest.record.DataValidatorAlwaysValidSpy;
import se.uu.ub.cora.therest.record.IdGeneratorSpy;
import se.uu.ub.cora.therest.record.StreamStorageSpy;
import se.uu.ub.cora.therest.testdata.TestDataRecordInMemoryStorage;

public class DependencyProviderForTestNotAuthorized extends SpiderDependencyProvider {

	private RecordStorage recordStorage = TestDataRecordInMemoryStorage
			.createRecordStorageInMemoryWithTestData();
	private RecordIdGenerator idGenerator = new IdGeneratorSpy();
	private DataValidator dataValidator = new DataValidatorAlwaysValidSpy();
	private DataRecordLinkCollector linkCollector = new DataRecordLinkCollectorSpy();
	private StreamStorage streamStorage = new StreamStorageSpy();

	public DependencyProviderForTestNotAuthorized(Map<String, String> initInfo) {
		super(initInfo);
	}

	@Override
	public SpiderAuthorizator getSpiderAuthorizator() {
		// RulesProvider rulesProvider = new RulesProviderImp(recordStorage);
		// return
		// SpiderAuthorizatorImp.usingSpiderDependencyProviderAndAuthorizatorAndRulesProvider(
		// this, new AlwaysAuthorized(), rulesProvider);
		return new SpiderAuthorizorNeverAuthorized();
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
	public PermissionRuleCalculator getPermissionRuleCalculator() {
		return new BasePermissionRuleCalculator();
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

	@Override
	public ExtendedFunctionalityProvider getExtendedFunctionalityProvider() {
		return new BaseExtendedFunctionalityProvider(this);
	}

	@Override
	public Authenticator getAuthenticator() {
		return new AuthenticatorForTest();
	}

	@Override
	public RecordSearch getRecordSearch() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DataGroupSearchTermCollector getDataGroupSearchTermCollector() {
		// TODO Auto-generated method stub
		return null;
	}

}
