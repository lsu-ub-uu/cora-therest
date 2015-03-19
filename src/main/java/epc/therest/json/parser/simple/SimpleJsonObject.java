package epc.therest.json.parser.simple;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.json.simple.JSONObject;

import epc.therest.json.parser.JsonArray;
import epc.therest.json.parser.JsonObject;
import epc.therest.json.parser.JsonParseException;
import epc.therest.json.parser.JsonValue;
import epc.therest.json.parser.JsonValueType;

public class SimpleJsonObject implements JsonObject {

	private JSONObject simpleJsonObject;

	public SimpleJsonObject(JSONObject jsonObject) {
		this.simpleJsonObject = jsonObject;
	}

	@Override
	public JsonValueType getValueType() {
		return JsonValueType.OBJECT;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Set<String> keySet() {
		return simpleJsonObject.keySet();
	}

	@Override
	public JsonValue getValue(String key) {
		Object value = simpleJsonObject.get(key);
		if (null == value) {
			throw new JsonParseException("Value does not exist");
		}
		return SimpleJsonClassFactory.createFromSimpleJsonValue(value);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Set<Entry<String, JsonValue>> entrySet() {
		Set<Entry<String, Object>> entrySet = simpleJsonObject.entrySet();
		Map<String, JsonValue> out = new HashMap<>();
		for (Entry<String, Object> entry : entrySet) {
			out.put(entry.getKey(),
					SimpleJsonClassFactory.createFromSimpleJsonValue(entry.getValue()));
		}
		return out.entrySet();
	}

	@Override
	public int size() {
		return simpleJsonObject.size();
	}

	@Override
	public JsonObject getObject(String key) {
		JsonValue jsonValue = getValue(key);
		if (JsonValueType.OBJECT.equals(jsonValue.getValueType())) {
			return (JsonObject) getValue(key);
		}
		throw new JsonParseException("Not an object");
	}

	@Override
	public JsonArray getArray(String key) {
		JsonValue jsonValue = getValue(key);
		if (JsonValueType.ARRAY.equals(jsonValue.getValueType())) {
			return (JsonArray) getValue(key);
		}
		throw new JsonParseException("Not an array");
	}

	@Override
	public boolean containsKey(String key) {
		if (null != simpleJsonObject.get(key)) {
			return true;
		}
		return false;
	}

	@Override
	public String toJsonString() {
		return simpleJsonObject.toJSONString();
	}

}
