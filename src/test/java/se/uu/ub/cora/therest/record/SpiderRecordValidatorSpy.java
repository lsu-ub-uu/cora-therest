package se.uu.ub.cora.therest.record;

import se.uu.ub.cora.spider.data.SpiderDataGroup;
import se.uu.ub.cora.spider.record.SpiderRecordValidator;
import se.uu.ub.cora.spider.record.ValidationResult;

public class SpiderRecordValidatorSpy implements SpiderRecordValidator {

	public String authToken;
	public String recordType;
	public SpiderDataGroup spiderDataGroup;
	public String actionToPerform;

	@Override
	public ValidationResult validateRecord(String authToken, String recordType,
			SpiderDataGroup spiderDataGroup, String actionToPerform) {
		this.authToken = authToken;
		this.recordType = recordType;
		this.spiderDataGroup = spiderDataGroup;
		this.actionToPerform = actionToPerform;

		return null;
	}

}
