package se.uu.ub.cora.therest.data.converter.spider;

import se.uu.ub.cora.spider.data.SpiderDataRecordLink;
import se.uu.ub.cora.therest.data.RestDataRecordLink;

public final class DataRecordLinkSpiderToRestConverter {
	private SpiderDataRecordLink spiderDataRecordLink;
	private String baseURL;
	private RestDataRecordLink restDataRecordLink;

	public static DataRecordLinkSpiderToRestConverter fromSpiderDataRecordLinkWithBaseURL(
			SpiderDataRecordLink spiderDataRecordLink, String baseURL) {
		return new DataRecordLinkSpiderToRestConverter(spiderDataRecordLink, baseURL);
	}

	private DataRecordLinkSpiderToRestConverter(SpiderDataRecordLink spiderDataRecordLink,
			String baseURL) {
		this.spiderDataRecordLink = spiderDataRecordLink;
		this.baseURL = baseURL;
	}

	public RestDataRecordLink toRest() {
		restDataRecordLink = RestDataRecordLink.withNameInDataAndRecordTypeAndRecordId(
				spiderDataRecordLink.getNameInData(), spiderDataRecordLink.getRecordType(),
				spiderDataRecordLink.getRecordId());
		createRestLinks(restDataRecordLink.getRecordType(), restDataRecordLink.getRecordId());
		return restDataRecordLink;
	}

	private void createRestLinks(String recordType, String recordId) {
		ActionSpiderToRestConverter actionSpiderToRestConverter = ActionSpiderToRestConverter
				.fromSpiderActionsWithBaseURLAndRecordTypeAndRecordId(
						spiderDataRecordLink.getActions(), baseURL, recordType, recordId);
		restDataRecordLink.setActionLinks(actionSpiderToRestConverter.toRest());
	}
}
