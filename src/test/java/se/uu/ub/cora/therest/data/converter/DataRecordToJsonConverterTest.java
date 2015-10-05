package se.uu.ub.cora.therest.data.converter;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import se.uu.ub.cora.spider.data.Action;
import se.uu.ub.cora.therest.data.ActionLink;
import se.uu.ub.cora.therest.data.RestDataGroup;
import se.uu.ub.cora.therest.data.RestDataRecord;
import se.uu.ub.cora.therest.data.converter.DataRecordToJsonConverter;
import se.uu.ub.cora.therest.json.builder.JsonBuilderFactory;
import se.uu.ub.cora.therest.json.builder.org.OrgJsonBuilderFactoryAdapter;

public class DataRecordToJsonConverterTest {
	@Test
	public void testToJson() {
		RestDataGroup restDataGroup = RestDataGroup.withNameInData("groupNameInData");
		RestDataRecord restDataRecord = RestDataRecord.withRestDataGroup(restDataGroup);

		JsonBuilderFactory jsonFactory = new OrgJsonBuilderFactoryAdapter();
		DataRecordToJsonConverter dataRecordToJsonConverter = DataRecordToJsonConverter
				.usingJsonFactoryForRestDataRecord(jsonFactory, restDataRecord);
		String jsonString = dataRecordToJsonConverter.toJson();

		assertEquals(jsonString, "{\"record\":{\"data\":{\"name\":\"groupNameInData\"}}}");
	}

	@Test
	public void testToJsonWithKey() {
		RestDataGroup restDataGroup = RestDataGroup.withNameInData("groupNameInData");
		RestDataRecord restDataRecord = RestDataRecord.withRestDataGroup(restDataGroup);
		restDataRecord.addKey("KEY1");

		JsonBuilderFactory jsonFactory = new OrgJsonBuilderFactoryAdapter();
		DataRecordToJsonConverter dataRecordToJsonConverter = DataRecordToJsonConverter
				.usingJsonFactoryForRestDataRecord(jsonFactory, restDataRecord);
		String jsonString = dataRecordToJsonConverter.toJson();

		assertEquals(jsonString, "{\"record\":{\"data\":{\"name\":\"groupNameInData\"}"
				+ ",\"keys\":[\"KEY1\"]" + "}}");
	}

	@Test
	public void testToJsonWithKeys() {
		RestDataGroup restDataGroup = RestDataGroup.withNameInData("groupNameInData");
		RestDataRecord restDataRecord = RestDataRecord.withRestDataGroup(restDataGroup);
		restDataRecord.addKey("KEY1");
		restDataRecord.addKey("KEY2");
		restDataRecord.addKey("KEY3");

		JsonBuilderFactory jsonFactory = new OrgJsonBuilderFactoryAdapter();
		DataRecordToJsonConverter dataRecordToJsonConverter = DataRecordToJsonConverter
				.usingJsonFactoryForRestDataRecord(jsonFactory, restDataRecord);
		String jsonString = dataRecordToJsonConverter.toJson();

		assertEquals(jsonString, "{\"record\":{\"data\":{\"name\":\"groupNameInData\"}"
				+ ",\"keys\":[\"KEY1\",\"KEY2\",\"KEY3\"]" + "}}");
	}

	@Test
	public void testToJsonWithActionLinks() {
		RestDataGroup restDataGroup = RestDataGroup.withNameInData("groupNameInData");
		RestDataRecord restDataRecord = RestDataRecord.withRestDataGroup(restDataGroup);
		restDataRecord.addActionLink("read", createReadActionLink());

		JsonBuilderFactory jsonFactory = new OrgJsonBuilderFactoryAdapter();
		DataRecordToJsonConverter dataRecordToJsonConverter = DataRecordToJsonConverter
				.usingJsonFactoryForRestDataRecord(jsonFactory, restDataRecord);
		String jsonString = dataRecordToJsonConverter.toJson();

		assertEquals(jsonString, "{\"record\":{\"data\":{\"name\":\"groupNameInData\"}"
				+ ",\"actionLinks\":{" + "\"read\":{\"requestMethod\":\"GET\",\"rel\":\"read\","
				+ "\"contentType\":\"application/metadata_record+json\","
				+ "\"url\":\"http://localhost:8080/therest/rest/record/place/place:0001\","
				+ "\"accept\":\"application/metadata_record+json\"}" + "}}}");
	}

	private ActionLink createReadActionLink() {
		ActionLink actionLink = ActionLink.withAction(Action.READ);
		actionLink.setAccept("application/metadata_record+json");
		actionLink.setContentType("application/metadata_record+json");
		actionLink.setRequestMethod("GET");
		actionLink.setURL("http://localhost:8080/therest/rest/record/place/place:0001");
		return actionLink;
	}

}
