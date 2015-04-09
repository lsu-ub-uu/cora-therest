package epc.therest.data.converter.spider;

import epc.spider.data.SpiderDataAtomic;
import epc.therest.data.RestDataAtomic;

public final class DataAtomicSpiderToRestConverter {

	public static DataAtomicSpiderToRestConverter fromSpiderDataAtomic(
			SpiderDataAtomic spiderDataAtomic) {
		return new DataAtomicSpiderToRestConverter(spiderDataAtomic);
	}

	private SpiderDataAtomic spiderDataAtomic;

	private DataAtomicSpiderToRestConverter(SpiderDataAtomic spiderDataAtomic) {
		this.spiderDataAtomic = spiderDataAtomic;
	}

	public RestDataAtomic toRest() {
		return RestDataAtomic.withDataIdAndValue(
				spiderDataAtomic.getDataId(), spiderDataAtomic.getValue());
	}

}
