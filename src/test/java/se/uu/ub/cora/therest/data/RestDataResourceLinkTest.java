/*
 * Copyright 2016 Uppsala University Library
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
 */package se.uu.ub.cora.therest.data;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.spider.data.Action;

public class RestDataResourceLinkTest {
	private RestDataResourceLink resourceLink;

	@BeforeMethod
	public void setUp() {
		resourceLink = RestDataResourceLink.withNameInData("aNameInData");

		RestDataAtomic streamId = RestDataAtomic.withNameInDataAndValue("streamId", "aStreamId");
		resourceLink.addChild(streamId);
	}

	@Test
	public void testInit() {
		assertEquals(resourceLink.getChildren().size(), 1);
		assertTrue(resourceLink.containsChildWithNameInData("streamId"));
	}

	@Test
	public void testWithActionLinks() {
		ActionLink actionLink = ActionLink.withAction(Action.READ);
		resourceLink.addActionLink("read", actionLink);
		assertEquals(resourceLink.getActionLink("read"), actionLink);
		assertEquals(resourceLink.getActionLinks().get("read"), actionLink);
		assertNull(resourceLink.getActionLink("notAnAction"));
	}

	@Test
	public void testWithRepeatId() {
		resourceLink.setRepeatId("x2");
		assertEquals(resourceLink.getRepeatId(), "x2");
	}
}
