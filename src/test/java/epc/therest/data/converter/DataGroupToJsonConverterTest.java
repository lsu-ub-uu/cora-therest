package epc.therest.data.converter;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import epc.spider.data.Action;
import epc.spider.data.SpiderDataGroup;
import epc.therest.data.ActionLink;
import epc.therest.data.RestDataAtomic;
import epc.therest.data.RestDataGroup;
import epc.therest.json.builder.JsonBuilderFactory;
import epc.therest.json.builder.org.OrgJsonBuilderFactoryAdapter;

public class DataGroupToJsonConverterTest {
	private DataToJsonConverterFactory dataToJsonConverterFactory;
	private JsonBuilderFactory factory;
	private RestDataGroup restDataGroup;

	@BeforeMethod
	public void beforeMethod() {
		dataToJsonConverterFactory = new DataToJsonConverterFactoryImp();
		factory = new OrgJsonBuilderFactoryAdapter();
		SpiderDataGroup.withDataId("groupDataId");
		restDataGroup = RestDataGroup.withDataId("groupDataId");
	}

	@Test
	public void testToJson() {
		DataToJsonConverter dataToJsonConverter = dataToJsonConverterFactory
				.createForRestDataElement(factory, restDataGroup);
		String json = dataToJsonConverter.toJson();

		Assert.assertEquals(json, "{\"groupDataId\":{}}");
	}

	@Test
	public void testToJsonGroupWithAttribute() {
		restDataGroup.addAttributeByIdWithValue("attributeDataId", "attributeValue");

		DataToJsonConverter dataToJsonConverter = dataToJsonConverterFactory
				.createForRestDataElement(factory, restDataGroup);
		String json = dataToJsonConverter.toJson();

		Assert.assertEquals(json,
				"{\"groupDataId\":{\"attributes\":{\"attributeDataId\":\"attributeValue\"}}}");
	}

	@Test
	public void testToJsonGroupWithAttributes() {
		restDataGroup.addAttributeByIdWithValue("attributeDataId", "attributeValue");
		restDataGroup.addAttributeByIdWithValue("attributeDataId2", "attributeValue2");

		DataToJsonConverter dataToJsonConverter = dataToJsonConverterFactory
				.createForRestDataElement(factory, restDataGroup);
		String json = dataToJsonConverter.toJson();

		Assert.assertEquals(json, "{\"groupDataId\":{\"attributes\":{"
				+ "\"attributeDataId\":\"attributeValue\","
				+ "\"attributeDataId2\":\"attributeValue2\"" + "}}}");
	}

	@Test
	public void testToJsonGroupWithAtomicChild() {
		restDataGroup.addChild(RestDataAtomic.withDataIdAndValue("atomicDataId", "atomicValue"));

		DataToJsonConverter dataToJsonConverter = dataToJsonConverterFactory
				.createForRestDataElement(factory, restDataGroup);
		String json = dataToJsonConverter.toJson();

		Assert.assertEquals(json,
				"{\"groupDataId\":{\"children\":[{\"atomicDataId\":\"atomicValue\"}]}}");
	}

	@Test
	public void testToJsonGroupWithAtomicChildAndGroupChildWithAtomicChild() {
		restDataGroup.addChild(RestDataAtomic.withDataIdAndValue("atomicDataId", "atomicValue"));

		RestDataGroup restDataGroup2 = RestDataGroup.withDataId("groupDataId2");
		restDataGroup.addChild(restDataGroup2);

		restDataGroup2.addChild(RestDataAtomic.withDataIdAndValue("atomicDataId2", "atomicValue2"));

		DataToJsonConverter dataToJsonConverter = dataToJsonConverterFactory
				.createForRestDataElement(factory, restDataGroup);
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
		restDataGroup.addActionLink(createReadActionLink());

		RestDataGroup recordInfo = RestDataGroup.withDataId("recordInfo");
		recordInfo.addChild(RestDataAtomic.withDataIdAndValue("id", "place:0001"));
		recordInfo.addChild(RestDataAtomic.withDataIdAndValue("type", "place"));
		recordInfo.addChild(RestDataAtomic.withDataIdAndValue("createdBy", "userId"));
		restDataGroup.addChild(recordInfo);

		DataToJsonConverter dataToJsonConverter = dataToJsonConverterFactory
				.createForRestDataElement(factory, restDataGroup);
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

	private ActionLink createReadActionLink() {
		ActionLink actionLink = ActionLink.withAction(Action.READ);
		actionLink.setAccept("application/metadata_record+json");
		actionLink.setContentType("application/metadata_record+json");
		actionLink.setRequestMethod("GET");
		actionLink.setURL("http://localhost:8080/therest/rest/record/place/place:0001");
		return actionLink;
	}

	@Test
	public void testToJsonGroupWithAttributesAndAtomicChildAndGroupChildWithAtomicChildAndAction() {
		restDataGroup.addAttributeByIdWithValue("attributeDataId", "attributeValue");
		restDataGroup.addAttributeByIdWithValue("attributeDataId2", "attributeValue2");
		restDataGroup.addActionLink(createReadActionLink());

		RestDataGroup recordInfo = RestDataGroup.withDataId("recordInfo");
		recordInfo.addChild(RestDataAtomic.withDataIdAndValue("id", "place:0001"));
		recordInfo.addChild(RestDataAtomic.withDataIdAndValue("type", "place"));
		recordInfo.addChild(RestDataAtomic.withDataIdAndValue("createdBy", "userId"));
		restDataGroup.addChild(recordInfo);

		restDataGroup.addChild(RestDataAtomic.withDataIdAndValue("atomicDataId", "atomicValue"));

		RestDataGroup dataGroup2 = RestDataGroup.withDataId("groupDataId2");
		dataGroup2.addAttributeByIdWithValue("g2AttributeDataId", "g2AttributeValue");
		restDataGroup.addChild(dataGroup2);

		dataGroup2.addChild(RestDataAtomic.withDataIdAndValue("atomicDataId2", "atomicValue2"));

		DataToJsonConverter dataToJsonConverter = dataToJsonConverterFactory
				.createForRestDataElement(factory, restDataGroup);
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
