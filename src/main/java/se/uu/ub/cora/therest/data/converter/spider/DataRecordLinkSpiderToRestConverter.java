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

import se.uu.ub.cora.spider.data.SpiderDataGroup;
import se.uu.ub.cora.spider.data.SpiderDataRecordLink;
import se.uu.ub.cora.therest.data.RestDataGroup;
import se.uu.ub.cora.therest.data.RestDataRecordLink;

public final class DataRecordLinkSpiderToRestConverter {
	private SpiderDataRecordLink spiderDataRecordLink;
	private String baseURL;
	private RestDataRecordLink restDataRecordLink;

	public static DataRecordLinkSpiderToRestConverter fromSpiderDataRecordLinkWithBaseURL(
			SpiderDataRecordLink spiderDataRecordLink, String baseURL) {
		return new DataRecordLinkSpiderToRestConverter(spiderDataRecordLink, baseURL);
	}

	private DataRecordLinkSpiderToRestConverter(SpiderDataRecordLink spiderDataRecordLink,
			String baseURL) {
		this.spiderDataRecordLink = spiderDataRecordLink;
		this.baseURL = baseURL;
	}

	public RestDataRecordLink toRest() {
		restDataRecordLink = RestDataRecordLink.withNameInDataAndLinkedRecordTypeAndLinkedRecordId(
				spiderDataRecordLink.getNameInData(), spiderDataRecordLink.getLinkedRecordType(),
				spiderDataRecordLink.getLinkedRecordId());
		restDataRecordLink.setRepeatId(spiderDataRecordLink.getRepeatId());
		restDataRecordLink.setLinkedRepeatId(spiderDataRecordLink.getLinkedRepeatId());

		addLinkedPathIfItExists();
		createRestLinks(restDataRecordLink.getLinkedRecordType(), restDataRecordLink.getLinkedRecordId());
		return restDataRecordLink;
	}

	private void addLinkedPathIfItExists() {
		if(spiderDataRecordLink.getLinkedPath() != null) {
			SpiderDataGroup spiderLinkedPath = spiderDataRecordLink.getLinkedPath();
			DataGroupSpiderToRestConverter dataGroupSpiderToRestConverter =
					DataGroupSpiderToRestConverter.fromSpiderDataGroupWithBaseURL(spiderLinkedPath, baseURL);
			RestDataGroup restLinkedPath = dataGroupSpiderToRestConverter.toRest();

			restDataRecordLink.setLinkedPath(restLinkedPath);
		}
	}

	private void createRestLinks(String recordType, String recordId) {
		ActionSpiderToRestConverter actionSpiderToRestConverter = ActionSpiderToRestConverter
				.fromSpiderActionsWithBaseURLAndRecordTypeAndRecordId(
						spiderDataRecordLink.getActions(), baseURL, recordType, recordId);
		restDataRecordLink.setActionLinks(actionSpiderToRestConverter.toRest());
	}
}
