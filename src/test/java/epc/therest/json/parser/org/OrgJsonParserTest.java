package epc.therest.json.parser.org;

import static org.testng.Assert.assertTrue;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import epc.therest.json.parser.JsonArray;
import epc.therest.json.parser.JsonObject;
import epc.therest.json.parser.JsonParseException;
import epc.therest.json.parser.JsonParser;
import epc.therest.json.parser.JsonValue;
import epc.therest.json.parser.JsonValueType;

public class OrgJsonParserTest {
	private JsonParser jsonParser;

	@BeforeMethod
	public void beforeMethod() {
		jsonParser = new OrgJsonParser();
	}

	@Test(expectedExceptions = JsonParseException.class)
	public void testFactorOnJsonStringNullJson() {
		String json = null;
		jsonParser.parseString(json);
	}

	@Test(expectedExceptions = JsonParseException.class)
	public void testFactorOnJsonStringEmptyJson() {
		String json = "";
		jsonParser.parseString(json);
	}

	@Test(expectedExceptions = JsonParseException.class)
	public void testFactorOnJsonStringBrokenJson() {
		String json = "{";
		jsonParser.parseString(json);
	}

	@Test
	public void testObjectCreate() {
		JsonValue jsonValue = jsonParser.parseString("{\"id\":\"value\"}");
		assertTrue(jsonValue instanceof JsonObject);
	}

	@Test
	public void testObjectGetValueType() {
		JsonValue jsonValue = jsonParser.parseString("{\"id\":\"value\"}");
		assertTrue(JsonValueType.OBJECT.equals(jsonValue.getValueType()));
	}

	@Test
	public void testAsObjectGetValueType() {
		JsonValue jsonValue = jsonParser.parseStringAsObject("{\"id\":\"value\"}");
		assertTrue(JsonValueType.OBJECT.equals(jsonValue.getValueType()));
	}

	@Test(expectedExceptions = JsonParseException.class)
	public void testWrongAsObjectGetValueType() {
		jsonParser.parseStringAsObject("[\"id\",\"value\"]");
	}

	@Test
	public void testArrayCreate() {
		JsonValue jsonValue = jsonParser.parseString("[\"id\",\"value\"]");
		assertTrue(jsonValue instanceof JsonArray);
	}

	@Test
	public void testArrayGetValueType() {
		JsonValue jsonValue = jsonParser.parseString("[\"id\",\"value\"]");
		assertTrue(JsonValueType.ARRAY.equals(jsonValue.getValueType()));
	}

	@Test
	public void testAsArrayGetValueType() {
		JsonValue jsonValue = jsonParser.parseStringAsArray("[\"id\",\"value\"]");
		assertTrue(JsonValueType.ARRAY.equals(jsonValue.getValueType()));
	}

	@Test(expectedExceptions = JsonParseException.class)
	public void testWrongAsArrayGetValueType() {
		JsonValue jsonValue = jsonParser.parseStringAsArray("{\"id\":\"value\"}");
		assertTrue(JsonValueType.ARRAY.equals(jsonValue.getValueType()));
	}
}
