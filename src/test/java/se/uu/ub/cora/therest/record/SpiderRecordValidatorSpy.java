/*
 * Copyright 2021, 2023 Uppsala University Library
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
import se.uu.ub.cora.data.DataRecordGroup;
import se.uu.ub.cora.spider.authorization.AuthorizationException;
import se.uu.ub.cora.spider.record.RecordValidator;
import se.uu.ub.cora.storage.RecordNotFoundException;
import se.uu.ub.cora.testutils.mcr.MethodCallRecorder;
import se.uu.ub.cora.therest.coradata.DataAtomicSpy;
import se.uu.ub.cora.therest.coradata.DataGroupSpy;
import se.uu.ub.cora.therest.coradata.DataRecordSpy;

public class SpiderRecordValidatorSpy implements RecordValidator {

	public String authToken;
	public String recordType;
	public DataGroup dataGroup;
	public DataRecordGroup recordToValidate;
	public DataGroup validationOrder;
	public boolean throwRecordNotFoundException = false;
	public MethodCallRecorder MCR = new MethodCallRecorder();

	@Override
	// public DataRecord validateRecord(String authToken, String recordType,
	// DataGroup validationRecord, DataGroup recordToValidate) {
	public DataRecord validateRecord(String authToken, String recordType, DataGroup validationOrder,
			DataRecordGroup recordToValidate) {
		MCR.addCall("authToken", authToken, "recordType", recordType, "validationOrder",
				validationOrder, "recordToValidate", recordToValidate);
		this.authToken = authToken;
		this.recordType = recordType;
		this.validationOrder = validationOrder;
		// this.dataGroup = validationRecord;
		this.recordToValidate = recordToValidate;

		if ("dummyNonAuthorizedToken".equals(authToken)) {
			throw new AuthorizationException("not authorized");
		}
		// if ("recordType_NON_EXISTING".equals(recordToValidate.getNameInData())) {
		if (throwRecordNotFoundException) {
			throw RecordNotFoundException.withMessage("no record exist with type " + recordType);
		}
		DataGroup validationResult = createValidationResult(recordType);

		DataRecordSpy dataRecordSpy = new DataRecordSpy(validationResult);
		MCR.addReturned(dataRecordSpy);
		return dataRecordSpy;
	}

	private DataGroup createValidationResult(String recordType) {
		DataGroup validationResult = new DataGroupSpy("validationResult");
		validationResult.addChild(new DataAtomicSpy("valid", "true"));
		DataGroup recordInfo = new DataGroupSpy("recordInfo");
		recordInfo.addChild(new DataAtomicSpy("id", "someSpyId"));
		DataGroup type = new DataGroupSpy("type");
		type.addChild(new DataAtomicSpy("linkedRecordType", "recordType"));
		type.addChild(new DataAtomicSpy("linkedRecordId", recordType));
		recordInfo.addChild(type);
		validationResult.addChild(recordInfo);
		return validationResult;
	}

	// @Override
	// public DataRecord validateRecord(String authToken, String recordType, DataGroup
	// validationOrder,
	// DataRecordGroup recordToValidate) {
	// // TODO Auto-generated method stub
	// return null;
	// }

}
