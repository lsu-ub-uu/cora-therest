package epc.therest.json.builder.javax;

import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

import epc.therest.json.builder.JsonArrayBuilder;
import epc.therest.json.builder.JsonBuilderFactory;
import epc.therest.json.builder.JsonObjectBuilder;
import epc.therest.json.builder.javax.JavaxJsonArrayBuilder;
import epc.therest.json.builder.javax.JavaxJsonBuilderFactory;
import epc.therest.json.builder.javax.JavaxJsonObjectBuilder;

public class JavaxJsonBuilderFactoryTest {
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
