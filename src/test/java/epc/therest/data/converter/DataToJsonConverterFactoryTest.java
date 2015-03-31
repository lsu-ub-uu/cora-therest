package epc.therest.data.converter;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import epc.spider.data.SpiderDataAtomic;
import epc.spider.data.SpiderDataGroup;
import epc.therest.data.RestDataElement;
import epc.therest.data.converter.spider.DataAtomicSpiderToRestConverter;
import epc.therest.data.converter.spider.DataGroupSpiderToRestConverter;
import epc.therest.json.builder.JsonBuilderFactory;
import epc.therest.json.builder.org.OrgJsonBuilderFactoryAdapter;

public class DataToJsonConverterFactoryTest {
	private DataToJsonConverterFactory dataToJsonConverterFactory;
	private JsonBuilderFactory factory;

	@BeforeMethod
	public void beforeMethod() {
		dataToJsonConverterFactory = new DataToJsonConverterFactoryImp();
		factory = new OrgJsonBuilderFactoryAdapter();
	}

	@Test
	public void testJsonCreatorFactoryDataGroup() {
		SpiderDataGroup dataGroup = SpiderDataGroup.withDataId("groupDataId");
		DataGroupSpiderToRestConverter converter = DataGroupSpiderToRestConverter
				.fromSpiderDataGroup(dataGroup);
		RestDataElement restDataElement = converter.toRest();

		DataToJsonConverter dataToJsonConverter = dataToJsonConverterFactory
				.createForRestDataElement(factory, restDataElement);

		Assert.assertTrue(dataToJsonConverter instanceof DataGroupToJsonConverter);
	}

	@Test
	public void testJsonCreatorFactoryDataAtomic() {
		SpiderDataAtomic dataAtomic = SpiderDataAtomic.withDataIdAndValue("atomicDataId",
				"atomicValue");
		DataAtomicSpiderToRestConverter converter = DataAtomicSpiderToRestConverter
				.fromSpiderDataAtomic(dataAtomic);
		RestDataElement restDataElement = converter.toRest();

		DataToJsonConverter dataToJsonConverter = dataToJsonConverterFactory
				.createForRestDataElement(factory, restDataElement);

		Assert.assertTrue(dataToJsonConverter instanceof DataAtomicToJsonConverter);
	}
}
