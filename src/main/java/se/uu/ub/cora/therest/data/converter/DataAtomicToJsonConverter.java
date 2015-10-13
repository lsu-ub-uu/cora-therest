package se.uu.ub.cora.therest.data.converter;

import se.uu.ub.cora.therest.data.RestDataAtomic;
import se.uu.ub.cora.therest.json.builder.JsonBuilderFactory;
import se.uu.ub.cora.therest.json.builder.JsonObjectBuilder;

public final class DataAtomicToJsonConverter extends DataToJsonConverter {

	private RestDataAtomic restDataAtomic;
	private JsonBuilderFactory factory;

	public static DataToJsonConverter usingJsonFactoryForRestDataAtomic(JsonBuilderFactory factory,
			RestDataAtomic dataAtomic) {
		return new DataAtomicToJsonConverter(factory, dataAtomic);
	}

	private DataAtomicToJsonConverter(JsonBuilderFactory factory, RestDataAtomic dataAtomic) {
		this.factory = factory;
		this.restDataAtomic = dataAtomic;
	}

	@Override
	public String toJson() {
		JsonObjectBuilder atomic = toJsonObjectBuilder();
		return atomic.toJsonFormattedString();
	}

	@Override
	JsonObjectBuilder toJsonObjectBuilder() {
		JsonObjectBuilder jsonObjectBuilder = factory.createObjectBuilder();

		jsonObjectBuilder.addKeyString("name",restDataAtomic.getNameInData());
		jsonObjectBuilder.addKeyString("value", restDataAtomic.getValue());
		return jsonObjectBuilder;
	}
}
