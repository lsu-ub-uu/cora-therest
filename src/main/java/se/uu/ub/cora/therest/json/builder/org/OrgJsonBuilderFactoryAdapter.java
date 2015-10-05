package se.uu.ub.cora.therest.json.builder.org;

import se.uu.ub.cora.therest.json.builder.JsonArrayBuilder;
import se.uu.ub.cora.therest.json.builder.JsonBuilderFactory;
import se.uu.ub.cora.therest.json.builder.JsonObjectBuilder;

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
