package epc.therest.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import epc.metadataformat.data.DataAtomic;
import epc.metadataformat.data.DataElement;
import epc.metadataformat.data.DataGroup;

public final class DataGroupRest implements DataElementRest {

	private final String dataId;
	private Map<String, String> attributes = new HashMap<>();
	private List<DataElementRest> children = new ArrayList<>();

	public static DataGroupRest withDataId(String dataId) {
		return new DataGroupRest(dataId);
	}

	private DataGroupRest(String dataId) {
		this.dataId = dataId;
	}

	public static DataGroupRest fromDataGroup(DataGroup dataGroup) {
		return new DataGroupRest(dataGroup);
	}

	private DataGroupRest(DataGroup dataGroup) {
		dataId = dataGroup.getDataId();
		attributes.putAll(dataGroup.getAttributes());
		convertAndSetChildren(dataGroup);
	}

	private void convertAndSetChildren(DataGroup dataGroup) {
		for (DataElement dataElement : dataGroup.getChildren()) {
			children.add(convertToRestEquivalentDataClass(dataElement));
		}
	}

	private DataElementRest convertToRestEquivalentDataClass(DataElement dataElement) {
		if (dataElement instanceof DataGroup) {
			return new DataGroupRest((DataGroup) dataElement);
		}
		return DataAtomicRest.fromDataAtomic((DataAtomic) dataElement);
	}

	@Override
	public String getDataId() {
		return dataId;
	}

	public Map<String, String> getAttributes() {
		return attributes;
	}

	public void addAttribute(String dataId, String value) {
		attributes.put(dataId, value);
	}

	public void addChild(DataElementRest dataElementRest) {
		children.add(dataElementRest);
	}

	public List<DataElementRest> getChildren() {
		return children;
	}
}
