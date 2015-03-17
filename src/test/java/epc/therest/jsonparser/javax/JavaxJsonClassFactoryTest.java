package epc.therest.jsonparser.javax;

import static org.testng.Assert.assertTrue;

import javax.json.Json;
import javax.json.JsonBuilderFactory;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import epc.therest.jsonparser.JsonArray;
import epc.therest.jsonparser.JsonObject;
import epc.therest.jsonparser.JsonString;
import epc.therest.jsonparser.JsonValue;

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
