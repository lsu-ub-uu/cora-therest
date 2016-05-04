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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import se.uu.ub.cora.spider.data.DataMissingException;

public class RestDataGroup implements RestDataElement, RestData {

	private final String nameInData;
	private Map<String, String> attributes = new HashMap<>();
	private List<RestDataElement> children = new ArrayList<>();
	private String repeatId;

	public static RestDataGroup withNameInData(String nameInData) {
		return new RestDataGroup(nameInData);
	}

	protected RestDataGroup(String nameInData) {
		this.nameInData = nameInData;
	}

	@Override
	public String getNameInData() {
		return nameInData;
	}

	public Map<String, String> getAttributes() {
		return attributes;
	}

	public void addAttributeByIdWithValue(String nameInData, String value) {
		attributes.put(nameInData, value);
	}

	public void addChild(RestDataElement restDataElement) {
		children.add(restDataElement);
	}

	public List<RestDataElement> getChildren() {
		return children;
	}

	public void setRepeatId(String repeatId) {
		this.repeatId = repeatId;
	}

	public String getRepeatId() {
		return repeatId;
	}

	public boolean containsChildWithNameInData(String nameInData) {
		for (RestDataElement restDataElement : getChildren()) {
			if (restDataElement.getNameInData().equals(nameInData)) {
				return true;
			}
		}
		return false;
	}

	public RestDataElement getFirstChildWithNameInData(String nameInData) {
		for (RestDataElement restDataElement : getChildren()) {
			if (restDataElement.getNameInData().equals(nameInData)) {
				return restDataElement;
			}
		}
		throw new DataMissingException("Requested child " + nameInData + " does not exist");
	}

	public void removeFirstChildWithNameInData(String childNameInData) {
		boolean childRemoved = tryToRemoveChild(childNameInData);
		if (!childRemoved) {
			throw new DataMissingException(
					"Element not found for childNameInData:" + childNameInData);
		}
	}

	private boolean tryToRemoveChild(String childNameInData) {
		for (RestDataElement dataElement : getChildren()) {
			if (dataElement.getNameInData().equals(childNameInData)) {
				getChildren().remove(dataElement);
				return true;
			}
		}
		return false;
	}
}
