package epc.therest.json.parser.org;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;

import epc.therest.json.parser.JsonArray;
import epc.therest.json.parser.JsonObject;
import epc.therest.json.parser.JsonParseException;
import epc.therest.json.parser.JsonString;
import epc.therest.json.parser.JsonValue;
import epc.therest.json.parser.JsonValueType;

public class OrgJsonArrayAdapter implements JsonArray {

	public static OrgJsonArrayAdapter usingOrgJsonArray(JSONArray orgJsonArray) {
		return new OrgJsonArrayAdapter(orgJsonArray);
	}

	private JSONArray orgJsonArray;

	private OrgJsonArrayAdapter(JSONArray orgJsonArray) {
		this.orgJsonArray = orgJsonArray;
	}

	@Override
	public JsonValueType getValueType() {
		return JsonValueType.ARRAY;
	}

	@Override
	public JsonValue getValue(int index) {
		try {
			return tryGetValue(index);
		} catch (Exception e) {
			throw new JsonParseException("Json array does not contain requested index", e);
		}
	}

	private JsonValue tryGetValue(int index) {
		Object object = orgJsonArray.get(index);
		return OrgJsonValueFactory.createFromOrgJsonObject(object);
	}

	@Override
	public JsonString getValueAsJsonString(int index) {
		JsonValue jsonValue = getValue(index);
		if (JsonValueType.STRING.equals(jsonValue.getValueType())) {
			return (JsonString) getValue(index);
		}
		throw new JsonParseException("Not a string");
	}

	@Override
	public JsonObject getValueAsJsonObject(int index) {
		JsonValue jsonValue = getValue(index);
		if (JsonValueType.OBJECT.equals(jsonValue.getValueType())) {
			return (JsonObject) getValue(index);
		}
		throw new JsonParseException("Not an object");
	}

	@Override
	public JsonArray getValueAsJsonArray(int index) {
		JsonValue jsonValue = getValue(index);
		if (JsonValueType.ARRAY.equals(jsonValue.getValueType())) {
			return (JsonArray) getValue(index);
		}
		throw new JsonParseException("Not an object");
	}

	@Override
	public Iterator<JsonValue> iterator() {
		List<JsonValue> list = new ArrayList<>();
		for (int i = 0; i < orgJsonArray.length(); i++) {
			list.add(OrgJsonValueFactory.createFromOrgJsonObject(orgJsonArray.get(i)));
		}
		return list.iterator();
	}

}
