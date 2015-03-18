package epc.therest.json.builder;

import epc.therest.json.parser.JsonObject;

public interface JsonObjectBuilder {

	void add(String key, String value);

	JsonObject build();

	void add(String dataId, JsonObjectBuilder jsonObjectBuilder);

	void add(String key, JsonArrayBuilder jsonArrayBuilder);

}
