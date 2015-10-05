package se.uu.ub.cora.therest.data.converter;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.therest.data.RestDataAtomic;
import se.uu.ub.cora.therest.data.RestDataElement;
import se.uu.ub.cora.therest.data.converter.JsonToDataConverter;
import se.uu.ub.cora.therest.data.converter.JsonToDataConverterFactory;
import se.uu.ub.cora.therest.data.converter.JsonToDataConverterFactoryImp;
import se.uu.ub.cora.therest.json.parser.JsonParseException;
import se.uu.ub.cora.therest.json.parser.JsonParser;
import se.uu.ub.cora.therest.json.parser.JsonValue;
import se.uu.ub.cora.therest.json.parser.org.OrgJsonParser;

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
		String json = "{\"atomicNameInData\":\"atomicValue\"}";
		RestDataAtomic restDataAtomic = createRestDataAtomicForJsonString(json);
		Assert.assertEquals(restDataAtomic.getNameInData(), "atomicNameInData");
		Assert.assertEquals(restDataAtomic.getValue(), "atomicValue");
	}

	@Test
	public void testToClassEmptyValue() {
		String json = "{\"atomicNameInData\":\"\"}";
		RestDataAtomic restDataAtomic = createRestDataAtomicForJsonString(json);
		Assert.assertEquals(restDataAtomic.getNameInData(), "atomicNameInData");
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
		String json = "{\"atomicNameInData\":\"atomicValue\",\"id2\":\"value2\"}";
		JsonValue jsonValue = jsonParser.parseString(json);
		JsonToDataConverter jsonToDataConverter = jsonToDataConverterFactory
				.createForJsonObject(jsonValue);
		jsonToDataConverter.toInstance();
	}

	@Test(expectedExceptions = JsonParseException.class)
	public void testToClassWrongJsonExtraArray() {
		String json = "{\"atomicNameInData\":\"atomicValue\",\"id2\":[]}";
		JsonValue jsonValue = jsonParser.parseString(json);
		JsonToDataConverter jsonToDataConverter = jsonToDataConverterFactory
				.createForJsonObject(jsonValue);
		jsonToDataConverter.toInstance();
	}

}
