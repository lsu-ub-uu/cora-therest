package epc.therest.jsonbuilder.javax;

import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

import epc.therest.jsonbuilder.JsonArrayBuilder;
import epc.therest.jsonbuilder.JsonBuilderFactory;
import epc.therest.jsonbuilder.JsonObjectBuilder;

public class JsonBuilderFactoryTest {
	@Test
	public void testCreateBuilderFactoryObject() {
		JsonBuilderFactory jsonBuilderFactory = new JavaxJsonBuilderFactory();
		JsonObjectBuilder jsonObjectBuilder = jsonBuilderFactory.createObjectBuilder();
		assertTrue(jsonObjectBuilder instanceof JavaxJsonObjectBuilder);
	}

	@Test
	public void testCreateBuilderFactoryArray() {
		JsonBuilderFactory jsonBuilderFactory = new JavaxJsonBuilderFactory();
		JsonArrayBuilder jsonArrayBuilder = jsonBuilderFactory.createArrayBuilder();
		assertTrue(jsonArrayBuilder instanceof JavaxJsonArrayBuilder);
	}
}
