package epc.therest.json;

import org.testng.Assert;
import org.testng.annotations.Test;

import epc.metadataformat.data.DataAtomic;
import epc.metadataformat.data.DataGroup;
import epc.therest.data.DataElementRest;
import epc.therest.data.DataGroupRest;

public class DataGroupJsonCreatorTest {
	@Test
	public void testToJson() {
		JsonCreatorFactory jsonCreatorFactory = new JsonCreatorFactoryImp();
		DataGroup dataGroup = DataGroup.withDataId("groupDataId");
		DataElementRest dataElementRest = DataGroupRest.fromDataGroup(dataGroup);

		JsonCreator jsonCreator = jsonCreatorFactory.factorOnDataElementRest(dataElementRest);
		String json = jsonCreator.toJson();

		Assert.assertEquals(json, "{\"groupDataId\":{}}");
	}

	@Test
	public void testToJsonGroupWithAttribute() {
		JsonCreatorFactory jsonCreatorFactory = new JsonCreatorFactoryImp();
		DataGroup dataGroup = DataGroup.withDataId("groupDataId");
		dataGroup.addAttributeByIdWithValue("attributeDataId", "attributeValue");

		DataElementRest dataElementRest = DataGroupRest.fromDataGroup(dataGroup);

		JsonCreator jsonCreator = jsonCreatorFactory.factorOnDataElementRest(dataElementRest);
		String json = jsonCreator.toJson();

		Assert.assertEquals(json,
				"{\"groupDataId\":{\"attributes\":{\"attributeDataId\":\"attributeValue\"}}}");
	}

	@Test
	public void testToJsonGroupWithAttributes() {
		JsonCreatorFactory jsonCreatorFactory = new JsonCreatorFactoryImp();
		DataGroup dataGroup = DataGroup.withDataId("groupDataId");
		dataGroup.addAttributeByIdWithValue("attributeDataId", "attributeValue");
		dataGroup.addAttributeByIdWithValue("attributeDataId2", "attributeValue2");

		DataElementRest dataElementRest = DataGroupRest.fromDataGroup(dataGroup);

		JsonCreator jsonCreator = jsonCreatorFactory.factorOnDataElementRest(dataElementRest);
		String json = jsonCreator.toJson();

		Assert.assertEquals(json, "{\"groupDataId\":{\"attributes\":{"
				+ "\"attributeDataId\":\"attributeValue\","
				+ "\"attributeDataId2\":\"attributeValue2\"" + "}}}");
	}

	@Test
	public void testToJsonGroupWithAtomicChild() {
		JsonCreatorFactory jsonCreatorFactory = new JsonCreatorFactoryImp();
		DataGroup dataGroup = DataGroup.withDataId("groupDataId");

		DataAtomic dataAtomic = DataAtomic.withDataIdAndValue("atomicDataId", "atomicValue");
		dataGroup.addChild(dataAtomic);

		DataElementRest dataElementRest = DataGroupRest.fromDataGroup(dataGroup);

		JsonCreator jsonCreator = jsonCreatorFactory.factorOnDataElementRest(dataElementRest);
		String json = jsonCreator.toJson();

		Assert.assertEquals(json,
				"{\"groupDataId\":{\"children\":[{\"atomicDataId\":\"atomicValue\"}]}}");
	}

	@Test
	public void testToJsonGroupWithAtomicChildAndGroupChildWithAtomicChild() {
		JsonCreatorFactory jsonCreatorFactory = new JsonCreatorFactoryImp();
		DataGroup dataGroup = DataGroup.withDataId("groupDataId");

		DataAtomic dataAtomic = DataAtomic.withDataIdAndValue("atomicDataId", "atomicValue");
		dataGroup.addChild(dataAtomic);

		DataGroup dataGroup2 = DataGroup.withDataId("groupDataId2");
		dataGroup.addChild(dataGroup2);

		DataAtomic dataAtomic2 = DataAtomic.withDataIdAndValue("atomicDataId2", "atomicValue2");
		dataGroup2.addChild(dataAtomic2);

		DataElementRest dataElementRest = DataGroupRest.fromDataGroup(dataGroup);

		JsonCreator jsonCreator = jsonCreatorFactory.factorOnDataElementRest(dataElementRest);
		String json = jsonCreator.toJson();

		String expectedJson = "{";
		expectedJson += "\"groupDataId\":{";
		expectedJson += "\"children\":[";
		expectedJson += "{\"atomicDataId\":\"atomicValue\"},";
		expectedJson += "{\"groupDataId2\":{\"children\":[{\"atomicDataId2\":\"atomicValue2\"}]}}";
		expectedJson += "]";
		expectedJson += "}";
		expectedJson += "}";

		Assert.assertEquals(json, expectedJson);
	}

	@Test
	public void testToJsonGroupWithAttributesAndAtomicChildAndGroupChildWithAtomicChild() {
		JsonCreatorFactory jsonCreatorFactory = new JsonCreatorFactoryImp();
		DataGroup dataGroup = DataGroup.withDataId("groupDataId");
		dataGroup.addAttributeByIdWithValue("attributeDataId", "attributeValue");
		dataGroup.addAttributeByIdWithValue("attributeDataId2", "attributeValue2");

		DataAtomic dataAtomic = DataAtomic.withDataIdAndValue("atomicDataId", "atomicValue");
		dataGroup.addChild(dataAtomic);

		DataGroup dataGroup2 = DataGroup.withDataId("groupDataId2");
		dataGroup2.addAttributeByIdWithValue("g2AttributeDataId", "g2AttributeValue");
		dataGroup.addChild(dataGroup2);

		DataAtomic dataAtomic2 = DataAtomic.withDataIdAndValue("atomicDataId2", "atomicValue2");
		dataGroup2.addChild(dataAtomic2);

		DataElementRest dataElementRest = DataGroupRest.fromDataGroup(dataGroup);

		JsonCreator jsonCreator = jsonCreatorFactory.factorOnDataElementRest(dataElementRest);
		String json = jsonCreator.toJson();
		String expectedJson = "{";
		expectedJson += "\"groupDataId\":{";

		expectedJson += "\"attributes\":{" + "\"attributeDataId\":\"attributeValue\","
				+ "\"attributeDataId2\":\"attributeValue2\"" + "},";

		expectedJson += "\"children\":[";
		expectedJson += "{\"atomicDataId\":\"atomicValue\"},";
		expectedJson += "{\"groupDataId2\":{";
		expectedJson += "\"attributes\":{\"g2AttributeDataId\":\"g2AttributeValue\"},";
		expectedJson += "\"children\":[{\"atomicDataId2\":\"atomicValue2\"}]}}";
		expectedJson += "]";
		expectedJson += "}";
		expectedJson += "}";

		Assert.assertEquals(json, expectedJson);
	}

}
