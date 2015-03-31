package epc.therest.data.converter;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import epc.spider.data.Action;
import epc.spider.data.SpiderDataAtomic;
import epc.spider.data.SpiderDataGroup;
import epc.therest.data.RestDataElement;
import epc.therest.data.converter.spider.DataGroupSpiderToRestConverter;
import epc.therest.json.builder.JsonBuilderFactory;
import epc.therest.json.builder.org.OrgJsonBuilderFactoryAdapter;

public class DataGroupToJsonConverterTest {
	private DataToJsonConverterFactory dataToJsonConverterFactory;
	private JsonBuilderFactory factory;
	private SpiderDataGroup dataGroup;

	@BeforeMethod
	public void beforeMethod() {
		dataToJsonConverterFactory = new DataToJsonConverterFactoryImp();
		factory = new OrgJsonBuilderFactoryAdapter();
		dataGroup = SpiderDataGroup.withDataId("groupDataId");
	}

	@Test
	public void testToJson() {
		DataGroupSpiderToRestConverter converter = DataGroupSpiderToRestConverter
				.fromSpiderDataGroup(dataGroup);
		RestDataElement restDataElement = converter.toRest();

		DataToJsonConverter dataToJsonConverter = dataToJsonConverterFactory
				.createForRestDataElement(factory, restDataElement);
		String json = dataToJsonConverter.toJson();

		Assert.assertEquals(json, "{\"groupDataId\":{}}");
	}

	@Test
	public void testToJsonGroupWithAttribute() {
		dataGroup.addAttributeByIdWithValue("attributeDataId", "attributeValue");

		DataGroupSpiderToRestConverter converter = DataGroupSpiderToRestConverter
				.fromSpiderDataGroup(dataGroup);
		RestDataElement restDataElement = converter.toRest();

		DataToJsonConverter dataToJsonConverter = dataToJsonConverterFactory
				.createForRestDataElement(factory, restDataElement);
		String json = dataToJsonConverter.toJson();

		Assert.assertEquals(json,
				"{\"groupDataId\":{\"attributes\":{\"attributeDataId\":\"attributeValue\"}}}");
	}

	@Test
	public void testToJsonGroupWithAttributes() {
		dataGroup.addAttributeByIdWithValue("attributeDataId", "attributeValue");
		dataGroup.addAttributeByIdWithValue("attributeDataId2", "attributeValue2");

		DataGroupSpiderToRestConverter converter = DataGroupSpiderToRestConverter
				.fromSpiderDataGroup(dataGroup);
		RestDataElement restDataElement = converter.toRest();

		DataToJsonConverter dataToJsonConverter = dataToJsonConverterFactory
				.createForRestDataElement(factory, restDataElement);
		String json = dataToJsonConverter.toJson();

		Assert.assertEquals(json, "{\"groupDataId\":{\"attributes\":{"
				+ "\"attributeDataId\":\"attributeValue\","
				+ "\"attributeDataId2\":\"attributeValue2\"" + "}}}");
	}

	@Test
	public void testToJsonGroupWithAtomicChild() {
		SpiderDataAtomic dataAtomic = SpiderDataAtomic.withDataIdAndValue("atomicDataId",
				"atomicValue");
		dataGroup.addChild(dataAtomic);

		DataGroupSpiderToRestConverter converter = DataGroupSpiderToRestConverter
				.fromSpiderDataGroup(dataGroup);
		RestDataElement restDataElement = converter.toRest();

		DataToJsonConverter dataToJsonConverter = dataToJsonConverterFactory
				.createForRestDataElement(factory, restDataElement);
		String json = dataToJsonConverter.toJson();

		Assert.assertEquals(json,
				"{\"groupDataId\":{\"children\":[{\"atomicDataId\":\"atomicValue\"}]}}");
	}

