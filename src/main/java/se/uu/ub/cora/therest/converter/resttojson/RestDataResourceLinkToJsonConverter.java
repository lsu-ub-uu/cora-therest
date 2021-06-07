/*
 * Copyright 2015, 2016 Uppsala University Library
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

package se.uu.ub.cora.therest.converter.resttojson;

import java.util.Map;

import se.uu.ub.cora.json.builder.JsonBuilderFactory;
import se.uu.ub.cora.json.builder.JsonObjectBuilder;
import se.uu.ub.cora.therest.data.ActionLink;
import se.uu.ub.cora.therest.data.RestDataResourceLink;

public final class RestDataResourceLinkToJsonConverter extends RestDataGroupToJsonConverter {

	private RestDataResourceLink resourceLink;

	private RestDataResourceLinkToJsonConverter(JsonBuilderFactory jsonFactory,
			RestDataResourceLink resourceLink) {
		super(jsonFactory, resourceLink);
		this.resourceLink = resourceLink;
	}

	public static RestDataResourceLinkToJsonConverter usingJsonFactoryForRestDataLink(
			JsonBuilderFactory jsonFactory, RestDataResourceLink resourceLink) {
		return new RestDataResourceLinkToJsonConverter(jsonFactory, resourceLink);
	}

	@Override
	public JsonObjectBuilder toJsonObjectBuilder() {
		possiblyAddActionLinks();
		return super.toJsonObjectBuilder();
	}

	private void possiblyAddActionLinks() {
		if (hasActionLinks()) {
			addActionLinksToRecordLink();
		}
	}

	private boolean hasActionLinks() {
		return !resourceLink.getActionLinks().isEmpty();
	}

	private void addActionLinksToRecordLink() {
		Map<String, ActionLink> actionLinks = resourceLink.getActionLinks();
		RestDataActionLinkToJsonConverter actionLinkConverter = new RestDataActionLinkToJsonConverter(
				jsonBuilderFactory, actionLinks);
		JsonObjectBuilder actionLinksObject = actionLinkConverter.toJsonObjectBuilder();
		dataGroupJsonObjectBuilder.addKeyJsonObjectBuilder("actionLinks", actionLinksObject);
	}

}
