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

import static org.testng.Assert.assertEquals;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.spider.data.SpiderDataAtomic;
import se.uu.ub.cora.spider.data.SpiderDataGroup;
import se.uu.ub.cora.therest.data.RestDataAtomic;
import se.uu.ub.cora.therest.data.RestDataAttribute;
import se.uu.ub.cora.therest.data.RestDataGroup;
import se.uu.ub.cora.therest.data.RestDataRecordLink;
import se.uu.ub.cora.therest.data.converter.ConverterException;

public class DataGroupRestToSpiderConverterTest {
	private RestDataGroup restDataGroup;
	private DataGroupRestToSpiderConverter dataGroupRestToSpiderConverter;

	@BeforeMethod
	public void setUp() {
		restDataGroup = RestDataGroup.withNameInData("nameInData");
		dataGroupRestToSpiderConverter = DataGroupRestToSpiderConverter
				.fromRestDataGroup(restDataGroup);
	}

	@Test
	public void testToSpider() {
		SpiderDataGroup spiderDataGroup = dataGroupRestToSpiderConverter.toSpider();
		assertEquals(spiderDataGroup.getNameInData(), "nameInData");
	}

	@Test
	public void testToSpiderWithRepeatId() {
		restDataGroup.setRepeatId("34");
		SpiderDataGroup spiderDataGroup = dataGroupRestToSpiderConverter.toSpider();
		assertEquals(spiderDataGroup.getNameInData(), "nameInData");
		assertEquals(spiderDataGroup.getRepeatId(), "34");
	}

	@Test
	public void testToSpiderWithAttribute() {
		restDataGroup.addAttributeByIdWithValue("attributeId", "attributeValue");

		SpiderDataGroup spiderDataGroup = dataGroupRestToSpiderConverter.toSpider();

		String attributeValue = spiderDataGroup.getAttributes().get("attributeId");
		assertEquals(attributeValue, "attributeValue");
	}

	@Test
	public void testToSpiderWithAtomicChild() {
		restDataGroup.addChild(RestDataAtomic.withNameInDataAndValue("atomicId", "atomicValue"));

		SpiderDataGroup spiderDataGroup = dataGroupRestToSpiderConverter.toSpider();

		SpiderDataAtomic atomicChild = (SpiderDataAtomic) spiderDataGroup.getChildren().get(0);
		assertEquals(atomicChild.getNameInData(), "atomicId");
		assertEquals(atomicChild.getValue(), "atomicValue");
	}

	@Test
	public void testToSpiderWithRecordLinkChild() {
		RestDataRecordLink restDataRecordLink = RestDataRecordLink.withNameInData("aLink");
		RestDataAtomic linkedRecordTypeChild = RestDataAtomic.withNameInDataAndValue("linkedRecordType", "someRecordType");
		restDataRecordLink.addChild(linkedRecordTypeChild);

		RestDataAtomic linkedRecordIdChild = RestDataAtomic.withNameInDataAndValue("linkedRecordId", "someRecordId");
		restDataRecordLink.addChild(linkedRecordIdChild);
		restDataGroup.addChild(restDataRecordLink);

		SpiderDataGroup spiderDataGroup = dataGroupRestToSpiderConverter.toSpider();

		SpiderDataGroup spiderDataRecordLink = (SpiderDataGroup)spiderDataGroup.getFirstChildWithNameInData("aLink");
		assertEquals(spiderDataRecordLink.getNameInData(), "aLink");

		SpiderDataAtomic linkedRecordType = (SpiderDataAtomic)spiderDataRecordLink.getFirstChildWithNameInData("linkedRecordType");
		SpiderDataAtomic linkedRecordId = (SpiderDataAtomic)spiderDataRecordLink.getFirstChildWithNameInData("linkedRecordId");

		assertEquals(linkedRecordType.getValue(), "someRecordType");
		assertEquals(linkedRecordId.getValue(), "someRecordId");
	}

	@Test
	public void testToSpiderWithGroupChild() {
		restDataGroup.addChild(RestDataGroup.withNameInData("groupId"));

		SpiderDataGroup spiderDataGroup = dataGroupRestToSpiderConverter.toSpider();

		SpiderDataGroup groupChild = (SpiderDataGroup) spiderDataGroup.getChildren().get(0);
		assertEquals(groupChild.getNameInData(), "groupId");
	}

	@Test
	public void testToSpiderWithGroupChildWithAtomicChild() {
		RestDataGroup restGroupChild = RestDataGroup.withNameInData("groupId");
		restGroupChild.addChild(RestDataAtomic.withNameInDataAndValue("grandChildAtomicId",
				"grandChildAtomicValue"));
		restGroupChild.addAttributeByIdWithValue("groupChildAttributeId",
				"groupChildAttributeValue");
		restDataGroup.addChild(restGroupChild);

		SpiderDataGroup spiderDataGroup = dataGroupRestToSpiderConverter.toSpider();

		SpiderDataGroup groupChild = (SpiderDataGroup) spiderDataGroup.getChildren().get(0);
		SpiderDataAtomic grandChildAtomic = (SpiderDataAtomic) groupChild.getChildren().get(0);
		assertEquals(grandChildAtomic.getNameInData(), "grandChildAtomicId");
		String groupChildAttributeValue = restGroupChild.getAttributes()
				.get("groupChildAttributeId");
		assertEquals(groupChildAttributeValue, "groupChildAttributeValue");
	}

	@Test(expectedExceptions = ConverterException.class)
	public void testToSpiderWithAttributeAsChild() {
		restDataGroup.addChild(RestDataAttribute.withNameInDataAndValue("atomicId", "atomicValue"));

		SpiderDataGroup spiderDataGroup = dataGroupRestToSpiderConverter.toSpider();

		SpiderDataAtomic atomicChild = (SpiderDataAtomic) spiderDataGroup.getChildren().get(0);
		assertEquals(atomicChild.getNameInData(), "atomicId");
		assertEquals(atomicChild.getValue(), "atomicValue");
	}
}
