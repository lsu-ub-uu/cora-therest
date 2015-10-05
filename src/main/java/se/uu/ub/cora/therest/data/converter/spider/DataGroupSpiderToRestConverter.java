package se.uu.ub.cora.therest.data.converter.spider;

import se.uu.ub.cora.spider.data.SpiderDataAtomic;
import se.uu.ub.cora.spider.data.SpiderDataElement;
import se.uu.ub.cora.spider.data.SpiderDataGroup;
import se.uu.ub.cora.therest.data.RestDataElement;
import se.uu.ub.cora.therest.data.RestDataGroup;

public final class DataGroupSpiderToRestConverter {

	public static DataGroupSpiderToRestConverter fromSpiderDataGroup(
			SpiderDataGroup spiderDataGroup) {
		return new DataGroupSpiderToRestConverter(spiderDataGroup);
	}

	private SpiderDataGroup spiderDataGroup;
	private RestDataGroup restDataGroup;

	private DataGroupSpiderToRestConverter(SpiderDataGroup spiderDataGroup) {
		this.spiderDataGroup = spiderDataGroup;
	}

	public RestDataGroup toRest() {
		restDataGroup = RestDataGroup.withNameInData(spiderDataGroup.getNameInData());
		restDataGroup.getAttributes().putAll(spiderDataGroup.getAttributes());
		convertAndSetChildren();
		return restDataGroup;
	}

	private void convertAndSetChildren() {
		for (SpiderDataElement spiderDataElement : spiderDataGroup.getChildren()) {
			RestDataElement convertedChild = convertToElementEquivalentDataClass(spiderDataElement);
			restDataGroup.getChildren().add(convertedChild);
		}
	}

	private RestDataElement convertToElementEquivalentDataClass(SpiderDataElement spiderDataElement) {
		if (spiderDataElement instanceof SpiderDataGroup) {
			return DataGroupSpiderToRestConverter.fromSpiderDataGroup(
					(SpiderDataGroup) spiderDataElement).toRest();
		}
		return DataAtomicSpiderToRestConverter.fromSpiderDataAtomic(
				(SpiderDataAtomic) spiderDataElement).toRest();
	}
}
