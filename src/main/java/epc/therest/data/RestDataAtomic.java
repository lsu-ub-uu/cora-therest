package epc.therest.data;

public final class RestDataAtomic implements RestDataElement {

	private String dataId;
	private String value;

	public static RestDataAtomic withDataIdAndValue(String dataId, String value) {
		return new RestDataAtomic(dataId, value);
	}

	private RestDataAtomic(String dataId, String value) {
		this.dataId = dataId;
		this.value = value;

	}

	@Override
	public String getDataId() {

		return dataId;
	}

	public String getValue() {
		return value;
	}
}
