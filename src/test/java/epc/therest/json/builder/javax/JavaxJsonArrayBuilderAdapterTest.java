package epc.therest.json.builder.javax;

import static org.testng.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import javax.json.Json;

import org.testng.annotations.Test;

import epc.therest.json.builder.JsonArrayBuilder;
import epc.therest.json.builder.JsonObjectBuilder;
import epc.therest.json.parser.JsonArray;
import epc.therest.json.parser.JsonObject;
import epc.therest.json.parser.JsonString;

public class JavaxJsonArrayBuilderAdapterTest {
	@Test
	public void testAddStringValue() {
		Map<String, Object> config = new HashMap<>();
		javax.json.JsonBuilderFactory jsonBuilderFactory = Json.createBuilderFactory(config);
		javax.json.JsonArrayBuilder javaxJsonArrayBuilder = jsonBuilderFactory.createArrayBuilder();

		JsonArrayBuilder jsonArrayBuilder = new JavaxJsonArrayBuilderAdapter(javaxJsonArrayBuilder);
		jsonArrayBuilder.addString("value");

		JsonArray jsonArray = jsonArrayBuilder.toJsonArray();
		assertEquals(((JsonString) jsonArray.getValue(0)).getStringValue(), "value");
	}

	@Test
	public void testAddArrayBuilderValue() {
		Map<String, Object> config = new HashMap<>();
		javax.json.JsonBuilderFactory jsonBuilderFactory = Json.createBuilderFactory(config);
		javax.json.JsonArrayBuilder javaxJsonArrayBuilder = jsonBuilderFactory.createArrayBuilder();
		javax.json.JsonArrayBuilder javaxJsonArrayBuilder2 = jsonBuilderFactory
				.createArrayBuilder();

		JsonArrayBuilder jsonArrayBuilder = new JavaxJsonArrayBuilderAdapter(javaxJsonArrayBuilder);
		JsonArrayBuilder jsonArrayBuilder2 = new JavaxJsonArrayBuilderAdapter(
				javaxJsonArrayBuilder2);

		jsonArrayBuilder2.addString("value");
		jsonArrayBuilder.addJsonArrayBuilder(jsonArrayBuilder2);

		JsonArray jsonArray = jsonArrayBuilder.toJsonArray();
		assertEquals(
				((JsonString) ((JsonArray) jsonArray.getValue(0)).getValue(0)).getStringValue(),
				"value");

	}

	@Test
	public void testAddObjectBuilderValue() {
		Map<String, Object> config = new HashMap<>();
		javax.json.JsonBuilderFactory jsonBuilderFactory = Json.createBuilderFactory(config);
		javax.json.JsonArrayBuilder javaxJsonArrayBuilder = jsonBuilderFactory.createArrayBuilder();
		javax.json.JsonObjectBuilder javaxJsonObjectBuilder = jsonBuilderFactory
				.createObjectBuilder();

		JsonArrayBuilder jsonArrayBuilder = new JavaxJsonArrayBuilderAdapter(javaxJsonArrayBuilder);
		JsonObjectBuilder jsonObjectBuilder = new JavaxJsonObjectBuilderAdapter(
				javaxJsonObjectBuilder);

		jsonObjectBuilder.addKeyString("id", "value");
		jsonArrayBuilder.addJsonObjectBuilder(jsonObjectBuilder);

		JsonArray jsonArray = jsonArrayBuilder.toJsonArray();
		assertEquals(
				((JsonString) ((JsonObject) jsonArray.getValue(0)).getValue("id")).getStringValue(),
				"value");

	}
}
