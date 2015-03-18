package epc.therest.json;

import epc.therest.jsonbuilder.JsonObjectBuilder;

public abstract class DataToJsonConverter {

	public abstract String toJson();

	abstract JsonObjectBuilder toJsonObjectBuilder();
}
