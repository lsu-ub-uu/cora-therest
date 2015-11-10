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

import static org.testng.Assert.assertEquals;

import java.util.List;

import org.testng.annotations.Test;

public class RestDataListTest {
	@Test
	public void testInit() {
		String containDataOfType = "metadata";
		RestDataList restDataList = RestDataList.withContainDataOfType(containDataOfType);
		assertEquals(restDataList.getContainDataOfType(), "metadata");
	}

	@Test
	public void testAddRecord() {
		RestDataList restDataList = RestDataList.withContainDataOfType("metadata");
		RestDataGroup restDataGroup = RestDataGroup.withNameInData("restDataGroupId");
		RestDataRecord record = RestDataRecord.withRestDataGroup(restDataGroup);
		restDataList.addData(record);
		List<RestData> records = restDataList.getDataList();
		assertEquals(records.get(0), record);
	}

	@Test
	public void testAddGroup() {
		RestDataList restDataList = RestDataList.withContainDataOfType("metadata");
		RestDataGroup restDataGroup = RestDataGroup.withNameInData("restDataGroupId");
		restDataList.addData(restDataGroup);
		List<RestData> groups = restDataList.getDataList();
		assertEquals(groups.get(0), restDataGroup);
	}

	@Test
	public void testTotalNo() {
		RestDataList restDataList = RestDataList.withContainDataOfType("metadata");
		restDataList.setTotalNo("2");
		assertEquals(restDataList.getTotalNo(), "2");
	}

	@Test
	public void testFromNo() {
		RestDataList restDataList = RestDataList.withContainDataOfType("metadata");
		restDataList.setFromNo("0");
		assertEquals(restDataList.getFromNo(), "0");
	}

	@Test
	public void testToNo() {
		RestDataList restDataList = RestDataList.withContainDataOfType("metadata");
		restDataList.setToNo("2");
		assertEquals(restDataList.getToNo(), "2");
	}
}
