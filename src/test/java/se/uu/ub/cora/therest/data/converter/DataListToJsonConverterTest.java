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

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import se.uu.ub.cora.json.builder.JsonBuilderFactory;
import se.uu.ub.cora.json.builder.org.OrgJsonBuilderFactoryAdapter;
import se.uu.ub.cora.therest.data.RestDataGroup;
import se.uu.ub.cora.therest.data.RestDataList;
import se.uu.ub.cora.therest.data.RestDataRecord;

public class DataListToJsonConverterTest {
	@Test
	public void testToJson() {
		RestDataList restDataList = RestDataList.withContainDataOfType("place");
		RestDataGroup restDataGroup = RestDataGroup.withNameInData("groupId");
		RestDataRecord restDataRecord = RestDataRecord.withRestDataGroup(restDataGroup);
		restDataList.addData(restDataRecord);
		restDataList.setTotalNo("1");
		restDataList.setFromNo("0");
		restDataList.setToNo("1");

		JsonBuilderFactory jsonFactory = new OrgJsonBuilderFactoryAdapter();
		DataListToJsonConverter recordListToJsonConverter = DataListToJsonConverter.usingJsonFactoryForRestDataList(jsonFactory, restDataList);
		String jsonString = recordListToJsonConverter.toJson();
		assertEquals(jsonString,
				"{\"dataList\":{\"fromNo\":\"0\",\""
						+ "data\":[{\"record\":{\"data\":{\"name\":\"groupId\"}}}],"
						+ "\"totalNo\":\"1\",\"containDataOfType\":\"place\",\"toNo\":\"1\"}}");
	}

	@Test
	public void testToJsonWithGroup() {
		RestDataList restDataList = RestDataList.withContainDataOfType("place");
		RestDataGroup restDataGroup = RestDataGroup.withNameInData("groupId");
		restDataList.addData(restDataGroup);
		restDataList.setTotalNo("1");
		restDataList.setFromNo("0");
		restDataList.setToNo("1");

		JsonBuilderFactory jsonFactory = new OrgJsonBuilderFactoryAdapter();
		DataListToJsonConverter recordListToJsonConverter = DataListToJsonConverter.usingJsonFactoryForRestDataList(jsonFactory, restDataList);
		String jsonString = recordListToJsonConverter.toJson();
		assertEquals(jsonString,
				"{\"dataList\":{\"fromNo\":\"0\",\"" + "data\":[{\"name\":\"groupId\"}],"
						+ "\"totalNo\":\"1\",\"containDataOfType\":\"place\",\"toNo\":\"1\"}}");
	}


}
