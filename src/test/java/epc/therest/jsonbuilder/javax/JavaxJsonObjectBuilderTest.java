package epc.therest.jsonbuilder.javax;

import static org.testng.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonBuilderFactory;

import org.testng.annotations.Test;

import epc.therest.jsonbuilder.JsonArrayBuilder;
import epc.therest.jsonbuilder.JsonObjectBuilder;
import epc.therest.jsonparser.JsonArray;
import epc.therest.jsonparser.JsonObject;
import epc.therest.jsonparser.JsonString;

public class JavaxJsonObjectBuilderTest {
	@Test
	public void testJsonObjectBuilderAddKeyValue() {
		Map<String, Object> config = new HashMap<>();
		JsonBuilderFactory jsonBuilderFactory = Json.createBuilderFactory(config);
		javax.json.JsonObjectBuilder javaxJsonObjectBuilder = jsonBuilderFactory
				.createObjectBuilder();
		JsonObjectBuilder jsonObjectBuilder = new JavaxJsonObjectBuilder(javaxJsonObjectBuilder);
		jsonObjectBuilder.add("id", "value");
		JsonObject jsonObject = jsonObjectBuilder.build();
		assertEquals(((JsonString) jsonObject.getValue("id")).getStringValue(), "value");
	}

	@Test
	public void testJsonObjectBuilderAddKeyJsonObjectBuilder() {
		Map<String, Object> config = new HashMap<>();
		JsonBuilderFactory jsonBuilderFactory = Json.createBuilderFactory(config);
		javax.json.JsonObjectBuilder javaxJsonObjectBuilder = jsonBuilderFactory
				.createObjectBuilder();
		JsonObjectBuilder jsonObjectBuilder = new JavaxJsonObjectBuilder(javaxJsonObjectBuilder);
		javax.json.JsonObjectBuilder javaxJsonObjectBuilder2 = jsonBuilderFactory
				.createObjectBuilder();
		JsonObjectBuilder jsonObjectBuilder2 = new JavaxJsonObjectBuilder(javaxJsonObjectBuilder2);
		jsonObjectBuilder2.add("id2", "value2");
		jsonObjectBuilder.add("id", jsonObjectBuilder2);
		JsonObject jsonObject = jsonObjectBuilder.build();

		JsonObject jsonObjectLevel1 = jsonObject.getObject("id");
		JsonString jsonString = (JsonString) jsonObjectLevel1.getValue("id2");

		assertEquals(jsonString.getStringValue(), "value2");
	}

	@Test
	public void testJsonObjectBuilderAddKeyJsonArrayBuilder() {
		Map<String, Object> config = new HashMap<>();
		JsonBuilderFactory jsonBuilderFactory = Json.createBuilderFactory(config);
		javax.json.JsonObjectBuilder javaxJsonObjectBuilder = jsonBuilderFactory
				.createObjectBuilder();
		JsonObjectBuilder jsonObjectBuilder = new JavaxJsonObjectBuilder(javaxJsonObjectBuilder);

		javax.json.JsonArrayBuilder javaxJsonArrayBuilder = jsonBuilderFactory.createArrayBuilder();

		JsonArrayBuilder jsonArrayBuilder = new JavaxJsonArrayBuilder(javaxJsonArrayBuilder);
		jsonArrayBuilder.add("value");

		jsonObjectBuilder.add("id", jsonArrayBuilder);
		JsonObject jsonObject = jsonObjectBuilder.build();

		JsonArray jsonArrayLevel1 = jsonObject.getArray("id");
		JsonString jsonString = (JsonString) jsonArrayLevel1.get(0);

		assertEquals(jsonString.getStringValue(), "value");
	}
}
