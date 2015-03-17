package epc.therest.jsonparser;

import org.testng.annotations.Test;

public class JsonValueTypeTest {
	@Test
	public void testEnum() {
		// small hack to get 100% coverage on enum
		JsonValueType.valueOf(JsonValueType.STRING.toString());
	}
}
