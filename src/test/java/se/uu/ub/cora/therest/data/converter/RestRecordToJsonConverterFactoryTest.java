/*
 * Copyright 2021 Uppsala University Library
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
package se.uu.ub.cora.therest.data.converter;

import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

import se.uu.ub.cora.json.builder.org.OrgJsonBuilderFactoryAdapter;
import se.uu.ub.cora.therest.data.RestDataRecord;

public class RestRecordToJsonConverterFactoryTest {

	@Test
	public void testFactor() {

		RestRecordToJsonConverterFactoryImp restRecordToJsonConverterFactoryImp = new RestRecordToJsonConverterFactoryImp();

		RestDataRecord restDataRecord = RestDataRecord.withRestDataGroup(null);

		RestRecordToJsonConverterImp dataRecordToJsonConverter = restRecordToJsonConverterFactoryImp
				.factor(restDataRecord);

		assertTrue(dataRecordToJsonConverter instanceof RestRecordToJsonConverter);

		assertTrue(dataRecordToJsonConverter
				.getJsonBuilderFactory() instanceof OrgJsonBuilderFactoryAdapter);

		assertSame(dataRecordToJsonConverter.getRestDataRecord(), restDataRecord);

	}

}
