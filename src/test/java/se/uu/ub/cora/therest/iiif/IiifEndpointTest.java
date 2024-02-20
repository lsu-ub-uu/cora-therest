package se.uu.ub.cora.therest.iiif;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;
import se.uu.ub.cora.spider.binary.iiif.IiifImageReader;

public class IiifEndpointTest {
	IiifEndpoint endpoint;
	IiifImageReader iifBinaryReader = new IiifImageReaderSpy();

	@BeforeMethod
	private void beforeMethod() {
		endpoint = new IiifEndpoint();
	}

	@Test
	public void testReadRequestPathAndParameters() throws Exception {
		Method method = getMethodUsingNameAndNumOfParameters("readBinary", 6);

		assertHttpMethodAndPathAnnotation(method, "GET",
				"{identifier}/{region}/{size}/{rotation}/{quality}.{format}");

		Annotation[][] parameters = method.getParameterAnnotations();
		assertPathParams(parameters, "identifier", 0);
		assertPathParams(parameters, "region", 1);
		assertPathParams(parameters, "size", 2);
		assertPathParams(parameters, "rotation", 3);
		assertPathParams(parameters, "quality", 4);
		assertPathParams(parameters, "format", 5);
	}

	private Method getMethodUsingNameAndNumOfParameters(String methodName, int numOfParameters)
			throws NoSuchMethodException {
		Class<? extends IiifEndpoint> endpointClass = endpoint.getClass();

		var parameters = generateParameters(numOfParameters);

		return endpointClass.getMethod(methodName, parameters);

	}

	private Class<?>[] generateParameters(int numOfParameters) {
		Class<?>[] parameters = new Class<?>[numOfParameters];

		for (int i = 0; i < numOfParameters; i++) {
			parameters[i] = String.class;
		}
		return parameters;
	}

	private void assertHttpMethodAndPathAnnotation(Method method, String httpMethod,
			String expectedPath) {
		assertHttpMethodAnnotation(method, httpMethod);
		assertPathAnnotation(method, expectedPath);
	}

	private void assertPathAnnotation(Method method, String expectedPath) {
		Path pathAnnotation = method.getAnnotation(Path.class);
		assertNotNull(pathAnnotation);
		assertEquals(pathAnnotation.value(), expectedPath);
	}

	private void assertHttpMethodAnnotation(Method method, String httpMethod) {
		Annotation[] annotations = method.getAnnotations();
		Class<? extends Annotation> httpMethodAnnotation = annotations[0].annotationType();
		String httpMethodAnnotationClassName = httpMethodAnnotation.toString();
		assertTrue(httpMethodAnnotationClassName.endsWith(httpMethod));
	}

	private void assertPathParams(Annotation[][] parameters, String parameterName,
			int parameterPosition) {
		PathParam typeParameter = (PathParam) parameters[parameterPosition][0];
		assertEquals(typeParameter.value(), parameterName);
	}

	@Test
	public void testReadResponseOk() throws Exception {
		Response response = endpoint.readBinary("someIdentifier", "someRegion", "someSize",
				"someRotation", "someQuality", "someformat");

		assertEquals(response.getStatusInfo(), Response.Status.OK);
	}
}
