package epc.therest.json;

import javax.json.JsonObjectBuilder;

public interface JsonCreator {

	String toJson();

	JsonObjectBuilder toJsonObjectBuilder();
}
