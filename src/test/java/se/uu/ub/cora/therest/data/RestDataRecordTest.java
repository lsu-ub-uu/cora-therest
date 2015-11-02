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

import java.util.Map;
import java.util.Set;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import se.uu.ub.cora.spider.data.Action;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class RestDataRecordTest {
	private RestDataRecord restDataRecord;

	@BeforeMethod
	public void beforeMethod() {
		RestDataGroup restDataGroup = RestDataGroup.withNameInData("nameInData");
		restDataRecord = RestDataRecord.withRestDataGroup(restDataGroup);
	}

	@Test
	public void testWithNameInData() {
		String nameInData = restDataRecord.getRestDataGroup().getNameInData();
		assertEquals(nameInData, "nameInData");
	}

	@Test
	public void testKeys() {
		restDataRecord.addKey("KEY1");
		restDataRecord.addKey("KEY2");
		Set<String> keys = restDataRecord.getKeys();
		assertTrue(keys.contains("KEY1"));
		assertTrue(keys.contains("KEY2"));
	}

	@Test
	public void testActionLinks() {
		restDataRecord.addActionLink("read", ActionLink.withAction(Action.READ));
		Map<String, ActionLink> actionLinks = restDataRecord.getActionLinks();
		ActionLink actionLinkOut = actionLinks.get("read");
		assertEquals(actionLinkOut.getAction(), Action.READ);
	}

	@Test
	public void testActionLinksGet() {
		restDataRecord.addActionLink("read", ActionLink.withAction(Action.READ));
		ActionLink actionLinkOut = restDataRecord.getActionLink("read");
		assertEquals(actionLinkOut.getAction(), Action.READ);
	}

}
