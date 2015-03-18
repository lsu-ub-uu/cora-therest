package epc.therest.json.builder;

public interface JsonBuilderFactory {

	JsonObjectBuilder createObjectBuilder();

	JsonArrayBuilder createArrayBuilder();

}
