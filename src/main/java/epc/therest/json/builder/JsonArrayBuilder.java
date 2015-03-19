package epc.therest.json.builder;

import epc.therest.json.parser.JsonArray;

public interface JsonArrayBuilder {

	void add(String value);

	void add(JsonArrayBuilder jsonArrayBuilder);

	void add(JsonObjectBuilder jsonObjectBuilder);

	JsonArray build();

}
