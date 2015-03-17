package epc.therest.json;

import org.testng.Assert;
import org.testng.annotations.Test;

import epc.spider.data.SpiderDataAtomic;
import epc.therest.data.RestDataElement;
import epc.therest.data.RestDataAtomic;

public class DataAtomicToJsonConverterTest {
	@Test
	public void testToJson() {
		DataToJsonConverterFactory dataToJsonConverterFactory = new DataToJsonConverterFactoryImp();
		SpiderDataAtomic dataAtomic = SpiderDataAtomic.withDataIdAndValue("atomicDataId",
				"atomicValue");
		RestDataElement restDataElement = RestDataAtomic.fromSpiderDataAtomic(dataAtomic);

		DataToJsonConverter dataToJsonConverter = dataToJsonConverterFactory.createForRestDataElement(restDataElement);
		String json = dataToJsonConverter.toJson();

		Assert.assertEquals(json, "{\"atomicDataId\":\"atomicValue\"}");
	}

	@Test
	public void testToJsonEmptyValue() {
		DataToJsonConverterFactory dataToJsonConverterFactory = new DataToJsonConverterFactoryImp();
		SpiderDataAtomic dataAtomic = SpiderDataAtomic.withDataIdAndValue("atomicDataId", "");
		RestDataElement restDataElement = RestDataAtomic.fromSpiderDataAtomic(dataAtomic);

		DataToJsonConverter dataToJsonConverter = dataToJsonConverterFactory.createForRestDataElement(restDataElement);
		String json = dataToJsonConverter.toJson();

		Assert.assertEquals(json, "{\"atomicDataId\":\"\"}");
	}
}
