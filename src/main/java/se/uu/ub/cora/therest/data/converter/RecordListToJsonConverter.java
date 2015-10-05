package se.uu.ub.cora.therest.data.converter;

import se.uu.ub.cora.therest.data.RestDataRecord;
import se.uu.ub.cora.therest.data.RestRecordList;
import se.uu.ub.cora.therest.json.builder.JsonArrayBuilder;
import se.uu.ub.cora.therest.json.builder.JsonBuilderFactory;
import se.uu.ub.cora.therest.json.builder.JsonObjectBuilder;

public class RecordListToJsonConverter {

	private JsonBuilderFactory jsonBuilderFactory;
	private RestRecordList restRecordList;
	private JsonObjectBuilder recordListJsonObjectBuilder;

	public RecordListToJsonConverter(JsonBuilderFactory jsonFactory, RestRecordList restRecordList) {
		this.jsonBuilderFactory = jsonFactory;
		this.restRecordList = restRecordList;
		recordListJsonObjectBuilder = jsonFactory.createObjectBuilder();
	}

	public String toJson() {
		return toJsonObjectBuilder().toJsonFormattedString();
	}

	JsonObjectBuilder toJsonObjectBuilder() {

		recordListJsonObjectBuilder.addKeyString("totalNo", restRecordList.getTotalNo());
		recordListJsonObjectBuilder.addKeyString("fromNo", restRecordList.getFromNo());
		recordListJsonObjectBuilder.addKeyString("toNo", restRecordList.getToNo());
		recordListJsonObjectBuilder.addKeyString("containRecordsOfType",
				restRecordList.getContainRecordsOfType());

		JsonArrayBuilder recordsJsonBuilder = jsonBuilderFactory.createArrayBuilder();

		for (RestDataRecord restDataRecord : restRecordList.getRecords()) {
			DataRecordToJsonConverter converter = DataRecordToJsonConverter
					.usingJsonFactoryForRestDataRecord(jsonBuilderFactory, restDataRecord);
			recordsJsonBuilder.addJsonObjectBuilder(converter.toJsonObjectBuilder());
		}

		recordListJsonObjectBuilder.addKeyJsonArrayBuilder("records", recordsJsonBuilder);

		// create surrounding json object that only has "recordList"and noOfRecords as its child
		JsonObjectBuilder rootWrappingJsonObjectBuilder = jsonBuilderFactory.createObjectBuilder();
		rootWrappingJsonObjectBuilder.addKeyJsonObjectBuilder("recordList",
				recordListJsonObjectBuilder);
		return rootWrappingJsonObjectBuilder;
	}

}
