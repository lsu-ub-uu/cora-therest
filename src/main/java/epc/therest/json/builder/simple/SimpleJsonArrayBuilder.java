package epc.therest.json.builder.simple;

import org.json.simple.JSONArray;

import epc.therest.json.builder.JsonArrayBuilder;
import epc.therest.json.builder.JsonObjectBuilder;
import epc.therest.json.parser.JsonArray;
import epc.therest.json.parser.simple.SimpleJsonArray;

public class SimpleJsonArrayBuilder implements JsonArrayBuilder {

	private JSONArray jsonArray = new JSONArray();

	@SuppressWarnings("unchecked")
	@Override
	public void add(String value) {
		jsonArray.add(value);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void add(JsonArrayBuilder jsonArrayBuilder) {
		SimpleJsonArrayBuilder simpleJsonArrayBuilder = (SimpleJsonArrayBuilder) jsonArrayBuilder;
		jsonArray.add(simpleJsonArrayBuilder.getWrappedBuilder());
	}

	JSONArray getWrappedBuilder() {
		return jsonArray;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void add(JsonObjectBuilder jsonObjectBuilder) {
		SimpleJsonObjectBuilder simpleJsonObjectBuilder = (SimpleJsonObjectBuilder) jsonObjectBuilder;
		jsonArray.add(simpleJsonObjectBuilder.getWrappedBuilder());
	}

	@Override
	public JsonArray build() {
		return new SimpleJsonArray(jsonArray);
	}

}
