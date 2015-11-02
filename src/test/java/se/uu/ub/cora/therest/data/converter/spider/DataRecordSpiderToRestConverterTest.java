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

import org.testng.annotations.Test;
import se.uu.ub.cora.spider.data.Action;
import se.uu.ub.cora.spider.data.SpiderDataAtomic;
import se.uu.ub.cora.spider.data.SpiderDataGroup;
import se.uu.ub.cora.spider.data.SpiderDataRecord;
import se.uu.ub.cora.therest.data.ActionLink;
import se.uu.ub.cora.therest.data.RestDataGroup;
import se.uu.ub.cora.therest.data.RestDataRecord;
import se.uu.ub.cora.therest.data.converter.ConverterException;

import static org.testng.Assert.assertEquals;

public class DataRecordSpiderToRestConverterTest {
	private String baseURL = "http://localhost:8080/therest/rest/record/";

	@Test
	public void testToRest() {
		SpiderDataGroup spiderDataGroup = SpiderDataGroup.withNameInData("groupId");
		SpiderDataRecord spiderDataRecord = SpiderDataRecord.withSpiderDataGroup(spiderDataGroup);
		DataRecordSpiderToRestConverter dataRecordSpiderToRestConverter = DataRecordSpiderToRestConverter
				.fromSpiderDataRecordWithBaseURL(spiderDataRecord, baseURL);
		RestDataRecord restDataRecord = dataRecordSpiderToRestConverter.toRest();
		RestDataGroup restDataGroup = restDataRecord.getRestDataGroup();
		assertEquals(restDataGroup.getNameInData(), "groupId");
	}

	@Test(expectedExceptions = ConverterException.class)
	public void testToRestWithActionLinkNoRecordInfoButOtherChild() {
		SpiderDataGroup spiderDataGroup = SpiderDataGroup.withNameInData("groupId");
		spiderDataGroup.addChild(SpiderDataAtomic.withNameInDataAndValue("type", "place"));
		SpiderDataRecord spiderDataRecord = SpiderDataRecord.withSpiderDataGroup(spiderDataGroup);
		spiderDataRecord.addAction(Action.READ);

		DataRecordSpiderToRestConverter dataRecordSpiderToRestConverter = DataRecordSpiderToRestConverter
				.fromSpiderDataRecordWithBaseURL(spiderDataRecord, baseURL);
		dataRecordSpiderToRestConverter.toRest();
	}

	@Test(expectedExceptions = ConverterException.class)
	public void testToRestWithActionLinkNoRecordInfo() {
		SpiderDataGroup spiderDataGroup = SpiderDataGroup.withNameInData("groupId");
		SpiderDataRecord spiderDataRecord = SpiderDataRecord.withSpiderDataGroup(spiderDataGroup);
		spiderDataRecord.addAction(Action.READ);
		DataRecordSpiderToRestConverter dataRecordSpiderToRestConverter = DataRecordSpiderToRestConverter
				.fromSpiderDataRecordWithBaseURL(spiderDataRecord, baseURL);
		dataRecordSpiderToRestConverter.toRest();
	}

	@Test(expectedExceptions = ConverterException.class)
	public void testToRestWithActionLinkNoId() {
		SpiderDataGroup spiderDataGroup = SpiderDataGroup.withNameInData("groupId");
		SpiderDataRecord spiderDataRecord = SpiderDataRecord.withSpiderDataGroup(spiderDataGroup);
		spiderDataRecord.addAction(Action.READ);

		SpiderDataGroup recordInfo = SpiderDataGroup.withNameInData("recordInfo");
		recordInfo.addChild(SpiderDataAtomic.withNameInDataAndValue("type", "place"));
		recordInfo.addChild(SpiderDataAtomic.withNameInDataAndValue("createdBy", "userId"));
		spiderDataGroup.addChild(recordInfo);

		DataRecordSpiderToRestConverter dataRecordSpiderToRestConverter = DataRecordSpiderToRestConverter
				.fromSpiderDataRecordWithBaseURL(spiderDataRecord, baseURL);
		dataRecordSpiderToRestConverter.toRest();
	}

	@Test(expectedExceptions = ConverterException.class)
	public void testToRestWithActionLinkNoType() {
		SpiderDataGroup spiderDataGroup = SpiderDataGroup.withNameInData("groupId");
		SpiderDataRecord spiderDataRecord = SpiderDataRecord.withSpiderDataGroup(spiderDataGroup);
		spiderDataRecord.addAction(Action.READ);

		SpiderDataGroup recordInfo = SpiderDataGroup.withNameInData("recordInfo");
		recordInfo.addChild(SpiderDataAtomic.withNameInDataAndValue("id", "place:0001"));
		recordInfo.addChild(SpiderDataAtomic.withNameInDataAndValue("createdBy", "userId"));
		spiderDataGroup.addChild(recordInfo);

		DataRecordSpiderToRestConverter dataRecordSpiderToRestConverter = DataRecordSpiderToRestConverter
				.fromSpiderDataRecordWithBaseURL(spiderDataRecord, baseURL);
		dataRecordSpiderToRestConverter.toRest();
	}

