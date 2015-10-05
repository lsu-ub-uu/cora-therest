package se.uu.ub.cora.therest.json.builder.org;

import se.uu.ub.cora.therest.json.builder.JsonArrayBuilder;
import se.uu.ub.cora.therest.json.builder.JsonObjectBuilder;
import se.uu.ub.cora.therest.json.parser.JsonArray;
import se.uu.ub.cora.therest.json.parser.org.OrgJsonArrayAdapter;

public class OrgJsonArrayBuilderAdapter implements JsonArrayBuilder {

	private org.json.JSONArray orgJsonArray = new org.json.JSONArray();

	@Override
	public void addString(String value) {
		orgJsonArray.put(value);
	}

	@Override
	public void addJsonObjectBuilder(JsonObjectBuilder jsonObjectBuilder) {
		OrgJsonObjectBuilderAdapter objectBuilderAdapter = (OrgJsonObjectBuilderAdapter) jsonObjectBuilder;
		orgJsonArray.put(objectBuilderAdapter.getWrappedBuilder());
	}

	@Override
	public void addJsonArrayBuilder(JsonArrayBuilder jsonArrayBuilder) {
		OrgJsonArrayBuilderAdapter arrayBuilderAdapter = (OrgJsonArrayBuilderAdapter) jsonArrayBuilder;
		orgJsonArray.put(arrayBuilderAdapter.getWrappedBuilder());
	}

	org.json.JSONArray getWrappedBuilder() {
		return orgJsonArray;
	}

	@Override
	public JsonArray toJsonArray() {
		return OrgJsonArrayAdapter.usingOrgJsonArray(orgJsonArray);
	}

	@Override
	public String toJsonFormattedString() {
		return orgJsonArray.toString();
	}

}
