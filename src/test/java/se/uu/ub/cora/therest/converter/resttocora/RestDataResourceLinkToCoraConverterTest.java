/*
 * Copyright 2019 Uppsala University Library
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
package se.uu.ub.cora.therest.converter.resttocora;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.data.DataResourceLink;
import se.uu.ub.cora.data.DataResourceLinkProvider;
import se.uu.ub.cora.therest.coradata.DataResourceLinkFactorySpy;
import se.uu.ub.cora.therest.coradata.DataResourceLinkSpy;
import se.uu.ub.cora.therest.data.RestDataAtomic;
import se.uu.ub.cora.therest.data.RestDataResourceLink;

public class RestDataResourceLinkToCoraConverterTest {

	private DataResourceLinkFactorySpy dataResourceLinkFactory;

	@BeforeMethod
	public void setUp() {
		dataResourceLinkFactory = new DataResourceLinkFactorySpy();
		DataResourceLinkProvider.setDataResourceLinkFactory(dataResourceLinkFactory);
	}

	@Test
	public void testConverterExtendsDataGroupConverter() {
		RestDataResourceLinkToCoraConverter converter = RestDataResourceLinkToCoraConverter
				.fromRestDataGroup(null);
		assertTrue(converter instanceof RestDataGroupToCoraConverter);
	}

	@Test
	public void testToData() {
		RestDataResourceLink restDataResourceLink = createResourceLinkWithChildren();

		RestDataResourceLinkToCoraConverter converter = RestDataResourceLinkToCoraConverter
				.fromRestDataGroup(restDataResourceLink);

		DataResourceLink dataResourceLink = (DataResourceLink) converter.convert();
		DataResourceLinkSpy factoredRecordLink = dataResourceLinkFactory.factoredResourceLink;
		assertSame(dataResourceLink, factoredRecordLink);

		assertEquals(factoredRecordLink.getChildren().size(), 4);
		assertEquals(factoredRecordLink.getFirstAtomicValueWithNameInData("streamId"),
				"someStreamId");
		assertEquals(factoredRecordLink.getFirstAtomicValueWithNameInData("filename"),
				"someFilename");
		assertEquals(factoredRecordLink.getFirstAtomicValueWithNameInData("filesize"), "12345");
		assertEquals(factoredRecordLink.getFirstAtomicValueWithNameInData("mimeType"),
				"someMimeType");

	}

	private RestDataResourceLink createResourceLinkWithChildren() {
		RestDataResourceLink restDataResourceLink = RestDataResourceLink.withNameInData("aLink");

		RestDataAtomic streamId = RestDataAtomic.withNameInDataAndValue("streamId", "someStreamId");
		restDataResourceLink.addChild(streamId);
		RestDataAtomic filename = RestDataAtomic.withNameInDataAndValue("filename", "someFilename");
		restDataResourceLink.addChild(filename);
		RestDataAtomic filesize = RestDataAtomic.withNameInDataAndValue("filesize", "12345");
		restDataResourceLink.addChild(filesize);
		RestDataAtomic mimeType = RestDataAtomic.withNameInDataAndValue("mimeType", "someMimeType");
		restDataResourceLink.addChild(mimeType);
		return restDataResourceLink;
	}
}
