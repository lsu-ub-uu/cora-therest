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

import se.uu.ub.cora.data.Action;
import se.uu.ub.cora.data.DataRecord;
import se.uu.ub.cora.spider.authentication.AuthenticationException;
import se.uu.ub.cora.spider.authorization.AuthorizationException;
import se.uu.ub.cora.spider.record.RecordReader;
import se.uu.ub.cora.storage.RecordNotFoundException;
import se.uu.ub.cora.therest.coradata.DataRecordSpy;
import se.uu.ub.cora.therest.testdata.DataCreator;

public class SpiderRecordReaderSpy implements RecordReader {

	public String authToken;
	public String type;
	public String id;
	public DataRecord dataRecord;

	@Override
	public DataRecord readRecord(String authToken, String type, String id) {
		this.authToken = authToken;
		this.type = type;
		this.id = id;
		possiblyThrowExceptionForRead(authToken, id);

		dataRecord = new DataRecordSpy(
				DataCreator.createRecordWithNameInDataAndIdAndTypeAndLinkedRecordId("nameInData",
						id, type, "linkedRecordId"));
		dataRecord.addAction(Action.READ);
		return dataRecord;
	}

	private void possiblyThrowExceptionForRead(String authToken, String id) {
		if ("dummyNonAuthenticatedToken".equals(authToken)) {
			throw new AuthenticationException("token not valid");
		} else if ("dummyNonAuthorizedToken".equals(authToken)) {
			throw new AuthorizationException("not authorized");
		}

		if ("place:0001_NOT_FOUND".equals(id)) {
			throw RecordNotFoundException.withMessage("No record exist with id " + id);
		}
	}
}
