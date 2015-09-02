package epc.therest.data.converter;

import java.util.Map;
import java.util.Map.Entry;

import epc.therest.data.ActionLink;
import epc.therest.data.RestDataRecord;
import epc.therest.json.builder.JsonArrayBuilder;
import epc.therest.json.builder.JsonBuilderFactory;
import epc.therest.json.builder.JsonObjectBuilder;

public final class DataRecordToJsonConverter {

	public static DataRecordToJsonConverter usingJsonFactoryForRestDataRecord(
			JsonBuilderFactory jsonFactory, RestDataRecord restDataRecord) {
		return new DataRecordToJsonConverter(jsonFactory, restDataRecord);
	}

	private JsonBuilderFactory jsonBuilderFactory;
	private RestDataRecord restDataRecord;
	private JsonObjectBuilder recordJsonObjectBuilder;

	private DataRecordToJsonConverter(JsonBuilderFactory jsonFactory, RestDataRecord restDataRecord) {
		this.jsonBuilderFactory = jsonFactory;
		this.restDataRecord = restDataRecord;
		recordJsonObjectBuilder = jsonFactory.createObjectBuilder();
	}

	public String toJson() {

		return toJsonObjectBuilder().toJsonFormattedString();
	}

	JsonObjectBuilder toJsonObjectBuilder() {
		// convert restDataGroup using existing converter
		DataToJsonConverterFactory dataToJsonConverterFactory = new DataToJsonConverterFactoryImp();
		DataToJsonConverter dataToJsonConverter = dataToJsonConverterFactory
				.createForRestDataElement(jsonBuilderFactory, restDataRecord.getRestDataGroup());
		JsonObjectBuilder jsonDataGroupObjectBuilder = dataToJsonConverter.toJsonObjectBuilder();
		recordJsonObjectBuilder.addKeyJsonObjectBuilder("data", jsonDataGroupObjectBuilder);

		// move actionLinks to this class
		if (!restDataRecord.getActionLinks().isEmpty()) {
			addActionLinksToRecord();
		}

		// add key support
		if (!restDataRecord.getKeys().isEmpty()) {
			JsonArrayBuilder keyBuilder = jsonBuilderFactory.createArrayBuilder();
			for (String key : restDataRecord.getKeys()) {
				keyBuilder.addString(key);
			}
			recordJsonObjectBuilder.addKeyJsonArrayBuilder("keys", keyBuilder);
		}

		// create surrounding json object that only has "record" as its child
		JsonObjectBuilder rootWrappingJsonObjectBuilder = jsonBuilderFactory.createObjectBuilder();
		rootWrappingJsonObjectBuilder.addKeyJsonObjectBuilder("record", recordJsonObjectBuilder);
		return rootWrappingJsonObjectBuilder;
	}

	private void addActionLinksToRecord() {
		JsonObjectBuilder actionLinksObject = jsonBuilderFactory.createObjectBuilder();
		Map<String, ActionLink> actionLinks = restDataRecord.getActionLinks();

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
		recordJsonObjectBuilder.addKeyJsonObjectBuilder("actionLinks", actionLinksObject);
	}
}
