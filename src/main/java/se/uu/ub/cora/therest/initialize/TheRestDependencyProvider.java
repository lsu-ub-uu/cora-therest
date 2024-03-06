/*
 * Copyright 2015, 2016, 2017, 2018, 2019, 2020 Uppsala University Library
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

import se.uu.ub.cora.gatekeeperclient.authentication.AuthenticatorImp;
import se.uu.ub.cora.httphandler.HttpHandlerFactory;
import se.uu.ub.cora.httphandler.HttpHandlerFactoryImp;
import se.uu.ub.cora.initialize.SettingsProvider;
import se.uu.ub.cora.search.RecordIndexer;
import se.uu.ub.cora.search.RecordIndexerFactory;
import se.uu.ub.cora.search.RecordSearch;
import se.uu.ub.cora.searchstorage.SearchStorageProvider;
import se.uu.ub.cora.solr.SolrClientProviderImp;
import se.uu.ub.cora.solrindex.SolrRecordIndexerFactory;
import se.uu.ub.cora.solrsearch.SolrRecordSearch;
import se.uu.ub.cora.spider.authentication.Authenticator;
import se.uu.ub.cora.spider.dependency.DependencyProviderAbstract;

/**
 * TheRestDependencyProvider wires up the system for use in production as used from the Rest.
 */
public class TheRestDependencyProvider extends DependencyProviderAbstract {

	private String gatekeeperUrl;
	private String solrUrl;
	private SolrClientProviderImp solrClientProvider;
	private SolrRecordIndexerFactory solrRecordIndexerFactory;

	public TheRestDependencyProvider() {
		super();
	}

	@Override
	protected void readInitInfo() {
		tryToSetGatekeeperUrl();
		tryToSetSolrUrl();
	}

	private void tryToSetGatekeeperUrl() {
		gatekeeperUrl = SettingsProvider.getSetting("gatekeeperURL");
	}

	private void tryToSetSolrUrl() {
		solrUrl = SettingsProvider.getSetting("solrURL");
	}

	@Override
	protected void tryToInitialize() {
		solrRecordIndexerFactory = new SolrRecordIndexerFactory();
		solrClientProvider = SolrClientProviderImp.usingBaseUrl(solrUrl);
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
				solrClientProvider, SearchStorageProvider.getStorageView());
	}

	@Override
	public RecordIndexer getRecordIndexer() {
		return solrRecordIndexerFactory.factor(solrUrl);
	}

	RecordIndexerFactory getRecordIndexerFactory() {
		return solrRecordIndexerFactory;
	}

}