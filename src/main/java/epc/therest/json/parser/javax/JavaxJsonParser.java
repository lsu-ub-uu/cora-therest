package epc.therest.json.parser.javax;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonReader;
import javax.json.JsonReaderFactory;
import javax.json.JsonStructure;

import epc.therest.json.parser.JsonParseException;
import epc.therest.json.parser.JsonParser;
import epc.therest.json.parser.JsonValue;
import epc.therest.json.parser.JsonValueType;

public class JavaxJsonParser implements JsonParser {
	@Override
	public JsonValue parseString(String json) {
		try {
			return tryToParseJsonString(json);
		} catch (Exception e) {
			throw new JsonParseException("Unable to parse json string", e);
		}
	}

	private JsonValue tryToParseJsonString(String json) {
		Map<String, Object> config = new HashMap<>();
		JsonReaderFactory jsonReaderFactory = Json.createReaderFactory(config);
		JsonReader jsonReader = jsonReaderFactory.createReader(new StringReader(json));
		JsonStructure jsonStructure = jsonReader.read();
		return JavaxJsonValueFactory.createFromJavaxJsonValue(jsonStructure);
	}

	@Override
	public epc.therest.json.parser.JsonObject parseStringAsObject(String jsonString) {
		JsonValue jsonValue = parseString(jsonString);
		if (JsonValueType.OBJECT.equals(jsonValue.getValueType())) {
			return (epc.therest.json.parser.JsonObject) jsonValue;
		}
		throw new JsonParseException("Json string does not contain a valid json object");
	}

	@Override
	public epc.therest.json.parser.JsonArray parseStringAsArray(String jsonString) {
		JsonValue jsonValue = parseString(jsonString);
		if (JsonValueType.ARRAY.equals(jsonValue.getValueType())) {
			return (epc.therest.json.parser.JsonArray) jsonValue;
		}
		throw new JsonParseException("Json string does not contain a valid json array");
	}
}
