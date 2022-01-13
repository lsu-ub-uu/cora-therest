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

import se.uu.ub.cora.data.converter.JsonToDataConverter;
import se.uu.ub.cora.data.converter.JsonToDataConverterFactory;
import se.uu.ub.cora.json.parser.JsonValue;

public class JsonToDataConverterFactorySpy implements JsonToDataConverterFactory {

	public JsonToDataConverterSpy jsonToDataConverterSpy;
	public List<JsonToDataConverterSpy> jsonToDataConverterSpies = new ArrayList<>();
	public boolean throwError = false;
	public JsonValue jsonValue;
	public List<JsonValue> jsonValues = new ArrayList<>();

	@Override
	public JsonToDataConverter createForJsonObject(JsonValue jsonValue) {
		jsonToDataConverterSpy = new JsonToDataConverterSpy();
		jsonToDataConverterSpies.add(jsonToDataConverterSpy);
		jsonValues.add(jsonValue);
		this.jsonValue = jsonValue;
		jsonToDataConverterSpy.throwError = throwError;
		return jsonToDataConverterSpy;
	}

}
