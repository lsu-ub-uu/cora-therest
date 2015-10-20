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

import se.uu.ub.cora.spider.data.SpiderDataAtomic;
import se.uu.ub.cora.spider.data.SpiderDataElement;
import se.uu.ub.cora.spider.data.SpiderDataGroup;
import se.uu.ub.cora.spider.data.SpiderDataRecord;
import se.uu.ub.cora.therest.data.RestDataGroup;
import se.uu.ub.cora.therest.data.RestDataRecord;
import se.uu.ub.cora.therest.data.converter.ConverterException;

public final class DataRecordSpiderToRestConverter {

	private SpiderDataRecord spiderDataRecord;
	private String baseURL;
	private SpiderDataGroup spiderDataGroup;
	private RestDataRecord restDataRecord;

	public static DataRecordSpiderToRestConverter fromSpiderDataRecordWithBaseURL(
			SpiderDataRecord spiderDataRecord, String baseURL) {
		return new DataRecordSpiderToRestConverter(spiderDataRecord, baseURL);
	}

	private DataRecordSpiderToRestConverter(SpiderDataRecord spiderDataRecord, String baseURL) {
		this.spiderDataRecord = spiderDataRecord;
		this.baseURL = baseURL;
	}

	public RestDataRecord toRest() {

		spiderDataGroup = spiderDataRecord.getSpiderDataGroup();
		DataGroupSpiderToRestConverter dataGroupSpiderToRestConverter = DataGroupSpiderToRestConverter
				.fromSpiderDataGroupWithBaseURL(spiderDataGroup, baseURL);
		RestDataGroup restDataGroup = dataGroupSpiderToRestConverter.toRest();
		restDataRecord = RestDataRecord.withRestDataGroup(restDataGroup);

		if (hasActions()) {
			convertActionsToLinks();
		}
		if (!spiderDataRecord.getKeys().isEmpty()) {
			for (String string : spiderDataRecord.getKeys()) {
				restDataRecord.addKey(string);
			}
		}
		return restDataRecord;
	}

	private boolean hasActions() {
		return !spiderDataRecord.getActions().isEmpty();
	}

	private void convertActionsToLinks() {
		SpiderDataGroup recordInfo = findRecordInfo();
		String id = findId(recordInfo);
		String type = findType(recordInfo);
		createRestLinks(type, id);
	}

	private SpiderDataGroup findRecordInfo() {
		SpiderDataGroup recordInfo = null;
		for (SpiderDataElement spiderDataElement : spiderDataGroup.getChildren()) {
			if ("recordInfo".equals(spiderDataElement.getNameInData())) {
				recordInfo = (SpiderDataGroup) spiderDataElement;
				break;
			}
		}
		if (null == recordInfo) {
			throw new ConverterException("No recordInfo found convertion not possible");
		}
		return recordInfo;
	}

	private String findId(SpiderDataGroup recordInfo) {
		String id = "";
		for (SpiderDataElement spiderDataElement : recordInfo.getChildren()) {
			if ("id".equals(spiderDataElement.getNameInData())) {
				id = ((SpiderDataAtomic) spiderDataElement).getValue();
			}
		}
		if ("".equals(id)) {
			throw new ConverterException("No id was found in recordInfo convertion not possible");
		}
		return id;
	}

	private String findType(SpiderDataGroup recordInfo) {
		String type = "";
		for (SpiderDataElement spiderDataElement : recordInfo.getChildren()) {
			if ("type".equals(spiderDataElement.getNameInData())) {
				type = ((SpiderDataAtomic) spiderDataElement).getValue();
			}
		}
		if ("".equals(type)) {
			throw new ConverterException("No type was found in recordInfo convertion not possible");
		}
		return type;
	}

	private void createRestLinks(String recordType, String recordId) {
		ActionSpiderToRestConverter actionSpiderToRestConverter = ActionSpiderToRestConverter
				.fromSpiderActionsWithBaseURLAndRecordTypeAndRecordId(spiderDataRecord.getActions(),
						baseURL, recordType, recordId);
		restDataRecord.setActionLinks(actionSpiderToRestConverter.toRest());

	}
}
