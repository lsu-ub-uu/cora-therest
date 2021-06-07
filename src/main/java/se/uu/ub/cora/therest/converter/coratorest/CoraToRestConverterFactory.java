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

import se.uu.ub.cora.data.DataList;
import se.uu.ub.cora.data.DataRecord;

public interface CoraToRestConverterFactory {

	/**
	 * Factors a {@link CoraToRestConverter} for converting from a {@link DataRecord} to a Rest
	 * version of the record.
	 * 
	 * @param dataRecord,
	 *            a {@link DataRecord} to convert
	 * 
	 * @param url,
	 *            a String used for converting links
	 * 
	 * @return a {@link CoraToRestConverter}
	 */
	CoraToRestConverter factorForDataRecord(DataRecord dataRecord, String url);

	/**
	 * Factors a {@link CoraToRestConverter} for converting from a {@link DataList} to a Rest
	 * version of the list.
	 * 
	 * @param recordList
	 * @param url
	 * 
	 * @param recordList,
	 *            a {@link DataList} to convert
	 * 
	 * @param url,
	 *            a String used for converting links
	 * 
	 * @return a {@link CoraToRestConverter}
	 */
	CoraToRestConverter factorForDataList(DataList recordList, String url);

}
