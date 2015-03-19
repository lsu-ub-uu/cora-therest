package epc.therest.json.builder.javax;

import java.util.HashMap;
import java.util.Map;

import javax.json.Json;

import epc.therest.json.builder.JsonArrayBuilder;
import epc.therest.json.builder.JsonBuilderFactory;
import epc.therest.json.builder.JsonObjectBuilder;

public class JavaxJsonBuilderFactory implements JsonBuilderFactory {
	private javax.json.JsonBuilderFactory jsonBuilderFactory;

	public JavaxJsonBuilderFactory() {
		Map<String, Object> config = new HashMap<>();
		jsonBuilderFactory = Json.createBuilderFactory(config);
	}

	@Override
	public JsonObjectBuilder createObjectBuilder() {
		javax.json.JsonObjectBuilder jsonObjectBuilder = jsonBuilderFactory.createObjectBuilder();
		return new JavaxJsonObjectBuilder(jsonObjectBuilder);
	}

	@Override
	public JsonArrayBuilder createArrayBuilder() {
		javax.json.JsonArrayBuilder jsonArrayBuilder = jsonBuilderFactory.createArrayBuilder();
		return new JavaxJsonArrayBuilder(jsonArrayBuilder);
	}

}
