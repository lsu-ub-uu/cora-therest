package epc.therest.json;

import org.testng.Assert;
import org.testng.annotations.Test;

import epc.spider.data.SpiderDataAtomic;
import epc.therest.data.DataElementRest;
import epc.therest.data.RestDataAtomic;

public class DataAtomicJsonCreatorTest {
	@Test
	public void testToJson() {
		JsonCreatorFactory jsonCreatorFactory = new JsonCreatorFactoryImp();
		SpiderDataAtomic dataAtomic = SpiderDataAtomic.withDataIdAndValue("atomicDataId",
				"atomicValue");
		DataElementRest dataElementRest = RestDataAtomic.fromSpiderDataAtomic(dataAtomic);

		JsonCreator jsonCreator = jsonCreatorFactory.createForDataElementRest(dataElementRest);
		String json = jsonCreator.toJson();

		Assert.assertEquals(json, "{\"atomicDataId\":\"atomicValue\"}");
	}

	@Test
	public void testToJsonEmptyValue() {
		JsonCreatorFactory jsonCreatorFactory = new JsonCreatorFactoryImp();
		SpiderDataAtomic dataAtomic = SpiderDataAtomic.withDataIdAndValue("atomicDataId", "");
		DataElementRest dataElementRest = RestDataAtomic.fromSpiderDataAtomic(dataAtomic);

		JsonCreator jsonCreator = jsonCreatorFactory.createForDataElementRest(dataElementRest);
		String json = jsonCreator.toJson();

		Assert.assertEquals(json, "{\"atomicDataId\":\"\"}");
	}
}
