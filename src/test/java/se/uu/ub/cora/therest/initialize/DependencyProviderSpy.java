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

import se.uu.ub.cora.bookkeeper.decorator.DataDecarator;
import se.uu.ub.cora.bookkeeper.linkcollector.DataRecordLinkCollector;
import se.uu.ub.cora.bookkeeper.termcollector.DataGroupTermCollector;
import se.uu.ub.cora.bookkeeper.validator.DataValidator;
import se.uu.ub.cora.search.RecordIndexer;
import se.uu.ub.cora.search.RecordSearch;
import se.uu.ub.cora.spider.authentication.Authenticator;
import se.uu.ub.cora.spider.authorization.PermissionRuleCalculator;
import se.uu.ub.cora.spider.authorization.SpiderAuthorizator;
import se.uu.ub.cora.spider.dependency.DependencyProviderAbstract;
import se.uu.ub.cora.storage.StreamStorageProvider;
import se.uu.ub.cora.storage.archive.RecordArchiveProvider;
import se.uu.ub.cora.storage.idgenerator.RecordIdGeneratorProvider;

public class DependencyProviderSpy extends DependencyProviderAbstract {

	public boolean initializeExtendedFunctionalityHasBeenCalled = false;

	public DependencyProviderSpy() {
		super();
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
	public Authenticator getAuthenticator() {
		// TODO Auto-generated method stub
		throw new RuntimeException("Throwing exception from DependencyProviderSpy");
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

	public RecordArchiveProvider getRecordArchiveProvider() {
		return recordArchiveProvider;
	}

	public StreamStorageProvider getStreamStorageProvider() {
		return streamStorageProvider;
	}

	public RecordIdGeneratorProvider getRecordIdGeneratorProvider() {
		return recordIdGeneratorProvider;
	}

	@Override
	public void initializeExtendedFunctionality() {
		initializeExtendedFunctionalityHasBeenCalled = true;
	}

	@Override
	public DataDecarator getDataDecorator() {
		// TODO Auto-generated method stub
		return null;
	}

}
