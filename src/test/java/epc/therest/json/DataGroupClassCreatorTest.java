package epc.therest.json;

import java.util.Iterator;

import org.testng.Assert;
import org.testng.annotations.Test;

import epc.therest.data.DataAtomicRest;
import epc.therest.data.DataElementRest;
import epc.therest.data.DataGroupRest;

public class DataGroupClassCreatorTest {
	@Test
	public void testToClass() {
		ClassCreatorFactory classCreatorFactory = new ClassCreatorFactoryImp();
		String json = "{\"groupDataId\":{}}";

		ClassCreator classCreator = classCreatorFactory.factorOnJsonString(json);

		DataElementRest dataElementRest = classCreator.toClass();
		DataGroupRest dataGroupRest = (DataGroupRest) dataElementRest;
		Assert.assertEquals(dataGroupRest.getDataId(), "groupDataId");
	}

	@Test
	public void testToClassWithAttribute() {
		ClassCreatorFactory classCreatorFactory = new ClassCreatorFactoryImp();
		String json = "{\"groupDataId\":{\"attributes\":{\"attributeDataId\":\"attributeValue\"}}}";

		ClassCreator classCreator = classCreatorFactory.factorOnJsonString(json);

		DataElementRest dataElementRest = classCreator.toClass();
		DataGroupRest dataGroupRest = (DataGroupRest) dataElementRest;
		Assert.assertEquals(dataGroupRest.getDataId(), "groupDataId");
		String attributeValue = dataGroupRest.getAttributes().get("attributeDataId");
		Assert.assertEquals(attributeValue, "attributeValue");
	}

	@Test
	public void testToClassWithAttributes() {
		ClassCreatorFactory classCreatorFactory = new ClassCreatorFactoryImp();
		String json = "{\"groupDataId\":{\"attributes\":{"
				+ "\"attributeDataId\":\"attributeValue\","
				+ "\"attributeDataId2\":\"attributeValue2\"" + "}}}";

		ClassCreator classCreator = classCreatorFactory.factorOnJsonString(json);

		DataElementRest dataElementRest = classCreator.toClass();
		DataGroupRest dataGroupRest = (DataGroupRest) dataElementRest;
		Assert.assertEquals(dataGroupRest.getDataId(), "groupDataId");
		String attributeValue = dataGroupRest.getAttributes().get("attributeDataId");
		Assert.assertEquals(attributeValue, "attributeValue");
		String attributeValue2 = dataGroupRest.getAttributes().get("attributeDataId2");
		Assert.assertEquals(attributeValue2, "attributeValue2");
	}

	@Test
	public void testToClassWithAtomicChild() {
		ClassCreatorFactory classCreatorFactory = new ClassCreatorFactoryImp();
		String json = "{\"groupDataId\":{\"children\":[{\"atomicDataId\":\"atomicValue\"}]}}";

		ClassCreator classCreator = classCreatorFactory.factorOnJsonString(json);

		DataElementRest dataElementRest = classCreator.toClass();
		DataGroupRest dataGroupRest = (DataGroupRest) dataElementRest;
		Assert.assertEquals(dataGroupRest.getDataId(), "groupDataId");
		DataAtomicRest child = (DataAtomicRest) dataGroupRest.getChildren().iterator().next();
		Assert.assertEquals(child.getDataId(), "atomicDataId");
		Assert.assertEquals(child.getValue(), "atomicValue");
	}

	@Test
	public void testToClassGroupWithAtomicChildAndGroupChildWithAtomicChild() {
		ClassCreatorFactory classCreatorFactory = new ClassCreatorFactoryImp();
		String json = "{";
		json += "\"groupDataId\":{";
		json += "\"children\":[";
		json += "{\"atomicDataId\":\"atomicValue\"},";
		json += "{\"groupDataId2\":{\"children\":[{\"atomicDataId2\":\"atomicValue2\"}]}}";
		json += "]";
		json += "}";
		json += "}";

		ClassCreator classCreator = classCreatorFactory.factorOnJsonString(json);

		DataElementRest dataElementRest = classCreator.toClass();
		DataGroupRest dataGroupRest = (DataGroupRest) dataElementRest;
		Assert.assertEquals(dataGroupRest.getDataId(), "groupDataId");
		Iterator<DataElementRest> iterator = dataGroupRest.getChildren().iterator();
		DataAtomicRest child = (DataAtomicRest) iterator.next();
		Assert.assertEquals(child.getDataId(), "atomicDataId");
		Assert.assertEquals(child.getValue(), "atomicValue");
		DataGroupRest child2 = (DataGroupRest) iterator.next();
		Assert.assertEquals(child2.getDataId(), "groupDataId2");
		DataAtomicRest subChild = (DataAtomicRest) child2.getChildren().iterator().next();
		Assert.assertEquals(subChild.getDataId(), "atomicDataId2");
		Assert.assertEquals(subChild.getValue(), "atomicValue2");
	}

	@Test
	public void testToClassGroupWithAttributesAndAtomicChildAndGroupChildWithAtomicChild() {
		ClassCreatorFactory classCreatorFactory = new ClassCreatorFactoryImp();
		String json = "{";
		json += "\"groupDataId\":{";

		json += "\"attributes\":{" + "\"attributeDataId\":\"attributeValue\","
				+ "\"attributeDataId2\":\"attributeValue2\"" + "},";

		json += "\"children\":[";
		json += "{\"atomicDataId\":\"atomicValue\"},";
		json += "{\"groupDataId2\":{";
		json += "\"attributes\":{\"g2AttributeDataId\":\"g2AttributeValue\"},";
		json += "\"children\":[{\"atomicDataId2\":\"atomicValue2\"}]}}";
		json += "]";
		json += "}";
		json += "}";

		ClassCreator classCreator = classCreatorFactory.factorOnJsonString(json);

		DataElementRest dataElementRest = classCreator.toClass();
		DataGroupRest dataGroupRest = (DataGroupRest) dataElementRest;
		Assert.assertEquals(dataGroupRest.getDataId(), "groupDataId");

		String attributeValue2 = dataGroupRest.getAttributes().get("attributeDataId");
		Assert.assertEquals(attributeValue2, "attributeValue");

		Iterator<DataElementRest> iterator = dataGroupRest.getChildren().iterator();
		DataAtomicRest child = (DataAtomicRest) iterator.next();
		Assert.assertEquals(child.getDataId(), "atomicDataId");
		Assert.assertEquals(child.getValue(), "atomicValue");
		DataGroupRest child2 = (DataGroupRest) iterator.next();
		Assert.assertEquals(child2.getDataId(), "groupDataId2");
		DataAtomicRest subChild = (DataAtomicRest) child2.getChildren().iterator().next();
		Assert.assertEquals(subChild.getDataId(), "atomicDataId2");
		Assert.assertEquals(subChild.getValue(), "atomicValue2");

		String attributeValue = child2.getAttributes().get("g2AttributeDataId");
		Assert.assertEquals(attributeValue, "g2AttributeValue");
	}

}
