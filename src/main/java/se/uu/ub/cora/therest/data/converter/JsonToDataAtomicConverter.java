package se.uu.ub.cora.therest.data.converter;

import se.uu.ub.cora.therest.data.RestDataAtomic;
import se.uu.ub.cora.therest.data.RestDataElement;
import se.uu.ub.cora.therest.json.parser.JsonObject;
import se.uu.ub.cora.therest.json.parser.JsonParseException;
import se.uu.ub.cora.therest.json.parser.JsonString;
import se.uu.ub.cora.therest.json.parser.JsonValue;

public final class JsonToDataAtomicConverter implements JsonToDataConverter {
	private static final int ALLOWED_NO_OF_ELEMENTS_AT_TOP_LEVEL = 2;
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
		String nameInData = jsonObject.getValueAsJsonString(NAME).getStringValue();
		String value = jsonObject.getValueAsJsonString(VALUE).getStringValue();
		return RestDataAtomic.withNameInDataAndValue(nameInData, value);
	}

	private void validateJsonData() {
		validateOnlyNameAndValueAtTopLevel();
		validateNameInDataValueIsString();
		validateValueValueIsString();
	}

	private void validateOnlyNameAndValueAtTopLevel() {
		if (jsonObject.size() != ALLOWED_NO_OF_ELEMENTS_AT_TOP_LEVEL) {
			throw new JsonParseException("Atomic data can only contain name and value");
		}
		if (!jsonObject.containsKey(NAME)) {
			throw new JsonParseException("Atomic data must contain name");
		}
		if (!jsonObject.containsKey(VALUE)) {
			throw new JsonParseException("Atomic data must contain value");
		}
	}

	private void validateNameInDataValueIsString() {
		JsonValue value = jsonObject.getValue(NAME);
		if (!(value instanceof JsonString)) {
			throw new JsonParseException("Value of atomic data name must be a String");
		}
	}

	private void validateValueValueIsString() {
		JsonValue value = jsonObject.getValue(VALUE);
		if (!(value instanceof JsonString)) {
			throw new JsonParseException("Value of atomic data value must be a String");
		}
	}
}