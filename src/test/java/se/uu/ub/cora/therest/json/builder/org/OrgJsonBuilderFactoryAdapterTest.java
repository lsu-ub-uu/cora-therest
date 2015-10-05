package se.uu.ub.cora.therest.json.builder.org;

import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

import se.uu.ub.cora.therest.json.builder.JsonArrayBuilder;
import se.uu.ub.cora.therest.json.builder.JsonBuilderFactory;
import se.uu.ub.cora.therest.json.builder.JsonObjectBuilder;
import se.uu.ub.cora.therest.json.builder.org.OrgJsonArrayBuilderAdapter;
import se.uu.ub.cora.therest.json.builder.org.OrgJsonBuilderFactoryAdapter;
import se.uu.ub.cora.therest.json.builder.org.OrgJsonObjectBuilderAdapter;

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
