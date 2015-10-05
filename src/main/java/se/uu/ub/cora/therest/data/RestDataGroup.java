package se.uu.ub.cora.therest.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class RestDataGroup implements RestDataElement {

	private final String nameInData;
	private Map<String, String> attributes = new HashMap<>();
	private List<RestDataElement> children = new ArrayList<>();

	public static RestDataGroup withNameInData(String nameInData) {
		return new RestDataGroup(nameInData);
	}

	private RestDataGroup(String nameInData) {
		this.nameInData = nameInData;
	}

	@Override
	public String getNameInData() {
		return nameInData;
	}

	public Map<String, String> getAttributes() {
		return attributes;
	}

	public void addAttributeByIdWithValue(String nameInData, String value) {
		attributes.put(nameInData, value);
	}

	public void addChild(RestDataElement restDataElement) {
		children.add(restDataElement);
	}

	public List<RestDataElement> getChildren() {
		return children;
	}
}
