package epc.therest.json.parser.simple;

import org.json.simple.JSONValue;

import epc.therest.json.parser.JsonArray;
import epc.therest.json.parser.JsonObject;
import epc.therest.json.parser.JsonParseException;
import epc.therest.json.parser.JsonParser;
import epc.therest.json.parser.JsonValue;
import epc.therest.json.parser.JsonValueType;

public class SimpleJsonParser implements JsonParser {

	@Override
	public JsonValue parseString(String jsonString) {
		try {
			Object jsonObject = JSONValue.parse(jsonString);
			if (null == jsonObject) {
				throw new JsonParseException("Unable to parse json string");
			}
			return SimpleJsonClassFactory.createFromSimpleJsonValue(jsonObject);
		} catch (Exception e) {
			throw new JsonParseException("Unable to parse json string", e);
		}
	}

	@Override
	public JsonObject parseStringAsObject(String jsonString) {
		JsonValue jsonValue = parseString(jsonString);
		if (JsonValueType.OBJECT.equals(jsonValue.getValueType())) {
			return (epc.therest.json.parser.JsonObject) jsonValue;
		}
		throw new JsonParseException("Json string does not contain a valid json object");
	}

	@Override
	public JsonArray parseStringAsArray(String jsonString) {
		JsonValue jsonValue = parseString(jsonString);
		if (JsonValueType.ARRAY.equals(jsonValue.getValueType())) {
			return (epc.therest.json.parser.JsonArray) jsonValue;
		}
		throw new JsonParseException("Json string does not contain a valid json array");
	}

}
