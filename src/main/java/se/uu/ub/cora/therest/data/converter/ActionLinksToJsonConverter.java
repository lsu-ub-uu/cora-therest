package se.uu.ub.cora.therest.data.converter;

import java.util.Map;

import se.uu.ub.cora.therest.data.ActionLink;
import se.uu.ub.cora.therest.json.builder.JsonBuilderFactory;
import se.uu.ub.cora.therest.json.builder.JsonObjectBuilder;

public class ActionLinksToJsonConverter extends DataToJsonConverter {

	private Map<String, ActionLink> actionLinks;

	public ActionLinksToJsonConverter(JsonBuilderFactory jsonFactory,
			Map<String, ActionLink> actionLinks) {
		this.actionLinks = actionLinks;
	}

	@Override
	public String toJson() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	JsonObjectBuilder toJsonObjectBuilder() {
		// TODO Auto-generated method stub
		return null;
	}

}
