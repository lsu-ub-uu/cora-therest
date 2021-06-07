/*
 * Copyright 2015, 2016, 2019, 2020 Uppsala University Library
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

package se.uu.ub.cora.therest.converter.coratorest;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.data.Action;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataRecord;
import se.uu.ub.cora.spider.data.DataMissingException;
import se.uu.ub.cora.therest.converter.ConverterException;
import se.uu.ub.cora.therest.coradata.DataAtomicSpy;
import se.uu.ub.cora.therest.coradata.DataGroupSpy;
import se.uu.ub.cora.therest.coradata.DataRecordSpy;
import se.uu.ub.cora.therest.data.RestDataRecord;

public class CoraDataRecordToRestConverterTest {
	private String baseURL = "http://localhost:8080/therest/rest/record/";
	private DataGroup dataGroup;
	private DataRecord dataRecord;
	private CoraDataRecordToRestConverter dataRecordSpiderToRestConverter;
	private CoraDataGroupToRestConverterFactorySpy converterFactory;

	@BeforeMethod
	public void setUp() {
		dataGroup = new DataGroupSpy("someNameInData");
		dataRecord = new DataRecordSpy(dataGroup);
		converterFactory = new CoraDataGroupToRestConverterFactorySpy();
		dataRecordSpiderToRestConverter = CoraDataRecordToRestConverter
				.fromDataRecordWithBaseURLAndConverterFactory(dataRecord, baseURL,
						converterFactory);
	}

	@Test
	public void testInit() {
		assertTrue(dataRecordSpiderToRestConverter instanceof CoraToRestConverter);
	}

	@Test
	public void testToRest() {
		dataGroup.addChild(createRecordInfo("place"));
		dataRecordSpiderToRestConverter.toRest();

		assertSame(converterFactory.dataGroups.get(0), dataGroup);
		assertSame(converterFactory.converterInfos.get(0).baseURL, baseURL);
		assertEquals(converterFactory.converterInfos.get(0).recordURL,
				baseURL + "place/place:0001");

		CoraToRestConverterSpy converter = converterFactory.factoredSpiderToRestConverters.get(0);
		assertTrue(converter.toRestWasCalled);
	}

	@Test(expectedExceptions = ConverterException.class)
	public void testToRestWithActionLinkNoRecordInfoButOtherChild() {
		dataGroup.addChild(new DataAtomicSpy("type", "place"));
		dataRecord.addAction(Action.READ);
		dataRecordSpiderToRestConverter.toRest();
	}

	@Test(expectedExceptions = ConverterException.class)
	public void testToRestWithActionLinkNoRecordInfo() {
		dataRecord.addAction(Action.READ);
		dataRecordSpiderToRestConverter.toRest();
	}

	@Test(expectedExceptions = ConverterException.class)
	public void testToRestWithActionLinkNoId() {
		dataRecord.addAction(Action.READ);

		DataGroup recordInfo = new DataGroupSpy("recordInfo");
		DataGroup type = new DataGroupSpy("type");
		type.addChild(new DataAtomicSpy("linkedRecordType", "recordType"));
		type.addChild(new DataAtomicSpy("linkedRecordId", "place"));
		recordInfo.addChild(type);

		recordInfo.addChild(new DataAtomicSpy("createdBy", "userId"));
		dataGroup.addChild(recordInfo);

		dataRecordSpiderToRestConverter.toRest();
	}

	@Test(expectedExceptions = ConverterException.class, expectedExceptionsMessageRegExp = ""
			+ "No recordInfo found conversion not possible:"
			+ " se.uu.ub.cora.spider.data.DataMissingException: Group not found for childNameInData:type")
	public void testToRestWithActionLinkNoType() {
		dataRecord.addAction(Action.READ);

		DataGroup recordInfo = new DataGroupSpy("recordInfo");
		recordInfo.addChild(new DataAtomicSpy("id", "place:0001"));
		recordInfo.addChild(new DataAtomicSpy("createdBy", "userId"));
		dataGroup.addChild(recordInfo);

		dataRecordSpiderToRestConverter.toRest();
	}

	@Test
	public void testToRestWithActionLinkNoTypeInitalExceptionIsSentAlong() {
		dataRecord.addAction(Action.READ);

		DataGroup recordInfo = new DataGroupSpy("recordInfo");
		recordInfo.addChild(new DataAtomicSpy("id", "place:0001"));
		recordInfo.addChild(new DataAtomicSpy("createdBy", "userId"));
		dataGroup.addChild(recordInfo);
		try {
			dataRecordSpiderToRestConverter.toRest();

		} catch (Exception e) {
			assertTrue(e.getCause() instanceof DataMissingException);
		}
	}

	@Test
	public void testToRestActionLinksSentToConverter() {
		dataRecord.addAction(Action.READ);
		dataRecord.addAction(Action.CREATE);
		dataRecord.addAction(Action.DELETE);

		dataGroup.addChild(createRecordInfo("place"));

		RestDataRecord restDataRecord = dataRecordSpiderToRestConverter.toRest();

		assertSame(converterFactory.dataGroups.get(0), dataGroup);
		assertSame(converterFactory.dataGroups.get(1), dataGroup);
		assertSame(converterFactory.converterInfos.get(1).baseURL, baseURL);
		assertEquals(converterFactory.converterInfos.get(1).recordURL,
				baseURL + "place/place:0001");

		assertTrue(converterFactory.addedActions.contains(Action.READ));
		assertTrue(converterFactory.addedActions.contains(Action.CREATE));
		assertTrue(converterFactory.addedActions.contains(Action.DELETE));
		assertEquals(converterFactory.addedActions.size(), 3);

		CoraActionToRestConverterSpy factoredActionsConverter = converterFactory.factoredActionsToRestConverters
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
		dataRecord.addAction(Action.READ);

		dataGroup.addChild(createRecordInfo("recordType"));

		RestDataRecord restDataRecord = dataRecordSpiderToRestConverter.toRest();

		assertSame(converterFactory.dataGroups.get(0), dataGroup);
		assertSame(converterFactory.dataGroups.get(1), dataGroup);
		assertSame(converterFactory.converterInfos.get(1).baseURL, baseURL);
		assertEquals(converterFactory.converterInfos.get(1).recordURL,
				baseURL + "recordType/place:0001");

		assertTrue(converterFactory.addedActions.contains(Action.READ));
		assertEquals(converterFactory.addedActions.size(), 1);

		CoraActionToRestConverterSpy factoredActionsConverter = converterFactory.factoredActionsToRestConverters
				.get(0);
		assertTrue(factoredActionsConverter.toRestWasCalled);

		assertEquals(factoredActionsConverter.actionLinks, restDataRecord.getActionLinks());
	}

	@Test
	public void testToRestWithReadPermissions() {
		dataGroup.addChild(createRecordInfo("place"));

		dataRecord.addReadPermission("readPermissionOne");

		RestDataRecord restDataRecord = dataRecordSpiderToRestConverter.toRest();
		String readPermission = restDataRecord.getReadPermissions().iterator().next();
		assertEquals(readPermission, "readPermissionOne");

	}

	@Test
	public void testToRestWithWritePermissions() {
		dataGroup.addChild(createRecordInfo("place"));

		dataRecord.addWritePermission("writePermissionOne");

		RestDataRecord restDataRecord = dataRecordSpiderToRestConverter.toRest();
		String writePermission = restDataRecord.getWritePermissions().iterator().next();
		assertEquals(writePermission, "writePermissionOne");

	}
}
