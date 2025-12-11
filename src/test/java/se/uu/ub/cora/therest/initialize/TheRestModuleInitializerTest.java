/*
 * Copyright 2019 Olov McKie
 * Copyright 2019, 2024 Uppsala University Library
 *
 * This file is part of Cora.
 *
 * Cora is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Cora is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Cora. If not, see <http://www.gnu.org/licenses/>.
 */
package se.uu.ub.cora.therest.initialize;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.util.Map;
import java.util.ServiceLoader;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import se.uu.ub.cora.initialize.InitializationException;
import se.uu.ub.cora.initialize.SettingsProvider;
import se.uu.ub.cora.logger.LoggerProvider;
import se.uu.ub.cora.logger.spies.LoggerFactorySpy;
import se.uu.ub.cora.logger.spies.LoggerSpy;
import se.uu.ub.cora.messaging.AmqpMessageListenerRoutingInfo;
import se.uu.ub.cora.messaging.MessagingProvider;
import se.uu.ub.cora.spider.cache.DataChangeMessageReceiver;
import se.uu.ub.cora.storage.StreamStorageProvider;
import se.uu.ub.cora.storage.archive.RecordArchiveProvider;
import se.uu.ub.cora.therest.cache.spies.MessageListenerSpy;
import se.uu.ub.cora.therest.cache.spies.MessagingFactorySpy;

public class TheRestModuleInitializerTest {
	private ServletContext source;
	private ServletContextEvent context;
	private TheRestModuleInitializer initializer;
	private LoggerFactorySpy loggerFactorySpy;
	private MessagingFactorySpy messagingFactory;
	private LoggerSpy loggerSettingsProvider;
	private LoggerSpy loggerRestInit;

	@BeforeMethod
	public void beforeMethod() {

		loggerFactorySpy = new LoggerFactorySpy();
		LoggerProvider.setLoggerFactory(loggerFactorySpy);

		loggerRestInit = new LoggerSpy();
		loggerFactorySpy.MRV.setSpecificReturnValuesSupplier("factorForClass", () -> loggerRestInit,
				TheRestModuleInitializer.class);

		loggerSettingsProvider = new LoggerSpy();
		SettingsProvider.onlyForTestSetLogger(loggerSettingsProvider);

		messagingFactory = new MessagingFactorySpy();
		MessagingProvider.setMessagingFactory(messagingFactory);

		source = new ServletContextSpy();
		setNeededInitParameters();
		context = new ServletContextEvent(source);
		initializer = new TheRestModuleInitializer();
	}

	@AfterMethod
	private void afterMethod() {
		SettingsProvider.onlyForTestClearLoggedNames();
	}

	private void setNeededInitParameters() {
		source.setInitParameter("dependencyProviderClassName",
				"se.uu.ub.cora.therest.initialize.DependencyProviderSpy");
		source.setInitParameter("initParam1", "initValue1");
		source.setInitParameter("initParam2", "initValue2");
		source.setInitParameter("rabbitMqHostname", "someHostname");
		source.setInitParameter("rabbitMqPort", "6666");
		source.setInitParameter("rabbitMqVirtualHost", "someVirtualHost");
		source.setInitParameter("rabbitMqDataExchange", "someExchange");
	}

