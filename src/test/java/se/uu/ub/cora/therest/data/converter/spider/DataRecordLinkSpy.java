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
package se.uu.ub.cora.therest.data.converter.spider;

import java.util.List;

import se.uu.ub.cora.data.Action;
import se.uu.ub.cora.data.DataRecordLink;

public class DataRecordLinkSpy extends DataGroupSpy implements DataRecordLink {

	public String nameInData;

	public DataRecordLinkSpy(String nameInData) {
		super(nameInData);
	}

	@Override
	public void addAction(Action action) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<Action> getActions() {
		// TODO Auto-generated method stub
		return null;
	}

	// @Override
	// public void addAction(Action action) {
	// // TODO Auto-generated method stub
	//
	// }
	//
	// @Override
	// public List<Action> getActions() {
	// // TODO Auto-generated method stub
	// return null;
	// }
	//
	// @Override
	// public String getNameInData() {
	// // TODO Auto-generated method stub
	// return null;
	// }
	//
	// @Override
	// public String getRepeatId() {
	// // TODO Auto-generated method stub
	// return null;
	// }
	//
	// @Override
	// public String getFirstAtomicValueWithNameInData(String nameInData) {
	// // TODO Auto-generated method stub
	// return null;
	// }
	//
	// @Override
	// public DataGroup getFirstGroupWithNameInData(String childNameInData) {
	// // TODO Auto-generated method stub
	// return null;
	// }
	//
	// @Override
	// public void addChild(DataElement dataElement) {
	// // TODO Auto-generated method stub
	//
	// }
	//
	// @Override
	// public List<DataElement> getChildren() {
	// // TODO Auto-generated method stub
	// return null;
	// }
	//
	// @Override
	// public boolean containsChildWithNameInData(String nameInData) {
	// // TODO Auto-generated method stub
	// return false;
	// }
	//
	// @Override
	// public void setRepeatId(String repeatId) {
	// // TODO Auto-generated method stub
	//
	// }
	//
	// @Override
	// public void addAttributeByIdWithValue(String id, String value) {
	// // TODO Auto-generated method stub
	//
	// }
	//
	// @Override
	// public DataElement getFirstChildWithNameInData(String nameInData) {
	// // TODO Auto-generated method stub
	// return null;
	// }
	//
	// @Override
	// public List<DataGroup> getAllGroupsWithNameInData(String nameInData) {
	// // TODO Auto-generated method stub
	// return null;
	// }
	//
	// @Override
	// public String getAttribute(String attributeId) {
	// // TODO Auto-generated method stub
	// return null;
	// }
	//
	// @Override
	// public List<DataAtomic> getAllDataAtomicsWithNameInData(String childNameInData) {
	// // TODO Auto-generated method stub
	// return null;
	// }
	//
	// @Override
	// public void removeFirstChildWithNameInData(String childNameInData) {
	// // TODO Auto-generated method stub
	//
	// }
	//
	// @Override
	// public Collection<DataGroup> getAllGroupsWithNameInDataAndAttributes(String childNameInData,
	// DataAttribute... childAttributes) {
	// // TODO Auto-generated method stub
	// return null;
	// }
	//
	// @Override
	// public Map<String, String> getAttributes() {
	// return null;
	// }

}
