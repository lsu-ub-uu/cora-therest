package epc.therest.jsonparser.javax;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

import epc.therest.jsonparser.JsonArray;
import epc.therest.jsonparser.JsonObject;
import epc.therest.jsonparser.JsonParser;
import epc.therest.jsonparser.JsonString;
import epc.therest.jsonparser.JsonValue;

public class JavaxJsonArrayTest {
	@Test
	public void testGetValueString() {
		JsonParser jsonParser = new JavaxJsonParser();
		JsonArray jsonArray = jsonParser.parseStringAsArray("[\"id\",\"value\"]");
		JsonValue value = jsonArray.get(0);
		assertTrue(value instanceof JsonString);
	}

	@Test
	public void testGetValueArray() {
		JsonParser jsonParser = new JavaxJsonParser();
		JsonArray jsonArray = jsonParser.parseStringAsArray("[[\"id\",\"value\"]]");
		JsonValue value = jsonArray.get(0);
		assertTrue(value instanceof JsonArray);
	}

	@Test
	public void testGetValueObject() {
		JsonParser jsonParser = new JavaxJsonParser();
		JsonArray jsonArray = jsonParser.parseStringAsArray("[{\"id\":\"value\"}]");
		JsonValue value = jsonArray.get(0);
		assertTrue(value instanceof JsonObject);
	}

	@Test
	public void testIterator() {
		JsonParser jsonParser = new JavaxJsonParser();
		JsonArray jsonArray = jsonParser.parseStringAsArray("[\"id\",\"value\"]");

		JsonValue afterIterator = null;
		for (JsonValue j : jsonArray) {
			afterIterator = j;
		}
		assertEquals(((JsonString) afterIterator).getStringValue(), "value");
	}

}
