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

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import static org.testng.Assert.assertEquals;

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
		source.setInitParameter("dependencyProviderClassName",
				"se.uu.ub.cora.therest.initialize.DependencyProviderSpy");
		systemInitializer.contextInitialized(context);

		assertEquals(systemInitializer.dependencyProvider.getClass(),
				DependencyProviderSpy.class);
	}

	@Test(expectedExceptions = RuntimeException.class)
	public void testInitializeSystemWithoutDependencyProviderClassName() {
		systemInitializer.contextInitialized(context);
	}

	@Test
	public void testInitializeSystemInitInfoSetInDependencyProvider() {
		source.setInitParameter("dependencyProviderClassName",
				"se.uu.ub.cora.therest.initialize.DependencyProviderSpy");
		systemInitializer.contextInitialized(context);

		DependencyProviderSpy dependencyProviderSpy = (DependencyProviderSpy) systemInitializer.dependencyProvider;
		assertEquals(dependencyProviderSpy.getInitInfo().get("dependencyProviderClassName"),
				"se.uu.ub.cora.therest.initialize.DependencyProviderSpy");
	}

	@Test(expectedExceptions = RuntimeException.class)
	public void testInitializeSystemTargetInvokationError() {
		source.setInitParameter("dependencyProviderClassName",
				"se.uu.ub.cora.therest.initialize.DependencyProviderMissingGatekeeperUrlSpy");
		systemInitializer.contextInitialized(context);
	}

	@Test
	public void testDestroySystem() {
		systemInitializer.contextDestroyed(null);
		// TODO: should we do something on destroy?
	}
}
