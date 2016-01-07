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

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import se.uu.ub.cora.spider.data.SpiderDataAtomic;
import se.uu.ub.cora.spider.data.SpiderDataGroup;
import se.uu.ub.cora.spider.data.SpiderDataRecordLink;
import se.uu.ub.cora.therest.data.RestDataAtomic;
import se.uu.ub.cora.therest.data.RestDataElement;
import se.uu.ub.cora.therest.data.RestDataGroup;
import se.uu.ub.cora.therest.data.RestDataRecordLink;

import java.util.Iterator;

import static org.testng.Assert.assertEquals;

public class DataGroupSpiderToRestConverterTest {
	private String baseURL = "http://localhost:8080/therest/rest/record/";
	private SpiderDataGroup spiderDataGroup;
	private DataGroupSpiderToRestConverter dataGroupSpiderToRestConverter;

	@BeforeMethod
	public void beforeMethod() {
		spiderDataGroup = SpiderDataGroup.withNameInData("nameInData");
		dataGroupSpiderToRestConverter = DataGroupSpiderToRestConverter
				.fromSpiderDataGroupWithBaseURL(spiderDataGroup, baseURL);
	}

	@Test
	public void testToRest() {
		RestDataGroup restDataGroup = dataGroupSpiderToRestConverter.toRest();
		assertEquals(restDataGroup.getNameInData(), "nameInData");
	}

	@Test
	public void testToRestWithRepeatId() {
		spiderDataGroup.setRepeatId("2");
		RestDataGroup restDataGroup = dataGroupSpiderToRestConverter.toRest();
		assertEquals(restDataGroup.getNameInData(), "nameInData");
		assertEquals(restDataGroup.getRepeatId(), "2");
	}

	@Test
	public void testToRestWithAttributes() {
		spiderDataGroup.addAttributeByIdWithValue("attributeNameInData", "attributeValue");
		RestDataGroup restDataGroup = dataGroupSpiderToRestConverter.toRest();
		String attributeId = restDataGroup.getAttributes().keySet().iterator().next();
		String attributeValue = restDataGroup.getAttributes().get(attributeId);
		assertEquals(attributeValue, "attributeValue");
	}

	@Test
	public void testToRestWithAtomicChild() {
		spiderDataGroup.addChild(
				SpiderDataAtomic.withNameInDataAndValue("childNameInData", "atomicValue"));
		RestDataGroup restDataGroup = dataGroupSpiderToRestConverter.toRest();
		RestDataAtomic restDataAtomic = (RestDataAtomic) restDataGroup.getChildren().iterator()
				.next();
		assertEquals(restDataAtomic.getNameInData(), "childNameInData");
		assertEquals(restDataAtomic.getValue(), "atomicValue");
	}

	@Test
	public void testToRestWithRecordLinkChild() {
		SpiderDataRecordLink dataRecordLink = SpiderDataRecordLink.withNameInData("childNameInData");

		SpiderDataAtomic linkedRecordTypeChild = SpiderDataAtomic.withNameInDataAndValue("linkedRecordType", "someRecordType");
		dataRecordLink.addChild(linkedRecordTypeChild);

		SpiderDataAtomic linkedRecordIdChild = SpiderDataAtomic.withNameInDataAndValue("linkedRecordId", "someRecordId");
		dataRecordLink.addChild(linkedRecordIdChild);

		spiderDataGroup.addChild(dataRecordLink);

		RestDataGroup restDataGroup = dataGroupSpiderToRestConverter.toRest();
		RestDataRecordLink restDataRecordLink = (RestDataRecordLink) restDataGroup.getChildren()
				.iterator().next();
		assertEquals(restDataRecordLink.getNameInData(), "childNameInData");

		RestDataAtomic linkedRecordType = (RestDataAtomic) restDataRecordLink.getFirstChildWithNameInData("linkedRecordType");
		assertEquals(linkedRecordType.getValue(), "someRecordType");

		RestDataAtomic linkedRecordId = (RestDataAtomic) restDataRecordLink.getFirstChildWithNameInData("linkedRecordId");
		assertEquals(linkedRecordId.getValue(), "someRecordId");
	}

	@Test
	public void testToRestWithGroupChild() {
		spiderDataGroup.addChild(SpiderDataGroup.withNameInData("childNameInData"));
		RestDataGroup restDataGroup = dataGroupSpiderToRestConverter.toRest();
		RestDataGroup restDataGroupChild = (RestDataGroup) restDataGroup.getChildren().iterator()
				.next();
		assertEquals(restDataGroupChild.getNameInData(), "childNameInData");
	}

	@Test
	public void testToRestWithGroupLevelsOfChildren() {
		spiderDataGroup.addChild(
				SpiderDataAtomic.withNameInDataAndValue("atomicNameInData", "atomicValue"));
		SpiderDataGroup dataGroup2 = SpiderDataGroup.withNameInData("childNameInData");
		dataGroup2.addChild(SpiderDataGroup.withNameInData("grandChildNameInData"));
		spiderDataGroup.addChild(dataGroup2);
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
