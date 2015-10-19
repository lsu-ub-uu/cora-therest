package se.uu.ub.cora.therest.data;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class RestDataGroupTest {
	private RestDataGroup restDataGroup;

	@BeforeMethod
	public void setUp() {
		restDataGroup = RestDataGroup.withNameInData("nameInData");
	}

	@Test
	public void testInit() {
		assertEquals(restDataGroup.getNameInData(), "nameInData",
				"NameInData shold be the one set in the constructor");

		assertNotNull(restDataGroup.getAttributes(),
				"Attributes should not be null for a new DataGroup");

		restDataGroup.addAttributeByIdWithValue("nameInData", "Value");

		assertEquals(restDataGroup.getAttributes().get("nameInData"), "Value",
				"Attribute with nameInData nameInData should have value Value");

		assertNotNull(restDataGroup.getChildren(),
				"Children should not be null for a new DataGroup");

		RestDataElement restDataElement = RestDataGroup.withNameInData("nameInData2");
		restDataGroup.addChild(restDataElement);
		assertEquals(restDataGroup.getChildren().stream().findAny().get(), restDataElement,
				"Child should be the same as the one we added");

	}

	@Test
	public void testInitWithRepeatId() {
		restDataGroup.setRepeatId("x1");
		assertEquals(restDataGroup.getRepeatId(), "x1");
	}
}
