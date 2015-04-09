package epc.therest.data.converter.spider;

import epc.spider.data.SpiderDataAtomic;
import epc.therest.data.RestDataAtomic;

public final class DataAtomicRestToSpiderConverter {

	public static DataAtomicRestToSpiderConverter fromRestDataAtomic(RestDataAtomic restDataAtomic) {
		return new DataAtomicRestToSpiderConverter(restDataAtomic);
	}

	private RestDataAtomic restDataAtomic;

	private DataAtomicRestToSpiderConverter(RestDataAtomic restDataAtomic) {
		this.restDataAtomic = restDataAtomic;
	}

	public SpiderDataAtomic toSpider() {
		return SpiderDataAtomic.withDataIdAndValue(
				restDataAtomic.getDataId(), restDataAtomic.getValue());
	}

}
