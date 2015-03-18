package epc.therest.jsonparser;

import java.util.Map.Entry;
import java.util.Set;

public interface JsonObject extends JsonValue {

	Set<String> keySet();

	JsonValue getValue(String key);

	Set<Entry<String, JsonValue>> entrySet();

	int size();

	JsonObject getObject(String key);

	JsonArray getArray(String key);

	boolean containsKey(String key);

	String toJsonString();

}
