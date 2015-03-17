package epc.therest.jsonparser.javax;

import epc.therest.jsonparser.JsonString;
import epc.therest.jsonparser.JsonValueType;

public class JavaxJsonString implements JsonString {

	private javax.json.JsonString javaxJsonString;

	public JavaxJsonString(javax.json.JsonString javaxJsonString) {
		this.javaxJsonString = javaxJsonString;
	}

	@Override
	public JsonValueType getValueType() {
		return JsonValueType.STRING;
	}

	@Override
	public String getStringValue() {
		return javaxJsonString.getString();
	}

}
