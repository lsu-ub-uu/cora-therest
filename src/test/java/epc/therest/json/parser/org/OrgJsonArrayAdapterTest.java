package epc.therest.json.parser.org;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

import epc.therest.json.parser.JsonArray;
import epc.therest.json.parser.JsonObject;
import epc.therest.json.parser.JsonParseException;
import epc.therest.json.parser.JsonString;
import epc.therest.json.parser.JsonValue;
import epc.therest.json.parser.JsonValueType;

public class OrgJsonArrayAdapterTest {
	@Test
	public void testUsingOrgJsonArray() {
		org.json.JSONArray orgJsonArray = new org.json.JSONArray("[\"value1\",\"value2\"]");
		JsonValue jsonValue = OrgJsonArrayAdapter.usingOrgJsonArray(orgJsonArray);
		assertTrue(jsonValue instanceof JsonArray);
	}

	@Test
	public void testGetValueType() {
		org.json.JSONArray orgJsonArray = new org.json.JSONArray("[\"value1\",\"value2\"]");
		JsonValue jsonValue = OrgJsonArrayAdapter.usingOrgJsonArray(orgJsonArray);
		assertEquals(jsonValue.getValueType(), JsonValueType.ARRAY);
	}

	@Test
	public void testGetValue() {
		JsonArray jsonArray = parseStringAsJsonArray("[\"value\",\"value2\"]");
		JsonString value = (JsonString) jsonArray.getValue(0);
		assertEquals(value.getStringValue(), "value");
	}

	private JsonArray parseStringAsJsonArray(String json) {
		org.json.JSONArray orgJsonArray = new org.json.JSONArray(json);
		JsonArray jsonArray = OrgJsonArrayAdapter.usingOrgJsonArray(orgJsonArray);
		return jsonArray;
	}

	@Test(expectedExceptions = JsonParseException.class)
	public void testGetValueNotFound() {
		JsonArray jsonArray = parseStringAsJsonArray("[\"value\",\"value2\"]");
		jsonArray.getValue(10);
	}

	@Test
	public void testGetValueAsJsonString() {
		JsonArray jsonArray = parseStringAsJsonArray("[\"value\",\"value2\"]");
		assertTrue(jsonArray.getValueAsJsonString(0) instanceof JsonString);
	}

	@Test(expectedExceptions = JsonParseException.class)
	public void testGetValueAsJsonStringNotFound() {
		JsonArray jsonArray = parseStringAsJsonArray("[\"value\",\"value2\"]");
		jsonArray.getValueAsJsonString(10);
	}

	@Test(expectedExceptions = JsonParseException.class)
	public void testGetValueAsJsonStringNotAString() {
		JsonArray jsonArray = parseStringAsJsonArray("[[\"value\",\"value2\"]]");
		jsonArray.getValueAsJsonString(0);
	}

	@Test
	public void testGetValueAsJsonObject() {
		JsonArray jsonArray = parseStringAsJsonArray("[{\"value\":\"value2\"}]");
		assertTrue(jsonArray.getValueAsJsonObject(0) instanceof JsonObject);
	}

	@Test(expectedExceptions = JsonParseException.class)
	public void testGetValueAsJsonObjectNotFound() {
		JsonArray jsonArray = parseStringAsJsonArray("[{\"value\":\"value2\"}]");
		jsonArray.getValueAsJsonObject(10);
	}

	@Test(expectedExceptions = JsonParseException.class)
	public void testGetValueAsJsonObjectNotAnObject() {
		JsonArray jsonArray = parseStringAsJsonArray("[\"value\",\"value2\"]");
		jsonArray.getValueAsJsonObject(0);
	}

	@Test
	public void testGetValueAsJsonArray() {
		JsonArray jsonArray = parseStringAsJsonArray("[[\"value\",\"value2\"]]");
		assertTrue(jsonArray.getValueAsJsonArray(0) instanceof JsonArray);
	}

	@Test(expectedExceptions = JsonParseException.class)
	public void testGetValueAsJsonArrayNotFound() {
		JsonArray jsonArray = parseStringAsJsonArray("[[\"value\",\"value2\"]]");
		jsonArray.getValueAsJsonArray(10);
	}

	@Test(expectedExceptions = JsonParseException.class)
	public void testGetValueAsJsonArrayNotAnArray() {
		JsonArray jsonArray = parseStringAsJsonArray("[{\"value\":\"value2\"}]");
		jsonArray.getValueAsJsonArray(0);
	}

	@Test
	public void testIterator() {
		JsonArray jsonArray = parseStringAsJsonArray("[\"value\",\"value2\"]");
		int counter = 0;
		JsonValue afterIterator = null;
		for (JsonValue jsonValue : jsonArray) {
			afterIterator = jsonValue;
			counter++;
		}
		assertEquals(((JsonString) afterIterator).getStringValue(), "value2");
		assertEquals(counter, 2);
	}
}
