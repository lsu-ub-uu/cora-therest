package epc.therest.jsonparser.javax;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.json.JsonValue.ValueType;

import epc.therest.jsonparser.JsonArray;
import epc.therest.jsonparser.JsonValue;
import epc.therest.jsonparser.JsonValueType;

public class JavaxJsonArray implements JsonArray {

	private javax.json.JsonArray javaxJsonArray;

	public JavaxJsonArray(javax.json.JsonArray javaxJsonArray) {
		this.javaxJsonArray = javaxJsonArray;

	}

	@Override
	public JsonValueType getValueType() {
		return JsonValueType.ARRAY;
	}

	@Override
	public JsonValue get(int index) {
		javax.json.JsonValue jsonValue = javaxJsonArray.get(index);
		return changeToRightType(jsonValue);
	}

	private JsonValue changeToRightType(javax.json.JsonValue jsonValue) {
		if (ValueType.STRING.equals(jsonValue.getValueType())) {
			return new JavaxJsonString((javax.json.JsonString) jsonValue);
		}
		if (ValueType.ARRAY.equals(jsonValue.getValueType())) {
			return new JavaxJsonArray((javax.json.JsonArray) jsonValue);
		}
		return new JavaxJsonObject((javax.json.JsonObject) jsonValue);
	}

	@Override
	public Iterator<JsonValue> iterator() {
		List<JsonValue> list = new ArrayList<>();
		for (javax.json.JsonValue jsonValue : javaxJsonArray) {
			list.add(changeToRightType(jsonValue));
		}
		return list.iterator();
	}
}
