package epc.therest.json.parser.org;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import org.testng.annotations.Test;

import epc.therest.json.parser.JsonArray;
import epc.therest.json.parser.JsonObject;
import epc.therest.json.parser.JsonParseException;
import epc.therest.json.parser.JsonString;
import epc.therest.json.parser.JsonValue;
import epc.therest.json.parser.JsonValueType;

public class OrgJsonObjectAdapterTest {
	@Test
	public void testUsingOrgJsonObject() {
		org.json.JSONObject orgJsonObject = new org.json.JSONObject("{\"id\":\"value\"}");
		JsonValue jsonValue = OrgJsonObjectAdapter.usingOrgJsonObject(orgJsonObject);
		assertTrue(jsonValue instanceof JsonObject);
	}

	@Test
	public void testGetValueType() {
		org.json.JSONObject orgJsonObject = new org.json.JSONObject("{\"id\":\"value\"}");
		JsonValue jsonValue = OrgJsonObjectAdapter.usingOrgJsonObject(orgJsonObject);
		assertEquals(jsonValue.getValueType(), JsonValueType.OBJECT);
	}

	@Test
	public void testGetValue() {
		JsonObject jsonObject = parseStringAsJsonObject("{\"id\":\"value\"}");
		JsonString value = (JsonString) jsonObject.getValue("id");
		assertEquals(value.getStringValue(), "value");
	}

	private JsonObject parseStringAsJsonObject(String json) {
		org.json.JSONObject orgJsonObject = new org.json.JSONObject(json);
		JsonObject jsonObject = OrgJsonObjectAdapter.usingOrgJsonObject(orgJsonObject);
		return jsonObject;
	}

	@Test(expectedExceptions = JsonParseException.class)
	public void testGetValueNotFound() {
		JsonObject jsonObject = parseStringAsJsonObject("{\"id\":\"value\"}");
		jsonObject.getValue("idNotFound");
	}

	@Test
	public void testGetValueAsJsonString() {
		JsonObject jsonObject = parseStringAsJsonObject("{\"id\":\"value\"}");
		JsonString value = jsonObject.getValueAsJsonString("id");
		assertEquals(value.getStringValue(), "value");
	}

	@Test(expectedExceptions = JsonParseException.class)
	public void testGetValueAsJsonStringNotFound() {
		JsonObject jsonObject = parseStringAsJsonObject("{\"id\":\"value\"}");
		jsonObject.getValueAsJsonString("idNotFound");
	}

	@Test(expectedExceptions = JsonParseException.class)
	public void testGetValueAsJsonStringNotAString() {
		JsonObject jsonObject = parseStringAsJsonObject("{\"id\":[\"value\"]}");
		jsonObject.getValueAsJsonString("id");
	}

	@Test
	public void testGetValueAsJsonObject() {
		JsonObject jsonObject = parseStringAsJsonObject("{\"id\":{\"id2\":\"value\"}}");
		JsonObject object = jsonObject.getValueAsJsonObject("id");
		assertTrue(object instanceof JsonObject);
	}

	@Test(expectedExceptions = JsonParseException.class)
	public void testGetValueAsJsonObjectNotFound() {
		JsonObject jsonObject = parseStringAsJsonObject("{\"id\":{\"id2\":\"value\"}}");
		JsonObject object = jsonObject.getValueAsJsonObject("idNotFound");
		assertTrue(object instanceof JsonObject);
	}

	@Test(expectedExceptions = JsonParseException.class)
	public void testGetValueAsJsonObjectNotAnObject() {
		JsonObject jsonObject = parseStringAsJsonObject("{\"id\":[\"id2\",\"value\"]}");
		JsonObject object = jsonObject.getValueAsJsonObject("id");
		assertTrue(object instanceof JsonObject);
	}

	@Test
	public void testGetValueAsJsonArray() {
		JsonObject jsonObject = parseStringAsJsonObject("{\"id\":[\"id2\",\"value\"]}");
		JsonArray array = jsonObject.getValueAsJsonArray("id");
		assertTrue(array instanceof JsonArray);
	}

	@Test(expectedExceptions = JsonParseException.class)
	public void testGetValueAsJsonArrayNotFound() {
		JsonObject jsonObject = parseStringAsJsonObject("{\"id\":[\"id2\",\"value\"]}");
		JsonArray array = jsonObject.getValueAsJsonArray("id2");
		assertTrue(array instanceof JsonArray);
	}

	@Test(expectedExceptions = JsonParseException.class)
	public void testGetValueAsJsonArrayNotAnArray() {
		JsonObject jsonObject = parseStringAsJsonObject("{\"id\":{\"id2\":\"value\"}}");
		JsonArray array = jsonObject.getValueAsJsonArray("id");
		assertTrue(array instanceof JsonArray);
	}

	@Test
	public void testContainsKey() {
		JsonObject jsonObject = parseStringAsJsonObject("{\"id\":\"value\"}");
		assertTrue(jsonObject.containsKey("id"));
	}

	@Test
	public void testContainsKeyNotFound() {
		JsonObject jsonObject = parseStringAsJsonObject("{\"id\":\"value\"}");
		assertFalse(jsonObject.containsKey("idNotFound"));
	}

	@Test
	public void testKeySet() {
		JsonObject jsonObject = parseStringAsJsonObject("{\"id\":\"value\"}");
		Set<String> keySet = jsonObject.keySet();
		String key = keySet.iterator().next();
		assertEquals(key, "id");
	}

	@Test
	public void testEntrySet() {
		JsonObject jsonObject = parseStringAsJsonObject("{\"id\":\"value\"}");
		Set<Entry<String, JsonValue>> entrySet = jsonObject.entrySet();
		Iterator<Entry<String, JsonValue>> iterator = entrySet.iterator();
		Entry<String, JsonValue> entry = iterator.next();
		JsonString jsonString = (JsonString) entry.getValue();
		String value = jsonString.getStringValue();
		assertEquals(value, "value");
	}

	@Test
	public void testSize() {
		JsonObject jsonObject = parseStringAsJsonObject("{\"id\":\"value\",\"id2\":\"value2\"}");
		assertEquals(jsonObject.size(), 2);
	}

	// TODO: add tests for stuff that does not work...
	// DO NOT IMPLEMENT toJsonString, move that to ObjectBuilder

}
