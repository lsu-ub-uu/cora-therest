package epc.therest.json;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import epc.therest.data.RestDataAtomic;
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
		ClassCreator classCreator = classCreatorFactory.createForJsonString(json);
		DataElementRest dataElementRest = classCreator.toClass();
		RestDataAtomic restDataAtomic = (RestDataAtomic) dataElementRest;
		return restDataAtomic;
	}

	@Test(expectedExceptions = JsonParseException.class)
	public void testToClassWrongJson() {
		String json = "{\"id\":[]}";
		ClassCreator classCreator = classCreatorFactory.createForJsonString(json);
		classCreator.toClass();
	}

	@Test(expectedExceptions = JsonParseException.class)
	public void testToClassWrongJsonExtraKeyValuePair() {
		String json = "{\"atomicDataId\":\"atomicValue\",\"id2\":\"value2\"}";
		ClassCreator classCreator = classCreatorFactory.createForJsonString(json);
		classCreator.toClass();
	}

	@Test(expectedExceptions = JsonParseException.class)
	public void testToClassWrongJsonExtraArray() {
		String json = "{\"atomicDataId\":\"atomicValue\",\"id2\":[]}";
		ClassCreator classCreator = classCreatorFactory.createForJsonString(json);
		classCreator.toClass();
	}

}
