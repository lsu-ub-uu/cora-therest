/*
 * Copyright 2015, 2016, 2019 Uppsala University Library
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

package se.uu.ub.cora.therest.converter.coratorest;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import se.uu.ub.cora.data.Action;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.therest.data.ActionLink;
import se.uu.ub.cora.therest.data.RestDataAtomic;
import se.uu.ub.cora.therest.data.RestDataGroup;

public final class CoraActionToRestConverterImp implements CoraActionToRestConverter {

	private static final String RECORD_TYPE = "recordType";
	private static final String APPLICATION_UUB_RECORD_LIST_JSON = "application/vnd.uub.recordList+json";
	private static final String APPLICATION_UUB_RECORD_JSON = "application/vnd.uub.record+json";
	private ConverterInfo converterInfo;
	private List<Action> actions;
	private String recordType;
	private String recordId;
	private DataGroup dataGroup;

	public CoraActionToRestConverterImp(List<Action> actions, ConverterInfo converterInfo) {
		this.actions = actions;
		this.converterInfo = converterInfo;
	}

	public CoraActionToRestConverterImp(List<Action> actions, ConverterInfo converterInfo,
			DataGroup dataGroup) {
		this.actions = actions;
		this.converterInfo = converterInfo;
		this.dataGroup = dataGroup;
	}

	public static CoraActionToRestConverter fromDataActionsWithConverterInfo(List<Action> actions,
			ConverterInfo converterInfo) {
		return new CoraActionToRestConverterImp(actions, converterInfo);
	}

	public static CoraActionToRestConverter fromDataActionsWithConverterInfoAndDataGroup(
			List<Action> actions, ConverterInfo converterInfo, DataGroup dataGroup) {
		return new CoraActionToRestConverterImp(actions, converterInfo, dataGroup);
	}

	@Override
	public Map<String, ActionLink> toRest() {
		recordType = converterInfo.recordType;
		recordId = converterInfo.recordId;
		Map<String, ActionLink> actionLinks = new LinkedHashMap<>();
		for (Action action : actions) {
			actionToRest(actionLinks, action);
		}
		return actionLinks;
	}

	private void actionToRest(Map<String, ActionLink> actionLinks, Action action) {
		String url = converterInfo.baseURL + recordType + "/";
		String urlWithRecordId = url + recordId;
		String urlForRecordTypeActions = converterInfo.baseURL + recordId + "/";
		ActionLink actionLink = ActionLink.withAction(action);

		if (Action.READ == action) {
			actionLink.setRequestMethod("GET");
			actionLink.setURL(urlWithRecordId);
			actionLink.setAccept(APPLICATION_UUB_RECORD_JSON);
		} else if (Action.UPDATE == action) {
			setUpPostForSingleRecord(actionLink);
			actionLink.setURL(urlWithRecordId);
		} else if (Action.READ_INCOMING_LINKS == action) {
			actionLink.setRequestMethod("GET");
			urlWithRecordId = urlWithRecordId + "/incomingLinks";
			actionLink.setURL(urlWithRecordId);
			actionLink.setAccept(APPLICATION_UUB_RECORD_LIST_JSON);
		} else if (Action.DELETE == action) {
			actionLink.setRequestMethod("DELETE");
			actionLink.setURL(urlWithRecordId);
		} else if (Action.CREATE == action) {
			actionLink.setRequestMethod("POST");
			actionLink.setURL(urlForRecordTypeActions);
			actionLink.setAccept(APPLICATION_UUB_RECORD_JSON);
			actionLink.setContentType(APPLICATION_UUB_RECORD_JSON);
		} else if (Action.UPLOAD == action) {
			actionLink.setRequestMethod("POST");
			actionLink.setURL(urlWithRecordId + "/master");
			actionLink.setContentType("multipart/form-data");
		} else if (Action.SEARCH == action) {
			setUpActionLinkForSearch(actionLink);
		} else if (Action.INDEX == action) {
			setUpActionLinkForIndexAction(actionLink);
		} else if (Action.VALIDATE == action) {
			actionLink.setRequestMethod("POST");
			actionLink.setURL(converterInfo.baseURL + "workOrder/");
			actionLink.setAccept(APPLICATION_UUB_RECORD_JSON);
			actionLink.setContentType("application/vnd.uub.workorder+json");
		} else if (Action.BATCH_INDEX == action) {
			actionLink.setRequestMethod("POST");
			actionLink.setURL(converterInfo.baseURL + "index/" + recordId + "/");
			actionLink.setAccept(APPLICATION_UUB_RECORD_JSON);
		} else {
			// list / search
			actionLink.setRequestMethod("GET");
			actionLink.setURL(urlForRecordTypeActions);
			actionLink.setAccept(APPLICATION_UUB_RECORD_LIST_JSON);
		}

		actionLinks.put(action.name().toLowerCase(), actionLink);
	}

	private void setUpActionLinkForSearch(ActionLink actionLink) {
		String searchId = getSearchId();
		actionLink.setRequestMethod("GET");
		actionLink.setURL(converterInfo.baseURL + "searchResult/" + searchId);
		actionLink.setAccept(APPLICATION_UUB_RECORD_LIST_JSON);
	}

	private String getSearchId() {
		String searchId = recordId;
		if (RECORD_TYPE.equals(recordType)) {
			searchId = getSearchIdIfPresentInMetadata(searchId);
		}
		return searchId;
	}

	private String getSearchIdIfPresentInMetadata(String searchId) {
		if (metadataContainsSearch()) {
			DataGroup searchGroup = dataGroup.getFirstGroupWithNameInData("search");
			return searchGroup.getFirstAtomicValueWithNameInData("linkedRecordId");
		}
		return searchId;
	}

	private boolean metadataContainsSearch() {
		return dataGroup != null && dataGroup.containsChildWithNameInData("search");
	}

	private void setUpPostForSingleRecord(ActionLink actionLink) {
		actionLink.setRequestMethod("POST");
		actionLink.setAccept(APPLICATION_UUB_RECORD_JSON);
		actionLink.setContentType(APPLICATION_UUB_RECORD_JSON);
	}

	private void setUpActionLinkForIndexAction(ActionLink actionLink) {
		actionLink.setRequestMethod("POST");
		actionLink.setURL(converterInfo.baseURL + "workOrder/");
		actionLink.setAccept(APPLICATION_UUB_RECORD_JSON);
		actionLink.setContentType(APPLICATION_UUB_RECORD_JSON);
		RestDataGroup body = createDataGroupForBody();
		actionLink.setBody(body);
	}

	private RestDataGroup createDataGroupForBody() {
		RestDataGroup body = RestDataGroup.withNameInData("workOrder");

		RestDataGroup recordTypeGroup = RestDataGroup.withNameInData(RECORD_TYPE);
		recordTypeGroup
				.addChild(RestDataAtomic.withNameInDataAndValue("linkedRecordType", RECORD_TYPE));
		recordTypeGroup
				.addChild(RestDataAtomic.withNameInDataAndValue("linkedRecordId", recordType));

		body.addChild(recordTypeGroup);
		body.addChild(RestDataAtomic.withNameInDataAndValue("recordId", recordId));
		body.addChild(RestDataAtomic.withNameInDataAndValue("type", "index"));
		return body;
	}

	public DataGroup getDataGroup() {
		// needed for test
		return dataGroup;
	}

	protected ConverterInfo getConverterInfo() {
		return converterInfo;
	}

	public List<Action> getActions() {
		return actions;
	}

}
