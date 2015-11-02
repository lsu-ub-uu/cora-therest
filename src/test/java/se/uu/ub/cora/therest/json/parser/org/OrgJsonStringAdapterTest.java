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

package se.uu.ub.cora.therest.json.parser.org;

import org.testng.annotations.Test;
import se.uu.ub.cora.therest.json.parser.JsonString;
import se.uu.ub.cora.therest.json.parser.JsonValue;
import se.uu.ub.cora.therest.json.parser.JsonValueType;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class OrgJsonStringAdapterTest {
	@Test
	public void testUsingString() {
		JsonValue jsonValue = new OrgJsonStringAdapter("");
		assertTrue(jsonValue instanceof JsonString);
	}

	@Test
	public void testGetValueType() {
		JsonValue jsonValue = new OrgJsonStringAdapter("");
		assertEquals(jsonValue.getValueType(), JsonValueType.STRING);
	}

	@Test
	public void testGetStringValue() {
		JsonString jsonString = new OrgJsonStringAdapter("value");
		assertEquals(jsonString.getStringValue(), "value");
	}
}
