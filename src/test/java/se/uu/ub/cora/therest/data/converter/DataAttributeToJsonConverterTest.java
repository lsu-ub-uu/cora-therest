package se.uu.ub.cora.therest.data.converter;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.therest.data.RestDataAttribute;
import se.uu.ub.cora.therest.data.RestDataElement;
import se.uu.ub.cora.therest.json.builder.JsonBuilderFactory;
import se.uu.ub.cora.therest.json.builder.org.OrgJsonBuilderFactoryAdapter;

public class DataAttributeToJsonConverterTest {
	private DataToJsonConverterFactory dataToJsonConverterFactory;
	private JsonBuilderFactory factory;

	@BeforeMethod
	public void beforeMethod() {
		dataToJsonConverterFactory = new DataToJsonConverterFactoryImp();
		factory = new OrgJsonBuilderFactoryAdapter();

	}

	@Test
	public void testToJson() {
		RestDataElement restDataElement = RestDataAttribute
				.withNameInDataAndValue("attributeNameInData", "attributeValue");
		DataToJsonConverter dataToJsonConverter = dataToJsonConverterFactory
				.createForRestDataElement(factory, restDataElement);
		String json = dataToJsonConverter.toJson();

		Assert.assertEquals(json, "{\"attributeNameInData\":\"attributeValue\"}");
	}

	@Test
	public void testToJsonEmptyValue() {
		RestDataElement restDataElement = RestDataAttribute
				.withNameInDataAndValue("attributeNameInData", "");
		DataToJsonConverter dataToJsonConverter = dataToJsonConverterFactory
				.createForRestDataElement(factory, restDataElement);
		String json = dataToJsonConverter.toJson();

		Assert.assertEquals(json, "{\"attributeNameInData\":\"\"}");
	}
}
