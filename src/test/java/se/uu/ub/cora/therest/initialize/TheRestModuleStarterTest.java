/*
 * Copyright 2019 Olov McKie
 * Copyright 2019 Uppsala University Library
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
import se.uu.ub.cora.spider.dependency.SpiderInstanceProvider;
import se.uu.ub.cora.storage.MetadataStorageProvider;
import se.uu.ub.cora.storage.RecordIdGeneratorProvider;
import se.uu.ub.cora.storage.RecordStorageProvider;
import se.uu.ub.cora.storage.StreamStorageProvider;
import se.uu.ub.cora.therest.log.LoggerFactorySpy;

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

		List<RecordStorageProvider> recordStorageProviders = new ArrayList<>();
		recordStorageProviders.add(new RecordStorageProviderSpy());
		providers.recordStorageProviderImplementations = recordStorageProviders;

		List<StreamStorageProvider> streamStorageProviders = new ArrayList<>();
		streamStorageProviders.add(new StreamStorageProviderSpy());
		providers.streamStorageProviderImplementations = streamStorageProviders;

		List<RecordIdGeneratorProvider> recordIdGeneratorProviders = new ArrayList<>();
		recordIdGeneratorProviders.add(new RecordIdGeneratorProviderSpy());
		providers.recordIdGeneratorProviderImplementations = recordIdGeneratorProviders;

		List<MetadataStorageProvider> metadataStorageProviders = new ArrayList<>();
		metadataStorageProviders.add(new MetadataStorageProviderSpy());
		providers.metadataStorageProviderImplementations = metadataStorageProviders;
	}

	@Test
	public void testInitInfoSetInSpiderInstanceProvider() throws Exception {
		startTheRestModuleStarter();
		assertSame(SpiderInstanceProvider.getInitInfo(), initInfo);
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
	public void testErrorIsThrownIfStarupOfDependencyProviderFails() throws Exception {
		initInfo.put("dependencyProviderClassName", "se.uu.ub.cora.therest.initialize.NOTEXISTING");
		startTheRestModuleStarter();
		String dependencyProviderClassName = SpiderInstanceProvider
				.getDependencyProviderClassName();
		assertEquals(dependencyProviderClassName, initInfo.get("dependencyProviderClassName"));
	}

	@Test
	public void testStartModuleInitInfoSentToRecordStorageProviderImplementation()
			throws Exception {
		RecordStorageProviderSpy recordStorageProvider = (RecordStorageProviderSpy) providers.recordStorageProviderImplementations
				.iterator().next();
		startTheRestModuleStarter();
		assertSame(recordStorageProvider.initInfo, initInfo);
	}

	private void startTheRestModuleStarter() {
		starter = new TheRestModuleStarterImp();
		starter.startUsingInitInfoAndProviders(initInfo, providers);
	}

	@Test(expectedExceptions = TheRestInitializationException.class, expectedExceptionsMessageRegExp = ""
			+ "No implementations found for RecordStorageProvider")
	public void testStartModuleThrowsErrorIfNoRecordStorageImplementations() throws Exception {
		providers.recordStorageProviderImplementations = new ArrayList<>();
		startTheRestModuleStarter();
	}

	@Test
	public void testStartModuleLogsErrorIfNoRecordStorageProviderImplementations()
			throws Exception {
		providers.recordStorageProviderImplementations = new ArrayList<>();
		startTheRestMakeSureAnExceptionIsThrown();
		assertEquals(loggerFactorySpy.getFatalLogMessageUsingClassNameAndNo(testedClassName, 0),
				"No implementations found for RecordStorageProvider");
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
	public void testStartModuleLogsInfoIfMoreThanOneRecordStorageProviderImplementations()
			throws Exception {
		List<RecordStorageProvider> recordStorageProviders = (List<RecordStorageProvider>) providers.recordStorageProviderImplementations;
		recordStorageProviders.add(new RecordStorageProviderSpy2());
		recordStorageProviders.add(new RecordStorageProviderSpy());
		startTheRestModuleStarter();
		assertEquals(loggerFactorySpy.getInfoLogMessageUsingClassNameAndNo(testedClassName, 0),
				"Found se.uu.ub.cora.therest.initialize.RecordStorageProviderSpy as "
						+ "RecordStorageProvider implementation with select order 0.");
		assertEquals(loggerFactorySpy.getInfoLogMessageUsingClassNameAndNo(testedClassName, 1),
				"Found se.uu.ub.cora.therest.initialize.RecordStorageProviderSpy2 as "
						+ "RecordStorageProvider implementation with select order 10.");
		assertEquals(loggerFactorySpy.getInfoLogMessageUsingClassNameAndNo(testedClassName, 2),
				"Found se.uu.ub.cora.therest.initialize.RecordStorageProviderSpy as "
						+ "RecordStorageProvider implementation with select order 0.");
	}

	@Test
	public void testStartModuleLogsInfoAndStartsRecordStorageProviderWithHigestSelectOrder()
			throws Exception {
		List<RecordStorageProvider> recordStorageProviders = (List<RecordStorageProvider>) providers.recordStorageProviderImplementations;
		recordStorageProviders.add(new RecordStorageProviderSpy2());
		recordStorageProviders.add(new RecordStorageProviderSpy());
		startTheRestModuleStarter();

		assertEquals(loggerFactorySpy.getInfoLogMessageUsingClassNameAndNo(testedClassName, 3),
				"Using se.uu.ub.cora.therest.initialize.RecordStorageProviderSpy2 as "
						+ "RecordStorageProvider implementation.");
		Iterator<RecordStorageProvider> recordStorageProviderIterator = providers.recordStorageProviderImplementations
				.iterator();
		RecordStorageProviderSpy recordStorageProvider = (RecordStorageProviderSpy) recordStorageProviderIterator
				.next();
		RecordStorageProviderSpy2 recordStorageProvider2 = (RecordStorageProviderSpy2) recordStorageProviderIterator
				.next();
		RecordStorageProviderSpy recordStorageProvider12 = (RecordStorageProviderSpy) recordStorageProviderIterator
				.next();
		assertSame(recordStorageProvider2.initInfo, initInfo);
		assertFalse(recordStorageProvider.started);
		assertTrue(recordStorageProvider2.started);
		assertFalse(recordStorageProvider12.started);
	}

	@Test
	public void testChoosenRecordStorageProviderIsUsedByDependencyProvider() throws Exception {
		startTheRestModuleStarter();
		RecordStorageProviderSpy recordStorageProvider = (RecordStorageProviderSpy) providers.recordStorageProviderImplementations
				.iterator().next();
		DependencyProviderSpy startedDependencyProvider = (DependencyProviderSpy) starter
				.getStartedDependencyProvider();
		assertSame(startedDependencyProvider.getRecordStorageProvider(), recordStorageProvider);
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
		assertEquals(loggerFactorySpy.getFatalLogMessageUsingClassNameAndNo(testedClassName, 0),
				"No implementations found for StreamStorageProvider");
	}

	@Test
	public void testStartModuleLogsInfoIfMoreThanOneStreamStorageProviderImplementations()
			throws Exception {
		List<StreamStorageProvider> streamStorageProviders = (List<StreamStorageProvider>) providers.streamStorageProviderImplementations;
		streamStorageProviders.add(new StreamStorageProviderSpy2());
		streamStorageProviders.add(new StreamStorageProviderSpy());
		startTheRestModuleStarter();
		assertEquals(loggerFactorySpy.getInfoLogMessageUsingClassNameAndNo(testedClassName, 2),
				"Found se.uu.ub.cora.therest.initialize.StreamStorageProviderSpy as "
						+ "StreamStorageProvider implementation with select order 0.");
		assertEquals(loggerFactorySpy.getInfoLogMessageUsingClassNameAndNo(testedClassName, 3),
				"Found se.uu.ub.cora.therest.initialize.StreamStorageProviderSpy2 as "
						+ "StreamStorageProvider implementation with select order 10.");
		assertEquals(loggerFactorySpy.getInfoLogMessageUsingClassNameAndNo(testedClassName, 4),
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

		assertEquals(loggerFactorySpy.getInfoLogMessageUsingClassNameAndNo(testedClassName, 5),
				"Using se.uu.ub.cora.therest.initialize.StreamStorageProviderSpy2 as "
						+ "StreamStorageProvider implementation.");
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
		assertEquals(loggerFactorySpy.getFatalLogMessageUsingClassNameAndNo(testedClassName, 0),
				"No implementations found for RecordIdGeneratorProvider");
	}

	@Test
	public void testStartModuleLogsInfoIfMoreThanOneRecordIdGeneratorProviderImplementations()
			throws Exception {
		List<RecordIdGeneratorProvider> recordIdGeneratorProviders = (List<RecordIdGeneratorProvider>) providers.recordIdGeneratorProviderImplementations;
		recordIdGeneratorProviders.add(new RecordIdGeneratorProviderSpy2());
		recordIdGeneratorProviders.add(new RecordIdGeneratorProviderSpy());
		startTheRestModuleStarter();
		assertEquals(loggerFactorySpy.getInfoLogMessageUsingClassNameAndNo(testedClassName, 4),
				"Found se.uu.ub.cora.therest.initialize.RecordIdGeneratorProviderSpy as "
						+ "RecordIdGeneratorProvider implementation with select order 0.");
		assertEquals(loggerFactorySpy.getInfoLogMessageUsingClassNameAndNo(testedClassName, 5),
				"Found se.uu.ub.cora.therest.initialize.RecordIdGeneratorProviderSpy2 as "
						+ "RecordIdGeneratorProvider implementation with select order 10.");
		assertEquals(loggerFactorySpy.getInfoLogMessageUsingClassNameAndNo(testedClassName, 6),
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

		assertEquals(loggerFactorySpy.getInfoLogMessageUsingClassNameAndNo(testedClassName, 7),
				"Using se.uu.ub.cora.therest.initialize.RecordIdGeneratorProviderSpy2 as "
						+ "RecordIdGeneratorProvider implementation.");
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
	public void testStartModuleInitInfoSentToMetadataStorageProviderImplementation()
			throws Exception {
		MetadataStorageProviderSpy metadataStorageProvider = (MetadataStorageProviderSpy) providers.metadataStorageProviderImplementations
				.iterator().next();
		startTheRestModuleStarter();
		assertSame(metadataStorageProvider.initInfo, initInfo);
	}

	@Test(expectedExceptions = TheRestInitializationException.class, expectedExceptionsMessageRegExp = ""
			+ "No implementations found for MetadataStorageProvider")
	public void testStartModuleThrowsErrorIfNoMetadataStorageImplementations() throws Exception {
		providers.metadataStorageProviderImplementations = new ArrayList<>();
		startTheRestModuleStarter();
	}

	@Test
	public void testStartModuleLogsErrorIfNoMetadataStorageProviderImplementations()
			throws Exception {
		providers.metadataStorageProviderImplementations = new ArrayList<>();
		startTheRestMakeSureAnExceptionIsThrown();
		assertEquals(loggerFactorySpy.getFatalLogMessageUsingClassNameAndNo(testedClassName, 0),
				"No implementations found for MetadataStorageProvider");
	}

	@Test
	public void testStartModuleLogsInfoIfMoreThanOneMetadataStorageProviderImplementations()
			throws Exception {
		List<MetadataStorageProvider> metadataStorageProviders = (List<MetadataStorageProvider>) providers.metadataStorageProviderImplementations;
		metadataStorageProviders.add(new MetadataStorageProviderSpy2());
		metadataStorageProviders.add(new MetadataStorageProviderSpy());
		startTheRestModuleStarter();
		assertEquals(loggerFactorySpy.getInfoLogMessageUsingClassNameAndNo(testedClassName, 6),
				"Found se.uu.ub.cora.therest.initialize.MetadataStorageProviderSpy as "
						+ "MetadataStorageProvider implementation with select order 0.");
		assertEquals(loggerFactorySpy.getInfoLogMessageUsingClassNameAndNo(testedClassName, 7),
				"Found se.uu.ub.cora.therest.initialize.MetadataStorageProviderSpy2 as "
						+ "MetadataStorageProvider implementation with select order 10.");
		assertEquals(loggerFactorySpy.getInfoLogMessageUsingClassNameAndNo(testedClassName, 8),
				"Found se.uu.ub.cora.therest.initialize.MetadataStorageProviderSpy as "
						+ "MetadataStorageProvider implementation with select order 0.");
	}

	@Test
	public void testStartModuleLogsInfoAndStartsMetadataStorageProviderWithHigestSelectOrder()
			throws Exception {
		List<MetadataStorageProvider> metadataStorageProviders = (List<MetadataStorageProvider>) providers.metadataStorageProviderImplementations;
		metadataStorageProviders.add(new MetadataStorageProviderSpy2());
		metadataStorageProviders.add(new MetadataStorageProviderSpy());
		startTheRestModuleStarter();

		assertEquals(loggerFactorySpy.getInfoLogMessageUsingClassNameAndNo(testedClassName, 9),
				"Using se.uu.ub.cora.therest.initialize.MetadataStorageProviderSpy2 as "
						+ "MetadataStorageProvider implementation.");
		Iterator<MetadataStorageProvider> metadataStorageProviderIterator = providers.metadataStorageProviderImplementations
				.iterator();
		MetadataStorageProviderSpy metadataStorageProvider = (MetadataStorageProviderSpy) metadataStorageProviderIterator
				.next();
		MetadataStorageProviderSpy2 metadataStorageProvider2 = (MetadataStorageProviderSpy2) metadataStorageProviderIterator
				.next();
		MetadataStorageProviderSpy metadataStorageProvider12 = (MetadataStorageProviderSpy) metadataStorageProviderIterator
				.next();
		assertSame(metadataStorageProvider2.initInfo, initInfo);
		assertFalse(metadataStorageProvider.started);
		assertTrue(metadataStorageProvider2.started);
		assertFalse(metadataStorageProvider12.started);
	}

	@Test
	public void testChoosenMetadataStorageProviderIsUsedByDependencyProvider() throws Exception {
		startTheRestModuleStarter();
		MetadataStorageProviderSpy metadataStorageProvider = (MetadataStorageProviderSpy) providers.metadataStorageProviderImplementations
				.iterator().next();
		DependencyProviderSpy startedDependencyProvider = (DependencyProviderSpy) starter
				.getStartedDependencyProvider();
		assertSame(startedDependencyProvider.getMetadataStorageProvider(), metadataStorageProvider);
	}
}
