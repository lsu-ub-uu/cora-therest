package se.uu.ub.cora.therest.json.parser;

import java.util.Map.Entry;
import java.util.Set;

public interface JsonObject extends JsonValue {

	JsonValue getValue(String key);

	JsonString getValueAsJsonString(String key);

	JsonObject getValueAsJsonObject(String key);

	JsonArray getValueAsJsonArray(String key);

	boolean containsKey(String key);

	Set<String> keySet();

	Set<Entry<String, JsonValue>> entrySet();

	int size();

	String toJsonFormattedString();
}
