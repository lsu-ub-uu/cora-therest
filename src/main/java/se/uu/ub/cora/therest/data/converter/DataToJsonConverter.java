package se.uu.ub.cora.therest.data.converter;

import se.uu.ub.cora.therest.json.builder.JsonObjectBuilder;

public abstract class DataToJsonConverter {

	public abstract String toJson();

	abstract JsonObjectBuilder toJsonObjectBuilder();
}
