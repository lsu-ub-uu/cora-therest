/*
 * Copyright 2019 Olov McKie
 * Copyright 2019, 2022, 2024 Uppsala University Library
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
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.logger.LoggerProvider;
import se.uu.ub.cora.logger.spies.LoggerFactorySpy;
import se.uu.ub.cora.logger.spies.LoggerSpy;
import se.uu.ub.cora.spider.dependency.SpiderInstanceProvider;
import se.uu.ub.cora.storage.StreamStorageProvider;
import se.uu.ub.cora.storage.archive.RecordArchiveProvider;
import se.uu.ub.cora.storage.idgenerator.RecordIdGeneratorProvider;

public class TheRestModuleStarterTest {

	private Map<String, String> initInfo;
	private LoggerFactorySpy loggerFactorySpy;
	private String testedClassName = "TheRestModuleStarterImp";
	private Providers providers;
	private TheRestModuleStarterImp starter;

	@BeforeMethod
	public void beforeMethod() {
		loggerFactorySpy = new LoggerFactorySpy();
		LoggerProvider.setLoggerFactory(loggerFactorySpy);
		initInfo = new HashMap<>();
		initInfo.put("guestUserId", "someGuestUserId");
		initInfo.put("dependencyProviderClassName",
				"se.uu.ub.cora.therest.initialize.DependencyProviderSpy");
		providers = new Providers();

		List<StreamStorageProvider> streamStorageProviders = new ArrayList<>();
		streamStorageProviders.add(new StreamStorageProviderSpy());
		providers.streamStorageProviderImplementations = streamStorageProviders;

		List<RecordArchiveProvider> recordArchiveProviders = new ArrayList<>();
		recordArchiveProviders.add(new RecordArchiveProviderSpy());
		providers.recordArchiveProviderImplementations = recordArchiveProviders;

		List<RecordIdGeneratorProvider> recordIdGeneratorProviders = new ArrayList<>();
		recordIdGeneratorProviders.add(new RecordIdGeneratorProviderSpy());
		providers.recordIdGeneratorProviderImplementations = recordIdGeneratorProviders;
	}

	@Test
	public void testDependencyProviderStartedAndAddedToSpiderInstanceProvider() throws Exception {
		startTheRestModuleStarter();
		String dependencyProviderClassName = SpiderInstanceProvider
				.getDependencyProviderClassName();
		assertEquals(dependencyProviderClassName, initInfo.get("dependencyProviderClassName"));
	}

	@Test(expectedExceptions = TheRestInitializationException.class, expectedExceptionsMessageRegExp = ""
			+ "Error starting The Rest: se.uu.ub.cora.therest.initialize.NOTEXISTING")
	public void testErrorIsThrownIfStartupOfDependencyProviderFails() throws Exception {
		initInfo.put("dependencyProviderClassName", "se.uu.ub.cora.therest.initialize.NOTEXISTING");
		startTheRestModuleStarter();
		String dependencyProviderClassName = SpiderInstanceProvider
				.getDependencyProviderClassName();
		assertEquals(dependencyProviderClassName, initInfo.get("dependencyProviderClassName"));
	}

	@Test
	public void testErrorIsThrownIfStartupOfDependencyProviderFailsInitialExceptionIsSentAlong()
			throws Exception {
		initInfo.put("dependencyProviderClassName", "se.uu.ub.cora.therest.initialize.NOTEXISTING");
		try {
			startTheRestModuleStarter();
			String dependencyProviderClassName = SpiderInstanceProvider
					.getDependencyProviderClassName();
			assertEquals(dependencyProviderClassName, initInfo.get("dependencyProviderClassName"));

		} catch (Exception e) {
			assertTrue(e.getCause() instanceof ClassNotFoundException);
		}
	}

	// @Test
	// public void testStartModuleInitInfoSentToRecordStorageProviderImplementation()
	// throws Exception {
	// RecordStorageProviderSpy recordStorageProvider = (RecordStorageProviderSpy)
	// providers.recordStorageProviderImplementations
	// .iterator().next();
	// startTheRestModuleStarter();
	// assertSame(recordStorageProvider.initInfo, initInfo);
	// }

	private void startTheRestModuleStarter() {
		starter = new TheRestModuleStarterImp();
		starter.startUsingInitInfoAndProviders(initInfo, providers);
	}

	private void startTheRestMakeSureAnExceptionIsThrown() {
		Exception caughtException = null;
		try {
			startTheRestModuleStarter();
		} catch (Exception e) {
			caughtException = e;
		}
		assertNotNull(caughtException);
	}

	@Test
	public void testStartModuleInitInfoSentToRecordArchiveProviderImplementation()
			throws Exception {
		startTheRestModuleStarter();
		RecordArchiveProviderSpy recordArchiveProvider = (RecordArchiveProviderSpy) providers.recordArchiveProviderImplementations
				.iterator().next();

		recordArchiveProvider.MCR.assertParameters("startUsingInitInfo", 0, initInfo);
	}

	@Test(expectedExceptions = TheRestInitializationException.class, expectedExceptionsMessageRegExp = ""
			+ "No implementations found for RecordArchiveProvider")
	public void testStartModuleThrowsErrorIfNoRecordArchiveImplementations() throws Exception {
		providers.recordArchiveProviderImplementations = new ArrayList<>();
		startTheRestModuleStarter();
	}

	@Test
	public void testStartModuleLogsErrorIfNoRecordArchiveProviderImplementations()
			throws Exception {
		providers.recordArchiveProviderImplementations = new ArrayList<>();
		startTheRestMakeSureAnExceptionIsThrown();

		LoggerSpy loggerSpy = (LoggerSpy) loggerFactorySpy.MCR.getReturnValue("factorForClass", 0);
		loggerSpy.MCR.assertNumberOfCallsToMethod("logFatalUsingMessage", 1);
		loggerSpy.MCR.assertParameter("logFatalUsingMessage", 0, "message",
				"No implementations found for RecordArchiveProvider");
	}

	@Test
	public void testStartModuleLogsInfoIfMoreThanOneArchiveStorageProviderImplementations()
			throws Exception {
		List<RecordArchiveProvider> recordArchiveProviders = (List<RecordArchiveProvider>) providers.recordArchiveProviderImplementations;
		RecordArchiveProviderSpy archiveProvider = new RecordArchiveProviderSpy();
		archiveProvider.order = 13;
		recordArchiveProviders.add(archiveProvider);
		recordArchiveProviders.add(new RecordArchiveProviderSpy());

		startTheRestModuleStarter();

		LoggerSpy loggerSpy = (LoggerSpy) loggerFactorySpy.MCR.getReturnValue("factorForClass", 0);
		loggerSpy.MCR.assertNumberOfCallsToMethod("logInfoUsingMessage", 8);
		loggerSpy.MCR.assertParameter("logInfoUsingMessage", 2, "message",
				"Found se.uu.ub.cora.therest.initialize.RecordArchiveProviderSpy as "
						+ "RecordArchiveProvider implementation with select order 0.");

		loggerSpy.MCR.assertParameter("logInfoUsingMessage", 3, "message",
				"Found se.uu.ub.cora.therest.initialize.RecordArchiveProviderSpy as "
						+ "RecordArchiveProvider implementation with select order 13.");

		loggerSpy.MCR.assertParameter("logInfoUsingMessage", 4, "message",
				"Found se.uu.ub.cora.therest.initialize.RecordArchiveProviderSpy as "
						+ "RecordArchiveProvider implementation with select order 0.");
	}

	@Test
	public void testStartModuleLogsInfoAndStartsRecordArchiveProviderWithHigestSelectOrder()
			throws Exception {
		List<RecordArchiveProvider> recordArchiveProviders = (List<RecordArchiveProvider>) providers.recordArchiveProviderImplementations;
		RecordArchiveProviderSpy archiveProvider = new RecordArchiveProviderSpy();
		archiveProvider.order = 13;
		recordArchiveProviders.add(archiveProvider);
		recordArchiveProviders.add(new RecordArchiveProviderSpy());
		startTheRestModuleStarter();

		Iterator<RecordArchiveProvider> recordArchiveProviderIterator = providers.recordArchiveProviderImplementations
				.iterator();
		RecordArchiveProviderSpy recordArchiveProvider = (RecordArchiveProviderSpy) recordArchiveProviderIterator
				.next();
		RecordArchiveProviderSpy recordArchiveProvider13 = (RecordArchiveProviderSpy) recordArchiveProviderIterator
				.next();
		RecordArchiveProviderSpy recordArchiveProvider3 = (RecordArchiveProviderSpy) recordArchiveProviderIterator
				.next();
		recordArchiveProvider.MCR.assertMethodNotCalled("startUsingInitInfo");
		recordArchiveProvider13.MCR.assertParameters("startUsingInitInfo", 0, initInfo);
		recordArchiveProvider3.MCR.assertMethodNotCalled("startUsingInitInfo");

		LoggerSpy loggerSpy = (LoggerSpy) loggerFactorySpy.MCR.getReturnValue("factorForClass", 0);
		loggerSpy.MCR.assertNumberOfCallsToMethod("logInfoUsingMessage", 8);
		loggerSpy.MCR.assertParameter("logInfoUsingMessage", 5, "message",
				"Using se.uu.ub.cora.therest.initialize.RecordArchiveProviderSpy as "
						+ "RecordArchiveProvider implementation.");
	}

	@Test
	public void testChoosenRecordArchiveProviderIsUsedByDependencyProvider() throws Exception {
		startTheRestModuleStarter();
		RecordArchiveProviderSpy recordArchiveProvider = (RecordArchiveProviderSpy) providers.recordArchiveProviderImplementations
				.iterator().next();
		DependencyProviderSpy startedDependencyProvider = (DependencyProviderSpy) starter
				.getStartedDependencyProvider();
		assertSame(startedDependencyProvider.getRecordArchiveProvider(), recordArchiveProvider);
	}

	@Test
	public void testStartModuleInitInfoSentToStreamStorageProviderImplementation()
			throws Exception {
		StreamStorageProviderSpy streamStorageProvider = (StreamStorageProviderSpy) providers.streamStorageProviderImplementations
				.iterator().next();
		startTheRestModuleStarter();
		assertSame(streamStorageProvider.initInfo, initInfo);
	}

	@Test(expectedExceptions = TheRestInitializationException.class, expectedExceptionsMessageRegExp = ""
			+ "No implementations found for StreamStorageProvider")
	public void testStartModuleThrowsErrorIfNoStreamStorageImplementations() throws Exception {
		providers.streamStorageProviderImplementations = new ArrayList<>();
		startTheRestModuleStarter();
	}

	@Test
	public void testStartModuleLogsErrorIfNoStreamStorageProviderImplementations()
			throws Exception {
		providers.streamStorageProviderImplementations = new ArrayList<>();
		startTheRestMakeSureAnExceptionIsThrown();

		LoggerSpy loggerSpy = (LoggerSpy) loggerFactorySpy.MCR.getReturnValue("factorForClass", 0);
		loggerSpy.MCR.assertNumberOfCallsToMethod("logFatalUsingMessage", 1);
		loggerSpy.MCR.assertParameter("logFatalUsingMessage", 0, "message",
				"No implementations found for StreamStorageProvider");
	}

	@Test
	public void testStartModuleLogsInfoIfMoreThanOneStreamStorageProviderImplementations()
			throws Exception {
		List<StreamStorageProvider> streamStorageProviders = (List<StreamStorageProvider>) providers.streamStorageProviderImplementations;
		streamStorageProviders.add(new StreamStorageProviderSpy2());
		streamStorageProviders.add(new StreamStorageProviderSpy());
		startTheRestModuleStarter();

		LoggerSpy loggerSpy = (LoggerSpy) loggerFactorySpy.MCR.getReturnValue("factorForClass", 0);
		loggerSpy.MCR.assertNumberOfCallsToMethod("logInfoUsingMessage", 8);
		loggerSpy.MCR.assertParameter("logInfoUsingMessage", 0, "message",
				"Found se.uu.ub.cora.therest.initialize.StreamStorageProviderSpy as "
						+ "StreamStorageProvider implementation with select order 0.");
		loggerSpy.MCR.assertParameter("logInfoUsingMessage", 1, "message",
				"Found se.uu.ub.cora.therest.initialize.StreamStorageProviderSpy2 as "
						+ "StreamStorageProvider implementation with select order 10.");
		loggerSpy.MCR.assertParameter("logInfoUsingMessage", 2, "message",
				"Found se.uu.ub.cora.therest.initialize.StreamStorageProviderSpy as "
						+ "StreamStorageProvider implementation with select order 0.");
	}

	@Test
	public void testStartModuleLogsInfoAndStartsStreamStorageProviderWithHigestSelectOrder()
			throws Exception {
		List<StreamStorageProvider> streamStorageProviders = (List<StreamStorageProvider>) providers.streamStorageProviderImplementations;
		streamStorageProviders.add(new StreamStorageProviderSpy2());
		streamStorageProviders.add(new StreamStorageProviderSpy());
		startTheRestModuleStarter();

		Iterator<StreamStorageProvider> streamStorageProviderIterator = providers.streamStorageProviderImplementations
				.iterator();
		StreamStorageProviderSpy streamStorageProvider = (StreamStorageProviderSpy) streamStorageProviderIterator
				.next();
		StreamStorageProviderSpy2 streamStorageProvider2 = (StreamStorageProviderSpy2) streamStorageProviderIterator
				.next();
		StreamStorageProviderSpy streamStorageProvider12 = (StreamStorageProviderSpy) streamStorageProviderIterator
				.next();
		assertSame(streamStorageProvider2.initInfo, initInfo);
		assertFalse(streamStorageProvider.started);
		assertTrue(streamStorageProvider2.started);
		assertFalse(streamStorageProvider12.started);

		LoggerSpy loggerSpy = (LoggerSpy) loggerFactorySpy.MCR.getReturnValue("factorForClass", 0);
		loggerSpy.MCR.assertNumberOfCallsToMethod("logInfoUsingMessage", 8);
		loggerSpy.MCR.assertParameter("logInfoUsingMessage", 3, "message",
				"Using se.uu.ub.cora.therest.initialize.StreamStorageProviderSpy2 as "
						+ "StreamStorageProvider implementation.");
	}

	@Test
	public void testChoosenStreamStorageProviderIsUsedByDependencyProvider() throws Exception {
		startTheRestModuleStarter();
		StreamStorageProviderSpy streamStorageProvider = (StreamStorageProviderSpy) providers.streamStorageProviderImplementations
				.iterator().next();
		DependencyProviderSpy startedDependencyProvider = (DependencyProviderSpy) starter
				.getStartedDependencyProvider();
		assertSame(startedDependencyProvider.getStreamStorageProvider(), streamStorageProvider);
	}

	@Test
	public void testStartModuleInitInfoSentToRecordIdGeneratorProviderImplementation()
			throws Exception {
		RecordIdGeneratorProviderSpy recordIdGeneratorProvider = (RecordIdGeneratorProviderSpy) providers.recordIdGeneratorProviderImplementations
				.iterator().next();
		startTheRestModuleStarter();
		assertSame(recordIdGeneratorProvider.initInfo, initInfo);
	}

	@Test(expectedExceptions = TheRestInitializationException.class, expectedExceptionsMessageRegExp = ""
			+ "No implementations found for RecordIdGeneratorProvider")
	public void testStartModuleThrowsErrorIfNoRecordIdGeneratorImplementations() throws Exception {
		providers.recordIdGeneratorProviderImplementations = new ArrayList<>();
		startTheRestModuleStarter();
	}

	@Test
	public void testStartModuleLogsErrorIfNoRecordIdGeneratorProviderImplementations()
			throws Exception {
		providers.recordIdGeneratorProviderImplementations = new ArrayList<>();
		startTheRestMakeSureAnExceptionIsThrown();

		LoggerSpy loggerSpy = (LoggerSpy) loggerFactorySpy.MCR.getReturnValue("factorForClass", 0);
		loggerSpy.MCR.assertNumberOfCallsToMethod("logFatalUsingMessage", 1);
		loggerSpy.MCR.assertParameter("logFatalUsingMessage", 0, "message",
				"No implementations found for RecordIdGeneratorProvider");
	}

	@Test
	public void testStartModuleLogsInfoIfMoreThanOneRecordIdGeneratorProviderImplementations()
			throws Exception {
		List<RecordIdGeneratorProvider> recordIdGeneratorProviders = (List<RecordIdGeneratorProvider>) providers.recordIdGeneratorProviderImplementations;
		recordIdGeneratorProviders.add(new RecordIdGeneratorProviderSpy2());
		recordIdGeneratorProviders.add(new RecordIdGeneratorProviderSpy());
		startTheRestModuleStarter();

		LoggerSpy loggerSpy = (LoggerSpy) loggerFactorySpy.MCR.getReturnValue("factorForClass", 0);
		loggerSpy.MCR.assertNumberOfCallsToMethod("logInfoUsingMessage", 8);
		loggerSpy.MCR.assertParameter("logInfoUsingMessage", 4, "message",
				"Found se.uu.ub.cora.therest.initialize.RecordIdGeneratorProviderSpy as "
						+ "RecordIdGeneratorProvider implementation with select order 0.");
		loggerSpy.MCR.assertParameter("logInfoUsingMessage", 5, "message",
				"Found se.uu.ub.cora.therest.initialize.RecordIdGeneratorProviderSpy2 as "
						+ "RecordIdGeneratorProvider implementation with select order 10.");
		loggerSpy.MCR.assertParameter("logInfoUsingMessage", 6, "message",
				"Found se.uu.ub.cora.therest.initialize.RecordIdGeneratorProviderSpy as "
						+ "RecordIdGeneratorProvider implementation with select order 0.");
	}

	@Test
	public void testStartModuleLogsInfoAndStartsRecordIdGeneratorProviderWithHigestSelectOrder()
			throws Exception {
		List<RecordIdGeneratorProvider> recordIdGeneratorProviders = (List<RecordIdGeneratorProvider>) providers.recordIdGeneratorProviderImplementations;
		recordIdGeneratorProviders.add(new RecordIdGeneratorProviderSpy2());
		recordIdGeneratorProviders.add(new RecordIdGeneratorProviderSpy());
		startTheRestModuleStarter();

		Iterator<RecordIdGeneratorProvider> recordIdGeneratorProviderIterator = providers.recordIdGeneratorProviderImplementations
				.iterator();
		RecordIdGeneratorProviderSpy recordIdGeneratorProvider = (RecordIdGeneratorProviderSpy) recordIdGeneratorProviderIterator
				.next();
		RecordIdGeneratorProviderSpy2 recordIdGeneratorProvider2 = (RecordIdGeneratorProviderSpy2) recordIdGeneratorProviderIterator
				.next();
		RecordIdGeneratorProviderSpy recordIdGeneratorProvider12 = (RecordIdGeneratorProviderSpy) recordIdGeneratorProviderIterator
				.next();
		assertSame(recordIdGeneratorProvider2.initInfo, initInfo);
		assertFalse(recordIdGeneratorProvider.started);
		assertTrue(recordIdGeneratorProvider2.started);
		assertFalse(recordIdGeneratorProvider12.started);

		LoggerSpy loggerSpy = (LoggerSpy) loggerFactorySpy.MCR.getReturnValue("factorForClass", 0);
		loggerSpy.MCR.assertNumberOfCallsToMethod("logInfoUsingMessage", 8);
		loggerSpy.MCR.assertParameter("logInfoUsingMessage", 7, "message",
				"Using se.uu.ub.cora.therest.initialize.RecordIdGeneratorProviderSpy2 as "
						+ "RecordIdGeneratorProvider implementation.");
	}

	@Test
	public void testChoosenRecordIdGeneratorProviderIsUsedByDependencyProvider() throws Exception {
		startTheRestModuleStarter();
		RecordIdGeneratorProviderSpy recordIdGeneratorProvider = (RecordIdGeneratorProviderSpy) providers.recordIdGeneratorProviderImplementations
				.iterator().next();
		DependencyProviderSpy startedDependencyProvider = (DependencyProviderSpy) starter
				.getStartedDependencyProvider();
		assertSame(startedDependencyProvider.getRecordIdGeneratorProvider(),
				recordIdGeneratorProvider);
	}

	@Test
	public void testInitializeExtendedFunctionalityHasBeenCalled() throws Exception {
		startTheRestModuleStarter();
		DependencyProviderSpy startedDependencyProvider = (DependencyProviderSpy) starter
				.getStartedDependencyProvider();
		assertTrue(startedDependencyProvider.initializeExtendedFunctionalityHasBeenCalled);
	}
}
