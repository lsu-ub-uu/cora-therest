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

import se.uu.ub.cora.spider.data.SpiderDataAtomic;
import se.uu.ub.cora.spider.data.SpiderDataGroup;
import se.uu.ub.cora.spider.data.SpiderDataRecordLink;
import se.uu.ub.cora.therest.data.RestDataAtomic;
import se.uu.ub.cora.therest.data.RestDataElement;
import se.uu.ub.cora.therest.data.RestDataGroup;
import se.uu.ub.cora.therest.data.RestDataRecordLink;
import se.uu.ub.cora.therest.data.converter.ConverterException;

public final class DataGroupRestToSpiderConverter {
	private RestDataGroup restDataGroup;
	private SpiderDataGroup spiderDataGroup;

	public static DataGroupRestToSpiderConverter fromRestDataGroup(RestDataGroup restDataGroup) {
		return new DataGroupRestToSpiderConverter(restDataGroup);
	}

	private DataGroupRestToSpiderConverter(RestDataGroup restDataGroup) {
		this.restDataGroup = restDataGroup;
	}

	public SpiderDataGroup toSpider() {
		try {
			return tryToSpiderate();
		} catch (ClassCastException e) {
			throw new ConverterException("Data has misplaced data types, conversion not possible",
					e);
		}
	}

	private SpiderDataGroup tryToSpiderate() {
		spiderDataGroup = SpiderDataGroup.withNameInData(restDataGroup.getNameInData());
		spiderDataGroup.setRepeatId(restDataGroup.getRepeatId());
		addAttributesToSpiderGroup();
		addChildrenToSpiderGroup();
		return spiderDataGroup;
	}

	private void addAttributesToSpiderGroup() {
		Map<String, String> attributes = restDataGroup.getAttributes();
		for (Entry<String, String> entry : attributes.entrySet()) {
			spiderDataGroup.addAttributeByIdWithValue(entry.getKey(), entry.getValue());
		}
	}

	private void addChildrenToSpiderGroup() {
		for (RestDataElement restDataElement : restDataGroup.getChildren()) {
			addChildToSpiderGroup(restDataElement);
		}
	}

	private void addChildToSpiderGroup(RestDataElement restDataElement) {
		if (restDataElement instanceof RestDataGroup) {
			addGroupChild(restDataElement);
		} else if (restDataElement instanceof RestDataRecordLink) {
			addLinkChild(restDataElement);
		} else {
			addAtomicChild(restDataElement);
		}
	}

	private void addGroupChild(RestDataElement restDataElement) {
		SpiderDataGroup spiderDataGroupChild = DataGroupRestToSpiderConverter
				.fromRestDataGroup((RestDataGroup) restDataElement).toSpider();
		spiderDataGroup.addChild(spiderDataGroupChild);
	}

	private void addLinkChild(RestDataElement restDataElement) {
		SpiderDataRecordLink spiderDataRecordLink = DataRecordLinkRestToSpiderConverter
				.fromRestDataRecordLink((RestDataRecordLink) restDataElement).toSpider();
		spiderDataGroup.addChild(spiderDataRecordLink);
	}

	private void addAtomicChild(RestDataElement restDataElement) {
		SpiderDataAtomic spiderDataAtomic = DataAtomicRestToSpiderConverter
				.fromRestDataAtomic((RestDataAtomic) restDataElement).toSpider();
		spiderDataGroup.addChild(spiderDataAtomic);
	}

}
