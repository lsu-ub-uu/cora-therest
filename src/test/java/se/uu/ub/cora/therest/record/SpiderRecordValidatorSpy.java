package se.uu.ub.cora.therest.record;

import se.uu.ub.cora.spider.authorization.AuthorizationException;
import se.uu.ub.cora.spider.data.SpiderDataGroup;
import se.uu.ub.cora.spider.record.SpiderRecordValidator;
import se.uu.ub.cora.spider.record.ValidationResult;
import se.uu.ub.cora.spider.record.storage.RecordNotFoundException;

public class SpiderRecordValidatorSpy implements SpiderRecordValidator {

	public String authToken;
	public String recordType;
	public SpiderDataGroup spiderDataGroup;
	public SpiderDataGroup recordToValidate;
	public SpiderDataGroup validationRecord;

	@Override
	public ValidationResult validateRecord(String authToken, String recordType,
			SpiderDataGroup validationRecord, SpiderDataGroup recordToValidate) {
		this.authToken = authToken;
		this.recordType = recordType;
		this.validationRecord = validationRecord;
		this.spiderDataGroup = validationRecord;
		this.recordToValidate = recordToValidate;

		if ("dummyNonAuthorizedToken".equals(authToken)) {
			throw new AuthorizationException("not authorized");
		}
		if ("recordType_NON_EXISTING".equals(recordType)) {
			throw new RecordNotFoundException("no record exist with type " + recordType);
		}

		return null;
	}

}
