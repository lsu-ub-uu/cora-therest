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

package se.uu.ub.cora.therest.data.converter.coradata;

import java.util.Collection;
import java.util.Map;

import se.uu.ub.cora.data.DataAtomic;
import se.uu.ub.cora.data.DataAttribute;
import se.uu.ub.cora.data.DataElement;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataRecordLink;
import se.uu.ub.cora.data.DataResourceLink;
import se.uu.ub.cora.therest.data.RestDataElement;
import se.uu.ub.cora.therest.data.RestDataGroup;
import se.uu.ub.cora.therest.data.converter.ConverterInfo;

public class DataGroupDataToRestConverter implements DataToRestConverter {

	private RestDataGroup restDataGroup;
	protected DataGroup dataGroup;
	protected ConverterInfo convertInfo;
	private DataGroupToRestConverterFactory dataGroupToRestConverterFactory;

	protected DataGroupDataToRestConverter(
			DataGroupToRestConverterFactory dataGroupToRestConverterFactory, DataGroup dataGroup,
			ConverterInfo converterInfo) {
		this.dataGroupToRestConverterFactory = dataGroupToRestConverterFactory;
		this.dataGroup = dataGroup;
		this.convertInfo = converterInfo;
	}

	public static DataGroupDataToRestConverter fromDataGroupWithDataGroupAndConverterInfo(
			DataGroupToRestConverterFactory dataGroupToRestConverterFactory, DataGroup dataGroup,
			ConverterInfo converterInfo) {
		return new DataGroupDataToRestConverter(dataGroupToRestConverterFactory, dataGroup,
				converterInfo);
	}

	@Override
	public RestDataGroup toRest() {
		restDataGroup = createNewRest();
		Collection<DataAttribute> attributes = dataGroup.getAttributes();
		Map<String, String> restAttributes = restDataGroup.getAttributes();
		for (DataAttribute dataAttribute : attributes) {
			restAttributes.put(dataAttribute.getNameInData(), dataAttribute.getValue());
		}
		restDataGroup.setRepeatId(dataGroup.getRepeatId());
		convertAndSetChildren();
		return restDataGroup;
	}

	protected RestDataGroup createNewRest() {
		return RestDataGroup.withNameInData(dataGroup.getNameInData());
	}

	private void convertAndSetChildren() {
		for (DataElement dataElement : dataGroup.getChildren()) {
			RestDataElement convertedChild = convertToElementEquivalentDataClass(dataElement);
			restDataGroup.getChildren().add(convertedChild);
		}
	}

	private RestDataElement convertToElementEquivalentDataClass(DataElement dataElement) {
		if (dataElement instanceof DataRecordLink) {
			return DataRecordLinkToRestConverter
					.fromDataRecordLinkWithConverterInfo(dataGroupToRestConverterFactory,
							(DataRecordLink) dataElement, convertInfo)
					.toRest();
		}
		if (dataElement instanceof DataResourceLink) {
			return DataResourceLinkDataToRestConverter
					.fromDataResourceLinkWithConverterInfo(dataGroupToRestConverterFactory,
							(DataResourceLink) dataElement, convertInfo)
					.toRest();
		}
		if (dataElement instanceof DataGroup) {
			return DataGroupDataToRestConverter.fromDataGroupWithDataGroupAndConverterInfo(
					dataGroupToRestConverterFactory, (DataGroup) dataElement, convertInfo).toRest();
		}
		return DataAtomicToRestConverter.fromDataAtomic((DataAtomic) dataElement).toRest();
	}
}
