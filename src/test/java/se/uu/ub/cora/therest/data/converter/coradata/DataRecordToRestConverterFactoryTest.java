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
package se.uu.ub.cora.therest.data.converter.coradata;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

import se.uu.ub.cora.data.DataRecord;
import se.uu.ub.cora.therest.data.DataGroupSpy;
import se.uu.ub.cora.therest.data.DataRecordSpy;

public class DataRecordToRestConverterFactoryTest {

	@Test
	public void testFactor() {

		DataRecordToRestConverterFactory factory = new DataRecordToRestConverterFactoryImp();

		DataRecord dataRecord = new DataRecordSpy(new DataGroupSpy("someNameInData"));
		String url = "someUrl";

		DataRecordToRestConverterImp dataRecordToRestConverter = (DataRecordToRestConverterImp) factory
				.factor(dataRecord, url);

		assertTrue(dataRecordToRestConverter instanceof DataRecordToRestConverterImp);

		assertTrue(dataRecordToRestConverter
				.getConverterFactory() instanceof DataToRestConverterFactoryImp);

		assertSame(dataRecordToRestConverter.getDataRecord(), dataRecord);

		assertEquals(dataRecordToRestConverter.getBaseUrl(), url);

	}

}
