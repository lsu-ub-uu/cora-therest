package epc.therest.json.parser.javax;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import epc.therest.json.parser.JsonArray;
import epc.therest.json.parser.JsonParser;
import epc.therest.json.parser.JsonString;

public class JavaxJsonStringAdapterTest {
	private JsonParser jsonParser;

	@BeforeMethod
	public void beforeMethod() {
		jsonParser = new JavaxJsonParser();
	}

	@Test
	public void testGetValueString() {
		JsonArray jsonArray = jsonParser.parseStringAsArray("[\"id\",\"value\"]");
		JsonString value = (JsonString) jsonArray.get(0);
		assertEquals(value.getStringValue(), "id");
	}
}
