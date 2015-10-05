package se.uu.ub.cora.therest.json.builder.org;

import se.uu.ub.cora.therest.json.builder.JsonArrayBuilder;
import se.uu.ub.cora.therest.json.builder.JsonObjectBuilder;
import se.uu.ub.cora.therest.json.parser.JsonObject;
import se.uu.ub.cora.therest.json.parser.org.OrgJsonObjectAdapter;

public class OrgJsonObjectBuilderAdapter implements JsonObjectBuilder {
	private org.json.JSONObject orgJsonObject = new org.json.JSONObject();

	@Override
	public void addKeyString(String key, String value) {
		orgJsonObject.put(key, value);
	}

	@Override
	public void addKeyJsonObjectBuilder(String key, JsonObjectBuilder jsonObjectBuilder) {
		OrgJsonObjectBuilderAdapter objectBuilderAdapter = (OrgJsonObjectBuilderAdapter) jsonObjectBuilder;
		orgJsonObject.put(key, objectBuilderAdapter.getWrappedBuilder());
	}

	org.json.JSONObject getWrappedBuilder() {
		return orgJsonObject;
	}

	@Override
	public void addKeyJsonArrayBuilder(String key, JsonArrayBuilder jsonArrayBuilder) {
		OrgJsonArrayBuilderAdapter arrayBuilderAdapter = (OrgJsonArrayBuilderAdapter) jsonArrayBuilder;
		orgJsonObject.put(key, arrayBuilderAdapter.getWrappedBuilder());
	}

	@Override
	public JsonObject toJsonObject() {
		return OrgJsonObjectAdapter.usingOrgJsonObject(orgJsonObject);
	}

	@Override
	public String toJsonFormattedString() {
		return orgJsonObject.toString();
	}

}
