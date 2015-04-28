package epc.therest.data.converter.spider;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import epc.spider.data.SpiderDataGroup;
import epc.spider.data.SpiderDataRecord;
import epc.spider.data.SpiderRecordList;
import epc.therest.data.RestDataGroup;
import epc.therest.data.RestRecordList;

public class RecordListSpiderToRestConverterTest {
	private String baseURL = "http://localhost:8080/therest/rest/record/";

	@Test
	public void testToRest() {
		SpiderDataGroup spiderDataGroup = SpiderDataGroup.withDataId("groupId");
		SpiderDataRecord spiderDataRecord = SpiderDataRecord.withSpiderDataGroup(spiderDataGroup);
		SpiderRecordList spiderRecordList = SpiderRecordList.withContainRecordsOfType("place");
		spiderRecordList.addRecord(spiderDataRecord);
		spiderRecordList.setTotalNo("10");
		spiderRecordList.setFromNo("0");
		spiderRecordList.setToNo("1");

		RecordListSpiderToRestConverter converter = RecordListSpiderToRestConverter
				.fromSpiderRecordListWithBaseURL(spiderRecordList, baseURL);

		RestRecordList recordList = converter.toRest();

		RestDataGroup restDataGroup = recordList.getRecords().get(0).getRestDataGroup();

		assertEquals(restDataGroup.getDataId(), "groupId");
	}
}
