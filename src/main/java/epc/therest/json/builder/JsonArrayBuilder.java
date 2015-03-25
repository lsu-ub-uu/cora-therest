package epc.therest.json.builder;

import epc.therest.json.parser.JsonArray;

public interface JsonArrayBuilder {

	void addString(String value);

	void addJsonObjectBuilder(JsonObjectBuilder jsonObjectBuilder);

	void addJsonArrayBuilder(JsonArrayBuilder jsonArrayBuilder);

	JsonArray toJsonArray();

	String toJsonFormattedString();

}
