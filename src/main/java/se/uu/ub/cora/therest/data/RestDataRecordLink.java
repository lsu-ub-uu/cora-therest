package se.uu.ub.cora.therest.data;

import java.util.LinkedHashMap;
import java.util.Map;

public final class RestDataRecordLink implements RestDataElement {
	private String nameInData;

	private Map<String, ActionLink> actionLinks = new LinkedHashMap<>();
	private String recordType;
	private String recordId;

	public static RestDataRecordLink withNameInDataAndRecordTypeAndRecordId(String nameInData,
			String recordType, String recordId) {
		return new RestDataRecordLink(nameInData, recordType, recordId);
	}

	private RestDataRecordLink(String nameInData, String recordType, String recordId) {
		this.nameInData = nameInData;
		this.recordType = recordType;
		this.recordId = recordId;

	}

	@Override
	public String getNameInData() {
		return nameInData;
	}

	public void addActionLink(String key, ActionLink actionLink) {
		actionLinks.put(key, actionLink);
	}

	public ActionLink getActionLink(String key) {
		return actionLinks.get(key);
	}

	public Map<String, ActionLink> getActionLinks() {
		return actionLinks;
	}

	public String getRecordType() {
		return recordType;
	}

	public String getRecordId() {
		return recordId;
	}

	public void setActionLinks(Map<String, ActionLink> actionLinks) {
		this.actionLinks = actionLinks;
	}

}
