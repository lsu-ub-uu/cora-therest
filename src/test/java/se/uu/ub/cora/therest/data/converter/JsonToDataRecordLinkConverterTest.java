package se.uu.ub.cora.therest.data.converter;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.therest.data.RestDataElement;
import se.uu.ub.cora.therest.data.RestDataRecordLink;
import se.uu.ub.cora.therest.json.parser.JsonObject;
import se.uu.ub.cora.therest.json.parser.JsonParseException;
import se.uu.ub.cora.therest.json.parser.JsonValue;
import se.uu.ub.cora.therest.json.parser.org.OrgJsonParser;

public class JsonToDataRecordLinkConverterTest {
	private OrgJsonParser jsonParser;

	@BeforeMethod
	public void beforeMethod() {
		jsonParser = new OrgJsonParser();
	}

	@Test
	public void testToClass() {
		String json = "{\"recordId\":\"aRecordId\",\"recordType\":\"aRecordType\""
				+ ",\"name\":\"nameInData\"}";
		RestDataRecordLink restDataRecordLink = createRestDataRecordLinkForJsonString(json);
		assertEquals(restDataRecordLink.getNameInData(), "nameInData");
	}

	private RestDataRecordLink createRestDataRecordLinkForJsonString(String json) {
		JsonValue jsonValue = jsonParser.parseString(json);
		JsonToDataConverter jsonToDataConverter = JsonToDataRecordLinkConverter
				.forJsonObject((JsonObject) jsonValue);
		RestDataElement restDataElement = jsonToDataConverter.toInstance();
		RestDataRecordLink restDataRecordLink = (RestDataRecordLink) restDataElement;
		return restDataRecordLink;
	}

	@Test
	public void testToClassWithRepeatId() {
		String json = "{\"recordId\":\"aRecordId\",\"recordType\":\"aRecordType\""
				+ ",\"name\":\"nameInData\",\"repeatId\":\"7\"}";
		RestDataRecordLink restDataRecordLink = createRestDataRecordLinkForJsonString(json);
		assertEquals(restDataRecordLink.getNameInData(), "nameInData");
		assertEquals(restDataRecordLink.getRepeatId(), "7");
	}

	@Test
	public void testToClassAnActionLink() {
		String json = "{\"recordId\":\"aRecordId\",\"recordType\":\"aRecordType\""
				+ ",\"name\":\"nameInData\",\"actionLinks\":\"someActionLink\"}";
		RestDataRecordLink restDataRecordLink = createRestDataRecordLinkForJsonString(json);
		assertEquals(restDataRecordLink.getNameInData(), "nameInData");
	}

	@Test
	public void testToClassWithRepeatIdAndActionLink() {
		String json = "{\"recordId\":\"aRecordId\",\"recordType\":\"aRecordType\""
				+ ",\"name\":\"nameInData\",\"actionLinks\":\"someActionLink\",\"repeatId\":\"7\"}";
		RestDataRecordLink restDataRecordLink = createRestDataRecordLinkForJsonString(json);
		assertEquals(restDataRecordLink.getNameInData(), "nameInData");
		assertEquals(restDataRecordLink.getRepeatId(), "7");
	}

	@Test(expectedExceptions = JsonParseException.class)
	public void testToClassMaxNoOfKeysButRepeatIdMissing() {
		String json = "{\"recordId\":\"aRecordId\",\"recordType\":\"aRecordType\""
				+ ",\"name\":\"nameInData\",\"actionLinks\":\"someActionLink\""
				+ ",\"NOTrepeatId\":\"7\"}";
		createRestDataRecordLinkForJsonString(json);
	}

	@Test(expectedExceptions = JsonParseException.class)
	public void testToClassMaxNoOfKeysButActionLinksMissing() {
		String json = "{\"recordId\":\"aRecordId\",\"recordType\":\"aRecordType\""
				+ ",\"name\":\"nameInData\",\"NOTactionLinks\":\"someActionLink\""
				+ ",\"repeatId\":\"7\"}";
		createRestDataRecordLinkForJsonString(json);
	}

	@Test(expectedExceptions = JsonParseException.class)
	public void testToClassNoName() {
		String json = "{\"recordId\":\"aRecordId\",\"recordType\":\"aRecordType\"" + "}";
		createRestDataRecordLinkForJsonString(json);
	}

	@Test(expectedExceptions = JsonParseException.class)
	public void testToClassNoRecordType() {
		String json = "{\"recordId\":\"aRecordId\"" + ",\"name\":\"nameInData\"}";
		createRestDataRecordLinkForJsonString(json);
	}

	@Test(expectedExceptions = JsonParseException.class)
	public void testToClassNoRecordId() {
		String json = "{\"recordType\":\"aRecordType\"" + ",\"name\":\"nameInData\"}";
		createRestDataRecordLinkForJsonString(json);
	}

	@Test(expectedExceptions = JsonParseException.class)
	public void testToClassNotAnActionLink() {
		String json = "{\"recordId\":\"aRecordId\",\"recordType\":\"aRecordType\""
				+ ",\"name\":\"nameInData\",\"notActionLinks\":\"somethingElse\"}";
		createRestDataRecordLinkForJsonString(json);
	}

	@Test(expectedExceptions = JsonParseException.class)
	public void testToClassMoreKeysThanAllowed() {
		String json = "{\"recordId\":\"aRecordId\",\"repeatId\":\"x\""
				+ ",\"recordType\":\"aRecordType\""
				+ ",\"name\":\"nameInData\",\"notAnActionLink\":\"somethingElse\""
				+ ",\"anExtraKey\":\"somethingElse2\"}";
		createRestDataRecordLinkForJsonString(json);
	}

}
