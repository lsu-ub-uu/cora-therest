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

	private static final String UNABLE_TO_PARSE_JSON_STRING = "Unable to parse json string";

	@Override
	public JsonValue parseString(String jsonString) {
		try {
			JsonValue jsonValue = tryToParseJsonString(jsonString);
			validateEntireStringWasParsed(jsonString, jsonValue);
			return jsonValue;
		} catch (Exception e) {
			throw new JsonParseException(UNABLE_TO_PARSE_JSON_STRING, e);
		}
	}

	private JsonValue tryToParseJsonString(String jsonString) {
		if (stringIsBelivedToContainAnObject(jsonString)) {
			return tryToParseJsonStringAsObject(jsonString);
		}
		return tryToParseJsonStringAsArray(jsonString);
	}

	private boolean stringIsBelivedToContainAnObject(String jsonString) {
		return jsonString.trim().startsWith("{");
	}

	private JsonValue tryToParseJsonStringAsObject(String jsonString) {
		JSONObject jsonObject = new JSONObject(jsonString);
		return OrgJsonValueFactory.createFromOrgJsonObject(jsonObject);
	}

	private JsonValue tryToParseJsonStringAsArray(String jsonString) {
		JSONArray jsonArray = new JSONArray(jsonString);
		return OrgJsonValueFactory.createFromOrgJsonObject(jsonArray);
	}

	private void validateEntireStringWasParsed(String jsonString, JsonValue jsonValue) {
		String allWhitespace = "\\s";
		String originalJsonWithoutWhitespace = jsonString.replaceAll(allWhitespace, "");
		String recreatedJsonString = recreateJsonStringFromJsonValue(jsonValue);
		String recreatedJsonWithoutWhitespace = recreatedJsonString.replaceAll(allWhitespace, "");
		if (originalJsonWithoutWhitespace.length() != recreatedJsonWithoutWhitespace.length()) {
			throw new JsonParseException("Unable to fully parse json string");
		}
	}

	private String recreateJsonStringFromJsonValue(JsonValue jsonValue) {
		String recreatedJsonString = "";
		if (JsonValueType.OBJECT.equals(jsonValue.getValueType())) {
			recreatedJsonString = ((JsonObject) jsonValue).toJsonFormattedString();
		} else {
			recreatedJsonString = ((JsonArray) jsonValue).toJsonFormattedString();
		}
		return recreatedJsonString;
	}

	@Override
	public JsonObject parseStringAsObject(String jsonString) {
		try {
			return (JsonObject) tryToParseJsonStringAsObject(jsonString);
		} catch (Exception e) {
			throw new JsonParseException(UNABLE_TO_PARSE_JSON_STRING, e);
		}
	}

	@Override
	public JsonArray parseStringAsArray(String jsonString) {
		try {
			return (JsonArray) tryToParseJsonStringAsArray(jsonString);
		} catch (Exception e) {
			throw new JsonParseException(UNABLE_TO_PARSE_JSON_STRING, e);
		}
	}

}