	@Test
	public void testNonExceptionThrowingStartup() {
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
	public void testLogMessagesOnStartup() {
		startTheRestModuleInitializerWithStarterSpy();
		loggerRestInit.MCR.assertParameter("logInfoUsingMessage", 0, "message",
				"TheRestModuleInitializer starting...");

		loggerSettingsProvider.MCR.assertParameter("logInfoUsingMessage", 0, "message",
				"Found: se.uu.ub.cora.therest.initialize.DependencyProviderSpy as: "
						+ "dependencyProviderClassName");
		loggerSettingsProvider.MCR.assertParameter("logInfoUsingMessage", 1, "message",
				"Found: someHostname as: rabbitMqHostname");
		loggerSettingsProvider.MCR.assertParameter("logInfoUsingMessage", 2, "message",
				"Found: 6666 as: rabbitMqPort");
		loggerSettingsProvider.MCR.assertParameter("logInfoUsingMessage", 3, "message",
				"Found: someVirtualHost as: rabbitMqVirtualHost");
		loggerSettingsProvider.MCR.assertParameter("logInfoUsingMessage", 4, "message",
				"Found: someExchange as: rabbitMqDataExchange");
		loggerSettingsProvider.MCR.assertNumberOfCallsToMethod("logInfoUsingMessage", 5);

		loggerRestInit.MCR.assertParameter("logInfoUsingMessage", 1, "message",
				"TheRestModuleInitializer started");
	}

	@Test(expectedExceptions = InitializationException.class, expectedExceptionsMessageRegExp = ""
			+ "Setting name: dependencyProviderClassName not found in SettingsProvider.")
	public void testErrorIsThrownIfMissingDependencyProviderClassName() {
		source = new ServletContextSpy();
		source.setInitParameter("theRestPublicPathToSystem", "/therest/rest/");
		context = new ServletContextEvent(source);
		startTheRestModuleInitializerWithStarterSpy();
	}

	@Test
	public void testErrorIsLoggedIfMissingDependencyProviderClassName() {
		source = new ServletContextSpy();
		context = new ServletContextEvent(source);
		try {
			startTheRestModuleInitializerWithStarterSpy();
			fail();
		} catch (Exception _) {
			loggerSettingsProvider.MCR.assertNumberOfCallsToMethod("logFatalUsingMessage", 1);
			loggerSettingsProvider.MCR.assertParameter("logFatalUsingMessage", 0, "message",
					"Setting name: dependencyProviderClassName not found in SettingsProvider.");
		}
	}

	@Test
	public void testSettingsProviderIsInitialized() {
		startTheRestModuleInitializerWithStarterSpy();

		assertEquals(SettingsProvider.getSetting("dependencyProviderClassName"),
				"se.uu.ub.cora.therest.initialize.DependencyProviderSpy");
		assertEquals(SettingsProvider.getSetting("initParam1"), "initValue1");
		assertEquals(SettingsProvider.getSetting("initParam2"), "initValue2");
	}

	@Test
	public void testInitParametersArePassedOnToStarter() {
		TheRestModuleStarterSpy starter = startTheRestModuleInitializerWithStarterSpy();
		Map<String, String> initInfo = getInitInfoFromStarterSpy(starter);

		assertEquals(initInfo.size(), 7);
		assertEquals(initInfo.get("dependencyProviderClassName"),
				"se.uu.ub.cora.therest.initialize.DependencyProviderSpy");
		assertEquals(initInfo.get("initParam1"), "initValue1");
		assertEquals(initInfo.get("initParam2"), "initValue2");
	}

	private Map<String, String> getInitInfoFromStarterSpy(TheRestModuleStarterSpy starter) {
		return (Map<String, String>) starter.MCR.getParameterForMethodAndCallNumberAndParameter(
				"startUsingInitInfoAndProviders", 0, "initInfo");
	}

	private Providers getProviders(TheRestModuleStarterSpy starter) {
		return (Providers) starter.MCR.getParameterForMethodAndCallNumberAndParameter(
				"startUsingInitInfoAndProviders", 0, "providers");
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
	public void testInitUsesDefaultTheRestModuleStarter() {
		TheRestModuleStarterImp starter = (TheRestModuleStarterImp) initializer
				.onlyForTestGetStarter();
		assertStarterIsTheRestModuleStarter(starter);
	}

	private void assertStarterIsTheRestModuleStarter(TheRestModuleStarter starter) {
		assertTrue(starter instanceof TheRestModuleStarterImp);
	}

	@Test
	public void testStartListening() {
		startTheRestModuleInitializerWithStarterSpy();

		messagingFactory.MCR.assertMethodWasCalled("factorTopicMessageListener");
		assertRoutingInfo();
		var listener = assertListenerAndReturn();
		assertMessageReceiver(listener);
	}

	private void assertRoutingInfo() {
		var routingInfo = (AmqpMessageListenerRoutingInfo) messagingFactory.MCR
				.getParameterForMethodAndCallNumberAndParameter("factorTopicMessageListener", 0,
						"messagingRoutingInfo");
		assertEquals(routingInfo.hostname, "someHostname");
		assertEquals(routingInfo.port, 6666);
		assertEquals(routingInfo.virtualHost, "someVirtualHost");
		assertEquals(routingInfo.exchange, "someExchange");
		assertEquals(routingInfo.routingKey, "*");
	}

	private MessageListenerSpy assertListenerAndReturn() {
		var listener = (MessageListenerSpy) messagingFactory.MCR
				.getReturnValue("factorTopicMessageListener", 0);
		listener.MCR.assertMethodWasCalled("listen");
		return listener;
	}

	private void assertMessageReceiver(MessageListenerSpy listener) {
		var messageReceiver = listener.MCR.getParameterForMethodAndCallNumberAndParameter("listen",
				0, "messageReceiver");
		assertTrue(messageReceiver instanceof DataChangeMessageReceiver);
	}

}
