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

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import se.uu.ub.cora.data.Action;
import se.uu.ub.cora.therest.testdata.DataCreator;

public class ActionLinkTest {
	@Test
	public void testInit() {
		ActionLink actionLink = ActionLink.withAction(Action.READ);
		assertEquals(actionLink.getAction(), Action.READ);
	}

	@Test
	public void testURL() {
		ActionLink actionLink = ActionLink.withAction(Action.READ);
		actionLink.setURL("http://test.org/test/test:001");
		assertEquals(actionLink.getURL(), "http://test.org/test/test:001");
	}

	@Test
	public void testRequestMethod() {
		ActionLink actionLink = ActionLink.withAction(Action.READ);
		actionLink.setRequestMethod("GET");
		assertEquals(actionLink.getRequestMethod(), "GET");
	}

	@Test
	public void testAccept() {
		ActionLink actionLink = ActionLink.withAction(Action.READ);
		actionLink.setAccept("application/metadata_record+json");
		assertEquals(actionLink.getAccept(), "application/metadata_record+json");
	}

	@Test
	public void testContentType() {
		ActionLink actionLink = ActionLink.withAction(Action.READ);
		actionLink.setContentType("application/metadata_record+json");
		assertEquals(actionLink.getContentType(), "application/metadata_record+json");
	}

	@Test
	public void testBody() {
		ActionLink actionLink = ActionLink.withAction(Action.READ);
		RestDataGroup workOrder = DataCreator.createWorkOrder();
		actionLink.setBody(workOrder);
		assertEquals(actionLink.getBody(), workOrder);
	}

}
