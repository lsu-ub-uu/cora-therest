package epc.therest.data.converter;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import epc.spider.data.SpiderDataAtomic;
import epc.therest.data.RestDataElement;
import epc.therest.data.converter.spider.DataAtomicSpiderToRestConverter;
import epc.therest.json.builder.JsonBuilderFactory;
import epc.therest.json.builder.org.OrgJsonBuilderFactoryAdapter;

public class DataAtomicToJsonConverterTest {
	private DataToJsonConverterFactory dataToJsonConverterFactory;
	private JsonBuilderFactory factory;

	@BeforeMethod
	public void beforeMethod() {
		dataToJsonConverterFactory = new DataToJsonConverterFactoryImp();
		factory = new OrgJsonBuilderFactoryAdapter();

	}

	@Test
	public void testToJson() {
		SpiderDataAtomic dataAtomic = SpiderDataAtomic.withDataIdAndValue("atomicDataId",
				"atomicValue");
		DataAtomicSpiderToRestConverter converter = DataAtomicSpiderToRestConverter
				.fromSpiderDataAtomic(dataAtomic);
		RestDataElement restDataElement = converter.toRest();

		DataToJsonConverter dataToJsonConverter = dataToJsonConverterFactory
				.createForRestDataElement(factory, restDataElement);
		String json = dataToJsonConverter.toJson();

		Assert.assertEquals(json, "{\"atomicDataId\":\"atomicValue\"}");
	}

	@Test
	public void testToJsonEmptyValue() {
		SpiderDataAtomic dataAtomic = SpiderDataAtomic.withDataIdAndValue("atomicDataId", "");
		DataAtomicSpiderToRestConverter converter = DataAtomicSpiderToRestConverter
				.fromSpiderDataAtomic(dataAtomic);
		RestDataElement restDataElement = converter.toRest();

		DataToJsonConverter dataToJsonConverter = dataToJsonConverterFactory
				.createForRestDataElement(factory, restDataElement);
		String json = dataToJsonConverter.toJson();

		Assert.assertEquals(json, "{\"atomicDataId\":\"\"}");
	}
}
