/*
 * Copyright 2015, 2017, 2018, 2019 Uppsala University Library
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
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.gatekeeperclient.authentication.AuthenticatorImp;
import se.uu.ub.cora.logger.LoggerProvider;
import se.uu.ub.cora.metacreator.extended.MetacreatorExtendedFunctionalityProvider;
import se.uu.ub.cora.solr.SolrClientProviderImp;
import se.uu.ub.cora.solrindex.SolrRecordIndexer;
import se.uu.ub.cora.solrsearch.SolrRecordSearch;
import se.uu.ub.cora.spider.authorization.PermissionRuleCalculator;
import se.uu.ub.cora.spider.dependency.SpiderInitializationException;
import se.uu.ub.cora.spider.record.RecordSearch;
import se.uu.ub.cora.spider.search.RecordIndexer;
import se.uu.ub.cora.storage.MetadataStorageProvider;
import se.uu.ub.cora.storage.RecordIdGeneratorProvider;
import se.uu.ub.cora.storage.RecordStorageProvider;
import se.uu.ub.cora.storage.SearchStorage;
import se.uu.ub.cora.storage.StreamStorageProvider;
import se.uu.ub.cora.therest.log.LoggerFactorySpy;

public class TheRestDependencyProviderTest {
	private TheRestDependencyProvider dependencyProvider;
	private Map<String, String> initInfo;
	private LoggerFactorySpy loggerFactorySpy;
	private String testedBaseClassName = "SpiderDependencyProvider";

	@BeforeMethod
	public void setUp() throws Exception {
		loggerFactorySpy = new LoggerFactorySpy();
		LoggerProvider.setLoggerFactory(loggerFactorySpy);
		try {
			initInfo = new HashMap<>();
			initInfo.put("gatekeeperURL", "http://localhost:8080/gatekeeper/");
			initInfo.put("solrURL", "http://localhost:8983/solr/stuff");
			dependencyProvider = new TheRestDependencyProvider(initInfo);
			setPluggedInStorageNormallySetByTheRestModuleStarterImp();
		} catch (Exception e) {
			// Make the correct tests crash instead of all
		}

	}

	private void setPluggedInStorageNormallySetByTheRestModuleStarterImp() {
		RecordStorageProvider recordStorageProvider = new RecordStorageProviderSpy();
		dependencyProvider.setRecordStorageProvider(recordStorageProvider);
		StreamStorageProvider streamStorageProvider = new StreamStorageProviderSpy();
		dependencyProvider.setStreamStorageProvider(streamStorageProvider);
		RecordIdGeneratorProvider recordIdGeneratorProvider = new RecordIdGeneratorProviderSpy();
		dependencyProvider.setRecordIdGeneratorProvider(recordIdGeneratorProvider);
		MetadataStorageProvider metadataStorageProvider = new MetadataStorageProviderSpy();
		dependencyProvider.setMetadataStorageProvider(metadataStorageProvider);
	}

	@Test
	public void testInit() {
		assertNotNull(dependencyProvider.getExtendedFunctionalityProvider());
		assertTrue(dependencyProvider.getAuthenticator() instanceof AuthenticatorImp);
		assertTrue(dependencyProvider
				.getExtendedFunctionalityProvider() instanceof MetacreatorExtendedFunctionalityProvider);
		assertTrue(dependencyProvider.getRecordIndexer() instanceof SolrRecordIndexer);
	}

	private Exception callSystemOneDependencyProviderAndReturnResultingError() {
		Exception thrownException = null;
		try {
			dependencyProvider = new TheRestDependencyProvider(initInfo);
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
		initInfo.remove("gatekeeperURL");

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
		assertEquals(authenticator.getBaseURL(), initInfo.get("gatekeeperURL"));
	}

	@Test
	public void testGetRecordSearch() {
		assertNotNull(dependencyProvider.getRecordSearch());
	}

	@Test
	public void testDependencyProviderReturnsOnlyOneInstanceOfRecordndexer() {
		RecordIndexer recordIndexer = dependencyProvider.getRecordIndexer();
		RecordIndexer recordIndexer2 = dependencyProvider.getRecordIndexer();
		assertEquals(recordIndexer, recordIndexer2);
	}

	@Test
	public void testMissingSolrUrlInInitInfo() {
		initInfo.remove("solrURL");

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
	public void testGetRecordIndexerUsesSolrUrlWhenCreatingSolrClientProvider() {
		SolrRecordIndexer recordIndexer = (SolrRecordIndexer) dependencyProvider.getRecordIndexer();
		SolrClientProviderImp solrClientProviderImp = (SolrClientProviderImp) recordIndexer
				.getSolrClientProvider();
		assertEquals(solrClientProviderImp.getBaseURL(), "http://localhost:8983/solr/stuff");
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
		SolrRecordSearch recordSearcher = (SolrRecordSearch) dependencyProvider.getRecordSearch();
		SearchStorage searchStorage = recordSearcher.getSearchStorage();
		assertSame(searchStorage, dependencyProvider.getRecordStorage());
	}

	@Test
	public void testDependencyProviderReturnsDifferentRecordSearch() {
		RecordSearch recordSearch = dependencyProvider.getRecordSearch();
		RecordSearch recordSearch2 = dependencyProvider.getRecordSearch();
		assertNotEquals(recordSearch, recordSearch2);
	}

}