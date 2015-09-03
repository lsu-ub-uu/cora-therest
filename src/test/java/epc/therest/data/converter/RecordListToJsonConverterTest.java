package epc.therest.data.converter;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import epc.therest.data.RestDataGroup;
import epc.therest.data.RestDataRecord;
import epc.therest.data.RestRecordList;
import epc.therest.json.builder.JsonBuilderFactory;
import epc.therest.json.builder.org.OrgJsonBuilderFactoryAdapter;

public class RecordListToJsonConverterTest {
	@Test
	public void testToJson() {
		RestRecordList restRecordList = RestRecordList.withContainRecordsOfType("place");
		RestDataGroup restDataGroup = RestDataGroup.withDataId("groupId");
		RestDataRecord restDataRecord = RestDataRecord.withRestDataGroup(restDataGroup);
		restRecordList.addRecord(restDataRecord);
		restRecordList.setTotalNo("1");
		restRecordList.setFromNo("0");
		restRecordList.setToNo("1");

		JsonBuilderFactory jsonFactory = new OrgJsonBuilderFactoryAdapter();
		RecordListToJsonConverter recordListToJsonConverter = new RecordListToJsonConverter(
				jsonFactory, restRecordList);
		String jsonString = recordListToJsonConverter.toJson();
		assertEquals(jsonString, "{\"recordList\":{\"fromNo\":\"0\",\""
				+ "records\":[{\"record\":{\"data\":{\"name\":\"groupId\"}}}],"
				+ "\"totalNo\":\"1\",\"toNo\":\"1\",\"containRecordsOfType\":\"place\"}}");

	}

}
