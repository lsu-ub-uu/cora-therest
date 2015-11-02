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

package se.uu.ub.cora.therest.data;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import se.uu.ub.cora.spider.data.Action;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

public class RestDataRecordLinkTest {
	private RestDataRecordLink recordLink;

	@BeforeMethod
	public void setUp() {
		recordLink = RestDataRecordLink.withNameInDataAndLinkedRecordTypeAndLinkedRecordId("nameInData",
				"aRecordType", "aRecordId");
	}

	@Test
	public void testInit() {
		assertEquals(recordLink.getLinkedRecordType(), "aRecordType");
		assertEquals(recordLink.getLinkedRecordId(), "aRecordId");
	}

	@Test
	public void testWithActionLinks() {
		ActionLink actionLink = ActionLink.withAction(Action.READ);
		recordLink.addActionLink("read", actionLink);
		assertEquals(recordLink.getActionLink("read"), actionLink);
		assertEquals(recordLink.getActionLinks().get("read"), actionLink);
		assertNull(recordLink.getActionLink("notAnAction"));
	}

	@Test
	public void testWithRepeatId() {
		recordLink.setRepeatId("x2");
		assertEquals(recordLink.getRepeatId(), "x2");
	}
}
