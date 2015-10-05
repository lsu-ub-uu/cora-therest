package se.uu.ub.cora.therest.data.converter;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.therest.data.RestDataAtomic;
import se.uu.ub.cora.therest.data.RestDataGroup;
import se.uu.ub.cora.therest.data.converter.DataToJsonConverter;
import se.uu.ub.cora.therest.data.converter.DataToJsonConverterFactory;
import se.uu.ub.cora.therest.data.converter.DataToJsonConverterFactoryImp;
import se.uu.ub.cora.therest.json.builder.JsonBuilderFactory;
import se.uu.ub.cora.therest.json.builder.org.OrgJsonBuilderFactoryAdapter;

public class DataGroupToJsonConverterTest {
	private DataToJsonConverterFactory dataToJsonConverterFactory;
	private JsonBuilderFactory factory;
	private RestDataGroup restDataGroup;

	@BeforeMethod
	public void beforeMethod() {
		dataToJsonConverterFactory = new DataToJsonConverterFactoryImp();
		factory = new OrgJsonBuilderFactoryAdapter();
		restDataGroup = RestDataGroup.withNameInData("groupNameInData");
	}

	@Test
	public void testToJson() {
		DataToJsonConverter dataToJsonConverter = dataToJsonConverterFactory
				.createForRestDataElement(factory, restDataGroup);
		String json = dataToJsonConverter.toJson();

		assertEquals(json, "{\"name\":\"groupNameInData\"}");
	}

	@Test
	public void testToJsonGroupWithAttribute() {
		restDataGroup.addAttributeByIdWithValue("attributeNameInData", "attributeValue");

		DataToJsonConverter dataToJsonConverter = dataToJsonConverterFactory
				.createForRestDataElement(factory, restDataGroup);
		String json = dataToJsonConverter.toJson();

		assertEquals(json,
				"{\"name\":\"groupNameInData\",\"attributes\":{\"attributeNameInData\":\"attributeValue\"}}");
	}

	@Test
	public void testToJsonGroupWithAttributes() {
		restDataGroup.addAttributeByIdWithValue("attributeNameInData", "attributeValue");
		restDataGroup.addAttributeByIdWithValue("attributeNameInData2", "attributeValue2");

		DataToJsonConverter dataToJsonConverter = dataToJsonConverterFactory
				.createForRestDataElement(factory, restDataGroup);
		String json = dataToJsonConverter.toJson();

		assertEquals(json, "{\"name\":\"groupNameInData\",\"attributes\":{"
				+ "\"attributeNameInData2\":\"attributeValue2\","
				+ "\"attributeNameInData\":\"attributeValue\"" + "}}");
	}

	@Test
	public void testToJsonGroupWithAtomicChild() {
		restDataGroup.addChild(RestDataAtomic.withNameInDataAndValue("atomicNameInData", "atomicValue"));

		DataToJsonConverter dataToJsonConverter = dataToJsonConverterFactory
				.createForRestDataElement(factory, restDataGroup);
		String json = dataToJsonConverter.toJson();

		assertEquals(json, "{\"children\":[{\"atomicNameInData\":\"atomicValue\"}],\"name\":\"groupNameInData\"}");
	}

	@Test
	public void testToJsonGroupWithAtomicChildAndGroupChildWithAtomicChild() {
		restDataGroup.addChild(RestDataAtomic.withNameInDataAndValue("atomicNameInData", "atomicValue"));

		RestDataGroup restDataGroup2 = RestDataGroup.withNameInData("groupNameInData2");
		restDataGroup.addChild(restDataGroup2);

		restDataGroup2.addChild(RestDataAtomic.withNameInDataAndValue("atomicNameInData2", "atomicValue2"));

		DataToJsonConverter dataToJsonConverter = dataToJsonConverterFactory
				.createForRestDataElement(factory, restDataGroup);
		String json = dataToJsonConverter.toJson();

		String expectedJson = "{";
//		expectedJson += "\"groupNameInData\"";
		expectedJson += "\"children\":[";
		expectedJson += "{\"atomicNameInData\":\"atomicValue\"},";
		expectedJson += "{\"children\":[{\"atomicNameInData2\":\"atomicValue2\"}]";
		expectedJson += ",\"name\":\"groupNameInData2\"}]";
		expectedJson += ",\"name\":\"groupNameInData\"}";

		assertEquals(json, expectedJson);
	}

	@Test
	public void testToJsonGroupWithAttributesAndAtomicChildAndGroupChildWithAtomicChild() {
		restDataGroup.addAttributeByIdWithValue("attributeNameInData", "attributeValue");
		restDataGroup.addAttributeByIdWithValue("attributeNameInData2", "attributeValue2");

		RestDataGroup recordInfo = RestDataGroup.withNameInData("recordInfo");
		recordInfo.addChild(RestDataAtomic.withNameInDataAndValue("id", "place:0001"));
		recordInfo.addChild(RestDataAtomic.withNameInDataAndValue("type", "place"));
		recordInfo.addChild(RestDataAtomic.withNameInDataAndValue("createdBy", "userId"));
		restDataGroup.addChild(recordInfo);

		restDataGroup.addChild(RestDataAtomic.withNameInDataAndValue("atomicNameInData", "atomicValue"));

		RestDataGroup dataGroup2 = RestDataGroup.withNameInData("groupNameInData2");
		dataGroup2.addAttributeByIdWithValue("g2AttributeNameInData", "g2AttributeValue");
		restDataGroup.addChild(dataGroup2);

		dataGroup2.addChild(RestDataAtomic.withNameInDataAndValue("atomicNameInData2", "atomicValue2"));

		DataToJsonConverter dataToJsonConverter = dataToJsonConverterFactory
				.createForRestDataElement(factory, restDataGroup);
		String json = dataToJsonConverter.toJson();
		String  expectedJson = "{\"children\":[";
		expectedJson += "{\"children\":[";
		expectedJson += "{\"id\":\"place:0001\"},";
		expectedJson += "{\"type\":\"place\"},";
		expectedJson += "{\"createdBy\":\"userId\"}],\"name\":\"recordInfo\"},";
		expectedJson += "{\"atomicNameInData\":\"atomicValue\"},";
		expectedJson += "{\"children\":[{\"atomicNameInData2\":\"atomicValue2\"}],";
		expectedJson += "\"name\":\"groupNameInData2\",\"attributes\":{";
		expectedJson += "\"g2AttributeNameInData\":\"g2AttributeValue\"}}],";
		expectedJson += "\"name\":\"groupNameInData\",\"attributes\":{";
		expectedJson += "\"attributeNameInData2\":\"attributeValue2\",";
		expectedJson += "\"attributeNameInData\":\"attributeValue\"}}";

		assertEquals(json, expectedJson);
	}

}
