package epc.therest.json;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import epc.therest.data.RestDataAtomic;
import epc.therest.data.RestDataElement;
import epc.therest.data.converter.JsonToDataConverter;
import epc.therest.data.converter.JsonToDataConverterFactory;
import epc.therest.data.converter.JsonToDataConverterFactoryImp;
import epc.therest.jsonparser.JsonParseException;
import epc.therest.jsonparser.JsonParser;
import epc.therest.jsonparser.JsonValue;
import epc.therest.jsonparser.javax.JavaxJsonClassFactory;
import epc.therest.jsonparser.javax.JavaxJsonClassFactoryImp;
import epc.therest.jsonparser.javax.JavaxJsonParser;

public class DataAtomicClassCreatorTest {
	private JsonToDataConverterFactory jsonToDataConverterFactory;
	private JsonParser jsonParser;

	@BeforeMethod
	public void beforeMethod() {
		jsonToDataConverterFactory = new JsonToDataConverterFactoryImp();
		JavaxJsonClassFactory javaxJsonClassFactory = new JavaxJsonClassFactoryImp();
		jsonParser = new JavaxJsonParser(javaxJsonClassFactory);
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
