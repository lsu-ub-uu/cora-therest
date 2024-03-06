/*
 * Copyright 2019 Uppsala University Library
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
package se.uu.ub.cora.therest.coradata;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import se.uu.ub.cora.data.DataAtomic;
import se.uu.ub.cora.data.DataAttribute;
import se.uu.ub.cora.data.DataChild;
import se.uu.ub.cora.data.DataChildFilter;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.spider.data.DataMissingException;
import se.uu.ub.cora.testutils.mcr.MethodCallRecorder;

public class DataGroupSpy implements DataGroup {
	public MethodCallRecorder MCR = new MethodCallRecorder();

	public String nameInData;
	public List<DataChild> children = new ArrayList<>();
	public String repeatId;
	public Set<DataAttribute> attributes = new HashSet<>();

	public DataGroupSpy(String nameInData) {
		this.nameInData = nameInData;
	}

	@Override
	public String getRepeatId() {
		MCR.addCall();
		MCR.addReturned(repeatId);
		return repeatId;
	}

	@Override
	public String getNameInData() {
		MCR.addCall();
		MCR.addReturned(nameInData);
		return nameInData;
	}

	@Override
	public String getFirstAtomicValueWithNameInData(String nameInData) {
		MCR.addCall("nameInData", nameInData);
		for (DataChild dataElement : children) {
			if (nameInData.equals(dataElement.getNameInData())) {
				if (dataElement instanceof DataAtomic) {
					String out = ((DataAtomic) dataElement).getValue();
					MCR.addReturned(out);
					return out;
				}
			}
		}
		throw new DataMissingException("Atomic value not found for childNameInData:" + nameInData);
	}

	@Override
	public DataGroup getFirstGroupWithNameInData(String childNameInData) {
		MCR.addCall("childNameInData", childNameInData);
		if (children.size() == 0) {
			DataGroup out = new DataGroupSpy(childNameInData);
			MCR.addReturned(out);
			return out;
		}
		for (DataChild dataElement : children) {
			if (childNameInData.equals(dataElement.getNameInData())) {
				if (dataElement instanceof DataGroup) {
					DataGroup dataGroup = (DataGroup) dataElement;
					MCR.addReturned(dataGroup);
					return dataGroup;
				}
			}
		}
		throw new DataMissingException("Group not found for childNameInData:" + childNameInData);
	}

	@Override
	public void addChild(DataChild dataElement) {
		MCR.addCall("dataElement", dataElement);
		children.add(dataElement);
	}

	@Override
	public List<DataChild> getChildren() {
		MCR.addCall();
		MCR.addReturned(children);
		return children;
	}

	@Override
	public boolean containsChildWithNameInData(String nameInData) {
		MCR.addCall("nameInData", nameInData);
		for (DataChild dataElement : children) {
			if (nameInData.equals(dataElement.getNameInData())) {
				MCR.addReturned(true);
				return true;
			}
		}
		MCR.addReturned(false);
		return false;
	}

	@Override
	public void setRepeatId(String repeatId) {
		MCR.addCall("repeatId", repeatId);
		this.repeatId = repeatId;
	}

	@Override
	public void addAttributeByIdWithValue(String id, String value) {
	}

	@Override
	public DataChild getFirstChildWithNameInData(String nameInData) {
		MCR.addCall("nameInData", nameInData);
		for (DataChild dataElement : children) {
			if (nameInData.equals(dataElement.getNameInData())) {
				MCR.addReturned(dataElement);
				return dataElement;
			}
		}
		MCR.addReturned(null);
		return null;
	}

	@Override
	public List<DataGroup> getAllGroupsWithNameInData(String nameInData) {
		MCR.addCall("nameInData", nameInData);
		List<DataGroup> matchingDataGroups = new ArrayList<>();
		for (DataChild dataElement : children) {
			if (nameInData.equals(dataElement.getNameInData())
					&& dataElement instanceof DataGroup) {
				matchingDataGroups.add((DataGroup) dataElement);
			}
		}
		MCR.addReturned(matchingDataGroups);
		return matchingDataGroups;
	}

	@Override
	public DataAttribute getAttribute(String attributeId) {
		MCR.addCall("attributeId", attributeId);
		for (DataAttribute dataAttribute : attributes) {
			if (dataAttribute.getNameInData().equals(attributeId)) {
				MCR.addReturned(dataAttribute);
				return dataAttribute;
			}
		}
		MCR.addReturned(null);
		return null;
	}

	@Override
	public Collection<DataAttribute> getAttributes() {
		MCR.addCall();
		return attributes;
	}

	@Override
	public List<DataAtomic> getAllDataAtomicsWithNameInData(String childNameInData) {
		MCR.addCall("childNameInData", childNameInData);
		List<DataAtomic> matchingDataAtomics = new ArrayList<>();
		for (DataChild dataElement : children) {
			if (childNameInData.equals(dataElement.getNameInData())
					&& dataElement instanceof DataAtomic) {
				matchingDataAtomics.add((DataAtomic) dataElement);
			}
		}
		MCR.addReturned(matchingDataAtomics);
		return matchingDataAtomics;
	}

	@Override
	public boolean removeFirstChildWithNameInData(String childNameInData) {
		MCR.addCall("childNameInData", childNameInData);
		boolean removed = false;
		for (DataChild dataElement : getChildren()) {
			if (dataElementsNameInDataIs(dataElement, childNameInData)) {
				getChildren().remove(dataElement);
				removed = true;
			}
		}
		MCR.addReturned(removed);
		return removed;
	}

	private boolean dataElementsNameInDataIs(DataChild dataElement, String childNameInData) {
		MCR.addCall("dataElement", dataElement, "childNameInData", childNameInData);
		boolean out = dataElement.getNameInData().equals(childNameInData);
		MCR.addReturned(out);
		return out;
	}

	@Override
	public boolean hasChildren() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void addChildren(Collection<DataChild> dataElements) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<DataChild> getAllChildrenWithNameInData(String nameInData) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean removeAllChildrenWithNameInData(String childNameInData) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public DataAtomic getFirstDataAtomicWithNameInData(String childNameInData) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<DataGroup> getAllGroupsWithNameInDataAndAttributes(String childNameInData,
			DataAttribute... childAttributes) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean removeAllChildrenWithNameInDataAndAttributes(String childNameInData,
			DataAttribute... childAttributes) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<DataChild> getAllChildrenWithNameInDataAndAttributes(String nameInData,
			DataAttribute... childAttributes) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasAttributes() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Collection<DataAtomic> getAllDataAtomicsWithNameInDataAndAttributes(
			String childNameInData, DataAttribute... childAttributes) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<DataChild> getAllChildrenMatchingFilter(DataChildFilter childFilter) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean removeAllChildrenMatchingFilter(DataChildFilter childFilter) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public <T> boolean containsChildOfTypeAndName(Class<T> type, String name) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public <T extends DataChild> T getFirstChildOfTypeAndName(Class<T> type, String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends DataChild> List<T> getChildrenOfTypeAndName(Class<T> type, String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends DataChild> boolean removeFirstChildWithTypeAndName(Class<T> type,
			String name) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public <T extends DataChild> boolean removeChildrenWithTypeAndName(Class<T> type, String name) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Optional<String> getAttributeValue(String nameInData) {
		// TODO Auto-generated method stub
		return Optional.empty();
	}

	@Override
	public boolean hasRepeatId() {
		// TODO Auto-generated method stub
		return false;
	}

}
