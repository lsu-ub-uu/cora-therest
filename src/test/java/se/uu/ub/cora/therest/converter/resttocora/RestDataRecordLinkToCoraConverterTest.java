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

import se.uu.ub.cora.data.DataRecordLink;
import se.uu.ub.cora.data.DataRecordLinkProvider;
import se.uu.ub.cora.therest.coradata.DataRecordLinkFactorySpy;
import se.uu.ub.cora.therest.coradata.DataRecordLinkSpy;
import se.uu.ub.cora.therest.data.RestDataAtomic;
import se.uu.ub.cora.therest.data.RestDataRecordLink;

public class RestDataRecordLinkToCoraConverterTest {

	private DataRecordLinkFactorySpy dataRecordLinkFactory;

	@BeforeMethod
	public void setUp() {
		dataRecordLinkFactory = new DataRecordLinkFactorySpy();
		DataRecordLinkProvider.setDataRecordLinkFactory(dataRecordLinkFactory);
	}

	@Test
	public void testConverterExtendsDataGroupConverter() {
		RestDataRecordLinkToCoraConverter converter = RestDataRecordLinkToCoraConverter
				.fromRestDataGroup(null);
		assertTrue(converter instanceof RestDataGroupToCoraConverter);
	}

	@Test
	public void testToData() {
		RestDataRecordLink restDataRecordLink = RestDataRecordLink.withNameInData("aLink");
		RestDataAtomic linkedRecordTypeChild = RestDataAtomic
				.withNameInDataAndValue("linkedRecordType", "someRecordType");
		restDataRecordLink.addChild(linkedRecordTypeChild);

		RestDataAtomic linkedRecordIdChild = RestDataAtomic.withNameInDataAndValue("linkedRecordId",
				"someRecordId");
		restDataRecordLink.addChild(linkedRecordIdChild);
		RestDataRecordLinkToCoraConverter converter = RestDataRecordLinkToCoraConverter
				.fromRestDataGroup(restDataRecordLink);

		DataRecordLink dataRecordLink = (DataRecordLink) converter.convert();
		DataRecordLinkSpy factoredRecordLink = dataRecordLinkFactory.factoredRecordLink;
		assertSame(dataRecordLink, factoredRecordLink);

		assertEquals(factoredRecordLink.getChildren().size(), 2);
		assertEquals(factoredRecordLink.getFirstAtomicValueWithNameInData("linkedRecordType"),
				"someRecordType");
		assertEquals(factoredRecordLink.getFirstAtomicValueWithNameInData("linkedRecordId"),
				"someRecordId");

	}
}
