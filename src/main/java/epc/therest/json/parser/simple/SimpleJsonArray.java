package epc.therest.json.parser.simple;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.simple.JSONArray;

import epc.therest.json.parser.JsonArray;
import epc.therest.json.parser.JsonValue;
import epc.therest.json.parser.JsonValueType;

public class SimpleJsonArray implements JsonArray {

	private JSONArray simpleJsonArray;

	public SimpleJsonArray(JSONArray simpleJsonArray) {
		this.simpleJsonArray = simpleJsonArray;
	}

	@Override
	public JsonValueType getValueType() {
		return JsonValueType.ARRAY;
	}

	@Override
	public Iterator<JsonValue> iterator() {
		List<JsonValue> list = new ArrayList<>();
		for (Object jsonValue : simpleJsonArray) {
			list.add(SimpleJsonClassFactory.createFromSimpleJsonValue(jsonValue));
		}
		return list.iterator();
	}

	@Override
	public JsonValue get(int index) {
		Object value = simpleJsonArray.get(index);
		return SimpleJsonClassFactory.createFromSimpleJsonValue(value);
	}

}
