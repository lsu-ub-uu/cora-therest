package epc.therest.json.parser;

public interface JsonArray extends JsonValue, Iterable<JsonValue> {

	JsonValue get(int index);

}
