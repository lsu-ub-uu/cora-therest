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

package se.uu.ub.cora.therest.converter.jsontorest;

import java.util.ArrayList;
import java.util.List;

import se.uu.ub.cora.json.parser.JsonArray;
import se.uu.ub.cora.json.parser.JsonObject;
import se.uu.ub.cora.json.parser.JsonParseException;
import se.uu.ub.cora.json.parser.JsonValue;

public class JsonToRestConverterFactoryImp implements JsonToRestConverterFactory {

	private static final int CORRECT_NUM_OF_CHILDREN = 4;
	private JsonObject jsonObject;

	@Override
	public JsonToRestConverter createForJsonObject(JsonValue jsonValue) {
		if (!(jsonValue instanceof JsonObject)) {
			throw new JsonParseException("Json value is not an object, can not convert");
		}
		jsonObject = (JsonObject) jsonValue;

		if (isGroup()) {
			return createConverterForGroupOrLink();
		}
		if (isAtomicData()) {
			return JsonDataAtomicToRestConverter.forJsonObject(jsonObject);
		}
		return JsonDataAttributeToRestConverter.forJsonObject(jsonObject);
	}

	private JsonToRestConverter createConverterForGroupOrLink() {
		List<String> foundNames = extractChildNames();
		if (isRecordLink(foundNames)) {
			return JsonDataRecordLinkToRestConverter.forJsonObject(jsonObject);
		}
		if (isResourceLink(foundNames)) {
			return JsonDataResourceLinkToRestConverter.forJsonObject(jsonObject);
		}

		return JsonDataGroupToRestConverter.forJsonObject(jsonObject);
	}

	private boolean isResourceLink(List<String> foundNames) {
		return foundNames.size() == CORRECT_NUM_OF_CHILDREN && foundNames.contains("streamId")
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
