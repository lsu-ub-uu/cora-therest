/*
 * Copyright 2015, 2021 Uppsala University Library
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

import java.util.Map;
import java.util.Map.Entry;

import se.uu.ub.cora.data.DataAtomic;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataGroupProvider;
import se.uu.ub.cora.data.DataRecordLink;
import se.uu.ub.cora.data.DataResourceLink;
import se.uu.ub.cora.therest.data.RestDataAtomic;
import se.uu.ub.cora.therest.data.RestDataElement;
import se.uu.ub.cora.therest.data.RestDataGroup;
import se.uu.ub.cora.therest.data.RestDataRecordLink;
import se.uu.ub.cora.therest.data.RestDataResourceLink;
import se.uu.ub.cora.therest.data.converter.ConverterException;
import se.uu.ub.cora.therest.data.converter.RestToDataConverter;

public class DataGroupRestToDataConverter implements RestToDataConverter {
	protected RestDataGroup restDataGroup;
	protected DataGroup dataGroup;

	public static RestToDataConverter fromRestDataGroup(RestDataGroup restDataGroup) {
		return new DataGroupRestToDataConverter(restDataGroup);
	}

	protected DataGroupRestToDataConverter(RestDataGroup restDataGroup) {
		this.restDataGroup = restDataGroup;
	}

	@Override
	public DataGroup convert() {
		try {
			return tryToConvert();
		} catch (ClassCastException e) {
			throw new ConverterException("Data has misplaced data types, conversion not possible",
					e);
		}
	}

	private DataGroup tryToConvert() {
		createInstanceOfDataElement();
		dataGroup.setRepeatId(restDataGroup.getRepeatId());
		addAttributesToDataGroup();
		addChildrenToDataGroup();
		return dataGroup;
	}

	protected void createInstanceOfDataElement() {
		dataGroup = DataGroupProvider.getDataGroupUsingNameInData(restDataGroup.getNameInData());
	}

	private void addAttributesToDataGroup() {
		Map<String, String> attributes = restDataGroup.getAttributes();
		for (Entry<String, String> entry : attributes.entrySet()) {
			dataGroup.addAttributeByIdWithValue(entry.getKey(), entry.getValue());
		}
	}

	private void addChildrenToDataGroup() {
		for (RestDataElement restDataElement : restDataGroup.getChildren()) {
			addChildToDataGroup(restDataElement);
		}
	}

	private void addChildToDataGroup(RestDataElement restDataElement) {
		if (restDataElement instanceof RestDataGroup) {
			handleDataGroup(restDataElement);
		} else {
			addAtomicChild(restDataElement);
		}
	}

	private void handleDataGroup(RestDataElement restDataElement) {
		if (restDataElement instanceof RestDataRecordLink) {
			addRecordLinkChild(restDataElement);
		} else if (restDataElement instanceof RestDataResourceLink) {
			addResourceLinkChild(restDataElement);
		} else {
			addGroupChild(restDataElement);
		}

	}

	private void addRecordLinkChild(RestDataElement restDataElement) {
		DataRecordLink dataGroupChild = (DataRecordLink) DataRecordLinkRestToDataConverter
				.fromRestDataGroup((RestDataGroup) restDataElement).convert();
		dataGroup.addChild(dataGroupChild);
	}

	private void addResourceLinkChild(RestDataElement restDataElement) {
		DataResourceLink dataResourceLink = (DataResourceLink) DataResourceLinkRestToDataConverter
				.fromRestDataGroup((RestDataGroup) restDataElement).convert();
		dataGroup.addChild(dataResourceLink);
	}

	private void addGroupChild(RestDataElement restDataElement) {
		DataGroup dataGroupChild = DataGroupRestToDataConverter
				.fromRestDataGroup((RestDataGroup) restDataElement).convert();
		dataGroup.addChild(dataGroupChild);
	}

	private void addAtomicChild(RestDataElement restDataElement) {
		DataAtomic dataAtomic = DataAtomicRestToDataConverter
				.fromRestDataAtomic((RestDataAtomic) restDataElement).convert();
		dataGroup.addChild(dataAtomic);
	}

	public RestDataGroup getRestDataGroup() {
		// needed for test
		return restDataGroup;
	}

}
