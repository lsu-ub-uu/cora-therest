package epc.therest.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import epc.spider.data.SpiderDataAtomic;
import epc.spider.data.SpiderDataElement;
import epc.spider.data.SpiderDataGroup;

public final class RestDataGroup implements DataElementRest {

	private final String dataId;
	private Map<String, String> attributes = new HashMap<>();
	private List<DataElementRest> children = new ArrayList<>();

	public static RestDataGroup withDataId(String dataId) {
		return new RestDataGroup(dataId);
	}

	private RestDataGroup(String dataId) {
		this.dataId = dataId;
	}

	public static RestDataGroup fromDataGroup(SpiderDataGroup spiderDataGroup) {
		return new RestDataGroup(spiderDataGroup);
	}

	private RestDataGroup(SpiderDataGroup spiderDataGroup) {
		dataId = spiderDataGroup.getDataId();
		attributes.putAll(spiderDataGroup.getAttributes());
		convertAndSetChildren(spiderDataGroup);
	}

	private void convertAndSetChildren(SpiderDataGroup spiderDataGroup) {
		for (SpiderDataElement spiderDataElement : spiderDataGroup.getChildren()) {
			children.add(convertToRestEquivalentDataClass(spiderDataElement));
		}
	}

	private DataElementRest convertToRestEquivalentDataClass(SpiderDataElement spiderDataElement) {
		if (spiderDataElement instanceof SpiderDataGroup) {
			return new RestDataGroup((SpiderDataGroup) spiderDataElement);
		}
		return RestDataAtomic.fromSpiderDataAtomic((SpiderDataAtomic) spiderDataElement);
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
