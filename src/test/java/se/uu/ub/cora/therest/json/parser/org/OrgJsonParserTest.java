package se.uu.ub.cora.therest.json.parser.org;

import static org.testng.Assert.assertTrue;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.therest.json.parser.JsonArray;
import se.uu.ub.cora.therest.json.parser.JsonObject;
import se.uu.ub.cora.therest.json.parser.JsonParseException;
import se.uu.ub.cora.therest.json.parser.JsonParser;
import se.uu.ub.cora.therest.json.parser.JsonValue;
import se.uu.ub.cora.therest.json.parser.JsonValueType;
import se.uu.ub.cora.therest.json.parser.org.OrgJsonParser;

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
	public void testObjectCreateWithSpaceInValue() {
		JsonValue jsonValue = jsonParser.parseString("{\"id\":\"This is a value with space\"}");
		assertTrue(jsonValue instanceof JsonObject);
	}

	@Test
	public void testObjectGetValueType() {
		JsonValue jsonValue = jsonParser.parseString("{\"id\":\"value\"}");
		assertTrue(JsonValueType.OBJECT.equals(jsonValue.getValueType()));
	}

	@Test(expectedExceptions = JsonParseException.class)
	public void testParseWrongJsonExtraKeyValuePairTopLevel() {
		String json = "{\"id\":{}},{\"id2\":\"value2\"}";
		jsonParser.parseString(json);
	}

	@Test(expectedExceptions = JsonParseException.class)
	public void testParseWrongJsonDuplicateKeyValuePair() {
		String json = "{\"id\":{},\"id\":\"value2\"}";
		jsonParser.parseString(json);
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
