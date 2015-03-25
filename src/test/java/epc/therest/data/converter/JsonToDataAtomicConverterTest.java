package epc.therest.data.converter;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import epc.therest.data.RestDataAtomic;
import epc.therest.data.RestDataElement;
import epc.therest.json.parser.JsonParseException;
import epc.therest.json.parser.JsonParser;
import epc.therest.json.parser.JsonValue;
import epc.therest.json.parser.org.OrgJsonParser;

public class JsonToDataAtomicConverterTest {
	private JsonToDataConverterFactory jsonToDataConverterFactory;
	private JsonParser jsonParser;

	@BeforeMethod
	public void beforeMethod() {
		jsonToDataConverterFactory = new JsonToDataConverterFactoryImp();
		jsonParser = new OrgJsonParser();
	}

	@Test
	public void testToClass() {
		String json = "{\"atomicDataId\":\"atomicValue\"}";
		RestDataAtomic restDataAtomic = createRestDataAtomicForJsonString(json);
		Assert.assertEquals(restDataAtomic.getDataId(), "atomicDataId");
		Assert.assertEquals(restDataAtomic.getValue(), "atomicValue");
	}

	@Test
	public void testToClassEmptyValue() {
		String json = "{\"atomicDataId\":\"\"}";
		RestDataAtomic restDataAtomic = createRestDataAtomicForJsonString(json);
		Assert.assertEquals(restDataAtomic.getDataId(), "atomicDataId");
		Assert.assertEquals(restDataAtomic.getValue(), "");
	}

	private RestDataAtomic createRestDataAtomicForJsonString(String json) {
		JsonValue jsonValue = jsonParser.parseString(json);
		JsonToDataConverter jsonToDataConverter = jsonToDataConverterFactory
				.createForJsonObject(jsonValue);
		RestDataElement restDataElement = jsonToDataConverter.toInstance();
		RestDataAtomic restDataAtomic = (RestDataAtomic) restDataElement;
		return restDataAtomic;
	}

	@Test(expectedExceptions = JsonParseException.class)
	public void testToClassWrongJson() {
		String json = "{\"id\":[]}";

		JsonValue jsonValue = jsonParser.parseString(json);
		JsonToDataConverter jsonToDataConverter = jsonToDataConverterFactory
				.createForJsonObject(jsonValue);
		jsonToDataConverter.toInstance();
	}

	@Test(expectedExceptions = JsonParseException.class)
	public void testToClassWrongJsonExtraKeyValuePair() {
		String json = "{\"atomicDataId\":\"atomicValue\",\"id2\":\"value2\"}";
		JsonValue jsonValue = jsonParser.parseString(json);
		JsonToDataConverter jsonToDataConverter = jsonToDataConverterFactory
				.createForJsonObject(jsonValue);
		jsonToDataConverter.toInstance();
	}

	@Test(expectedExceptions = JsonParseException.class)
	public void testToClassWrongJsonExtraArray() {
		String json = "{\"atomicDataId\":\"atomicValue\",\"id2\":[]}";
		JsonValue jsonValue = jsonParser.parseString(json);
		JsonToDataConverter jsonToDataConverter = jsonToDataConverterFactory
				.createForJsonObject(jsonValue);
		jsonToDataConverter.toInstance();
	}

}
