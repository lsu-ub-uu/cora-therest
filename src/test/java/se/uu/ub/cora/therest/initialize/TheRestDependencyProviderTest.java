/*
 * Copyright 2015, 2017, 2018, 2019, 2020 Uppsala University Library
 * Copyright 2017 Olov McKie
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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNotSame;
import static org.testng.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.gatekeeperclient.authentication.AuthenticatorImp;
import se.uu.ub.cora.logger.LoggerProvider;
import se.uu.ub.cora.search.RecordIndexer;
import se.uu.ub.cora.search.RecordIndexerFactory;
import se.uu.ub.cora.search.RecordSearch;
import se.uu.ub.cora.searchstorage.SearchStorageProvider;
import se.uu.ub.cora.searchstorage.SearchStorageView;
import se.uu.ub.cora.solr.SolrClientProviderImp;
import se.uu.ub.cora.solrindex.SolrRecordIndexer;
import se.uu.ub.cora.solrindex.SolrRecordIndexerFactory;
import se.uu.ub.cora.solrsearch.SolrRecordSearch;
import se.uu.ub.cora.spider.authorization.PermissionRuleCalculator;
import se.uu.ub.cora.spider.dependency.SpiderInitializationException;
import se.uu.ub.cora.storage.RecordStorageProvider;
import se.uu.ub.cora.storage.StreamStorageProvider;
import se.uu.ub.cora.storage.idgenerator.RecordIdGeneratorProvider;
import se.uu.ub.cora.storage.spies.RecordStorageInstanceProviderSpy;
import se.uu.ub.cora.therest.log.LoggerFactorySpy;

public class TheRestDependencyProviderTest {
	private TheRestDependencyProvider dependencyProvider;
	private Map<String, String> settings;
	private LoggerFactorySpy loggerFactorySpy;
	private String testedBaseClassName = "DependencyProviderAbstract";
	private RecordStorageInstanceProviderSpy recordStorageInstanceProviderSpy = new RecordStorageInstanceProviderSpy();

	@BeforeMethod
	public void setUp() throws Exception {
		loggerFactorySpy = new LoggerFactorySpy();
		LoggerProvider.setLoggerFactory(loggerFactorySpy);
		settings = new HashMap<>();
		settings.put("gatekeeperURL", "http://localhost:8080/gatekeeper/");
		settings.put("solrURL", "http://localhost:8983/solr/stuff");
		dependencyProvider = new TheRestDependencyProvider(settings);
		setPluggedInStorageNormallySetByTheRestModuleStarterImp();
	}

	private void setPluggedInStorageNormallySetByTheRestModuleStarterImp() {
		RecordStorageProvider
				.onlyForTestSetRecordStorageInstanceProvider(recordStorageInstanceProviderSpy);
		StreamStorageProvider streamStorageProvider = new StreamStorageProviderSpy();
		dependencyProvider.setStreamStorageProvider(streamStorageProvider);
		RecordIdGeneratorProvider recordIdGeneratorProvider = new RecordIdGeneratorProviderSpy();
		dependencyProvider.setRecordIdGeneratorProvider(recordIdGeneratorProvider);
	}

	@Test
	public void testInit() {
		assertTrue(dependencyProvider.getAuthenticator() instanceof AuthenticatorImp);
		assertTrue(dependencyProvider.getRecordIndexer() instanceof SolrRecordIndexer);
	}

	private Exception callSystemOneDependencyProviderAndReturnResultingError() {
		Exception thrownException = null;
		try {
			dependencyProvider = new TheRestDependencyProvider(settings);
		} catch (Exception e) {
			thrownException = e;
		}
		return thrownException;
	}

	@Test
	public void testGetPermissionRuleCalculator() {
		PermissionRuleCalculator permissionRuleCalculator = dependencyProvider
				.getPermissionRuleCalculator();
		PermissionRuleCalculator permissionRuleCalculator2 = dependencyProvider
				.getPermissionRuleCalculator();
		assertNotEquals(permissionRuleCalculator, permissionRuleCalculator2);
	}

	@Test
	public void testMissingGatekeeperUrlInInitInfo() {
		settings.remove("gatekeeperURL");

		Exception thrownException = callSystemOneDependencyProviderAndReturnResultingError();

		assertTrue(thrownException instanceof SpiderInitializationException);
		assertEquals(thrownException.getMessage(),
				"InitInfo in TheRestDependencyProvider must contain: gatekeeperURL");
		assertEquals(loggerFactorySpy.getNoOfFatalLogMessagesUsingClassName(testedBaseClassName),
				1);
		assertEquals(loggerFactorySpy.getFatalLogMessageUsingClassNameAndNo(testedBaseClassName, 0),
				"InitInfo in TheRestDependencyProvider must contain: gatekeeperURL");
	}

	@Test
	public void testtestGetAuthenticatorUsesGatekeeperUrl() {
		AuthenticatorImp authenticator = (AuthenticatorImp) dependencyProvider.getAuthenticator();
		assertNotNull(authenticator);
		assertEquals(authenticator.getBaseURL(), settings.get("gatekeeperURL"));
	}

	@Test
	public void testGetRecordSearch() {
		assertNotNull(dependencyProvider.getRecordSearch());
	}

	@Test
	public void testRecordIndexerFactory() {
		RecordIndexerFactory recordIndexerFactory = dependencyProvider.getRecordIndexerFactory();
		assertTrue(recordIndexerFactory instanceof SolrRecordIndexerFactory);
	}

	@Test
	public void testDependencyProviderReturnsDifferentIndexers() {
		RecordIndexer recordIndexer = dependencyProvider.getRecordIndexer();
		RecordIndexer recordIndexer2 = dependencyProvider.getRecordIndexer();
		assertNotSame(recordIndexer, recordIndexer2);
	}

	@Test
	public void testGetRecordIndexerUsesSolrUrlWhenCreatingSolrClientProvider() {
		SolrRecordIndexer recordIndexer = (SolrRecordIndexer) dependencyProvider.getRecordIndexer();
		SolrClientProviderImp solrClientProviderImp = (SolrClientProviderImp) recordIndexer
				.getSolrClientProvider();
		assertEquals(solrClientProviderImp.getBaseURL(), "http://localhost:8983/solr/stuff");
	}

	@Test
	public void testMissingSolrUrlInInitInfo() {
		settings.remove("solrURL");

		Exception thrownException = callSystemOneDependencyProviderAndReturnResultingError();

		assertTrue(thrownException instanceof SpiderInitializationException);
		assertEquals(thrownException.getMessage(),
				"InitInfo in TheRestDependencyProvider must contain: solrURL");
		assertEquals(loggerFactorySpy.getNoOfFatalLogMessagesUsingClassName(testedBaseClassName),
				1);
		assertEquals(loggerFactorySpy.getFatalLogMessageUsingClassNameAndNo(testedBaseClassName, 0),
				"InitInfo in TheRestDependencyProvider must contain: solrURL");
	}

	@Test
	public void testGetRecordSearchUsesSolrUrlWhenCreatingSolrClientProvider() {
		SolrRecordSearch recordSearcher = (SolrRecordSearch) dependencyProvider.getRecordSearch();
		SolrClientProviderImp solrClientProviderImp = (SolrClientProviderImp) recordSearcher
				.getSolrClientProvider();
		assertEquals(solrClientProviderImp.getBaseURL(), "http://localhost:8983/solr/stuff");
	}

	@Test
	public void testGetRecordSearchUsesRecordStorageAsSearchStorage() {
		SearchStorageViewInstanceProviderSpy instanceProviderSpy = new SearchStorageViewInstanceProviderSpy();
		SearchStorageProvider.onlyForTestSetSearchStorageViewInstanceProvider(instanceProviderSpy);

		SolrRecordSearch recordSearcher = (SolrRecordSearch) dependencyProvider.getRecordSearch();

		SearchStorageView searchStorageView = recordSearcher.onlyForTestGetSearchStorageView();
		instanceProviderSpy.MCR.assertReturn("getStorageView", 0, searchStorageView);
	}

	@Test
	public void testDependencyProviderReturnsDifferentRecordSearch() {
		RecordSearch recordSearch = dependencyProvider.getRecordSearch();
		RecordSearch recordSearch2 = dependencyProvider.getRecordSearch();
		assertNotEquals(recordSearch, recordSearch2);
	}

}