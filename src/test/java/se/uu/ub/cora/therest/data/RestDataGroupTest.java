package se.uu.ub.cora.therest.data;

import static org.testng.Assert.assertEquals;

import org.testng.Assert;
import org.testng.annotations.Test;

import se.uu.ub.cora.therest.data.RestDataElement;
import se.uu.ub.cora.therest.data.RestDataGroup;

public class RestDataGroupTest {
	@Test
	public void testInit() {
		RestDataGroup restDataGroup = RestDataGroup.withNameInData("nameInData");
		assertEquals(restDataGroup.getNameInData(), "nameInData",
				"NameInData shold be the one set in the constructor");

		Assert.assertNotNull(restDataGroup.getAttributes(),
				"Attributes should not be null for a new DataGroup");

		restDataGroup.addAttributeByIdWithValue("nameInData", "Value");

		Assert.assertEquals(restDataGroup.getAttributes().get("nameInData"), "Value",
				"Attribute with nameInData nameInData should have value Value");

		Assert.assertNotNull(restDataGroup.getChildren(),
				"Children should not be null for a new DataGroup");

		RestDataElement restDataElement = RestDataGroup.withNameInData("nameInData2");
		restDataGroup.addChild(restDataElement);
		Assert.assertEquals(restDataGroup.getChildren().stream().findAny().get(), restDataElement,
				"Child should be the same as the one we added");

	}
}
