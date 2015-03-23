package epc.therest.json.parser.javax;

import static org.testng.Assert.assertTrue;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

import javax.json.Json;
import javax.json.JsonBuilderFactory;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import epc.therest.json.parser.JsonArray;
import epc.therest.json.parser.JsonObject;
import epc.therest.json.parser.JsonString;
import epc.therest.json.parser.JsonValue;

public class JavaxJsonValueFactoryTest {
	private JsonBuilderFactory javaxFactory;

	@BeforeMethod
	public void beforeMethod() {
		javaxFactory = Json.createBuilderFactory(null);
	}

	@Test
	public void testPrivateConstructor() throws Exception {
		Constructor<JavaxJsonValueFactory> constructor = JavaxJsonValueFactory.class
				.getDeclaredConstructor();
		Assert.assertTrue(Modifier.isPrivate(constructor.getModifiers()));
	}

	@Test(expectedExceptions = InvocationTargetException.class)
	public void testPrivateConstructorInvoke() throws Exception {
		Constructor<JavaxJsonValueFactory> constructor = JavaxJsonValueFactory.class
				.getDeclaredConstructor();
		Assert.assertTrue(Modifier.isPrivate(constructor.getModifiers()));
		constructor.setAccessible(true);
		constructor.newInstance();
	}

	@Test
	public void testCreateFromObject() {
		javax.json.JsonValue javaxJsonValue = javaxFactory.createObjectBuilder().add("id", "value")
				.build();

		JsonValue jsonValue = JavaxJsonValueFactory.createFromJavaxJsonValue(javaxJsonValue);
		assertTrue(jsonValue instanceof JsonObject);
	}

	@Test
	public void testCreateFromArray() {
		javax.json.JsonValue javaxJsonValue = javaxFactory.createArrayBuilder().add("id").build();

		JsonValue jsonValue = JavaxJsonValueFactory.createFromJavaxJsonValue(javaxJsonValue);
		assertTrue(jsonValue instanceof JsonArray);
	}

	@Test
	public void testCreateFromString() {
		javax.json.JsonObject javaxJsonValue = javaxFactory.createObjectBuilder()
				.add("id", "value").build();

		javax.json.JsonString jsonString = javaxJsonValue.getJsonString("id");

		JsonValue jsonValue = JavaxJsonValueFactory.createFromJavaxJsonValue(jsonString);
		assertTrue(jsonValue instanceof JsonString);
	}
}
