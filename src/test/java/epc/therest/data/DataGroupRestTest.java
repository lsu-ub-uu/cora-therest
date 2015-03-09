package epc.therest.data;

import static org.testng.Assert.assertEquals;

import java.util.Iterator;

import org.testng.Assert;
import org.testng.annotations.Test;

import epc.metadataformat.data.DataAtomic;
import epc.metadataformat.data.DataGroup;

public class DataGroupRestTest {
	@Test
	public void testInit() {
		DataGroupRest dataGroupRest = DataGroupRest.fromDataId("dataId");
		assertEquals(dataGroupRest.getDataId(), "dataId",
				"DataId shold be the one set in the constructor");

		Assert.assertNotNull(dataGroupRest.getAttributes(),
				"Attributes should not be null for a new DataGroup");

		dataGroupRest.addAttribute("dataId", "Value");

		Assert.assertEquals(dataGroupRest.getAttributes().get("dataId"), "Value",
				"Attribute with dataId dataId should have value Value");

		Assert.assertNotNull(dataGroupRest.getChildren(),
				"Children should not be null for a new DataGroup");

		DataElementRest dataElementRest = DataGroupRest.fromDataId("dataId2");
		dataGroupRest.addChild(dataElementRest);
		Assert.assertEquals(dataGroupRest.getChildren().stream().findAny().get(), dataElementRest,
				"Child should be the same as the one we added");

	}

	@Test
	public void testCreateDataGroupRestFromDataGroup() {
		DataGroupRest dataGroupRest = DataGroupRest.fromDataGroup(DataGroup.withDataId("dataGroup"));
		Assert.assertEquals(dataGroupRest.getDataId(), "dataGroup");
	}

	@Test
	public void testCreateDataGroupRestFromDataGroupWithAttribute() {
		DataGroup dataGroup = DataGroup.withDataId("dataId");
		dataGroup.addAttributeByIdWithValue("attributeId", "attributeValue");
		DataGroupRest dataGroupRest = DataGroupRest.fromDataGroup(dataGroup);
		assertEquals(dataGroupRest.getAttributes().get("attributeId"), "attributeValue",
				"Attribute value in DataGroupRest should be the same as in the DataGroup");
	}

	@Test
	public void testCreateDataGroupRestFromDataGroupWithDataGroupChild() {
		DataGroup dataGroup = DataGroup.withDataId("dataId");
		dataGroup.addChild(DataGroup.withDataId("childDataId"));
		DataGroupRest dataGroupRest = DataGroupRest.fromDataGroup(dataGroup);
		Assert.assertEquals(dataGroupRest.getChildren().stream().findAny().get().getDataId(),
				"childDataId");
	}

	@Test
	public void testCreateDataGroupRestFromDataGroupWithDataAtomicChild() {
		DataGroup dataGroup = DataGroup.withDataId("dataId");
		dataGroup.addChild(DataAtomic.withDataIdAndValue("childDataId", "atomicValue"));
		DataGroupRest dataGroupRest = DataGroupRest.fromDataGroup(dataGroup);
		Assert.assertEquals(dataGroupRest.getChildren().stream().findAny().get().getDataId(),
				"childDataId");
	}

	@Test
	public void testCreateDataGroupRestFromDataGroupLevelsOfChildren() {
		DataGroup dataGroup = DataGroup.withDataId("dataId");
		dataGroup.addChild(DataAtomic.withDataIdAndValue("atomicDataId", "atomicValue"));
		DataGroup dataGroup2 = DataGroup.withDataId("childDataId");
		dataGroup2.addChild(DataGroup.withDataId("grandChildDataId"));
		dataGroup.addChild(dataGroup2);
		DataGroupRest dataGroupRest = DataGroupRest.fromDataGroup(dataGroup);
		Iterator<DataElementRest> iterator = dataGroupRest.getChildren().iterator();
		Assert.assertTrue(iterator.hasNext(), "dataGroupRest should have at least one child");

		DataAtomicRest dataAtomicChild = (DataAtomicRest) iterator.next();
		Assert.assertEquals(dataAtomicChild.getDataId(), "atomicDataId",
				"DataId should be the one set in the DataAtomic, first child of dataGroup");

		DataGroupRest dataGroupChild = (DataGroupRest) iterator.next();
		Assert.assertEquals(dataGroupChild.getDataId(), "childDataId",
				"DataId should be the one set in dataGroup2");

		Assert.assertEquals(dataGroupChild.getChildren().stream().findAny().get().getDataId(),
				"grandChildDataId", "DataId should be the one set in the child of dataGroup2");

	}
}
