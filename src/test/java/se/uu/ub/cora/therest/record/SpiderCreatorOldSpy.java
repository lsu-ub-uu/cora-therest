/*
 * Copyright 2016, 2021 Uppsala University Library
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

import se.uu.ub.cora.data.DataRecord;
import se.uu.ub.cora.data.DataRecordGroup;
import se.uu.ub.cora.data.spies.DataRecordSpy;
import se.uu.ub.cora.spider.authorization.AuthorizationException;
import se.uu.ub.cora.spider.record.ConflictException;
import se.uu.ub.cora.spider.record.DataException;
import se.uu.ub.cora.spider.record.MisuseException;
import se.uu.ub.cora.spider.record.RecordCreator;
import se.uu.ub.cora.storage.RecordConflictException;
import se.uu.ub.cora.storage.RecordNotFoundException;
import se.uu.ub.cora.testutils.mcr.MethodCallRecorder;

public class SpiderCreatorOldSpy implements RecordCreator {

	public String authToken;
	public String type;
	public DataRecordGroup record;
	public MethodCallRecorder MCR = new MethodCallRecorder();

	@Override
	// public DataRecord createAndStoreRecord(String authToken, String type, DataGroup record) {
	public DataRecord createAndStoreRecord(String authToken, String type,
			DataRecordGroup recordGroup) {
		MCR.addCall("authToken", authToken, "type", type, "recordGroup", recordGroup);
		this.authToken = authToken;
		this.type = type;
		this.record = recordGroup;
		if ("dummyNonAuthorizedToken".equals(authToken)) {
			throw new AuthorizationException("not authorized");
		}
		if ("recordType_NON_EXISTING".equals(type)) {
			throw RecordNotFoundException.withMessage("no record exist with type " + type);
		} else if ("place_NON_VALID".equals(type)) {
			throw new DataException("Data is not valid");
		} else if ("abstract".equals(type)) {
			throw new MisuseException(
					"Data creation on abstract recordType:" + type + " is not allowed");
		} else if ("place_duplicate_spider".equals(type)) {
			throw ConflictException.withMessage("Record already exists in spider");
		} else if ("place_duplicate".equals(type)) {
			throw RecordConflictException.withMessage("Record already exists");
		} else if ("place_unexpected_error".equals(type)) {
			throw new NullPointerException("Some error");
		}
		// DataRecordSpy dataRecordSpy = new DataRecordSpy(
		// DataCreator.createRecordWithNameInDataAndIdAndTypeAndLinkedRecordId("nameInData",
		// "someId", type, "linkedRecordId"));
		DataRecordSpy dataRecordSpy = new DataRecordSpy();
		dataRecordSpy.MRV.setDefaultReturnValuesSupplier("getId", () -> "someCreatedId");
		MCR.addReturned(dataRecordSpy);
		return dataRecordSpy;
	}

	// @Override
	// public DataRecord createAndStoreRecord(String authToken, String type,
	// DataRecordGroup recordGroup) {
	// // TODO Auto-generated method stub
	// return null;
	// }

}
