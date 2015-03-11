package epc.therest.json;

import org.testng.Assert;
import org.testng.annotations.Test;

import epc.therest.data.DataAtomicRest;
import epc.therest.data.DataElementRest;

public class DataAtomicClassCreatorTest {
	@Test
	public void testToClass() {
		ClassCreatorFactory classCreatorFactory = new ClassCreatorFactoryImp();
		String json = "{\"atomicDataId\":\"atomicValue\"}";

		ClassCreator classCreator = classCreatorFactory.factorOnJsonString(json);

		DataElementRest dataElementRest = classCreator.toClass();
		DataAtomicRest dataAtomicRest = (DataAtomicRest) dataElementRest;
		Assert.assertEquals(dataAtomicRest.getDataId(), "atomicDataId");
		Assert.assertEquals(dataAtomicRest.getValue(), "atomicValue");
	}

	@Test
	public void testToClassEmptyValue() {
		ClassCreatorFactory classCreatorFactory = new ClassCreatorFactoryImp();
		String json = "{\"atomicDataId\":\"\"}";

		ClassCreator classCreator = classCreatorFactory.factorOnJsonString(json);

		DataElementRest dataElementRest = classCreator.toClass();
		DataAtomicRest dataAtomicRest = (DataAtomicRest) dataElementRest;
		Assert.assertEquals(dataAtomicRest.getDataId(), "atomicDataId");
		Assert.assertEquals(dataAtomicRest.getValue(), "");
	}
}
