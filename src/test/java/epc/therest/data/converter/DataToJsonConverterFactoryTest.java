package epc.therest.data.converter;

import org.testng.Assert;
import org.testng.annotations.Test;

import epc.spider.data.SpiderDataAtomic;
import epc.spider.data.SpiderDataGroup;
import epc.therest.data.RestDataAtomic;
import epc.therest.data.RestDataElement;
import epc.therest.data.RestDataGroup;
import epc.therest.data.converter.DataAtomicToJsonConverter;
import epc.therest.data.converter.DataGroupToJsonConverter;
import epc.therest.data.converter.DataToJsonConverter;
import epc.therest.data.converter.DataToJsonConverterFactory;
import epc.therest.data.converter.DataToJsonConverterFactoryImp;
import epc.therest.json.builder.JsonBuilderFactory;
import epc.therest.json.builder.javax.JavaxJsonBuilderFactoryAdapter;

public class DataToJsonConverterFactoryTest {
	@Test
	public void testJsonCreatorFactoryDataGroup() {
		DataToJsonConverterFactory dataToJsonConverterFactory = new DataToJsonConverterFactoryImp();
		SpiderDataGroup dataGroup = SpiderDataGroup.withDataId("groupDataId");
		RestDataElement restDataElement = RestDataGroup.fromDataGroup(dataGroup);

		JsonBuilderFactory factory = new JavaxJsonBuilderFactoryAdapter();
		DataToJsonConverter dataToJsonConverter = dataToJsonConverterFactory
				.createForRestDataElement(factory, restDataElement);

		Assert.assertTrue(dataToJsonConverter instanceof DataGroupToJsonConverter);
	}

	@Test
	public void testJsonCreatorFactoryDataAtomic() {
		DataToJsonConverterFactory dataToJsonConverterFactory = new DataToJsonConverterFactoryImp();
		SpiderDataAtomic dataAtomic = SpiderDataAtomic.withDataIdAndValue("atomicDataId",
				"atomicValue");
		RestDataElement restDataElement = RestDataAtomic.fromSpiderDataAtomic(dataAtomic);

		JsonBuilderFactory factory = new JavaxJsonBuilderFactoryAdapter();
		DataToJsonConverter dataToJsonConverter = dataToJsonConverterFactory
				.createForRestDataElement(factory, restDataElement);

		Assert.assertTrue(dataToJsonConverter instanceof DataAtomicToJsonConverter);
	}
}
