/*
 * Copyright 2015, 2016 Uppsala University Library
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
import se.uu.ub.cora.spider.data.SpiderDataResourceLink;
import se.uu.ub.cora.therest.data.RestDataAtomic;
import se.uu.ub.cora.therest.data.RestDataResourceLink;

public final class DataResourceLinkSpiderToRestConverter {
	private static final String LINKED_REPEAT_ID = "linkedRepeatId";
	private SpiderDataResourceLink spiderDataResourceLink;
	private String baseURL;
	private RestDataResourceLink restDataResourceLink;

	public static DataResourceLinkSpiderToRestConverter fromSpiderDataRecordLinkWithBaseURL(
			SpiderDataResourceLink spiderDataResourceLink, String baseURL) {
		return new DataResourceLinkSpiderToRestConverter(spiderDataResourceLink, baseURL);
	}

	private DataResourceLinkSpiderToRestConverter(SpiderDataResourceLink spiderDataRecordLink,
			String baseURL) {
		this.spiderDataResourceLink = spiderDataRecordLink;
		this.baseURL = baseURL;
	}

	public RestDataResourceLink toRest() {
		SpiderDataAtomic streamId = (SpiderDataAtomic) spiderDataResourceLink
				.getFirstChildWithNameInData("streamId");

		restDataResourceLink = RestDataResourceLink
				.withNameInData(spiderDataResourceLink.getNameInData());

		RestDataAtomic restStreamId = RestDataAtomic.withNameInDataAndValue("streamId",
				streamId.getValue());
		restDataResourceLink.addChild(restStreamId);

		restDataResourceLink.setRepeatId(spiderDataResourceLink.getRepeatId());
		createRestLinks("someRecordType", restStreamId.getValue());
		return restDataResourceLink;
	}

	private void createRestLinks(String recordType, String recordId) {
		ActionSpiderToRestConverter actionSpiderToRestConverter = ActionSpiderToRestConverter
				.fromSpiderActionsWithBaseURLAndRecordTypeAndRecordId(
						spiderDataResourceLink.getActions(), baseURL, recordType, recordId);
		restDataResourceLink.setActionLinks(actionSpiderToRestConverter.toRest());
	}
}
