package epc.therest.json.builder.simple;

import org.json.simple.JSONArray;

import epc.therest.json.builder.JsonArrayBuilder;
import epc.therest.json.builder.JsonObjectBuilder;
import epc.therest.json.parser.JsonArray;
import epc.therest.json.parser.simple.SimpleJsonArrayAdapter;

public class SimpleJsonArrayBuilderAdapter implements JsonArrayBuilder {

	private JSONArray jsonArray = new JSONArray();

	@SuppressWarnings("unchecked")
	@Override
	public void addString(String value) {
		jsonArray.add(value);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void addJsonArrayBuilder(JsonArrayBuilder jsonArrayBuilder) {
		SimpleJsonArrayBuilderAdapter simpleJsonArrayBuilderAdapter = (SimpleJsonArrayBuilderAdapter) jsonArrayBuilder;
		jsonArray.add(simpleJsonArrayBuilderAdapter.getWrappedBuilder());
	}

	JSONArray getWrappedBuilder() {
		return jsonArray;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void addJsonObjectBuilder(JsonObjectBuilder jsonObjectBuilder) {
		SimpleJsonObjectBuilderAdapter simpleJsonObjectBuilderAdapter = (SimpleJsonObjectBuilderAdapter) jsonObjectBuilder;
		jsonArray.add(simpleJsonObjectBuilderAdapter.getWrappedBuilder());
	}

	@Override
	public JsonArray toJsonArray() {
		return new SimpleJsonArrayAdapter(jsonArray);
	}

	@Override
	public String toJsonFormattedString() {
		// TODO Auto-generated method stub
		return null;
	}

}
