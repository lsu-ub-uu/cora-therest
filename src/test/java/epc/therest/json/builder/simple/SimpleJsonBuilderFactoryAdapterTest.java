package epc.therest.json.builder.simple;

import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

import epc.therest.json.builder.JsonArrayBuilder;
import epc.therest.json.builder.JsonBuilderFactory;
import epc.therest.json.builder.JsonObjectBuilder;

public class SimpleJsonBuilderFactoryAdapterTest {
	@Test
	public void testCreateObjectBuilder() {
		JsonBuilderFactory jsonBuilderFactory = new SimpleJsonBuilderFactoryAdapter();
		JsonObjectBuilder jsonObjectBuilder = jsonBuilderFactory.createObjectBuilder();
		assertTrue(jsonObjectBuilder instanceof SimpleJsonObjectBuilderAdapter);
	}

	@Test
	public void testCreateArrayBuilder() {
		JsonBuilderFactory jsonBuilderFactory = new SimpleJsonBuilderFactoryAdapter();
		JsonArrayBuilder jsonArrayBuilder = jsonBuilderFactory.createArrayBuilder();
		assertTrue(jsonArrayBuilder instanceof SimpleJsonArrayBuilderAdapter);
	}
}
