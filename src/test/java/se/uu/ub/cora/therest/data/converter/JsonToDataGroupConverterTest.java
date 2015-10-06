package se.uu.ub.cora.therest.data.converter;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.util.Iterator;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.therest.data.RestDataAtomic;
import se.uu.ub.cora.therest.data.RestDataElement;
import se.uu.ub.cora.therest.data.RestDataGroup;
import se.uu.ub.cora.therest.json.parser.JsonObject;
import se.uu.ub.cora.therest.json.parser.JsonParseException;
import se.uu.ub.cora.therest.json.parser.JsonParser;
import se.uu.ub.cora.therest.json.parser.JsonValue;
import se.uu.ub.cora.therest.json.parser.org.OrgJsonParser;

public class JsonToDataGroupConverterTest {
	private JsonToDataConverterFactory jsonToDataConverterFactory;
	private JsonParser jsonParser;

	@BeforeMethod
	public void beforeMethod() {
		jsonToDataConverterFactory = new JsonToDataConverterFactoryImp();
		jsonParser = new OrgJsonParser();
	}

	@Test
	public void testToClass() {
		String json = "{\"name\":\"groupNameInData\", \"children\":[]}";
		RestDataGroup restDataGroup = createRestDataGroupForJsonString(json);
		assertEquals(restDataGroup.getNameInData(), "groupNameInData");
	}

	@Test
	public void testToClassWithAttribute() {
		String json = "{\"name\":\"groupNameInData\",\"attributes\":{\"attributeNameInData\":\"attributeValue\"}, \"children\":[]}";
		RestDataGroup restDataGroup = createRestDataGroupForJsonString(json);
		assertEquals(restDataGroup.getNameInData(), "groupNameInData");
		String attributeValue = restDataGroup.getAttributes().get("attributeNameInData");
		assertEquals(attributeValue, "attributeValue");
	}

	@Test
	public void testToClassWithAttributes() {
		String json = "{\"name\":\"groupNameInData\",\"attributes\":{"
				+ "\"attributeNameInData\":\"attributeValue\","
				+ "\"attributeNameInData2\":\"attributeValue2\"" + "},\"children\":[]}";

		RestDataGroup restDataGroup = createRestDataGroupForJsonString(json);
		assertEquals(restDataGroup.getNameInData(), "groupNameInData");
		String attributeValue = restDataGroup.getAttributes().get("attributeNameInData");
		assertEquals(attributeValue, "attributeValue");
		String attributeValue2 = restDataGroup.getAttributes().get("attributeNameInData2");
		assertEquals(attributeValue2, "attributeValue2");
	}

	@Test
	public void testToClassWithAtomicChild() {
		String json = "{\"name\":\"groupNameInData\","
				+ "\"children\":[{\"name\":\"atomicNameInData\",\"value\":\"atomicValue\"}]}";

		RestDataGroup restDataGroup = createRestDataGroupForJsonString(json);
		assertEquals(restDataGroup.getNameInData(), "groupNameInData");
		RestDataAtomic child = (RestDataAtomic) restDataGroup.getChildren().iterator().next();
		assertEquals(child.getNameInData(), "atomicNameInData");
		assertEquals(child.getValue(), "atomicValue");
	}

	@Test
	public void testToClassGroupWithAtomicChildAndGroupChildWithAtomicChild() {
		String json = "{";
		json += "\"name\":\"groupNameInData\",";
		json += "\"children\":[";
		json += "{\"name\":\"atomicNameInData\",\"value\":\"atomicValue\"},";
		json += "{\"name\":\"groupNameInData2\","
				+ "\"children\":[{\"name\":\"atomicNameInData2\",\"value\":\"atomicValue2\"}]}";
		json += "]";
		json += "}";

		RestDataGroup restDataGroup = createRestDataGroupForJsonString(json);
		assertEquals(restDataGroup.getNameInData(), "groupNameInData");
		Iterator<RestDataElement> iterator = restDataGroup.getChildren().iterator();
		RestDataAtomic child = (RestDataAtomic) iterator.next();
		assertEquals(child.getNameInData(), "atomicNameInData");
		assertEquals(child.getValue(), "atomicValue");
		RestDataGroup child2 = (RestDataGroup) iterator.next();
		assertEquals(child2.getNameInData(), "groupNameInData2");
		RestDataAtomic subChild = (RestDataAtomic) child2.getChildren().iterator().next();
		assertEquals(subChild.getNameInData(), "atomicNameInData2");
		assertEquals(subChild.getValue(), "atomicValue2");
	}

