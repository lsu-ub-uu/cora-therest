package se.uu.ub.cora.therest.initialize;

import se.uu.ub.cora.bookkeeper.data.DataGroup;
import se.uu.ub.cora.bookkeeper.searchtermcollector.DataGroupTermCollector;

public class DataGroupSearchTermCollectorSpy implements DataGroupTermCollector {
    @Override
    public DataGroup collectTerms(String metadataId, DataGroup collectedSearchTerms) {
        return DataGroup.withNameInData("recordIndexData");
    }
}
