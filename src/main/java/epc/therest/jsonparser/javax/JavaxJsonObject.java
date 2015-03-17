package epc.therest.jsonparser.javax;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import epc.therest.jsonparser.JsonArray;
import epc.therest.jsonparser.JsonObject;
import epc.therest.jsonparser.JsonParseException;
import epc.therest.jsonparser.JsonValue;
import epc.therest.jsonparser.JsonValueType;

public class JavaxJsonObject implements JsonObject {

	private javax.json.JsonObject javaxJsonObject;
	private JavaxJsonClassFactoryImp factory;

	public static JavaxJsonObject usingJavaxJsonObject(JavaxJsonClassFactoryImp factory,
			javax.json.JsonObject javaxJsonObject) {
		return new JavaxJsonObject(factory, javaxJsonObject);
	}

	private JavaxJsonObject(JavaxJsonClassFactoryImp factory, javax.json.JsonObject javaxJsonObject) {
		this.factory = factory;
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
			return factory.createFromJavaxJsonValue(jsonValue);
		}
		throw new JsonParseException("Json object does not contain requested key");
	}

	@Override
	public Set<Entry<String, JsonValue>> entrySet() {
		Set<Entry<String, javax.json.JsonValue>> entrySet = javaxJsonObject.entrySet();
		Map<String, JsonValue> out = new HashMap<>();
		for (Entry<String, javax.json.JsonValue> entry : entrySet) {
			out.put(entry.getKey(), factory.createFromJavaxJsonValue(entry.getValue()));
		}
		return out.entrySet();
	}

	@Override
	public int size() {
		return javaxJsonObject.size();
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
}
