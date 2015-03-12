package epc.therest.json;

import javax.json.JsonObjectBuilder;

public abstract class JsonCreator {

	public abstract String toJson();

	abstract JsonObjectBuilder toJsonObjectBuilder();
}
