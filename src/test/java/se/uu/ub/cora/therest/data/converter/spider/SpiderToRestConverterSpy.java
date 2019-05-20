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
package se.uu.ub.cora.therest.data.converter.spider;

import se.uu.ub.cora.therest.data.RestDataAtomic;
import se.uu.ub.cora.therest.data.RestDataGroup;

public class SpiderToRestConverterSpy implements SpiderToRestConverter {

	public boolean toRestWasCalled = false;

	@Override
	public RestDataGroup toRest() {

		toRestWasCalled = true;
		RestDataGroup dataGroup = RestDataGroup.withNameInData("someNameInData");
		dataGroup.addChild(createRecordInfo("place"));
		return dataGroup;
	}

	private RestDataGroup createRecordInfo(String type) {
		RestDataGroup recordInfo = RestDataGroup.withNameInData("recordInfo");
		recordInfo.addChild(RestDataAtomic.withNameInDataAndValue("id", "place:0001"));
		RestDataGroup typeGroup = RestDataGroup.withNameInData("type");
		typeGroup.addChild(RestDataAtomic.withNameInDataAndValue("linkedRecordType", "recordType"));
		typeGroup.addChild(RestDataAtomic.withNameInDataAndValue("linkedRecordId", type));
		recordInfo.addChild(typeGroup);
		recordInfo.addChild(RestDataAtomic.withNameInDataAndValue("createdBy", "userId"));
		return recordInfo;
	}

}
