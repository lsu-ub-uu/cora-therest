/*
 * Copyright 2025 Olov McKie
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
package se.uu.ub.cora.therest.record;

import static org.testng.Assert.assertTrue;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.logger.LoggerFactory;
import se.uu.ub.cora.logger.LoggerProvider;
import se.uu.ub.cora.logger.spies.LoggerFactorySpy;
import se.uu.ub.cora.therest.converter.EndpointConverterImp;
import se.uu.ub.cora.therest.error.ErrorHandlerImp;

public class RecordEndpointDependencyFactoryTest {
	private RecordEndpointDependencyFactory depFactory;

	@BeforeMethod
	public void beforeMethod() {
		LoggerFactory loggerFactory = new LoggerFactorySpy();
		LoggerProvider.setLoggerFactory(loggerFactory);

		depFactory = new RecordEndpointDependencyFactoryImp();
	}

	@Test
	public void testFactorDecoratedReader() {
		EndpointDecoratedReaderImp reader = (EndpointDecoratedReaderImp) depFactory
				.createDecoratedReader();

		assertTrue(reader.onlyForTestGetEndpointConverter() instanceof EndpointConverterImp);
		assertTrue(reader.onlyForTestGetErrorHandler() instanceof ErrorHandlerImp);
	}
}
