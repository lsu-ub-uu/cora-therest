/*
 * Copyright 2017, 2022 Uppsala University Library
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
import se.uu.ub.cora.spider.authentication.AuthenticationException;
import se.uu.ub.cora.spider.authorization.AuthorizationException;
import se.uu.ub.cora.spider.record.DataException;
import se.uu.ub.cora.spider.record.RecordSearcher;
import se.uu.ub.cora.storage.RecordNotFoundException;
import se.uu.ub.cora.testutils.mcr.MethodCallRecorder;
import se.uu.ub.cora.therest.coradata.DataListSpy;

public class SpiderRecordSearcherSpy implements RecordSearcher {

	public String authToken;
	public String searchId;
	public DataGroup searchData;
	public DataList searchResult;
	public MethodCallRecorder MCR = new MethodCallRecorder();

	@Override
	public DataList search(String authToken, String searchId, DataGroup searchData) {
		MCR.addCall("authToken", authToken, "searchId", searchId, "searchData", searchData);
		this.authToken = authToken;
		this.searchId = searchId;
		this.searchData = searchData;
		possiblyThrowException(authToken, searchId);
		searchResult = new DataListSpy("mix");
		searchResult.setFromNo("0");
		searchResult.setToNo("1");
		searchResult.setTotalNo("1");
		MCR.addReturned(searchResult);
		return searchResult;
	}

	private void possiblyThrowException(String authToken, String searchId) {
		if ("nonExistingToken".equals(authToken)) {
			throw new AuthenticationException("User not authenticated");
		}
		if ("dummyNonAuthorizedToken".equals(authToken)) {
			throw new AuthorizationException("User not authorized");
		}
		if ("aSearchId_NOT_FOUND".equals(searchId)) {
			throw new RecordNotFoundException("Record does not exist");
		}
		if ("aSearchId_INVALID_DATA".equals(searchId)) {
			throw new DataException("SearchData is invalid");
		}
	}

}
