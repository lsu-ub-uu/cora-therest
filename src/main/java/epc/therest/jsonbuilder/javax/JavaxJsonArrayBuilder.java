package epc.therest.jsonbuilder.javax;

import epc.therest.jsonbuilder.JsonArrayBuilder;
import epc.therest.jsonbuilder.JsonObjectBuilder;
import epc.therest.jsonparser.JsonArray;
import epc.therest.jsonparser.javax.JavaxJsonArray;
import epc.therest.jsonparser.javax.JavaxJsonClassFactoryImp;

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
	public JsonArray build() {
		return JavaxJsonArray.usingJavaxJsonArray(new JavaxJsonClassFactoryImp(),
				javaxJsonArrayBuilder.build());
	}

	@Override
	public void add(JsonArrayBuilder jsonArrayBuilder) {
		JavaxJsonArrayBuilder javaxBuilder = (JavaxJsonArrayBuilder) jsonArrayBuilder;
		javax.json.JsonArrayBuilder javaxJsonArrayBuilderChild = javaxBuilder
				.getWrappedBuilder();
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
}
