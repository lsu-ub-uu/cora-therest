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

package se.uu.ub.cora.therest.converter.jsontorest;

import se.uu.ub.cora.json.parser.JsonObject;
import se.uu.ub.cora.json.parser.JsonParseException;
import se.uu.ub.cora.therest.data.RestDataElement;
import se.uu.ub.cora.therest.data.RestDataGroup;
import se.uu.ub.cora.therest.data.RestDataRecordLink;

public final class JsonToDataRecordLinkConverter extends JsonToDataGroupConverter
		implements JsonToDataConverter {

	private static final int MIN_NUM_OF_CHILDREN = 2;
	private static final int MAX_NUM_OF_CHILDREN = 3;

	static JsonToDataRecordLinkConverter forJsonObject(JsonObject jsonObject) {
		return new JsonToDataRecordLinkConverter(jsonObject);
	}

	private JsonToDataRecordLinkConverter(JsonObject jsonObject) {
		super(jsonObject);
	}

	@Override
	public RestDataElement toInstance() {
		RestDataRecordLink recordLink = (RestDataRecordLink) super.toInstance();
		throwErrorIfLinkChildrenAreIncorrect(recordLink);
		return recordLink;
	}

	@Override
	protected void createInstanceOfDataElement(String nameInData) {
		restDataGroup = RestDataRecordLink.withNameInData(nameInData);
	}

	private void throwErrorIfLinkChildrenAreIncorrect(RestDataGroup recordLink) {
		if (incorrectNumberOfChildren(recordLink) || missingMandatoryChildren(recordLink)
				|| maxNumOfChildrenButOptionalChildIsMissing(recordLink)) {
			throw new JsonParseException(
					"RecordLinkData must contain children with name linkedRecordType and linkedRecordId "
							+ "and might contain child with name linkedRepeatId");
		}
	}

	private boolean incorrectNumberOfChildren(RestDataGroup recordLink) {
		int numberOfChildren = recordLink.getChildren().size();
		return numberOfChildren < MIN_NUM_OF_CHILDREN || numberOfChildren > MAX_NUM_OF_CHILDREN;
	}

	private boolean missingMandatoryChildren(RestDataGroup recordLink) {
		return !recordLink.containsChildWithNameInData("linkedRecordType")
				|| !recordLink.containsChildWithNameInData("linkedRecordId");
	}

	private boolean maxNumOfChildrenButOptionalChildIsMissing(RestDataGroup recordLink) {
		return recordLink.getChildren().size() == MAX_NUM_OF_CHILDREN
				&& !recordLink.containsChildWithNameInData("linkedRepeatId");
	}
}
