package epc.therest.jsonparser.javax;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonReaderFactory;
import javax.json.JsonStructure;
import javax.json.JsonValue.ValueType;

import epc.therest.jsonparser.JsonParseException;
import epc.therest.jsonparser.JsonParser;
import epc.therest.jsonparser.JsonValue;
import epc.therest.jsonparser.JsonValueType;

public class JavaxJsonParser implements JsonParser {

	@Override
	public JsonValue parseString(String json) {
		try {
			Map<String, Object> config = new HashMap<>();
			JsonReaderFactory jsonReaderFactory = Json.createReaderFactory(config);
			JsonReader jsonReader = jsonReaderFactory.createReader(new StringReader(json));
			JsonStructure jsonStructure = jsonReader.read();
			ValueType valueType = jsonStructure.getValueType();
			if (ValueType.OBJECT.equals(valueType)) {
				return new JavaxJsonObject((JsonObject) jsonStructure);
			}
			return new JavaxJsonArray((JsonArray) jsonStructure);
		} catch (Exception e) {
			throw new JsonParseException("Json string does not contain a valid json", e);
		}
	}

	@Override
	public epc.therest.jsonparser.JsonObject parseStringAsObject(String jsonString) {
		JsonValue jsonValue = parseString(jsonString);
		if (JsonValueType.OBJECT.equals(jsonValue.getValueType())) {
			return (epc.therest.jsonparser.JsonObject) jsonValue;
		}
		throw new JsonParseException("Json string does not contain a valid json object");

	}

	@Override
	public epc.therest.jsonparser.JsonArray parseStringAsArray(String jsonString) {
		JsonValue jsonValue = parseString(jsonString);
		if (JsonValueType.ARRAY.equals(jsonValue.getValueType())) {
			return (epc.therest.jsonparser.JsonArray) jsonValue;
		}
		throw new JsonParseException("Json string does not contain a valid json array");
	}
}
