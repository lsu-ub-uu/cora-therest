package se.uu.ub.cora.therest.data.converter;

import static org.testng.Assert.assertTrue;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.therest.data.converter.JsonToDataAtomicConverter;
import se.uu.ub.cora.therest.data.converter.JsonToDataConverter;
import se.uu.ub.cora.therest.data.converter.JsonToDataConverterFactory;
import se.uu.ub.cora.therest.data.converter.JsonToDataConverterFactoryImp;
import se.uu.ub.cora.therest.data.converter.JsonToDataGroupConverter;
import se.uu.ub.cora.therest.json.parser.JsonParseException;
import se.uu.ub.cora.therest.json.parser.JsonParser;
import se.uu.ub.cora.therest.json.parser.JsonValue;
import se.uu.ub.cora.therest.json.parser.org.OrgJsonParser;

public class JsonToDataConverterFactoryTest {
	private JsonToDataConverterFactory jsonToDataConverterFactory;
	private JsonParser jsonParser;

	@BeforeMethod
	public void beforeMethod() {
		jsonToDataConverterFactory = new JsonToDataConverterFactoryImp();
		jsonParser = new OrgJsonParser();
	}

	@Test
	public void testFactorOnJsonStringDataGroupEmptyChildren() {
		String json = "{\"name\":\"groupNameInData\", \"children\":[]}";
		JsonValue jsonValue = jsonParser.parseString(json);
		JsonToDataConverter jsonToDataConverter = jsonToDataConverterFactory
				.createForJsonObject(jsonValue);
		assertTrue(jsonToDataConverter instanceof JsonToDataGroupConverter);
	}

	@Test
	public void testFactorOnJsonStringDataGroupAtomicChild() {
		String json = "{\"name\":\"id\", \"children\":[{\"id2\":\"value\"}]}";
		JsonValue jsonValue = jsonParser.parseString(json);
		JsonToDataConverter jsonToDataConverter = jsonToDataConverterFactory
				.createForJsonObject(jsonValue);
		assertTrue(jsonToDataConverter instanceof JsonToDataGroupConverter);
	}

	@Test
	public void testFactorOnJsonStringDataAtomic() {
		String json = "{\"atomicNameInData\":\"atomicValue\"}";
		JsonValue jsonValue = jsonParser.parseString(json);
		JsonToDataConverter jsonToDataConverter = jsonToDataConverterFactory
				.createForJsonObject(jsonValue);
		assertTrue(jsonToDataConverter instanceof JsonToDataAtomicConverter);
	}

	@Test(expectedExceptions = JsonParseException.class)
	public void testFactorOnJsonObjectNullJson() {
		jsonToDataConverterFactory.createForJsonObject(null);
	}


	@Test(expectedExceptions = JsonParseException.class)
	public void testClassCreatorGroupNotAGroup() {
		String json = "[{\"id\":{\"id2\":\"value\"}}]";
		JsonValue jsonValue = jsonParser.parseString(json);
		jsonToDataConverterFactory.createForJsonObject(jsonValue);
	}

}
