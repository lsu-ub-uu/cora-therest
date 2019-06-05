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

package se.uu.ub.cora.therest.data.converter.spider;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import se.uu.ub.cora.spider.data.SpiderDataAtomic;
import se.uu.ub.cora.spider.data.SpiderDataGroup;
import se.uu.ub.cora.spider.data.SpiderDataList;
import se.uu.ub.cora.spider.data.SpiderDataRecord;
import se.uu.ub.cora.therest.data.RestDataGroup;
import se.uu.ub.cora.therest.data.RestDataList;
import se.uu.ub.cora.therest.data.RestDataRecord;

public class DataListSpiderToRestConverterTest {
	private String baseURL = "http://localhost:8080/therest/rest/record/";

	@Test
	public void testToRest() {
		SpiderDataGroup spiderDataGroup = SpiderDataGroup.withNameInData("groupId");
		spiderDataGroup.addChild(createRecordInfo());
		SpiderDataRecord spiderDataRecord = SpiderDataRecord.withSpiderDataGroup(spiderDataGroup);
		SpiderDataList spiderDataList = SpiderDataList.withContainDataOfType("place");
		spiderDataList.addData(spiderDataRecord);
		spiderDataList.setTotalNo("10");
		spiderDataList.setFromNo("1");
		spiderDataList.setToNo("1");

		DataListSpiderToRestConverter converter = DataListSpiderToRestConverter
				.fromSpiderDataListWithBaseURL(spiderDataList, baseURL);

		RestDataList recordList = converter.toRest();

		assertEquals(recordList.getFromNo(), "1");
		assertEquals(recordList.getToNo(), "1");
		assertEquals(recordList.getTotalNo(), "10");

		RestDataGroup restDataGroup = ((RestDataRecord) recordList.getDataList().get(0))
				.getRestDataGroup();

		assertEquals(restDataGroup.getNameInData(), "groupId");
	}

	private SpiderDataGroup createRecordInfo() {
		SpiderDataGroup recordInfo = SpiderDataGroup.withNameInData("recordInfo");
		recordInfo.addChild(SpiderDataAtomic.withNameInDataAndValue("id", "place:0001"));
		SpiderDataGroup typeGroup = SpiderDataGroup.withNameInData("type");
		typeGroup.addChild(
				SpiderDataAtomic.withNameInDataAndValue("linkedRecordType", "recordType"));
		typeGroup.addChild(SpiderDataAtomic.withNameInDataAndValue("linkedRecordId", "place"));
		recordInfo.addChild(typeGroup);
		recordInfo.addChild(SpiderDataAtomic.withNameInDataAndValue("createdBy", "userId"));
		return recordInfo;
	}

	@Test
	public void testToRestWithGroup() {
		SpiderDataGroup spiderDataGroup = SpiderDataGroup.withNameInData("groupId");
		spiderDataGroup.addChild(createRecordInfo());
		SpiderDataList spiderDataList = SpiderDataList.withContainDataOfType("place");
		spiderDataList.addData(spiderDataGroup);
		spiderDataList.setTotalNo("10");
		spiderDataList.setFromNo("1");
		spiderDataList.setToNo("1");

		DataListSpiderToRestConverter converter = DataListSpiderToRestConverter
				.fromSpiderDataListWithBaseURL(spiderDataList, baseURL);

		RestDataList recordList = converter.toRest();

		assertEquals(recordList.getFromNo(), "1");
		assertEquals(recordList.getToNo(), "1");
		assertEquals(recordList.getTotalNo(), "10");

		RestDataGroup restDataGroup = ((RestDataGroup) recordList.getDataList().get(0));

		assertEquals(restDataGroup.getNameInData(), "groupId");
	}
}
