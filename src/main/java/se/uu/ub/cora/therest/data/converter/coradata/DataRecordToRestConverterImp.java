/*
 * Copyright 2015, 2016, 2019, 2020 Uppsala University Library
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

import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataRecord;
import se.uu.ub.cora.therest.data.RestDataGroup;
import se.uu.ub.cora.therest.data.RestDataRecord;
import se.uu.ub.cora.therest.data.converter.ConverterException;
import se.uu.ub.cora.therest.data.converter.ConverterInfo;

public final class DataRecordToRestConverterImp implements DataRecordToRestConverter {

	private DataRecord dataRecord;
	private String baseURL;
	private DataGroup dataGroup;
	private RestDataRecord restDataRecord;
	private String recordId;
	private String recordType;
	private ConverterInfo converterInfo;
	private DataToRestConverterFactory converterFactory;

	public static DataRecordToRestConverterImp fromDataRecordWithBaseURLAndConverterFactory(
			DataRecord dataRecord, String url, DataToRestConverterFactory converterFactory) {
		return new DataRecordToRestConverterImp(dataRecord, url, converterFactory);
	}

	private DataRecordToRestConverterImp(DataRecord dataRecord, String url,
			DataToRestConverterFactory converterFactory) {
		this.dataRecord = dataRecord;
		this.baseURL = url;
		this.converterFactory = converterFactory;
	}

	@Override
	public RestDataRecord toRest() {
		try {
			return convertToRest();
		} catch (Exception e) {
			throw new ConverterException("No recordInfo found conversion not possible: " + e, e);
		}
	}

	private RestDataRecord convertToRest() {
		dataGroup = dataRecord.getDataGroup();
		extractIdAndType();
		createConverterInfo();

		convertToRestRecord();

		createRestLinks();
		convertReadPermissions();
		convertWritePermissions();
		return restDataRecord;
	}

	private void extractIdAndType() {
		DataGroup recordInfo = dataGroup.getFirstGroupWithNameInData("recordInfo");
		recordId = recordInfo.getFirstAtomicValueWithNameInData("id");
		DataGroup typeGroup = recordInfo.getFirstGroupWithNameInData("type");
		recordType = typeGroup.getFirstAtomicValueWithNameInData("linkedRecordId");
	}

	private void createConverterInfo() {
		String recordURL = baseURL + String.join("/", recordType, recordId);
		converterInfo = ConverterInfo.withBaseURLAndRecordURLAndTypeAndId(baseURL, recordURL,
				recordType, recordId);
	}

	private void convertToRestRecord() {
		DataToRestConverter dataGroupToRestConverter = converterFactory
				.factorForDataGroupWithConverterInfo(dataGroup, converterInfo);
		RestDataGroup restDataGroup = dataGroupToRestConverter.toRest();
		restDataRecord = RestDataRecord.withRestDataGroup(restDataGroup);
	}

	private void createRestLinks() {
		ActionDataToRestConverter actionToRestConverter = converterFactory
				.factorForActionsUsingConverterInfoAndDataGroup(dataRecord.getActions(),
						converterInfo, dataGroup);

		restDataRecord.setActionLinks(actionToRestConverter.toRest());
	}

	private void convertReadPermissions() {
		for (String readPermission : dataRecord.getReadPermissions()) {
			restDataRecord.addReadPermission(readPermission);
		}
	}

	private void convertWritePermissions() {
		for (String writePermission : dataRecord.getWritePermissions()) {
			restDataRecord.addWritePermission(writePermission);
		}
	}

	public DataToRestConverterFactory getConverterFactory() {
		// needed for test
		return converterFactory;

	}

	public DataRecord getDataRecord() {
		// needed for test
		return dataRecord;
	}

	public String getBaseUrl() {
		// needed for test
		return baseURL;
	}
}
