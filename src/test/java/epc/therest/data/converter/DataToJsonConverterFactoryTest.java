package epc.therest.data.converter;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import epc.therest.data.RestDataAtomic;
import epc.therest.data.RestDataElement;
import epc.therest.data.RestDataGroup;
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
		RestDataElement restDataElement = RestDataGroup.withDataId("groupDataId");

		DataToJsonConverter dataToJsonConverter = dataToJsonConverterFactory
				.createForRestDataElement(factory, restDataElement);

		Assert.assertTrue(dataToJsonConverter instanceof DataGroupToJsonConverter);
	}

	@Test
	public void testJsonCreatorFactoryDataAtomic() {
		RestDataElement restDataElement = RestDataAtomic.withDataIdAndValue("atomicDataId",
				"atomicValue");

		DataToJsonConverter dataToJsonConverter = dataToJsonConverterFactory
				.createForRestDataElement(factory, restDataElement);

		Assert.assertTrue(dataToJsonConverter instanceof DataAtomicToJsonConverter);
	}
}
