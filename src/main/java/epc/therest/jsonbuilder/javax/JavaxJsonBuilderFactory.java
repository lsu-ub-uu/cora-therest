package epc.therest.jsonbuilder.javax;

import java.util.HashMap;
import java.util.Map;

import javax.json.Json;

import epc.therest.jsonbuilder.JsonArrayBuilder;
import epc.therest.jsonbuilder.JsonBuilderFactory;
import epc.therest.jsonbuilder.JsonObjectBuilder;

public class JavaxJsonBuilderFactory implements JsonBuilderFactory {
	private javax.json.JsonBuilderFactory jsonBuilderFactory;

	public JavaxJsonBuilderFactory() {
		Map<String, Object> config = new HashMap<>();
		jsonBuilderFactory = Json.createBuilderFactory(config);
	}

	@Override
	public JsonObjectBuilder createObjectBuilder() {
		return new JavaxJsonObjectBuilder(jsonBuilderFactory.createObjectBuilder());
	}

	@Override
	public JsonArrayBuilder createArrayBuilder() {
		return new JavaxJsonArrayBuilder(jsonBuilderFactory.createArrayBuilder());
	}

}
