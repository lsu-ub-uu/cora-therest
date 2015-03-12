package epc.therest.json;

import static org.testng.Assert.assertEquals;

import java.util.Iterator;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import epc.therest.data.DataAtomicRest;
import epc.therest.data.DataElementRest;
import epc.therest.data.DataGroupRest;

public class DataGroupClassCreatorTest {
	private ClassCreatorFactory classCreatorFactory;

	@BeforeMethod
	public void beforeMethod() {
		classCreatorFactory = new ClassCreatorFactoryImp();

	}

	@Test
	public void testToClass() {
		String json = "{\"groupDataId\":{}}";
		DataGroupRest dataGroupRest = createDataGroupRestForJsonString(json);
		assertEquals(dataGroupRest.getDataId(), "groupDataId");
	}

	@Test
	public void testToClassWithAttribute() {
		String json = "{\"groupDataId\":{\"attributes\":{\"attributeDataId\":\"attributeValue\"}}}";
		DataGroupRest dataGroupRest = createDataGroupRestForJsonString(json);
		assertEquals(dataGroupRest.getDataId(), "groupDataId");
		String attributeValue = dataGroupRest.getAttributes().get("attributeDataId");
		assertEquals(attributeValue, "attributeValue");
	}

	@Test
	public void testToClassWithAttributes() {
		String json = "{\"groupDataId\":{\"attributes\":{"
				+ "\"attributeDataId\":\"attributeValue\","
				+ "\"attributeDataId2\":\"attributeValue2\"" + "}}}";

		DataGroupRest dataGroupRest = createDataGroupRestForJsonString(json);
		assertEquals(dataGroupRest.getDataId(), "groupDataId");
		String attributeValue = dataGroupRest.getAttributes().get("attributeDataId");
		assertEquals(attributeValue, "attributeValue");
		String attributeValue2 = dataGroupRest.getAttributes().get("attributeDataId2");
		assertEquals(attributeValue2, "attributeValue2");
	}

	@Test
	public void testToClassWithAtomicChild() {
		String json = "{\"groupDataId\":{\"children\":[{\"atomicDataId\":\"atomicValue\"}]}}";

		DataGroupRest dataGroupRest = createDataGroupRestForJsonString(json);
		assertEquals(dataGroupRest.getDataId(), "groupDataId");
		DataAtomicRest child = (DataAtomicRest) dataGroupRest.getChildren().iterator().next();
		assertEquals(child.getDataId(), "atomicDataId");
		assertEquals(child.getValue(), "atomicValue");
	}

	@Test
	public void testToClassGroupWithAtomicChildAndGroupChildWithAtomicChild() {
		String json = "{";
		json += "\"groupDataId\":{";
		json += "\"children\":[";
		json += "{\"atomicDataId\":\"atomicValue\"},";
		json += "{\"groupDataId2\":{\"children\":[{\"atomicDataId2\":\"atomicValue2\"}]}}";
		json += "]";
		json += "}";
		json += "}";

		DataGroupRest dataGroupRest = createDataGroupRestForJsonString(json);
		assertEquals(dataGroupRest.getDataId(), "groupDataId");
		Iterator<DataElementRest> iterator = dataGroupRest.getChildren().iterator();
		DataAtomicRest child = (DataAtomicRest) iterator.next();
		assertEquals(child.getDataId(), "atomicDataId");
		assertEquals(child.getValue(), "atomicValue");
		DataGroupRest child2 = (DataGroupRest) iterator.next();
		assertEquals(child2.getDataId(), "groupDataId2");
		DataAtomicRest subChild = (DataAtomicRest) child2.getChildren().iterator().next();
		assertEquals(subChild.getDataId(), "atomicDataId2");
		assertEquals(subChild.getValue(), "atomicValue2");
	}

	@Test
	public void testToClassGroupWithAttributesAndAtomicChildAndGroupChildWithAtomicChild() {
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

		DataGroupRest dataGroupRest = createDataGroupRestForJsonString(json);
		assertEquals(dataGroupRest.getDataId(), "groupDataId");

		String attributeValue2 = dataGroupRest.getAttributes().get("attributeDataId");
		assertEquals(attributeValue2, "attributeValue");

		Iterator<DataElementRest> iterator = dataGroupRest.getChildren().iterator();
		DataAtomicRest child = (DataAtomicRest) iterator.next();
		assertEquals(child.getDataId(), "atomicDataId");
		assertEquals(child.getValue(), "atomicValue");
		DataGroupRest child2 = (DataGroupRest) iterator.next();
		assertEquals(child2.getDataId(), "groupDataId2");
		DataAtomicRest subChild = (DataAtomicRest) child2.getChildren().iterator().next();
		assertEquals(subChild.getDataId(), "atomicDataId2");
		assertEquals(subChild.getValue(), "atomicValue2");

		String attributeValue = child2.getAttributes().get("g2AttributeDataId");
		assertEquals(attributeValue, "g2AttributeValue");
	}

	private DataGroupRest createDataGroupRestForJsonString(String json) {
		ClassCreator classCreator = classCreatorFactory.createForJsonString(json);
		DataElementRest dataElementRest = classCreator.toClass();
		DataGroupRest dataGroupRest = (DataGroupRest) dataElementRest;
		return dataGroupRest;
	}

	@Test(expectedExceptions = JsonParseException.class)
	public void testToClassWrongJsonExtraKeyValuePair() {
		String json = "{\"id\":{},\"id2\":\"value2\"}";
		ClassCreator classCreator = classCreatorFactory.createForJsonString(json);
		classCreator.toClass();
	}

	@Test(expectedExceptions = JsonParseException.class)
	public void testToClassWrongJsonOnlyKeyValuePairInsideGroup() {
		String json = "{\"id\":{\"id2\":\"value2\"}}";
		ClassCreator classCreator = classCreatorFactory.createForJsonString(json);
		classCreator.toClass();
	}

	@Test(expectedExceptions = JsonParseException.class)
	public void testToClassWrongJsonAttributesIsGroup() {
		String json = "{\"groupDataId\":{\"attributes\":{\"attributeDataId\":\"attributeValue\",\"bla\":{} }}}";
		ClassCreator classCreator = classCreatorFactory.createForJsonString(json);
		classCreator.toClass();
	}

	@Test(expectedExceptions = JsonParseException.class)
	public void testToClassWrongJsonAttributesIsArray() {
		String json = "{\"groupDataId\":{\"attributes\":{\"attributeDataId\":\"attributeValue\",\"bla\":[true] }}}";
		ClassCreator classCreator = classCreatorFactory.createForJsonString(json);
		classCreator.toClass();
	}

	@Test(expectedExceptions = JsonParseException.class)
	public void testToClassWrongJsonChildrenIsArray() {
		String json = "{\"groupDataId\":{\"children\":[{\"atomicDataId\":\"atomicValue\"},[]]}}";
		ClassCreator classCreator = classCreatorFactory.createForJsonString(json);
		classCreator.toClass();
	}

	@Test(expectedExceptions = JsonParseException.class)
	public void testToClassWrongJsonChildrenIsString() {
		String json = "{\"groupDataId\":{\"children\":[{\"atomicDataId\":\"atomicValue\"},\"string\"]}}";
		ClassCreator classCreator = classCreatorFactory.createForJsonString(json);
		classCreator.toClass();
	}
}
