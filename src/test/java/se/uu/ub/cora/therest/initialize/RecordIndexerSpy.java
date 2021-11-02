package se.uu.ub.cora.therest.initialize;

import java.util.List;

import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.search.RecordIndexer;

public class RecordIndexerSpy implements RecordIndexer {

	@Override
	public void indexData(List<String> ids, DataGroup recordIndexData, DataGroup record) {
	}

	@Override
	public void deleteFromIndex(String type, String id) {
		// TODO Auto-generated method stub

	}

	@Override
	public void indexDataWithoutExplicitCommit(List<String> ids, DataGroup collectedData,
			DataGroup dataRecord) {
		// TODO Auto-generated method stub

	}
}
