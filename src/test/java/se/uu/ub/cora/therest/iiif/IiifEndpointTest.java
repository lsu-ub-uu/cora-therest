/*
 * Copyright 2024 Uppsala University Library
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
package se.uu.ub.cora.therest.iiif;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.spider.binary.iiif.IiifReader;
import se.uu.ub.cora.spider.spies.binary.iiif.IiifReaderSpy;
import se.uu.ub.cora.therest.AnnotationTestHelper;

public class IiifEndpointTest {
	IiifEndpoint endpoint;
	IiifReader iifBinaryReader = new IiifReaderSpy();

	@BeforeMethod
	private void beforeMethod() {
		endpoint = new IiifEndpoint();
	}

	@Test
	public void testReadRequestPathAndParameters() throws Exception {
		// Class<?>[] parameters = { String.class, String.class, String.class, String.class,
		// InputStream.class, String.class };
		// AnnotationTestHelper annotationHelper = AnnotationTestHelper
		// .createAnnotationTestHelperForClassMethodNameAndParameters(
		// recordEndpoint.getClass(), "uploadResourceXml", parameters);

		AnnotationTestHelper annotationHelper = AnnotationTestHelper
				.createAnnotationTestHelperForClassMethodNameAndNumOfParameters(endpoint.getClass(),
						"readBinary", 6);

		annotationHelper.assertHttpMethodAndPathAnnotation("GET",
				"{identifier}/{region}/{size}/{rotation}/{quality}.{format}");
		// annotationHelper.assertConsumesAnnotation(APPLICATION_VND_UUB_RECORD_JSON);
		// annotationHelper.assertProducesAnnotation(APPLICATION_VND_UUB_RECORD_LIST_XML_QS09);
		// annotationHelper.assertAnnotationForAuthTokenParameters();
		// annotationHelper.assertAnnotationForAuthTokensAndTypeParameters();
		// annotationHelper.assertAnnotationForAuthTokenAndTypeAndIdParameters();
		// annotationHelper.assertFormDataParamAnnotationByNameAndPositionAndType("file", 4);
		// annotationHelper.assertPathParamAnnotationByNameAndPosition("searchId", 2);
		// annotationHelper.assertQueryParamAnnotationByNameAndPosition("filter", 3);
		annotationHelper.assertPathParamAnnotationByNameAndPosition("identifier", 0);
		annotationHelper.assertPathParamAnnotationByNameAndPosition("region", 1);
		annotationHelper.assertPathParamAnnotationByNameAndPosition("size", 2);
		annotationHelper.assertPathParamAnnotationByNameAndPosition("rotation", 3);
		annotationHelper.assertPathParamAnnotationByNameAndPosition("quality", 4);
		annotationHelper.assertPathParamAnnotationByNameAndPosition("format", 5);
	}

	@Test
	public void testReadResponseOk() throws Exception {
		// Response response = endpoint.readBinary("someIdentifier", "someRegion", "someSize",
		// "someRotation", "someQuality", "someformat");
		//
		// assertEquals(response.getStatusInfo(), Response.Status.OK);
	}
}
