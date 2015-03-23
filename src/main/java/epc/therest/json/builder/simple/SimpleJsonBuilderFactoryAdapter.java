package epc.therest.json.builder.simple;

import epc.therest.json.builder.JsonArrayBuilder;
import epc.therest.json.builder.JsonBuilderFactory;
import epc.therest.json.builder.JsonObjectBuilder;

public class SimpleJsonBuilderFactoryAdapter implements JsonBuilderFactory {

	@Override
	public JsonObjectBuilder createObjectBuilder() {
		return new SimpleJsonObjectBuilderAdapter();
	}

	@Override
	public JsonArrayBuilder createArrayBuilder() {
		return new SimpleJsonArrayBuilderAdapter();
	}

}
