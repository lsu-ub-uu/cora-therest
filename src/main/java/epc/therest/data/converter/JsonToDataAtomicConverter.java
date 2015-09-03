package epc.therest.data.converter;

import epc.therest.data.RestDataAtomic;
import epc.therest.data.RestDataElement;
import epc.therest.json.parser.JsonObject;
import epc.therest.json.parser.JsonParseException;
import epc.therest.json.parser.JsonString;
import epc.therest.json.parser.JsonValue;

public final class JsonToDataAtomicConverter implements JsonToDataConverter {
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
			throw new JsonParseException("Error parsing jsonObject: "+e.getMessage(), e);
		}
	}

	private RestDataElement tryToInstantiate() {
		validateJsonData();
		String dataId = getDataIdFromJsonObject();
		JsonString value = (JsonString) jsonObject.getValue(dataId);
		return RestDataAtomic.withDataIdAndValue(dataId, value.getStringValue());
	}

	private String getDataIdFromJsonObject() {
		return jsonObject.keySet().iterator().next();
	}

	private void validateJsonData() {
		validateOnlyOneKeyValuePairAtTopLevel();
		validateDataIdValueIsString();
	}

	private void validateOnlyOneKeyValuePairAtTopLevel() {
		if (jsonObject.size() != 1) {
			throw new JsonParseException("Atomic data can only contain one key value pair");
		}
	}

	private void validateDataIdValueIsString() {
		String dataId = getDataIdFromJsonObject();
		JsonValue value = jsonObject.getValue(dataId);
		if(!(value instanceof JsonString)){
			throw new JsonParseException("Value of atomic data must be a String");
		}
	}
}
