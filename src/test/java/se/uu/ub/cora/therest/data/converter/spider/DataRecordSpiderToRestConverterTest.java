/*
 * Copyright 2015, 2016 Uppsala University Library
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

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.spider.data.Action;
import se.uu.ub.cora.spider.data.SpiderDataAtomic;
import se.uu.ub.cora.spider.data.SpiderDataGroup;
import se.uu.ub.cora.spider.data.SpiderDataRecord;
import se.uu.ub.cora.spider.data.SpiderDataResourceLink;
import se.uu.ub.cora.therest.data.ActionLink;
import se.uu.ub.cora.therest.data.RestDataGroup;
import se.uu.ub.cora.therest.data.RestDataRecord;
import se.uu.ub.cora.therest.data.RestDataResourceLink;
import se.uu.ub.cora.therest.data.converter.ConverterException;

public class DataRecordSpiderToRestConverterTest {
	private String baseURL = "http://localhost:8080/therest/rest/record/";
	private SpiderDataGroup spiderDataGroup;
	private SpiderDataRecord spiderDataRecord;
	private DataRecordSpiderToRestConverter dataRecordSpiderToRestConverter;

	@BeforeMethod
	public void setUp() {
		spiderDataGroup = SpiderDataGroup.withNameInData("groupId");
		spiderDataRecord = SpiderDataRecord.withSpiderDataGroup(spiderDataGroup);
		dataRecordSpiderToRestConverter = DataRecordSpiderToRestConverter
				.fromSpiderDataRecordWithBaseURL(spiderDataRecord, baseURL);

	}

	@Test
	public void testToRest() {
		spiderDataGroup.addChild(createRecordInfo());

		RestDataRecord restDataRecord = dataRecordSpiderToRestConverter.toRest();
		RestDataGroup restDataGroup = restDataRecord.getRestDataGroup();
		assertEquals(restDataGroup.getNameInData(), "groupId");
	}

	@Test(expectedExceptions = ConverterException.class)
	public void testToRestWithActionLinkNoRecordInfoButOtherChild() {
		spiderDataGroup.addChild(SpiderDataAtomic.withNameInDataAndValue("type", "place"));
		spiderDataRecord.addAction(Action.READ);
		dataRecordSpiderToRestConverter.toRest();
	}

	@Test(expectedExceptions = ConverterException.class)
	public void testToRestWithActionLinkNoRecordInfo() {
		spiderDataRecord.addAction(Action.READ);
		dataRecordSpiderToRestConverter.toRest();
	}

	@Test(expectedExceptions = ConverterException.class)
	public void testToRestWithActionLinkNoId() {
		spiderDataRecord.addAction(Action.READ);

		SpiderDataGroup recordInfo = SpiderDataGroup.withNameInData("recordInfo");
		recordInfo.addChild(SpiderDataAtomic.withNameInDataAndValue("type", "place"));
		recordInfo.addChild(SpiderDataAtomic.withNameInDataAndValue("createdBy", "userId"));
		spiderDataGroup.addChild(recordInfo);

		dataRecordSpiderToRestConverter.toRest();
	}

	@Test(expectedExceptions = ConverterException.class)
	public void testToRestWithActionLinkNoType() {
		spiderDataRecord.addAction(Action.READ);

		SpiderDataGroup recordInfo = SpiderDataGroup.withNameInData("recordInfo");
		recordInfo.addChild(SpiderDataAtomic.withNameInDataAndValue("id", "place:0001"));
		recordInfo.addChild(SpiderDataAtomic.withNameInDataAndValue("createdBy", "userId"));
		spiderDataGroup.addChild(recordInfo);

		dataRecordSpiderToRestConverter.toRest();
	}

	@Test
	public void testToRestWithActionLinkREAD() {
		spiderDataRecord.addAction(Action.READ);

		spiderDataGroup.addChild(createRecordInfo());

		RestDataRecord restDataRecord = dataRecordSpiderToRestConverter.toRest();
		ActionLink actionLink = restDataRecord.getActionLink("read");
		assertEquals(actionLink.getAction(), Action.READ);
		assertEquals(actionLink.getURL(),
				"http://localhost:8080/therest/rest/record/place/place:0001");
		assertEquals(actionLink.getRequestMethod(), "GET");
	}

	private SpiderDataGroup createRecordInfo() {
		SpiderDataGroup recordInfo = SpiderDataGroup.withNameInData("recordInfo");
		recordInfo.addChild(SpiderDataAtomic.withNameInDataAndValue("id", "place:0001"));
		recordInfo.addChild(SpiderDataAtomic.withNameInDataAndValue("type", "place"));
		recordInfo.addChild(SpiderDataAtomic.withNameInDataAndValue("createdBy", "userId"));
		return recordInfo;
	}

	@Test
	public void testToRestWithActionLinkUPDATE() {
		spiderDataRecord.addAction(Action.UPDATE);

		spiderDataGroup.addChild(createRecordInfo());

		RestDataRecord restDataRecord = dataRecordSpiderToRestConverter.toRest();
		ActionLink actionLink = restDataRecord.getActionLink("update");
		assertEquals(actionLink.getAction(), Action.UPDATE);
		assertEquals(actionLink.getURL(),
				"http://localhost:8080/therest/rest/record/place/place:0001");
		assertEquals(actionLink.getRequestMethod(), "POST");
	}

	@Test
	public void testToRestWithActionLinkDELETE() {
		spiderDataRecord.addAction(Action.DELETE);

		spiderDataGroup.addChild(createRecordInfo());

		RestDataRecord restDataRecord = dataRecordSpiderToRestConverter.toRest();
		ActionLink actionLink = restDataRecord.getActionLink("delete");
		assertEquals(actionLink.getAction(), Action.DELETE);
		assertEquals(actionLink.getURL(),
				"http://localhost:8080/therest/rest/record/place/place:0001");
		assertEquals(actionLink.getRequestMethod(), "DELETE");
	}

	@Test
	public void testToRestWithResourceLink() {
		spiderDataGroup.addChild(createRecordInfo());
		SpiderDataResourceLink master = SpiderDataResourceLink.withNameInData("master");
		spiderDataGroup.addChild(master);
		master.addAction(Action.READ);

		RestDataRecord restDataRecord = dataRecordSpiderToRestConverter.toRest();
		RestDataGroup restDataGroup = restDataRecord.getRestDataGroup();
		RestDataResourceLink restMaster = (RestDataResourceLink) restDataGroup
				.getFirstChildWithNameInData("master");
		ActionLink actionLink = restMaster.getActionLink("read");
		assertEquals(actionLink.getAction(), Action.READ);
		assertEquals(actionLink.getURL(),
				"http://localhost:8080/therest/rest/record/place/place:0001/master");
		assertEquals(actionLink.getRequestMethod(), "GET");
	}

	@Test
	public void testToRestWithKeys() {
		spiderDataGroup.addChild(createRecordInfo());

		spiderDataRecord.addKey("KEY1");

		RestDataRecord restDataRecord = dataRecordSpiderToRestConverter.toRest();
		String key = restDataRecord.getKeys().iterator().next();
		assertEquals(key, "KEY1");

	}
}
