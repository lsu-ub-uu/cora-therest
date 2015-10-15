package se.uu.ub.cora.therest.data.converter;

import se.uu.ub.cora.therest.data.RestDataElement;
import se.uu.ub.cora.therest.data.RestDataRecordLink;
import se.uu.ub.cora.therest.json.parser.JsonObject;
import se.uu.ub.cora.therest.json.parser.JsonParseException;

public final class JsonToDataRecordLinkConverter implements JsonToDataConverter {

	private JsonObject jsonObject;
	private static final int NUM_OF_ALLOWED_KEYS_AT_TOP_LEVEL = 4;

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

		if (maxKeysAtTopLevelButActionLinksIsMissing()) {
			throw new JsonParseException("Group data may contain key \"actionLinks\"");
		}
		if (moreKeysAtTopLevelThanAllowed()) {
			throw new JsonParseException("Group data can only contain keys \"name\",\"recordType\","
					+ "\"recordId\" and \"actionLinks\" ");
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

	private boolean maxKeysAtTopLevelButActionLinksIsMissing() {
		return jsonObject.keySet().size() == NUM_OF_ALLOWED_KEYS_AT_TOP_LEVEL && !hasActionLinks();
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
		return RestDataRecordLink
				.withNameInDataAndRecordTypeAndRecordId(nameInData, recordType, recordId);
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
