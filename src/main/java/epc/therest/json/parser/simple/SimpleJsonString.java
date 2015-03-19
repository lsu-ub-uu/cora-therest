package epc.therest.json.parser.simple;

import epc.therest.json.parser.JsonString;
import epc.therest.json.parser.JsonValueType;

public class SimpleJsonString implements JsonString {

	private String value;

	public SimpleJsonString(String value) {
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
