package epc.therest.json;

import org.testng.Assert;
import org.testng.annotations.Test;

import epc.spider.data.SpiderDataAtomic;
import epc.spider.data.SpiderDataGroup;
import epc.therest.data.RestDataElement;
import epc.therest.data.RestDataGroup;
import epc.therest.jsonbuilder.JsonBuilderFactory;
import epc.therest.jsonbuilder.javax.JavaxJsonBuilderFactory;

public class DataGroupToJsonConverterTest {
	@Test
	public void testToJson() {
		DataToJsonConverterFactory dataToJsonConverterFactory = new DataToJsonConverterFactoryImp();
		SpiderDataGroup dataGroup = SpiderDataGroup.withDataId("groupDataId");
		RestDataElement restDataElement = RestDataGroup.fromDataGroup(dataGroup);

		JsonBuilderFactory factory = new JavaxJsonBuilderFactory();

		DataToJsonConverter dataToJsonConverter = dataToJsonConverterFactory
				.createForRestDataElement(factory, restDataElement);
		String json = dataToJsonConverter.toJson();

		Assert.assertEquals(json, "{\"groupDataId\":{}}");
	}

	@Test
	public void testToJsonGroupWithAttribute() {
		DataToJsonConverterFactory dataToJsonConverterFactory = new DataToJsonConverterFactoryImp();
		SpiderDataGroup dataGroup = SpiderDataGroup.withDataId("groupDataId");
		dataGroup.addAttributeByIdWithValue("attributeDataId", "attributeValue");

		RestDataElement restDataElement = RestDataGroup.fromDataGroup(dataGroup);

		JsonBuilderFactory factory = new JavaxJsonBuilderFactory();
		DataToJsonConverter dataToJsonConverter = dataToJsonConverterFactory
				.createForRestDataElement(factory, restDataElement);
		String json = dataToJsonConverter.toJson();

		Assert.assertEquals(json,
				"{\"groupDataId\":{\"attributes\":{\"attributeDataId\":\"attributeValue\"}}}");
	}

	@Test
	public void testToJsonGroupWithAttributes() {
		DataToJsonConverterFactory dataToJsonConverterFactory = new DataToJsonConverterFactoryImp();
		SpiderDataGroup dataGroup = SpiderDataGroup.withDataId("groupDataId");
		dataGroup.addAttributeByIdWithValue("attributeDataId", "attributeValue");
		dataGroup.addAttributeByIdWithValue("attributeDataId2", "attributeValue2");

		RestDataElement restDataElement = RestDataGroup.fromDataGroup(dataGroup);

		JsonBuilderFactory factory = new JavaxJsonBuilderFactory();
		DataToJsonConverter dataToJsonConverter = dataToJsonConverterFactory
				.createForRestDataElement(factory, restDataElement);
		String json = dataToJsonConverter.toJson();

		Assert.assertEquals(json, "{\"groupDataId\":{\"attributes\":{"
				+ "\"attributeDataId\":\"attributeValue\","
				+ "\"attributeDataId2\":\"attributeValue2\"" + "}}}");
	}

	@Test
	public void testToJsonGroupWithAtomicChild() {
		DataToJsonConverterFactory dataToJsonConverterFactory = new DataToJsonConverterFactoryImp();
		SpiderDataGroup dataGroup = SpiderDataGroup.withDataId("groupDataId");

		SpiderDataAtomic dataAtomic = SpiderDataAtomic.withDataIdAndValue("atomicDataId",
				"atomicValue");
		dataGroup.addChild(dataAtomic);

		RestDataElement restDataElement = RestDataGroup.fromDataGroup(dataGroup);

		JsonBuilderFactory factory = new JavaxJsonBuilderFactory();
		DataToJsonConverter dataToJsonConverter = dataToJsonConverterFactory
				.createForRestDataElement(factory, restDataElement);
		String json = dataToJsonConverter.toJson();

		Assert.assertEquals(json,
				"{\"groupDataId\":{\"children\":[{\"atomicDataId\":\"atomicValue\"}]}}");
	}

	@Test
	public void testToJsonGroupWithAtomicChildAndGroupChildWithAtomicChild() {
		DataToJsonConverterFactory dataToJsonConverterFactory = new DataToJsonConverterFactoryImp();
		SpiderDataGroup dataGroup = SpiderDataGroup.withDataId("groupDataId");

		SpiderDataAtomic dataAtomic = SpiderDataAtomic.withDataIdAndValue("atomicDataId",
				"atomicValue");
		dataGroup.addChild(dataAtomic);

		SpiderDataGroup dataGroup2 = SpiderDataGroup.withDataId("groupDataId2");
		dataGroup.addChild(dataGroup2);

		SpiderDataAtomic dataAtomic2 = SpiderDataAtomic.withDataIdAndValue("atomicDataId2",
				"atomicValue2");
		dataGroup2.addChild(dataAtomic2);

		RestDataElement restDataElement = RestDataGroup.fromDataGroup(dataGroup);

		JsonBuilderFactory factory = new JavaxJsonBuilderFactory();
		DataToJsonConverter dataToJsonConverter = dataToJsonConverterFactory
				.createForRestDataElement(factory, restDataElement);
		String json = dataToJsonConverter.toJson();

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
		DataToJsonConverterFactory dataToJsonConverterFactory = new DataToJsonConverterFactoryImp();
		SpiderDataGroup dataGroup = SpiderDataGroup.withDataId("groupDataId");
		dataGroup.addAttributeByIdWithValue("attributeDataId", "attributeValue");
		dataGroup.addAttributeByIdWithValue("attributeDataId2", "attributeValue2");

		SpiderDataAtomic dataAtomic = SpiderDataAtomic.withDataIdAndValue("atomicDataId",
				"atomicValue");
		dataGroup.addChild(dataAtomic);

		SpiderDataGroup dataGroup2 = SpiderDataGroup.withDataId("groupDataId2");
		dataGroup2.addAttributeByIdWithValue("g2AttributeDataId", "g2AttributeValue");
		dataGroup.addChild(dataGroup2);

		SpiderDataAtomic dataAtomic2 = SpiderDataAtomic.withDataIdAndValue("atomicDataId2",
				"atomicValue2");
		dataGroup2.addChild(dataAtomic2);

		RestDataElement restDataElement = RestDataGroup.fromDataGroup(dataGroup);

		JsonBuilderFactory factory = new JavaxJsonBuilderFactory();
		DataToJsonConverter dataToJsonConverter = dataToJsonConverterFactory
				.createForRestDataElement(factory, restDataElement);
		String json = dataToJsonConverter.toJson();
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
