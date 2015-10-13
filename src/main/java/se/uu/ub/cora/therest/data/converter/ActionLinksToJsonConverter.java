package se.uu.ub.cora.therest.data.converter;

import java.util.Map;
import java.util.Map.Entry;

import se.uu.ub.cora.therest.data.ActionLink;
import se.uu.ub.cora.therest.json.builder.JsonBuilderFactory;
import se.uu.ub.cora.therest.json.builder.JsonObjectBuilder;

public class ActionLinksToJsonConverter extends DataToJsonConverter {

	private Map<String, ActionLink> actionLinks;
	private JsonBuilderFactory jsonBuilderFactory;
	private JsonObjectBuilder actionLinksObject;

	public ActionLinksToJsonConverter(JsonBuilderFactory jsonBuilderFactory,
			Map<String, ActionLink> actionLinks) {
		this.jsonBuilderFactory = jsonBuilderFactory;
		this.actionLinks = actionLinks;
		actionLinksObject = jsonBuilderFactory.createObjectBuilder();
	}

	@Override
	public String toJson() {
		return toJsonObjectBuilder().toJsonFormattedString();
	}

	@Override
	JsonObjectBuilder toJsonObjectBuilder() {
		addActionLinksToBuilderObject();
		return actionLinksObject;
	}

	private void addActionLinksToBuilderObject() {

		for (Entry<String, ActionLink> actionLinkEntry : actionLinks.entrySet()) {
			ActionLink actionLink = actionLinkEntry.getValue();
			JsonObjectBuilder internalLinkBuilder = jsonBuilderFactory.createObjectBuilder();
			String actionString = actionLink.getAction().toString().toLowerCase();
			internalLinkBuilder.addKeyString("rel", actionString);
			internalLinkBuilder.addKeyString("url", actionLink.getURL());
			internalLinkBuilder.addKeyString("requestMethod", actionLink.getRequestMethod());
			internalLinkBuilder.addKeyString("accept", actionLink.getAccept());
			internalLinkBuilder.addKeyString("contentType", actionLink.getContentType());
			actionLinksObject.addKeyJsonObjectBuilder(actionString, internalLinkBuilder);
		}
	}

}
