package epc.therest.json.builder.simple;

import org.json.simple.JSONObject;

import epc.therest.json.builder.JsonArrayBuilder;
import epc.therest.json.builder.JsonObjectBuilder;
import epc.therest.json.parser.JsonObject;
import epc.therest.json.parser.simple.SimpleJsonObject;

public class SimpleJsonObjectBuilder implements JsonObjectBuilder {

	private JSONObject jsonObject = new JSONObject();

	@SuppressWarnings("unchecked")
	@Override
	public void add(String key, String value) {
		jsonObject.put(key, value);

	}

	@Override
	public JsonObject build() {
		return new SimpleJsonObject(jsonObject);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void add(String dataId, JsonObjectBuilder jsonObjectBuilder) {
		SimpleJsonObjectBuilder simpleJsonObjectBuilder = (SimpleJsonObjectBuilder) jsonObjectBuilder;
		jsonObject.put(dataId, simpleJsonObjectBuilder.getWrappedBuilder());
	}

	@SuppressWarnings("unchecked")
	@Override
	public void add(String key, JsonArrayBuilder jsonArrayBuilder) {
		SimpleJsonArrayBuilder simpleJsonArrayBuilder = (SimpleJsonArrayBuilder) jsonArrayBuilder;
		jsonObject.put(key, simpleJsonArrayBuilder.getWrappedBuilder());

	}

	public Object getWrappedBuilder() {
		return jsonObject;
	}

}
