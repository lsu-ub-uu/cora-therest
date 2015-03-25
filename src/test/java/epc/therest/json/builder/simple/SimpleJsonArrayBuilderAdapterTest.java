package epc.therest.json.builder.simple;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import epc.therest.json.builder.JsonArrayBuilder;
import epc.therest.json.builder.JsonObjectBuilder;
import epc.therest.json.parser.JsonArray;
import epc.therest.json.parser.JsonObject;
import epc.therest.json.parser.JsonString;

public class SimpleJsonArrayBuilderAdapterTest {
	@Test
	public void testAddStringValue() {
		JsonArrayBuilder jsonArrayBuilder = new SimpleJsonArrayBuilderAdapter();
		jsonArrayBuilder.addString("value");
		JsonArray jsonArray = jsonArrayBuilder.toJsonArray();
		assertEquals(((JsonString) jsonArray.getValue(0)).getStringValue(), "value");
	}

	@Test
	public void testAddArrayBuilderValue() {
		JsonArrayBuilder jsonArrayBuilder = new SimpleJsonArrayBuilderAdapter();

		JsonArrayBuilder jsonArrayBuilder2 = new SimpleJsonArrayBuilderAdapter();

		jsonArrayBuilder2.addString("value");
		jsonArrayBuilder.addJsonArrayBuilder(jsonArrayBuilder2);

		JsonArray jsonArray = jsonArrayBuilder.toJsonArray();
		assertEquals(
				((JsonString) ((JsonArray) jsonArray.getValue(0)).getValue(0)).getStringValue(),
				"value");

	}

	@Test
	public void testAddObjectBuilderValue() {
		JsonArrayBuilder jsonArrayBuilder = new SimpleJsonArrayBuilderAdapter();

		JsonObjectBuilder jsonObjectBuilder2 = new SimpleJsonObjectBuilderAdapter();

		jsonObjectBuilder2.addKeyString("id", "value");
		jsonArrayBuilder.addJsonObjectBuilder(jsonObjectBuilder2);

		JsonArray jsonArray = jsonArrayBuilder.toJsonArray();
		assertEquals(
				((JsonString) ((JsonObject) jsonArray.getValue(0)).getValue("id")).getStringValue(),
				"value");
	}
}
