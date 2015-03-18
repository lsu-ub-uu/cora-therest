package epc.therest.json.parser;

public interface JsonParser {

	JsonValue parseString(String jsonString);

	JsonObject parseStringAsObject(String jsonString);

	JsonArray parseStringAsArray(String jsonString);

}
