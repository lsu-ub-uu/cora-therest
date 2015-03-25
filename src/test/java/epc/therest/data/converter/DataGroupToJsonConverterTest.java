package epc.therest.data.converter;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import epc.spider.data.SpiderDataAtomic;
import epc.spider.data.SpiderDataGroup;
import epc.therest.data.RestDataElement;
import epc.therest.data.RestDataGroup;
import epc.therest.json.builder.JsonBuilderFactory;
import epc.therest.json.builder.org.OrgJsonBuilderFactoryAdapter;

public class DataGroupToJsonConverterTest {
	private DataToJsonConverterFactory dataToJsonConverterFactory;
	private JsonBuilderFactory factory;

	@BeforeMethod
	public void beforeMethod() {
		dataToJsonConverterFactory = new DataToJsonConverterFactoryImp();
		factory = new OrgJsonBuilderFactoryAdapter();
	}

	@Test
	public void testToJson() {
		SpiderDataGroup dataGroup = SpiderDataGroup.withDataId("groupDataId");
		RestDataElement restDataElement = RestDataGroup.fromDataGroup(dataGroup);

		DataToJsonConverter dataToJsonConverter = dataToJsonConverterFactory
				.createForRestDataElement(factory, restDataElement);
		String json = dataToJsonConverter.toJson();

		Assert.assertEquals(json, "{\"groupDataId\":{}}");
	}

	@Test
	public void testToJsonGroupWithAttribute() {
		SpiderDataGroup dataGroup = SpiderDataGroup.withDataId("groupDataId");
		dataGroup.addAttributeByIdWithValue("attributeDataId", "attributeValue");

		RestDataElement restDataElement = RestDataGroup.fromDataGroup(dataGroup);

		DataToJsonConverter dataToJsonConverter = dataToJsonConverterFactory
				.createForRestDataElement(factory, restDataElement);
		String json = dataToJsonConverter.toJson();

		Assert.assertEquals(json,
				"{\"groupDataId\":{\"attributes\":{\"attributeDataId\":\"attributeValue\"}}}");
	}

	@Test
	public void testToJsonGroupWithAttributes() {
		SpiderDataGroup dataGroup = SpiderDataGroup.withDataId("groupDataId");
		dataGroup.addAttributeByIdWithValue("attributeDataId", "attributeValue");
		dataGroup.addAttributeByIdWithValue("attributeDataId2", "attributeValue2");

		RestDataElement restDataElement = RestDataGroup.fromDataGroup(dataGroup);

		DataToJsonConverter dataToJsonConverter = dataToJsonConverterFactory
				.createForRestDataElement(factory, restDataElement);
		String json = dataToJsonConverter.toJson();

		Assert.assertEquals(json, "{\"groupDataId\":{\"attributes\":{"
				+ "\"attributeDataId\":\"attributeValue\","
				+ "\"attributeDataId2\":\"attributeValue2\"" + "}}}");
	}

	@Test
	public void testToJsonGroupWithAtomicChild() {
		SpiderDataGroup dataGroup = SpiderDataGroup.withDataId("groupDataId");

		SpiderDataAtomic dataAtomic = SpiderDataAtomic.withDataIdAndValue("atomicDataId",
				"atomicValue");
		dataGroup.addChild(dataAtomic);

		RestDataElement restDataElement = RestDataGroup.fromDataGroup(dataGroup);

		DataToJsonConverter dataToJsonConverter = dataToJsonConverterFactory
				.createForRestDataElement(factory, restDataElement);
		String json = dataToJsonConverter.toJson();

		Assert.assertEquals(json,
				"{\"groupDataId\":{\"children\":[{\"atomicDataId\":\"atomicValue\"}]}}");
	}

	@Test
	public void testToJsonGroupWithAtomicChildAndGroupChildWithAtomicChild() {
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

		DataToJsonConverter dataToJsonConverter = dataToJsonConverterFactory
				.createForRestDataElement(factory, restDataElement);
		String json = dataToJsonConverter.toJson();
		String expectedJson = "{";
		expectedJson += "\"groupDataId\":{";
		expectedJson += "\"children\":[";
		expectedJson += "{\"atomicDataId\":\"atomicValue\"},";
		expectedJson += "{\"groupDataId2\":{";
		expectedJson += "\"children\":[{\"atomicDataId2\":\"atomicValue2\"}],";
		expectedJson += "\"attributes\":{\"g2AttributeDataId\":\"g2AttributeValue\"}" + "}";
		expectedJson += "}" + "],";

		expectedJson += "\"attributes\":{" + "\"attributeDataId\":\"attributeValue\","
				+ "\"attributeDataId2\":\"attributeValue2\"}";

		expectedJson += "}";
		expectedJson += "}";

		Assert.assertEquals(json, expectedJson);
	}

}
