package epc.therest.data.converter;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import epc.therest.data.RestDataAtomic;
import epc.therest.data.RestDataElement;
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
		RestDataElement restDataElement = RestDataAtomic.withNameInDataAndValue("atomicNameInData",
				"atomicValue");
		DataToJsonConverter dataToJsonConverter = dataToJsonConverterFactory
				.createForRestDataElement(factory, restDataElement);
		String json = dataToJsonConverter.toJson();

		Assert.assertEquals(json, "{\"atomicNameInData\":\"atomicValue\"}");
	}

	@Test
	public void testToJsonEmptyValue() {
		RestDataElement restDataElement = RestDataAtomic.withNameInDataAndValue("atomicNameInData", "");
		DataToJsonConverter dataToJsonConverter = dataToJsonConverterFactory
				.createForRestDataElement(factory, restDataElement);
		String json = dataToJsonConverter.toJson();

		Assert.assertEquals(json, "{\"atomicNameInData\":\"\"}");
	}
}
