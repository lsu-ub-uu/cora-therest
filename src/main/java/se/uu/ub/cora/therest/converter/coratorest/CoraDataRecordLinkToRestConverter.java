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

package se.uu.ub.cora.therest.converter.coratorest;

import java.util.Collection;
import java.util.Map;

import se.uu.ub.cora.data.DataAtomic;
import se.uu.ub.cora.data.DataAttribute;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataRecordLink;
import se.uu.ub.cora.therest.data.RestDataAtomic;
import se.uu.ub.cora.therest.data.RestDataGroup;
import se.uu.ub.cora.therest.data.RestDataRecordLink;

public final class CoraDataRecordLinkToRestConverter {
	private static final String LINKED_REPEAT_ID = "linkedRepeatId";
	private DataRecordLink dataRecordLink;
	private ConverterInfo converterInfo;
	private RestDataRecordLink restDataRecordLink;

	private CoraDataRecordLinkToRestConverter(DataRecordLink dataRecordLink,
			ConverterInfo converterInfo) {
		this.dataRecordLink = dataRecordLink;
		this.converterInfo = converterInfo;
	}

	public static CoraDataRecordLinkToRestConverter fromDataRecordLinkWithConverterInfo(
			DataRecordLink dataRecordLink, ConverterInfo converterInfo) {
		return new CoraDataRecordLinkToRestConverter(dataRecordLink, converterInfo);
	}

	public RestDataRecordLink toRest() {
		createDataRestRecordLinkSetNameInData();

		addLinkedRecordTypeToRestDataRecordLink();
		addLinkedRecordIdToRestDataRecordLink();

		restDataRecordLink.setRepeatId(dataRecordLink.getRepeatId());
		Collection<DataAttribute> attributes = dataRecordLink.getAttributes();
		Map<String, String> restAttributes = restDataRecordLink.getAttributes();
		for (DataAttribute dataAttribute : attributes) {
			restAttributes.put(dataAttribute.getNameInData(), dataAttribute.getValue());
		}

		addLinkedRepeatIdIfItExists();
		addLinkedPathIfItExists();
		createRestLinks();
		return restDataRecordLink;
	}

	private void createDataRestRecordLinkSetNameInData() {
		restDataRecordLink = RestDataRecordLink.withNameInData(dataRecordLink.getNameInData());
	}

	private void addLinkedRecordTypeToRestDataRecordLink() {
		createAndAddRestAtomicChildWithNameInData("linkedRecordType");
	}

	private void createAndAddRestAtomicChildWithNameInData(String nameInData) {
		RestDataAtomic restLinkedRecordId = createRestDataAtomicFromDataAtomic(nameInData);
		restDataRecordLink.addChild(restLinkedRecordId);
	}

	private RestDataAtomic createRestDataAtomicFromDataAtomic(String nameInData) {
		DataAtomic linkedRecordId = getAtomicChildFromDataRecordLinkByNameInData(nameInData);
		return RestDataAtomic.withNameInDataAndValue(nameInData, linkedRecordId.getValue());
	}

	private DataAtomic getAtomicChildFromDataRecordLinkByNameInData(String nameInData) {
		return (DataAtomic) dataRecordLink.getFirstChildWithNameInData(nameInData);
	}

	private void addLinkedRecordIdToRestDataRecordLink() {
		createAndAddRestAtomicChildWithNameInData("linkedRecordId");
	}

	private void addLinkedRepeatIdIfItExists() {
		if (dataRecordLink.containsChildWithNameInData(LINKED_REPEAT_ID)) {

			RestDataAtomic restLinkedRepeatId = createRestDataAtomicFromDataAtomic(
					LINKED_REPEAT_ID);

			restDataRecordLink.addChild(restLinkedRepeatId);
		}
	}

	private void addLinkedPathIfItExists() {
		if (dataRecordLink.containsChildWithNameInData("linkedPath")) {
			DataGroup linkedPath = (DataGroup) dataRecordLink
					.getFirstChildWithNameInData("linkedPath");
			// uses data group converter
			CoraDataGroupToRestConverter dataGroupToRestConverter = CoraDataGroupToRestConverter
					.fromDataGroupWithDataGroupAndConverterInfo(linkedPath, converterInfo);
			RestDataGroup restLinkedPath = dataGroupToRestConverter.toRest();

			restDataRecordLink.addChild(restLinkedPath);
		}
	}

	private void createRestLinks() {
		ConverterInfo linkConverterInfo = createConverterInfoForLink();
		CoraActionToRestConverter actionToRestConverter = CoraActionToRestConverterImp
				.fromDataActionsWithConverterInfo(dataRecordLink.getActions(), linkConverterInfo);
		restDataRecordLink.setActionLinks(actionToRestConverter.toRest());
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
