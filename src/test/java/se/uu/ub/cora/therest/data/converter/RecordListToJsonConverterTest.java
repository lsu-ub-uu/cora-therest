package se.uu.ub.cora.therest.data.converter;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import se.uu.ub.cora.therest.data.RestDataGroup;
import se.uu.ub.cora.therest.data.RestDataRecord;
import se.uu.ub.cora.therest.data.RestRecordList;
import se.uu.ub.cora.therest.data.converter.RecordListToJsonConverter;
import se.uu.ub.cora.therest.json.builder.JsonBuilderFactory;
import se.uu.ub.cora.therest.json.builder.org.OrgJsonBuilderFactoryAdapter;

public class RecordListToJsonConverterTest {
	@Test
	public void testToJson() {
		RestRecordList restRecordList = RestRecordList.withContainRecordsOfType("place");
		RestDataGroup restDataGroup = RestDataGroup.withNameInData("groupId");
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
