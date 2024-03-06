/*
 * Copyright 2019 Olov McKie
 * Copyright 2019, 2024 Uppsala University Library
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

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import se.uu.ub.cora.initialize.SettingsProvider;
import se.uu.ub.cora.logger.LoggerProvider;
import se.uu.ub.cora.logger.spies.LoggerFactorySpy;
import se.uu.ub.cora.logger.spies.LoggerSpy;
import se.uu.ub.cora.storage.StreamStorageProvider;
import se.uu.ub.cora.storage.archive.RecordArchiveProvider;
import se.uu.ub.cora.storage.idgenerator.RecordIdGeneratorProvider;

public class TheRestModuleInitializerTest {
	private ServletContext source;
	private ServletContextEvent context;
	private TheRestModuleInitializer initializer;
	private LoggerFactorySpy loggerFactorySpy;

	@BeforeMethod
	public void beforeMethod() {
		loggerFactorySpy = new LoggerFactorySpy();
		LoggerProvider.setLoggerFactory(loggerFactorySpy);
		source = new ServletContextSpy();
		source.setInitParameter("theRestPublicPathToSystem", "/therest/rest/");
		source.setInitParameter("dependencyProviderClassName",
				"se.uu.ub.cora.therest.initialize.DependencyProviderSpy");
		source.setInitParameter("initParam1", "initValue1");
		source.setInitParameter("initParam2", "initValue2");
		context = new ServletContextEvent(source);
		initializer = new TheRestModuleInitializer();
	}

	@Test
	public void testNonExceptionThrowingStartup() throws Exception {
		TheRestModuleStarterSpy starter = startTheRestModuleInitializerWithStarterSpy();
		starter.MCR.assertMethodWasCalled("startUsingInitInfoAndProviders");
	}

	private TheRestModuleStarterSpy startTheRestModuleInitializerWithStarterSpy() {
		TheRestModuleStarterSpy starter = new TheRestModuleStarterSpy();
		initializer.onlyForTestSetStarter(starter);
		initializer.contextInitialized(context);
		return starter;
	}

	@Test
	public void testLogMessagesOnStartup() throws Exception {
		startTheRestModuleInitializerWithStarterSpy();
		LoggerSpy loggerSpy = (LoggerSpy) loggerFactorySpy.MCR.getReturnValue("factorForClass", 1);
		loggerSpy.MCR.assertNumberOfCallsToMethod("logInfoUsingMessage", 4);
		loggerSpy.MCR.assertParameter("logInfoUsingMessage", 0, "message",
				"TheRestModuleInitializer starting...");
		loggerSpy.MCR.assertParameter("logInfoUsingMessage", 1, "message",
				"Found /therest/rest/ as theRestPublicPathToSystem");
		loggerSpy.MCR.assertParameter("logInfoUsingMessage", 2, "message",
				"Found se.uu.ub.cora.therest.initialize.DependencyProviderSpy as "
						+ "dependencyProviderClassName");
		loggerSpy.MCR.assertParameter("logInfoUsingMessage", 3, "message",
				"TheRestModuleInitializer started");
	}

	@Test(expectedExceptions = TheRestInitializationException.class, expectedExceptionsMessageRegExp = ""
			+ "InitInfo must contain theRestPublicPathToSystem")
	public void testErrorIsThrownIfMissingTheRestPublicPathToSystem() throws Exception {
		source = new ServletContextSpy();
		context = new ServletContextEvent(source);
		startTheRestModuleInitializerWithStarterSpy();
	}

	@Test
	public void testErrorIsLoggedIfMissingTheRestPublicPathToSystem() throws Exception {
		source = new ServletContextSpy();
		context = new ServletContextEvent(source);
		try {
			startTheRestModuleInitializerWithStarterSpy();
		} catch (Exception e) {

		}

		LoggerSpy loggerSpy = (LoggerSpy) loggerFactorySpy.MCR.getReturnValue("factorForClass", 1);
		loggerSpy.MCR.assertNumberOfCallsToMethod("logFatalUsingMessage", 1);
		loggerSpy.MCR.assertParameter("logFatalUsingMessage", 0, "message",
				"InitInfo must contain theRestPublicPathToSystem");
	}

	@Test(expectedExceptions = TheRestInitializationException.class, expectedExceptionsMessageRegExp = ""
			+ "InitInfo must contain dependencyProviderClassName")
	public void testErrorIsThrownIfMissingDependencyProviderClassName() throws Exception {
		source = new ServletContextSpy();
		source.setInitParameter("theRestPublicPathToSystem", "/therest/rest/");
		context = new ServletContextEvent(source);
		startTheRestModuleInitializerWithStarterSpy();
	}

	@Test
	public void testErrorIsLoggedIfMissingDependencyProviderClassName() throws Exception {
		source = new ServletContextSpy();
		source.setInitParameter("theRestPublicPathToSystem", "/therest/rest/");
		context = new ServletContextEvent(source);
		try {
			startTheRestModuleInitializerWithStarterSpy();
		} catch (Exception e) {

		}

		LoggerSpy loggerSpy = (LoggerSpy) loggerFactorySpy.MCR.getReturnValue("factorForClass", 1);
		loggerSpy.MCR.assertNumberOfCallsToMethod("logFatalUsingMessage", 1);
		loggerSpy.MCR.assertParameter("logFatalUsingMessage", 0, "message",
				"InitInfo must contain dependencyProviderClassName");
	}

	@Test
	public void testSettingsProviderIsInitialized() throws Exception {
		startTheRestModuleInitializerWithStarterSpy();

		assertEquals(SettingsProvider.getSetting("theRestPublicPathToSystem"), "/therest/rest/");
		assertEquals(SettingsProvider.getSetting("dependencyProviderClassName"),
				"se.uu.ub.cora.therest.initialize.DependencyProviderSpy");
		assertEquals(SettingsProvider.getSetting("initParam1"), "initValue1");
		assertEquals(SettingsProvider.getSetting("initParam2"), "initValue2");
	}

	@Test
	public void testInitParametersArePassedOnToStarter() {
		TheRestModuleStarterSpy starter = startTheRestModuleInitializerWithStarterSpy();
		Map<String, String> initInfo = getInitInfoFromStarterSpy(starter);

		assertEquals(initInfo.size(), 4);
		assertEquals(initInfo.get("theRestPublicPathToSystem"), "/therest/rest/");
		assertEquals(initInfo.get("dependencyProviderClassName"),
				"se.uu.ub.cora.therest.initialize.DependencyProviderSpy");
		assertEquals(initInfo.get("initParam1"), "initValue1");
		assertEquals(initInfo.get("initParam2"), "initValue2");
	}

	private Map<String, String> getInitInfoFromStarterSpy(TheRestModuleStarterSpy starter) {
		return (Map<String, String>) starter.MCR.getValueForMethodNameAndCallNumberAndParameterName(
				"startUsingInitInfoAndProviders", 0, "initInfo");
	}

	private Providers getProviders(TheRestModuleStarterSpy starter) {
		Providers providers = (Providers) starter.MCR
				.getValueForMethodNameAndCallNumberAndParameterName(
						"startUsingInitInfoAndProviders", 0, "providers");
		return providers;
	}

	@Test
	public void testStreamStorageProviderImplementationsArePassedOnToStarter() {
		TheRestModuleStarterSpy starter = startTheRestModuleInitializerWithStarterSpy();

		Providers providers = getProviders(starter);
		Iterable<StreamStorageProvider> provider = providers.streamStorageProviderImplementations;

		assertTrue(provider instanceof ServiceLoader);
	}

	@Test
	public void testRecordArchiveProviderImplementationsArePassedOnToStarter() {
		TheRestModuleStarterSpy starter = startTheRestModuleInitializerWithStarterSpy();

		Providers providers = getProviders(starter);

		Iterable<RecordArchiveProvider> provider = providers.recordArchiveProviderImplementations;

		assertTrue(provider instanceof ServiceLoader);
	}

	@Test
	public void testRecordIdGeneratorProviderImplementationsArePassedOnToStarter() {
		TheRestModuleStarterSpy starter = startTheRestModuleInitializerWithStarterSpy();

		Providers providers = getProviders(starter);
		Iterable<RecordIdGeneratorProvider> provider = providers.recordIdGeneratorProviderImplementations;

		assertTrue(provider instanceof ServiceLoader);
	}

	@Test
	public void testInitUsesDefaultTheRestModuleStarter() throws Exception {
		TheRestModuleStarterImp starter = (TheRestModuleStarterImp) initializer
				.onlyForTestGetStarter();
		assertStarterIsTheRestModuleStarter(starter);
	}

	private void assertStarterIsTheRestModuleStarter(TheRestModuleStarter starter) {
		assertTrue(starter instanceof TheRestModuleStarterImp);
	}

}
