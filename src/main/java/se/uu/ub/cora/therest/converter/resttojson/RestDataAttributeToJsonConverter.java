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

package se.uu.ub.cora.therest.converter.resttojson;

import se.uu.ub.cora.json.builder.JsonBuilderFactory;
import se.uu.ub.cora.json.builder.JsonObjectBuilder;
import se.uu.ub.cora.therest.data.RestDataAttribute;

public final class RestDataAttributeToJsonConverter implements RestToJsonConverter {
	private JsonBuilderFactory factory;
	private RestDataAttribute restDataAttribute;

	public static RestToJsonConverter usingJsonFactoryForRestDataAttribute(
			JsonBuilderFactory factory, RestDataAttribute dataAttribute) {
		return new RestDataAttributeToJsonConverter(factory, dataAttribute);
	}

	private RestDataAttributeToJsonConverter(JsonBuilderFactory factory,
			RestDataAttribute dataAttribute) {
		this.factory = factory;
		this.restDataAttribute = dataAttribute;
	}

	@Override
	public JsonObjectBuilder toJsonObjectBuilder() {
		JsonObjectBuilder jsonObjectBuilder = factory.createObjectBuilder();

		jsonObjectBuilder.addKeyString(restDataAttribute.getNameInData(),
				restDataAttribute.getValue());
		return jsonObjectBuilder;
	}

}
