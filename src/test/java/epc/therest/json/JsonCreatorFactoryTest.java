package epc.therest.json;

import org.testng.Assert;
import org.testng.annotations.Test;

import epc.spider.data.SpiderDataAtomic;
import epc.spider.data.SpiderDataGroup;
import epc.therest.data.DataElementRest;
import epc.therest.data.RestDataAtomic;
import epc.therest.data.RestDataGroup;

public class JsonCreatorFactoryTest {
	@Test
	public void testJsonCreatorFactoryDataGroup() {
		JsonCreatorFactory jsonCreatorFactory = new JsonCreatorFactoryImp();
		SpiderDataGroup dataGroup = SpiderDataGroup.withDataId("groupDataId");
		DataElementRest dataElementRest = RestDataGroup.fromDataGroup(dataGroup);

		JsonCreator jsonCreator = jsonCreatorFactory.createForDataElementRest(dataElementRest);

		Assert.assertTrue(jsonCreator instanceof DataGroupJsonCreator);
	}

	@Test
	public void testJsonCreatorFactoryDataAtomic() {
		JsonCreatorFactory jsonCreatorFactory = new JsonCreatorFactoryImp();
		SpiderDataAtomic dataAtomic = SpiderDataAtomic.withDataIdAndValue("atomicDataId",
				"atomicValue");
		DataElementRest dataElementRest = RestDataAtomic.fromSpiderDataAtomic(dataAtomic);

		JsonCreator jsonCreator = jsonCreatorFactory.createForDataElementRest(dataElementRest);

		Assert.assertTrue(jsonCreator instanceof DataAtomicJsonCreator);
	}
}
