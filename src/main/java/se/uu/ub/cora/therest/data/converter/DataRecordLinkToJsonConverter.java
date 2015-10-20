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