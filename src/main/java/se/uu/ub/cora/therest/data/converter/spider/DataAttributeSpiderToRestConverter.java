package se.uu.ub.cora.therest.data.converter.spider;

import se.uu.ub.cora.spider.data.SpiderDataAttribute;
import se.uu.ub.cora.therest.data.RestDataAttribute;

public class DataAttributeSpiderToRestConverter {
	public static DataAttributeSpiderToRestConverter fromSpiderDataAttribute(
			SpiderDataAttribute spiderDataAttribute) {
		return new DataAttributeSpiderToRestConverter(spiderDataAttribute);
	}

	private SpiderDataAttribute spiderDataAttribute;

	private DataAttributeSpiderToRestConverter(SpiderDataAttribute spiderDataAttribute) {
		this.spiderDataAttribute = spiderDataAttribute;
	}

	public RestDataAttribute toRest() {
		return RestDataAttribute.withNameInDataAndValue(
				spiderDataAttribute.getNameInData(), spiderDataAttribute.getValue());
	}
}
