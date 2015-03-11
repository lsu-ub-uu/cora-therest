package epc.therest.json;

import static org.testng.Assert.assertTrue;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonReaderFactory;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class ClassCreatorFactoryTest {
	private ClassCreatorFactory classCreatorFactory;

	@BeforeMethod
	public void beforeMethod() {
		classCreatorFactory = new ClassCreatorFactoryImp();
	}

	@Test(expectedExceptions = JsonParseException.class)
	public void testFactorOnJsonStringNullJson() {
		String json = null;
		classCreatorFactory.factorOnJsonString(json);
	}

	@Test(expectedExceptions = JsonParseException.class)
	public void testFactorOnJsonStringEmptyJson() {
		String json = "";
		classCreatorFactory.factorOnJsonString(json);
	}

	@Test(expectedExceptions = JsonParseException.class)
	public void testFactorOnJsonStringWrongJson() {
		String json = "[]";
		classCreatorFactory.factorOnJsonString(json);
	}

	@Test(expectedExceptions = JsonParseException.class)
	public void testFactorOnJsonStringBrokenJson() {
		String json = "{";
		classCreatorFactory.factorOnJsonString(json);
	}

	@Test
	public void testFactorOnJsonStringDataGroup() {
		String json = "{\"groupDataId\":{}}";
		ClassCreator classCreator = classCreatorFactory.factorOnJsonString(json);
		assertTrue(classCreator instanceof DataGroupClassCreator);
	}

	@Test
	public void testFactorOnJsonStringDataAtomic() {
		String json = "{\"atomicDataId\":\"atomicValue\"}";
		ClassCreator classCreator = classCreatorFactory.factorOnJsonString(json);
		assertTrue(classCreator instanceof DataAtomicClassCreator);
	}

	@Test(expectedExceptions = JsonParseException.class)
	public void testFactorOnJsonObjectNullJson() {
		classCreatorFactory.factorOnJsonObject(null);
	}

	@Test
	public void testFactorOnJsonObjectDataGroup() {
		JsonObject jsonObject = createJsonObjectForJsonString("{\"groupDataId\":{}}");
		ClassCreator classCreator = classCreatorFactory.factorOnJsonObject(jsonObject);
		assertTrue(classCreator instanceof DataGroupClassCreator);
	}

	@Test
	public void testFactorOnJsonObjectDataAtomic() {
		JsonObject jsonObject = createJsonObjectForJsonString("{\"atomicDataId\":\"atomicValue\"}");
		ClassCreator classCreator = classCreatorFactory.factorOnJsonObject(jsonObject);
		assertTrue(classCreator instanceof DataAtomicClassCreator);
	}

	private JsonObject createJsonObjectForJsonString(String json) {
		Map<String, Object> config = new HashMap<>();
		JsonReaderFactory jsonReaderFactory = Json.createReaderFactory(config);
		JsonReader jsonReader = jsonReaderFactory.createReader(new StringReader(json));
		JsonObject jsonObject = jsonReader.readObject();
		return jsonObject;
	}

}
