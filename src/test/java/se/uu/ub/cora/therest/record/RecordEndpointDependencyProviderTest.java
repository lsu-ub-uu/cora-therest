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

import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class RecordEndpointDependencyProviderTest {
	private RecordEndpointDependencyFactorySpy depFactory;

	@BeforeMethod
	public void beforeMethod() {
		depFactory = new RecordEndpointDependencyFactorySpy();
	}

	@AfterMethod
	public void afterMethod() {
		RecordEndpointDependencyProvider
				.onlyForTestSetFactory(new RecordEndpointDependencyFactoryImp());
	}

	@Test(priority = 0)
	public void testDefaultFactory() {
		RecordEndpointDependencyFactory defaultFactory = RecordEndpointDependencyProvider
				.onlyForTestGetFactory();
		assertTrue(defaultFactory instanceof RecordEndpointDependencyFactoryImp);
	}

	@Test
	public void testGetDecoratedReaderUsesFactory() {
		RecordEndpointDependencyProvider.onlyForTestSetFactory(depFactory);

		EndpointDecoratedReader reader = RecordEndpointDependencyProvider.getDecoratedReader();

		depFactory.MCR.assertReturn("createDecoratedReader", 0, reader);
	}

	@Test
	public void testOnlyForTestSetFactory() {
		RecordEndpointDependencyProvider.onlyForTestSetFactory(depFactory);
	}

	@Test
	public void testOnlyForTestGetFactory() {
		RecordEndpointDependencyProvider.onlyForTestSetFactory(depFactory);
		assertSame(RecordEndpointDependencyProvider.onlyForTestGetFactory(), depFactory);
	}

}
