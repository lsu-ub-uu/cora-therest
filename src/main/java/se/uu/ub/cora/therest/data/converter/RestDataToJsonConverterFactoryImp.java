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

package se.uu.ub.cora.therest.data.converter;

import se.uu.ub.cora.json.builder.JsonBuilderFactory;
import se.uu.ub.cora.json.builder.org.OrgJsonBuilderFactoryAdapter;
import se.uu.ub.cora.therest.data.RestData;
import se.uu.ub.cora.therest.data.RestDataAtomic;
import se.uu.ub.cora.therest.data.RestDataAttribute;
import se.uu.ub.cora.therest.data.RestDataElement;
import se.uu.ub.cora.therest.data.RestDataGroup;
import se.uu.ub.cora.therest.data.RestDataList;
import se.uu.ub.cora.therest.data.RestDataRecordLink;
import se.uu.ub.cora.therest.data.RestDataResourceLink;

public class RestDataToJsonConverterFactoryImp implements RestDataToJsonConverterFactory {

	@Override
	public RestDataToJsonConverter createForRestDataElement(JsonBuilderFactory factory,
			RestDataElement restDataElement) {

		if (restDataElement instanceof RestDataGroup) {
			if (restDataElement instanceof RestDataRecordLink) {
				return DataRecordLinkToJsonConverter.usingJsonFactoryForRestDataLink(factory,
						(RestDataRecordLink) restDataElement);
			}
			if (restDataElement instanceof RestDataResourceLink) {
				return DataResourceLinkToJsonConverter.usingJsonFactoryForRestDataLink(factory,
						(RestDataResourceLink) restDataElement);
			}
			return DataGroupToJsonConverter.usingJsonFactoryForRestDataGroup(factory,
					(RestDataGroup) restDataElement);
		}
		if (restDataElement instanceof RestDataAtomic) {
			return DataAtomicToJsonConverter.usingJsonFactoryForRestDataAtomic(factory,
					(RestDataAtomic) restDataElement);
		}
		return DataAttributeToJsonConverter.usingJsonFactoryForRestDataAttribute(factory,
				(RestDataAttribute) restDataElement);
	}

	@Override
	public RestDataToJsonConverter createForRestData(RestData restData) {
		OrgJsonBuilderFactoryAdapter orgJsonBuilderFactoryAdapter = new OrgJsonBuilderFactoryAdapter();
		if (restData instanceof RestDataList) {
			return DataListToJsonConverter
					.usingJsonFactoryForRestDataList(orgJsonBuilderFactoryAdapter, restData);
		}
		return RestRecordToJsonConverter
				.usingJsonFactoryForRestDataRecord(orgJsonBuilderFactoryAdapter, restData);
	}
}
