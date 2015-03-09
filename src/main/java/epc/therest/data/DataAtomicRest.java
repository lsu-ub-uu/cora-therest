package epc.therest.data;

import epc.metadataformat.data.DataAtomic;

public class DataAtomicRest implements DataElementRest {

	public String dataId;
	public String value;

	public DataAtomicRest() {
		dataId = "this constructor is here to enable conversion to JSON";
	}

	public DataAtomicRest(String dataId, String value) {
		this.dataId = dataId;
		this.value = value;

	}

	public DataAtomicRest(DataAtomic dataAtomic) {
		this.dataId = dataAtomic.getDataId();
		this.value = dataAtomic.getValue();
	}

	public String getDataId() {

		return dataId;
	}

	public String getValue() {
		return value;
	}

}
