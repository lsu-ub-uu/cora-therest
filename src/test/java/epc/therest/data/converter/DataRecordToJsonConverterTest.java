package epc.therest.data.converter;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import epc.spider.data.Action;
import epc.therest.data.ActionLink;
import epc.therest.data.RestDataGroup;
import epc.therest.data.RestDataRecord;
import epc.therest.json.builder.JsonBuilderFactory;
import epc.therest.json.builder.org.OrgJsonBuilderFactoryAdapter;

public class DataRecordToJsonConverterTest {
	@Test
	public void testToJson() {
		RestDataGroup restDataGroup = RestDataGroup.withDataId("groupDataId");
		RestDataRecord restDataRecord = RestDataRecord.withRestDataGroup(restDataGroup);

		JsonBuilderFactory jsonFactory = new OrgJsonBuilderFactoryAdapter();
		DataRecordToJsonConterter dataRecordToJsonConterter = DataRecordToJsonConterter
				.usingJsonFactoryForRestDataRecord(jsonFactory, restDataRecord);
		String jsonString = dataRecordToJsonConterter.toJson();

		assertEquals(jsonString, "{\"record\":{\"data\":{\"groupDataId\":{}}}}");
	}

	@Test
	public void testToJsonWithKey() {
		RestDataGroup restDataGroup = RestDataGroup.withDataId("groupDataId");
		RestDataRecord restDataRecord = RestDataRecord.withRestDataGroup(restDataGroup);
		restDataRecord.addKey("KEY1");

		JsonBuilderFactory jsonFactory = new OrgJsonBuilderFactoryAdapter();
		DataRecordToJsonConterter dataRecordToJsonConterter = DataRecordToJsonConterter
				.usingJsonFactoryForRestDataRecord(jsonFactory, restDataRecord);
		String jsonString = dataRecordToJsonConterter.toJson();

		assertEquals(jsonString, "{\"record\":{\"data\":{\"groupDataId\":{}}"
				+ ",\"keys\":[\"KEY1\"]" + "}}");
	}

	@Test
	public void testToJsonWithKeys() {
		RestDataGroup restDataGroup = RestDataGroup.withDataId("groupDataId");
		RestDataRecord restDataRecord = RestDataRecord.withRestDataGroup(restDataGroup);
		restDataRecord.addKey("KEY1");
		restDataRecord.addKey("KEY2");
		restDataRecord.addKey("KEY3");

		JsonBuilderFactory jsonFactory = new OrgJsonBuilderFactoryAdapter();
		DataRecordToJsonConterter dataRecordToJsonConterter = DataRecordToJsonConterter
				.usingJsonFactoryForRestDataRecord(jsonFactory, restDataRecord);
		String jsonString = dataRecordToJsonConterter.toJson();

		assertEquals(jsonString, "{\"record\":{\"data\":{\"groupDataId\":{}}"
				+ ",\"keys\":[\"KEY1\",\"KEY2\",\"KEY3\"]" + "}}");
	}

	@Test
	public void testToJsonWithActionLinks() {
		RestDataGroup restDataGroup = RestDataGroup.withDataId("groupDataId");
		RestDataRecord restDataRecord = RestDataRecord.withRestDataGroup(restDataGroup);
		restDataRecord.addActionLink("read", createReadActionLink());

		JsonBuilderFactory jsonFactory = new OrgJsonBuilderFactoryAdapter();
		DataRecordToJsonConterter dataRecordToJsonConterter = DataRecordToJsonConterter
				.usingJsonFactoryForRestDataRecord(jsonFactory, restDataRecord);
		String jsonString = dataRecordToJsonConterter.toJson();

		assertEquals(jsonString, "{\"record\":{\"data\":{\"groupDataId\":{}}"
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
