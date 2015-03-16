package epc.therest.json;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.util.Iterator;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import epc.therest.data.DataElementRest;
import epc.therest.data.RestDataAtomic;
import epc.therest.data.RestDataGroup;

public class DataGroupClassCreatorTest {
	private ClassCreatorFactory classCreatorFactory;

	@BeforeMethod
	public void beforeMethod() {
		classCreatorFactory = new ClassCreatorFactoryImp();

	}

	@Test
	public void testToClass() {
		String json = "{\"groupDataId\":{}}";
		RestDataGroup restDataGroup = createRestDataGroupForJsonString(json);
		assertEquals(restDataGroup.getDataId(), "groupDataId");
	}

	@Test
	public void testToClassWithAttribute() {
		String json = "{\"groupDataId\":{\"attributes\":{\"attributeDataId\":\"attributeValue\"}}}";
		RestDataGroup restDataGroup = createRestDataGroupForJsonString(json);
		assertEquals(restDataGroup.getDataId(), "groupDataId");
		String attributeValue = restDataGroup.getAttributes().get("attributeDataId");
		assertEquals(attributeValue, "attributeValue");
	}

	@Test
	public void testToClassWithAttributes() {
		String json = "{\"groupDataId\":{\"attributes\":{"
				+ "\"attributeDataId\":\"attributeValue\","
				+ "\"attributeDataId2\":\"attributeValue2\"" + "}}}";

		RestDataGroup restDataGroup = createRestDataGroupForJsonString(json);
		assertEquals(restDataGroup.getDataId(), "groupDataId");
		String attributeValue = restDataGroup.getAttributes().get("attributeDataId");
		assertEquals(attributeValue, "attributeValue");
		String attributeValue2 = restDataGroup.getAttributes().get("attributeDataId2");
		assertEquals(attributeValue2, "attributeValue2");
	}

	@Test
	public void testToClassWithAtomicChild() {
		String json = "{\"groupDataId\":{\"children\":[{\"atomicDataId\":\"atomicValue\"}]}}";

		RestDataGroup restDataGroup = createRestDataGroupForJsonString(json);
		assertEquals(restDataGroup.getDataId(), "groupDataId");
		RestDataAtomic child = (RestDataAtomic) restDataGroup.getChildren().iterator().next();
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

		RestDataGroup restDataGroup = createRestDataGroupForJsonString(json);
		assertEquals(restDataGroup.getDataId(), "groupDataId");
		Iterator<DataElementRest> iterator = restDataGroup.getChildren().iterator();
		RestDataAtomic child = (RestDataAtomic) iterator.next();
		assertEquals(child.getDataId(), "atomicDataId");
		assertEquals(child.getValue(), "atomicValue");
		RestDataGroup child2 = (RestDataGroup) iterator.next();
		assertEquals(child2.getDataId(), "groupDataId2");
		RestDataAtomic subChild = (RestDataAtomic) child2.getChildren().iterator().next();
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

		RestDataGroup restDataGroup = createRestDataGroupForJsonString(json);
		assertEquals(restDataGroup.getDataId(), "groupDataId");

		String attributeValue2 = restDataGroup.getAttributes().get("attributeDataId");
		assertEquals(attributeValue2, "attributeValue");

		Iterator<DataElementRest> iterator = restDataGroup.getChildren().iterator();
		RestDataAtomic child = (RestDataAtomic) iterator.next();
		assertEquals(child.getDataId(), "atomicDataId");
		assertEquals(child.getValue(), "atomicValue");
		RestDataGroup child2 = (RestDataGroup) iterator.next();
		assertEquals(child2.getDataId(), "groupDataId2");
		RestDataAtomic subChild = (RestDataAtomic) child2.getChildren().iterator().next();
		assertEquals(subChild.getDataId(), "atomicDataId2");
		assertEquals(subChild.getValue(), "atomicValue2");

		String attributeValue = child2.getAttributes().get("g2AttributeDataId");
		assertEquals(attributeValue, "g2AttributeValue");
	}

	private RestDataGroup createRestDataGroupForJsonString(String json) {
		ClassCreator classCreator = classCreatorFactory.createForJsonString(json);
		DataElementRest dataElementRest = classCreator.toClass();
		RestDataGroup restDataGroup = (RestDataGroup) dataElementRest;
		return restDataGroup;
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

	@Test(expectedExceptions = JsonParseException.class, enabled = false)
	// test disabled as this is currently not supported :(
	public void testToClassWrongJsonTwoAttributes() {
		String json = "{\"groupDataId\":{\"attributes\":{\"attributeDataId\":\"attributeValue\"}"
				+ ",\"attributes\":{\"attributeDataId2\":\"attributeValue2\"}}}";
		ClassCreator classCreator = classCreatorFactory.createForJsonString(json);
		DataElementRest class1 = classCreator.toClass();
		assertNotNull(class1);
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
