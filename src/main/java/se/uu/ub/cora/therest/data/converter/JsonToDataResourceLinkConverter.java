/*
 * Copyright 2019 Uppsala University Library
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

import se.uu.ub.cora.json.parser.JsonObject;
import se.uu.ub.cora.json.parser.JsonParseException;
import se.uu.ub.cora.therest.data.RestDataElement;
import se.uu.ub.cora.therest.data.RestDataGroup;
import se.uu.ub.cora.therest.data.RestDataResourceLink;

public class JsonToDataResourceLinkConverter extends JsonToDataGroupConverter
		implements JsonToDataConverter {

	private static final int NUM_OF_RESOURCELINK_CHILDREN = 4;

	private JsonToDataResourceLinkConverter(JsonObject jsonObject) {
		super(jsonObject);
	}

	@Override
	public RestDataElement toInstance() {
		RestDataResourceLink resourceLink = (RestDataResourceLink) super.toInstance();
		throwErrorIfLinkChildrenAreIncorrect(resourceLink);
		return resourceLink;
	}

	private void throwErrorIfLinkChildrenAreIncorrect(RestDataGroup recordLink) {
		if (incorrectNumberOfChildren(recordLink) || incorrectChildren(recordLink)) {
			throw new JsonParseException(
					"RecordLinkData must and can only contain children with name "
							+ "streamId and filename and filesize and mimeType");
		}
	}

	private boolean incorrectNumberOfChildren(RestDataGroup recordLink) {
		return recordLink.getChildren().size() != NUM_OF_RESOURCELINK_CHILDREN;
	}

	private boolean incorrectChildren(RestDataGroup recordLink) {
		return !recordLink.containsChildWithNameInData("streamId")
				|| !recordLink.containsChildWithNameInData("filename")
				|| !recordLink.containsChildWithNameInData("filesize")
				|| !recordLink.containsChildWithNameInData("mimeType");
	}

	public static JsonToDataResourceLinkConverter forJsonObject(JsonObject jsonObject) {
		return new JsonToDataResourceLinkConverter(jsonObject);
	}

	@Override
	protected void createInstanceOfDataElement(String nameInData) {
		restDataGroup = RestDataResourceLink.withNameInData(nameInData);
	}

}
