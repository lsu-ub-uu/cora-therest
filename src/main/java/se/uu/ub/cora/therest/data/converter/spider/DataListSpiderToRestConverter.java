/*
 * Copyright 2015, 2019 Uppsala University Library
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

package se.uu.ub.cora.therest.data.converter.spider;

import se.uu.ub.cora.data.Data;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataList;
import se.uu.ub.cora.data.DataRecord;
import se.uu.ub.cora.therest.data.RestDataGroup;
import se.uu.ub.cora.therest.data.RestDataList;
import se.uu.ub.cora.therest.data.RestDataRecord;
import se.uu.ub.cora.therest.data.converter.ConverterInfo;

public final class DataListSpiderToRestConverter {

	private DataList spiderDataList;
	private String baseURL;

	public static DataListSpiderToRestConverter fromSpiderDataListWithBaseURL(
			DataList spiderDataList, String url) {
		return new DataListSpiderToRestConverter(spiderDataList, url);
	}

	private DataListSpiderToRestConverter(DataList spiderDataList, String url) {
		this.spiderDataList = spiderDataList;
		this.baseURL = url;
	}

	public RestDataList toRest() {
		RestDataList restDataList = RestDataList
				.withContainDataOfType(spiderDataList.getContainDataOfType());

		restDataList.setTotalNo(spiderDataList.getTotalNumberOfTypeInStorage());
		restDataList.setFromNo(spiderDataList.getFromNo());
		restDataList.setToNo(spiderDataList.getToNo());

		for (Data spiderData : spiderDataList.getDataList()) {
			convertSpiderDataToRest(restDataList, spiderData);
		}
		return restDataList;
	}

	private void convertSpiderDataToRest(RestDataList restDataList, Data spiderData) {
		if (spiderData instanceof DataRecord) {
			convertSpiderRecordToRest(restDataList, (DataRecord) spiderData);
		} else {
			convertSpiderGroupToRest(restDataList, (DataGroup) spiderData);
		}
	}

	private void convertSpiderRecordToRest(RestDataList restDataList, DataRecord spiderData) {
		SpiderToRestConverterFactory converterFactory = new SpiderToRestConverterFactoryImp();
		RestDataRecord restRecord = DataRecordSpiderToRestConverter
				.fromSpiderDataRecordWithBaseURLAndConverterFactory(spiderData, baseURL,
						converterFactory)
				.toRest();
		restDataList.addData(restRecord);
	}

	private void convertSpiderGroupToRest(RestDataList restDataList, DataGroup spiderData) {
		ConverterInfo converterInfo = ConverterInfo.withBaseURLAndRecordURLAndTypeAndId(baseURL, "",
				"", "");
		RestDataGroup restGroup = DataGroupSpiderToRestConverter
				.fromDataGroupWithDataGroupAndConverterInfo(spiderData, converterInfo).toRest();
		restDataList.addData(restGroup);
	}
}
