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

import java.util.Map.Entry;

import se.uu.ub.cora.therest.data.RestDataElement;
import se.uu.ub.cora.therest.data.RestDataGroup;
import se.uu.ub.cora.therest.json.builder.JsonArrayBuilder;
import se.uu.ub.cora.therest.json.builder.JsonBuilderFactory;
import se.uu.ub.cora.therest.json.builder.JsonObjectBuilder;

public final class DataGroupToJsonConverter extends DataToJsonConverter {

	private RestDataGroup restDataGroup;
	private JsonObjectBuilder dataGroupJsonObjectBuilder;
	private JsonBuilderFactory jsonBuilderFactory;

	public static DataToJsonConverter usingJsonFactoryForRestDataGroup(
			se.uu.ub.cora.therest.json.builder.JsonBuilderFactory factory,
			RestDataGroup restDataGroup) {
		return new DataGroupToJsonConverter(factory, restDataGroup);
	}

	private DataGroupToJsonConverter(JsonBuilderFactory factory, RestDataGroup restDataGroup) {
		this.jsonBuilderFactory = factory;
		this.restDataGroup = restDataGroup;
		dataGroupJsonObjectBuilder = factory.createObjectBuilder();
	}

	@Override
	public String toJson() {
		return toJsonObjectBuilder().toJsonFormattedString();
	}

	@Override
	JsonObjectBuilder toJsonObjectBuilder() {
		possiblyAddRepeatId();
		if (hasAttributes()) {
			addAttributesToGroup();
		}
		if (hasChildren()) {
			addChildrenToGroup();
		}
		dataGroupJsonObjectBuilder.addKeyString("name", restDataGroup.getNameInData());
		return dataGroupJsonObjectBuilder;
	}

	private void possiblyAddRepeatId() {
		if (hasNonEmptyRepeatId()) {
			dataGroupJsonObjectBuilder.addKeyString("repeatId", restDataGroup.getRepeatId());
		}
	}

	private boolean hasNonEmptyRepeatId() {
		return restDataGroup.getRepeatId() != null && !restDataGroup.getRepeatId().equals("");
	}

	private boolean hasAttributes() {
		return !restDataGroup.getAttributes().isEmpty();
	}

	private void addAttributesToGroup() {
		JsonObjectBuilder attributes = jsonBuilderFactory.createObjectBuilder();
		for (Entry<String, String> attributeEntry : restDataGroup.getAttributes().entrySet()) {
			attributes.addKeyString(attributeEntry.getKey(), attributeEntry.getValue());
		}
		dataGroupJsonObjectBuilder.addKeyJsonObjectBuilder("attributes", attributes);
	}

	private boolean hasChildren() {
		return !restDataGroup.getChildren().isEmpty();
	}

	private void addChildrenToGroup() {
		DataToJsonConverterFactory dataToJsonConverterFactory = new DataToJsonConverterFactoryImp();
		JsonArrayBuilder childrenArray = jsonBuilderFactory.createArrayBuilder();
		for (RestDataElement restDataElement : restDataGroup.getChildren()) {
			childrenArray.addJsonObjectBuilder(dataToJsonConverterFactory
					.createForRestDataElement(jsonBuilderFactory, restDataElement)
					.toJsonObjectBuilder());
		}
		dataGroupJsonObjectBuilder.addKeyJsonArrayBuilder("children", childrenArray);
	}
}
