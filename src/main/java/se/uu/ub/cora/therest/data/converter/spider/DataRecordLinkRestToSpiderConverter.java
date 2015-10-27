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

import se.uu.ub.cora.spider.data.SpiderDataRecordLink;
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

	public SpiderDataRecordLink toSpider() {
		SpiderDataRecordLink spiderDataRecordLink = SpiderDataRecordLink
				.withNameInDataAndRecordTypeAndRecordId(restDataRecordLink.getNameInData(),
						restDataRecordLink.getRecordType(), restDataRecordLink.getRecordId());
		spiderDataRecordLink.setRepeatId(restDataRecordLink.getRepeatId());
		spiderDataRecordLink.setLinkedRepeatId(restDataRecordLink.getLinkedRepeatId());

		addLinkedPathIfItExists(spiderDataRecordLink);
		return spiderDataRecordLink;
	}

	private void addLinkedPathIfItExists(SpiderDataRecordLink spiderDataRecordLink) {
		if(restDataRecordLink.getLinkedPath() != null) {
			DataGroupRestToSpiderConverter linkedPathConverter =
					DataGroupRestToSpiderConverter.fromRestDataGroup(restDataRecordLink.getLinkedPath());

			spiderDataRecordLink.setLinkedPath(linkedPathConverter.toSpider());
		}
	}

}
