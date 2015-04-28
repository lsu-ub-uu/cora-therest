package epc.therest.data.converter.spider;

import epc.spider.data.SpiderDataRecord;
import epc.spider.data.SpiderRecordList;
import epc.therest.data.RestDataRecord;
import epc.therest.data.RestRecordList;

public final class RecordListSpiderToRestConverter {

	private SpiderRecordList spiderRecordList;
	private String baseURL;

	public static RecordListSpiderToRestConverter fromSpiderRecordListWithBaseURL(
			SpiderRecordList spiderRecordList, String baseURL) {
		return new RecordListSpiderToRestConverter(spiderRecordList, baseURL);
	}

	private RecordListSpiderToRestConverter(SpiderRecordList spiderRecordList, String baseURL) {
		this.spiderRecordList = spiderRecordList;
		this.baseURL = baseURL;
	}

	public RestRecordList toRest() {
		RestRecordList restRecordList = RestRecordList.withContainRecordsOfType(spiderRecordList
				.getContainRecordsOfType());

		restRecordList.setTotalNo(spiderRecordList.getToNo());
		restRecordList.setFromNo(spiderRecordList.getFromNo());
		restRecordList.setToNo(spiderRecordList.getToNo());

		for (SpiderDataRecord spiderDataRecord : spiderRecordList.getRecords()) {
			RestDataRecord restRecord = DataRecordSpiderToRestConverter
					.fromSpiderDataRecordWithBaseURL(spiderDataRecord, baseURL).toRest();
			restRecordList.addRecord(restRecord);
		}
		return restRecordList;
	}
}
