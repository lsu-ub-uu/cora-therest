package se.uu.ub.cora.therest.data;

public final class RestDataAtomic implements RestDataElement {

	private String nameInData;
	private String value;
	private String repeatId;

	public static RestDataAtomic withNameInDataAndValue(String nameInData, String value) {
		return new RestDataAtomic(nameInData, value);
	}

	private RestDataAtomic(String nameInData, String value) {
		this.nameInData = nameInData;
		this.value = value;

	}

	@Override
	public String getNameInData() {

		return nameInData;
	}

	public String getValue() {
		return value;
	}

	public String getRepeatId() {
		return repeatId;
	}

	public void setRepeatId(String repeatId) {
		this.repeatId = repeatId;
	}
}
