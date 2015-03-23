package epc.therest.json.parser.simple;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import epc.therest.json.parser.JsonValue;

public final class SimpleJsonValueFactory {

	private SimpleJsonValueFactory() {
		// not called
		throw new UnsupportedOperationException();
	}

	static JsonValue createFromSimpleJsonValue(Object value) {
		if (value instanceof JSONArray) {
			return new SimpleJsonArrayAdapter((JSONArray) value);
		}
		if (value instanceof JSONObject) {
			return new SimpleJsonObjectAdapter((JSONObject) value);
		}
		return new SimpleJsonStringAdapter((String) value);
	}
}
