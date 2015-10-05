package se.uu.ub.cora.therest.data.converter.spider;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import se.uu.ub.cora.spider.data.SpiderDataGroup;
import se.uu.ub.cora.spider.data.SpiderDataRecord;
import se.uu.ub.cora.spider.data.SpiderRecordList;
import se.uu.ub.cora.therest.data.RestDataGroup;
import se.uu.ub.cora.therest.data.RestRecordList;
import se.uu.ub.cora.therest.data.converter.spider.RecordListSpiderToRestConverter;

public class RecordListSpiderToRestConverterTest {
	private String baseURL = "http://localhost:8080/therest/rest/record/";

	@Test
	public void testToRest() {
		SpiderDataGroup spiderDataGroup = SpiderDataGroup.withNameInData("groupId");
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

		assertEquals(restDataGroup.getNameInData(), "groupId");
	}
}
