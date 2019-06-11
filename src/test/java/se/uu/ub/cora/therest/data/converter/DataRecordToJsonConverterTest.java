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
import se.uu.ub.cora.spider.data.Action;
import se.uu.ub.cora.therest.data.ActionLink;
import se.uu.ub.cora.therest.data.RestDataGroup;
import se.uu.ub.cora.therest.data.RestDataRecord;

public class DataRecordToJsonConverterTest {
	@Test
	public void testToJson() {
		RestDataGroup restDataGroup = RestDataGroup.withNameInData("groupNameInData");
		RestDataRecord restDataRecord = RestDataRecord.withRestDataGroup(restDataGroup);

		JsonBuilderFactory jsonFactory = new OrgJsonBuilderFactoryAdapter();
		DataRecordToJsonConverter dataRecordToJsonConverter = DataRecordToJsonConverter
				.usingJsonFactoryForRestDataRecord(jsonFactory, restDataRecord);
		String jsonString = dataRecordToJsonConverter.toJson();

		assertEquals(jsonString, "{\"record\":{\"data\":{\"name\":\"groupNameInData\"}}}");
	}

	@Test
	public void testToJsonWithKey() {
		RestDataGroup restDataGroup = RestDataGroup.withNameInData("groupNameInData");
		RestDataRecord restDataRecord = RestDataRecord.withRestDataGroup(restDataGroup);
		restDataRecord.addKey("KEY1");

		JsonBuilderFactory jsonFactory = new OrgJsonBuilderFactoryAdapter();
		DataRecordToJsonConverter dataRecordToJsonConverter = DataRecordToJsonConverter
				.usingJsonFactoryForRestDataRecord(jsonFactory, restDataRecord);
		String jsonString = dataRecordToJsonConverter.toJson();

		assertEquals(jsonString, "{\"record\":{\"data\":{\"name\":\"groupNameInData\"}"
				+ ",\"keys\":[\"KEY1\"]" + "}}");
	}

	@Test
	public void testToJsonWithKeys() {
		RestDataGroup restDataGroup = RestDataGroup.withNameInData("groupNameInData");
		RestDataRecord restDataRecord = RestDataRecord.withRestDataGroup(restDataGroup);
		restDataRecord.addKey("KEY1");
		restDataRecord.addKey("KEY2");
		restDataRecord.addKey("KEY3");

		JsonBuilderFactory jsonFactory = new OrgJsonBuilderFactoryAdapter();
		DataRecordToJsonConverter dataRecordToJsonConverter = DataRecordToJsonConverter
				.usingJsonFactoryForRestDataRecord(jsonFactory, restDataRecord);
		String jsonString = dataRecordToJsonConverter.toJson();

		assertEquals(jsonString, "{\"record\":{\"data\":{\"name\":\"groupNameInData\"}"
				+ ",\"keys\":[\"KEY1\",\"KEY2\",\"KEY3\"]" + "}}");
	}

	@Test
	public void testToJsonWithActionLinks() {
		RestDataGroup restDataGroup = RestDataGroup.withNameInData("groupNameInData");
		RestDataRecord restDataRecord = RestDataRecord.withRestDataGroup(restDataGroup);
		restDataRecord.addActionLink("read", createReadActionLink());

		JsonBuilderFactory jsonFactory = new OrgJsonBuilderFactoryAdapter();
		DataRecordToJsonConverter dataRecordToJsonConverter = DataRecordToJsonConverter
				.usingJsonFactoryForRestDataRecord(jsonFactory, restDataRecord);
		String jsonString = dataRecordToJsonConverter.toJson();

		assertEquals(jsonString, "{\"record\":{\"data\":{\"name\":\"groupNameInData\"}"
				+ ",\"actionLinks\":{" + "\"read\":{\"requestMethod\":\"GET\",\"rel\":\"read\","
				+ "\"contentType\":\"application/metadata_record+json\","
				+ "\"url\":\"http://localhost:8080/therest/rest/record/place/place:0001\","
				+ "\"accept\":\"application/metadata_record+json\"}" + "}}}");
	}

	private ActionLink createReadActionLink() {
		ActionLink actionLink = ActionLink.withAction(Action.READ);
		actionLink.setAccept("application/metadata_record+json");
		actionLink.setContentType("application/metadata_record+json");
		actionLink.setRequestMethod("GET");
		actionLink.setURL("http://localhost:8080/therest/rest/record/place/place:0001");
		return actionLink;
	}

}
