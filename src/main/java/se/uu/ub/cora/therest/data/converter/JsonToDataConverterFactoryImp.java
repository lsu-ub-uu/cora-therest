/*
 * Copyright 2015 Uppsala University Library
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

package se.uu.ub.cora.therest.data.converter;

import se.uu.ub.cora.therest.json.parser.JsonArray;
import se.uu.ub.cora.therest.json.parser.JsonObject;
import se.uu.ub.cora.therest.json.parser.JsonParseException;
import se.uu.ub.cora.therest.json.parser.JsonValue;

public class JsonToDataConverterFactoryImp implements JsonToDataConverterFactory {

	private JsonObject jsonObject;

	@Override
	public JsonToDataConverter createForJsonObject(JsonValue jsonValue) {
		if (!(jsonValue instanceof JsonObject)) {
			throw new JsonParseException("Json value is not an object, can not convert");
		}
		jsonObject = (JsonObject) jsonValue;

		if (isGroup()) {
			if (isRecordLink()) {
				return JsonToDataRecordLinkConverter.forJsonObject(jsonObject);
			}
			return JsonToDataGroupConverter.forJsonObject(jsonObject);
		}
		if (isAtomicData()) {
			return JsonToDataAtomicConverter.forJsonObject(jsonObject);
		}
		return JsonToDataAttributeConverter.forJsonObject(jsonObject);
	}

	private boolean isAtomicData() {
		return jsonObject.containsKey("value");
	}

	private boolean isGroup() {
		return jsonObject.containsKey("children");
	}

	private boolean isRecordLink() {
		JsonArray children = jsonObject.getValueAsJsonArray("children");
		for (JsonValue child : children) {
			JsonObject childObject =  (JsonObject)child;
			if(childObject.containsKey("name") && nameIsLinkedRecordId(childObject)){
				return true;
			}
		}
		return false;
	}

	private boolean nameIsLinkedRecordId(JsonObject childObject) {
		String name = childObject.getValueAsJsonString("name").getStringValue();
		return name.equals("linkedRecordId");
	}
}
