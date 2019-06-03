package se.uu.ub.cora.therest.initialize;

import java.util.Map;

import se.uu.ub.cora.storage.RecordIdGeneratorProvider;
import se.uu.ub.cora.storage.StreamStorage;

public class RecordIdGeneratorProviderSpy2 implements RecordIdGeneratorProvider {
	public Map<String, String> initInfo;
	boolean started = false;

	@Override
	public int getOrderToSelectImplementionsBy() {
		return 10;
	}

	@Override
	public void startUsingInitInfo(Map<String, String> initInfo) {
		started = true;
		this.initInfo = initInfo;
	}

	@Override
	public StreamStorage getRecordIdGenerator() {
		// TODO Auto-generated method stub
		return null;
	}

}
