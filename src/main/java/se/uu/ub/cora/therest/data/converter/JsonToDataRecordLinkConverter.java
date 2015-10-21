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

import se.uu.ub.cora.therest.data.RestDataElement;
import se.uu.ub.cora.therest.data.RestDataRecordLink;
import se.uu.ub.cora.therest.json.parser.JsonObject;
import se.uu.ub.cora.therest.json.parser.JsonParseException;

public final class JsonToDataRecordLinkConverter implements JsonToDataConverter {

	private static final int ONE_OPTIONAL_KEY_IS_PRESENT = 4;
	private JsonObject jsonObject;
	private static final int NUM_OF_ALLOWED_KEYS_AT_TOP_LEVEL = 5;

	public static JsonToDataRecordLinkConverter forJsonObject(JsonObject jsonObject) {
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
		return createDataGroupInstance();
	}

	private void validateOnlyCorrectKeysAtTopLevel() {

		validateMandatoryKeys();

		if (threeKeysAtTopLevelButActionLinksAndRepeatIdIsMissing()
				|| maxKeysAtTopLevelButRepeatIdOrActionLinksIsMissing()
				|| moreKeysAtTopLevelThanAllowed()) {
			throw new JsonParseException("Group data can only contain keys name, repeatId, "
					+ "recordType, recordId and actionLinks");
		}

	}

	private void validateMandatoryKeys() {
		if (!jsonObject.containsKey("name")) {
			throw new JsonParseException("Group data must contain key \"name\"");
		}
		if (!jsonObject.containsKey("recordType")) {
			throw new JsonParseException("Group data must contain key \"recordType\"");
		}
		if (!jsonObject.containsKey("recordId")) {
			throw new JsonParseException("Group data must contain key \"recordId\"");
		}
	}

	private boolean threeKeysAtTopLevelButActionLinksAndRepeatIdIsMissing() {
		return jsonObject.keySet().size() == ONE_OPTIONAL_KEY_IS_PRESENT && !hasActionLinks()
				&& !hasRepeatId();
	}

	private boolean hasRepeatId() {
		return jsonObject.containsKey("repeatId");
	}

	private boolean maxKeysAtTopLevelButRepeatIdOrActionLinksIsMissing() {
		return jsonObject.keySet().size() == NUM_OF_ALLOWED_KEYS_AT_TOP_LEVEL
				&& (!hasActionLinks() || !hasRepeatId());
	}

	private boolean hasActionLinks() {
		return jsonObject.containsKey("actionLinks");
	}

	private boolean moreKeysAtTopLevelThanAllowed() {
		return jsonObject.keySet().size() > NUM_OF_ALLOWED_KEYS_AT_TOP_LEVEL;
	}

	private RestDataElement createDataGroupInstance() {
		String nameInData = getNameInDataFromJsonObject();
		String recordType = getRecordTypeFromJsonObject();
		String recordId = getRecordIdFromJsonObject();
		RestDataRecordLink restDataRecordLink = RestDataRecordLink
				.withNameInDataAndRecordTypeAndRecordId(nameInData, recordType, recordId);
		if (hasRepeatId()) {
			restDataRecordLink
					.setRepeatId(jsonObject.getValueAsJsonString("repeatId").getStringValue());
		}
		return restDataRecordLink;

	}

	private String getNameInDataFromJsonObject() {
		return jsonObject.getValueAsJsonString("name").getStringValue();
	}

	private String getRecordTypeFromJsonObject() {
		return jsonObject.getValueAsJsonString("recordType").getStringValue();
	}

	private String getRecordIdFromJsonObject() {
		return jsonObject.getValueAsJsonString("recordId").getStringValue();
	}
}
