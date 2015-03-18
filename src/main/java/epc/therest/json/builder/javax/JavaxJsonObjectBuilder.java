package epc.therest.json.builder.javax;

import epc.therest.json.builder.JsonArrayBuilder;
import epc.therest.json.builder.JsonObjectBuilder;
import epc.therest.json.parser.JsonObject;
import epc.therest.json.parser.javax.JavaxJsonClassFactoryImp;
import epc.therest.json.parser.javax.JavaxJsonObject;

public class JavaxJsonObjectBuilder implements JsonObjectBuilder {

	private javax.json.JsonObjectBuilder javaxJsonObjectBuilder;

	public JavaxJsonObjectBuilder(javax.json.JsonObjectBuilder javaxJsonObjectBuilder) {
		this.javaxJsonObjectBuilder = javaxJsonObjectBuilder;
	}

	@Override
	public void add(String key, String value) {
		javaxJsonObjectBuilder.add(key, value);
	}

	@Override
	public void add(String key, JsonObjectBuilder jsonObjectBuilder) {
		javax.json.JsonObjectBuilder javaxJsonObjectBuilderChild = ((JavaxJsonObjectBuilder) jsonObjectBuilder)
				.getWrappedBuilder();
		javaxJsonObjectBuilder.add(key, javaxJsonObjectBuilderChild);

	}

	@Override
	public JsonObject build() {
		return JavaxJsonObject.usingJavaxJsonObject(new JavaxJsonClassFactoryImp(),
				javaxJsonObjectBuilder.build());
	}

	javax.json.JsonObjectBuilder getWrappedBuilder() {
		return javaxJsonObjectBuilder;
	}

	@Override
	public void add(String key, JsonArrayBuilder jsonArrayBuilder) {
		JavaxJsonArrayBuilder javaxBuilder = (JavaxJsonArrayBuilder) jsonArrayBuilder;
		javax.json.JsonArrayBuilder javaxJsonArrayBuilderChild = javaxBuilder.getWrappedBuilder();
		javaxJsonObjectBuilder.add(key, javaxJsonArrayBuilderChild);

	}
}
