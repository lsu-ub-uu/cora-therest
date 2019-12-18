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

import java.util.ArrayList;
import java.util.List;

import se.uu.ub.cora.json.parser.JsonArray;
import se.uu.ub.cora.json.parser.JsonObject;
import se.uu.ub.cora.json.parser.JsonParseException;
import se.uu.ub.cora.json.parser.JsonValue;

public class JsonToDataConverterFactoryImp implements JsonToDataConverterFactory {

	private JsonObject jsonObject;

	@Override
	public JsonToDataConverter createForJsonObject(JsonValue jsonValue) {
		if (!(jsonValue instanceof JsonObject)) {
			throw new JsonParseException("Json value is not an object, can not convert");
		}
		jsonObject = (JsonObject) jsonValue;

		if (isGroup()) {
			return createConverterForGroupOrLink();
		}
		if (isAtomicData()) {
			return JsonToDataAtomicConverter.forJsonObject(jsonObject);
		}
		return JsonToDataAttributeConverter.forJsonObject(jsonObject);
	}

	private JsonToDataConverter createConverterForGroupOrLink() {
		List<String> foundNames = extractChildNames();
		if (isRecordLink(foundNames)) {
			return JsonToDataRecordLinkConverter.forJsonObject(jsonObject);
		}
		if (isResourceLink(foundNames)) {
			return JsonToDataResourceLinkConverter.forJsonObject(jsonObject);
		}

		return JsonToDataGroupConverter.forJsonObject(jsonObject);
	}

	private boolean isResourceLink(List<String> foundNames) {
		return foundNames.size() == 4 && foundNames.contains("streamId")
				&& foundNames.contains("filename") && foundNames.contains("filesize")
				&& foundNames.contains("mimeType");
	}

	private boolean isRecordLink(List<String> foundNames) {
		return foundNames.contains("linkedRecordType") && foundNames.contains("linkedRecordId");
	}

	private List<String> extractChildNames() {
		JsonArray childrenArray = jsonObject.getValueAsJsonArray("children");
		List<String> foundNames = new ArrayList<>();
		for (JsonValue child : childrenArray) {
			String name = getNameInDataFromChild((JsonObject) child);
			foundNames.add(name);
		}
		return foundNames;
	}

	private String getNameInDataFromChild(JsonObject child) {
		return child.getValueAsJsonString("name").getStringValue();
	}

	private boolean isAtomicData() {
		return jsonObject.containsKey("value");
	}

	private boolean isGroup() {
		return jsonObject.containsKey("children");
	}
}
