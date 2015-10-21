package se.uu.ub.cora.therest.data.converter;

import java.util.Map;

import se.uu.ub.cora.therest.data.ActionLink;
import se.uu.ub.cora.therest.data.RestDataRecordLink;
import se.uu.ub.cora.therest.json.builder.JsonBuilderFactory;
import se.uu.ub.cora.therest.json.builder.JsonObjectBuilder;

public final class DataRecordLinkToJsonConverter extends DataToJsonConverter {

	private RestDataRecordLink recordLink;
	private JsonObjectBuilder recordLinkBuilder;
	private JsonBuilderFactory jsonFactory;

	public static DataRecordLinkToJsonConverter usingJsonFactoryForRestDataLink(
			JsonBuilderFactory jsonFactory, RestDataRecordLink dataLink) {
		return new DataRecordLinkToJsonConverter(jsonFactory, dataLink);
	}

	private DataRecordLinkToJsonConverter(JsonBuilderFactory jsonFactory,
			RestDataRecordLink recordLink) {
		this.jsonFactory = jsonFactory;
		this.recordLink = recordLink;
		recordLinkBuilder = jsonFactory.createObjectBuilder();
	}

	@Override
	public String toJson() {
		return toJsonObjectBuilder().toJsonFormattedString();
	}

	@Override
	JsonObjectBuilder toJsonObjectBuilder() {
		possiblyAddRepeatId();
		recordLinkBuilder.addKeyString("name", recordLink.getNameInData());
		addRecordTypeAndRecordId();
		possiblyAddActionLinks();
		return recordLinkBuilder;
	}

	private void possiblyAddRepeatId() {
		if (hasNonEmptyRepeatId()) {
			recordLinkBuilder.addKeyString("repeatId", recordLink.getRepeatId());
		}
	}

	private boolean hasNonEmptyRepeatId() {
		return recordLink.getRepeatId() != null && !recordLink.getRepeatId().equals("");
	}

	private void addRecordTypeAndRecordId() {
		recordLinkBuilder.addKeyString("recordType", recordLink.getRecordType());
		recordLinkBuilder.addKeyString("recordId", recordLink.getRecordId());
	}

	private void possiblyAddActionLinks() {
		if (hasActionLinks()) {
			addActionLinksToRecordLink();
		}
	}

	private boolean hasActionLinks() {
		return !recordLink.getActionLinks().isEmpty();
	}

	private void addActionLinksToRecordLink() {
		Map<String, ActionLink> actionLinks = recordLink.getActionLinks();
		ActionLinksToJsonConverter actionLinkConverter = new ActionLinksToJsonConverter(jsonFactory,
				actionLinks);
		JsonObjectBuilder actionLinksObject = actionLinkConverter.toJsonObjectBuilder();
		recordLinkBuilder.addKeyJsonObjectBuilder("actionLinks", actionLinksObject);
	}

}
