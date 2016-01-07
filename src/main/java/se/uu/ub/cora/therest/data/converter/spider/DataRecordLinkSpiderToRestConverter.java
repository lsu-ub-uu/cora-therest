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
import se.uu.ub.cora.spider.data.SpiderDataGroup;
import se.uu.ub.cora.spider.data.SpiderDataRecordLink;
import se.uu.ub.cora.therest.data.RestDataAtomic;
import se.uu.ub.cora.therest.data.RestDataGroup;
import se.uu.ub.cora.therest.data.RestDataGroupRecordLink;

public final class DataRecordLinkSpiderToRestConverter {
	private static final String LINKED_REPEAT_ID = "linkedRepeatId";
	private SpiderDataRecordLink spiderDataRecordLink;
	private String baseURL;
	private RestDataGroupRecordLink restDataRecordLink;

	public static DataRecordLinkSpiderToRestConverter fromSpiderDataRecordLinkWithBaseURL(
			SpiderDataRecordLink spiderDataRecordLink, String baseURL) {
		return new DataRecordLinkSpiderToRestConverter(spiderDataRecordLink, baseURL);
	}

	private DataRecordLinkSpiderToRestConverter(SpiderDataRecordLink spiderDataRecordLink,
			String baseURL) {
		this.spiderDataRecordLink = spiderDataRecordLink;
		this.baseURL = baseURL;
	}

	public RestDataGroupRecordLink toRest() {

		SpiderDataAtomic linkedRecordType = (SpiderDataAtomic) spiderDataRecordLink.getFirstChildWithNameInData("linkedRecordType");
		SpiderDataAtomic linkedRecordId = (SpiderDataAtomic) spiderDataRecordLink.getFirstChildWithNameInData("linkedRecordId");

		restDataRecordLink = RestDataGroupRecordLink.withNameInData(spiderDataRecordLink.getNameInData());
		RestDataAtomic restLinkedRecordType = RestDataAtomic.withNameInDataAndValue("linkedRecordType", linkedRecordType.getValue());
		restDataRecordLink.addChild(restLinkedRecordType);

		RestDataAtomic restLinkedRecordId = RestDataAtomic.withNameInDataAndValue("linkedRecordId", linkedRecordId.getValue());
		restDataRecordLink.addChild(restLinkedRecordId);

		restDataRecordLink.setRepeatId(spiderDataRecordLink.getRepeatId());
		addLinkedRepeatIdIfItExists();
		addLinkedPathIfItExists();
		createRestLinks(restLinkedRecordType.getValue(), restLinkedRecordId.getValue());
		return restDataRecordLink;
	}

	private void addLinkedRepeatIdIfItExists() {
		if(spiderDataRecordLink.containsChildWithNameInData(LINKED_REPEAT_ID)) {
			SpiderDataAtomic linkedRepeatId = (SpiderDataAtomic) spiderDataRecordLink.getFirstChildWithNameInData(LINKED_REPEAT_ID);

			RestDataAtomic restLinkedRepeatId = RestDataAtomic.withNameInDataAndValue(LINKED_REPEAT_ID, linkedRepeatId.getValue());

			restDataRecordLink.addChild(restLinkedRepeatId);
		}
	}

	private void addLinkedPathIfItExists() {
		if(spiderDataRecordLink.containsChildWithNameInData("linkedPath")) {
			SpiderDataGroup spiderLinkedPath = (SpiderDataGroup)spiderDataRecordLink.getFirstChildWithNameInData("linkedPath");
			DataGroupSpiderToRestConverter dataGroupSpiderToRestConverter =
					DataGroupSpiderToRestConverter.fromSpiderDataGroupWithBaseURL(spiderLinkedPath, baseURL);
			RestDataGroup restLinkedPath = dataGroupSpiderToRestConverter.toRest();

			restDataRecordLink.addChild(restLinkedPath);
		}
	}

	private void createRestLinks(String recordType, String recordId) {
		ActionSpiderToRestConverter actionSpiderToRestConverter = ActionSpiderToRestConverter
				.fromSpiderActionsWithBaseURLAndRecordTypeAndRecordId(
						spiderDataRecordLink.getActions(), baseURL, recordType, recordId);
		restDataRecordLink.setActionLinks(actionSpiderToRestConverter.toRest());
	}
}
