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

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import se.uu.ub.cora.spider.data.SpiderDataAtomic;
import se.uu.ub.cora.spider.data.SpiderDataGroup;
import se.uu.ub.cora.spider.data.SpiderDataRecordLink;
import se.uu.ub.cora.therest.data.RestDataAtomic;
import se.uu.ub.cora.therest.data.RestDataGroup;
import se.uu.ub.cora.therest.data.RestDataRecordLink;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;

public class DataRecordLinkRestToSpiderConverterTest {
	private RestDataRecordLink restDataRecordLink;
	private DataRecordLinkRestToSpiderConverter converter;

	@BeforeMethod
	public void setUp() {
		restDataRecordLink = RestDataRecordLink.withNameInData("nameInData");
		RestDataAtomic linkedRecordType = RestDataAtomic.withNameInDataAndValue("linkedRecordType", "linkedRecordType");
		restDataRecordLink.addChild(linkedRecordType);

		RestDataAtomic linkedRecordId = RestDataAtomic.withNameInDataAndValue("linkedRecordId", "linkedRecordId");
		restDataRecordLink.addChild(linkedRecordId);
		converter = DataRecordLinkRestToSpiderConverter.fromRestDataRecordLink(restDataRecordLink);

	}

	@Test
	public void testToSpider() {
		SpiderDataRecordLink spiderDataRecordLink = converter.toSpider();
		assertEquals(spiderDataRecordLink.getNameInData(), "nameInData");

		SpiderDataAtomic linkedRecordType = (SpiderDataAtomic) spiderDataRecordLink.getFirstChildWithNameInData("linkedRecordType");
		SpiderDataAtomic linkedRecordId = (SpiderDataAtomic) spiderDataRecordLink.getFirstChildWithNameInData("linkedRecordId");

		assertEquals(linkedRecordType.getValue(), "linkedRecordType");
		assertEquals(linkedRecordId.getValue(), "linkedRecordId");
		assertFalse(spiderDataRecordLink.containsChildWithNameInData("linkedPath"));
	}

	@Test
	public void testToSpiderWithRepeatId() {
		restDataRecordLink.setRepeatId("45");
		SpiderDataRecordLink spiderDataRecordLink = converter.toSpider();

		SpiderDataAtomic linkedRecordType = (SpiderDataAtomic) spiderDataRecordLink.getFirstChildWithNameInData("linkedRecordType");
		SpiderDataAtomic linkedRecordId = (SpiderDataAtomic) spiderDataRecordLink.getFirstChildWithNameInData("linkedRecordId");

		assertEquals(spiderDataRecordLink.getNameInData(), "nameInData");
		assertEquals(linkedRecordType.getValue(), "linkedRecordType");
		assertEquals(linkedRecordId.getValue(), "linkedRecordId");
		assertEquals(spiderDataRecordLink.getRepeatId(), "45");
	}

	@Test
	public void testToSpiderWithLinkedRepeatId(){
		RestDataAtomic linkedRepeatIdChild = RestDataAtomic.withNameInDataAndValue("linkedRepeatId", "linkedOne");
		restDataRecordLink.addChild(linkedRepeatIdChild);
		SpiderDataRecordLink spiderDataRecordLink = converter.toSpider();

		SpiderDataAtomic linkedRepeatId = (SpiderDataAtomic) spiderDataRecordLink.getFirstChildWithNameInData("linkedRepeatId");
		assertEquals(linkedRepeatId.getValue(), "linkedOne");
	}

	@Test
	public void testToSpiderWithLinkedPath(){
		restDataRecordLink.addChild(RestDataGroup.withNameInData("linkedPath"));
		SpiderDataRecordLink spiderDataRecordLink = converter.toSpider();
		SpiderDataGroup linkedPath = (SpiderDataGroup) spiderDataRecordLink.getFirstChildWithNameInData("linkedPath");
		assertEquals(linkedPath.getNameInData(), "linkedPath");
	}
}
