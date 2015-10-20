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

package se.uu.ub.cora.therest.data.converter.spider;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import se.uu.ub.cora.spider.data.Action;
import se.uu.ub.cora.therest.data.ActionLink;

public final class ActionSpiderToRestConverter {

	private String baseURL;
	private Set<Action> actions;
	private String recordType;
	private String recordId;

	public static ActionSpiderToRestConverter fromSpiderActionsWithBaseURLAndRecordTypeAndRecordId(
			Set<Action> actions, String baseURL, String recordType, String recordId) {
		return new ActionSpiderToRestConverter(actions, baseURL, recordType, recordId);
	}

	private ActionSpiderToRestConverter(Set<Action> actions, String baseURL, String recordType,
			String recordId) {
		this.actions = actions;
		this.baseURL = baseURL;
		this.recordType = recordType;
		this.recordId = recordId;
	}

	public Map<String, ActionLink> toRest() {
		Map<String, ActionLink> actionLinks = new LinkedHashMap<>();
		for (Action action : actions) {
			String url = recordType + "/" + recordId;
			ActionLink actionLink = ActionLink.withAction(action);

			if (Action.READ.equals(action)) {
				actionLink.setRequestMethod("GET");
			} else if (Action.UPDATE.equals(action)) {
				actionLink.setRequestMethod("POST");
			}else if(Action.READ_INCOMING_LINKS.equals(action)){
				actionLink.setRequestMethod("GET");
				url = url + "/incomingLinks";
			}else {
				actionLink.setRequestMethod("DELETE");
			}

			actionLink.setURL(baseURL + url);
			actionLink.setAccept("application/uub+record+json");
			actionLink.setContentType("application/uub+record+json");
			actionLinks.put(action.name().toLowerCase(), actionLink);
		}
		return actionLinks;
	}

}
