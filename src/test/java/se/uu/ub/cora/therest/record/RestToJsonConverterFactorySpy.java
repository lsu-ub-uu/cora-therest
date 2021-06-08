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

import se.uu.ub.cora.json.builder.JsonBuilderFactory;
import se.uu.ub.cora.therest.converter.resttojson.RestToJsonConverter;
import se.uu.ub.cora.therest.converter.resttojson.RestToJsonConverterFactory;
import se.uu.ub.cora.therest.data.RestData;
import se.uu.ub.cora.therest.data.RestDataElement;

public class RestToJsonConverterFactorySpy implements RestToJsonConverterFactory {
	public RestRecordToJsonConverterSpy restRecordToJsonConverterSpy;
	public RestData restData;

	@Override
	public RestToJsonConverter createForRestData(RestData restData) {
		this.restData = restData;
		restRecordToJsonConverterSpy = new RestRecordToJsonConverterSpy();
		return restRecordToJsonConverterSpy;
	}

	@Override
	public RestToJsonConverter createForRestDataElement(JsonBuilderFactory factory,
			RestDataElement restDataElement) {
		// TODO Auto-generated method stub
		return null;
	}

}
