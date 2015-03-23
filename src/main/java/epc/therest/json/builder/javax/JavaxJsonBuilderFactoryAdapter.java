package epc.therest.json.builder.javax;

import java.util.HashMap;
import java.util.Map;

import javax.json.Json;

import epc.therest.json.builder.JsonArrayBuilder;
import epc.therest.json.builder.JsonBuilderFactory;
import epc.therest.json.builder.JsonObjectBuilder;

public class JavaxJsonBuilderFactoryAdapter implements JsonBuilderFactory {
	private javax.json.JsonBuilderFactory adaptedJsonBuilderFactory;

	public JavaxJsonBuilderFactoryAdapter() {
		Map<String, Object> config = new HashMap<>();
		adaptedJsonBuilderFactory = Json.createBuilderFactory(config);
	}

	@Override
	public JsonObjectBuilder createObjectBuilder() {
		javax.json.JsonObjectBuilder jsonObjectBuilder = adaptedJsonBuilderFactory.createObjectBuilder();
		return new JavaxJsonObjectBuilderAdapter(jsonObjectBuilder);
	}

	@Override
	public JsonArrayBuilder createArrayBuilder() {
		javax.json.JsonArrayBuilder jsonArrayBuilder = adaptedJsonBuilderFactory.createArrayBuilder();
		return new JavaxJsonArrayBuilderAdapter(jsonArrayBuilder);
	}

}
