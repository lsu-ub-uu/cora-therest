/*
 * Copyright 2025 Uppsala University Library
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
package se.uu.ub.cora.therest.dependency;

import static org.testng.Assert.assertTrue;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.therest.converter.EndpointIncomingConverter;
import se.uu.ub.cora.therest.converter.EndpointOutgoingConverter;
import se.uu.ub.cora.therest.error.ErrorHandler;
import se.uu.ub.cora.therest.record.EndpointSearch;
import se.uu.ub.cora.therest.url.UrlHandler;

public class TheRestInstanceProviderTest {
	private TheRestInstanceFactorySpy factory;

	@BeforeMethod
	private void beforeMethod() {
		TheRestInstanceProvider.onlyForTestResetTheRestInstanceFactory();
	}

	@AfterMethod
	private void afterMethod() {
		TheRestInstanceProvider.onlyForTestResetTheRestInstanceFactory();
	}

	private void setSpyFactory() {
		factory = new TheRestInstanceFactorySpy();
		TheRestInstanceProvider.onlyForTestSetTheRestInstanceFactory(factory);
	}

	@Test
	public void testDefaultInstanceFactoryIsSet() {
		TheRestInstanceFactory defaultFactory = TheRestInstanceProvider
				.onlyForTestGetTheRestInstanceFactory();
		assertTrue(defaultFactory instanceof TheRestInstanceFactoryImp);
	}

	@Test(dependsOnMethods = "testDefaultInstanceFactoryIsSet")
	public void testPrivateConstructor() throws Exception {
		Constructor<TheRestInstanceProvider> constructor = TheRestInstanceProvider.class
				.getDeclaredConstructor();
		Assert.assertTrue(Modifier.isPrivate(constructor.getModifiers()));
	}

	@Test(dependsOnMethods = "testDefaultInstanceFactoryIsSet", expectedExceptions = InvocationTargetException.class)
	public void testPrivateConstructorInvoke() throws Exception {
		Constructor<TheRestInstanceProvider> constructor = TheRestInstanceProvider.class
				.getDeclaredConstructor();
		Assert.assertTrue(Modifier.isPrivate(constructor.getModifiers()));
		constructor.setAccessible(true);
		constructor.newInstance();
	}

	@Test(dependsOnMethods = "testDefaultInstanceFactoryIsSet")
	public void testGetUrlHandler() {
		setSpyFactory();
		UrlHandler uh = TheRestInstanceProvider.getUrlHandler();

		factory.MCR.assertReturn("factorUrlHandler", 0, uh);
	}

	@Test(dependsOnMethods = "testDefaultInstanceFactoryIsSet")
	public void testGetErrorHandler() {
		setSpyFactory();
		ErrorHandler eh = TheRestInstanceProvider.getErrorHandler();

		factory.MCR.assertReturn("factorErrorHandler", 0, eh);
	}

	@Test(dependsOnMethods = "testDefaultInstanceFactoryIsSet")
	public void testGetEndpointOutgoingConverter() {
		setSpyFactory();
		EndpointOutgoingConverter ec = TheRestInstanceProvider.getEndpointOutgoingConverter();

		factory.MCR.assertReturn("factorEndpointOutgoingConverter", 0, ec);
	}

	@Test(dependsOnMethods = "testDefaultInstanceFactoryIsSet")
	public void testGetEndpointIncomingConverter() {
		setSpyFactory();
		EndpointIncomingConverter ec = TheRestInstanceProvider.getEndpointIncomingConverter();

		factory.MCR.assertReturn("factorEndpointIncomingConverter", 0, ec);
	}

	@Test(dependsOnMethods = "testDefaultInstanceFactoryIsSet")
	public void testGetRecordSearch() {
		setSpyFactory();
		EndpointSearch es = TheRestInstanceProvider.getEndpointSearch();

		factory.MCR.assertReturn("factorEndpointSearch", 0, es);
	}

	@Test(dependsOnMethods = "testDefaultInstanceFactoryIsSet")
	public void testGetRecordSearchDecorated() {
		setSpyFactory();
		EndpointSearch es = TheRestInstanceProvider.getEndpointSearchDecorated();

		factory.MCR.assertReturn("factorEndpointSearchDecorated", 0, es);
	}

}
