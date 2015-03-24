package epc.therest.json.builder.simple;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import epc.therest.json.builder.JsonArrayBuilder;
import epc.therest.json.builder.JsonObjectBuilder;
import epc.therest.json.parser.JsonArray;
import epc.therest.json.parser.JsonObject;
import epc.therest.json.parser.JsonString;

public class SimpleJsonObjectBuilderAdapterTest {
	@Test
	public void testJsonObjectBuilderAddKeyValue() {
		JsonObjectBuilder jsonObjectBuilder = new SimpleJsonObjectBuilderAdapter();
		jsonObjectBuilder.add("id", "value");
		JsonObject jsonObject = jsonObjectBuilder.build();
		assertEquals(((JsonString) jsonObject.getValue("id")).getStringValue(), "value");
	}

	@Test
	public void testJsonObjectBuilderAddKeyJsonObjectBuilder() {
		JsonObjectBuilder jsonObjectBuilder = new SimpleJsonObjectBuilderAdapter();
		JsonObjectBuilder jsonObjectBuilder2 = new SimpleJsonObjectBuilderAdapter();
		jsonObjectBuilder2.add("id2", "value2");
		jsonObjectBuilder.add("id", jsonObjectBuilder2);
		JsonObject jsonObject = jsonObjectBuilder.build();

		JsonObject jsonObjectLevel1 = jsonObject.getValueAsJsonObject("id");
		JsonString jsonString = (JsonString) jsonObjectLevel1.getValue("id2");

		assertEquals(jsonString.getStringValue(), "value2");
	}

	@Test
	public void testJsonObjectBuilderAddKeyJsonArrayBuilder() {
		JsonObjectBuilder jsonObjectBuilder = new SimpleJsonObjectBuilderAdapter();

		JsonArrayBuilder jsonArrayBuilder = new SimpleJsonArrayBuilderAdapter();
		jsonArrayBuilder.add("value");

		jsonObjectBuilder.add("id", jsonArrayBuilder);
		JsonObject jsonObject = jsonObjectBuilder.build();

		JsonArray jsonArrayLevel1 = jsonObject.getValueAsJsonArray("id");
		JsonString jsonString = (JsonString) jsonArrayLevel1.getValue(0);

		assertEquals(jsonString.getStringValue(), "value");
	}

}
