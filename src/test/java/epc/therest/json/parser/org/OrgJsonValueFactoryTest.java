package epc.therest.json.parser.org;

import static org.testng.Assert.assertTrue;

import org.json.JSONObject;
import org.testng.annotations.Test;

import epc.therest.json.parser.JsonObject;
import epc.therest.json.parser.JsonValue;

public class OrgJsonValueFactoryTest {
	@Test
	public void testCreateFromOrgJsonObject() {
		Object orgJsonObject = new JSONObject();
		JsonValue jsonValue = OrgJsonValueFactory.createFromOrgJsonObject(orgJsonObject);
		assertTrue(jsonValue instanceof JsonObject);
	}
}
