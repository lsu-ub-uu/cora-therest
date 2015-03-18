package epc.therest.json.parser.javax;

import static org.testng.Assert.assertTrue;

import javax.json.Json;
import javax.json.JsonBuilderFactory;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import epc.therest.json.parser.JsonArray;
import epc.therest.json.parser.JsonObject;
import epc.therest.json.parser.JsonString;
import epc.therest.json.parser.JsonValue;
import epc.therest.json.parser.javax.JavaxJsonClassFactory;
import epc.therest.json.parser.javax.JavaxJsonClassFactoryImp;

public class JavaxJsonClassFactoryTest {
	private JsonBuilderFactory javaxFactory;
	private JavaxJsonClassFactory factory;

	@BeforeMethod
	public void beforeMethod() {
		javaxFactory = Json.createBuilderFactory(null);
		factory = new JavaxJsonClassFactoryImp();
	}

	@Test
	public void testCreateFromObject() {
		javax.json.JsonValue javaxJsonValue = javaxFactory.createObjectBuilder().add("id", "value")
				.build();

		JsonValue jsonValue = factory.createFromJavaxJsonValue(javaxJsonValue);
		assertTrue(jsonValue instanceof JsonObject);
	}

	@Test
	public void testCreateFromArray() {
		javax.json.JsonValue javaxJsonValue = javaxFactory.createArrayBuilder().add("id").build();

		JsonValue jsonValue = factory.createFromJavaxJsonValue(javaxJsonValue);
		assertTrue(jsonValue instanceof JsonArray);
	}

	@Test
	public void testCreateFromString() {
		javax.json.JsonObject javaxJsonValue = javaxFactory.createObjectBuilder()
				.add("id", "value").build();

		javax.json.JsonString jsonString = javaxJsonValue.getJsonString("id");

		JsonValue jsonValue = factory.createFromJavaxJsonValue(jsonString);
		assertTrue(jsonValue instanceof JsonString);
	}
}
