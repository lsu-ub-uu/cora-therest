package epc.therest.json.builder.org;

import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

import epc.therest.json.builder.JsonArrayBuilder;
import epc.therest.json.builder.JsonBuilderFactory;
import epc.therest.json.builder.JsonObjectBuilder;

public class OrgJsonBuilderFactoryAdapterTest {
	@Test
	public void testCreateBuilderFactoryObject() {
		JsonBuilderFactory jsonBuilderFactory = new OrgJsonBuilderFactoryAdapter();
		JsonObjectBuilder jsonObjectBuilder = jsonBuilderFactory.createObjectBuilder();
		assertTrue(jsonObjectBuilder instanceof OrgJsonObjectBuilderAdapter);
	}

	@Test
	public void testCreateBuilderFactoryArray() {
		JsonBuilderFactory jsonBuilderFactory = new OrgJsonBuilderFactoryAdapter();
		JsonArrayBuilder jsonArrayBuilder = jsonBuilderFactory.createArrayBuilder();
		assertTrue(jsonArrayBuilder instanceof OrgJsonArrayBuilderAdapter);
	}
}
