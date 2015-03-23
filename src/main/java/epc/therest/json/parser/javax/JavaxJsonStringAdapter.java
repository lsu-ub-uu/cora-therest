package epc.therest.json.parser.javax;

import epc.therest.json.parser.JsonString;
import epc.therest.json.parser.JsonValueType;

public final class JavaxJsonStringAdapter implements JsonString {

	private javax.json.JsonString javaxJsonString;

	public static JavaxJsonStringAdapter usingJavaxJsonStringAdapter(javax.json.JsonString javaxJsonString) {
		return new JavaxJsonStringAdapter(javaxJsonString);
	}

	private JavaxJsonStringAdapter(javax.json.JsonString javaxJsonString) {
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
