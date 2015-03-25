package epc.therest.json.parser;

public interface JsonArray extends JsonValue, Iterable<JsonValue> {

	JsonValue getValue(int index);

	JsonString getValueAsJsonString(int index);

	JsonObject getValueAsJsonObject(int index);

	JsonArray getValueAsJsonArray(int index);

	String toJsonFormattedString();
}
