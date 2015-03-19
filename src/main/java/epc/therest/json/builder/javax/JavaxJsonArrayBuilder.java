package epc.therest.json.builder.javax;

import epc.therest.json.builder.JsonArrayBuilder;
import epc.therest.json.builder.JsonObjectBuilder;
import epc.therest.json.parser.JsonArray;
import epc.therest.json.parser.javax.JavaxJsonArray;
import epc.therest.json.parser.javax.JavaxJsonClassFactoryImp;

public class JavaxJsonArrayBuilder implements JsonArrayBuilder {

	private javax.json.JsonArrayBuilder javaxJsonArrayBuilder;

	public JavaxJsonArrayBuilder(javax.json.JsonArrayBuilder javaxJsonArrayBuilder) {
		this.javaxJsonArrayBuilder = javaxJsonArrayBuilder;
	}

	@Override
	public void add(String value) {
		javaxJsonArrayBuilder.add(value);
	}

	@Override
	public void add(JsonArrayBuilder jsonArrayBuilder) {
		JavaxJsonArrayBuilder javaxBuilder = (JavaxJsonArrayBuilder) jsonArrayBuilder;
		javax.json.JsonArrayBuilder javaxJsonArrayBuilderChild = javaxBuilder.getWrappedBuilder();
		javaxJsonArrayBuilder.add(javaxJsonArrayBuilderChild);
	}

	javax.json.JsonArrayBuilder getWrappedBuilder() {
		return javaxJsonArrayBuilder;
	}

	@Override
	public void add(JsonObjectBuilder jsonObjectBuilder) {
		javax.json.JsonObjectBuilder javaxJsonObjectBuilderChild = ((JavaxJsonObjectBuilder) jsonObjectBuilder)
				.getWrappedBuilder();
		javaxJsonArrayBuilder.add(javaxJsonObjectBuilderChild);
	}

	@Override
	public JsonArray build() {
		return JavaxJsonArray.usingJavaxJsonArray(new JavaxJsonClassFactoryImp(),
				javaxJsonArrayBuilder.build());
	}
}
