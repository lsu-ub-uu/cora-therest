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
