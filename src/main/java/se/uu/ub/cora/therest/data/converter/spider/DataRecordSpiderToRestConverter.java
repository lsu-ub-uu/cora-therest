/*
 * Copyright 2015, 2016, 2019 Uppsala University Library
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

import se.uu.ub.cora.spider.data.SpiderDataGroup;
import se.uu.ub.cora.spider.data.SpiderDataRecord;
import se.uu.ub.cora.therest.data.RestDataGroup;
import se.uu.ub.cora.therest.data.RestDataRecord;
import se.uu.ub.cora.therest.data.converter.ConverterException;
import se.uu.ub.cora.therest.data.converter.ConverterInfo;

public final class DataRecordSpiderToRestConverter {

	private SpiderDataRecord spiderDataRecord;
	private String baseURL;
	private SpiderDataGroup spiderDataGroup;
	private RestDataRecord restDataRecord;
	private String recordId;
	private String recordType;
	private ConverterInfo converterInfo;
	private SpiderToRestConverterFactory converterFactory;

	public static DataRecordSpiderToRestConverter fromSpiderDataRecordWithBaseURLAndConverterFactory(
			SpiderDataRecord spiderDataRecord, String url,
			SpiderToRestConverterFactory converterFactory) {
		return new DataRecordSpiderToRestConverter(spiderDataRecord, url, converterFactory);
	}

	private DataRecordSpiderToRestConverter(SpiderDataRecord spiderDataRecord, String url,
			SpiderToRestConverterFactory converterFactory) {
		this.spiderDataRecord = spiderDataRecord;
		this.baseURL = url;
		this.converterFactory = converterFactory;
	}

	public RestDataRecord toRest() {
		try {
			return convertToRest();
		} catch (Exception e) {
			throw new ConverterException("No recordInfo found conversion not possible: " + e);
		}
	}

	private RestDataRecord convertToRest() {
		spiderDataGroup = spiderDataRecord.getSpiderDataGroup();
		extractIdAndType();
		createConverterInfo();

		convertToRestRecord();

		if (hasActions()) {
			createRestLinks();
		}
		if (hasKeys()) {
			convertKeys();
		}
		return restDataRecord;
	}

	private void extractIdAndType() {
		SpiderDataGroup recordInfo = spiderDataGroup.extractGroup("recordInfo");
		recordId = recordInfo.extractAtomicValue("id");
		SpiderDataGroup typeGroup = recordInfo.extractGroup("type");
		recordType = typeGroup.extractAtomicValue("linkedRecordId");
	}

	private void createConverterInfo() {
		String recordURL = baseURL + String.join("/", recordType, recordId);
		converterInfo = ConverterInfo.withBaseURLAndRecordURLAndTypeAndId(baseURL, recordURL,
				recordType, recordId);
	}

	private void convertToRestRecord() {
		SpiderToRestConverter dataGroupSpiderToRestConverter = converterFactory
				.factorForSpiderDataGroupWithConverterInfo(spiderDataGroup, converterInfo);
		RestDataGroup restDataGroup = dataGroupSpiderToRestConverter.toRest();
		restDataRecord = RestDataRecord.withRestDataGroup(restDataGroup);
	}

	private boolean hasActions() {
		return !spiderDataRecord.getActions().isEmpty();
	}

	private void createRestLinks() {
		ActionSpiderToRestConverter actionSpiderToRestConverter = converterFactory
				.factorForActionsUsingConverterInfoAndDataGroup(spiderDataRecord.getActions(),
						converterInfo, spiderDataGroup);

		// ActionSpiderToRestConverter actionSpiderToRestConverter;
		// if ("recordType".equals(recordType)) {
		// actionSpiderToRestConverter = createConverterForLinksForRecordType();
		// } else {
		// actionSpiderToRestConverter = createConverterForLinks();
		// }
		restDataRecord.setActionLinks(actionSpiderToRestConverter.toRest());
	}

	// private ActionSpiderToRestConverter createConverterForLinksForRecordType() {
	// return ActionSpiderToRestConverterImp.fromSpiderActionsWithConverterInfoAndDataGroup(
	// spiderDataRecord.getActions(), converterInfo, spiderDataGroup);
	// }
	//
	// private ActionSpiderToRestConverter createConverterForLinks() {
	// return ActionSpiderToRestConverterImp
	// .fromSpiderActionsWithConverterInfo(spiderDataRecord.getActions(), converterInfo);
	// }

	private boolean hasKeys() {
		return !spiderDataRecord.getKeys().isEmpty();
	}

	private void convertKeys() {
		for (String string : spiderDataRecord.getKeys()) {
			restDataRecord.addKey(string);
		}
	}
}
