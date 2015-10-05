package se.uu.ub.cora.therest.json.parser.org;

import static org.testng.Assert.assertTrue;

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
import se.uu.ub.cora.therest.json.parser.org.OrgJsonValueFactory;

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
