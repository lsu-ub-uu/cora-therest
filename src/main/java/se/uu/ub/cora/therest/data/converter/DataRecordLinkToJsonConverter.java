package se.uu.ub.cora.therest.data.converter;

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
		addIdentifierToLink();
		return recordLinkBuilder;
	}

	private void addIdentifierToLink() {
		DataToJsonConverterFactory dataToJsonConverterFactory = new DataToJsonConverterFactoryImp();

		JsonObjectBuilder identifier = jsonFactory.createObjectBuilder();
		identifier.addKeyString("recordType", recordLink.getRecordType());
		identifier.addKeyString("recordId", recordLink.getRecordId());

		// dataToJsonConverterFactory.createForRestDataElement(jsonFactory,
		// dataLink.getIdentifier())
		// .toJsonObjectBuilder();
		recordLinkBuilder.addKeyJsonObjectBuilder("identifier", identifier);
	}
}
