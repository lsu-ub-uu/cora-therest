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

import se.uu.ub.cora.data.Action;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataRecord;
import se.uu.ub.cora.spider.data.DataMissingException;
import se.uu.ub.cora.therest.data.DataAtomicSpy;
import se.uu.ub.cora.therest.data.DataGroupSpy;
import se.uu.ub.cora.therest.data.DataRecordSpy;
import se.uu.ub.cora.therest.data.RestDataRecord;
import se.uu.ub.cora.therest.data.converter.ConverterException;

public class DataRecordToRestConverterTest {
	private String baseURL = "http://localhost:8080/therest/rest/record/";
	private DataGroup spiderDataGroup;
	private DataRecord spiderDataRecord;
	private DataRecordToRestConverter dataRecordSpiderToRestConverter;
	private DataToRestConverterFactorySpy converterFactory;

	@BeforeMethod
	public void setUp() {
		spiderDataGroup = new DataGroupSpy("someNameInData");
		spiderDataRecord = new DataRecordSpy(spiderDataGroup);
		converterFactory = new DataToRestConverterFactorySpy();
		dataRecordSpiderToRestConverter = DataRecordToRestConverter
				.fromDataRecordWithBaseURLAndConverterFactory(spiderDataRecord, baseURL,
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

		DataToRestConverterSpy converter = converterFactory.factoredSpiderToRestConverters.get(0);
		assertTrue(converter.toRestWasCalled);
	}

	@Test(expectedExceptions = ConverterException.class)
	public void testToRestWithActionLinkNoRecordInfoButOtherChild() {
		spiderDataGroup.addChild(new DataAtomicSpy("type", "place"));
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

		DataGroup recordInfo = new DataGroupSpy("recordInfo");
		DataGroup type = new DataGroupSpy("type");
		type.addChild(new DataAtomicSpy("linkedRecordType", "recordType"));
		type.addChild(new DataAtomicSpy("linkedRecordId", "place"));
		recordInfo.addChild(type);

		recordInfo.addChild(new DataAtomicSpy("createdBy", "userId"));
		spiderDataGroup.addChild(recordInfo);

		dataRecordSpiderToRestConverter.toRest();
	}

	@Test(expectedExceptions = ConverterException.class, expectedExceptionsMessageRegExp = ""
			+ "No recordInfo found conversion not possible:"
			+ " se.uu.ub.cora.spider.data.DataMissingException: Group not found for childNameInData:type")
	public void testToRestWithActionLinkNoType() {
		spiderDataRecord.addAction(Action.READ);

		DataGroup recordInfo = new DataGroupSpy("recordInfo");
		recordInfo.addChild(new DataAtomicSpy("id", "place:0001"));
		recordInfo.addChild(new DataAtomicSpy("createdBy", "userId"));
		spiderDataGroup.addChild(recordInfo);

		dataRecordSpiderToRestConverter.toRest();
	}

	@Test
	public void testToRestWithActionLinkNoTypeInitalExceptionIsSentAlong() {
		spiderDataRecord.addAction(Action.READ);

		DataGroup recordInfo = new DataGroupSpy("recordInfo");
		recordInfo.addChild(new DataAtomicSpy("id", "place:0001"));
		recordInfo.addChild(new DataAtomicSpy("createdBy", "userId"));
		spiderDataGroup.addChild(recordInfo);
		try {
			dataRecordSpiderToRestConverter.toRest();

		} catch (Exception e) {
			assertTrue(e.getCause() instanceof DataMissingException);
		}
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

		ActionDataToRestConverterSpy factoredActionsConverter = converterFactory.factoredActionsToRestConverters
				.get(0);
		assertTrue(factoredActionsConverter.toRestWasCalled);

		assertEquals(factoredActionsConverter.actionLinks, restDataRecord.getActionLinks());
	}

	private DataGroup createRecordInfo(String type) {
		DataGroup recordInfo = new DataGroupSpy("recordInfo");
		recordInfo.addChild(new DataAtomicSpy("id", "place:0001"));
		DataGroup typeGroup = new DataGroupSpy("type");
		typeGroup.addChild(new DataAtomicSpy("linkedRecordType", "recordType"));
		typeGroup.addChild(new DataAtomicSpy("linkedRecordId", type));
		recordInfo.addChild(typeGroup);
		recordInfo.addChild(new DataAtomicSpy("createdBy", "userId"));
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

		ActionDataToRestConverterSpy factoredActionsConverter = converterFactory.factoredActionsToRestConverters
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
