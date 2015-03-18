package epc.therest.data.converter;

import epc.therest.json.builder.JsonObjectBuilder;

public abstract class DataToJsonConverter {

	public abstract String toJson();

	abstract JsonObjectBuilder toJsonObjectBuilder();
}
