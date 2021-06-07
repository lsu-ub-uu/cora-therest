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

import se.uu.ub.cora.json.parser.JsonValue;
import se.uu.ub.cora.therest.converter.jsontorest.JsonToDataConverter;
import se.uu.ub.cora.therest.converter.jsontorest.JsonToDataConverterFactory;

public class JsonToDataConverterFactorySpy implements JsonToDataConverterFactory {

	public List<JsonValue> jsonValues = new ArrayList<>();
	public List<JsonToDataConverterSpy> jsonToDataConverterSpies = new ArrayList<>();

	@Override
	public JsonToDataConverter createForJsonObject(JsonValue jsonValue) {
		jsonValues.add(jsonValue);
		JsonToDataConverterSpy jsonToDataConverterSpy = new JsonToDataConverterSpy();
		jsonToDataConverterSpies.add(jsonToDataConverterSpy);
		return jsonToDataConverterSpy;
	}

}
