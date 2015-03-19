package epc.therest.json.parser.simple;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import epc.therest.json.parser.JsonValue;

public final class SimpleJsonClassFactory {

	private SimpleJsonClassFactory() {
		// not called
		throw new UnsupportedOperationException();
	}

	static JsonValue createFromSimpleJsonValue(Object value) {
		if (value instanceof JSONArray) {
			return new SimpleJsonArray((JSONArray) value);
		}
		if (value instanceof JSONObject) {
			return new SimpleJsonObject((JSONObject) value);
		}
		return new SimpleJsonString((String) value);
	}
}
