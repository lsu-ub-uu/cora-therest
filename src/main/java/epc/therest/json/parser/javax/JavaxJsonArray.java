package epc.therest.json.parser.javax;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import epc.therest.json.parser.JsonArray;
import epc.therest.json.parser.JsonValue;
import epc.therest.json.parser.JsonValueType;

public final class JavaxJsonArray implements JsonArray {

	private javax.json.JsonArray javaxJsonArray;
	private JavaxJsonClassFactoryImp factory;

	public static JavaxJsonArray usingJavaxJsonArray(JavaxJsonClassFactoryImp factory,
			javax.json.JsonArray javaxJsonArray) {
		return new JavaxJsonArray(factory, javaxJsonArray);
	}

	private JavaxJsonArray(JavaxJsonClassFactoryImp factory, javax.json.JsonArray javaxJsonArray) {
		this.factory = factory;
		this.javaxJsonArray = javaxJsonArray;
	}

	@Override
	public JsonValueType getValueType() {
		return JsonValueType.ARRAY;
	}

	@Override
	public JsonValue get(int index) {
		javax.json.JsonValue jsonValue = javaxJsonArray.get(index);
		return factory.createFromJavaxJsonValue(jsonValue);
	}

	@Override
	public Iterator<JsonValue> iterator() {
		List<JsonValue> list = new ArrayList<>();
		for (javax.json.JsonValue jsonValue : javaxJsonArray) {
			list.add(factory.createFromJavaxJsonValue(jsonValue));
		}
		return list.iterator();
	}
}
