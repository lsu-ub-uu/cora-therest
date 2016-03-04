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

import se.uu.ub.cora.therest.data.RestDataAttribute;
import se.uu.ub.cora.therest.json.builder.JsonBuilderFactory;
import se.uu.ub.cora.therest.json.builder.JsonObjectBuilder;

public final class DataAttributeToJsonConverter implements DataToJsonConverter {
	private JsonBuilderFactory factory;
	private RestDataAttribute restDataAttribute;

	public static DataToJsonConverter usingJsonFactoryForRestDataAttribute(JsonBuilderFactory factory,
			RestDataAttribute dataAttribute) {
		return new DataAttributeToJsonConverter(factory, dataAttribute);
	}

	private DataAttributeToJsonConverter(JsonBuilderFactory factory,
			RestDataAttribute dataAttribute) {
		this.factory = factory;
		this.restDataAttribute = dataAttribute;
	}

	@Override
	public String toJson() {
		JsonObjectBuilder attribute = toJsonObjectBuilder();
		return attribute.toJsonFormattedString();
	}

	@Override
	public JsonObjectBuilder toJsonObjectBuilder() {
		JsonObjectBuilder jsonObjectBuilder = factory.createObjectBuilder();

		jsonObjectBuilder.addKeyString(restDataAttribute.getNameInData(),
				restDataAttribute.getValue());
		return jsonObjectBuilder;
	}

}
