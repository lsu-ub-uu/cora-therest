/*
 * Copyright 2015 Uppsala University Library
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

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.data.DataAttribute;
import se.uu.ub.cora.data.DataAttributeProvider;
import se.uu.ub.cora.therest.coradata.DataAttributeFactorySpy;
import se.uu.ub.cora.therest.data.RestDataAttribute;

public class DataAttributeRestToSpiderConverterTest {

	DataAttributeFactorySpy dataAttributeFactory;

	@BeforeMethod
	public void setUp() {
		dataAttributeFactory = new DataAttributeFactorySpy();
		DataAttributeProvider.setDataAttributeFactory(dataAttributeFactory);
	}

	@Test
	public void testToSpider() {
		String nameInData = "type";
		String value = "someTypeValue";
		RestDataAttribute restDataAttribute = RestDataAttribute.withNameInDataAndValue(nameInData,
				value);
		DataAttributeRestToDataConverter converter = DataAttributeRestToDataConverter
				.fromRestDataAttribute(restDataAttribute);
		DataAttribute dataAttribute = converter.convert();
		assertSame(dataAttribute, dataAttributeFactory.factoredDataAttribute);

		assertEquals(dataAttributeFactory.nameInData, nameInData);
		assertEquals(dataAttributeFactory.value, value);

	}
}
