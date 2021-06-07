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
package se.uu.ub.cora.therest.converter.coratorest;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.data.DataList;
import se.uu.ub.cora.data.DataRecord;
import se.uu.ub.cora.therest.coradata.DataGroupSpy;
import se.uu.ub.cora.therest.coradata.DataListSpy;
import se.uu.ub.cora.therest.coradata.DataRecordSpy;

public class CoraToRestConverterFactoryTest {

	private CoraToRestConverterFactory factory;
	private String url;

	@BeforeMethod
	public void setUp() {
		factory = new CoraToRestConverterFactoryImp();
		url = "someUrl";
	}

	@Test
	public void testFactorForDataRecord() {
		DataRecord dataRecord = new DataRecordSpy(new DataGroupSpy("someNameInData"));

		CoraDataRecordToRestConverter converter = (CoraDataRecordToRestConverter) factory
				.factorForDataRecord(dataRecord, url);

		assertTrue(
				converter.getConverterFactory() instanceof CoraDataGroupToRestConverterFactoryImp);
		assertSame(converter.getDataRecord(), dataRecord);
		assertEquals(converter.getBaseUrl(), url);

	}

	@Test
	public void testFactorForDataList() {
		DataList recordList = new DataListSpy("someRecordType");

		CoraDataListToRestConverter converter = (CoraDataListToRestConverter) factory
				.factorForDataList(recordList, url);

		assertSame(converter.getDataList(), recordList);
		assertEquals(converter.getBaseUrl(), url);
	}

}
