/*
 * Copyright 2015, 2019 Uppsala University Library
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

package se.uu.ub.cora.therest.data.converter.coradata;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.Iterator;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.data.DataAtomic;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataRecordLink;
import se.uu.ub.cora.therest.data.DataAtomicSpy;
import se.uu.ub.cora.therest.data.DataGroupSpy;
import se.uu.ub.cora.therest.data.RestDataAtomic;
import se.uu.ub.cora.therest.data.RestDataElement;
import se.uu.ub.cora.therest.data.RestDataGroup;
import se.uu.ub.cora.therest.data.RestDataRecordLink;
import se.uu.ub.cora.therest.data.RestDataResourceLink;
import se.uu.ub.cora.therest.data.converter.ConverterInfo;
import se.uu.ub.cora.therest.testdata.DataCreator;

public class DataGroupDataToRestConverterTest {
	private ConverterInfo converterInfo = ConverterInfo.withBaseURLAndRecordURLAndTypeAndId(
			"http://localhost:8080/therest/rest/record/",
			"http://localhost:8080/therest/rest/record/someRecordType/someRecordId",
			"someRecordType", "someRecordId");

	private DataGroup dataGroup;
	private DataGroupDataToRestConverter dataGroupSpiderToRestConverter;
	private DataGroupToRestConverterFactoryImp dataGroupToRestConverterFactoryImp;

	@BeforeMethod
	public void beforeMethod() {
		dataGroupToRestConverterFactoryImp = new DataGroupToRestConverterFactoryImp();
		dataGroup = new DataGroupSpy("nameInData");
		dataGroupSpiderToRestConverter = DataGroupDataToRestConverter
				.fromDataGroupWithDataGroupAndConverterInfo(dataGroupToRestConverterFactoryImp,
						dataGroup, converterInfo);
	}

	@Test
	public void testToRest() {
		RestDataGroup restDataGroup = dataGroupSpiderToRestConverter.toRest();
		assertEquals(restDataGroup.getNameInData(), "nameInData");
	}

	@Test
	public void testToRestWithRepeatId() {
		dataGroup.setRepeatId("2");
		RestDataGroup restDataGroup = dataGroupSpiderToRestConverter.toRest();
		assertEquals(restDataGroup.getNameInData(), "nameInData");
		assertEquals(restDataGroup.getRepeatId(), "2");
	}

	@Test
	public void testToRestWithAttributes() {
		dataGroup.addAttributeByIdWithValue("attributeNameInData", "attributeValue");
		RestDataGroup restDataGroup = dataGroupSpiderToRestConverter.toRest();
		String attributeId = restDataGroup.getAttributes().keySet().iterator().next();
		String attributeValue = restDataGroup.getAttributes().get(attributeId);
		assertEquals(attributeValue, "attributeValue");
	}

	@Test
	public void testToRestWithAtomicChild() {
		dataGroup.addChild(new DataAtomicSpy("childNameInData", "atomicValue"));
		RestDataGroup restDataGroup = dataGroupSpiderToRestConverter.toRest();
		RestDataAtomic restDataAtomic = (RestDataAtomic) restDataGroup.getChildren().iterator()
				.next();
		assertEquals(restDataAtomic.getNameInData(), "childNameInData");
		assertEquals(restDataAtomic.getValue(), "atomicValue");
	}

	@Test
	public void testToRestWithRecordLinkChild() {
		DataRecordLink dataRecordLink = new DataRecordLinkSpy("childNameInData");

		DataAtomic linkedRecordTypeChild = new DataAtomicSpy("linkedRecordType", "someRecordType");
		dataRecordLink.addChild(linkedRecordTypeChild);

		DataAtomic linkedRecordIdChild = new DataAtomicSpy("linkedRecordId", "someRecordId");
		dataRecordLink.addChild(linkedRecordIdChild);

		dataGroup.addChild(dataRecordLink);

		RestDataGroup restDataGroup = dataGroupSpiderToRestConverter.toRest();
		RestDataRecordLink restDataRecordLink = (RestDataRecordLink) restDataGroup.getChildren()
				.iterator().next();
		assertEquals(restDataRecordLink.getNameInData(), "childNameInData");

		RestDataAtomic linkedRecordType = (RestDataAtomic) restDataRecordLink
				.getFirstChildWithNameInData("linkedRecordType");
		assertEquals(linkedRecordType.getValue(), "someRecordType");

		RestDataAtomic linkedRecordId = (RestDataAtomic) restDataRecordLink
				.getFirstChildWithNameInData("linkedRecordId");
		assertEquals(linkedRecordId.getValue(), "someRecordId");
	}

	@Test
	public void testToRestWithResourceLinkChild() {
		dataGroup.addChild(DataCreator.createResourceLinkMaster());
		RestDataGroup restDataGroup = dataGroupSpiderToRestConverter.toRest();
		RestDataElement restMaster = restDataGroup.getFirstChildWithNameInData("master");
		assertTrue((RestDataResourceLink) restMaster instanceof RestDataResourceLink);
	}

	@Test
	public void testToRestWithGroupChild() {
		dataGroup.addChild(new DataGroupSpy("childNameInData"));
		RestDataGroup restDataGroup = dataGroupSpiderToRestConverter.toRest();
		RestDataGroup restDataGroupChild = (RestDataGroup) restDataGroup.getChildren().iterator()
				.next();
		assertEquals(restDataGroupChild.getNameInData(), "childNameInData");
	}

	@Test
	public void testToRestWithGroupLevelsOfChildren() {
		dataGroup.addChild(new DataAtomicSpy("atomicNameInData", "atomicValue"));
		DataGroup dataGroup2 = new DataGroupSpy("childNameInData");
		dataGroup2.addChild(new DataGroupSpy("grandChildNameInData"));
		dataGroup.addChild(dataGroup2);
		RestDataGroup restDataGroup = dataGroupSpiderToRestConverter.toRest();
		Iterator<RestDataElement> iterator = restDataGroup.getChildren().iterator();
		Assert.assertTrue(iterator.hasNext(), "dataGroupRest should have at least one child");

		RestDataAtomic dataAtomicChild = (RestDataAtomic) iterator.next();
		Assert.assertEquals(dataAtomicChild.getNameInData(), "atomicNameInData",
				"NameInData should be the one set in the DataAtomic, first child of dataGroup");

		RestDataGroup dataGroupChild = (RestDataGroup) iterator.next();
		Assert.assertEquals(dataGroupChild.getNameInData(), "childNameInData",
				"NameInData should be the one set in dataGroup2");

		Assert.assertEquals(dataGroupChild.getChildren().stream().findAny().get().getNameInData(),
				"grandChildNameInData",
				"NameInData should be the one set in the child of dataGroup2");
	}

}
