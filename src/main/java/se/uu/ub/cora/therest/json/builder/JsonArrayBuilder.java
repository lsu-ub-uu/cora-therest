package se.uu.ub.cora.therest.json.builder;

import se.uu.ub.cora.therest.json.parser.JsonArray;

public interface JsonArrayBuilder {

	void addString(String value);

	void addJsonObjectBuilder(JsonObjectBuilder jsonObjectBuilder);

	void addJsonArrayBuilder(JsonArrayBuilder jsonArrayBuilder);

	JsonArray toJsonArray();

	String toJsonFormattedString();

}
