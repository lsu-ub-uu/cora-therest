package epc.therest.json.parser.simple;

import static org.testng.Assert.assertTrue;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import epc.therest.json.parser.JsonArray;
import epc.therest.json.parser.JsonObject;
import epc.therest.json.parser.JsonString;
import epc.therest.json.parser.JsonValue;

public class SimpleJsonValueFactoryTest {
	@Test
	public void testPrivateConstructor() throws Exception {
		Constructor<SimpleJsonValueFactory> constructor = SimpleJsonValueFactory.class
				.getDeclaredConstructor();
		Assert.assertTrue(Modifier.isPrivate(constructor.getModifiers()));
	}

	@Test(expectedExceptions = InvocationTargetException.class)
	public void testPrivateConstructorInvoke() throws Exception {
		Constructor<SimpleJsonValueFactory> constructor = SimpleJsonValueFactory.class
				.getDeclaredConstructor();
		Assert.assertTrue(Modifier.isPrivate(constructor.getModifiers()));
		constructor.setAccessible(true);
		constructor.newInstance();
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testCreateFromObject() {
		JSONObject javaxJsonValue = new JSONObject();
		javaxJsonValue.put("id", "value");

		JsonValue jsonValue = SimpleJsonValueFactory.createFromSimpleJsonValue(javaxJsonValue);
		assertTrue(jsonValue instanceof JsonObject);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testCreateFromArray() {
		JSONArray javaxJsonValue = new JSONArray();
		javaxJsonValue.add("id");

		JsonValue jsonValue = SimpleJsonValueFactory.createFromSimpleJsonValue(javaxJsonValue);
		assertTrue(jsonValue instanceof JsonArray);
	}

	@Test
	public void testCreateFromString() {
		JsonValue jsonValue = SimpleJsonValueFactory.createFromSimpleJsonValue("id");
		assertTrue(jsonValue instanceof JsonString);
	}
}
