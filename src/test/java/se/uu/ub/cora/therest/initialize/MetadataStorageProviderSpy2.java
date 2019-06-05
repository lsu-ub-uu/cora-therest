package se.uu.ub.cora.therest.initialize;

import java.util.Map;

import se.uu.ub.cora.bookkeeper.storage.MetadataStorage;
import se.uu.ub.cora.storage.MetadataStorageProvider;

public class MetadataStorageProviderSpy2 implements MetadataStorageProvider {

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
	public MetadataStorage getMetadataStorage() {
		// TODO Auto-generated method stub
		return null;
	}

}
