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

import se.uu.ub.cora.json.parser.JsonArray;
import se.uu.ub.cora.json.parser.JsonObject;
import se.uu.ub.cora.json.parser.JsonParseException;
import se.uu.ub.cora.json.parser.JsonParser;
import se.uu.ub.cora.json.parser.JsonValue;

public class JsonParserSpy implements JsonParser {

	public String jsonString;
	public JsonValueSpy returnedJsonValue;
	public boolean throwError = false;

	@Override
	public JsonValue parseString(String jsonString) {
		this.jsonString = jsonString;
		if (throwError) {
			throw new JsonParseException("some parse exception from spy");
		}
		returnedJsonValue = new JsonValueSpy();
		return returnedJsonValue;
	}

	@Override
	public JsonObject parseStringAsObject(String jsonString) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JsonArray parseStringAsArray(String jsonString) {
		// TODO Auto-generated method stub
		return null;
	}

}
