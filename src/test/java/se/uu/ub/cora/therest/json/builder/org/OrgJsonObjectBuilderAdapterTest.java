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

package se.uu.ub.cora.therest.json.builder.org;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import se.uu.ub.cora.therest.json.builder.JsonArrayBuilder;
import se.uu.ub.cora.therest.json.parser.JsonArray;
import se.uu.ub.cora.therest.json.parser.JsonObject;
import se.uu.ub.cora.therest.json.parser.JsonString;

import static org.testng.Assert.assertEquals;

public class OrgJsonObjectBuilderAdapterTest {
	private OrgJsonObjectBuilderAdapter objectBuilderAdapter;

	@BeforeMethod
	public void beforeMethod() {
		objectBuilderAdapter = new OrgJsonObjectBuilderAdapter();
	}

	@Test
	public void testAddKeyValue() {
		objectBuilderAdapter.addKeyString("key", "value");
		JsonObject jsonObject = objectBuilderAdapter.toJsonObject();
		JsonString jsonString = jsonObject.getValueAsJsonString("key");
		assertEquals(jsonString.getStringValue(), "value");
	}

	@Test
	public void testAddKeyJsonObjectBuilder() {
		OrgJsonObjectBuilderAdapter objectBuilderAdapterChild = new OrgJsonObjectBuilderAdapter();
		objectBuilderAdapterChild.addKeyString("keyChild", "valueChild");

		objectBuilderAdapter.addKeyJsonObjectBuilder("key", objectBuilderAdapterChild);
		JsonObject jsonObject = objectBuilderAdapter.toJsonObject();

		JsonObject jsonObjectChild = jsonObject.getValueAsJsonObject("key");

		JsonString jsonString = jsonObjectChild.getValueAsJsonString("keyChild");
		assertEquals(jsonString.getStringValue(), "valueChild");
	}

	@Test
	public void testAddKeyJsonArrayBuilder() {
		JsonArrayBuilder jsonArrayBuilderChild = new OrgJsonArrayBuilderAdapter();
		jsonArrayBuilderChild.addString("valueChild");

		objectBuilderAdapter.addKeyJsonArrayBuilder("key", jsonArrayBuilderChild);

		JsonObject jsonObject = objectBuilderAdapter.toJsonObject();

		JsonArray jsonArrayChild = jsonObject.getValueAsJsonArray("key");

		JsonString jsonString = jsonArrayChild.getValueAsJsonString(0);
		assertEquals(jsonString.getStringValue(), "valueChild");
	}

	@Test
	public void testToJsonFormattedString() {
		OrgJsonObjectBuilderAdapter objectBuilderAdapterChild = new OrgJsonObjectBuilderAdapter();
		objectBuilderAdapterChild.addKeyString("keyChild", "valueChild");

		objectBuilderAdapter.addKeyJsonObjectBuilder("key", objectBuilderAdapterChild);

		String json = objectBuilderAdapter.toJsonFormattedString();
		assertEquals(json, "{\"key\":{\"keyChild\":\"valueChild\"}}");
	}
}
