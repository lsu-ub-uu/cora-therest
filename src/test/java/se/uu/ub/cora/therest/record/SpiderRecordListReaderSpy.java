/*
 * Copyright 2016 Uppsala University Library
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

package se.uu.ub.cora.therest.record;

import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataList;
import se.uu.ub.cora.spider.authorization.AuthorizationException;
import se.uu.ub.cora.spider.record.RecordListReader;
import se.uu.ub.cora.storage.RecordNotFoundException;
import se.uu.ub.cora.testutils.mcr.MethodCallRecorder;
import se.uu.ub.cora.therest.coradata.DataListSpy;

public class SpiderRecordListReaderSpy implements RecordListReader {

	public String authToken;
	public String type;
	public DataGroup filter;
	public DataListSpy returnedDataList;

	MethodCallRecorder MCR = new MethodCallRecorder();

	@Override
	public DataList readRecordList(String authToken, String type, DataGroup filter) {
		MCR.addCall("authToken", authToken, "type", type, "filter", filter);
		this.authToken = authToken;
		this.type = type;
		this.filter = filter;
		possiblyThrowException(authToken, type);
		returnedDataList = new DataListSpy(type);
		MCR.addReturned(returnedDataList);
		return returnedDataList;
	}

	private void possiblyThrowException(String authToken, String type) {
		if ("place_NOT_FOUND".equals(type)) {
			throw RecordNotFoundException.withMessage("Record not found");
		}
		if ("dummyNonAuthorizedToken".equals(authToken) || authToken == null) {
			throw new AuthorizationException("not authorized");
		}
	}

}
