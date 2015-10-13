package se.uu.ub.cora.therest.data.converter;

import static org.testng.Assert.assertTrue;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.therest.data.RestDataAtomic;
import se.uu.ub.cora.therest.data.RestDataAttribute;
import se.uu.ub.cora.therest.data.RestDataElement;
import se.uu.ub.cora.therest.data.RestDataGroup;
import se.uu.ub.cora.therest.data.RestDataRecordLink;
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

		assertTrue(dataToJsonConverter instanceof DataGroupToJsonConverter);
	}

	@Test
	public void testJsonCreatorFactoryDataAtomic() {
		RestDataElement restDataElement = RestDataAtomic.withNameInDataAndValue("atomicNameInData",
				"atomicValue");

		DataToJsonConverter dataToJsonConverter = dataToJsonConverterFactory
				.createForRestDataElement(factory, restDataElement);

		assertTrue(dataToJsonConverter instanceof DataAtomicToJsonConverter);
	}

	@Test
	public void testJsonCreatorFactoryDataAttribute() {
		RestDataElement restDataElement = RestDataAttribute
				.withNameInDataAndValue("attributeNameInData", "attributeValue");

		DataToJsonConverter dataToJsonConverter = dataToJsonConverterFactory
				.createForRestDataElement(factory, restDataElement);

		assertTrue(dataToJsonConverter instanceof DataAttributeToJsonConverter);
	}

	@Test
	public void testJsonCreateFactoryDataRecordLink() {
		RestDataElement restDataElement = RestDataRecordLink.withNameInData("recordLinkNameInData");
		DataToJsonConverter dataToJsonConverter = dataToJsonConverterFactory
				.createForRestDataElement(factory, restDataElement);

		assertTrue(dataToJsonConverter instanceof DataRecordLinkToJsonConverter);

	}
}
