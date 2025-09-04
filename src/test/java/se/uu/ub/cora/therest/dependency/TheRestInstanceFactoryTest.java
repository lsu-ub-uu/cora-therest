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

import se.uu.ub.cora.therest.converter.EndpointConverterImp;
import se.uu.ub.cora.therest.error.ErrorHandlerImp;
import se.uu.ub.cora.therest.record.EndpointDecoratedReaderImp;
import se.uu.ub.cora.therest.url.UrlHandler;
import se.uu.ub.cora.therest.url.UrlHandlerImp;

public class TheRestInstanceFactoryTest {
	private TheRestInstanceFactory factory;

	@BeforeMethod
	public void setUp() {
		factory = new TheRestInstanceFactoryImp();
	}

	@Test
	public void testFactorUrlHandler() {
		UrlHandler uh = factory.factorUrlHandler();

		assertTrue(uh instanceof UrlHandlerImp);
	}

	@Test
	public void testFactorDecoratedReader() {
		EndpointDecoratedReaderImp reader = (EndpointDecoratedReaderImp) factory
				.createDecoratedReader();

		assertTrue(reader.onlyForTestGetEndpointConverter() instanceof EndpointConverterImp);
		assertTrue(reader.onlyForTestGetErrorHandler() instanceof ErrorHandlerImp);
	}
}
