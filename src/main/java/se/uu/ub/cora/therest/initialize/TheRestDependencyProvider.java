/*
 * Copyright 2015, 2016, 2017, 2018, 2019 Uppsala University Library
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

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import se.uu.ub.cora.gatekeeperclient.authentication.AuthenticatorImp;
import se.uu.ub.cora.httphandler.HttpHandlerFactory;
import se.uu.ub.cora.httphandler.HttpHandlerFactoryImp;
import se.uu.ub.cora.metacreator.extended.MetacreatorExtendedFunctionalityProvider;
import se.uu.ub.cora.solr.SolrClientProviderImp;
import se.uu.ub.cora.solrindex.SolrRecordIndexer;
import se.uu.ub.cora.solrsearch.SolrRecordSearch;
import se.uu.ub.cora.spider.authentication.Authenticator;
import se.uu.ub.cora.spider.dependency.SpiderDependencyProvider;
import se.uu.ub.cora.spider.extended.ExtendedFunctionalityProvider;
import se.uu.ub.cora.spider.record.RecordSearch;
import se.uu.ub.cora.spider.search.RecordIndexer;
import se.uu.ub.cora.storage.SearchStorage;

/**
 * SystemOneDependencyProvider wires up the system for use in "production", as this is in SystemOne
 * production currently means using all in memory storage (stored on disk), so do NOT use this class
 * in production as it is written today. :)
 *
 */
public class TheRestDependencyProvider extends SpiderDependencyProvider {

	private String gatekeeperUrl;
	private String solrUrl;
	private SolrRecordIndexer solrRecordIndexer;
	private SolrClientProviderImp solrClientProvider;

	public TheRestDependencyProvider(Map<String, String> initInfo) {
		super(initInfo);
	}

	@Override
	protected void readInitInfo() {
		tryToSetGatekeeperUrl();
		tryToSetSolrUrl();
	}

	private void tryToSetGatekeeperUrl() {
		ensureKeyExistsInInitInfo("gatekeeperURL");
		gatekeeperUrl = initInfo.get("gatekeeperURL");
	}

	private void tryToSetSolrUrl() {
		ensureKeyExistsInInitInfo("solrURL");
		solrUrl = initInfo.get("solrURL");
	}

	@Override
	protected void tryToInitialize() throws NoSuchMethodException, ClassNotFoundException,
			IllegalAccessException, InvocationTargetException {

		solrClientProvider = SolrClientProviderImp.usingBaseUrl(solrUrl);
		solrRecordIndexer = SolrRecordIndexer
				.createSolrRecordIndexerUsingSolrClientProvider(solrClientProvider);
	}

	@Override
	public ExtendedFunctionalityProvider getExtendedFunctionalityProvider() {
		return new MetacreatorExtendedFunctionalityProvider(this);
	}

	@Override
	public Authenticator getAuthenticator() {
		HttpHandlerFactory httpHandlerFactory = new HttpHandlerFactoryImp();
		return AuthenticatorImp.usingBaseUrlAndHttpHandlerFactory(gatekeeperUrl,
				httpHandlerFactory);
	}

	@Override
	public RecordSearch getRecordSearch() {
		return SolrRecordSearch.createSolrRecordSearchUsingSolrClientProviderAndSearchStorage(
				solrClientProvider, (SearchStorage) getRecordStorage());
	}

	@Override
	public RecordIndexer getRecordIndexer() {
		return solrRecordIndexer;
	}

}