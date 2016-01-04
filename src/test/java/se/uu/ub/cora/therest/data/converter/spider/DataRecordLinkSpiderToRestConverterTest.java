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
import se.uu.ub.cora.spider.data.SpiderDataGroupRecordLink;
import se.uu.ub.cora.therest.data.ActionLink;
import se.uu.ub.cora.therest.data.RestDataRecordLink;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

public class DataRecordLinkSpiderToRestConverterTest {
	private String baseURL = "http://localhost:8080/therest/rest/record/";
	private SpiderDataGroupRecordLink spiderDataRecordLink;
	private DataRecordLinkSpiderToRestConverter dataRecordLinkSpiderToRestConverter;

	@BeforeMethod
	public void setUp() {
		spiderDataRecordLink = SpiderDataGroupRecordLink.withNameInData("nameInData");

		SpiderDataAtomic linkedRecordType = SpiderDataAtomic.withNameInDataAndValue("linkedRecordType", "linkedRecordType");
		spiderDataRecordLink.addChild(linkedRecordType);

		SpiderDataAtomic linkedRecordId = SpiderDataAtomic.withNameInDataAndValue("linkedRecordId", "linkedRecordId");
		spiderDataRecordLink.addChild(linkedRecordId);

		dataRecordLinkSpiderToRestConverter = DataRecordLinkSpiderToRestConverter
				.fromSpiderDataRecordLinkWithBaseURL(spiderDataRecordLink, baseURL);

	}

	@Test
	public void testToRest() {
		RestDataRecordLink restDataRecordLink = dataRecordLinkSpiderToRestConverter.toRest();
		assertEquals(restDataRecordLink.getNameInData(), "nameInData");
		assertEquals(restDataRecordLink.getLinkedRecordType(), "linkedRecordType");
		assertEquals(restDataRecordLink.getLinkedRecordId(), "linkedRecordId");
		assertNull(restDataRecordLink.getLinkedPath());
	}

	@Test
	public void testToRestWithRepeatId() {
		spiderDataRecordLink.setRepeatId("j");
		RestDataRecordLink restDataRecordLink = dataRecordLinkSpiderToRestConverter.toRest();
		assertEquals(restDataRecordLink.getNameInData(), "nameInData");
		assertEquals(restDataRecordLink.getLinkedRecordType(), "linkedRecordType");
		assertEquals(restDataRecordLink.getLinkedRecordId(), "linkedRecordId");
		assertEquals(restDataRecordLink.getRepeatId(), "j");
	}

	@Test
	public void testToRestWithLinkedRepeatId(){
		SpiderDataAtomic linkedRepeatId = SpiderDataAtomic.withNameInDataAndValue("linkedRepeatId", "linkedOne");
		spiderDataRecordLink.addChild(linkedRepeatId);
		RestDataRecordLink restDataRecordLink = dataRecordLinkSpiderToRestConverter.toRest();
		assertEquals(restDataRecordLink.getLinkedRepeatId(), "linkedOne");
	}

	@Test
	public void testToRestWithLinkedPath(){
		SpiderDataGroup spiderDataGroup = SpiderDataGroup.withNameInData("linkedPath");
		spiderDataRecordLink.addChild(spiderDataGroup);
		RestDataRecordLink restDataRecordLink = dataRecordLinkSpiderToRestConverter.toRest();
		assertEquals(restDataRecordLink.getLinkedPath().getNameInData(), "linkedPath");
	}

	@Test
	public void testToRestWithAction() {
		spiderDataRecordLink.addAction(Action.READ);
		RestDataRecordLink restDataRecordLink = dataRecordLinkSpiderToRestConverter.toRest();
		assertEquals(restDataRecordLink.getNameInData(), "nameInData");
		assertEquals(restDataRecordLink.getLinkedRecordType(), "linkedRecordType");
		assertEquals(restDataRecordLink.getLinkedRecordId(), "linkedRecordId");
		ActionLink actionLink = restDataRecordLink.getActionLink("read");
		assertEquals(actionLink.getAction(), Action.READ);
		assertEquals(actionLink.getURL(),
				"http://localhost:8080/therest/rest/record/linkedRecordType/linkedRecordId");
		assertEquals(actionLink.getRequestMethod(), "GET");
	}

}
