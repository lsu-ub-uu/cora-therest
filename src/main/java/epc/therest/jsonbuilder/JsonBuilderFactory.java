package epc.therest.jsonbuilder;

public interface JsonBuilderFactory {

	JsonObjectBuilder createObjectBuilder();

	JsonArrayBuilder createArrayBuilder();

}
