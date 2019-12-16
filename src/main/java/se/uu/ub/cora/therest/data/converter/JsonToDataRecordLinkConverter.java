/*
 * Copyright 2019 Uppsala University Library
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

import java.util.Map.Entry;

import se.uu.ub.cora.json.parser.JsonArray;
import se.uu.ub.cora.json.parser.JsonObject;
import se.uu.ub.cora.json.parser.JsonParseException;
import se.uu.ub.cora.json.parser.JsonString;
import se.uu.ub.cora.json.parser.JsonValue;
import se.uu.ub.cora.therest.data.RestDataElement;
import se.uu.ub.cora.therest.data.RestDataRecordLink;

public final class JsonToDataRecordLinkConverter implements JsonToDataConverter {

	private static final int ONE_OPTIONAL_KEY_IS_PRESENT = 3;
	private static final String CHILDREN = "children";
	private static final String ATTRIBUTES = "attributes";
	private static final int NUM_OF_ALLOWED_KEYS_AT_TOP_LEVEL = 4;
	private JsonObject jsonObject;
	private RestDataRecordLink restDataRecordLink;

	static JsonToDataRecordLinkConverter forJsonObject(JsonObject jsonObject) {
		return new JsonToDataRecordLinkConverter(jsonObject);
	}

	private JsonToDataRecordLinkConverter(JsonObject jsonObject) {
		this.jsonObject = jsonObject;
	}

	@Override
	public RestDataElement toInstance() {
		try {
			return tryToInstanciate();
		} catch (Exception e) {
			throw new JsonParseException("Error parsing jsonObject: " + e.getMessage(), e);
		}
	}

	private RestDataElement tryToInstanciate() {
		validateOnlyCorrectKeysAtTopLevel();
		RestDataRecordLink createDataGroupInstance = (RestDataRecordLink) createDataGroupInstance();
		throwErrorIfLinkChildrenAreIncorrect(createDataGroupInstance);
		return createDataGroupInstance;
	}

	private void throwErrorIfLinkChildrenAreIncorrect(RestDataRecordLink recordLink) {
		if (incorrectNumberOfChildren(recordLink) || incorrectChildren(recordLink)) {
			throw new JsonParseException(
					"RecordLinkData must and can only contain children with name linkedRecordType and linkedRecordId");
		}
	}

	private boolean incorrectNumberOfChildren(RestDataRecordLink recordLink) {
		return recordLink.getChildren().size() != 2;
	}

	private boolean incorrectChildren(RestDataRecordLink recordLink) {
		return !recordLink.containsChildWithNameInData("linkedRecordType")
				|| !recordLink.containsChildWithNameInData("linkedRecordId");
	}

	private String getNameInDataFromJsonObject() {
		return jsonObject.getValueAsJsonString("name").getStringValue();
	}

	private void validateOnlyCorrectKeysAtTopLevel() {

		if (!jsonObject.containsKey("name")) {
			throw new JsonParseException("RecordLink data must contain key: name");
		}

		if (!hasChildren()) {
			throw new JsonParseException("RecordLink data must contain key: children");
		}

		validateNoOfKeysAtTopLevel();
	}

	private void validateNoOfKeysAtTopLevel() {
		if (threeKeysAtTopLevelButAttributeAndRepeatIdIsMissing()) {
			throw new JsonParseException(
					"RecordLink data must contain name and children, and may contain "
							+ "attributes or repeatId");
		}
		if (maxKeysAtTopLevelButAttributeOrRepeatIdIsMissing()) {
			throw new JsonParseException("Group data must contain key: attributes");
		}

		if (moreKeysAtTopLevelThanAllowed()) {
			throw new JsonParseException(
					"RecordLink data can only contain keys: name, children and attributes");
		}
	}

	private boolean threeKeysAtTopLevelButAttributeAndRepeatIdIsMissing() {
		int oneOptionalKeyPresent = ONE_OPTIONAL_KEY_IS_PRESENT;
		return jsonObject.keySet().size() == oneOptionalKeyPresent && !hasAttributes()
				&& !hasRepeatId();
	}

	private boolean maxKeysAtTopLevelButAttributeOrRepeatIdIsMissing() {
		return jsonObject.keySet().size() == NUM_OF_ALLOWED_KEYS_AT_TOP_LEVEL
				&& (!hasAttributes() || !hasRepeatId());
	}

	private boolean moreKeysAtTopLevelThanAllowed() {
		return jsonObject.keySet().size() > NUM_OF_ALLOWED_KEYS_AT_TOP_LEVEL;
	}

	private RestDataElement createDataGroupInstance() {
		String nameInData = getNameInDataFromJsonObject();
		restDataRecordLink = RestDataRecordLink.withNameInData(nameInData);
		possiblyAddRepeatId();
		possiblyAddAttributes();
		addChildren();
		return restDataRecordLink;
	}

	private void possiblyAddRepeatId() {
		if (hasRepeatId()) {
			restDataRecordLink
					.setRepeatId(jsonObject.getValueAsJsonString("repeatId").getStringValue());
		}

	}

	private boolean hasRepeatId() {
		return jsonObject.containsKey("repeatId");
	}

	private void possiblyAddAttributes() {
		if (hasAttributes()) {
			addAttributes();
		}
	}

	private boolean hasAttributes() {
		return jsonObject.containsKey(ATTRIBUTES);
	}

	private void addAttributes() {
		JsonObject attributes = jsonObject.getValueAsJsonObject(ATTRIBUTES);
		for (Entry<String, JsonValue> attributeEntry : attributes.entrySet()) {
			addAttribute(attributeEntry);
		}
	}

	private void addAttribute(Entry<String, JsonValue> attributeEntry) {
		String value = ((JsonString) attributeEntry.getValue()).getStringValue();
		restDataRecordLink.addAttributeByIdWithValue(attributeEntry.getKey(), value);
	}

	private boolean hasChildren() {
		return jsonObject.containsKey(CHILDREN);
	}

	private void addChildren() {
		JsonArray children = jsonObject.getValueAsJsonArray(CHILDREN);
		for (JsonValue child : children) {
			addChild((JsonObject) child);
		}
	}

	private void addChild(JsonObject child) {
		JsonToDataConverterFactoryImp jsonToDataConverterFactoryImp = new JsonToDataConverterFactoryImp();
		JsonToDataConverter childJsonToDataConverter = jsonToDataConverterFactoryImp
				.createForJsonObject(child);
		restDataRecordLink.addChild(childJsonToDataConverter.toInstance());
	}
}
