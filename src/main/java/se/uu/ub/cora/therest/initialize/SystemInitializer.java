/*
 * Copyright 2015 Uppsala University Library
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
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import se.uu.ub.cora.spider.dependency.SpiderDependencyProvider;
import se.uu.ub.cora.spider.dependency.SpiderInstanceFactory;
import se.uu.ub.cora.spider.dependency.SpiderInstanceFactoryImp;
import se.uu.ub.cora.spider.dependency.SpiderInstanceProvider;

@WebListener
public class SystemInitializer implements ServletContextListener {
	public SpiderDependencyProvider dependencyProvider;
	private ServletContext servletContext;
	private Map<String, String> initInfo;

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		servletContext = arg0.getServletContext();
		try {
			tryToInitialize();
		} catch (InvocationTargetException e) {
			throw new RuntimeException(
					"Error starting The Rest: " + e.getTargetException().getMessage());
		} catch (Exception e) {
			throw new RuntimeException("Error starting The Rest: " + e.getMessage());
		}
	}

	private void tryToInitialize() throws InstantiationException, IllegalAccessException,
			ClassNotFoundException, InvocationTargetException, NoSuchMethodException {
		String dependencyProviderString = getClassNameToInitializeAsDependencyProviderFromContext();
		collectInitInformation();
		createInstanceOfDependencyProviderClass(dependencyProviderString);
		createAndSetFactoryInSpiderInstanceProvider();
	}

	private void collectInitInformation() {
		initInfo = new HashMap<>();
		Enumeration<String> initParameterNames = servletContext.getInitParameterNames();
		while (initParameterNames.hasMoreElements()) {
			String key = initParameterNames.nextElement();
			initInfo.put(key, servletContext.getInitParameter(key));
		}
	}

	private String getClassNameToInitializeAsDependencyProviderFromContext() {
		String initParameter = servletContext.getInitParameter("dependencyProviderClassName");
		if (initParameter == null) {
			throw new RuntimeException("Context must have a dependencyProviderClassName set.");
		}
		return initParameter;
	}

	private void createInstanceOfDependencyProviderClass(String dependencyProviderString)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException,
			InvocationTargetException, NoSuchMethodException, RuntimeException {
		Constructor<?> constructor = Class.forName(dependencyProviderString)
				.getConstructor(Map.class);
		dependencyProvider = (SpiderDependencyProvider) constructor.newInstance(initInfo);
	}

	private void createAndSetFactoryInSpiderInstanceProvider() {
		SpiderInstanceFactory factory = SpiderInstanceFactoryImp
				.usingDependencyProvider(dependencyProvider);
		SpiderInstanceProvider.setSpiderInstanceFactory(factory);
	}

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		// not sure we need anything here
	}
}
