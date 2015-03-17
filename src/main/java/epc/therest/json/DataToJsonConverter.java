package epc.therest.json;

import javax.json.JsonObjectBuilder;

public abstract class DataToJsonConverter {

	public abstract String toJson();

	abstract JsonObjectBuilder toJsonObjectBuilder();
}
