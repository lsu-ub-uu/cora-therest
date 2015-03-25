package epc.therest.json.parser.org;

import org.json.JSONArray;
import org.json.JSONObject;

import epc.therest.json.parser.JsonValue;

public final class OrgJsonValueFactory {
	private OrgJsonValueFactory() {
		// not in use
		throw new UnsupportedOperationException();
	}

	public static JsonValue createFromOrgJsonObject(Object orgJsonObject) {
		if (orgJsonObject instanceof org.json.JSONObject) {
			return OrgJsonObjectAdapter.usingOrgJsonObject((JSONObject) orgJsonObject);
		}
		if (orgJsonObject instanceof org.json.JSONArray) {
			return OrgJsonArrayAdapter.usingOrgJsonArray((JSONArray) orgJsonObject);
		}
		return new OrgJsonStringAdapter((String) orgJsonObject);
	}

}
