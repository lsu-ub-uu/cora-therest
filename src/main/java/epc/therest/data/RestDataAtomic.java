package epc.therest.data;

import epc.spider.data.SpiderDataAtomic;

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

	public static RestDataAtomic fromSpiderDataAtomic(SpiderDataAtomic spiderDataAtomic) {
		return new RestDataAtomic(spiderDataAtomic);
	}

	private RestDataAtomic(SpiderDataAtomic spiderDataAtomic) {
		this.dataId = spiderDataAtomic.getDataId();
		this.value = spiderDataAtomic.getValue();
	}

	@Override
	public String getDataId() {

		return dataId;
	}

	public String getValue() {
		return value;
	}
}
