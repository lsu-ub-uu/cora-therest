package se.uu.ub.cora.therest.record;

import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataRecord;
import se.uu.ub.cora.spider.authorization.AuthorizationException;
import se.uu.ub.cora.spider.record.RecordValidator;
import se.uu.ub.cora.storage.RecordNotFoundException;
import se.uu.ub.cora.therest.coradata.DataAtomicSpy;
import se.uu.ub.cora.therest.coradata.DataGroupSpy;
import se.uu.ub.cora.therest.coradata.DataRecordSpy;

public class SpiderRecordValidatorSpy implements RecordValidator {

	public String authToken;
	public String recordType;
	public DataGroup dataGroup;
	public DataGroup recordToValidate;
	public DataGroup validationRecord;
	public boolean throwRecordNotFoundException = false;

	@Override
	public DataRecord validateRecord(String authToken, String recordType,
			DataGroup validationRecord, DataGroup recordToValidate) {
		this.authToken = authToken;
		this.recordType = recordType;
		this.validationRecord = validationRecord;
		this.dataGroup = validationRecord;
		this.recordToValidate = recordToValidate;

		if ("dummyNonAuthorizedToken".equals(authToken)) {
			throw new AuthorizationException("not authorized");
		}
		// if ("recordType_NON_EXISTING".equals(recordToValidate.getNameInData())) {
		if (throwRecordNotFoundException) {
			throw new RecordNotFoundException("no record exist with type " + recordType);
		}
		DataGroup validationResult = createValidationResult(recordType);
		return new DataRecordSpy(validationResult);
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

}
