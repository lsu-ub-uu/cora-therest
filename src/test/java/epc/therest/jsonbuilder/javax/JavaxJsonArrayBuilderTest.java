package epc.therest.jsonbuilder.javax;

import static org.testng.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import javax.json.Json;

import org.testng.annotations.Test;

import epc.therest.jsonbuilder.JsonArrayBuilder;
import epc.therest.jsonbuilder.JsonObjectBuilder;
import epc.therest.jsonparser.JsonArray;
import epc.therest.jsonparser.JsonObject;
import epc.therest.jsonparser.JsonString;

public class JavaxJsonArrayBuilderTest {
	@Test
	public void testAddStringValue() {
		Map<String, Object> config = new HashMap<>();
		javax.json.JsonBuilderFactory jsonBuilderFactory = Json.createBuilderFactory(config);
		javax.json.JsonArrayBuilder javaxJsonArrayBuilder = jsonBuilderFactory.createArrayBuilder();

		JsonArrayBuilder jsonArrayBuilder = new JavaxJsonArrayBuilder(javaxJsonArrayBuilder);
		jsonArrayBuilder.add("value");

		JsonArray jsonArray = jsonArrayBuilder.build();
		assertEquals(((JsonString) jsonArray.get(0)).getStringValue(), "value");
	}

	@Test
	public void testAddArrayBuilderValue() {
		Map<String, Object> config = new HashMap<>();
		javax.json.JsonBuilderFactory jsonBuilderFactory = Json.createBuilderFactory(config);
		javax.json.JsonArrayBuilder javaxJsonArrayBuilder = jsonBuilderFactory.createArrayBuilder();
		javax.json.JsonArrayBuilder javaxJsonArrayBuilder2 = jsonBuilderFactory
				.createArrayBuilder();

		JsonArrayBuilder jsonArrayBuilder = new JavaxJsonArrayBuilder(javaxJsonArrayBuilder);
		JsonArrayBuilder jsonArrayBuilder2 = new JavaxJsonArrayBuilder(javaxJsonArrayBuilder2);

		jsonArrayBuilder2.add("value");
		jsonArrayBuilder.add(jsonArrayBuilder2);

		JsonArray jsonArray = jsonArrayBuilder.build();
		assertEquals(((JsonString) ((JsonArray) jsonArray.get(0)).get(0)).getStringValue(), "value");

	}

	@Test
	public void testAddObjectBuilderValue() {
		Map<String, Object> config = new HashMap<>();
		javax.json.JsonBuilderFactory jsonBuilderFactory = Json.createBuilderFactory(config);
		javax.json.JsonArrayBuilder javaxJsonArrayBuilder = jsonBuilderFactory.createArrayBuilder();
		javax.json.JsonObjectBuilder javaxJsonObjectBuilder = jsonBuilderFactory
				.createObjectBuilder();

		JsonArrayBuilder jsonArrayBuilder = new JavaxJsonArrayBuilder(javaxJsonArrayBuilder);
		JsonObjectBuilder jsonObjectBuilder = new JavaxJsonObjectBuilder(javaxJsonObjectBuilder);

		jsonObjectBuilder.add("id", "value");
		jsonArrayBuilder.add(jsonObjectBuilder);

		JsonArray jsonArray = jsonArrayBuilder.build();
		assertEquals(
				((JsonString) ((JsonObject) jsonArray.get(0)).getValue("id")).getStringValue(),
				"value");

	}
}
