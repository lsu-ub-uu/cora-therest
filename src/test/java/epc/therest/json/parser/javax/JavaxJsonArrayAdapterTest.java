package epc.therest.json.parser.javax;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import epc.therest.json.parser.JsonArray;
import epc.therest.json.parser.JsonObject;
import epc.therest.json.parser.JsonParser;
import epc.therest.json.parser.JsonString;
import epc.therest.json.parser.JsonValue;

public class JavaxJsonArrayAdapterTest {
	private JsonParser jsonParser;

	@BeforeMethod
	public void beforeMethod() {
		jsonParser = new JavaxJsonParser();
	}

	@Test
	public void testGetValueString() {
		JsonArray jsonArray = jsonParser.parseStringAsArray("[\"id\",\"value\"]");
		JsonValue value = jsonArray.getValue(0);
		assertTrue(value instanceof JsonString);
	}

	@Test
	public void testGetValueArray() {
		JsonArray jsonArray = jsonParser.parseStringAsArray("[[\"id\",\"value\"]]");
		JsonValue value = jsonArray.getValue(0);
		assertTrue(value instanceof JsonArray);
	}

	@Test
	public void testGetValueObject() {
		JsonArray jsonArray = jsonParser.parseStringAsArray("[{\"id\":\"value\"}]");
		JsonValue value = jsonArray.getValue(0);
		assertTrue(value instanceof JsonObject);
	}

	@Test
	public void testIterator() {
		JsonArray jsonArray = jsonParser.parseStringAsArray("[\"id\",\"value\"]");

		JsonValue afterIterator = null;
		for (JsonValue j : jsonArray) {
			afterIterator = j;
		}
		assertEquals(((JsonString) afterIterator).getStringValue(), "value");
	}

}
