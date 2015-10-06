package se.uu.ub.cora.therest.data.converter.spider;

import se.uu.ub.cora.spider.data.SpiderDataAttribute;
import se.uu.ub.cora.therest.data.RestDataAttribute;

public final class DataAttributeRestToSpiderConverter {
	public static DataAttributeRestToSpiderConverter fromRestDataAttribute(
			RestDataAttribute restDataAttribute) {
		return new DataAttributeRestToSpiderConverter(restDataAttribute);
	}

	private RestDataAttribute restDataAttribute;

	private DataAttributeRestToSpiderConverter(RestDataAttribute restDataAttribute) {
		this.restDataAttribute = restDataAttribute;
	}

	public SpiderDataAttribute toSpider() {
		return SpiderDataAttribute.withNameInDataAndValue(restDataAttribute.getNameInData(),
				restDataAttribute.getValue());
	}
}
