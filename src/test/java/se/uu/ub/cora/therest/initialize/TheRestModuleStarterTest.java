/*
 * Copyright 2019 Olov McKie
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
import se.uu.ub.cora.storage.RecordStorageProvider;
import se.uu.ub.cora.therest.log.LoggerFactorySpy;

public class TheRestModuleStarterTest {

	private Map<String, String> initInfo;
	// private List<UserPickerProvider> userPickerProviders;
	// private List<RecordStorageProvider> RecordStorageProviders;
	private LoggerFactorySpy loggerFactorySpy;
	private String testedClassName = "TheRestModuleStarterImp";
	private Providers providers;

	@BeforeMethod
	public void beforeMethod() {
		loggerFactorySpy = new LoggerFactorySpy();
		LoggerProvider.setLoggerFactory(loggerFactorySpy);
		initInfo = new HashMap<>();
		initInfo.put("guestUserId", "someGuestUserId");
		providers = new Providers();

		List<RecordStorageProvider> recordStorageProviders = new ArrayList<>();
		recordStorageProviders.add(new RecordStorageProviderSpy());
		providers.recordStorageProviderImplementations = recordStorageProviders;

		// userPickerProviders = new ArrayList<>();
		// userPickerProviders.add(new UserPickerProviderSpy(null));
		// RecordStorageProviders = new ArrayList<>();
		// RecordStorageProviders.add(new RecordStorageProviderSpy());

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
		TheRestModuleStarter starter = new TheRestModuleStarterImp();
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

	// @Test
	// public void testStartModuleInitInfoSentToRecordStorageProviderImplementation() throws
	// Exception
	// {
	// RecordStorageProviderSpy RecordStorageProviderSpy = (RecordStorageProviderSpy)
	// RecordStorageProviders
	// .get(0);
	// startTheRestModuleStarter();
	// assertSame(RecordStorageProviderSpy.initInfo, initInfo);
	// }
	//
	// @Test
	// public void testStartModuleLogsImplementationDetails() throws Exception {
	// startTheRestModuleStarter();
	// assertEquals(loggerFactorySpy.getInfoLogMessageUsingClassNameAndNo(testedClassName, 0),
	// "Found se.uu.ub.cora.TheRestserver.initialize.UserPickerProviderSpy as "
	// + "UserPickerProvider implementation with select order 0.");
	// assertEquals(loggerFactorySpy.getInfoLogMessageUsingClassNameAndNo(testedClassName, 1),
	// "Using se.uu.ub.cora.TheRestserver.initialize.UserPickerProviderSpy as "
	// + "UserPickerProvider implementation.");
	// assertEquals(loggerFactorySpy.getInfoLogMessageUsingClassNameAndNo(testedClassName, 2),
	// "Found se.uu.ub.cora.TheRestserver.initialize.RecordStorageProviderSpy as "
	// + "RecordStorageProvider implementation with select order 0.");
	// assertEquals(loggerFactorySpy.getInfoLogMessageUsingClassNameAndNo(testedClassName, 3),
	// "Using se.uu.ub.cora.TheRestserver.initialize.RecordStorageProviderSpy as "
	// + "RecordStorageProvider implementation.");
	// assertEquals(loggerFactorySpy.getInfoLogMessageUsingClassNameAndNo(testedClassName, 4),
	// "Found someGuestUserId as guestUserId");
	// }
	//
	// @Test(expectedExceptions = TheRestInitializationException.class,
	// expectedExceptionsMessageRegExp = ""
	// + "No implementations found for UserPickerProvider")
	// public void testStartModuleThrowsErrorIfNoUserPickerProviderImplementations() throws
	// Exception {
	// userPickerProviders.clear();
	// startTheRestModuleStarter();
	// }
	//
	// @Test
	// public void testStartModuleLogsErrorIfNoUserPickerProviderImplementations() throws Exception
	// {
	// userPickerProviders.clear();
	// startTheRestMakeSureAnExceptionIsThrown();
	// assertEquals(loggerFactorySpy.getFatalLogMessageUsingClassNameAndNo(testedClassName, 0),
	// "No implementations found for UserPickerProvider");
	// }
	//

	// @Test
	// public void testStartModuleLogsErrorIfMoreThanOneUserPickerProviderImplementations()
	// throws Exception {
	// userPickerProviders.add(new UserPickerProviderSpy2(null));
	// userPickerProviders.add(new UserPickerProviderSpy(null));
	// startTheRestModuleStarter();
	// String testedClassName = "TheRestModuleStarterImp";
	// assertEquals(loggerFactorySpy.getInfoLogMessageUsingClassNameAndNo(testedClassName, 0),
	// "Found se.uu.ub.cora.TheRestserver.initialize.UserPickerProviderSpy as "
	// + "UserPickerProvider implementation with select order 0.");
	// assertEquals(loggerFactorySpy.getInfoLogMessageUsingClassNameAndNo(testedClassName, 1),
	// "Found se.uu.ub.cora.TheRestserver.initialize.UserPickerProviderSpy2 as "
	// + "UserPickerProvider implementation with select order 2.");
	// assertEquals(loggerFactorySpy.getInfoLogMessageUsingClassNameAndNo(testedClassName, 2),
	// "Found se.uu.ub.cora.TheRestserver.initialize.UserPickerProviderSpy as "
	// + "UserPickerProvider implementation with select order 0.");
	//
	// assertEquals(loggerFactorySpy.getInfoLogMessageUsingClassNameAndNo(testedClassName, 3),
	// "Using se.uu.ub.cora.TheRestserver.initialize.UserPickerProviderSpy2 as "
	// + "UserPickerProvider implementation.");
	//
	// }
	//
	// @Test(expectedExceptions = TheRestInitializationException.class,
	// expectedExceptionsMessageRegExp = ""
	// + "InitInfo must contain guestUserId")
	// public void testStartModuleThrowsErrorIfMissingGuestUserId() throws Exception {
	// initInfo.clear();
	// startTheRestModuleStarter();
	// }
	//
	// @Test
	// public void testStartModuleLogsErrorIfMissingGuestUserId() throws Exception {
	// initInfo.clear();
	// startTheRestMakeSureAnExceptionIsThrown();
	// assertEquals(loggerFactorySpy.getFatalLogMessageUsingClassNameAndNo(testedClassName, 0),
	// "InitInfo must contain guestUserId");
	// }
	//
	// @Test()
	// public void testStartModuleGuestUserIdSentToUserPickerImplementation() throws Exception {
	// UserPickerProviderSpy userPickerProviderSpy = (UserPickerProviderSpy) userPickerProviders
	// .get(0);
	// startTheRestModuleStarter();
	// assertEquals(userPickerProviderSpy.guestUserId(), "someGuestUserId");
	// }
	//
	// @Test()
	// public void testStartModuleRecordStorageSentToUserPickerImplementation() throws Exception {
	// UserPickerProviderSpy userPickerProviderSpy = (UserPickerProviderSpy) userPickerProviders
	// .get(0);
	// RecordStorageProviderSpy RecordStorageProviderSpy = (RecordStorageProviderSpy)
	// RecordStorageProviders
	// .get(0);
	// RecordStorage RecordStorage = RecordStorageProviderSpy.getRecordStorage();
	// startTheRestModuleStarter();
	// assertEquals(userPickerProviderSpy.getRecordStorage(), RecordStorage);
	// }
	//
	// @Test
	// public void testTheRestInstanceProviderSetUpWithLocator() throws Exception {
	// UserPickerProviderSpy userPickerProviderSpy = (UserPickerProviderSpy) userPickerProviders
	// .get(0);
	// startTheRestModuleStarter();
	// assertTrue(TheRestImp.INSTANCE.getUserPickerProvider() instanceof UserPickerProviderSpy);
	// assertSame(TheRestImp.INSTANCE.getUserPickerProvider(), userPickerProviderSpy);
	// assertNotNull(TheRestInstanceProvider.getTheRest());
	// }
}
