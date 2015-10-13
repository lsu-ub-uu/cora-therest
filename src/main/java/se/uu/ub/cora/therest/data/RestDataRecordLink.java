package se.uu.ub.cora.therest.data;

import java.util.LinkedHashMap;
import java.util.Map;

public class RestDataRecordLink implements RestDataElement {
	private String nameInData;
	private Map<String, ActionLink> actionLinks = new LinkedHashMap<>();
	private String recordType;
	private String recordId;

	public static RestDataRecordLink withNameInData(String nameInData) {
		return new RestDataRecordLink(nameInData);
	}

	private RestDataRecordLink(String nameInData) {
		this.nameInData = nameInData;
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

	public void setRecordType(String recordType) {
		this.recordType = recordType;

	}

	public void setRecordId(String recordId) {
		this.recordId = recordId;

	}

	public String getRecordType() {
		return recordType;
	}

	public String getRecordId() {
		return recordId;
	}

}
