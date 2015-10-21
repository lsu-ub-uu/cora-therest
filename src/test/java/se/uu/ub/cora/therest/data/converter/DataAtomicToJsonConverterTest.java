package se.uu.ub.cora.therest.data.converter;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.therest.data.RestDataAtomic;
import se.uu.ub.cora.therest.json.builder.org.OrgJsonBuilderFactoryAdapter;

public class DataAtomicToJsonConverterTest {
	private RestDataAtomic restDataAtomic;
	private DataToJsonConverter converter;

	@BeforeMethod
	public void beforeMethod() {
		restDataAtomic = RestDataAtomic.withNameInDataAndValue("atomicNameInData", "atomicValue");
		OrgJsonBuilderFactoryAdapter factory = new OrgJsonBuilderFactoryAdapter();
		converter = DataAtomicToJsonConverter.usingJsonFactoryForRestDataAtomic(factory,
				restDataAtomic);
	}

	@Test
	public void testToJson() {
		String json = converter.toJson();

		Assert.assertEquals(json, "{\"name\":\"atomicNameInData\",\"value\":\"atomicValue\"}");
	}

	@Test
	public void testToJsonWithRepeatId() {
		restDataAtomic.setRepeatId("2");
		String json = converter.toJson();

		Assert.assertEquals(json,
				"{\"repeatId\":\"2\",\"name\":\"atomicNameInData\",\"value\":\"atomicValue\"}");
	}

	@Test
	public void testToJsonWithEmptyRepeatId() {
		restDataAtomic.setRepeatId("");
		String json = converter.toJson();

		Assert.assertEquals(json, "{\"name\":\"atomicNameInData\",\"value\":\"atomicValue\"}");
	}

	@Test
	public void testToJsonEmptyValue() {
		RestDataAtomic restDataAtomic = RestDataAtomic.withNameInDataAndValue("atomicNameInData",
				"");
		OrgJsonBuilderFactoryAdapter factory = new OrgJsonBuilderFactoryAdapter();
		converter = DataAtomicToJsonConverter.usingJsonFactoryForRestDataAtomic(factory,
				restDataAtomic);
		String json = converter.toJson();

		Assert.assertEquals(json, "{\"name\":\"atomicNameInData\",\"value\":\"\"}");
	}
}
