package epc.therest.json.parser.org;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.json.JSONObject;

import epc.therest.json.parser.JsonArray;
import epc.therest.json.parser.JsonObject;
import epc.therest.json.parser.JsonParseException;
import epc.therest.json.parser.JsonString;
import epc.therest.json.parser.JsonValue;
import epc.therest.json.parser.JsonValueType;

public final class OrgJsonObjectAdapter implements JsonObject {

	public static OrgJsonObjectAdapter usingOrgJsonObject(JSONObject orgJsonObject) {
		return new OrgJsonObjectAdapter(orgJsonObject);
	}

	private JSONObject orgJsonObject;

	private OrgJsonObjectAdapter(JSONObject orgJsonObject) {
		this.orgJsonObject = orgJsonObject;
	}

	@Override
	public JsonValueType getValueType() {
		return JsonValueType.OBJECT;
	}

	@Override
	public JsonValue getValue(String key) {
		try {
			return tryGetValue(key);
		} catch (Exception e) {
			throw new JsonParseException("Json object does not contain requested key", e);
		}
	}

	private JsonValue tryGetValue(String key) {
		Object object = orgJsonObject.get(key);
		return OrgJsonValueFactory.createFromOrgJsonObject(object);
	}

	@Override
	public JsonString getValueAsJsonString(String key) {
		JsonValue jsonValue = getValue(key);
		if (JsonValueType.STRING.equals(jsonValue.getValueType())) {
			return (JsonString) getValue(key);
		}
		throw new JsonParseException("Not a string");
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
	public boolean containsKey(String key) {
		return orgJsonObject.has(key);
	}

	@Override
	public Set<String> keySet() {
		return orgJsonObject.keySet();
	}

	@Override
	public Set<Entry<String, JsonValue>> entrySet() {
		Map<String, JsonValue> outSet = new HashMap<>();
		for (String key : orgJsonObject.keySet()) {
			Object child = orgJsonObject.get(key);
			JsonValue jsonValue = OrgJsonValueFactory.createFromOrgJsonObject(child);
			outSet.put(key, jsonValue);
		}
		return outSet.entrySet();
	}

	@Override
	public int size() {
		return orgJsonObject.length();
	}

	@Override
	public String toJsonFormattedString() {
		return orgJsonObject.toString();
	}
}