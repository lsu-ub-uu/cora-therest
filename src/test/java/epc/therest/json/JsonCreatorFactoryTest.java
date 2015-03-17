package epc.therest.json;

import org.testng.Assert;
import org.testng.annotations.Test;

import epc.spider.data.SpiderDataAtomic;
import epc.spider.data.SpiderDataGroup;
import epc.therest.data.RestDataElement;
import epc.therest.data.RestDataAtomic;
import epc.therest.data.RestDataGroup;

public class JsonCreatorFactoryTest {
	@Test
	public void testJsonCreatorFactoryDataGroup() {
		JsonCreatorFactory jsonCreatorFactory = new JsonCreatorFactoryImp();
		SpiderDataGroup dataGroup = SpiderDataGroup.withDataId("groupDataId");
		RestDataElement restDataElement = RestDataGroup.fromDataGroup(dataGroup);

		JsonCreator jsonCreator = jsonCreatorFactory.createForRestDataElement(restDataElement);

		Assert.assertTrue(jsonCreator instanceof DataGroupJsonCreator);
	}

	@Test
	public void testJsonCreatorFactoryDataAtomic() {
		JsonCreatorFactory jsonCreatorFactory = new JsonCreatorFactoryImp();
		SpiderDataAtomic dataAtomic = SpiderDataAtomic.withDataIdAndValue("atomicDataId",
				"atomicValue");
		RestDataElement restDataElement = RestDataAtomic.fromSpiderDataAtomic(dataAtomic);

		JsonCreator jsonCreator = jsonCreatorFactory.createForRestDataElement(restDataElement);

		Assert.assertTrue(jsonCreator instanceof DataAtomicJsonCreator);
	}
}
