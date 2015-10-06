package se.uu.ub.cora.therest.data.converter;

import se.uu.ub.cora.therest.data.RestDataAttribute;
import se.uu.ub.cora.therest.json.builder.JsonBuilderFactory;
import se.uu.ub.cora.therest.json.builder.JsonObjectBuilder;

public final class DataAttributeToJsonConverter extends DataToJsonConverter {
	private JsonBuilderFactory factory;
	private RestDataAttribute restDataAttribute;

	public static DataToJsonConverter forRestDataAttribute(JsonBuilderFactory factory,
			RestDataAttribute dataAttribute) {
		return new DataAttributeToJsonConverter(factory, dataAttribute);
	}

	private DataAttributeToJsonConverter(JsonBuilderFactory factory,
			RestDataAttribute dataAttribute) {
		this.factory = factory;
		this.restDataAttribute = dataAttribute;
	}

	@Override
	public String toJson() {
		JsonObjectBuilder attribute = toJsonObjectBuilder();
		return attribute.toJsonFormattedString();
	}

	@Override
	JsonObjectBuilder toJsonObjectBuilder() {
		JsonObjectBuilder jsonObjectBuilder = factory.createObjectBuilder();

		jsonObjectBuilder.addKeyString(restDataAttribute.getNameInData(),
				restDataAttribute.getValue());
		return jsonObjectBuilder;
	}

}
