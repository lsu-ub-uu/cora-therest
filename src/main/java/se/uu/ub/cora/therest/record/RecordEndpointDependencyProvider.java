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

public class RecordEndpointDependencyProvider {

	private static RecordEndpointDependencyFactory depFactory = new RecordEndpointDependencyFactoryImp();

	RecordEndpointDependencyProvider() {
	}

	public static EndpointDecoratedReader getDecoratedReader() {
		return depFactory.createDecoratedReader();
	}

	public static void onlyForTestSetFactory(RecordEndpointDependencyFactory depFactory) {
		RecordEndpointDependencyProvider.depFactory = depFactory;
	}

	public static RecordEndpointDependencyFactory onlyForTestGetFactory() {
		return depFactory;
	}
}
