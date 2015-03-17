package epc.therest.json;

import static org.testng.Assert.assertTrue;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import epc.therest.data.converter.JsonToDataConverter;
import epc.therest.data.converter.JsonToDataConverterFactory;
import epc.therest.data.converter.JsonToDataConverterFactoryImp;
import epc.therest.data.converter.JsonToDataAtomicConverter;
import epc.therest.data.converter.JsonToDataGroupConverter;
import epc.therest.jsonparser.JsonParseException;
import epc.therest.jsonparser.JsonParser;
import epc.therest.jsonparser.JsonValue;
import epc.therest.jsonparser.javax.JavaxJsonParser;

public class ClassCreatorFactoryTest {
	// @Test(expectedExceptions = JsonParseException.class)
	// public void testFactorOnJsonStringWrongJson() {
	// String json = "[]";
	// jsonParser.parseString(json);
	// }

	private JsonToDataConverterFactory jsonToDataConverterFactory;
	private JsonParser jsonParser;

	@BeforeMethod
	public void beforeMethod() {
		jsonToDataConverterFactory = new JsonToDataConverterFactoryImp();
		jsonParser = new JavaxJsonParser();

	}

	@Test
	public void testFactorOnJsonStringDataGroup() {
		String json = "{\"groupDataId\":{}}";
		JsonValue jsonValue = jsonParser.parseString(json);
		JsonToDataConverter jsonToDataConverter = jsonToDataConverterFactory.createForJsonObject(jsonValue);
		assertTrue(jsonToDataConverter instanceof JsonToDataGroupConverter);
	}

	@Test
	public void testFactorOnJsonStringDataAtomic() {
		String json = "{\"atomicDataId\":\"atomicValue\"}";
		JsonValue jsonValue = jsonParser.parseString(json);
		JsonToDataConverter jsonToDataConverter = jsonToDataConverterFactory.createForJsonObject(jsonValue);
		assertTrue(jsonToDataConverter instanceof JsonToDataAtomicConverter);
	}

	@Test(expectedExceptions = JsonParseException.class)
	public void testFactorOnJsonObjectNullJson() {
		jsonToDataConverterFactory.createForJsonObject(null);
	}

	@Test
	public void testClassCreatorAtomic() {
		String json = "{\"id\":\"value\"}";
		JsonValue jsonValue = jsonParser.parseString(json);
		JsonToDataConverter jsonToDataConverter = jsonToDataConverterFactory.createForJsonObject(jsonValue);
		assertTrue(jsonToDataConverter instanceof JsonToDataAtomicConverter);
	}

	@Test
	public void testClassCreatorGroup() {
		String json = "{\"id\":{\"id2\":\"value\"}}";
		JsonValue jsonValue = jsonParser.parseString(json);
		JsonToDataConverter jsonToDataConverter = jsonToDataConverterFactory.createForJsonObject(jsonValue);
		assertTrue(jsonToDataConverter instanceof JsonToDataGroupConverter);
	}

	@Test(expectedExceptions = JsonParseException.class)
	public void testClassCreatorGroupNotAGroup() {
		String json = "[{\"id\":{\"id2\":\"value\"}}]";
		JsonValue jsonValue = jsonParser.parseString(json);
		jsonToDataConverterFactory.createForJsonObject(jsonValue);
	}

	@Test(expectedExceptions = JsonParseException.class)
	public void testClassCreatorGroupWithTwoTopLevel() {
		String json = "{\"id\":{\"id2\":\"value\"},\"id4\":{\"id3\":\"value\"}}";
		JsonValue jsonValue = jsonParser.parseString(json);
		JsonToDataConverter jsonToDataConverter = jsonToDataConverterFactory.createForJsonObject(jsonValue);
		jsonToDataConverter.toInstance();
	}
}
