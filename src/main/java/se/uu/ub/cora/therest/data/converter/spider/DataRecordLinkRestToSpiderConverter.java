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
import se.uu.ub.cora.spider.data.SpiderDataGroupRecordLink;
import se.uu.ub.cora.therest.data.RestDataRecordLink;

public final class DataRecordLinkRestToSpiderConverter {
	private RestDataRecordLink restDataRecordLink;

	public static DataRecordLinkRestToSpiderConverter fromRestDataRecordLink(
			RestDataRecordLink restDataRecordLink) {
		return new DataRecordLinkRestToSpiderConverter(restDataRecordLink);
	}

	private DataRecordLinkRestToSpiderConverter(RestDataRecordLink restDataRecordLink) {
		this.restDataRecordLink = restDataRecordLink;
	}

	public SpiderDataGroupRecordLink toSpider() {
		SpiderDataGroupRecordLink spiderDataRecordLink = SpiderDataGroupRecordLink.withNameInData(restDataRecordLink.getNameInData());
		SpiderDataAtomic linkedRecordType = SpiderDataAtomic.withNameInDataAndValue("linkedRecordType", restDataRecordLink.getLinkedRecordType());
		spiderDataRecordLink.addChild(linkedRecordType);

		SpiderDataAtomic linkedRecordId = SpiderDataAtomic.withNameInDataAndValue("linkedRecordId", restDataRecordLink.getLinkedRecordId());
		spiderDataRecordLink.addChild(linkedRecordId);
//				.withNameInDataAndLinkedRecordTypeAndLinkedRecordId(restDataRecordLink.getNameInData(),
//						restDataRecordLink.getLinkedRecordType(), restDataRecordLink.getLinkedRecordId());
		spiderDataRecordLink.setRepeatId(restDataRecordLink.getRepeatId());

		SpiderDataAtomic linkedRepeatId = SpiderDataAtomic.withNameInDataAndValue("linkedRepeatId", restDataRecordLink.getLinkedRepeatId());
		spiderDataRecordLink.addChild(linkedRepeatId);
//		spiderDataRecordLink.setLinkedRepeatId(restDataRecordLink.getLinkedRepeatId());

		addLinkedPathIfItExists(spiderDataRecordLink);
		return spiderDataRecordLink;
	}

	private void addLinkedPathIfItExists(SpiderDataGroupRecordLink spiderDataRecordLink) {
		if(restDataRecordLink.getLinkedPath() != null) {
			DataGroupRestToSpiderConverter linkedPathConverter =
					DataGroupRestToSpiderConverter.fromRestDataGroup(restDataRecordLink.getLinkedPath());

			spiderDataRecordLink.addChild(linkedPathConverter.toSpider());
		}
	}

}
