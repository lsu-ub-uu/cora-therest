package epc.therest.data.converter;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import epc.spider.data.SpiderDataGroup;
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
		restDataGroup = RestDataGroup.withDataId("groupDataId");
	}

	@Test
	public void testToJson() {
		DataToJsonConverter dataToJsonConverter = dataToJsonConverterFactory
				.createForRestDataElement(factory, restDataGroup);
		String json = dataToJsonConverter.toJson();

		assertEquals(json, "{\"name\":\"groupDataId\"}");
	}

	@Test
	public void testToJsonGroupWithAttribute() {
		restDataGroup.addAttributeByIdWithValue("attributeDataId", "attributeValue");

		DataToJsonConverter dataToJsonConverter = dataToJsonConverterFactory
				.createForRestDataElement(factory, restDataGroup);
		String json = dataToJsonConverter.toJson();

		assertEquals(json,
				"{\"name\":\"groupDataId\",\"attributes\":{\"attributeDataId\":\"attributeValue\"}}");
	}

	@Test
	public void testToJsonGroupWithAttributes() {
		restDataGroup.addAttributeByIdWithValue("attributeDataId", "attributeValue");
		restDataGroup.addAttributeByIdWithValue("attributeDataId2", "attributeValue2");

		DataToJsonConverter dataToJsonConverter = dataToJsonConverterFactory
				.createForRestDataElement(factory, restDataGroup);
		String json = dataToJsonConverter.toJson();

		assertEquals(json, "{\"name\":\"groupDataId\",\"attributes\":{"
				+ "\"attributeDataId\":\"attributeValue\","
				+ "\"attributeDataId2\":\"attributeValue2\"" + "}}");
	}

	@Test
	public void testToJsonGroupWithAtomicChild() {
		restDataGroup.addChild(RestDataAtomic.withDataIdAndValue("atomicDataId", "atomicValue"));

		DataToJsonConverter dataToJsonConverter = dataToJsonConverterFactory
				.createForRestDataElement(factory, restDataGroup);
		String json = dataToJsonConverter.toJson();

		assertEquals(json, "{\"children\":[{\"atomicDataId\":\"atomicValue\"}],\"name\":\"groupDataId\"}");
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
//		expectedJson += "\"groupDataId\"";
		expectedJson += "\"children\":[";
		expectedJson += "{\"atomicDataId\":\"atomicValue\"},";
		expectedJson += "{\"children\":[{\"atomicDataId2\":\"atomicValue2\"}]";
		expectedJson += ",\"name\":\"groupDataId2\"}]";
		expectedJson += ",\"name\":\"groupDataId\"}";

		assertEquals(json, expectedJson);
	}

	@Test
	public void testToJsonGroupWithAttributesAndAtomicChildAndGroupChildWithAtomicChild() {
		restDataGroup.addAttributeByIdWithValue("attributeDataId", "attributeValue");
		restDataGroup.addAttributeByIdWithValue("attributeDataId2", "attributeValue2");

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
		String  expectedJson = "{\"children\":[";
		expectedJson += "{\"children\":[";
		expectedJson += "{\"id\":\"place:0001\"},";
		expectedJson += "{\"type\":\"place\"},";
		expectedJson += "{\"createdBy\":\"userId\"}],\"name\":\"recordInfo\"},";
		expectedJson += "{\"atomicDataId\":\"atomicValue\"},";
		expectedJson += "{\"children\":[{\"atomicDataId2\":\"atomicValue2\"}],";
		expectedJson += "\"name\":\"groupDataId2\",\"attributes\":{";
		expectedJson += "\"g2AttributeDataId\":\"g2AttributeValue\"}}],";
		expectedJson += "\"name\":\"groupDataId\",\"attributes\":{";
		expectedJson += "\"attributeDataId\":\"attributeValue\",";
		expectedJson += "\"attributeDataId2\":\"attributeValue2\"}}";

		assertEquals(json, expectedJson);
	}

}
