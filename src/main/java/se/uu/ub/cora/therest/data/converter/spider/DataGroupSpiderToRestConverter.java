package se.uu.ub.cora.therest.data.converter.spider;

import se.uu.ub.cora.spider.data.SpiderDataAtomic;
import se.uu.ub.cora.spider.data.SpiderDataElement;
import se.uu.ub.cora.spider.data.SpiderDataGroup;
import se.uu.ub.cora.spider.data.SpiderDataRecordLink;
import se.uu.ub.cora.therest.data.RestDataElement;
import se.uu.ub.cora.therest.data.RestDataGroup;

public final class DataGroupSpiderToRestConverter {

	private SpiderDataGroup spiderDataGroup;
	private RestDataGroup restDataGroup;
	private String baseURL;

	public static DataGroupSpiderToRestConverter fromSpiderDataGroupWithBaseURL(
			SpiderDataGroup spiderDataGroup, String baseURL) {
		return new DataGroupSpiderToRestConverter(spiderDataGroup, baseURL);
	}

	private DataGroupSpiderToRestConverter(SpiderDataGroup spiderDataGroup, String baseURL) {
		this.spiderDataGroup = spiderDataGroup;
		this.baseURL = baseURL;
	}

	public RestDataGroup toRest() {
		restDataGroup = RestDataGroup.withNameInData(spiderDataGroup.getNameInData());
		restDataGroup.getAttributes().putAll(spiderDataGroup.getAttributes());
		restDataGroup.setRepeatId(spiderDataGroup.getRepeatId());
		convertAndSetChildren();
		return restDataGroup;
	}

	private void convertAndSetChildren() {
		for (SpiderDataElement spiderDataElement : spiderDataGroup.getChildren()) {
			RestDataElement convertedChild = convertToElementEquivalentDataClass(spiderDataElement);
			restDataGroup.getChildren().add(convertedChild);
		}
	}

	private RestDataElement convertToElementEquivalentDataClass(
			SpiderDataElement spiderDataElement) {
		if (spiderDataElement instanceof SpiderDataGroup) {
			return DataGroupSpiderToRestConverter
					.fromSpiderDataGroupWithBaseURL((SpiderDataGroup) spiderDataElement, baseURL)
					.toRest();
		}
		if (spiderDataElement instanceof SpiderDataRecordLink) {
			return DataRecordLinkSpiderToRestConverter.fromSpiderDataRecordLinkWithBaseURL(
					(SpiderDataRecordLink) spiderDataElement, baseURL).toRest();
		}
		return DataAtomicSpiderToRestConverter
				.fromSpiderDataAtomic((SpiderDataAtomic) spiderDataElement).toRest();
	}
}
