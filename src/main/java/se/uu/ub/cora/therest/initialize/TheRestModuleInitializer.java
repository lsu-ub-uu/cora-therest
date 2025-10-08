/*
 * Copyright 2019 Olov McKie
 * Copyright 2019 Uppsala University Library
 * 
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

import java.util.Enumeration;
import java.util.HashMap;
import java.util.ServiceLoader;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import se.uu.ub.cora.initialize.SettingsProvider;
import se.uu.ub.cora.logger.Logger;
import se.uu.ub.cora.logger.LoggerProvider;
import se.uu.ub.cora.messaging.AmqpMessageListenerRoutingInfo;
import se.uu.ub.cora.messaging.MessageRoutingInfo;
import se.uu.ub.cora.messaging.MessagingProvider;
import se.uu.ub.cora.spider.cache.DataChangeMessageReceiver;
import se.uu.ub.cora.storage.StreamStorageProvider;
import se.uu.ub.cora.storage.archive.RecordArchiveProvider;

@WebListener
public class TheRestModuleInitializer implements ServletContextListener {
	private TheRestModuleStarter starter = new TheRestModuleStarterImp();
	private ServletContext servletContext;

	private Logger log = LoggerProvider.getLoggerForClass(TheRestModuleInitializer.class);
	private Providers providers = new Providers();
	private HashMap<String, String> initInfo;

	@Override
	public void contextInitialized(ServletContextEvent contextEvent) {
		servletContext = contextEvent.getServletContext();
		initializeTheRest();
	}

	private void initializeTheRest() {
		String simpleName = TheRestModuleInitializer.class.getSimpleName();
		log.logInfoUsingMessage(simpleName + " starting...");

		collectInitInformationIntoSettingsProvider();
		ensureNeededParametersExistsInInitInfo();
		collectProviderImplementationsAndAddToProviders();
		startTheRestStarter();
		startListenForDataChangesForUser();

		log.logInfoUsingMessage(simpleName + " started");
	}

	private void ensureNeededParametersExistsInInitInfo() {
		SettingsProvider.getSetting("theRestPublicPathToSystem");
		SettingsProvider.getSetting("dependencyProviderClassName");
	}

	private void startListenForDataChangesForUser() {
		MessageRoutingInfo routingInfo = createRoutingInfo();
		var listener = MessagingProvider.getTopicMessageListener(routingInfo);

		listener.listen(new DataChangeMessageReceiver());
	}

	private MessageRoutingInfo createRoutingInfo() {
		String hostname = SettingsProvider.getSetting("rabbitMqHostname");
		int port = Integer.parseInt(SettingsProvider.getSetting("rabbitMqPort"));
		String virtualHost = SettingsProvider.getSetting("rabbitMqVirtualHost");
		String exchange = SettingsProvider.getSetting("rabbitMqDataExchange");
		String routingKey = "*";

		return new AmqpMessageListenerRoutingInfo(hostname, port, virtualHost, exchange,
				routingKey);
	}

	private void collectInitInformationIntoSettingsProvider() {
		Enumeration<String> initParameterNames = servletContext.getInitParameterNames();
		initInfo = new HashMap<>();
		while (initParameterNames.hasMoreElements()) {
			String key = initParameterNames.nextElement();
			initInfo.put(key, servletContext.getInitParameter(key));
		}
		SettingsProvider.setSettings(initInfo);
	}

	private void collectProviderImplementationsAndAddToProviders() {
		providers.streamStorageProviderImplementations = ServiceLoader
				.load(StreamStorageProvider.class);
		providers.recordArchiveProviderImplementations = ServiceLoader
				.load(RecordArchiveProvider.class);
	}

	private void startTheRestStarter() {
		starter.startUsingInitInfoAndProviders(initInfo, providers);
	}

	TheRestModuleStarter onlyForTestGetStarter() {
		return starter;
	}

	void onlyForTestSetStarter(TheRestModuleStarter starter) {
		this.starter = starter;
	}

}
