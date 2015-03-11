package epc.therest.json;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import epc.therest.data.DataAtomicRest;
import epc.therest.data.DataElementRest;

public class DataAtomicClassCreatorTest {
	private ClassCreatorFactory classCreatorFactory;

	@BeforeMethod
	public void beforeMethod() {
		classCreatorFactory = new ClassCreatorFactoryImp();
	}

	@Test
	public void testToClass() {
		String json = "{\"atomicDataId\":\"atomicValue\"}";
		DataAtomicRest dataAtomicRest = createDataAtomicRestForJsonString(json);
		Assert.assertEquals(dataAtomicRest.getDataId(), "atomicDataId");
		Assert.assertEquals(dataAtomicRest.getValue(), "atomicValue");
	}

	@Test
	public void testToClassEmptyValue() {
		String json = "{\"atomicDataId\":\"\"}";
		DataAtomicRest dataAtomicRest = createDataAtomicRestForJsonString(json);
		Assert.assertEquals(dataAtomicRest.getDataId(), "atomicDataId");
		Assert.assertEquals(dataAtomicRest.getValue(), "");
	}

	private DataAtomicRest createDataAtomicRestForJsonString(String json) {
		ClassCreator classCreator = classCreatorFactory.factorOnJsonString(json);
		DataElementRest dataElementRest = classCreator.toClass();
		DataAtomicRest dataAtomicRest = (DataAtomicRest) dataElementRest;
		return dataAtomicRest;
	}

	@Test(expectedExceptions = JsonParseException.class)
	public void testToClassWrongJson() {
		String json = "{\"id\":[]}";
		ClassCreator classCreator = classCreatorFactory.factorOnJsonString(json);
		classCreator.toClass();
	}

	@Test(expectedExceptions = JsonParseException.class)
	public void testToClassWrongJsonExtraKeyValuePair() {
		String json = "{\"atomicDataId\":\"atomicValue\",\"id2\":\"value2\"}";
		ClassCreator classCreator = classCreatorFactory.factorOnJsonString(json);
		classCreator.toClass();
	}

	@Test(expectedExceptions = JsonParseException.class)
	public void testToClassWrongJsonExtraArray() {
		String json = "{\"atomicDataId\":\"atomicValue\",\"id2\":[]}";
		ClassCreator classCreator = classCreatorFactory.factorOnJsonString(json);
		classCreator.toClass();
	}

}
