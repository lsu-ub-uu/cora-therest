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
		recordLinkBuilder.addKeyString("name", recordLink.getNameInData());
		addRecordTypeAndRecordIdToRecordLink();
		possiblyAddActionLinksToRecordLink();
		return recordLinkBuilder;
	}

	private void addRecordTypeAndRecordIdToRecordLink() {
		recordLinkBuilder.addKeyString("recordType", recordLink.getRecordType());
		recordLinkBuilder.addKeyString("recordId", recordLink.getRecordId());
	}

	private void possiblyAddActionLinksToRecordLink() {
		if (recordLinkHasActionLinks()) {
			addActionLinksToRecordLink();
		}
	}

	private boolean recordLinkHasActionLinks() {
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
