/*
 * Copyright 2021, 2022 Uppsala University Library
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
import se.uu.ub.cora.data.DataRecord;
import se.uu.ub.cora.spider.authorization.AuthorizationException;
import se.uu.ub.cora.spider.record.RecordListIndexer;
import se.uu.ub.cora.storage.RecordNotFoundException;
import se.uu.ub.cora.testutils.mcr.MethodCallRecorder;
import se.uu.ub.cora.therest.coradata.DataAtomicSpy;
import se.uu.ub.cora.therest.coradata.DataGroupSpy;
import se.uu.ub.cora.therest.coradata.DataRecordSpy;

public class IndexBatchJobCreatorSpy implements RecordListIndexer {

	public String authToken;
	public String type;
	public DataGroup filter;
	public DataRecordSpy recordToReturn;
	MethodCallRecorder MCR = new MethodCallRecorder();

	@Override
	public DataRecord indexRecordList(String authToken, String type, DataGroup filter) {
		MCR.addCall("authToken", authToken, "type", type, "filter", filter);
		this.authToken = authToken;

		this.type = type;
		this.filter = filter;
		possiblyThrowException(authToken, type);
		DataGroupSpy indexBatchJob = new DataGroupSpy("indexBatchJob");
		DataGroupSpy recordInfo = new DataGroupSpy("recordInfo");
		recordInfo.addChild(new DataAtomicSpy("id", "someId"));
		DataGroupSpy typeGroup = new DataGroupSpy("type");
		typeGroup.addChild(new DataAtomicSpy("linkedRecordType", "recordType"));
		typeGroup.addChild(new DataAtomicSpy("linkedRecordId", "someRecordType"));
		recordInfo.addChild(typeGroup);
		indexBatchJob.addChild(recordInfo);

		recordToReturn = new DataRecordSpy(indexBatchJob);
		MCR.addReturned(recordToReturn);
		return recordToReturn;
	}

	private void possiblyThrowException(String authToken, String type) {
		if ("dummyNonAuthorizedToken".equals(authToken) || authToken == null) {
			throw new AuthorizationException("not authorized");
		}
		if ("recordType_NON_EXISTING".equals(type)) {
			throw RecordNotFoundException.withMessage("no record exist with type " + type);
		}
	}

}
