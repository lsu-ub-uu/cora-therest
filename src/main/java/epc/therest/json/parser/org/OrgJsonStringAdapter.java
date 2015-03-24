package epc.therest.json.parser.org;

import epc.therest.json.parser.JsonString;
import epc.therest.json.parser.JsonValueType;

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
