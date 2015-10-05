package se.uu.ub.cora.therest.json.parser;

import org.testng.annotations.Test;

import se.uu.ub.cora.therest.json.parser.JsonValueType;

public class JsonValueTypeTest {
	@Test
	public void testEnum() {
		// small hack to get 100% coverage on enum
		JsonValueType.valueOf(JsonValueType.STRING.toString());
	}
}
