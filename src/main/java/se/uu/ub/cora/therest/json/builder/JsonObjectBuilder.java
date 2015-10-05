package se.uu.ub.cora.therest.json.builder;

import se.uu.ub.cora.therest.json.parser.JsonObject;

public interface JsonObjectBuilder {

	void addKeyString(String key, String value);

	void addKeyJsonObjectBuilder(String key, JsonObjectBuilder jsonObjectBuilder);

	void addKeyJsonArrayBuilder(String key, JsonArrayBuilder jsonArrayBuilder);

	JsonObject toJsonObject();

	String toJsonFormattedString();
}
