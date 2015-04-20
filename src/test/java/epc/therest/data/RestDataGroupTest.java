package epc.therest.data;

import static org.testng.Assert.assertEquals;

import org.testng.Assert;
import org.testng.annotations.Test;

public class RestDataGroupTest {
	@Test
	public void testInit() {
		RestDataGroup restDataGroup = RestDataGroup.withDataId("dataId");
		assertEquals(restDataGroup.getDataId(), "dataId",
				"DataId shold be the one set in the constructor");

		Assert.assertNotNull(restDataGroup.getAttributes(),
				"Attributes should not be null for a new DataGroup");

		restDataGroup.addAttributeByIdWithValue("dataId", "Value");

		Assert.assertEquals(restDataGroup.getAttributes().get("dataId"), "Value",
				"Attribute with dataId dataId should have value Value");

		Assert.assertNotNull(restDataGroup.getChildren(),
				"Children should not be null for a new DataGroup");

		RestDataElement restDataElement = RestDataGroup.withDataId("dataId2");
		restDataGroup.addChild(restDataElement);
		Assert.assertEquals(restDataGroup.getChildren().stream().findAny().get(), restDataElement,
				"Child should be the same as the one we added");

	}
}
