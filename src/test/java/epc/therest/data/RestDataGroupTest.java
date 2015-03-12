package epc.therest.data;

import static org.testng.Assert.assertEquals;

import java.util.Iterator;

import org.testng.Assert;
import org.testng.annotations.Test;

import epc.metadataformat.data.DataAtomic;
import epc.metadataformat.data.DataGroup;

public class RestDataGroupTest {
	@Test
	public void testInit() {
		RestDataGroup restDataGroup = RestDataGroup.withDataId("dataId");
		assertEquals(restDataGroup.getDataId(), "dataId",
				"DataId shold be the one set in the constructor");

		Assert.assertNotNull(restDataGroup.getAttributes(),
				"Attributes should not be null for a new DataGroup");

		restDataGroup.addAttribute("dataId", "Value");

		Assert.assertEquals(restDataGroup.getAttributes().get("dataId"), "Value",
				"Attribute with dataId dataId should have value Value");

		Assert.assertNotNull(restDataGroup.getChildren(),
				"Children should not be null for a new DataGroup");

		DataElementRest dataElementRest = RestDataGroup.withDataId("dataId2");
		restDataGroup.addChild(dataElementRest);
		Assert.assertEquals(restDataGroup.getChildren().stream().findAny().get(), dataElementRest,
				"Child should be the same as the one we added");

	}

	@Test
	public void testCreateDataGroupRestFromDataGroup() {
		RestDataGroup restDataGroup = RestDataGroup
				.fromDataGroup(DataGroup.withDataId("dataGroup"));
		Assert.assertEquals(restDataGroup.getDataId(), "dataGroup");
	}

	@Test
	public void testCreateDataGroupRestFromDataGroupWithAttribute() {
		DataGroup dataGroup = DataGroup.withDataId("dataId");
		dataGroup.addAttributeByIdWithValue("attributeId", "attributeValue");
		RestDataGroup restDataGroup = RestDataGroup.fromDataGroup(dataGroup);
		assertEquals(restDataGroup.getAttributes().get("attributeId"), "attributeValue",
				"Attribute value in RestDataGroup should be the same as in the DataGroup");
	}

	@Test
	public void testCreateDataGroupRestFromDataGroupWithDataGroupChild() {
		DataGroup dataGroup = DataGroup.withDataId("dataId");
		dataGroup.addChild(DataGroup.withDataId("childDataId"));
		RestDataGroup restDataGroup = RestDataGroup.fromDataGroup(dataGroup);
		Assert.assertEquals(restDataGroup.getChildren().stream().findAny().get().getDataId(),
				"childDataId");
	}

	@Test
	public void testCreateDataGroupRestFromDataGroupWithDataAtomicChild() {
		DataGroup dataGroup = DataGroup.withDataId("dataId");
		dataGroup.addChild(DataAtomic.withDataIdAndValue("childDataId", "atomicValue"));
		RestDataGroup restDataGroup = RestDataGroup.fromDataGroup(dataGroup);
		Assert.assertEquals(restDataGroup.getChildren().stream().findAny().get().getDataId(),
				"childDataId");
	}

	@Test
	public void testCreateDataGroupRestFromDataGroupLevelsOfChildren() {
		DataGroup dataGroup = DataGroup.withDataId("dataId");
		dataGroup.addChild(DataAtomic.withDataIdAndValue("atomicDataId", "atomicValue"));
		DataGroup dataGroup2 = DataGroup.withDataId("childDataId");
		dataGroup2.addChild(DataGroup.withDataId("grandChildDataId"));
		dataGroup.addChild(dataGroup2);
		RestDataGroup restDataGroup = RestDataGroup.fromDataGroup(dataGroup);
		Iterator<DataElementRest> iterator = restDataGroup.getChildren().iterator();
		Assert.assertTrue(iterator.hasNext(), "dataGroupRest should have at least one child");

		RestDataAtomic dataAtomicChild = (RestDataAtomic) iterator.next();
		Assert.assertEquals(dataAtomicChild.getDataId(), "atomicDataId",
				"DataId should be the one set in the DataAtomic, first child of dataGroup");

		RestDataGroup dataGroupChild = (RestDataGroup) iterator.next();
		Assert.assertEquals(dataGroupChild.getDataId(), "childDataId",
				"DataId should be the one set in dataGroup2");

		Assert.assertEquals(dataGroupChild.getChildren().stream().findAny().get().getDataId(),
				"grandChildDataId", "DataId should be the one set in the child of dataGroup2");

	}
}