	@Test
	public void testToClassGroupWithAttributesAndAtomicChildAndGroupChildWithAtomicChild() {
		String json = "{";
		json += "\"name\":\"groupNameInData\",";
		json += "\"attributes\":{" + "\"attributeNameInData\":\"attributeValue\","
				+ "\"attributeNameInData2\":\"attributeValue2\"" + "},";
		json += "\"children\":[";
		json += "{\"name\":\"atomicNameInData\",\"value\":\"atomicValue\"},";
		json += "{\"name\":\"groupNameInData2\",";
		json += "\"attributes\":{\"g2AttributeNameInData\":\"g2AttributeValue\"},";
		json += "\"children\":[{\"name\":\"atomicNameInData2\",\"value\":\"atomicValue2\"}]}";
		json += "]";
		json += "}";

		RestDataGroup restDataGroup = createRestDataGroupForJsonString(json);
		assertEquals(restDataGroup.getNameInData(), "groupNameInData");

		String attributeValue2 = restDataGroup.getAttributes().get("attributeNameInData");
		assertEquals(attributeValue2, "attributeValue");

		Iterator<RestDataElement> iterator = restDataGroup.getChildren().iterator();
		RestDataAtomic child = (RestDataAtomic) iterator.next();
		assertEquals(child.getNameInData(), "atomicNameInData");
		assertEquals(child.getValue(), "atomicValue");
		RestDataGroup child2 = (RestDataGroup) iterator.next();
		assertEquals(child2.getNameInData(), "groupNameInData2");
		RestDataAtomic subChild = (RestDataAtomic) child2.getChildren().iterator().next();
		assertEquals(subChild.getNameInData(), "atomicNameInData2");
		assertEquals(subChild.getValue(), "atomicValue2");

		String attributeValue = child2.getAttributes().get("g2AttributeNameInData");
		assertEquals(attributeValue, "g2AttributeValue");
	}

	private RestDataGroup createRestDataGroupForJsonString(String json) {
		JsonValue jsonValue = jsonParser.parseString(json);
		JsonToDataConverter jsonToDataConverter = jsonToDataConverterFactory
				.createForJsonObject(jsonValue);
		RestDataElement restDataElement = jsonToDataConverter.toInstance();
		RestDataGroup restDataGroup = (RestDataGroup) restDataElement;
		return restDataGroup;
	}

	@Test(expectedExceptions = JsonParseException.class)
	public void testToClassWrongJsonTopLevelNoName() {
		String json = "{\"children\":[],\"extra\":{\"id2\":\"value2\"}}";
		JsonValue jsonValue = jsonParser.parseString(json);
		JsonToDataConverter jsonToDataConverter = jsonToDataConverterFactory
				.createForJsonObject(jsonValue);
		jsonToDataConverter.toInstance();
	}

	@Test(expectedExceptions = JsonParseException.class)
	public void testToClassWrongJsonTopLevelNoChildren() {
		String json = "{\"name\":\"id\",\"attributes\":{}}";
		JsonValue jsonValue = jsonParser.parseString(json);

		JsonToDataConverter jsonToDataConverter = JsonToDataGroupConverter
				.forJsonObject((JsonObject) jsonValue);
		jsonToDataConverter.toInstance();
	}

	@Test(expectedExceptions = JsonParseException.class)
	public void testToClassWrongJsonKeyTopLevel() {
		String json = "{\"name\":\"id\",\"children\":[],\"extra\":{\"id2\":\"value2\"}}";
		JsonValue jsonValue = jsonParser.parseString(json);
		JsonToDataConverter jsonToDataConverter = jsonToDataConverterFactory
				.createForJsonObject(jsonValue);
		jsonToDataConverter.toInstance();
	}

