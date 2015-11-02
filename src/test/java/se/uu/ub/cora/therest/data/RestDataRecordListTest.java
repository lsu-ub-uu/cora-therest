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

package se.uu.ub.cora.therest.data;

import java.util.List;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class RestDataRecordListTest {
	@Test
	public void testInit2() {
		String containRecordsOfType = "metadata";
		RestRecordList restRecordList = RestRecordList
				.withContainRecordsOfType(containRecordsOfType);
		assertEquals(restRecordList.getContainRecordsOfType(), "metadata");
	}

	@Test
	public void testAddRecord() {
		RestRecordList restRecordList = RestRecordList.withContainRecordsOfType("metadata");
		RestDataGroup restDataGroup = RestDataGroup.withNameInData("restDataGroupId");
		RestDataRecord record = RestDataRecord.withRestDataGroup(restDataGroup);
		restRecordList.addRecord(record);
		List<RestDataRecord> records = restRecordList.getRecords();
		assertEquals(records.get(0), record);
	}

	@Test
	public void testTotalNo() {
		RestRecordList restRecordList = RestRecordList.withContainRecordsOfType("metadata");
		restRecordList.setTotalNo("2");
		assertEquals(restRecordList.getTotalNo(), "2");
	}

	@Test
	public void testFromNo() {
		RestRecordList restRecordList = RestRecordList.withContainRecordsOfType("metadata");
		restRecordList.setFromNo("0");
		assertEquals(restRecordList.getFromNo(), "0");
	}

	@Test
	public void testToNo() {
		RestRecordList restRecordList = RestRecordList.withContainRecordsOfType("metadata");
		restRecordList.setToNo("2");
		assertEquals(restRecordList.getToNo(), "2");
	}
}
