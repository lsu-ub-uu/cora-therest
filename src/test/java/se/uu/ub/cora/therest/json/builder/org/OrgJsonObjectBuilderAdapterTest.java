package se.uu.ub.cora.therest.json.builder.org;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.therest.json.builder.JsonArrayBuilder;
import se.uu.ub.cora.therest.json.builder.org.OrgJsonArrayBuilderAdapter;
import se.uu.ub.cora.therest.json.builder.org.OrgJsonObjectBuilderAdapter;
import se.uu.ub.cora.therest.json.parser.JsonArray;
import se.uu.ub.cora.therest.json.parser.JsonObject;
import se.uu.ub.cora.therest.json.parser.JsonString;

public class OrgJsonObjectBuilderAdapterTest {
	private OrgJsonObjectBuilderAdapter objectBuilderAdapter;

	@BeforeMethod
	public void beforeMethod() {
		objectBuilderAdapter = new OrgJsonObjectBuilderAdapter();
	}

	@Test
	public void testAddKeyValue() {
		objectBuilderAdapter.addKeyString("key", "value");
		JsonObject jsonObject = objectBuilderAdapter.toJsonObject();
		JsonString jsonString = jsonObject.getValueAsJsonString("key");
		assertEquals(jsonString.getStringValue(), "value");
	}

	@Test
	public void testAddKeyJsonObjectBuilder() {
		OrgJsonObjectBuilderAdapter objectBuilderAdapterChild = new OrgJsonObjectBuilderAdapter();
		objectBuilderAdapterChild.addKeyString("keyChild", "valueChild");

		objectBuilderAdapter.addKeyJsonObjectBuilder("key", objectBuilderAdapterChild);
		JsonObject jsonObject = objectBuilderAdapter.toJsonObject();

		JsonObject jsonObjectChild = jsonObject.getValueAsJsonObject("key");

		JsonString jsonString = jsonObjectChild.getValueAsJsonString("keyChild");
		assertEquals(jsonString.getStringValue(), "valueChild");
	}

	@Test
	public void testAddKeyJsonArrayBuilder() {
		JsonArrayBuilder jsonArrayBuilderChild = new OrgJsonArrayBuilderAdapter();
		jsonArrayBuilderChild.addString("valueChild");

		objectBuilderAdapter.addKeyJsonArrayBuilder("key", jsonArrayBuilderChild);

		JsonObject jsonObject = objectBuilderAdapter.toJsonObject();

		JsonArray jsonArrayChild = jsonObject.getValueAsJsonArray("key");

		JsonString jsonString = jsonArrayChild.getValueAsJsonString(0);
		assertEquals(jsonString.getStringValue(), "valueChild");
	}

	@Test
	public void testToJsonFormattedString() {
		OrgJsonObjectBuilderAdapter objectBuilderAdapterChild = new OrgJsonObjectBuilderAdapter();
		objectBuilderAdapterChild.addKeyString("keyChild", "valueChild");

		objectBuilderAdapter.addKeyJsonObjectBuilder("key", objectBuilderAdapterChild);

		String json = objectBuilderAdapter.toJsonFormattedString();
		assertEquals(json, "{\"key\":{\"keyChild\":\"valueChild\"}}");
	}
}
