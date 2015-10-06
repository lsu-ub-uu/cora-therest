package se.uu.ub.cora.therest.data;

public class RestDataAttribute implements RestDataElement {
	
	private String nameInData;
	private String value;

	public static RestDataAttribute withNameInDataAndValue(String nameInData, String value) {
		return new RestDataAttribute(nameInData, value);
	}

	private RestDataAttribute(String nameInData, String value) {
		this.nameInData = nameInData;
		this.value = value;
	}

	public String getNameInData() {
		return nameInData;
	}

	public String getValue() {
		return value;
	}

}
