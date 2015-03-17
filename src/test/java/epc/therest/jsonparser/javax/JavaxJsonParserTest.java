package epc.therest.jsonparser.javax;

import static org.testng.Assert.assertTrue;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import epc.therest.jsonparser.JsonArray;
import epc.therest.jsonparser.JsonObject;
import epc.therest.jsonparser.JsonParseException;
import epc.therest.jsonparser.JsonParser;
import epc.therest.jsonparser.JsonValue;
import epc.therest.jsonparser.JsonValueType;

public class JavaxJsonParserTest {
	private JsonParser jsonParser;

	@BeforeMethod
	public void beforeMethod() {
		jsonParser = new JavaxJsonParser();
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
		JsonParser jsonParser = new JavaxJsonParser();
		JsonValue jsonValue = jsonParser.parseString("{\"id\":\"value\"}");
		assertTrue(jsonValue instanceof JsonObject);
	}

	@Test
	public void testObjectGetValueType() {
		JsonParser jsonParser = new JavaxJsonParser();
		JsonValue jsonValue = jsonParser.parseString("{\"id\":\"value\"}");
		assertTrue(JsonValueType.OBJECT.equals(jsonValue.getValueType()));
	}

	@Test
	public void testAsObjectGetValueType() {
		JsonParser jsonParser = new JavaxJsonParser();
		JsonValue jsonValue = jsonParser.parseStringAsObject("{\"id\":\"value\"}");
		assertTrue(JsonValueType.OBJECT.equals(jsonValue.getValueType()));
	}

	@Test(expectedExceptions = JsonParseException.class)
	public void testWrongAsObjectGetValueType() {
		JsonParser jsonParser = new JavaxJsonParser();
		jsonParser.parseStringAsObject("[\"id\",\"value\"]");
	}

	@Test
	public void testArrayCreate() {
		JsonParser jsonParser = new JavaxJsonParser();
		JsonValue jsonValue = jsonParser.parseString("[\"id\",\"value\"]");
		assertTrue(jsonValue instanceof JsonArray);
	}

	@Test
	public void testArrayGetValueType() {
		JsonParser jsonParser = new JavaxJsonParser();
		JsonValue jsonValue = jsonParser.parseString("[\"id\",\"value\"]");
		assertTrue(JsonValueType.ARRAY.equals(jsonValue.getValueType()));
	}

	@Test
	public void testAsArrayGetValueType() {
		JsonParser jsonParser = new JavaxJsonParser();
		JsonValue jsonValue = jsonParser.parseStringAsArray("[\"id\",\"value\"]");
		assertTrue(JsonValueType.ARRAY.equals(jsonValue.getValueType()));
	}

	@Test(expectedExceptions = JsonParseException.class)
	public void testWrongAsArrayGetValueType() {
		JsonParser jsonParser = new JavaxJsonParser();
		JsonValue jsonValue = jsonParser.parseStringAsArray("{\"id\":\"value\"}");
		assertTrue(JsonValueType.ARRAY.equals(jsonValue.getValueType()));
	}

}
