package se.uu.ub.cora.therest.json.builder;

public interface JsonBuilderFactory {

	JsonObjectBuilder createObjectBuilder();

	JsonArrayBuilder createArrayBuilder();

}