	@Test(expectedExceptions = JsonParseException.class)
	public void testToClassWrongJsonKeyTopLevelWithAttributes() {
		String json = "{\"name\":\"id\",\"children\":[], \"attributes\":{},\"extra\":{\"id2\":\"value2\"}}";
		JsonValue jsonValue = jsonParser.parseString(json);
		JsonToDataConverter jsonToDataConverter = jsonToDataConverterFactory
				.createForJsonObject(jsonValue);
		jsonToDataConverter.toInstance();
	}

	@Test(expectedExceptions = JsonParseException.class)
	public void testToClassWrongJsonAttributesIsGroup() {
		String json = "{\"name\":\"groupNameInData\", \"attributes\":{\"attributeNameInData\":\"attributeValue\",\"bla\":{} }}";
		JsonValue jsonValue = jsonParser.parseString(json);
		JsonToDataConverter jsonToDataConverter = jsonToDataConverterFactory
				.createForJsonObject(jsonValue);
		jsonToDataConverter.toInstance();
	}

	@Test(expectedExceptions = JsonParseException.class)
	public void testToClassWrongJsonTwoAttributes() {
		String json = "{\"name\":\"groupNameInData\",\"children\":[],\"attributes\":{\"attributeNameInData\":\"attributeValue\"}"
				+ ",\"attributes\":{\"attributeNameInData2\":\"attributeValue2\"}}";
		JsonValue jsonValue = jsonParser.parseString(json);
		JsonToDataConverter jsonToDataConverter = jsonToDataConverterFactory
				.createForJsonObject(jsonValue);
		RestDataElement class1 = jsonToDataConverter.toInstance();
		assertNotNull(class1);
	}

	@Test(expectedExceptions = JsonParseException.class)
	public void testToClassWrongJsonOneAttributesIsArray() {
		String json = "{\"name\":\"groupNameInData\",\"children\":[],\"attributes\":{\"attributeNameInData\":\"attributeValue\",\"bla\":[true] }}";
		JsonValue jsonValue = jsonParser.parseString(json);
		JsonToDataConverter jsonToDataConverter = jsonToDataConverterFactory
				.createForJsonObject(jsonValue);
		jsonToDataConverter.toInstance();
	}

	@Test(expectedExceptions = JsonParseException.class)
	public void testToClassWrongJsonAttributesIsArray() {
		String json = "{\"name\":\"groupNameInData\",\"children\":[],\"attributes\":[{\"attributeNameInData\":\"attributeValue\"}]}";
		JsonValue jsonValue = jsonParser.parseString(json);
		JsonToDataConverter jsonToDataConverter = jsonToDataConverterFactory
				.createForJsonObject(jsonValue);
		jsonToDataConverter.toInstance();
	}

	@Test(expectedExceptions = JsonParseException.class)
	public void testToClassWrongJsonOneChildIsArray() {
		String json = "{\"name\":\"groupNameInData\",\"children\":[{\"atomicNameInData\":\"atomicValue\"},[]]}";
		JsonValue jsonValue = jsonParser.parseString(json);
		JsonToDataConverter jsonToDataConverter = jsonToDataConverterFactory
				.createForJsonObject(jsonValue);
		jsonToDataConverter.toInstance();
	}

	@Test(expectedExceptions = JsonParseException.class)
	public void testToClassWrongJsonOneChildIsString() {
		String json = "{\"name\":\"groupNameInData\",\"children\":[{\"atomicNameInData\":\"atomicValue\"},\"string\"]}";
		JsonValue jsonValue = jsonParser.parseString(json);
		JsonToDataConverter jsonToDataConverter = jsonToDataConverterFactory
				.createForJsonObject(jsonValue);
		jsonToDataConverter.toInstance();
	}

	@Test(expectedExceptions = JsonParseException.class)
	public void testToClassWrongJsonChildrenIsNotCorrectObject() {
		String json = "{\"name\":\"groupNameInData\",\"children\":[{\"atomicNameInData\":\"atomicValue\""
				+ ",\"atomicNameInData2\":\"atomicValue2\"}]}";
		JsonValue jsonValue = jsonParser.parseString(json);
		JsonToDataConverter jsonToDataConverter = jsonToDataConverterFactory
				.createForJsonObject(jsonValue);
		jsonToDataConverter.toInstance();
	}
}
