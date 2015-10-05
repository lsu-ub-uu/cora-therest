package se.uu.ub.cora.therest.data.converter.spider;

import se.uu.ub.cora.spider.data.SpiderDataAtomic;
import se.uu.ub.cora.therest.data.RestDataAtomic;

public final class DataAtomicRestToSpiderConverter {

	public static DataAtomicRestToSpiderConverter fromRestDataAtomic(RestDataAtomic restDataAtomic) {
		return new DataAtomicRestToSpiderConverter(restDataAtomic);
	}

	private RestDataAtomic restDataAtomic;

	private DataAtomicRestToSpiderConverter(RestDataAtomic restDataAtomic) {
		this.restDataAtomic = restDataAtomic;
	}

	public SpiderDataAtomic toSpider() {
		return SpiderDataAtomic.withNameInDataAndValue(
				restDataAtomic.getNameInData(), restDataAtomic.getValue());
	}

}
