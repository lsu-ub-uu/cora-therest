/*
 * Copyright 2017, 2018, 2019 Uppsala University Library
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
import se.uu.ub.cora.bookkeeper.termcollector.DataGroupTermCollector;
import se.uu.ub.cora.bookkeeper.validator.DataValidator;
import se.uu.ub.cora.spider.authentication.Authenticator;
import se.uu.ub.cora.spider.authorization.PermissionRuleCalculator;
import se.uu.ub.cora.spider.authorization.SpiderAuthorizator;
import se.uu.ub.cora.spider.dependency.SpiderDependencyProvider;
import se.uu.ub.cora.spider.extended.ExtendedFunctionalityProvider;
import se.uu.ub.cora.spider.record.RecordSearch;
import se.uu.ub.cora.spider.search.RecordIndexer;
import se.uu.ub.cora.storage.MetadataStorageProvider;
import se.uu.ub.cora.storage.RecordIdGeneratorProvider;
import se.uu.ub.cora.storage.RecordStorageProvider;
import se.uu.ub.cora.storage.StreamStorageProvider;

public class DependencyProviderSpy extends SpiderDependencyProvider {

	public DependencyProviderSpy(Map<String, String> initInfo) {
		super(initInfo);
	}

	@Override
	public SpiderAuthorizator getSpiderAuthorizator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PermissionRuleCalculator getPermissionRuleCalculator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DataValidator getDataValidator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DataRecordLinkCollector getDataRecordLinkCollector() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ExtendedFunctionalityProvider getExtendedFunctionalityProvider() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Authenticator getAuthenticator() {
		// TODO Auto-generated method stub
		throw new RuntimeException("Throwing exception from DependencyProviderSpy");
	}

	public Map<String, String> getInitInfo() {
		return initInfo;
	}

	@Override
	public RecordSearch getRecordSearch() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DataGroupTermCollector getDataGroupTermCollector() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RecordIndexer getRecordIndexer() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void tryToInitialize() throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	protected void readInitInfo() {
		// TODO Auto-generated method stub

	}

	public RecordStorageProvider getRecordStorageProvider() {
		return recordStorageProvider;
	}

	public StreamStorageProvider getStreamStorageProvider() {
		return streamStorageProvider;
	}

	public RecordIdGeneratorProvider getRecordIdGeneratorProvider() {
		return recordIdGeneratorProvider;
	}

	public MetadataStorageProvider getMetadataStorageProvider() {
		return metadataStorageProvider;
	}

}
