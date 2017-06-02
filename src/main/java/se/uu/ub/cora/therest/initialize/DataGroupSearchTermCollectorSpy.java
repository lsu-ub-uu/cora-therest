package se.uu.ub.cora.therest.initialize;

import se.uu.ub.cora.bookkeeper.data.DataGroup;
import se.uu.ub.cora.bookkeeper.searchtermcollector.DataGroupSearchTermCollector;

public class DataGroupSearchTermCollectorSpy implements DataGroupSearchTermCollector {
    @Override
    public DataGroup collectSearchTerms(String metadataId, DataGroup collectedSearchTerms) {
        return DataGroup.withNameInData("recordIndexData");
    }
}
