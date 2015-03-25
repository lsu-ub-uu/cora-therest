package epc.therest.json.parser.javax;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import epc.therest.json.parser.JsonArray;
import epc.therest.json.parser.JsonObject;
import epc.therest.json.parser.JsonParseException;
import epc.therest.json.parser.JsonString;
import epc.therest.json.parser.JsonValue;
import epc.therest.json.parser.JsonValueType;

public final class JavaxJsonObjectAdapter implements JsonObject {

	private javax.json.JsonObject javaxJsonObject;

	public static JavaxJsonObjectAdapter usingJavaxJsonObjectAdapter(
			javax.json.JsonObject javaxJsonObject) {
		return new JavaxJsonObjectAdapter(javaxJsonObject);
	}

	private JavaxJsonObjectAdapter(javax.json.JsonObject javaxJsonObject) {
		this.javaxJsonObject = javaxJsonObject;
	}

	@Override
	public JsonValueType getValueType() {
		return JsonValueType.OBJECT;
	}

	@Override
	public boolean containsKey(String key) {
		return javaxJsonObject.containsKey(key);
	}

	@Override
	public Set<String> keySet() {
		return javaxJsonObject.keySet();
	}

	@Override
	public JsonValue getValue(String key) {
		javax.json.JsonValue jsonValue = javaxJsonObject.get(key);
		if (null != jsonValue) {
			return JavaxJsonValueFactory.createFromJavaxJsonValue(jsonValue);
		}
		throw new JsonParseException("Json object does not contain requested key");
	}

	@Override
	public Set<Entry<String, JsonValue>> entrySet() {
		Set<Entry<String, javax.json.JsonValue>> entrySet = javaxJsonObject.entrySet();
		Map<String, JsonValue> out = new HashMap<>();
		for (Entry<String, javax.json.JsonValue> entry : entrySet) {
			out.put(entry.getKey(),
					JavaxJsonValueFactory.createFromJavaxJsonValue(entry.getValue()));
		}
		return out.entrySet();
	}

	@Override
	public int size() {
		return javaxJsonObject.size();
	}

	@Override
	public JsonObject getValueAsJsonObject(String key) {
		JsonValue jsonValue = getValue(key);
		if (JsonValueType.OBJECT.equals(jsonValue.getValueType())) {
			return (JsonObject) getValue(key);
		}
		throw new JsonParseException("Not an object");
	}

	@Override
	public JsonArray getValueAsJsonArray(String key) {
		JsonValue jsonValue = getValue(key);
		if (JsonValueType.ARRAY.equals(jsonValue.getValueType())) {
			return (JsonArray) getValue(key);
		}
		throw new JsonParseException("Not an array");
	}

	@Override
	public JsonString getValueAsJsonString(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String toJsonFormattedString() {
		// TODO Auto-generated method stub
		return null;
	}
}
