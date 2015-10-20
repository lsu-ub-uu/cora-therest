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

package se.uu.ub.cora.therest.json.parser.org;

import org.json.JSONArray;
import org.json.JSONObject;
import se.uu.ub.cora.therest.json.parser.JsonValue;

public final class OrgJsonValueFactory {
	private OrgJsonValueFactory() {
		// not in use
		throw new UnsupportedOperationException();
	}

	public static JsonValue createFromOrgJsonObject(Object orgJsonObject) {
		if (orgJsonObject instanceof org.json.JSONObject) {
			return OrgJsonObjectAdapter.usingOrgJsonObject((JSONObject) orgJsonObject);
		}
		if (orgJsonObject instanceof org.json.JSONArray) {
			return OrgJsonArrayAdapter.usingOrgJsonArray((JSONArray) orgJsonObject);
		}
		return new OrgJsonStringAdapter((String) orgJsonObject);
	}

}
