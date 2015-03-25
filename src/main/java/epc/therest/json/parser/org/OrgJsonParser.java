package epc.therest.json.parser.org;

import org.json.JSONArray;
import org.json.JSONObject;

import epc.therest.json.parser.JsonArray;
import epc.therest.json.parser.JsonObject;
import epc.therest.json.parser.JsonParseException;
import epc.therest.json.parser.JsonParser;
import epc.therest.json.parser.JsonValue;
import epc.therest.json.parser.JsonValueType;

public class OrgJsonParser implements JsonParser {

	@Override
	public JsonValue parseString(String jsonString) {
		try {
			JsonValue jsonValue = tryToParseJsonString(jsonString);
			validateEntireStringWasParsed(jsonString, jsonValue);
			return jsonValue;
		} catch (Exception e) {
			throw new JsonParseException("Unable to parse json string", e);
		}
	}

	private void validateEntireStringWasParsed(String jsonString, JsonValue jsonValue) {
		String allWhitespace = "\\s";
		String originalJsonWithoutWhitespace = jsonString.replaceAll(allWhitespace, "");
		String convertedJsonWithoutWhitespace = "";
		if (JsonValueType.OBJECT.equals(jsonValue.getValueType())) {
			convertedJsonWithoutWhitespace = ((JsonObject) jsonValue).toJsonFormattedString();
		} else {
			convertedJsonWithoutWhitespace = ((JsonArray) jsonValue).toJsonFormattedString();
		}
		if (originalJsonWithoutWhitespace.length() != convertedJsonWithoutWhitespace.length()) {
			throw new JsonParseException("Unable to fully parse json string");
		}
	}

	private JsonValue tryToParseJsonString(String jsonString) {
		try {
			return tryToParseJsonStringAsObject(jsonString);
		} catch (Exception e) {
			return tryToParseJsonStringAsArray(jsonString);
		}
	}

	private JsonValue tryToParseJsonStringAsObject(String jsonString) {
		JSONObject jsonObject = new JSONObject(jsonString);
		return OrgJsonValueFactory.createFromOrgJsonObject(jsonObject);
	}

	private JsonValue tryToParseJsonStringAsArray(String jsonString) {
		JSONArray jsonArray = new JSONArray(jsonString);
		return OrgJsonValueFactory.createFromOrgJsonObject(jsonArray);
	}

	@Override
	public JsonObject parseStringAsObject(String jsonString) {
		try {
			return (JsonObject) tryToParseJsonStringAsObject(jsonString);
		} catch (Exception e) {
			throw new JsonParseException("Unable to parse json string", e);
		}
	}

	@Override
	public JsonArray parseStringAsArray(String jsonString) {
		try {
			return (JsonArray) tryToParseJsonStringAsArray(jsonString);
		} catch (Exception e) {
			throw new JsonParseException("Unable to parse json string", e);
		}
	}

}
