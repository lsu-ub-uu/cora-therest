/*
 * Copyright 2015, 2021 Uppsala University Library
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
import static org.testng.Assert.assertTrue;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.json.builder.JsonBuilderFactory;
import se.uu.ub.cora.json.builder.org.OrgJsonBuilderFactoryAdapter;
import se.uu.ub.cora.therest.data.RestDataAtomic;
import se.uu.ub.cora.therest.data.RestDataAttribute;
import se.uu.ub.cora.therest.data.RestDataElement;
import se.uu.ub.cora.therest.data.RestDataGroup;
import se.uu.ub.cora.therest.data.RestDataList;
import se.uu.ub.cora.therest.data.RestDataRecord;
import se.uu.ub.cora.therest.data.RestDataRecordLink;
import se.uu.ub.cora.therest.data.RestDataResourceLink;

public class DataToJsonConverterFactoryTest {
	private RestDataToJsonConverterFactory dataToJsonConverterFactory;
	private JsonBuilderFactory factory;

	@BeforeMethod
	public void beforeMethod() {
		dataToJsonConverterFactory = new RestDataToJsonConverterFactoryImp();
		factory = new OrgJsonBuilderFactoryAdapter();
	}

	@Test
	public void testJsonCreatorFactoryDataGroup() {
		RestDataElement restDataElement = RestDataGroup.withNameInData("groupNameInData");

		RestDataToJsonConverter dataToJsonConverter = dataToJsonConverterFactory
				.createForRestDataElement(factory, restDataElement);

		assertTrue(dataToJsonConverter instanceof DataGroupToJsonConverter);
	}

	@Test
	public void testJsonCreatorFactoryDataAtomic() {
		RestDataElement restDataElement = RestDataAtomic.withNameInDataAndValue("atomicNameInData",
				"atomicValue");

		RestDataToJsonConverter dataToJsonConverter = dataToJsonConverterFactory
				.createForRestDataElement(factory, restDataElement);

		assertTrue(dataToJsonConverter instanceof DataAtomicToJsonConverter);
	}

	@Test
	public void testJsonCreatorFactoryDataAttribute() {
		RestDataElement restDataElement = RestDataAttribute
				.withNameInDataAndValue("attributeNameInData", "attributeValue");

		RestDataToJsonConverter dataToJsonConverter = dataToJsonConverterFactory
				.createForRestDataElement(factory, restDataElement);

		assertTrue(dataToJsonConverter instanceof DataAttributeToJsonConverter);
	}

	@Test
	public void testJsonCreateFactoryDataRecordLink() {
		RestDataRecordLink recordLink = RestDataRecordLink.withNameInData("recordLinkNameInData");
		RestDataAtomic linkedRecordType = RestDataAtomic.withNameInDataAndValue("linkedRecordType",
				"someRecordType");
		recordLink.addChild(linkedRecordType);

		RestDataAtomic linkedRecordId = RestDataAtomic.withNameInDataAndValue("linkedRecordId",
				"someRecordId");
		recordLink.addChild(linkedRecordId);
		RestDataToJsonConverter dataToJsonConverter = dataToJsonConverterFactory
				.createForRestDataElement(factory, recordLink);

		assertTrue(dataToJsonConverter instanceof DataRecordLinkToJsonConverter);

	}

	@Test
	public void testJsonCreateFactoryDataResourceLink() {
		RestDataResourceLink resourceLink = RestDataResourceLink
				.withNameInData("recordLinkNameInData");

		resourceLink.addChild(RestDataAtomic.withNameInDataAndValue("streamId", "someStreamId"));
		resourceLink.addChild(RestDataAtomic.withNameInDataAndValue("filename", "adele1.png"));
		resourceLink.addChild(RestDataAtomic.withNameInDataAndValue("filesize", "1234567"));
		resourceLink.addChild(RestDataAtomic.withNameInDataAndValue("mimeType", "application/png"));
		RestDataToJsonConverter dataToJsonConverter = dataToJsonConverterFactory
				.createForRestDataElement(factory, resourceLink);

		assertTrue(dataToJsonConverter instanceof DataResourceLinkToJsonConverter);

	}

	@Test
	public void testFactorDataListToJsonConverter() {
		RestDataList restDataList = RestDataList.withContainDataOfType("someRecordType");
		DataListToJsonConverter converter = (DataListToJsonConverter) dataToJsonConverterFactory
				.createForRestData(restDataList);
		assertEquals(converter.getRestDataList(), restDataList);
		assertTrue(converter.getJsonBuilderFactory() instanceof OrgJsonBuilderFactoryAdapter);
	}

	@Test
	public void testFactorDataRecordToJsonConverter() {
		RestDataRecord restDataRecord = RestDataRecord.withRestDataGroup(null);
		RestRecordToJsonConverter converter = (RestRecordToJsonConverter) dataToJsonConverterFactory
				.createForRestData(restDataRecord);
		assertEquals(converter.getRestDataRecord(), restDataRecord);
		assertTrue(converter.getJsonBuilderFactory() instanceof OrgJsonBuilderFactoryAdapter);
	}
}
