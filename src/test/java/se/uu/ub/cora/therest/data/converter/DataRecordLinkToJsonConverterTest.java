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

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.data.Action;
import se.uu.ub.cora.json.builder.JsonBuilderFactory;
import se.uu.ub.cora.json.builder.org.OrgJsonBuilderFactoryAdapter;
import se.uu.ub.cora.therest.data.ActionLink;
import se.uu.ub.cora.therest.data.RestDataAtomic;
import se.uu.ub.cora.therest.data.RestDataGroup;
import se.uu.ub.cora.therest.data.RestDataRecordLink;

public class DataRecordLinkToJsonConverterTest {
	private RestDataRecordLink recordLink;
	private DataRecordLinkToJsonConverter converter;

	@BeforeMethod
	public void setUp() {
		recordLink = RestDataRecordLink.withNameInData("nameInData");

		RestDataAtomic linkedRecordType = RestDataAtomic.withNameInDataAndValue("linkedRecordType",
				"aRecordType");
		recordLink.addChild(linkedRecordType);

		RestDataAtomic linkedRecordId = RestDataAtomic.withNameInDataAndValue("linkedRecordId",
				"aRecordId");
		recordLink.addChild(linkedRecordId);

		JsonBuilderFactory jsonFactory = new OrgJsonBuilderFactoryAdapter();

		converter = DataRecordLinkToJsonConverter.usingJsonFactoryForRestDataLink(jsonFactory,
				recordLink);

	}

	@Test
	public void testToJson() {
		String jsonString = converter.toJson();

		assertEquals(jsonString,
				"{\"children\":[" + "{\"name\":\"linkedRecordType\",\"value\":\"aRecordType\"},"
						+ "{\"name\":\"linkedRecordId\",\"value\":\"aRecordId\"}]"
						+ ",\"name\":\"nameInData\"}");
	}

	@Test
	public void testToJsonGroupWithAttribute() {
		recordLink.addAttributeByIdWithValue("attributeNameInData", "attributeValue");
		String jsonString = converter.toJson();

		assertEquals(jsonString,
				"{\"children\":[" + "{\"name\":\"linkedRecordType\",\"value\":\"aRecordType\"},"
						+ "{\"name\":\"linkedRecordId\",\"value\":\"aRecordId\"}]"
						+ ",\"name\":\"nameInData\","
						+ "\"attributes\":{\"attributeNameInData\":\"attributeValue\"}}");
	}

	@Test
	public void testToJsonWithRepeatId() {
		recordLink.setRepeatId("22");
		String jsonString = converter.toJson();

		assertEquals(jsonString,
				"{\"repeatId\":\"22\",\"children\":["
						+ "{\"name\":\"linkedRecordType\",\"value\":\"aRecordType\"}"
						+ ",{\"name\":\"linkedRecordId\",\"value\":\"aRecordId\"}"
						+ "],\"name\":\"nameInData\"}");
	}

	@Test
	public void testToJsonWithEmptyRepeatId() {
		recordLink.setRepeatId("");
		String jsonString = converter.toJson();

		assertEquals(jsonString,
				"{\"children\":[" + "{\"name\":\"linkedRecordType\",\"value\":\"aRecordType\"}"
						+ ",{\"name\":\"linkedRecordId\",\"value\":\"aRecordId\"}"
						+ "],\"name\":\"nameInData\"}");
	}

	@Test
	public void testToJsonWithLinkedRepeatId() {
		RestDataAtomic linkedRepeatId = RestDataAtomic.withNameInDataAndValue("linkedRepeatId",
				"linkedOne");
		recordLink.addChild(linkedRepeatId);
		String jsonString = converter.toJson();

		assertEquals(jsonString,
				"{\"children\":[" + "{\"name\":\"linkedRecordType\",\"value\":\"aRecordType\"}"
						+ ",{\"name\":\"linkedRecordId\",\"value\":\"aRecordId\"}"
						+ ",{\"name\":\"linkedRepeatId\",\"value\":\"linkedOne\"}"
						+ "],\"name\":\"nameInData\"}");
	}

	@Test
	public void testToJsonWithEmptyLinkedRepeatId() {
		RestDataAtomic linkedRepeatId = RestDataAtomic.withNameInDataAndValue("linkedRepeatId", "");
		recordLink.addChild(linkedRepeatId);
		String jsonString = converter.toJson();

		assertEquals(jsonString,
				"{\"children\":[" + "{\"name\":\"linkedRecordType\",\"value\":\"aRecordType\"}"
						+ ",{\"name\":\"linkedRecordId\",\"value\":\"aRecordId\"}"
						+ "],\"name\":\"nameInData\"}");
	}

	@Test
	public void testToJsonWithLinkedPath() {
		RestDataGroup linkedPathDataGroup = RestDataGroup.withNameInData("linkedPath");
		recordLink.addChild(linkedPathDataGroup);
		String jsonString = converter.toJson();

		assertEquals(jsonString,
				"{\"children\":[" + "{\"name\":\"linkedRecordType\",\"value\":\"aRecordType\"}"
						+ ",{\"name\":\"linkedRecordId\",\"value\":\"aRecordId\"}"
						+ ",{\"name\":\"linkedPath\"}" + "],\"name\":\"nameInData\"}");
	}

	@Test
	public void testToJsonWithActionLink() {
		recordLink.addActionLink("read", createReadActionLink());

		String jsonString = converter.toJson();
		assertEquals(jsonString, "{\"children\":["
				+ "{\"name\":\"linkedRecordType\",\"value\":\"aRecordType\"}"
				+ ",{\"name\":\"linkedRecordId\",\"value\":\"aRecordId\"}"
				+ "],\"actionLinks\":{\"read\":{\"requestMethod\":\"GET\",\"rel\":\"read\""
				+ ",\"contentType\":\"application/metadata_record+json\""
				+ ",\"url\":\"http://localhost:8080/therest/rest/record/place/place:0001\""
				+ ",\"accept\":\"application/metadata_record+json\"}},\"name\":\"nameInData\"}");
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
