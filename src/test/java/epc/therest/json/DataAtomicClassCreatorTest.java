package epc.therest.json;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import epc.therest.data.DataElementRest;
import epc.therest.data.RestDataAtomic;
import epc.therest.jsonparser.JsonParseException;
import epc.therest.jsonparser.JsonParser;
import epc.therest.jsonparser.JsonValue;
import epc.therest.jsonparser.javax.JavaxJsonParser;

public class DataAtomicClassCreatorTest {
	private ClassCreatorFactory classCreatorFactory;
	private JsonParser jsonParser;

	@BeforeMethod
	public void beforeMethod() {
		classCreatorFactory = new ClassCreatorFactoryImp();
		jsonParser = new JavaxJsonParser();
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
		ClassCreator classCreator = classCreatorFactory.createForJsonObject(jsonValue);
		DataElementRest dataElementRest = classCreator.toInstance();
		RestDataAtomic restDataAtomic = (RestDataAtomic) dataElementRest;
		return restDataAtomic;
	}

	@Test(expectedExceptions = JsonParseException.class)
	public void testToClassWrongJson() {
		String json = "{\"id\":[]}";

		JsonValue jsonValue = jsonParser.parseString(json);
		ClassCreator classCreator = classCreatorFactory.createForJsonObject(jsonValue);
		classCreator.toInstance();
	}

	@Test(expectedExceptions = JsonParseException.class)
	public void testToClassWrongJsonExtraKeyValuePair() {
		String json = "{\"atomicDataId\":\"atomicValue\",\"id2\":\"value2\"}";
		JsonValue jsonValue = jsonParser.parseString(json);
		ClassCreator classCreator = classCreatorFactory.createForJsonObject(jsonValue);
		classCreator.toInstance();
	}

	@Test(expectedExceptions = JsonParseException.class)
	public void testToClassWrongJsonExtraArray() {
		String json = "{\"atomicDataId\":\"atomicValue\",\"id2\":[]}";
		JsonValue jsonValue = jsonParser.parseString(json);
		ClassCreator classCreator = classCreatorFactory.createForJsonObject(jsonValue);
		classCreator.toInstance();
	}

}
