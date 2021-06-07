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

package se.uu.ub.cora.therest.converter.coratorest;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataList;
import se.uu.ub.cora.data.DataRecord;
import se.uu.ub.cora.therest.coradata.DataAtomicSpy;
import se.uu.ub.cora.therest.coradata.DataGroupSpy;
import se.uu.ub.cora.therest.coradata.DataListSpy;
import se.uu.ub.cora.therest.coradata.DataRecordSpy;
import se.uu.ub.cora.therest.data.RestDataGroup;
import se.uu.ub.cora.therest.data.RestDataList;
import se.uu.ub.cora.therest.data.RestDataRecord;

public class CoraDataListToRestConverterTest {
	private String baseURL = "http://localhost:8080/therest/rest/record/";

	@Test
	public void testToRest() {
		DataGroup dataGroup = new DataGroupSpy("groupId");
		dataGroup.addChild(createRecordInfo());
		DataRecord dataRecord = new DataRecordSpy(dataGroup);
		DataList dataList = new DataListSpy("place");
		dataList.addData(dataRecord);
		dataList.setTotalNo("10");
		dataList.setFromNo("1");
		dataList.setToNo("1");

		CoraToRestConverter converter = CoraDataListToRestConverter
				.fromDataListWithBaseURL(dataList, baseURL);

		RestDataList recordList = (RestDataList) converter.toRest();

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
		DataGroup dataGroup = new DataGroupSpy("groupId");
		dataGroup.addChild(createRecordInfo());
		DataList dataList = new DataListSpy("place");
		dataList.addData(dataGroup);
		dataList.setTotalNo("10");
		dataList.setFromNo("1");
		dataList.setToNo("1");

		CoraToRestConverter converter = CoraDataListToRestConverter
				.fromDataListWithBaseURL(dataList, baseURL);

		RestDataList recordList = (RestDataList) converter.toRest();

		assertEquals(recordList.getFromNo(), "1");
		assertEquals(recordList.getToNo(), "1");
		assertEquals(recordList.getTotalNo(), "10");

		RestDataGroup restDataGroup = ((RestDataGroup) recordList.getDataList().get(0));

		assertEquals(restDataGroup.getNameInData(), "groupId");
	}
}
