package se.uu.ub.cora.therest.data.converter.spider;

import se.uu.ub.cora.spider.data.SpiderDataAtomic;
import se.uu.ub.cora.therest.data.RestDataAtomic;

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
		return RestDataAtomic.withNameInDataAndValue(
				spiderDataAtomic.getNameInData(), spiderDataAtomic.getValue());
	}

}
