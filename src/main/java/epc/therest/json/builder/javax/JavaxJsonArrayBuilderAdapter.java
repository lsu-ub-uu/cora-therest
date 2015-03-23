package epc.therest.json.builder.javax;

import epc.therest.json.builder.JsonArrayBuilder;
import epc.therest.json.builder.JsonObjectBuilder;
import epc.therest.json.parser.JsonArray;
import epc.therest.json.parser.javax.JavaxJsonArrayAdapter;

public class JavaxJsonArrayBuilderAdapter implements JsonArrayBuilder {

	private javax.json.JsonArrayBuilder javaxJsonArrayBuilder;

	public JavaxJsonArrayBuilderAdapter(javax.json.JsonArrayBuilder javaxJsonArrayBuilder) {
		this.javaxJsonArrayBuilder = javaxJsonArrayBuilder;
	}

	@Override
	public void add(String value) {
		javaxJsonArrayBuilder.add(value);
	}

	@Override
	public void add(JsonArrayBuilder jsonArrayBuilder) {
		JavaxJsonArrayBuilderAdapter javaxAdapter = (JavaxJsonArrayBuilderAdapter) jsonArrayBuilder;
		javax.json.JsonArrayBuilder javaxJsonArrayBuilderChild = javaxAdapter.getWrappedBuilder();
		javaxJsonArrayBuilder.add(javaxJsonArrayBuilderChild);
	}

	javax.json.JsonArrayBuilder getWrappedBuilder() {
		return javaxJsonArrayBuilder;
	}

	@Override
	public void add(JsonObjectBuilder jsonObjectBuilder) {
		javax.json.JsonObjectBuilder javaxJsonObjectBuilderChild = ((JavaxJsonObjectBuilderAdapter) jsonObjectBuilder)
				.getWrappedBuilder();
		javaxJsonArrayBuilder.add(javaxJsonObjectBuilderChild);
	}

	@Override
	public JsonArray build() {
		return JavaxJsonArrayAdapter.usingJavaxJsonArrayAdapter(javaxJsonArrayBuilder.build());
	}
}
