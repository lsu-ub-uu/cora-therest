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
import se.uu.ub.cora.spider.data.SpiderDataRecordLink;
import se.uu.ub.cora.spider.data.SpiderDataResourceLink;
import se.uu.ub.cora.therest.data.RestDataElement;
import se.uu.ub.cora.therest.data.RestDataGroup;
import se.uu.ub.cora.therest.data.converter.ConverterInfo;

public class DataGroupSpiderToRestConverter implements SpiderToRestConverter {

	private RestDataGroup restDataGroup;
	protected SpiderDataGroup spiderDataGroup;
	protected ConverterInfo convertInfo;

	protected DataGroupSpiderToRestConverter(SpiderDataGroup spiderDataGroup,
			ConverterInfo converterInfo) {
		this.spiderDataGroup = spiderDataGroup;
		this.convertInfo = converterInfo;
	}

	public static DataGroupSpiderToRestConverter fromSpiderDataGroupWithBaseURL(
			SpiderDataGroup spiderDataGroup, ConverterInfo baseURL) {
		return new DataGroupSpiderToRestConverter(spiderDataGroup, baseURL);
	}

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
		for (SpiderDataElement spiderDataElement : spiderDataGroup.getChildren()) {
			RestDataElement convertedChild = convertToElementEquivalentDataClass(spiderDataElement);
			restDataGroup.getChildren().add(convertedChild);
		}
	}

	private RestDataElement convertToElementEquivalentDataClass(
			SpiderDataElement spiderDataElement) {
		if (spiderDataElement instanceof SpiderDataRecordLink) {
			return DataRecordLinkSpiderToRestConverter.fromSpiderDataRecordLinkWithBaseURL(
					(SpiderDataRecordLink) spiderDataElement, convertInfo).toRest();
		}
		if (spiderDataElement instanceof SpiderDataResourceLink) {
			return DataResourceLinkSpiderToRestConverter.fromSpiderDataResourceLinkWithBaseURL(
					(SpiderDataResourceLink) spiderDataElement, convertInfo).toRest();
		}
		if (spiderDataElement instanceof SpiderDataGroup) {
			return DataGroupSpiderToRestConverter.fromSpiderDataGroupWithBaseURL(
					(SpiderDataGroup) spiderDataElement, convertInfo).toRest();
		}
		return DataAtomicSpiderToRestConverter
				.fromSpiderDataAtomic((SpiderDataAtomic) spiderDataElement).toRest();
	}
}
