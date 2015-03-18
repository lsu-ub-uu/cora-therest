package epc.therest.jsonbuilder;

import epc.therest.jsonparser.JsonArray;

public interface JsonArrayBuilder {

	void add(String value);

	JsonArray build();

	void add(JsonArrayBuilder jsonArrayBuilder);

	void add(JsonObjectBuilder jsonObjectBuilder);

}
