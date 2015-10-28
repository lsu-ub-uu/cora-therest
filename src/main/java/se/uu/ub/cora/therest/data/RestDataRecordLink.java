/*
 * Copyright 2015 Uppsala University Library
 *
 * This file is part of Cora.
 *
 *     Cora is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Cora is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Cora.  If not, see <http://www.gnu.org/licenses/>.
 */

package se.uu.ub.cora.therest.data;


import java.util.LinkedHashMap;
import java.util.Map;

public final class RestDataRecordLink implements RestDataElement {
	private String nameInData;

	private Map<String, ActionLink> actionLinks = new LinkedHashMap<>();
	private String linkedRecordType;
	private String linkedRecordId;

	private String repeatId;
	private String linkedRepeatId;
	private RestDataGroup linkedPath;

	public static RestDataRecordLink withNameInDataAndLinkedRecordTypeAndLinkedRecordId(
			String nameInData, String linkedRecordType, String linkedRecordId) {
		return new RestDataRecordLink(nameInData, linkedRecordType, linkedRecordId);
	}

	private RestDataRecordLink(String nameInData, String linkedRecordType, String linkedRecordId) {
		this.nameInData = nameInData;
		this.linkedRecordType = linkedRecordType;
		this.linkedRecordId = linkedRecordId;

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

	public String getLinkedRecordType() {
		return linkedRecordType;
	}

	public String getLinkedRecordId() {
		return linkedRecordId;
	}

	public void setActionLinks(Map<String, ActionLink> actionLinks) {
		this.actionLinks = actionLinks;
	}

	public void setRepeatId(String repeatId) {
		this.repeatId = repeatId;
	}

	public String getRepeatId() {
		return repeatId;
	}

	public String getLinkedRepeatId() {
		return linkedRepeatId;
	}

	public void setLinkedRepeatId(String linkedRepeatId) {
		this.linkedRepeatId = linkedRepeatId;
	}

	public RestDataGroup getLinkedPath() {
		return linkedPath;
	}

	public void setLinkedPath(RestDataGroup linkedPath) {
		this.linkedPath = linkedPath;
	}
}
