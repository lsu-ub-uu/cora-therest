package epc.therest.data.converter;

import epc.therest.data.RestDataRecord;
import epc.therest.data.RestRecordList;
import epc.therest.json.builder.JsonArrayBuilder;
import epc.therest.json.builder.JsonBuilderFactory;
import epc.therest.json.builder.JsonObjectBuilder;

public class RecordListToJsonConverter {

	private JsonBuilderFactory jsonBuilderFactory;
	private RestRecordList restRecordList;
	private JsonObjectBuilder recordListJsonObjectBuilder;
	private JsonBuilderFactory jsonFactory;

	public RecordListToJsonConverter(JsonBuilderFactory jsonFactory, RestRecordList restRecordList) {
		this.jsonFactory = jsonFactory;
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

		JsonArrayBuilder recordsJsonBuilder = jsonFactory.createArrayBuilder();

		for (RestDataRecord restDataRecord : restRecordList.getRecords()) {
			DataRecordToJsonConterter converter = DataRecordToJsonConterter
					.usingJsonFactoryForRestDataRecord(jsonFactory, restDataRecord);
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
