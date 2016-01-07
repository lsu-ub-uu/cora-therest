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
import se.uu.ub.cora.spider.data.SpiderDataRecordLink;
import se.uu.ub.cora.therest.data.RestDataAtomic;
import se.uu.ub.cora.therest.data.RestDataGroup;
import se.uu.ub.cora.therest.data.RestDataGroupRecordLink;

public final class DataRecordLinkRestToSpiderConverter {
	private static final String LINKED_REPEAT_ID = "linkedRepeatId";
	private RestDataGroupRecordLink restDataRecordLink;

	public static DataRecordLinkRestToSpiderConverter fromRestDataRecordLink(
			RestDataGroupRecordLink restDataRecordLink) {
		return new DataRecordLinkRestToSpiderConverter(restDataRecordLink);
	}

	private DataRecordLinkRestToSpiderConverter(RestDataGroupRecordLink restDataRecordLink) {
		this.restDataRecordLink = restDataRecordLink;
	}

	public SpiderDataRecordLink toSpider(){
		SpiderDataRecordLink spiderDataRecordLink = SpiderDataRecordLink.withNameInData(restDataRecordLink.getNameInData());
		RestDataAtomic restLinkedRecordType = (RestDataAtomic) restDataRecordLink.getFirstChildWithNameInData("linkedRecordType");

		SpiderDataAtomic linkedRecordType = SpiderDataAtomic
				.withNameInDataAndValue("linkedRecordType", restLinkedRecordType.getValue());
		spiderDataRecordLink.addChild(linkedRecordType);

		RestDataAtomic restLinkedRecordId = (RestDataAtomic) restDataRecordLink.getFirstChildWithNameInData("linkedRecordId");
		SpiderDataAtomic linkedRecordId = SpiderDataAtomic.
				withNameInDataAndValue("linkedRecordId", restLinkedRecordId.getValue());
		spiderDataRecordLink.addChild(linkedRecordId);

		spiderDataRecordLink.setRepeatId(restDataRecordLink.getRepeatId());

		if(restDataRecordLink.containsChildWithNameInData(LINKED_REPEAT_ID)) {
			RestDataAtomic restLinkedRepeatId = (RestDataAtomic) restDataRecordLink.getFirstChildWithNameInData(LINKED_REPEAT_ID);

			SpiderDataAtomic linkedRepeatId = SpiderDataAtomic.withNameInDataAndValue(LINKED_REPEAT_ID, restLinkedRepeatId.getValue());
			spiderDataRecordLink.addChild(linkedRepeatId);
		}
		addLinkedPathIfItExists(spiderDataRecordLink);
		return spiderDataRecordLink;
	}

	private void addLinkedPathIfItExists(SpiderDataRecordLink spiderDataRecordLink) {
		if(restDataRecordLink.containsChildWithNameInData("linkedPath")) {
			RestDataGroup linkedPath = (RestDataGroup) restDataRecordLink.getFirstChildWithNameInData("linkedPath");
			DataGroupRestToSpiderConverter linkedPathConverter =
					DataGroupRestToSpiderConverter.fromRestDataGroup(linkedPath);

			spiderDataRecordLink.addChild(linkedPathConverter.toSpider());
		}
	}

}
