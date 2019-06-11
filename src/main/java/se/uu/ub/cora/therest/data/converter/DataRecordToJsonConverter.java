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

import java.util.Map;

import se.uu.ub.cora.json.builder.JsonArrayBuilder;
import se.uu.ub.cora.json.builder.JsonBuilderFactory;
import se.uu.ub.cora.json.builder.JsonObjectBuilder;
import se.uu.ub.cora.therest.data.ActionLink;
import se.uu.ub.cora.therest.data.RestDataRecord;

public final class DataRecordToJsonConverter {

	private JsonBuilderFactory jsonBuilderFactory;
	private RestDataRecord restDataRecord;
	private JsonObjectBuilder recordJsonObjectBuilder;

	public static DataRecordToJsonConverter usingJsonFactoryForRestDataRecord(
			JsonBuilderFactory jsonFactory, RestDataRecord restDataRecord) {
		return new DataRecordToJsonConverter(jsonFactory, restDataRecord);
	}

	private DataRecordToJsonConverter(JsonBuilderFactory jsonFactory,
			RestDataRecord restDataRecord) {
		this.jsonBuilderFactory = jsonFactory;
		this.restDataRecord = restDataRecord;
		recordJsonObjectBuilder = jsonFactory.createObjectBuilder();
	}

	public String toJson() {
		return toJsonObjectBuilder().toJsonFormattedString();
	}

	JsonObjectBuilder toJsonObjectBuilder() {
		convertMainRestDataGroup();
		convertActionLinks();
		convertKeys();
		return createTopLevelJsonObjectWithRecordAsChild();
	}

	private void convertMainRestDataGroup() {
		DataToJsonConverterFactory dataToJsonConverterFactory = new DataToJsonConverterFactoryImp();
		DataToJsonConverter dataToJsonConverter = dataToJsonConverterFactory
				.createForRestDataElement(jsonBuilderFactory, restDataRecord.getRestDataGroup());
		JsonObjectBuilder jsonDataGroupObjectBuilder = dataToJsonConverter.toJsonObjectBuilder();
		recordJsonObjectBuilder.addKeyJsonObjectBuilder("data", jsonDataGroupObjectBuilder);
	}

	private void convertActionLinks() {
		if (recordHasActionLinks()) {
			addActionLinksToRecord();
		}
	}

	private boolean recordHasActionLinks() {
		return !restDataRecord.getActionLinks().isEmpty();
	}

	private void addActionLinksToRecord() {
		Map<String, ActionLink> actionLinks = restDataRecord.getActionLinks();
		ActionLinksToJsonConverter actionLinkConverter = new ActionLinksToJsonConverter(
				jsonBuilderFactory, actionLinks);
		JsonObjectBuilder actionLinksObject = actionLinkConverter.toJsonObjectBuilder();
		recordJsonObjectBuilder.addKeyJsonObjectBuilder("actionLinks", actionLinksObject);
	}

	private void convertKeys() {
		if (recordHasKeys()) {
			addKeysToRecord();
		}
	}

	private boolean recordHasKeys() {
		return !restDataRecord.getKeys().isEmpty();
	}

	private void addKeysToRecord() {
		JsonArrayBuilder keyBuilder = jsonBuilderFactory.createArrayBuilder();
		for (String key : restDataRecord.getKeys()) {
			keyBuilder.addString(key);
		}
		recordJsonObjectBuilder.addKeyJsonArrayBuilder("keys", keyBuilder);
	}

	private JsonObjectBuilder createTopLevelJsonObjectWithRecordAsChild() {
		JsonObjectBuilder rootWrappingJsonObjectBuilder = jsonBuilderFactory.createObjectBuilder();
		rootWrappingJsonObjectBuilder.addKeyJsonObjectBuilder("record", recordJsonObjectBuilder);
		return rootWrappingJsonObjectBuilder;
	}

}
