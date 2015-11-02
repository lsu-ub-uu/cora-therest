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

import org.testng.annotations.Test;
import se.uu.ub.cora.therest.data.RestDataGroup;
import se.uu.ub.cora.therest.data.RestDataRecord;
import se.uu.ub.cora.therest.data.RestRecordList;
import se.uu.ub.cora.therest.json.builder.JsonBuilderFactory;
import se.uu.ub.cora.therest.json.builder.org.OrgJsonBuilderFactoryAdapter;

import static org.testng.Assert.assertEquals;

public class RecordListToJsonConverterTest {
	@Test
	public void testToJson() {
		RestRecordList restRecordList = RestRecordList.withContainRecordsOfType("place");
		RestDataGroup restDataGroup = RestDataGroup.withNameInData("groupId");
		RestDataRecord restDataRecord = RestDataRecord.withRestDataGroup(restDataGroup);
		restRecordList.addRecord(restDataRecord);
		restRecordList.setTotalNo("1");
		restRecordList.setFromNo("0");
		restRecordList.setToNo("1");

		JsonBuilderFactory jsonFactory = new OrgJsonBuilderFactoryAdapter();
		RecordListToJsonConverter recordListToJsonConverter = new RecordListToJsonConverter(
				jsonFactory, restRecordList);
		String jsonString = recordListToJsonConverter.toJson();
		assertEquals(jsonString, "{\"recordList\":{\"fromNo\":\"0\",\""
				+ "records\":[{\"record\":{\"data\":{\"name\":\"groupId\"}}}],"
				+ "\"totalNo\":\"1\",\"toNo\":\"1\",\"containRecordsOfType\":\"place\"}}");

	}

}
