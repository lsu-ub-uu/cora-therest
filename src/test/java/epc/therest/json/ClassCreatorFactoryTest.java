package epc.therest.json;

import static org.testng.Assert.assertTrue;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

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

	private ClassCreatorFactory classCreatorFactory;
	private JsonParser jsonParser;

	@BeforeMethod
	public void beforeMethod() {
		classCreatorFactory = new ClassCreatorFactoryImp();
		jsonParser = new JavaxJsonParser();

	}

	@Test
	public void testFactorOnJsonStringDataGroup() {
		String json = "{\"groupDataId\":{}}";
		JsonValue jsonValue = jsonParser.parseString(json);
		ClassCreator classCreator = classCreatorFactory.createForJsonObject(jsonValue);
		assertTrue(classCreator instanceof DataGroupClassCreator);
	}

	@Test
	public void testFactorOnJsonStringDataAtomic() {
		String json = "{\"atomicDataId\":\"atomicValue\"}";
		JsonValue jsonValue = jsonParser.parseString(json);
		ClassCreator classCreator = classCreatorFactory.createForJsonObject(jsonValue);
		assertTrue(classCreator instanceof DataAtomicClassCreator);
	}

	@Test(expectedExceptions = JsonParseException.class)
	public void testFactorOnJsonObjectNullJson() {
		classCreatorFactory.createForJsonObject(null);
	}

	@Test
	public void testClassCreatorAtomic() {
		String json = "{\"id\":\"value\"}";
		JsonValue jsonValue = jsonParser.parseString(json);
		ClassCreator classCreator = classCreatorFactory.createForJsonObject(jsonValue);
		assertTrue(classCreator instanceof DataAtomicClassCreator);
	}

	@Test
	public void testClassCreatorGroup() {
		String json = "{\"id\":{\"id2\":\"value\"}}";
		JsonValue jsonValue = jsonParser.parseString(json);
		ClassCreator classCreator = classCreatorFactory.createForJsonObject(jsonValue);
		assertTrue(classCreator instanceof DataGroupClassCreator);
	}

	@Test(expectedExceptions = JsonParseException.class)
	public void testClassCreatorGroupNotAGroup() {
		String json = "[{\"id\":{\"id2\":\"value\"}}]";
		JsonValue jsonValue = jsonParser.parseString(json);
		classCreatorFactory.createForJsonObject(jsonValue);
	}

	@Test(expectedExceptions = JsonParseException.class)
	public void testClassCreatorGroupWithTwoTopLevel() {
		String json = "{\"id\":{\"id2\":\"value\"},\"id4\":{\"id3\":\"value\"}}";
		JsonValue jsonValue = jsonParser.parseString(json);
		ClassCreator classCreator = classCreatorFactory.createForJsonObject(jsonValue);
		classCreator.toInstance();
	}
}
