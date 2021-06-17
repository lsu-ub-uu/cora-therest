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
package se.uu.ub.cora.therest.record;

import se.uu.ub.cora.data.DataList;
import se.uu.ub.cora.data.DataRecord;
import se.uu.ub.cora.therest.data.converter.coradata.DataToRestConverter;
import se.uu.ub.cora.therest.data.converter.coradata.DataToRestConverterFactory;

public class DataToRestConverterFactorySpy implements DataToRestConverterFactory {

	public DataRecord dataRecord;
	public String url;
	public DataRecordToRestConverterSpy toRestConverter;
	public DataList recordList;

	@Override
	public DataToRestConverter factorForDataRecord(DataRecord dataRecord, String url) {
		this.dataRecord = dataRecord;
		this.url = url;
		toRestConverter = new DataRecordToRestConverterSpy();
		return toRestConverter;
	}

	@Override
	public DataToRestConverter factorForDataList(DataList recordList, String url) {
		this.recordList = recordList;
		this.url = url;
		toRestConverter = new DataRecordToRestConverterSpy();
		toRestConverter.typeOfData = "recordList";
		return toRestConverter;
	}

}
