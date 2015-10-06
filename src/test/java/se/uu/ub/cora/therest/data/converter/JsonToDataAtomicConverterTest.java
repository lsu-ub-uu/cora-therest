package se.uu.ub.cora.therest.data.converter;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.therest.data.RestDataAtomic;
import se.uu.ub.cora.therest.data.RestDataElement;
import se.uu.ub.cora.therest.json.parser.JsonObject;
import se.uu.ub.cora.therest.json.parser.JsonParseException;
import se.uu.ub.cora.therest.json.parser.JsonParser;
import se.uu.ub.cora.therest.json.parser.JsonValue;
import se.uu.ub.cora.therest.json.parser.org.OrgJsonParser;

public class JsonToDataAtomicConverterTest {
	private JsonParser jsonParser;

	@BeforeMethod
	public void beforeMethod() {
		jsonParser = new OrgJsonParser();
	}

	@Test
	public void testToClass() {
		String json = "{\"name\":\"atomicNameInData\",\"value\":\"atomicValue\"}";
		RestDataAtomic restDataAtomic = createRestDataAtomicForJsonString(json);
		Assert.assertEquals(restDataAtomic.getNameInData(), "atomicNameInData");
		Assert.assertEquals(restDataAtomic.getValue(), "atomicValue");
	}

	private RestDataAtomic createRestDataAtomicForJsonString(String json) {
		JsonValue jsonValue = jsonParser.parseString(json);
		JsonToDataConverter jsonToDataConverter = JsonToDataAtomicConverter
				.forJsonObject((JsonObject) jsonValue);
		RestDataElement restDataElement = jsonToDataConverter.toInstance();

		RestDataAtomic restDataAtomic = (RestDataAtomic) restDataElement;
		return restDataAtomic;
	}

	@Test
	public void testToClassEmptyValue() {
		String json = "{\"name\":\"atomicNameInData\",\"value\":\"\"}";
		RestDataAtomic restDataAtomic = createRestDataAtomicForJsonString(json);
		Assert.assertEquals(restDataAtomic.getNameInData(), "atomicNameInData");
		Assert.assertEquals(restDataAtomic.getValue(), "");
	}

	@Test(expectedExceptions = JsonParseException.class)
	public void testToClassWrongJsonValueIsNotString() {
		String json = "{\"name\":\"id\",\"value\":[]}";
		createRestDataAtomicForJsonString(json);
	}

	@Test(expectedExceptions = JsonParseException.class)
	public void testToClassWrongJsonNameIsNotString() {
		String json = "{\"name\":{},\"value\":\"atomicValue\"}";
		createRestDataAtomicForJsonString(json);
	}

	@Test(expectedExceptions = JsonParseException.class)
	public void testToClassWrongJsonNotName() {
		String json = "{\"nameNOT\":\"id\",\"value\":\"atomicValue\"}";
		createRestDataAtomicForJsonString(json);
	}

	@Test(expectedExceptions = JsonParseException.class)
	public void testToClassWrongJsonMissingValue() {
		String json = "{\"name\":\"id\",\"valueNOT\":\"atomicValue\"}";
		createRestDataAtomicForJsonString(json);
	}

	@Test(expectedExceptions = JsonParseException.class)
	public void testToClassWrongJsonExtraKey() {
		String json = "{\"name\":\"id\",\"value\":\"atomicValue\",\"extra\":\"extra\"}";
		createRestDataAtomicForJsonString(json);
	}

	@Test(expectedExceptions = JsonParseException.class)
	public void testToClassWrongJsonExtraKeyValuePair() {
		String json = "{\"name\":\"atomicNameInData\",\"value\":\"atomicValue\","
				+ "\"name\":\"id2\",\"value\":\"value2\"}";
		createRestDataAtomicForJsonString(json);
	}

	@Test(expectedExceptions = JsonParseException.class)
	public void testToClassWrongJsonExtraArray() {
		String json = "{\"name\":\"atomicNameInData\",\"value\":\"atomicValue\","
				+ "\"name\":\"id2\",\"value\":[]}";
		createRestDataAtomicForJsonString(json);
	}

}
