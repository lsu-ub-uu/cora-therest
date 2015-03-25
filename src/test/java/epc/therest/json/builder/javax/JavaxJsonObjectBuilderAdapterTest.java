package epc.therest.json.builder.javax;

import static org.testng.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonBuilderFactory;

import org.testng.annotations.Test;

import epc.therest.json.builder.JsonArrayBuilder;
import epc.therest.json.builder.JsonObjectBuilder;
import epc.therest.json.parser.JsonArray;
import epc.therest.json.parser.JsonObject;
import epc.therest.json.parser.JsonString;

public class JavaxJsonObjectBuilderAdapterTest {
	@Test
	public void testJsonObjectBuilderAddKeyValue() {
		Map<String, Object> config = new HashMap<>();
		JsonBuilderFactory jsonBuilderFactory = Json.createBuilderFactory(config);
		javax.json.JsonObjectBuilder javaxJsonObjectBuilder = jsonBuilderFactory
				.createObjectBuilder();
		JsonObjectBuilder jsonObjectBuilder = new JavaxJsonObjectBuilderAdapter(
				javaxJsonObjectBuilder);
		jsonObjectBuilder.addKeyString("id", "value");
		JsonObject jsonObject = jsonObjectBuilder.toJsonObject();
		assertEquals(((JsonString) jsonObject.getValue("id")).getStringValue(), "value");
	}

	@Test
	public void testJsonObjectBuilderAddKeyJsonObjectBuilder() {
		Map<String, Object> config = new HashMap<>();
		JsonBuilderFactory jsonBuilderFactory = Json.createBuilderFactory(config);
		javax.json.JsonObjectBuilder javaxJsonObjectBuilder = jsonBuilderFactory
				.createObjectBuilder();
		JsonObjectBuilder jsonObjectBuilder = new JavaxJsonObjectBuilderAdapter(
				javaxJsonObjectBuilder);
		javax.json.JsonObjectBuilder javaxJsonObjectBuilder2 = jsonBuilderFactory
				.createObjectBuilder();
		JsonObjectBuilder jsonObjectBuilder2 = new JavaxJsonObjectBuilderAdapter(
				javaxJsonObjectBuilder2);
		jsonObjectBuilder2.addKeyString("id2", "value2");
		jsonObjectBuilder.addKeyJsonObjectBuilder("id", jsonObjectBuilder2);
		JsonObject jsonObject = jsonObjectBuilder.toJsonObject();

		JsonObject jsonObjectLevel1 = jsonObject.getValueAsJsonObject("id");
		JsonString jsonString = (JsonString) jsonObjectLevel1.getValue("id2");

		assertEquals(jsonString.getStringValue(), "value2");
	}

	@Test
	public void testJsonObjectBuilderAddKeyJsonArrayBuilder() {
		Map<String, Object> config = new HashMap<>();
		JsonBuilderFactory jsonBuilderFactory = Json.createBuilderFactory(config);
		javax.json.JsonObjectBuilder javaxJsonObjectBuilder = jsonBuilderFactory
				.createObjectBuilder();
		JsonObjectBuilder jsonObjectBuilder = new JavaxJsonObjectBuilderAdapter(
				javaxJsonObjectBuilder);

		javax.json.JsonArrayBuilder javaxJsonArrayBuilder = jsonBuilderFactory.createArrayBuilder();

		JsonArrayBuilder jsonArrayBuilder = new JavaxJsonArrayBuilderAdapter(javaxJsonArrayBuilder);
		jsonArrayBuilder.addString("value");

		jsonObjectBuilder.addKeyJsonArrayBuilder("id", jsonArrayBuilder);
		JsonObject jsonObject = jsonObjectBuilder.toJsonObject();

		JsonArray jsonArrayLevel1 = jsonObject.getValueAsJsonArray("id");
		JsonString jsonString = (JsonString) jsonArrayLevel1.getValue(0);

		assertEquals(jsonString.getStringValue(), "value");
	}
}
