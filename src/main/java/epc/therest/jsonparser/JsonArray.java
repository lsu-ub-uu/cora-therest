package epc.therest.jsonparser;

public interface JsonArray extends JsonValue, Iterable<JsonValue> {

	JsonValue get(int index);

}
