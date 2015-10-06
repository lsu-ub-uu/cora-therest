package se.uu.ub.cora.therest.data;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

public class RestDataAttributeTest {
	@Test
	public void testInit() {
		RestDataAttribute restDataAttribute = RestDataAttribute.withNameInDataAndValue("nameInData",
				"value");
		assertEquals(restDataAttribute.getNameInData(), "nameInData");
		assertEquals(restDataAttribute.getValue(), "value");
	}
}
