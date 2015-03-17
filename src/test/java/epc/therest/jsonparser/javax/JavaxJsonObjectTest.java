package epc.therest.jsonparser.javax;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.Map.Entry;
import java.util.Set;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import epc.therest.jsonparser.JsonArray;
import epc.therest.jsonparser.JsonObject;
import epc.therest.jsonparser.JsonParseException;
import epc.therest.jsonparser.JsonParser;
import epc.therest.jsonparser.JsonString;
import epc.therest.jsonparser.JsonValue;

public class JavaxJsonObjectTest {
	private JsonParser jsonParser;

	@BeforeMethod
	public void beforeMethod() {
		JavaxJsonClassFactory javaxJsonClassFactory = new JavaxJsonClassFactoryImp();
		jsonParser = new JavaxJsonParser(javaxJsonClassFactory);
	}

	@Test
	public void testContainsKey() {
		JsonObject jsonObject = jsonParser.parseStringAsObject("{\"id\":\"value\"}");
		assertTrue(jsonObject.containsKey("id"));
	}

	@Test
	public void testContainsKeyNotFound() {
		JsonObject jsonObject = jsonParser.parseStringAsObject("{\"id\":\"value\"}");
		assertFalse(jsonObject.containsKey("id2"));
	}

	@Test
	public void testKeySet() {
		JsonObject jsonObject = jsonParser.parseStringAsObject("{\"id\":\"value\"}");
		Set<String> keys = jsonObject.keySet();
		assertEquals(keys.iterator().next(), "id");
	}

	@Test
	public void testGetValueString() {
		JsonObject jsonObject = jsonParser.parseStringAsObject("{\"id\":\"value\"}");
		JsonValue value = jsonObject.getValue("id");
		assertTrue(value instanceof JsonString);
	}

	@Test(expectedExceptions = JsonParseException.class)
	public void testGetValueStringNotFound() {
		JsonObject jsonObject = jsonParser.parseStringAsObject("{\"id\":\"value\"}");
		jsonObject.getValue("id2");
	}

	@Test
	public void testGetValueArray() {
		JsonObject jsonObject = jsonParser.parseStringAsObject("{\"id\":[\"value\"]}");
		JsonValue value = jsonObject.getValue("id");
		assertTrue(value instanceof JsonArray);
	}

	@Test
	public void testGetValueObject() {
		JsonObject jsonObject = jsonParser.parseStringAsObject("{\"id\":{\"id2\":\"value\"}}");
		JsonValue value = jsonObject.getValue("id");
		assertTrue(value instanceof JsonObject);
	}

	@Test
	public void testEntrySet() {
		JsonObject jsonObject = jsonParser.parseStringAsObject("{\"id\":{\"id2\":\"value\"}}");
		Set<Entry<String, JsonValue>> value = jsonObject.entrySet();
		Entry<String, JsonValue> jsonValue = value.iterator().next();
		assertTrue(jsonValue.getValue() instanceof JsonObject);
	}

	@Test
	public void testSize() {
		JsonObject jsonObject = jsonParser.parseStringAsObject("{\"id\":{\"id2\":\"value\"}}");
		assertEquals(jsonObject.size(), 1);
	}

	@Test
	public void testGetObject() {
		JsonObject jsonObject = jsonParser.parseStringAsObject("{\"id\":{\"id2\":\"value\"}}");
		JsonObject object = jsonObject.getObject("id");
		assertTrue(object instanceof JsonObject);
	}

	@Test(expectedExceptions = JsonParseException.class)
	public void testGetObjectNotAnObject() {
		JsonObject jsonObject = jsonParser.parseStringAsObject("{\"id\":\"value\"}");
		JsonObject object = jsonObject.getObject("id");
		assertTrue(object instanceof JsonObject);
	}

	@Test
	public void testGetArray() {
		JsonObject jsonObject = jsonParser.parseStringAsObject("{\"id\":[\"value\"]}");
		JsonArray array = jsonObject.getArray("id");
		assertTrue(array instanceof JsonArray);
	}

	@Test(expectedExceptions = JsonParseException.class)
	public void testGetArrayNotAnArray() {
		JsonObject jsonObject = jsonParser.parseStringAsObject("{\"id\":{\"id\":\"value\"}}");
		JsonArray array = jsonObject.getArray("id");
		assertTrue(array instanceof JsonArray);
	}
}
