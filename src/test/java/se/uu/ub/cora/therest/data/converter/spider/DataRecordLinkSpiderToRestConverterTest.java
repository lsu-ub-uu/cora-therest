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
import se.uu.ub.cora.therest.data.RestDataGroupRecordLink;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;

public class DataRecordLinkSpiderToRestConverterTest {
	private String baseURL = "http://localhost:8080/therest/rest/record/";
	private SpiderDataRecordLink spiderDataRecordLink;
	private DataRecordLinkSpiderToRestConverter dataRecordLinkSpiderToRestConverter;

	@BeforeMethod
	public void setUp() {
		spiderDataRecordLink = SpiderDataRecordLink.withNameInData("nameInData");

		SpiderDataAtomic linkedRecordType = SpiderDataAtomic.withNameInDataAndValue("linkedRecordType", "linkedRecordType");
		spiderDataRecordLink.addChild(linkedRecordType);

		SpiderDataAtomic linkedRecordId = SpiderDataAtomic.withNameInDataAndValue("linkedRecordId", "linkedRecordId");
		spiderDataRecordLink.addChild(linkedRecordId);

		dataRecordLinkSpiderToRestConverter = DataRecordLinkSpiderToRestConverter
				.fromSpiderDataRecordLinkWithBaseURL(spiderDataRecordLink, baseURL);

	}

	@Test
	public void testToRest() {
		RestDataGroupRecordLink restDataRecordLink = dataRecordLinkSpiderToRestConverter.toRest();
		assertEquals(restDataRecordLink.getNameInData(), "nameInData");

		RestDataAtomic linkedRecordType = (RestDataAtomic) restDataRecordLink.getFirstChildWithNameInData("linkedRecordType");
		RestDataAtomic linkedRecordId = (RestDataAtomic) restDataRecordLink.getFirstChildWithNameInData("linkedRecordId");

		assertEquals(linkedRecordType.getValue(), "linkedRecordType");
		assertEquals(linkedRecordId.getValue(), "linkedRecordId");
		assertFalse(restDataRecordLink.containsChildWithNameInData("linkedPath"));
	}

	@Test
	public void testToRestWithRepeatId() {
		spiderDataRecordLink.setRepeatId("j");
		RestDataGroupRecordLink restDataRecordLink = dataRecordLinkSpiderToRestConverter.toRest();
		assertEquals(restDataRecordLink.getNameInData(), "nameInData");

		RestDataAtomic linkedRecordType = (RestDataAtomic) restDataRecordLink.getFirstChildWithNameInData("linkedRecordType");
		RestDataAtomic linkedRecordId = (RestDataAtomic) restDataRecordLink.getFirstChildWithNameInData("linkedRecordId");

		assertEquals(linkedRecordType.getValue(), "linkedRecordType");
		assertEquals(linkedRecordId.getValue(), "linkedRecordId");
		assertEquals(restDataRecordLink.getRepeatId(), "j");
	}

	@Test
	public void testToRestWithLinkedRepeatId(){
		SpiderDataAtomic linkedRepeatId = SpiderDataAtomic.withNameInDataAndValue("linkedRepeatId", "linkedOne");
		spiderDataRecordLink.addChild(linkedRepeatId);
		RestDataGroupRecordLink restDataRecordLink = dataRecordLinkSpiderToRestConverter.toRest();

		RestDataAtomic restLinkedRepeatId  = (RestDataAtomic) restDataRecordLink.getFirstChildWithNameInData("linkedRepeatId");
		assertEquals(restLinkedRepeatId.getValue(), "linkedOne");
	}

	@Test
	public void testToRestWithLinkedPath(){
		SpiderDataGroup spiderDataGroup = SpiderDataGroup.withNameInData("linkedPath");
		spiderDataRecordLink.addChild(spiderDataGroup);
		RestDataGroupRecordLink restDataRecordLink = dataRecordLinkSpiderToRestConverter.toRest();
		RestDataGroup linkedPath = (RestDataGroup) restDataRecordLink.getFirstChildWithNameInData("linkedPath");
		assertEquals(linkedPath.getNameInData(), "linkedPath");
	}

	@Test
	public void testToRestWithAction() {
		spiderDataRecordLink.addAction(Action.READ);
		RestDataGroupRecordLink restDataRecordLink = dataRecordLinkSpiderToRestConverter.toRest();
		assertEquals(restDataRecordLink.getNameInData(), "nameInData");

		RestDataAtomic linkedRecordType = (RestDataAtomic) restDataRecordLink.getFirstChildWithNameInData("linkedRecordType");
		RestDataAtomic linkedRecordId = (RestDataAtomic) restDataRecordLink.getFirstChildWithNameInData("linkedRecordId");

		assertEquals(linkedRecordType.getValue(), "linkedRecordType");
		assertEquals(linkedRecordId.getValue(), "linkedRecordId");
		ActionLink actionLink = restDataRecordLink.getActionLink("read");
		assertEquals(actionLink.getAction(), Action.READ);
		assertEquals(actionLink.getURL(),
				"http://localhost:8080/therest/rest/record/linkedRecordType/linkedRecordId");
		assertEquals(actionLink.getRequestMethod(), "GET");
	}

}