	@Test
	public void testToJsonGroupWithAtomicChildAndGroupChildWithAtomicChild() {
		SpiderDataAtomic dataAtomic = SpiderDataAtomic.withDataIdAndValue("atomicDataId",
				"atomicValue");
		dataGroup.addChild(dataAtomic);

		SpiderDataGroup dataGroup2 = SpiderDataGroup.withDataId("groupDataId2");
		dataGroup.addChild(dataGroup2);

		SpiderDataAtomic dataAtomic2 = SpiderDataAtomic.withDataIdAndValue("atomicDataId2",
				"atomicValue2");
		dataGroup2.addChild(dataAtomic2);

		DataGroupSpiderToRestConverter converter = DataGroupSpiderToRestConverter
				.fromSpiderDataGroup(dataGroup);
		RestDataElement restDataElement = converter.toRest();

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
	public void testToJsonGroupWithAction() {
		dataGroup.addAction(Action.READ);
		SpiderDataGroup recordInfo = SpiderDataGroup.withDataId("recordInfo");
		recordInfo.addChild(SpiderDataAtomic.withDataIdAndValue("id", "place:0001"));
		recordInfo.addChild(SpiderDataAtomic.withDataIdAndValue("type", "place"));
		recordInfo.addChild(SpiderDataAtomic.withDataIdAndValue("createdBy", "userId"));
		dataGroup.addChild(recordInfo);

		DataGroupSpiderToRestConverter converter = DataGroupSpiderToRestConverter
				.fromSpiderDataGroup(dataGroup);
		RestDataElement restDataElement = converter.toRest();

		DataToJsonConverter dataToJsonConverter = dataToJsonConverterFactory
				.createForRestDataElement(factory, restDataElement);
		String json = dataToJsonConverter.toJson();

		String expectedJson = "{\"groupDataId\":" + "{\"children\":[" + "{\"recordInfo\":"
				+ "{\"children\":[" + "{\"id\":\"place:0001\"}," + "{\"type\":\"place\"},"
				+ "{\"createdBy\":\"userId\"}]}}]" + ",\"actionLinks\":{"
				+ "\"read\":{\"requestMethod\":\"GET\",\"rel\":\"read\","
				+ "\"contentType\":\"application/metadata_record+json\","
				+ "\"url\":\"http://localhost:8080/therest/rest/record/place/place:0001\","
				+ "\"accept\":\"application/metadata_record+json\"}}}}";
		Assert.assertEquals(json, expectedJson);
	}

	@Test
	public void testToJsonGroupWithAttributesAndAtomicChildAndGroupChildWithAtomicChildAndAction() {
		dataGroup.addAttributeByIdWithValue("attributeDataId", "attributeValue");
		dataGroup.addAttributeByIdWithValue("attributeDataId2", "attributeValue2");
		dataGroup.addAction(Action.READ);
		SpiderDataGroup recordInfo = SpiderDataGroup.withDataId("recordInfo");
		recordInfo.addChild(SpiderDataAtomic.withDataIdAndValue("id", "place:0001"));
		recordInfo.addChild(SpiderDataAtomic.withDataIdAndValue("type", "place"));
		recordInfo.addChild(SpiderDataAtomic.withDataIdAndValue("createdBy", "userId"));
		dataGroup.addChild(recordInfo);

		SpiderDataAtomic dataAtomic = SpiderDataAtomic.withDataIdAndValue("atomicDataId",
				"atomicValue");
		dataGroup.addChild(dataAtomic);

		SpiderDataGroup dataGroup2 = SpiderDataGroup.withDataId("groupDataId2");
		dataGroup2.addAttributeByIdWithValue("g2AttributeDataId", "g2AttributeValue");
		dataGroup.addChild(dataGroup2);

		SpiderDataAtomic dataAtomic2 = SpiderDataAtomic.withDataIdAndValue("atomicDataId2",
				"atomicValue2");
		dataGroup2.addChild(dataAtomic2);

		DataGroupSpiderToRestConverter converter = DataGroupSpiderToRestConverter
				.fromSpiderDataGroup(dataGroup);
		RestDataElement restDataElement = converter.toRest();

		DataToJsonConverter dataToJsonConverter = dataToJsonConverterFactory
				.createForRestDataElement(factory, restDataElement);
		String json = dataToJsonConverter.toJson();
		String expectedJson = "{\"groupDataId\":{";
		expectedJson += "\"children\":[";
		expectedJson += "{\"recordInfo\":{";
		expectedJson += "\"children\":[";
		expectedJson += "{\"id\":\"place:0001\"},";
		expectedJson += "{\"type\":\"place\"},";
		expectedJson += "{\"createdBy\":\"userId\"}]}},";
		expectedJson += "{\"atomicDataId\":\"atomicValue\"},";
		expectedJson += "{\"groupDataId2\":{\"children\":[{\"atomicDataId2\":\"atomicValue2\"}],";
		expectedJson += "\"attributes\":{";
		expectedJson += "\"g2AttributeDataId\":\"g2AttributeValue\"}}}],";
		expectedJson += "\"actionLinks\":{";
		expectedJson += "\"read\":{";
		expectedJson += "\"requestMethod\":\"GET\",";
		expectedJson += "\"rel\":\"read\",";
		expectedJson += "\"contentType\":\"application/metadata_record+json\",";
		expectedJson += "\"url\":\"http://localhost:8080/therest/rest/record/place/place:0001\",";
		expectedJson += "\"accept\":\"application/metadata_record+json\"}},";
		expectedJson += "\"attributes\":{";
		expectedJson += "\"attributeDataId\":\"attributeValue\",";
		expectedJson += "\"attributeDataId2\":\"attributeValue2\"}}}";

		Assert.assertEquals(json, expectedJson);
	}

}
