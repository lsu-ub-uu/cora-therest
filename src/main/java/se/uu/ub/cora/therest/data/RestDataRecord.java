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

package se.uu.ub.cora.therest.data;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public final class RestDataRecord implements RestData {
	private RestDataGroup restDataGroup;
	private Set<String> keys = new LinkedHashSet<>();
	private Set<String> readPermissions = new LinkedHashSet<>();
	private Set<String> writePermissions = new LinkedHashSet<>();
	private Map<String, ActionLink> actionLinks = new LinkedHashMap<>();

	public static RestDataRecord withRestDataGroup(RestDataGroup restDataGroup) {
		return new RestDataRecord(restDataGroup);
	}

	private RestDataRecord(RestDataGroup restDataGroup) {
		this.restDataGroup = restDataGroup;
	}

	public RestDataGroup getRestDataGroup() {
		return restDataGroup;
	}

	public void addKey(String key) {
		keys.add(key);
	}

	public Set<String> getKeys() {
		return keys;
	}

	public void addActionLink(String key, ActionLink actionLink) {
		actionLinks.put(key, actionLink);
	}

	public Map<String, ActionLink> getActionLinks() {
		return actionLinks;
	}

	public ActionLink getActionLink(String key) {
		return actionLinks.get(key);
	}

	public void setActionLinks(Map<String, ActionLink> actionLinks) {
		this.actionLinks = actionLinks;
	}

	public void addReadPermission(String readPermission) {
		readPermissions.add(readPermission);
	}

	public Set<String> getReadPermissions() {
		return readPermissions;
	}

	public void addWritePermission(String writePermission) {
		writePermissions.add(writePermission);

	}

	public Set<String> getWritePermissions() {
		return writePermissions;
	}

}
