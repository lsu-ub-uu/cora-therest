package epc.therest.json;

import epc.therest.data.RestDataAtomic;
import epc.therest.jsonbuilder.JsonBuilderFactory;
import epc.therest.jsonbuilder.JsonObjectBuilder;
import epc.therest.jsonparser.JsonObject;

public final class DataAtomicToJsonConverter extends DataToJsonConverter {

	private RestDataAtomic restDataAtomic;
	private JsonBuilderFactory factory;

	public static DataToJsonConverter forRestDataAtomic(JsonBuilderFactory factory,
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
		JsonObject build = atomic.build();
		return build.toJsonString();
	}

	@Override
	JsonObjectBuilder toJsonObjectBuilder() {
		JsonObjectBuilder jsonObjectBuilder = factory.createObjectBuilder();

		jsonObjectBuilder.add(restDataAtomic.getDataId(), restDataAtomic.getValue());
		return jsonObjectBuilder;
	}
}
