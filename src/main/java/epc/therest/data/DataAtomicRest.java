package epc.therest.data;

import epc.metadataformat.data.DataAtomic;

public final class DataAtomicRest implements DataElementRest {

	private String dataId;
	private String value;

	public static DataAtomicRest withDataIdAndValue(String dataId, String value) {
		return new DataAtomicRest(dataId, value);
	}

	private DataAtomicRest(String dataId, String value) {
		this.dataId = dataId;
		this.value = value;

	}

	public static DataAtomicRest fromDataAtomic(DataAtomic dataAtomic) {
		return new DataAtomicRest(dataAtomic);
	}

	private DataAtomicRest(DataAtomic dataAtomic) {
		this.dataId = dataAtomic.getDataId();
		this.value = dataAtomic.getValue();
	}

	@Override
	public String getDataId() {

		return dataId;
	}

	public String getValue() {
		return value;
	}
}
