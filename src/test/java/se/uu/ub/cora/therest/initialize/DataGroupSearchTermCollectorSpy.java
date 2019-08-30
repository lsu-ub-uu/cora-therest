package se.uu.ub.cora.therest.initialize;

import se.uu.ub.cora.bookkeeper.termcollector.DataGroupTermCollector;
import se.uu.ub.cora.data.DataGroup;

public class DataGroupSearchTermCollectorSpy implements DataGroupTermCollector {
	@Override
	public DataGroup collectTerms(String metadataId, DataGroup collectedSearchTerms) {
		return DataGroup.withNameInData("recordIndexData");
	}
}
