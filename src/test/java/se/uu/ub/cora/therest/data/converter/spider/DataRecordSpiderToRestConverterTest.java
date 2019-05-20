/*
 * Copyright 2015, 2016, 2019 Uppsala University Library
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
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.spider.data.Action;
import se.uu.ub.cora.spider.data.SpiderDataAtomic;
import se.uu.ub.cora.spider.data.SpiderDataGroup;
import se.uu.ub.cora.spider.data.SpiderDataRecord;
import se.uu.ub.cora.therest.data.RestDataRecord;
import se.uu.ub.cora.therest.data.converter.ConverterException;

public class DataRecordSpiderToRestConverterTest {
	private String baseURL = "http://localhost:8080/therest/rest/record/";
	private SpiderDataGroup spiderDataGroup;
	private SpiderDataRecord spiderDataRecord;
	private DataRecordSpiderToRestConverter dataRecordSpiderToRestConverter;
	private SpiderToRestConverterFactorySpy converterFactory;

	@BeforeMethod
	public void setUp() {
		spiderDataGroup = SpiderDataGroup.withNameInData("someNameInData");
		spiderDataRecord = SpiderDataRecord.withSpiderDataGroup(spiderDataGroup);
		converterFactory = new SpiderToRestConverterFactorySpy();
		dataRecordSpiderToRestConverter = DataRecordSpiderToRestConverter
				.fromSpiderDataRecordWithBaseURLAndConverterFactory(spiderDataRecord, baseURL,
						converterFactory);
	}

	@Test
	public void testToRest() {
		spiderDataGroup.addChild(createRecordInfo("place"));
		dataRecordSpiderToRestConverter.toRest();

		assertSame(converterFactory.dataGroups.get(0), spiderDataGroup);
		assertSame(converterFactory.converterInfos.get(0).baseURL, baseURL);
		assertEquals(converterFactory.converterInfos.get(0).recordURL,
				baseURL + "place/place:0001");

		SpiderToRestConverterSpy converter = converterFactory.factoredSpiderToRestConverters.get(0);
		assertTrue(converter.toRestWasCalled);
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
		SpiderDataGroup type = SpiderDataGroup.withNameInData("type");
		type.addChild(SpiderDataAtomic.withNameInDataAndValue("linkedRecordType", "recordType"));
		type.addChild(SpiderDataAtomic.withNameInDataAndValue("linkedRecordId", "place"));
		recordInfo.addChild(type);

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
	public void testToRestActionLinksSentToConverter() {
		spiderDataRecord.addAction(Action.READ);
		spiderDataRecord.addAction(Action.CREATE);
		spiderDataRecord.addAction(Action.DELETE);

		spiderDataGroup.addChild(createRecordInfo("place"));

		RestDataRecord restDataRecord = dataRecordSpiderToRestConverter.toRest();

		assertSame(converterFactory.dataGroups.get(0), spiderDataGroup);
		assertSame(converterFactory.dataGroups.get(1), spiderDataGroup);
		assertSame(converterFactory.converterInfos.get(1).baseURL, baseURL);
		assertEquals(converterFactory.converterInfos.get(1).recordURL,
				baseURL + "place/place:0001");

		assertTrue(converterFactory.addedActions.contains(Action.READ));
		assertTrue(converterFactory.addedActions.contains(Action.CREATE));
		assertTrue(converterFactory.addedActions.contains(Action.DELETE));
		assertEquals(converterFactory.addedActions.size(), 3);

		ActionSpiderToRestConverterSpy factoredActionsConverter = converterFactory.factoredActionsToRestConverters
				.get(0);
		assertTrue(factoredActionsConverter.toRestWasCalled);

		assertEquals(factoredActionsConverter.actionLinks, restDataRecord.getActionLinks());
	}

	private SpiderDataGroup createRecordInfo(String type) {
		SpiderDataGroup recordInfo = SpiderDataGroup.withNameInData("recordInfo");
		recordInfo.addChild(SpiderDataAtomic.withNameInDataAndValue("id", "place:0001"));
		SpiderDataGroup typeGroup = SpiderDataGroup.withNameInData("type");
		typeGroup.addChild(
				SpiderDataAtomic.withNameInDataAndValue("linkedRecordType", "recordType"));
		typeGroup.addChild(SpiderDataAtomic.withNameInDataAndValue("linkedRecordId", type));
		recordInfo.addChild(typeGroup);
		recordInfo.addChild(SpiderDataAtomic.withNameInDataAndValue("createdBy", "userId"));
		return recordInfo;
	}

	@Test
	public void testToRestWithActionLinkSEARCHWhenRecordTypeIsRecordType() {
		spiderDataRecord.addAction(Action.READ);

		spiderDataGroup.addChild(createRecordInfo("recordType"));

		RestDataRecord restDataRecord = dataRecordSpiderToRestConverter.toRest();

		assertSame(converterFactory.dataGroups.get(0), spiderDataGroup);
		assertSame(converterFactory.dataGroups.get(1), spiderDataGroup);
		assertSame(converterFactory.converterInfos.get(1).baseURL, baseURL);
		assertEquals(converterFactory.converterInfos.get(1).recordURL,
				baseURL + "recordType/place:0001");

		assertTrue(converterFactory.addedActions.contains(Action.READ));
		assertEquals(converterFactory.addedActions.size(), 1);

		ActionSpiderToRestConverterSpy factoredActionsConverter = converterFactory.factoredActionsToRestConverters
				.get(0);
		assertTrue(factoredActionsConverter.toRestWasCalled);

		assertEquals(factoredActionsConverter.actionLinks, restDataRecord.getActionLinks());
	}

	// TODO: Ändra detta efter användning av factory för actions
	// @Test
	// public void testToRestWithResourceLink() {
	// spiderDataGroup.addChild(createRecordInfo("place"));
	// spiderDataGroup.addChild(DataCreator.createResourceLinkMaster());
	//
	// RestDataRecord restDataRecord = dataRecordSpiderToRestConverter.toRest();
	//
	// assertSame(converterFactory.dataGroups.get(0), spiderDataGroup);
	// assertSame(converterFactory.dataGroups.get(1), spiderDataGroup);
	// assertSame(converterFactory.converterInfos.get(1).baseURL, baseURL);
	// assertEquals(converterFactory.converterInfos.get(1).recordURL,
	// baseURL + "recordType/place:0001");
	//
	// assertTrue(converterFactory.addedActions.contains(Action.READ));
	// assertEquals(converterFactory.addedActions.size(), 1);

	// RestDataGroup restDataGroup = restDataRecord.getRestDataGroup();
	// RestDataResourceLink restMaster = (RestDataResourceLink) restDataGroup
	// .getFirstChildWithNameInData("master");
	// ActionLink actionLink = restMaster.getActionLink("read");
	// assertEquals(actionLink.getAction(), Action.READ);
	// assertEquals(actionLink.getURL(),
	// "http://localhost:8080/therest/rest/record/place/place:0001/master");
	// assertEquals(actionLink.getRequestMethod(), "GET");
	// }

	@Test
	public void testToRestWithKeys() {
		spiderDataGroup.addChild(createRecordInfo("place"));

		spiderDataRecord.addKey("KEY1");

		RestDataRecord restDataRecord = dataRecordSpiderToRestConverter.toRest();
		String key = restDataRecord.getKeys().iterator().next();
		assertEquals(key, "KEY1");

	}
}
