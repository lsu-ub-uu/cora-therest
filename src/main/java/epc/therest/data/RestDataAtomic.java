package epc.therest.data;

import epc.metadataformat.data.DataAtomic;

public final class RestDataAtomic implements DataElementRest {

	private String dataId;
	private String value;

	public static RestDataAtomic withDataIdAndValue(String dataId, String value) {
		return new RestDataAtomic(dataId, value);
	}

	private RestDataAtomic(String dataId, String value) {
		this.dataId = dataId;
		this.value = value;

	}

	public static RestDataAtomic fromDataAtomic(DataAtomic dataAtomic) {
		return new RestDataAtomic(dataAtomic);
	}

	private RestDataAtomic(DataAtomic dataAtomic) {
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
