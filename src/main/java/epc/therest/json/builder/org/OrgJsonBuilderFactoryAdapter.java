package epc.therest.json.builder.org;

import epc.therest.json.builder.JsonArrayBuilder;
import epc.therest.json.builder.JsonBuilderFactory;
import epc.therest.json.builder.JsonObjectBuilder;

public class OrgJsonBuilderFactoryAdapter implements JsonBuilderFactory {

	@Override
	public JsonObjectBuilder createObjectBuilder() {
		return new OrgJsonObjectBuilderAdapter();
	}

	@Override
	public JsonArrayBuilder createArrayBuilder() {
		return new OrgJsonArrayBuilderAdapter();
	}

}
