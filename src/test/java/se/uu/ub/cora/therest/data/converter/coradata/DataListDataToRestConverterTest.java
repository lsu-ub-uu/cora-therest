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

package se.uu.ub.cora.therest.data.converter.coradata;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataList;
import se.uu.ub.cora.data.DataRecord;
import se.uu.ub.cora.therest.data.DataAtomicSpy;
import se.uu.ub.cora.therest.data.DataGroupSpy;
import se.uu.ub.cora.therest.data.DataListSpy;
import se.uu.ub.cora.therest.data.DataRecordSpy;
import se.uu.ub.cora.therest.data.RestDataGroup;
import se.uu.ub.cora.therest.data.RestDataList;
import se.uu.ub.cora.therest.data.RestDataRecord;
import se.uu.ub.cora.therest.data.converter.coradata.DataListDataToRestConverter;

public class DataListDataToRestConverterTest {
	private String baseURL = "http://localhost:8080/therest/rest/record/";

	@Test
	public void testToRest() {
		DataGroup spiderDataGroup = new DataGroupSpy("groupId");
		spiderDataGroup.addChild(createRecordInfo());
		DataRecord spiderDataRecord = new DataRecordSpy(spiderDataGroup);
		DataList spiderDataList = new DataListSpy("place");
		spiderDataList.addData(spiderDataRecord);
		spiderDataList.setTotalNo("10");
		spiderDataList.setFromNo("1");
		spiderDataList.setToNo("1");

		DataListDataToRestConverter converter = DataListDataToRestConverter
				.fromSpiderDataListWithBaseURL(spiderDataList, baseURL);

		RestDataList recordList = converter.toRest();

		assertEquals(recordList.getFromNo(), "1");
		assertEquals(recordList.getToNo(), "1");
		assertEquals(recordList.getTotalNo(), "10");

		RestDataGroup restDataGroup = ((RestDataRecord) recordList.getDataList().get(0))
				.getRestDataGroup();

		assertEquals(restDataGroup.getNameInData(), "groupId");
	}

	private DataGroup createRecordInfo() {
		DataGroup recordInfo = new DataGroupSpy("recordInfo");
		recordInfo.addChild(new DataAtomicSpy("id", "place:0001"));
		DataGroup typeGroup = new DataGroupSpy("type");
		typeGroup.addChild(new DataAtomicSpy("linkedRecordType", "recordType"));
		typeGroup.addChild(new DataAtomicSpy("linkedRecordId", "place"));
		recordInfo.addChild(typeGroup);
		recordInfo.addChild(new DataAtomicSpy("createdBy", "userId"));
		return recordInfo;
	}

	@Test
	public void testToRestWithGroup() {
		DataGroup spiderDataGroup = new DataGroupSpy("groupId");
		spiderDataGroup.addChild(createRecordInfo());
		DataList spiderDataList = new DataListSpy("place");
		spiderDataList.addData(spiderDataGroup);
		spiderDataList.setTotalNo("10");
		spiderDataList.setFromNo("1");
		spiderDataList.setToNo("1");

		DataListDataToRestConverter converter = DataListDataToRestConverter
				.fromSpiderDataListWithBaseURL(spiderDataList, baseURL);

		RestDataList recordList = converter.toRest();

		assertEquals(recordList.getFromNo(), "1");
		assertEquals(recordList.getToNo(), "1");
		assertEquals(recordList.getTotalNo(), "10");

		RestDataGroup restDataGroup = ((RestDataGroup) recordList.getDataList().get(0));

		assertEquals(restDataGroup.getNameInData(), "groupId");
	}
}
