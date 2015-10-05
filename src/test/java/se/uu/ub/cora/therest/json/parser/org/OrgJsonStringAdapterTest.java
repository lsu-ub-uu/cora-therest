package se.uu.ub.cora.therest.json.parser.org;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

import se.uu.ub.cora.therest.json.parser.JsonString;
import se.uu.ub.cora.therest.json.parser.JsonValue;
import se.uu.ub.cora.therest.json.parser.JsonValueType;
import se.uu.ub.cora.therest.json.parser.org.OrgJsonStringAdapter;

public class OrgJsonStringAdapterTest {
	@Test
	public void testUsingString() {
		JsonValue jsonValue = new OrgJsonStringAdapter("");
		assertTrue(jsonValue instanceof JsonString);
	}

	@Test
	public void testGetValueType() {
		JsonValue jsonValue = new OrgJsonStringAdapter("");
		assertEquals(jsonValue.getValueType(), JsonValueType.STRING);
	}

	@Test
	public void testGetStringValue() {
		JsonString jsonString = new OrgJsonStringAdapter("value");
		assertEquals(jsonString.getStringValue(), "value");
	}
}
