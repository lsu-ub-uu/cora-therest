package epc.therest.json.builder.javax;

import static org.testng.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonBuilderFactory;

import org.testng.annotations.Test;

import epc.therest.json.builder.JsonArrayBuilder;
import epc.therest.json.builder.JsonObjectBuilder;
import epc.therest.json.builder.javax.JavaxJsonArrayBuilderAdapter;
import epc.therest.json.builder.javax.JavaxJsonObjectBuilderAdapter;
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
		JsonObjectBuilder jsonObjectBuilder = new JavaxJsonObjectBuilderAdapter(javaxJsonObjectBuilder);
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
		JsonObjectBuilder jsonObjectBuilder = new JavaxJsonObjectBuilderAdapter(javaxJsonObjectBuilder);
		javax.json.JsonObjectBuilder javaxJsonObjectBuilder2 = jsonBuilderFactory
				.createObjectBuilder();
		JsonObjectBuilder jsonObjectBuilder2 = new JavaxJsonObjectBuilderAdapter(javaxJsonObjectBuilder2);
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
		JsonObjectBuilder jsonObjectBuilder = new JavaxJsonObjectBuilderAdapter(javaxJsonObjectBuilder);

		javax.json.JsonArrayBuilder javaxJsonArrayBuilder = jsonBuilderFactory.createArrayBuilder();

		JsonArrayBuilder jsonArrayBuilder = new JavaxJsonArrayBuilderAdapter(javaxJsonArrayBuilder);
		jsonArrayBuilder.add("value");

		jsonObjectBuilder.add("id", jsonArrayBuilder);
		JsonObject jsonObject = jsonObjectBuilder.build();

		JsonArray jsonArrayLevel1 = jsonObject.getArray("id");
		JsonString jsonString = (JsonString) jsonArrayLevel1.get(0);

		assertEquals(jsonString.getStringValue(), "value");
	}
}
