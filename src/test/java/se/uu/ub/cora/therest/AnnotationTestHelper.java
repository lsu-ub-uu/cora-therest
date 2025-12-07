/*
 * Copyright 2024, 2025 Uppsala University Library
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
package se.uu.ub.cora.therest;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.glassfish.jersey.media.multipart.FormDataParam;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;

public class AnnotationTestHelper {

	private Method method;
	private Annotation[][] parameterAnnotations;
	private Annotation[] classAnnotations;

	public static AnnotationTestHelper createAnnotationTestHelperForClass(Class<?> endpointClass) {
		return new AnnotationTestHelper(endpointClass);
	}

	public static AnnotationTestHelper createAnnotationTestHelperForClassMethodNameAndNumOfParameters(
			Class<?> endpointClass, String methodName, int numOfParameters)
			throws NoSuchMethodException {
		var parameters = generateParameters(numOfParameters);
		return new AnnotationTestHelper(endpointClass, methodName, parameters);
	}

	private AnnotationTestHelper(Class<?> endpointClass, String methodName, Class<?>[] parameters)
			throws NoSuchMethodException {

		classAnnotations = endpointClass.getDeclaredAnnotations();
		method = endpointClass.getMethod(methodName, parameters);
		parameterAnnotations = method.getParameterAnnotations();

	}

	public AnnotationTestHelper(Class<?> endpointClass) {
		classAnnotations = endpointClass.getDeclaredAnnotations();
	}

	public void assertPathAnnotationForClass(String expectedAnnotation) {
		if (annotationExistsInClass(expectedAnnotation)) {
			assertTrue(true);
		} else {
			fail();
		}
	}

	private boolean annotationExistsInClass(String expectedAnnotation) {
		for (Annotation annotation : classAnnotations) {
			Path pathAnnotation = (Path) annotation;
			if (pathAnnotation.value().equals(expectedAnnotation)) {
				return true;
			}
		}
		return false;
	}

	public static AnnotationTestHelper createAnnotationTestHelperForClassMethodNameAndParameters(
			Class<?> class1, String methodName, Class<?>[] parameters)
			throws NoSuchMethodException {
		return new AnnotationTestHelper(class1, methodName, parameters);
	}

	public void assertHttpMethodAndPathAnnotation(String httpMethod, String expectedPath) {
		assertHttpMethodAnnotation(httpMethod);
		assertPathAnnotation(expectedPath);
	}

	public void assertHttpMethodAnnotation(String httpMethod) {
		Annotation[] annotations = method.getAnnotations();
		Class<? extends Annotation> httpMethodAnnotation = annotations[0].annotationType();
		String httpMethodAnnotationClassName = httpMethodAnnotation.toString();
		assertTrue(httpMethodAnnotationClassName.endsWith(httpMethod));
	}

	public void assertPathAnnotation(String expectedPath) {
		Path pathAnnotation = method.getAnnotation(Path.class);
		assertNotNull(pathAnnotation);
		assertEquals(pathAnnotation.value(), expectedPath);
	}

	public void assertAnnotationForAuthTokenParameters() {
		assertAuthTokenAnnotations(parameterAnnotations, 0);
	}

	public void assertAnnotationForAuthTokensAndTypeParameters() {
		assertAnnotationForAuthTokenParameters();
		assertTypeAnnotation(parameterAnnotations, 2);
	}

	public void assertAnnotationForAuthTokensAndTypeAndIdParameters() {
		assertAnnotationForAuthTokensAndTypeParameters();
		assertPathParamAnnotationByNameAndPosition("id", 3);
	}

	private void assertAuthTokenAnnotations(Annotation[][] parameterAnnotations,
			int startPosition) {
		HeaderParam headerAuthTokenParameter = (HeaderParam) parameterAnnotations[startPosition][0];
		assertEquals(headerAuthTokenParameter.value(), "authToken");
		QueryParam queryAuthTokenParameter = (QueryParam) parameterAnnotations[startPosition
				+ 1][0];
		assertEquals(queryAuthTokenParameter.value(), "authToken");
	}

	private void assertTypeAnnotation(Annotation[][] parameterAnnotations, int startPosition) {
		assertPathParamAnnotationByNameAndPosition("type", startPosition);
	}

	public void assertAnnotationForHeaderAuthToken() {
		HeaderParam headerAuthTokenParameter = (HeaderParam) parameterAnnotations[0][0];
		assertEquals(headerAuthTokenParameter.value(), "authToken");
	}

	public void assertPathParamAnnotationByNameAndPosition(String name, int startPosition) {
		PathParam parameter = (PathParam) parameterAnnotations[startPosition][0];
		assertEquals(parameter.value(), name);
	}

	public void assertContextAnnotationForPosition(int startPosition) {
		var parameter = parameterAnnotations[startPosition][0];
		assertEquals(parameter.annotationType(), Context.class);
	}

	public void assertFormDataParamAnnotationByNameAndPositionAndType(String name,
			int startPosition) {
		FormDataParam parameter = (FormDataParam) parameterAnnotations[startPosition][0];
		assertEquals(parameter.value(), name);
	}

	public void assertQueryParamAnnotationByNameAndPosition(String name, int startPosition) {
		QueryParam parameter = (QueryParam) parameterAnnotations[startPosition][0];
		assertEquals(parameter.value(), name);
	}

	public void assertProducesAnnotation(String... accept) {
		Produces producesAnnotation = method.getAnnotation(Produces.class);
		assertNotNull(producesAnnotation);
		assertProducesValues(producesAnnotation, accept);
		assertEquals(producesAnnotation.value().length, accept.length);
	}

	public void assertConsumesAnnotation(String... accept) {
		Consumes consumesAnnotation = method.getAnnotation(Consumes.class);
		assertNotNull(consumesAnnotation);
		assertConsumesValues(consumesAnnotation, accept);
		assertEquals(consumesAnnotation.value().length, accept.length);
	}

	private void assertConsumesValues(Consumes consumesAnnotation, String... accept) {
		for (int i = 0; i < accept.length; i++) {
			assertEquals(consumesAnnotation.value()[i], accept[i]);
		}
	}

	private void assertProducesValues(Produces producesAnnotation, String... accept) {
		for (int i = 0; i < accept.length; i++) {
			assertEquals(producesAnnotation.value()[i], accept[i]);
		}
	}

	public static Method getMethodUsingNameAndNumOfParameters(Class<?> class1, String methodName,
			int numOfParameters) throws NoSuchMethodException {
		Class<?> endpointClass = class1;

		var parameters = generateParameters(numOfParameters);

		return endpointClass.getMethod(methodName, parameters);
	}

	private static Class<?>[] generateParameters(int numOfParameters) {
		Class<?>[] parameters = new Class<?>[numOfParameters];

		for (int i = 0; i < numOfParameters; i++) {
			parameters[i] = String.class;
		}
		return parameters;
	}

}