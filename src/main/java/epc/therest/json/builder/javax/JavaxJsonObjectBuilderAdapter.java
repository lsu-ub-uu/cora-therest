package epc.therest.json.builder.javax;

import epc.therest.json.builder.JsonArrayBuilder;
import epc.therest.json.builder.JsonObjectBuilder;
import epc.therest.json.parser.JsonObject;
import epc.therest.json.parser.javax.JavaxJsonObjectAdapter;

public class JavaxJsonObjectBuilderAdapter implements JsonObjectBuilder {

	private javax.json.JsonObjectBuilder javaxJsonObjectBuilder;

	public JavaxJsonObjectBuilderAdapter(javax.json.JsonObjectBuilder javaxJsonObjectBuilder) {
		this.javaxJsonObjectBuilder = javaxJsonObjectBuilder;
	}

	@Override
	public void addKeyString(String key, String value) {
		javaxJsonObjectBuilder.add(key, value);
	}

	@Override
	public void addKeyJsonObjectBuilder(String key, JsonObjectBuilder jsonObjectBuilder) {
		javax.json.JsonObjectBuilder javaxJsonObjectBuilderChild = ((JavaxJsonObjectBuilderAdapter) jsonObjectBuilder)
				.getWrappedBuilder();
		javaxJsonObjectBuilder.add(key, javaxJsonObjectBuilderChild);

	}

	@Override
	public JsonObject toJsonObject() {
		return JavaxJsonObjectAdapter.usingJavaxJsonObjectAdapter(javaxJsonObjectBuilder.build());
	}

	javax.json.JsonObjectBuilder getWrappedBuilder() {
		return javaxJsonObjectBuilder;
	}

	@Override
	public void addKeyJsonArrayBuilder(String key, JsonArrayBuilder jsonArrayBuilder) {
		JavaxJsonArrayBuilderAdapter javaxAdapter = (JavaxJsonArrayBuilderAdapter) jsonArrayBuilder;
		javax.json.JsonArrayBuilder javaxJsonArrayBuilderChild = javaxAdapter.getWrappedBuilder();
		javaxJsonObjectBuilder.add(key, javaxJsonArrayBuilderChild);

	}

	@Override
	public String toJsonFormattedString() {
		// TODO Auto-generated method stub
		return null;
	}
}
