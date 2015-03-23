package epc.therest.json.parser.javax;

import javax.json.JsonString;
import javax.json.JsonValue.ValueType;

import epc.therest.json.parser.JsonValue;

public final class JavaxJsonValueFactory {
	private JavaxJsonValueFactory() {
		// not in use
		throw new UnsupportedOperationException();
	}

	public static JsonValue createFromJavaxJsonValue(javax.json.JsonValue jsonValue) {
		if (ValueType.OBJECT.equals(jsonValue.getValueType())) {
			return JavaxJsonObjectAdapter
					.usingJavaxJsonObjectAdapter((javax.json.JsonObject) jsonValue);
		}
		if (ValueType.ARRAY.equals(jsonValue.getValueType())) {
			return JavaxJsonArrayAdapter
					.usingJavaxJsonArrayAdapter((javax.json.JsonArray) jsonValue);
		}
		return JavaxJsonStringAdapter.usingJavaxJsonStringAdapter((JsonString) jsonValue);
	}
}
