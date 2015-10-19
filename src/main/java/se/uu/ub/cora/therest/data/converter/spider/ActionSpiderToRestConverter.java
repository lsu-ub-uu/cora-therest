package se.uu.ub.cora.therest.data.converter.spider;

import se.uu.ub.cora.spider.data.Action;
import se.uu.ub.cora.therest.data.ActionLink;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

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
