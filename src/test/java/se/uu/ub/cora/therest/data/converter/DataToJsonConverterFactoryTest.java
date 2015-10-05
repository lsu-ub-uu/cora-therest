package se.uu.ub.cora.therest.data.converter;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.therest.data.RestDataAtomic;
import se.uu.ub.cora.therest.data.RestDataElement;
import se.uu.ub.cora.therest.data.RestDataGroup;
import se.uu.ub.cora.therest.data.converter.DataAtomicToJsonConverter;
import se.uu.ub.cora.therest.data.converter.DataGroupToJsonConverter;
import se.uu.ub.cora.therest.data.converter.DataToJsonConverter;
import se.uu.ub.cora.therest.data.converter.DataToJsonConverterFactory;
import se.uu.ub.cora.therest.data.converter.DataToJsonConverterFactoryImp;
import se.uu.ub.cora.therest.json.builder.JsonBuilderFactory;
import se.uu.ub.cora.therest.json.builder.org.OrgJsonBuilderFactoryAdapter;

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
		RestDataElement restDataElement = RestDataGroup.withNameInData("groupNameInData");

		DataToJsonConverter dataToJsonConverter = dataToJsonConverterFactory
				.createForRestDataElement(factory, restDataElement);

		Assert.assertTrue(dataToJsonConverter instanceof DataGroupToJsonConverter);
	}

	@Test
	public void testJsonCreatorFactoryDataAtomic() {
		RestDataElement restDataElement = RestDataAtomic.withNameInDataAndValue("atomicNameInData",
				"atomicValue");

		DataToJsonConverter dataToJsonConverter = dataToJsonConverterFactory
				.createForRestDataElement(factory, restDataElement);

		Assert.assertTrue(dataToJsonConverter instanceof DataAtomicToJsonConverter);
	}
}
