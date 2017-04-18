/*
 * Copyright 2015, 2016 Uppsala University Library
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

package se.uu.ub.cora.therest.data.converter.spider;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import se.uu.ub.cora.spider.data.Action;
import se.uu.ub.cora.therest.data.ActionLink;
import se.uu.ub.cora.therest.data.converter.ConverterInfo;

public final class ActionSpiderToRestConverter {

	private static final String APPLICATION_UUB_RECORD_LIST_JSON = "application/uub+recordList+json";
	private static final String APPLICATION_UUB_RECORD_JSON = "application/uub+record+json";
	private ConverterInfo converterInfo;
	private List<Action> actions;
	private String recordType;
	private String recordId;

	public static ActionSpiderToRestConverter fromSpiderActionsWithBaseURLAndRecordTypeAndRecordId(
			List<Action> actions, ConverterInfo baseURL2, String recordType, String recordId) {
		return new ActionSpiderToRestConverter(actions, baseURL2, recordType, recordId);
	}

	private ActionSpiderToRestConverter(List<Action> actions, ConverterInfo baseURL2,
			String recordType, String recordId) {
		this.actions = actions;
		this.converterInfo = baseURL2;
		this.recordType = recordType;
		this.recordId = recordId;
	}

	public Map<String, ActionLink> toRest() {
		Map<String, ActionLink> actionLinks = new LinkedHashMap<>();
		for (Action action : actions) {
			String url = converterInfo.baseURL + recordType + "/";
			String urlWithRecordId = url + recordId;
			String urlForRecordTypeActions = converterInfo.baseURL + recordId + "/";
			ActionLink actionLink = ActionLink.withAction(action);

			if (Action.READ.equals(action)) {
				actionLink.setRequestMethod("GET");
				actionLink.setURL(urlWithRecordId);
				actionLink.setAccept(APPLICATION_UUB_RECORD_JSON);
			} else if (Action.UPDATE.equals(action)) {
				actionLink.setRequestMethod("POST");
				actionLink.setURL(urlWithRecordId);
				actionLink.setAccept(APPLICATION_UUB_RECORD_JSON);
				actionLink.setContentType(APPLICATION_UUB_RECORD_JSON);
			} else if (Action.READ_INCOMING_LINKS.equals(action)) {
				actionLink.setRequestMethod("GET");
				urlWithRecordId = urlWithRecordId + "/incomingLinks";
				actionLink.setURL(urlWithRecordId);
				actionLink.setAccept(APPLICATION_UUB_RECORD_LIST_JSON);
			} else if (Action.DELETE.equals(action)) {
				actionLink.setRequestMethod("DELETE");
				actionLink.setURL(urlWithRecordId);
			} else if (Action.CREATE.equals(action)) {
				actionLink.setRequestMethod("POST");
				actionLink.setURL(urlForRecordTypeActions);
				actionLink.setAccept(APPLICATION_UUB_RECORD_JSON);
				actionLink.setContentType(APPLICATION_UUB_RECORD_JSON);
			} else if (Action.UPLOAD.equals(action)) {
				actionLink.setRequestMethod("POST");
				actionLink.setURL(urlWithRecordId + "/master");
				actionLink.setContentType("multipart/form-data");
			} else if (Action.SEARCH.equals(action)) {
				actionLink.setRequestMethod("GET");
				actionLink.setURL(converterInfo.baseURL + "searchResult/" + recordId);
				actionLink.setAccept(APPLICATION_UUB_RECORD_LIST_JSON);
			} else {
				// list / search
				actionLink.setRequestMethod("GET");
				actionLink.setURL(urlForRecordTypeActions);
				actionLink.setAccept(APPLICATION_UUB_RECORD_LIST_JSON);
			}

			actionLinks.put(action.name().toLowerCase(), actionLink);
		}
		return actionLinks;
	}

}
