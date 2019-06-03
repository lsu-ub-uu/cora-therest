package se.uu.ub.cora.therest.initialize;

import java.util.Map;

import se.uu.ub.cora.storage.StreamStorage;
import se.uu.ub.cora.storage.StreamStorageProvider;

public class StreamStorageProviderSpy implements StreamStorageProvider {
	public Map<String, String> initInfo;
	boolean started = false;

	@Override
	public int getOrderToSelectImplementionsBy() {
		return 0;
	}

	@Override
	public void startUsingInitInfo(Map<String, String> initInfo) {
		started = true;
		this.initInfo = initInfo;
	}

	@Override
	public StreamStorage getStreamStorage() {
		// TODO Auto-generated method stub
		return null;
	}

}
