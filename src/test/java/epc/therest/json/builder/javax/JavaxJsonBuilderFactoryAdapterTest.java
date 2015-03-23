package epc.therest.json.builder.javax;

import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

import epc.therest.json.builder.JsonArrayBuilder;
import epc.therest.json.builder.JsonBuilderFactory;
import epc.therest.json.builder.JsonObjectBuilder;
import epc.therest.json.builder.javax.JavaxJsonArrayBuilderAdapter;
import epc.therest.json.builder.javax.JavaxJsonBuilderFactoryAdapter;
import epc.therest.json.builder.javax.JavaxJsonObjectBuilderAdapter;

public class JavaxJsonBuilderFactoryAdapterTest {
	@Test
	public void testCreateBuilderFactoryObject() {
		JsonBuilderFactory jsonBuilderFactory = new JavaxJsonBuilderFactoryAdapter();
		JsonObjectBuilder jsonObjectBuilder = jsonBuilderFactory.createObjectBuilder();
		assertTrue(jsonObjectBuilder instanceof JavaxJsonObjectBuilderAdapter);
	}

	@Test
	public void testCreateBuilderFactoryArray() {
		JsonBuilderFactory jsonBuilderFactory = new JavaxJsonBuilderFactoryAdapter();
		JsonArrayBuilder jsonArrayBuilder = jsonBuilderFactory.createArrayBuilder();
		assertTrue(jsonArrayBuilder instanceof JavaxJsonArrayBuilderAdapter);
	}
}
