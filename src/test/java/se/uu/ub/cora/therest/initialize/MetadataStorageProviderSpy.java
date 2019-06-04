package se.uu.ub.cora.therest.initialize;

import java.util.Map;

import se.uu.ub.cora.storage.MetadataStorage;
import se.uu.ub.cora.storage.MetadataStorageProvider;

public class MetadataStorageProviderSpy implements MetadataStorageProvider {

	public Map<String, String> initInfo;
	boolean started = false;

	@Override
	public int getOrderToSelectImplementionsBy() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void startUsingInitInfo(Map<String, String> initInfo) {
		started = true;
		this.initInfo = initInfo;
	}

	@Override
	public MetadataStorage getMetadataStorage() {
		// TODO Auto-generated method stub
		return null;
	}

}
