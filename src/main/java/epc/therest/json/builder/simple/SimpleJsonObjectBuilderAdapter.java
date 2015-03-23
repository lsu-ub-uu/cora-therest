package epc.therest.json.builder.simple;

import org.json.simple.JSONObject;

import epc.therest.json.builder.JsonArrayBuilder;
import epc.therest.json.builder.JsonObjectBuilder;
import epc.therest.json.parser.JsonObject;
import epc.therest.json.parser.simple.SimpleJsonObjectAdapter;

public class SimpleJsonObjectBuilderAdapter implements JsonObjectBuilder {

	private JSONObject jsonObject = new JSONObject();

	@SuppressWarnings("unchecked")
	@Override
	public void add(String key, String value) {
		jsonObject.put(key, value);

	}

	@Override
	public JsonObject build() {
		return new SimpleJsonObjectAdapter(jsonObject);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void add(String dataId, JsonObjectBuilder jsonObjectBuilder) {
		SimpleJsonObjectBuilderAdapter simpleJsonObjectBuilderAdapter = (SimpleJsonObjectBuilderAdapter) jsonObjectBuilder;
		jsonObject.put(dataId, simpleJsonObjectBuilderAdapter.getWrappedBuilder());
	}

	@SuppressWarnings("unchecked")
	@Override
	public void add(String key, JsonArrayBuilder jsonArrayBuilder) {
		SimpleJsonArrayBuilderAdapter simpleJsonArrayBuilderAdapter = (SimpleJsonArrayBuilderAdapter) jsonArrayBuilder;
		jsonObject.put(key, simpleJsonArrayBuilderAdapter.getWrappedBuilder());

	}

	public Object getWrappedBuilder() {
		return jsonObject;
	}

}
