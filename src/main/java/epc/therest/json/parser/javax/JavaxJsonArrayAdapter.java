package epc.therest.json.parser.javax;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import epc.therest.json.parser.JsonArray;
import epc.therest.json.parser.JsonObject;
import epc.therest.json.parser.JsonString;
import epc.therest.json.parser.JsonValue;
import epc.therest.json.parser.JsonValueType;

public final class JavaxJsonArrayAdapter implements JsonArray {

	private javax.json.JsonArray javaxJsonArray;

	public static JavaxJsonArrayAdapter usingJavaxJsonArrayAdapter(
			javax.json.JsonArray javaxJsonArray) {
		return new JavaxJsonArrayAdapter(javaxJsonArray);
	}

	private JavaxJsonArrayAdapter(javax.json.JsonArray javaxJsonArray) {
		this.javaxJsonArray = javaxJsonArray;
	}

	@Override
	public JsonValueType getValueType() {
		return JsonValueType.ARRAY;
	}

	@Override
	public JsonValue getValue(int index) {
		javax.json.JsonValue jsonValue = javaxJsonArray.get(index);
		return JavaxJsonValueFactory.createFromJavaxJsonValue(jsonValue);
	}

	@Override
	public Iterator<JsonValue> iterator() {
		List<JsonValue> list = new ArrayList<>();
		for (javax.json.JsonValue jsonValue : javaxJsonArray) {
			list.add(JavaxJsonValueFactory.createFromJavaxJsonValue(jsonValue));
		}
		return list.iterator();
	}

	@Override
	public JsonString getValueAsJsonString(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JsonObject getValueAsJsonObject(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JsonArray getValueAsJsonArray(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String toJsonFormattedString() {
		// TODO Auto-generated method stub
		return null;
	}
}