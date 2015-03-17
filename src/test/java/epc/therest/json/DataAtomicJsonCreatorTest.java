package epc.therest.json;

import org.testng.Assert;
import org.testng.annotations.Test;

import epc.spider.data.SpiderDataAtomic;
import epc.therest.data.RestDataElement;
import epc.therest.data.RestDataAtomic;

public class DataAtomicJsonCreatorTest {
	@Test
	public void testToJson() {
		JsonCreatorFactory jsonCreatorFactory = new JsonCreatorFactoryImp();
		SpiderDataAtomic dataAtomic = SpiderDataAtomic.withDataIdAndValue("atomicDataId",
				"atomicValue");
		RestDataElement restDataElement = RestDataAtomic.fromSpiderDataAtomic(dataAtomic);

		JsonCreator jsonCreator = jsonCreatorFactory.createForRestDataElement(restDataElement);
		String json = jsonCreator.toJson();

		Assert.assertEquals(json, "{\"atomicDataId\":\"atomicValue\"}");
	}

	@Test
	public void testToJsonEmptyValue() {
		JsonCreatorFactory jsonCreatorFactory = new JsonCreatorFactoryImp();
		SpiderDataAtomic dataAtomic = SpiderDataAtomic.withDataIdAndValue("atomicDataId", "");
		RestDataElement restDataElement = RestDataAtomic.fromSpiderDataAtomic(dataAtomic);

		JsonCreator jsonCreator = jsonCreatorFactory.createForRestDataElement(restDataElement);
		String json = jsonCreator.toJson();

		Assert.assertEquals(json, "{\"atomicDataId\":\"\"}");
	}
}
