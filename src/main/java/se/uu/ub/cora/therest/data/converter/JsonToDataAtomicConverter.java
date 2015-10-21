package se.uu.ub.cora.therest.data.converter;

import se.uu.ub.cora.therest.data.RestDataAtomic;
import se.uu.ub.cora.therest.data.RestDataElement;
import se.uu.ub.cora.therest.json.parser.JsonObject;
import se.uu.ub.cora.therest.json.parser.JsonParseException;
import se.uu.ub.cora.therest.json.parser.JsonString;

public final class JsonToDataAtomicConverter implements JsonToDataConverter {
	private static final String REPEAT_ID = "repeatId";
	private static final int ALLOWED_MAX_NO_OF_ELEMENTS_AT_TOP_LEVEL = 3;
	private static final String NAME = "name";
	private static final String VALUE = "value";
	private JsonObject jsonObject;

	static JsonToDataAtomicConverter forJsonObject(JsonObject jsonObject) {
		return new JsonToDataAtomicConverter(jsonObject);
	}

	private JsonToDataAtomicConverter(JsonObject jsonObject) {
		this.jsonObject = jsonObject;
	}

	@Override
	public RestDataElement toInstance() {
		try {
			return tryToInstantiate();
		} catch (Exception e) {
			throw new JsonParseException("Error parsing jsonObject: " + e.getMessage(), e);
		}
	}

	private RestDataElement tryToInstantiate() {
		validateJsonData();
		return convertJsonToDataAtomic();
	}

	private void validateJsonData() {
		validateNameInData();
		validateValue();
		validateRepeatId();
		validateNoExtraElements();
	}

	private void validateNameInData() {
		if (keyMissingOrNotStringValueInJsonObject(NAME)) {
			throw new JsonParseException("Value of atomic data name must contain a String");
		}
	}

	private boolean keyMissingOrNotStringValueInJsonObject(String key) {
		return !jsonObject.containsKey(key) || !(jsonObject.getValue(key) instanceof JsonString);
	}

	private void validateValue() {
		if (keyMissingOrNotStringValueInJsonObject(VALUE)) {
			throw new JsonParseException("Value of atomic data value must contain a String");
		}
	}

	private void validateRepeatId() {
		if (jsonObject.size() == ALLOWED_MAX_NO_OF_ELEMENTS_AT_TOP_LEVEL
				&& keyMissingOrNotStringValueInJsonObject(REPEAT_ID)) {
			throw new JsonParseException(
					"Atomic data can only contain string value for name, value and repeatId");
		}
	}

	private void validateNoExtraElements() {
		if (jsonObject.size() > ALLOWED_MAX_NO_OF_ELEMENTS_AT_TOP_LEVEL) {
			throw new JsonParseException("Atomic data can only contain name, value and repeatId");
		}
	}

	private RestDataAtomic convertJsonToDataAtomic() {
		RestDataAtomic restDataAtomic = createFromJsonWithNameInDataAndValue();
		addRepeatIdFromJson(restDataAtomic);
		return restDataAtomic;
	}

	private RestDataAtomic createFromJsonWithNameInDataAndValue() {
		String nameInData = getStringFromJson(NAME);
		String value = getStringFromJson(VALUE);
		return RestDataAtomic.withNameInDataAndValue(nameInData, value);
	}

	private String getStringFromJson(String key) {
		return jsonObject.getValueAsJsonString(key).getStringValue();
	}

	private void addRepeatIdFromJson(RestDataAtomic restDataAtomic) {
		if (jsonObject.containsKey(REPEAT_ID)) {
			restDataAtomic.setRepeatId(jsonObject.getValueAsJsonString(REPEAT_ID).getStringValue());
		}
	}
}
