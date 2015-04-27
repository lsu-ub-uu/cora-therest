package epc.therest.data;

import static org.testng.Assert.assertEquals;

import java.util.List;

import org.testng.annotations.Test;

public class RestDataRecordListTest {
	@Test
	public void testInit2() {
		String containRecordsOfType = "metadata";
		RestRecordList restRecordList = RestRecordList
				.withContainRecordsOfType(containRecordsOfType);
		assertEquals(restRecordList.getContainRecordsOfType(), "metadata");
	}

	@Test
	public void testAddRecord() {
		RestRecordList restRecordList = RestRecordList.withContainRecordsOfType("metadata");
		RestDataGroup restDataGroup = RestDataGroup.withDataId("restDataGroupId");
		RestDataRecord record = RestDataRecord.withRestDataGroup(restDataGroup);
		restRecordList.addRecord(record);
		List<RestDataRecord> records = restRecordList.getRecords();
		assertEquals(records.get(0), record);
	}

	@Test
	public void testTotalNo() {
		RestRecordList restRecordList = RestRecordList.withContainRecordsOfType("metadata");
		restRecordList.setTotalNo("2");
		assertEquals(restRecordList.getTotalNo(), "2");
	}

	@Test
	public void testFromNo() {
		RestRecordList restRecordList = RestRecordList.withContainRecordsOfType("metadata");
		restRecordList.setFromNo("0");
		assertEquals(restRecordList.getFromNo(), "0");
	}

	@Test
	public void testToNo() {
		RestRecordList restRecordList = RestRecordList.withContainRecordsOfType("metadata");
		restRecordList.setToNo("2");
		assertEquals(restRecordList.getToNo(), "2");
	}
}
