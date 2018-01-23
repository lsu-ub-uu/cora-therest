/*
 * Copyright 2015, 2018 Uppsala University Library
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

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.spider.dependency.SpiderInstanceProvider;
import se.uu.ub.cora.spider.record.SpiderRecordReader;

public class SystemInitializerTest {
	private SystemInitializer systemInitializer;
	private ServletContext source;
	private ServletContextEvent context;

	@BeforeMethod
	public void setUp() {
		systemInitializer = new SystemInitializer();
		source = new ServletContextSpy();
		context = new ServletContextEvent(source);
	}

	@Test
	public void testInitializeSystem() {
		setNeededInitParameters();
		systemInitializer.contextInitialized(context);

		assertEquals(systemInitializer.dependencyProvider.getClass(), DependencyProviderSpy.class);
	}

	private void setNeededInitParameters() {
		source.setInitParameter("dependencyProviderClassName",
				"se.uu.ub.cora.therest.initialize.DependencyProviderSpy");
		source.setInitParameter("publicPathToSystem", "/systemOne/rest/");
	}

	@Test(expectedExceptions = RuntimeException.class, expectedExceptionsMessageRegExp = "Error "
			+ "starting The Rest: Context must have a dependencyProviderClassName set.")
	public void testInitializeSystemWithoutDependencyProviderClassName() {
		systemInitializer.contextInitialized(context);
	}

	@Test
	public void testInitializeSystemInitInfoSetInDependencyProvider() {
		setNeededInitParameters();
		systemInitializer.contextInitialized(context);

		DependencyProviderSpy dependencyProviderSpy = (DependencyProviderSpy) systemInitializer.dependencyProvider;
		assertEquals(dependencyProviderSpy.getInitInfo().get("dependencyProviderClassName"),
				"se.uu.ub.cora.therest.initialize.DependencyProviderSpy");
	}

	@Test(expectedExceptions = RuntimeException.class, expectedExceptionsMessageRegExp = "Throwing "
			+ "exception from DependencyProviderSpy")
	public void testInitializeSystemTryInstanceProviderAndItsFactory() {
		setNeededInitParameters();
		systemInitializer.contextInitialized(context);

		assertTrue(SpiderInstanceProvider.getSpiderRecordReader() instanceof SpiderRecordReader);
	}

	@Test(expectedExceptions = RuntimeException.class, expectedExceptionsMessageRegExp = "Error"
			+ " invoking classes in The Rest: InitInfo must contain gatekeeperURL")
	public void testInitializeSystemTargetInvokationError() {
		source.setInitParameter("dependencyProviderClassName",
				"se.uu.ub.cora.therest.initialize.DependencyProviderMissingGatekeeperUrlSpy");
		source.setInitParameter("publicPathToSystem", "/systemOne/rest/");
		systemInitializer.contextInitialized(context);
	}

	@Test(expectedExceptions = RuntimeException.class, expectedExceptionsMessageRegExp = "Error "
			+ "starting The Rest: Context must have a publicPathToSystem set.")
	public void testInitializeMissingPublicPathToSystem() {
		source.setInitParameter("dependencyProviderClassName",
				"se.uu.ub.cora.therest.initialize.DependencyProviderSpy");
		systemInitializer.contextInitialized(context);
	}

	@Test
	public void testInitInfoSetInSpiderInstanceProvider() throws Exception {
		setNeededInitParameters();
		systemInitializer.contextInitialized(context);
		assertEquals(SpiderInstanceProvider.getInitInfo().get("publicPathToSystem"),
				"/systemOne/rest/");
	}

	@Test
	public void testDestroySystem() {
		systemInitializer.contextDestroyed(null);
		// TODO: should we do something on destroy?
	}
}
