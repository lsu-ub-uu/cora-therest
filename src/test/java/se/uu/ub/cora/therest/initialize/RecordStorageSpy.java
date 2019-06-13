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
package se.uu.ub.cora.therest.initialize;

import java.util.Collection;

import se.uu.ub.cora.bookkeeper.data.DataGroup;
import se.uu.ub.cora.storage.RecordStorage;
import se.uu.ub.cora.storage.SearchStorage;
import se.uu.ub.cora.storage.SpiderReadResult;

public class RecordStorageSpy implements RecordStorage, SearchStorage {

	@Override
	public void create(String arg0, String arg1, DataGroup arg2, DataGroup arg3, DataGroup arg4,
			String arg5) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteByTypeAndId(String arg0, String arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public Collection<DataGroup> generateLinkCollectionPointingToRecord(String arg0, String arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean linksExistForRecord(String arg0, String arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public DataGroup read(String arg0, String arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SpiderReadResult readAbstractList(String arg0, DataGroup arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DataGroup readLinkList(String arg0, String arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SpiderReadResult readList(String arg0, DataGroup arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean recordExistsForAbstractOrImplementingRecordTypeAndRecordId(String arg0,
			String arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean recordsExistForRecordType(String arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void update(String arg0, String arg1, DataGroup arg2, DataGroup arg3, DataGroup arg4,
			String arg5) {
		// TODO Auto-generated method stub

	}

	@Override
	public DataGroup getCollectIndexTerm(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DataGroup getSearchTerm(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

}
