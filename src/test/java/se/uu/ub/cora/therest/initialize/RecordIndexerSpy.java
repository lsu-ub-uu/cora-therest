package se.uu.ub.cora.therest.initialize;

import se.uu.ub.cora.bookkeeper.data.DataGroup;
import se.uu.ub.cora.spider.search.RecordIndexer;

public class RecordIndexerSpy implements RecordIndexer {

	@Override
	public void indexData(DataGroup recordIndexData, DataGroup record) {
	}

	@Override
	public void deleteFromIndex(String type, String id) {
		// TODO Auto-generated method stub

	}
}
