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

import se.uu.ub.cora.data.DataAtomic;
import se.uu.ub.cora.data.DataElement;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataRecordLink;
import se.uu.ub.cora.data.DataResourceLink;
import se.uu.ub.cora.therest.data.RestDataElement;
import se.uu.ub.cora.therest.data.RestDataGroup;
import se.uu.ub.cora.therest.data.converter.ConverterInfo;

public class DataGroupDataToRestConverter implements DataToRestConverter {

	private RestDataGroup restDataGroup;
	protected DataGroup spiderDataGroup;
	protected ConverterInfo convertInfo;

	protected DataGroupDataToRestConverter(DataGroup spiderDataGroup,
			ConverterInfo converterInfo) {
		this.spiderDataGroup = spiderDataGroup;
		this.convertInfo = converterInfo;
	}

	public static DataGroupDataToRestConverter fromDataGroupWithDataGroupAndConverterInfo(
			DataGroup spiderDataGroup, ConverterInfo converterInfo) {
		return new DataGroupDataToRestConverter(spiderDataGroup, converterInfo);
	}

	@Override
	public RestDataGroup toRest() {
		restDataGroup = createNewRest();
		restDataGroup.getAttributes().putAll(spiderDataGroup.getAttributes());
		restDataGroup.setRepeatId(spiderDataGroup.getRepeatId());
		convertAndSetChildren();
		return restDataGroup;
	}

	protected RestDataGroup createNewRest() {
		return RestDataGroup.withNameInData(spiderDataGroup.getNameInData());
	}

	private void convertAndSetChildren() {
		for (DataElement spiderDataElement : spiderDataGroup.getChildren()) {
			RestDataElement convertedChild = convertToElementEquivalentDataClass(spiderDataElement);
			restDataGroup.getChildren().add(convertedChild);
		}
	}

	private RestDataElement convertToElementEquivalentDataClass(DataElement spiderDataElement) {
		if (spiderDataElement instanceof DataRecordLink) {
			return DataRecordLinkToRestConverter.fromDataRecordLinkWithConverterInfo(
					(DataRecordLink) spiderDataElement, convertInfo).toRest();
		}
		if (spiderDataElement instanceof DataResourceLink) {
			return DataResourceLinkDataToRestConverter
					.fromDataResourceLinkWithConverterInfo(
							(DataResourceLink) spiderDataElement, convertInfo)
					.toRest();
		}
		if (spiderDataElement instanceof DataGroup) {
			return DataGroupDataToRestConverter.fromDataGroupWithDataGroupAndConverterInfo(
					(DataGroup) spiderDataElement, convertInfo).toRest();
		}
		return DataAtomicToRestConverter.fromSpiderDataAtomic((DataAtomic) spiderDataElement)
				.toRest();
	}
}
