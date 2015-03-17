package epc.therest.jsonparser;

public interface JsonParser {

	JsonValue parseString(String jsonString);

	JsonObject parseStringAsObject(String jsonString);

	JsonArray parseStringAsArray(String jsonString);

}