	@Test
	public void testToRestWithActionLinkREAD() {
		SpiderDataGroup spiderDataGroup = SpiderDataGroup.withNameInData("groupId");
		SpiderDataRecord spiderDataRecord = SpiderDataRecord.withSpiderDataGroup(spiderDataGroup);
		spiderDataRecord.addAction(Action.READ);

		SpiderDataGroup recordInfo = SpiderDataGroup.withNameInData("recordInfo");
		recordInfo.addChild(SpiderDataAtomic.withNameInDataAndValue("id", "place:0001"));
		recordInfo.addChild(SpiderDataAtomic.withNameInDataAndValue("type", "place"));
		recordInfo.addChild(SpiderDataAtomic.withNameInDataAndValue("createdBy", "userId"));
		spiderDataGroup.addChild(recordInfo);

		DataRecordSpiderToRestConverter dataRecordSpiderToRestConverter = DataRecordSpiderToRestConverter
				.fromSpiderDataRecordWithBaseURL(spiderDataRecord, baseURL);
		RestDataRecord restDataRecord = dataRecordSpiderToRestConverter.toRest();
		ActionLink actionLink = restDataRecord.getActionLink("read");
		assertEquals(actionLink.getAction(), Action.READ);
		assertEquals(actionLink.getURL(),
				"http://localhost:8080/therest/rest/record/place/place:0001");
		assertEquals(actionLink.getRequestMethod(), "GET");
	}

	@Test
	public void testToRestWithActionLinkUPDATE() {
		SpiderDataGroup spiderDataGroup = SpiderDataGroup.withNameInData("groupId");
		SpiderDataRecord spiderDataRecord = SpiderDataRecord.withSpiderDataGroup(spiderDataGroup);
		spiderDataRecord.addAction(Action.UPDATE);

		SpiderDataGroup recordInfo = SpiderDataGroup.withNameInData("recordInfo");
		recordInfo.addChild(SpiderDataAtomic.withNameInDataAndValue("id", "place:0001"));
		recordInfo.addChild(SpiderDataAtomic.withNameInDataAndValue("type", "place"));
		recordInfo.addChild(SpiderDataAtomic.withNameInDataAndValue("createdBy", "userId"));
		spiderDataGroup.addChild(recordInfo);

		DataRecordSpiderToRestConverter dataRecordSpiderToRestConverter = DataRecordSpiderToRestConverter
				.fromSpiderDataRecordWithBaseURL(spiderDataRecord, baseURL);
		RestDataRecord restDataRecord = dataRecordSpiderToRestConverter.toRest();
		ActionLink actionLink = restDataRecord.getActionLink("update");
		assertEquals(actionLink.getAction(), Action.UPDATE);
		assertEquals(actionLink.getURL(),
				"http://localhost:8080/therest/rest/record/place/place:0001");
		assertEquals(actionLink.getRequestMethod(), "POST");
	}

	@Test
	public void testToRestWithActionLinkDELETE() {
		SpiderDataGroup spiderDataGroup = SpiderDataGroup.withNameInData("groupId");
		SpiderDataRecord spiderDataRecord = SpiderDataRecord.withSpiderDataGroup(spiderDataGroup);
		spiderDataRecord.addAction(Action.DELETE);

		SpiderDataGroup recordInfo = SpiderDataGroup.withNameInData("recordInfo");
		recordInfo.addChild(SpiderDataAtomic.withNameInDataAndValue("id", "place:0001"));
		recordInfo.addChild(SpiderDataAtomic.withNameInDataAndValue("type", "place"));
		recordInfo.addChild(SpiderDataAtomic.withNameInDataAndValue("createdBy", "userId"));
		spiderDataGroup.addChild(recordInfo);

		DataRecordSpiderToRestConverter dataRecordSpiderToRestConverter = DataRecordSpiderToRestConverter
				.fromSpiderDataRecordWithBaseURL(spiderDataRecord, baseURL);
		RestDataRecord restDataRecord = dataRecordSpiderToRestConverter.toRest();
		ActionLink actionLink = restDataRecord.getActionLink("delete");
		assertEquals(actionLink.getAction(), Action.DELETE);
		assertEquals(actionLink.getURL(),
				"http://localhost:8080/therest/rest/record/place/place:0001");
		assertEquals(actionLink.getRequestMethod(), "DELETE");
	}

	@Test
	public void testToRestWithKeys() {
		SpiderDataGroup spiderDataGroup = SpiderDataGroup.withNameInData("groupId");
		SpiderDataRecord spiderDataRecord = SpiderDataRecord.withSpiderDataGroup(spiderDataGroup);
		spiderDataRecord.addKey("KEY1");

		DataRecordSpiderToRestConverter dataRecordSpiderToRestConverter = DataRecordSpiderToRestConverter
				.fromSpiderDataRecordWithBaseURL(spiderDataRecord, baseURL);
		RestDataRecord restDataRecord = dataRecordSpiderToRestConverter.toRest();
		String key = restDataRecord.getKeys().iterator().next();
		assertEquals(key, "KEY1");

	}
}
