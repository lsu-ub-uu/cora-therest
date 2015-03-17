package epc.therest.data.converter;

import epc.therest.data.RestDataElement;
import epc.therest.data.RestDataAtomic;
import epc.therest.jsonparser.JsonObject;
import epc.therest.jsonparser.JsonParseException;
import epc.therest.jsonparser.JsonString;

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
			return tryToInstanciate();
		} catch (Exception e) {
			throw new JsonParseException("Error parsing jsonObject", e);
		}
	}

	private RestDataElement tryToInstanciate() {
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
	}

	private void validateOnlyOneKeyValuePairAtTopLevel() {
		if (jsonObject.size() != 1) {
			throw new JsonParseException("Atomic data can only contain one key value pair");
		}
	}
}
