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

import se.uu.ub.cora.json.builder.JsonArrayBuilder;
import se.uu.ub.cora.json.builder.JsonBuilderFactory;
import se.uu.ub.cora.json.builder.JsonObjectBuilder;
import se.uu.ub.cora.therest.data.RestData;
import se.uu.ub.cora.therest.data.RestDataGroup;
import se.uu.ub.cora.therest.data.RestDataList;
import se.uu.ub.cora.therest.data.RestDataRecord;

public final class DataListToJsonConverter {

	public static DataListToJsonConverter usingJsonFactoryForRestDataList(
			JsonBuilderFactory jsonFactory, RestDataList restRecordList) {
		return new DataListToJsonConverter(jsonFactory, restRecordList);
	}

	private JsonBuilderFactory jsonBuilderFactory;
	private RestDataList restRecordList;
	private JsonObjectBuilder recordListJsonObjectBuilder;

	private DataListToJsonConverter(JsonBuilderFactory jsonFactory, RestDataList restRecordList) {
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
		recordListJsonObjectBuilder.addKeyString("containDataOfType",
				restRecordList.getContainDataOfType());

		JsonArrayBuilder recordsJsonBuilder = jsonBuilderFactory.createArrayBuilder();

		for (RestData restData : restRecordList.getDataList()) {
			convertRestToJsonBuilder(recordsJsonBuilder, restData);
		}

		recordListJsonObjectBuilder.addKeyJsonArrayBuilder("data", recordsJsonBuilder);

		JsonObjectBuilder rootWrappingJsonObjectBuilder = jsonBuilderFactory.createObjectBuilder();
		rootWrappingJsonObjectBuilder.addKeyJsonObjectBuilder("dataList",
				recordListJsonObjectBuilder);
		return rootWrappingJsonObjectBuilder;
	}

	private void convertRestToJsonBuilder(JsonArrayBuilder recordsJsonBuilder, RestData restData) {
		if (restData instanceof RestDataRecord) {
			convertRestRecordToJsonBuilder(recordsJsonBuilder, restData);
		} else {
			convertRestGroupToJsonBuilder(recordsJsonBuilder, restData);
		}
	}

	private void convertRestRecordToJsonBuilder(JsonArrayBuilder recordsJsonBuilder,
			RestData restData) {
		DataRecordToJsonConverter converter = DataRecordToJsonConverter
				.usingJsonFactoryForRestDataRecord(jsonBuilderFactory, (RestDataRecord) restData);
		recordsJsonBuilder.addJsonObjectBuilder(converter.toJsonObjectBuilder());
	}

	private void convertRestGroupToJsonBuilder(JsonArrayBuilder recordsJsonBuilder,
			RestData restData) {
		DataGroupToJsonConverter converter = DataGroupToJsonConverter
				.usingJsonFactoryForRestDataGroup(jsonBuilderFactory, (RestDataGroup) restData);
		recordsJsonBuilder.addJsonObjectBuilder(converter.toJsonObjectBuilder());
	}

}
