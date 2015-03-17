package epc.therest.jsonparser.javax;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonReader;
import javax.json.JsonReaderFactory;
import javax.json.JsonStructure;

import epc.therest.jsonparser.JsonParseException;
import epc.therest.jsonparser.JsonParser;
import epc.therest.jsonparser.JsonValue;
import epc.therest.jsonparser.JsonValueType;

public class JavaxJsonParser implements JsonParser {

	private JavaxJsonClassFactory javaxJsonClassFactory;

	public JavaxJsonParser(JavaxJsonClassFactory javaxJsonClassFactory) {
		this.javaxJsonClassFactory = javaxJsonClassFactory;
	}

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
		return javaxJsonClassFactory.createFromJavaxJsonValue(jsonStructure);
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
