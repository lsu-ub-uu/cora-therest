/*
 * Copyright 2021 Uppsala University Library
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

import java.util.ArrayList;
import java.util.List;

import se.uu.ub.cora.therest.converter.resttocora.RestToCoraConverter;
import se.uu.ub.cora.therest.converter.resttocora.RestToCoraConverterFactory;
import se.uu.ub.cora.therest.data.RestDataElement;

public class RestToDataConverterFactorySpy implements RestToCoraConverterFactory {

	public List<RestDataElement> dataElements = new ArrayList<>();
	public List<RestToDataConverterSpy> factoredConverters = new ArrayList<>();
	public boolean throwError = false;

	@Override
	public RestToCoraConverter factor(RestDataElement dataElement) {
		dataElements.add(dataElement);
		RestToDataConverterSpy toDataConverter = new RestToDataConverterSpy();
		toDataConverter.throwError = throwError;
		factoredConverters.add(toDataConverter);
		return toDataConverter;
	}

}
