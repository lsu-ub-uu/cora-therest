/*
 * Copyright 2015 Uppsala University Library
 *
 * This file is part of Cora.
 *
 *     Cora is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Cora is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Cora.  If not, see <http://www.gnu.org/licenses/>.
 */

package se.uu.ub.cora.therest.json.parser.org;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import org.testng.annotations.Test;
import se.uu.ub.cora.therest.json.parser.JsonArray;
import se.uu.ub.cora.therest.json.parser.JsonObject;
import se.uu.ub.cora.therest.json.parser.JsonParseException;
import se.uu.ub.cora.therest.json.parser.JsonString;
import se.uu.ub.cora.therest.json.parser.JsonValue;
import se.uu.ub.cora.therest.json.parser.JsonValueType;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

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
}
