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

package se.uu.ub.cora.therest.data.converter.spider;

import se.uu.ub.cora.spider.data.SpiderDataRecord;
import se.uu.ub.cora.spider.data.SpiderRecordList;
import se.uu.ub.cora.therest.data.RestDataRecord;
import se.uu.ub.cora.therest.data.RestRecordList;

public final class RecordListSpiderToRestConverter {

	private SpiderRecordList spiderRecordList;
	private String baseURL;

	public static RecordListSpiderToRestConverter fromSpiderRecordListWithBaseURL(
			SpiderRecordList spiderRecordList, String baseURL) {
		return new RecordListSpiderToRestConverter(spiderRecordList, baseURL);
	}

	private RecordListSpiderToRestConverter(SpiderRecordList spiderRecordList, String baseURL) {
		this.spiderRecordList = spiderRecordList;
		this.baseURL = baseURL;
	}

	public RestRecordList toRest() {
		RestRecordList restRecordList = RestRecordList.withContainRecordsOfType(spiderRecordList
				.getContainRecordsOfType());

		restRecordList.setTotalNo(spiderRecordList.getToNo());
		restRecordList.setFromNo(spiderRecordList.getFromNo());
		restRecordList.setToNo(spiderRecordList.getToNo());

		for (SpiderDataRecord spiderDataRecord : spiderRecordList.getRecords()) {
			RestDataRecord restRecord = DataRecordSpiderToRestConverter
					.fromSpiderDataRecordWithBaseURL(spiderDataRecord, baseURL).toRest();
			restRecordList.addRecord(restRecord);
		}
		return restRecordList;
	}
}
