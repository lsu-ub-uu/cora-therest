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

import se.uu.ub.cora.json.builder.JsonBuilderFactory;
import se.uu.ub.cora.json.builder.JsonObjectBuilder;
import se.uu.ub.cora.therest.data.ActionLink;
import se.uu.ub.cora.therest.data.RestDataAtomic;
import se.uu.ub.cora.therest.data.RestDataRecordLink;

public final class DataRecordLinkToJsonConverter extends DataGroupToJsonConverter {

	private static final String LINKED_REPEAT_ID = "linkedRepeatId";
	private RestDataRecordLink recordLink;

	private DataRecordLinkToJsonConverter(JsonBuilderFactory jsonFactory,
			RestDataRecordLink recordLink) {
		super(jsonFactory, recordLink);
		this.recordLink = recordLink;
	}

	public static DataRecordLinkToJsonConverter usingJsonFactoryForRestDataLink(
			JsonBuilderFactory jsonFactory, RestDataRecordLink dataLink) {
		return new DataRecordLinkToJsonConverter(jsonFactory, dataLink);
	}

	@Override
	public JsonObjectBuilder toJsonObjectBuilder() {
		removeEmptyLinkedRepeatId();
		possiblyAddActionLinks();
		return super.toJsonObjectBuilder();
	}

	private void removeEmptyLinkedRepeatId() {
		if (hasEmptyLinkedRepeatId()) {
			restDataGroup.removeFirstChildWithNameInData(LINKED_REPEAT_ID);
		}
	}

	private boolean hasEmptyLinkedRepeatId() {
		return restDataGroup.containsChildWithNameInData(LINKED_REPEAT_ID)
				&& ((RestDataAtomic) restDataGroup.getFirstChildWithNameInData(LINKED_REPEAT_ID))
						.getValue().equals("");
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
		ActionLinksToJsonConverter actionLinkConverter = new ActionLinksToJsonConverter(
				jsonBuilderFactory, actionLinks);
		JsonObjectBuilder actionLinksObject = actionLinkConverter.toJsonObjectBuilder();
		dataGroupJsonObjectBuilder.addKeyJsonObjectBuilder("actionLinks", actionLinksObject);
	}

}
