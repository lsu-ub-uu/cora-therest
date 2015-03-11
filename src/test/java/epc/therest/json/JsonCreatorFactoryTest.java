package epc.therest.json;

import org.testng.Assert;
import org.testng.annotations.Test;

import epc.metadataformat.data.DataAtomic;
import epc.metadataformat.data.DataGroup;
import epc.therest.data.DataAtomicRest;
import epc.therest.data.DataElementRest;
import epc.therest.data.DataGroupRest;

public class JsonCreatorFactoryTest {
	@Test
	public void testJsonCreatorFactoryDataGroup() {
		JsonCreatorFactory jsonCreatorFactory = new JsonCreatorFactoryImp();
		DataGroup dataGroup = DataGroup.withDataId("groupDataId");
		DataElementRest dataElementRest = DataGroupRest.fromDataGroup(dataGroup);

		JsonCreator jsonCreator = jsonCreatorFactory.factorOnDataElementRest(dataElementRest);

		Assert.assertTrue(jsonCreator instanceof DataGroupJsonCreator);
	}

	@Test
	public void testJsonCreatorFactoryDataAtomic() {
		JsonCreatorFactory jsonCreatorFactory = new JsonCreatorFactoryImp();
		DataAtomic dataAtomic = DataAtomic.withDataIdAndValue("atomicDataId", "atomicValue");
		DataElementRest dataElementRest = DataAtomicRest.fromDataAtomic(dataAtomic);

		JsonCreator jsonCreator = jsonCreatorFactory.factorOnDataElementRest(dataElementRest);

		Assert.assertTrue(jsonCreator instanceof DataAtomicJsonCreator);
	}
}
