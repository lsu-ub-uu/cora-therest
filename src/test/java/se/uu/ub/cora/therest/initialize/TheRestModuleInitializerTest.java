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
import static org.testng.Assert.assertTrue;

import java.util.Map;
import java.util.ServiceLoader;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.logger.LoggerProvider;
import se.uu.ub.cora.storage.RecordStorageProvider;
import se.uu.ub.cora.therest.log.LoggerFactorySpy;

public class TheRestModuleInitializerTest {
	private ServletContext source;
	private ServletContextEvent context;
	private TheRestModuleInitializer initializer;
	private LoggerFactorySpy loggerFactorySpy;
	private String testedClassName = "TheRestModuleInitializer";

	@BeforeMethod
	public void beforeMethod() {
		loggerFactorySpy = new LoggerFactorySpy();
		LoggerProvider.setLoggerFactory(loggerFactorySpy);
		source = new ServletContextSpy();
		source.setInitParameter("initParam1", "initValue1");
		source.setInitParameter("initParam2", "initValue2");
		context = new ServletContextEvent(source);
		initializer = new TheRestModuleInitializer();
	}

	@Test
	public void testNonExceptionThrowingStartup() throws Exception {
		TheRestModuleStarterSpy starter = startTheRestModuleInitializerWithStarterSpy();
		assertTrue(starter.startWasCalled);
	}

	private TheRestModuleStarterSpy startTheRestModuleInitializerWithStarterSpy() {
		TheRestModuleStarterSpy starter = new TheRestModuleStarterSpy();
		initializer.setStarter(starter);
		initializer.contextInitialized(context);
		return starter;
	}

	@Test
	public void testLogMessagesOnStartup() throws Exception {
		startTheRestModuleInitializerWithStarterSpy();
		assertEquals(loggerFactorySpy.getInfoLogMessageUsingClassNameAndNo(testedClassName, 0),
				"TheRestModuleInitializer starting...");
		assertEquals(loggerFactorySpy.getInfoLogMessageUsingClassNameAndNo(testedClassName, 1),
				"TheRestModuleInitializer started");
	}

	@Test
	public void testInitParametersArePassedOnToStarter() {
		TheRestModuleStarterSpy starter = startTheRestModuleInitializerWithStarterSpy();
		Map<String, String> initInfo = starter.initInfo;
		assertEquals(initInfo.size(), 2);
		assertEquals(initInfo.get("initParam1"), "initValue1");
		assertEquals(initInfo.get("initParam2"), "initValue2");
	}

	@Test
	public void testRecordStorageProviderImplementationsArePassedOnToStarter() {
		TheRestModuleStarterSpy starter = startTheRestModuleInitializerWithStarterSpy();

		Iterable<RecordStorageProvider> iterable = starter.recordStorageProviderImplementations;
		assertTrue(iterable instanceof ServiceLoader);
	}

	// @Test
	// public void testUserStorageProviderImplementationsArePassedOnToStarter() {
	// TheRestModuleStarterSpy starter = startTheRestModuleInitializerWithStarterSpy();
	//
	// Iterable<UserStorageProvider> iterable = starter.userStorageProviderImplementations;
	// assertTrue(iterable instanceof ServiceLoader);
	// }
	//
	@Test
	public void testInitUsesDefaultTheRestModuleStarter() throws Exception {
		// makeSureErrorIsThrownAsNoImplementationsExistInThisModule();
		TheRestModuleStarterImp starter = (TheRestModuleStarterImp) initializer.getStarter();
		assertStarterIsTheRestModuleStarter(starter);
	}

	// private void makeSureErrorIsThrownAsNoImplementationsExistInThisModule() {
	// Exception caughtException = null;
	// try {
	// initializer.contextInitialized(context);
	// } catch (Exception e) {
	// caughtException = e;
	// }
	// assertTrue(caughtException instanceof TheRestInitializationException);
	// assertEquals(caughtException.getMessage(),
	// "No implementations found for UserPickerProvider");
	// }

	private void assertStarterIsTheRestModuleStarter(TheRestModuleStarter starter) {
		assertTrue(starter instanceof TheRestModuleStarterImp);
	}

}
