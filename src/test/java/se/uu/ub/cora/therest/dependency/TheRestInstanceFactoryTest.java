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

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.json.parser.org.OrgJsonParser;
import se.uu.ub.cora.logger.LoggerProvider;
import se.uu.ub.cora.logger.spies.LoggerFactorySpy;
import se.uu.ub.cora.therest.converter.EndpointIncomingConverter;
import se.uu.ub.cora.therest.converter.EndpointIncomingConverterImp;
import se.uu.ub.cora.therest.converter.EndpointOutgoingConverter;
import se.uu.ub.cora.therest.converter.EndpointOutgoingConverterImp;
import se.uu.ub.cora.therest.error.ErrorHandler;
import se.uu.ub.cora.therest.error.ErrorHandlerImp;
import se.uu.ub.cora.therest.url.UrlHandler;
import se.uu.ub.cora.therest.url.UrlHandlerImp;

public class TheRestInstanceFactoryTest {
	private LoggerFactorySpy loggerFactorySpy;
	private TheRestInstanceFactory factory;

	@BeforeMethod
	public void setUp() {
		loggerFactorySpy = new LoggerFactorySpy();
		LoggerProvider.setLoggerFactory(loggerFactorySpy);
		factory = new TheRestInstanceFactoryImp();
	}

	@Test
	public void testFactorUrlHandler() {
		UrlHandler uh = factory.factorUrlHandler();

		assertTrue(uh instanceof UrlHandlerImp);
	}

	@Test
	public void testFactorErrorHandler() {
		ErrorHandler eh = factory.factorErrorHandler();

		assertTrue(eh instanceof ErrorHandlerImp);
	}

	@Test
	public void testFactorEndpontOutgingConverter() {
		EndpointOutgoingConverter ec = factory.factorEndpointOutgoingConverter();

		assertTrue(ec instanceof EndpointOutgoingConverterImp);
	}

	@Test
	public void testFactorEndpontIncomingConverter() {
		EndpointIncomingConverter ec = factory.factorEndpointIncomingConverter();

		assertTrue(ec instanceof EndpointIncomingConverterImp);
		assertTrue(((EndpointIncomingConverterImp) ec)
				.onlyForTestGetJsonParser() instanceof OrgJsonParser);
	}

}
