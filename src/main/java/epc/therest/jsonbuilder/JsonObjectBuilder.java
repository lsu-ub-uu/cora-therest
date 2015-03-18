package epc.therest.jsonbuilder;

import epc.therest.jsonparser.JsonObject;

public interface JsonObjectBuilder {

	void add(String key, String value);

	JsonObject build();

	void add(String dataId, JsonObjectBuilder jsonObjectBuilder);

	void add(String key, JsonArrayBuilder jsonArrayBuilder);

}
