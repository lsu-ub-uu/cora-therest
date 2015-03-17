package epc.therest.jsonparser.javax;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import epc.therest.jsonparser.JsonArray;
import epc.therest.jsonparser.JsonParser;
import epc.therest.jsonparser.JsonString;

public class JavaxJsonStringTest {
	@Test
	public void testGetValueString() {
		JsonParser jsonParser = new JavaxJsonParser();
		JsonArray jsonArray = jsonParser.parseStringAsArray("[\"id\",\"value\"]");
		JsonString value = (JsonString) jsonArray.get(0);
		assertEquals(value.getStringValue(), "id");
	}
}
