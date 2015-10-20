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

package se.uu.ub.cora.therest.json.builder.org;

import se.uu.ub.cora.therest.json.builder.JsonArrayBuilder;
import se.uu.ub.cora.therest.json.builder.JsonObjectBuilder;
import se.uu.ub.cora.therest.json.parser.JsonArray;
import se.uu.ub.cora.therest.json.parser.org.OrgJsonArrayAdapter;

public class OrgJsonArrayBuilderAdapter implements JsonArrayBuilder {

	private org.json.JSONArray orgJsonArray = new org.json.JSONArray();

	@Override
	public void addString(String value) {
		orgJsonArray.put(value);
	}

	@Override
	public void addJsonObjectBuilder(JsonObjectBuilder jsonObjectBuilder) {
		OrgJsonObjectBuilderAdapter objectBuilderAdapter = (OrgJsonObjectBuilderAdapter) jsonObjectBuilder;
		orgJsonArray.put(objectBuilderAdapter.getWrappedBuilder());
	}

	@Override
	public void addJsonArrayBuilder(JsonArrayBuilder jsonArrayBuilder) {
		OrgJsonArrayBuilderAdapter arrayBuilderAdapter = (OrgJsonArrayBuilderAdapter) jsonArrayBuilder;
		orgJsonArray.put(arrayBuilderAdapter.getWrappedBuilder());
	}

	org.json.JSONArray getWrappedBuilder() {
		return orgJsonArray;
	}

	@Override
	public JsonArray toJsonArray() {
		return OrgJsonArrayAdapter.usingOrgJsonArray(orgJsonArray);
	}

	@Override
	public String toJsonFormattedString() {
		return orgJsonArray.toString();
	}

}
