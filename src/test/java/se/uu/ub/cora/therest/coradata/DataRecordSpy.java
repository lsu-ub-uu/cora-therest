/**Copyright 2019 Uppsala University Library**This file is part of Cora.**Cora is free/software:you can redistribute it and/or modify*it under the terms of the GNU General Public/License as published by*the Free Software Foundation,either version 3 of the License,or*(at your/option)any later version.**Cora is distributed in the hope that it will be useful,*but WITHOUT/ANY WARRANTY;without even the implied warranty of*MERCHANTABILITY or FITNESS FOR A PARTICULAR/PURPOSE.See the*GNU General Public License for more details.**You should have received a copy of/the GNU General Public License*along with Cora.If not,see<http://www.gnu.org/licenses/>.
*/
package se.uu.ub.cora.therest.coradata;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import se.uu.ub.cora.data.Action;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataRecord;
import se.uu.ub.cora.data.DataRecordGroup;

public class DataRecordSpy implements DataRecord {

	public DataGroup dataGroup;
	public Set<String> keys = new LinkedHashSet<>();
	public Set<String> readPermissions = new LinkedHashSet<>();
	public Set<String> writePermissions = new LinkedHashSet<>();
	public List<Action> actions = new ArrayList<>();

	public DataRecordSpy(DataGroup dataGroup) {
		this.dataGroup = dataGroup;
	}

	@Override
	public List<Action> getActions() {
		return actions;
	}

	@Override
	public void addAction(Action action) {
		actions.add(action);
	}

	@Override
	public void addReadPermission(String readPermission) {
		readPermissions.add(readPermission);
	}

	@Override
	public Set<String> getReadPermissions() {
		return readPermissions;
	}

	@Override
	public void addWritePermission(String writePermission) {
		writePermissions.add(writePermission);
	}

	@Override
	public Set<String> getWritePermissions() {
		return writePermissions;
	}

	@Override
	public void addReadPermissions(Collection<String> readPermissions) {
		// TODO Auto-generated method stub
	}

	@Override
	public void addWritePermissions(Collection<String> writePermissions) {
		// TODO Auto-generated method stub
	}

	@Override
	public String getType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getId() {
		return "idFromDataRecordSpy";
	}

	@Override
	public boolean hasActions() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasReadPermissions() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasWritePermissions() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getSearchId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addProtocol(String protocol) {
		// TODO Auto-generated method stub
	}

	@Override
	public Set<String> getProtocols() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setDataRecordGroup(DataRecordGroup dataRecordGroup) {
		// TODO Auto-generated method stub
	}

	@Override
	public DataRecordGroup getDataRecordGroup() {
		// TODO Auto-generated method stub
		return null;
	}

}
