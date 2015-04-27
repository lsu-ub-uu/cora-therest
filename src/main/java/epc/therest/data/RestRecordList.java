package epc.therest.data;

import java.util.ArrayList;
import java.util.List;

public final class RestRecordList {

	private String containRecordsOfType;
	private List<RestDataRecord> records = new ArrayList<>();
	private String totalNo;
	private String fromNo;
	private String toNo;

	public static RestRecordList withContainRecordsOfType(String containRecordsOfType) {
		return new RestRecordList(containRecordsOfType);
	}

	private RestRecordList(String containRecordsOfType) {
		this.containRecordsOfType = containRecordsOfType;
	}

	public String getContainRecordsOfType() {
		return containRecordsOfType;
	}

	public void addRecord(RestDataRecord record) {
		records.add(record);
	}

	public List<RestDataRecord> getRecords() {
		return records;
	}

	public void setTotalNo(String totalNo) {
		this.totalNo = totalNo;
	}

	public String getTotalNo() {
		return totalNo;
	}

	public void setFromNo(String fromNo) {
		this.fromNo = fromNo;

	}

	public String getFromNo() {
		return fromNo;
	}

	public void setToNo(String toNo) {
		this.toNo = toNo;

	}

	public String getToNo() {
		return toNo;
	}

}
