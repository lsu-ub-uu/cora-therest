package epc.therest.json.builder.simple;

import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

import epc.therest.json.builder.JsonArrayBuilder;
import epc.therest.json.builder.JsonBuilderFactory;
import epc.therest.json.builder.JsonObjectBuilder;

public class SimpleJsonBuilderFactoryTest {
	@Test
	public void testCreateObjectBuilder() {
		JsonBuilderFactory jsonBuilderFactory = new SimpleJsonBuilderFactory();
		JsonObjectBuilder jsonObjectBuilder = jsonBuilderFactory.createObjectBuilder();
		assertTrue(jsonObjectBuilder instanceof SimpleJsonObjectBuilder);
	}

	@Test
	public void testCreateArrayBuilder() {
		JsonBuilderFactory jsonBuilderFactory = new SimpleJsonBuilderFactory();
		JsonArrayBuilder jsonArrayBuilder = jsonBuilderFactory.createArrayBuilder();
		assertTrue(jsonArrayBuilder instanceof SimpleJsonArrayBuilder);
	}
}
