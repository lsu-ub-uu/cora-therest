package se.uu.ub.cora.therest.json.parser.org;

import se.uu.ub.cora.therest.json.parser.JsonString;
import se.uu.ub.cora.therest.json.parser.JsonValueType;

public class OrgJsonStringAdapter implements JsonString {

	private String value;

	public OrgJsonStringAdapter(String value) {
		this.value = value;
	}

	@Override
	public JsonValueType getValueType() {
		return JsonValueType.STRING;
	}

	@Override
	public String getStringValue() {
		return value;
	}

}
