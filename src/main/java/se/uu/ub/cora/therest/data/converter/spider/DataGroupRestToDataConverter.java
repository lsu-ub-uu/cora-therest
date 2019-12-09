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

import java.util.Map;
import java.util.Map.Entry;

import se.uu.ub.cora.data.DataAtomic;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataGroupProvider;
import se.uu.ub.cora.therest.data.RestDataAtomic;
import se.uu.ub.cora.therest.data.RestDataElement;
import se.uu.ub.cora.therest.data.RestDataGroup;
import se.uu.ub.cora.therest.data.converter.ConverterException;

public final class DataGroupRestToDataConverter {
	private RestDataGroup restDataGroup;
	private DataGroup dataGroup;

	public static DataGroupRestToDataConverter fromRestDataGroup(RestDataGroup restDataGroup) {
		return new DataGroupRestToDataConverter(restDataGroup);
	}

	private DataGroupRestToDataConverter(RestDataGroup restDataGroup) {
		this.restDataGroup = restDataGroup;
	}

	public DataGroup convert() {
		try {
			return tryToConvert();
		} catch (ClassCastException e) {
			throw new ConverterException("Data has misplaced data types, conversion not possible",
					e);
		}
	}

	private DataGroup tryToConvert() {
		dataGroup = DataGroupProvider.getDataGroupUsingNameInData(restDataGroup.getNameInData());
		dataGroup.setRepeatId(restDataGroup.getRepeatId());
		addAttributesToDataGroup();
		addChildrenToDataGroup();
		return dataGroup;
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
			addGroupChild(restDataElement);
		} else {
			addAtomicChild(restDataElement);
		}
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

}
