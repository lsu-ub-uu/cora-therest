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

import se.uu.ub.cora.spider.data.SpiderDataAtomic;
import se.uu.ub.cora.spider.data.SpiderDataGroup;
import se.uu.ub.cora.spider.data.SpiderDataRecordLink;
import se.uu.ub.cora.therest.data.RestDataAtomic;
import se.uu.ub.cora.therest.data.RestDataGroup;
import se.uu.ub.cora.therest.data.RestDataRecordLink;
import se.uu.ub.cora.therest.data.converter.ConverterInfo;

public final class DataRecordLinkSpiderToRestConverter {
	private static final String LINKED_REPEAT_ID = "linkedRepeatId";
	private SpiderDataRecordLink spiderDataRecordLink;
	private ConverterInfo converterInfo;
	private RestDataRecordLink restDataRecordLink;

	private DataRecordLinkSpiderToRestConverter(SpiderDataRecordLink spiderDataRecordLink,
			ConverterInfo converterInfo) {
		this.spiderDataRecordLink = spiderDataRecordLink;
		this.converterInfo = converterInfo;
	}

	public static DataRecordLinkSpiderToRestConverter fromSpiderDataRecordLinkWithConverterInfo(
			SpiderDataRecordLink spiderDataRecordLink, ConverterInfo converterInfo) {
		return new DataRecordLinkSpiderToRestConverter(spiderDataRecordLink, converterInfo);
	}

	public RestDataRecordLink toRest() {
		createDataRestRecordLinkSetNameInData();

		addLinkedRecordTypeToRestDataRecordLink();
		addLinkedRecordIdToRestDataRecordLink();

		restDataRecordLink.setRepeatId(spiderDataRecordLink.getRepeatId());
		restDataRecordLink.getAttributes().putAll(spiderDataRecordLink.getAttributes());

		addLinkedRepeatIdIfItExists();
		addLinkedPathIfItExists();
		createRestLinks();
		return restDataRecordLink;
	}

	private void createDataRestRecordLinkSetNameInData() {
		restDataRecordLink = RestDataRecordLink
				.withNameInData(spiderDataRecordLink.getNameInData());
	}

	private void addLinkedRecordTypeToRestDataRecordLink() {
		createAndAddRestAtomicChildWithNameInData("linkedRecordType");
	}

	private void createAndAddRestAtomicChildWithNameInData(String nameInData) {
		RestDataAtomic restLinkedRecordId = createRestDataAtomicFromSpiderDataAtomic(nameInData);
		restDataRecordLink.addChild(restLinkedRecordId);
	}

	private RestDataAtomic createRestDataAtomicFromSpiderDataAtomic(String nameInData) {
		SpiderDataAtomic linkedRecordId = getAtomicChildFromSpiderDataRecordLinkByNameInData(
				nameInData);
		return RestDataAtomic.withNameInDataAndValue(nameInData, linkedRecordId.getValue());
	}

	private SpiderDataAtomic getAtomicChildFromSpiderDataRecordLinkByNameInData(String nameInData) {
		return (SpiderDataAtomic) spiderDataRecordLink.getFirstChildWithNameInData(nameInData);
	}

	private void addLinkedRecordIdToRestDataRecordLink() {
		createAndAddRestAtomicChildWithNameInData("linkedRecordId");
	}

	private void addLinkedRepeatIdIfItExists() {
		if (spiderDataRecordLink.containsChildWithNameInData(LINKED_REPEAT_ID)) {

			RestDataAtomic restLinkedRepeatId = createRestDataAtomicFromSpiderDataAtomic(
					LINKED_REPEAT_ID);

			restDataRecordLink.addChild(restLinkedRepeatId);
		}
	}

	private void addLinkedPathIfItExists() {
		if (spiderDataRecordLink.containsChildWithNameInData("linkedPath")) {
			SpiderDataGroup spiderLinkedPath = (SpiderDataGroup) spiderDataRecordLink
					.getFirstChildWithNameInData("linkedPath");
			DataGroupSpiderToRestConverter dataGroupSpiderToRestConverter = DataGroupSpiderToRestConverter
					.fromSpiderDataGroupWithDataGroupAndConverterInfo(spiderLinkedPath,
							converterInfo);
			RestDataGroup restLinkedPath = dataGroupSpiderToRestConverter.toRest();

			restDataRecordLink.addChild(restLinkedPath);
		}
	}

	private void createRestLinks() {
		ConverterInfo linkConverterInfo = createConverterInfoForLink();
		ActionSpiderToRestConverter actionSpiderToRestConverter = ActionSpiderToRestConverterImp
				.fromSpiderActionsWithConverterInfo(spiderDataRecordLink.getActions(),
						linkConverterInfo);
		restDataRecordLink.setActionLinks(actionSpiderToRestConverter.toRest());
	}

	private ConverterInfo createConverterInfoForLink() {
		String linkedRecordType = getAtomicValueFromRestLinkUsingNameInData("linkedRecordType");
		String linkedRecordId = getAtomicValueFromRestLinkUsingNameInData("linkedRecordId");
		return ConverterInfo.withBaseURLAndRecordURLAndTypeAndId(converterInfo.baseURL,
				converterInfo.recordURL, linkedRecordType, linkedRecordId);
	}

	private String getAtomicValueFromRestLinkUsingNameInData(String nameInData) {
		RestDataAtomic linkedRecordType = (RestDataAtomic) restDataRecordLink
				.getFirstChildWithNameInData(nameInData);
		return linkedRecordType.getValue();
	}
}
