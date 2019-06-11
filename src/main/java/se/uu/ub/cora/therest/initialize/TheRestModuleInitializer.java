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

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import se.uu.ub.cora.logger.Logger;
import se.uu.ub.cora.logger.LoggerProvider;
import se.uu.ub.cora.storage.MetadataStorageProvider;
import se.uu.ub.cora.storage.RecordIdGeneratorProvider;
import se.uu.ub.cora.storage.RecordStorageProvider;
import se.uu.ub.cora.storage.StreamStorageProvider;

@WebListener
public class TheRestModuleInitializer implements ServletContextListener {
	private TheRestModuleStarter starter = new TheRestModuleStarterImp();
	private ServletContext servletContext;
	private HashMap<String, String> initInfo = new HashMap<>();
	private Logger log = LoggerProvider.getLoggerForClass(TheRestModuleInitializer.class);
	private Providers providers = new Providers();

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		servletContext = arg0.getServletContext();
		initializeTheRest();
	}

	private void initializeTheRest() {
		String simpleName = TheRestModuleInitializer.class.getSimpleName();
		log.logInfoUsingMessage(simpleName + " starting...");
		collectInitInformation();
		ensureNeededParametersExistsInInitInfo();
		collectProviderImplementationsAndAddToProviders();
		startTheRestStarter();
		log.logInfoUsingMessage(simpleName + " started");
	}

	private void ensureNeededParametersExistsInInitInfo() {
		tryToGetInitParameter("theRestPublicPathToSystem");
		tryToGetInitParameter("dependencyProviderClassName");
	}

	private String tryToGetInitParameter(String parameterName) {
		throwErrorIfKeyIsMissingFromInitInfo(parameterName);
		String parameter = initInfo.get(parameterName);
		log.logInfoUsingMessage("Found " + parameter + " as " + parameterName);
		return parameter;
	}

	private void throwErrorIfKeyIsMissingFromInitInfo(String key) {
		if (!initInfo.containsKey(key)) {
			String errorMessage = "InitInfo must contain " + key;
			log.logFatalUsingMessage(errorMessage);
			throw new TheRestInitializationException(errorMessage);
		}
	}

	private void collectInitInformation() {
		Enumeration<String> initParameterNames = servletContext.getInitParameterNames();
		while (initParameterNames.hasMoreElements()) {
			String key = initParameterNames.nextElement();
			initInfo.put(key, servletContext.getInitParameter(key));
		}
	}

	private void collectProviderImplementationsAndAddToProviders() {
		providers.recordStorageProviderImplementations = ServiceLoader
				.load(RecordStorageProvider.class);
		providers.streamStorageProviderImplementations = ServiceLoader
				.load(StreamStorageProvider.class);
		providers.recordIdGeneratorProviderImplementations = ServiceLoader
				.load(RecordIdGeneratorProvider.class);
		providers.metadataStorageProviderImplementations = ServiceLoader
				.load(MetadataStorageProvider.class);
	}

	private void startTheRestStarter() {
		starter.startUsingInitInfoAndProviders(initInfo, providers);
	}

	TheRestModuleStarter getStarter() {
		// needed for test
		return starter;
	}

	void setStarter(TheRestModuleStarter starter) {
		// needed for test
		this.starter = starter;
	}

}
