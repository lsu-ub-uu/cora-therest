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
import se.uu.ub.cora.json.builder.JsonObjectBuilder;
import se.uu.ub.cora.therest.data.RestDataAtomic;

public final class DataAtomicToJsonConverter extends RestDataToJsonConverter {

	private RestDataAtomic restDataAtomic;
	private JsonBuilderFactory factory;

	public static RestDataToJsonConverter usingJsonFactoryForRestDataAtomic(JsonBuilderFactory factory,
			RestDataAtomic dataAtomic) {
		return new DataAtomicToJsonConverter(factory, dataAtomic);
	}

	private DataAtomicToJsonConverter(JsonBuilderFactory factory, RestDataAtomic dataAtomic) {
		this.factory = factory;
		this.restDataAtomic = dataAtomic;
	}

	@Override
	public JsonObjectBuilder toJsonObjectBuilder() {
		JsonObjectBuilder jsonObjectBuilder = factory.createObjectBuilder();

		jsonObjectBuilder.addKeyString("name", restDataAtomic.getNameInData());
		jsonObjectBuilder.addKeyString("value", restDataAtomic.getValue());
		possiblyAddRepeatId(jsonObjectBuilder);
		return jsonObjectBuilder;
	}

	private void possiblyAddRepeatId(JsonObjectBuilder jsonObjectBuilder) {
		if (hasNonEmptyRepeatId()) {
			jsonObjectBuilder.addKeyString("repeatId", restDataAtomic.getRepeatId());
		}
	}

	private boolean hasNonEmptyRepeatId() {
		return restDataAtomic.getRepeatId() != null && !"".equals(restDataAtomic.getRepeatId());
	}
}
