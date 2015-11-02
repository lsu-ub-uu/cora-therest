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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.annotations.Test;
import se.uu.ub.cora.therest.json.parser.JsonArray;
import se.uu.ub.cora.therest.json.parser.JsonObject;
import se.uu.ub.cora.therest.json.parser.JsonString;
import se.uu.ub.cora.therest.json.parser.JsonValue;

import static org.testng.Assert.assertTrue;

public class OrgJsonValueFactoryTest {
	@Test
	public void testPrivateConstructor() throws Exception {
		Constructor<OrgJsonValueFactory> constructor = OrgJsonValueFactory.class
				.getDeclaredConstructor();
		assertTrue(Modifier.isPrivate(constructor.getModifiers()));
	}

	@Test(expectedExceptions = InvocationTargetException.class)
	public void testPrivateConstructorInvoke() throws Exception {
		Constructor<OrgJsonValueFactory> constructor = OrgJsonValueFactory.class
				.getDeclaredConstructor();
		// Assert.assertTrue(Modifier.isPrivate(constructor.getModifiers()));
		constructor.setAccessible(true);
		constructor.newInstance();
	}

	@Test
	public void testCreateFromOrgJsonObject() {
		Object orgJsonObject = new JSONObject();
		JsonValue jsonValue = OrgJsonValueFactory.createFromOrgJsonObject(orgJsonObject);
		assertTrue(jsonValue instanceof JsonObject);
	}

	@Test
	public void testCreateFromOrgJsonArray() {
		Object orgJsonObject = new JSONArray();
		JsonValue jsonValue = OrgJsonValueFactory.createFromOrgJsonObject(orgJsonObject);
		assertTrue(jsonValue instanceof JsonArray);
	}

	@Test
	public void testCreateFromOrgJsonString() {
		Object string = new String();
		JsonValue jsonValue = OrgJsonValueFactory.createFromOrgJsonObject(string);
		assertTrue(jsonValue instanceof JsonString);
	}

}
