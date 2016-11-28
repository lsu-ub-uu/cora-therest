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
import se.uu.ub.cora.spider.data.Action;
import se.uu.ub.cora.spider.data.SpiderDataAtomic;
import se.uu.ub.cora.spider.data.SpiderDataGroup;
import se.uu.ub.cora.spider.data.SpiderDataRecordLink;
import se.uu.ub.cora.therest.data.ActionLink;
import se.uu.ub.cora.therest.data.RestDataAtomic;
import se.uu.ub.cora.therest.data.RestDataGroup;
import se.uu.ub.cora.therest.data.RestDataRecordLink;
import se.uu.ub.cora.therest.data.converter.ConverterInfo;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;

public class DataRecordLinkSpiderToRestConverterTest {
	private ConverterInfo converterInfo = ConverterInfo.withBaseURLAndRecordURL(
			"http://localhost:8080/therest/rest/record/",
			"http://localhost:8080/therest/rest/record/someRecordType/someRecordId");

	private SpiderDataRecordLink spiderDataRecordLink;
	private DataRecordLinkSpiderToRestConverter dataRecordLinkSpiderToRestConverter;

	@BeforeMethod
	public void setUp() {
		spiderDataRecordLink = SpiderDataRecordLink.withNameInData("nameInData");

		SpiderDataAtomic linkedRecordType = SpiderDataAtomic
				.withNameInDataAndValue("linkedRecordType", "linkedRecordType");
		spiderDataRecordLink.addChild(linkedRecordType);

		SpiderDataAtomic linkedRecordId = SpiderDataAtomic.withNameInDataAndValue("linkedRecordId",
				"linkedRecordId");
		spiderDataRecordLink.addChild(linkedRecordId);

		dataRecordLinkSpiderToRestConverter = DataRecordLinkSpiderToRestConverter
				.fromSpiderDataRecordLinkWithBaseURL(spiderDataRecordLink, converterInfo);

	}

	@Test
	public void testToRest() {
		RestDataRecordLink restDataRecordLink = dataRecordLinkSpiderToRestConverter.toRest();
		assertEquals(restDataRecordLink.getNameInData(), "nameInData");

		RestDataAtomic linkedRecordType = (RestDataAtomic) restDataRecordLink
				.getFirstChildWithNameInData("linkedRecordType");
		RestDataAtomic linkedRecordId = (RestDataAtomic) restDataRecordLink
				.getFirstChildWithNameInData("linkedRecordId");

		assertEquals(linkedRecordType.getValue(), "linkedRecordType");
		assertEquals(linkedRecordId.getValue(), "linkedRecordId");
		assertFalse(restDataRecordLink.containsChildWithNameInData("linkedPath"));
	}
	
	@Test
	public void testToRestWithAttributes() {
		spiderDataRecordLink.addAttributeByIdWithValue("attributeNameInData", "attributeValue");

		RestDataRecordLink restDataRecordLink = dataRecordLinkSpiderToRestConverter.toRest();
			
		String attributeId = restDataRecordLink.getAttributes().keySet().iterator().next();
		String attributeValue = restDataRecordLink.getAttributes().get(attributeId);
		assertEquals(attributeValue, "attributeValue");
	}

	@Test
	public void testToRestWithRepeatId() {
		spiderDataRecordLink.setRepeatId("j");
		RestDataRecordLink restDataRecordLink = dataRecordLinkSpiderToRestConverter.toRest();
		assertEquals(restDataRecordLink.getNameInData(), "nameInData");

		RestDataAtomic linkedRecordType = (RestDataAtomic) restDataRecordLink
				.getFirstChildWithNameInData("linkedRecordType");
		RestDataAtomic linkedRecordId = (RestDataAtomic) restDataRecordLink
				.getFirstChildWithNameInData("linkedRecordId");

		assertEquals(linkedRecordType.getValue(), "linkedRecordType");
		assertEquals(linkedRecordId.getValue(), "linkedRecordId");
		assertEquals(restDataRecordLink.getRepeatId(), "j");
	}

	@Test
	public void testToRestWithLinkedRepeatId() {
		SpiderDataAtomic linkedRepeatId = SpiderDataAtomic.withNameInDataAndValue("linkedRepeatId",
				"linkedOne");
		spiderDataRecordLink.addChild(linkedRepeatId);
		RestDataRecordLink restDataRecordLink = dataRecordLinkSpiderToRestConverter.toRest();

		RestDataAtomic restLinkedRepeatId = (RestDataAtomic) restDataRecordLink
				.getFirstChildWithNameInData("linkedRepeatId");
		assertEquals(restLinkedRepeatId.getValue(), "linkedOne");
	}

	@Test
	public void testToRestWithLinkedPath() {
		SpiderDataGroup spiderDataGroup = SpiderDataGroup.withNameInData("linkedPath");
		spiderDataRecordLink.addChild(spiderDataGroup);
		RestDataRecordLink restDataRecordLink = dataRecordLinkSpiderToRestConverter.toRest();
		RestDataGroup linkedPath = (RestDataGroup) restDataRecordLink
				.getFirstChildWithNameInData("linkedPath");
		assertEquals(linkedPath.getNameInData(), "linkedPath");
	}

	@Test
	public void testToRestWithAction() {
		spiderDataRecordLink.addAction(Action.READ);
		RestDataRecordLink restDataRecordLink = dataRecordLinkSpiderToRestConverter.toRest();
		assertEquals(restDataRecordLink.getNameInData(), "nameInData");

		RestDataAtomic linkedRecordType = (RestDataAtomic) restDataRecordLink
				.getFirstChildWithNameInData("linkedRecordType");
		RestDataAtomic linkedRecordId = (RestDataAtomic) restDataRecordLink
				.getFirstChildWithNameInData("linkedRecordId");

		assertEquals(linkedRecordType.getValue(), "linkedRecordType");
		assertEquals(linkedRecordId.getValue(), "linkedRecordId");
		ActionLink actionLink = restDataRecordLink.getActionLink("read");
		assertEquals(actionLink.getAction(), Action.READ);
		assertEquals(actionLink.getURL(),
				"http://localhost:8080/therest/rest/record/linkedRecordType/linkedRecordId");
		assertEquals(actionLink.getRequestMethod(), "GET");
	}

}
