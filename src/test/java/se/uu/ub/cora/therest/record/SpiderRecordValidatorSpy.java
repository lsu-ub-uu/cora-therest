package se.uu.ub.cora.therest.record;

import se.uu.ub.cora.spider.authorization.AuthorizationException;
import se.uu.ub.cora.spider.data.SpiderDataAtomic;
import se.uu.ub.cora.spider.data.SpiderDataGroup;
import se.uu.ub.cora.spider.data.SpiderDataRecord;
import se.uu.ub.cora.spider.record.SpiderRecordValidator;
import se.uu.ub.cora.spider.record.storage.RecordNotFoundException;

public class SpiderRecordValidatorSpy implements SpiderRecordValidator {

	public String authToken;
	public String recordType;
	public SpiderDataGroup spiderDataGroup;
	public SpiderDataGroup recordToValidate;
	public SpiderDataGroup validationRecord;

	@Override
	public SpiderDataRecord validateRecord(String authToken, String recordType,
			SpiderDataGroup validationRecord, SpiderDataGroup recordToValidate) {
		this.authToken = authToken;
		this.recordType = recordType;
		this.validationRecord = validationRecord;
		this.spiderDataGroup = validationRecord;
		this.recordToValidate = recordToValidate;

		if ("dummyNonAuthorizedToken".equals(authToken)) {
			throw new AuthorizationException("not authorized");
		}
		if ("recordType_NON_EXISTING".equals(recordToValidate.getNameInData())) {
			throw new RecordNotFoundException("no record exist with type " + recordType);
		}
		SpiderDataGroup validationResult = createValidationResult(recordType);
		return SpiderDataRecord.withSpiderDataGroup(validationResult);
	}

	private SpiderDataGroup createValidationResult(String recordType) {
		SpiderDataGroup validationResult = SpiderDataGroup.withNameInData("validationResult");
		validationResult.addChild(SpiderDataAtomic.withNameInDataAndValue("valid", "true"));
		SpiderDataGroup recordInfo = SpiderDataGroup.withNameInData("recordInfo");
		recordInfo.addChild(SpiderDataAtomic.withNameInDataAndValue("id", "someSpyId"));
		SpiderDataGroup type = SpiderDataGroup.withNameInData("type");
		type.addChild(SpiderDataAtomic.withNameInDataAndValue("linkedRecordType", "recordType"));
		type.addChild(SpiderDataAtomic.withNameInDataAndValue("linkedRecordId", recordType));
		recordInfo.addChild(type);
		validationResult.addChild(recordInfo);
		return validationResult;
	}

}
