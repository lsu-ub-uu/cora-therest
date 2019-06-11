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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import se.uu.ub.cora.logger.Logger;
import se.uu.ub.cora.logger.LoggerProvider;
import se.uu.ub.cora.spider.dependency.SpiderDependencyProvider;
import se.uu.ub.cora.spider.dependency.SpiderInstanceFactory;
import se.uu.ub.cora.spider.dependency.SpiderInstanceFactoryImp;
import se.uu.ub.cora.spider.dependency.SpiderInstanceProvider;
import se.uu.ub.cora.storage.MetadataStorageProvider;
import se.uu.ub.cora.storage.RecordIdGeneratorProvider;
import se.uu.ub.cora.storage.RecordStorageProvider;
import se.uu.ub.cora.storage.SelectOrder;
import se.uu.ub.cora.storage.StreamStorageProvider;

public class TheRestModuleStarterImp implements TheRestModuleStarter {
	private static final String FOUND = "Found ";
	private Map<String, String> initInfo;
	private Logger log = LoggerProvider.getLoggerForClass(TheRestModuleStarterImp.class);
	private Providers providers;
	private SpiderDependencyProvider dependencyProvider;

	@Override
	public void startUsingInitInfoAndProviders(Map<String, String> initInfo, Providers providers) {
		this.initInfo = initInfo;
		this.providers = providers;
		start();
	}

	public void start() {

		SpiderInstanceProvider.setInitInfo(initInfo);
		try {
			createInstanceOfDependencyProviderClass();
		} catch (Exception e) {
			throw new TheRestInitializationException("Error starting The Rest: " + e.getMessage());
		}
		createAndSetFactoryInSpiderInstanceProvider();

		RecordStorageProvider recordStorageProvider = getImplementationBasedOnPreferenceLevelThrowErrorIfNone(
				providers.recordStorageProviderImplementations, "RecordStorageProvider");
		recordStorageProvider.startUsingInitInfo(initInfo);
		dependencyProvider.setRecordStorageProvider(recordStorageProvider);

		StreamStorageProvider streamStorageProvider = getImplementationBasedOnPreferenceLevelThrowErrorIfNone(
				providers.streamStorageProviderImplementations, "StreamStorageProvider");
		streamStorageProvider.startUsingInitInfo(initInfo);
		dependencyProvider.setStreamStorageProvider(streamStorageProvider);

		RecordIdGeneratorProvider recordIdGeneratorProvider = getImplementationBasedOnPreferenceLevelThrowErrorIfNone(
				providers.recordIdGeneratorProviderImplementations, "RecordIdGeneratorProvider");
		recordIdGeneratorProvider.startUsingInitInfo(initInfo);
		dependencyProvider.setRecordIdGeneratorProvider(recordIdGeneratorProvider);

		MetadataStorageProvider metadataStorageProvider = getImplementationBasedOnPreferenceLevelThrowErrorIfNone(
				providers.metadataStorageProviderImplementations, "MetadataStorageProvider");
		metadataStorageProvider.startUsingInitInfo(initInfo);
		dependencyProvider.setMetadataStorageProvider(metadataStorageProvider);
	}

	private void createInstanceOfDependencyProviderClass()
			throws InstantiationException, IllegalAccessException, ClassNotFoundException,
			InvocationTargetException, NoSuchMethodException {
		String dependencyProviderString = initInfo.get("dependencyProviderClassName");
		Constructor<?> constructor = Class.forName(dependencyProviderString)
				.getConstructor(Map.class);
		dependencyProvider = (SpiderDependencyProvider) constructor.newInstance(initInfo);
	}

	private void createAndSetFactoryInSpiderInstanceProvider() {
		SpiderInstanceFactory factory = SpiderInstanceFactoryImp
				.usingDependencyProvider(dependencyProvider);
		SpiderInstanceProvider.setSpiderInstanceFactory(factory);
	}

	private <T extends SelectOrder> T getImplementationBasedOnPreferenceLevelThrowErrorIfNone(
			Iterable<T> implementations, String interfaceClassName) {
		T implementation = findAndLogPreferedImplementation(implementations, interfaceClassName);
		throwErrorIfNoImplementationFound(interfaceClassName, implementation);
		log.logInfoUsingMessage("Using " + implementation.getClass().getName() + " as "
				+ interfaceClassName + " implementation.");
		return implementation;
	}

	private <T extends SelectOrder> T findAndLogPreferedImplementation(Iterable<T> implementations,
			String interfaceClassName) {
		T implementation = null;
		int preferenceLevel = -99999;
		for (T currentImplementation : implementations) {
			if (preferenceLevel < currentImplementation.getOrderToSelectImplementionsBy()) {
				preferenceLevel = currentImplementation.getOrderToSelectImplementionsBy();
				implementation = currentImplementation;
			}
			log.logInfoUsingMessage(FOUND + currentImplementation.getClass().getName() + " as "
					+ interfaceClassName + " implementation with select order "
					+ currentImplementation.getOrderToSelectImplementionsBy() + ".");
		}
		return implementation;
	}

	private <T extends SelectOrder> void throwErrorIfNoImplementationFound(
			String interfaceClassName, T implementation) {
		if (null == implementation) {
			String errorMessage = "No implementations found for " + interfaceClassName;
			log.logFatalUsingMessage(errorMessage);
			throw new TheRestInitializationException(errorMessage);
		}
	}

	SpiderDependencyProvider getStartedDependencyProvider() {
		return dependencyProvider;
	}
}
