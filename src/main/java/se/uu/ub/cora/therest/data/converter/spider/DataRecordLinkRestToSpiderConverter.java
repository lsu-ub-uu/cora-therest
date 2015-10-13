package se.uu.ub.cora.therest.data.converter.spider;

import se.uu.ub.cora.spider.data.SpiderDataRecordLink;
import se.uu.ub.cora.therest.data.RestDataRecordLink;

public class DataRecordLinkRestToSpiderConverter {
	private RestDataRecordLink restDataRecordLink;
	private SpiderDataRecordLink spiderDataRecordLink;

	public static DataRecordLinkRestToSpiderConverter fromRestDataRecordLink(
			RestDataRecordLink restDataRecordLink) {
		return new DataRecordLinkRestToSpiderConverter(restDataRecordLink);
	}

	private DataRecordLinkRestToSpiderConverter(RestDataRecordLink restDataRecordLink) {
		this.restDataRecordLink = restDataRecordLink;
	}

	public SpiderDataRecordLink toSpider() {
		spiderDataRecordLink = SpiderDataRecordLink.withNameInDataAndRecordTypeAndRecordId(
				restDataRecordLink.getNameInData(), restDataRecordLink.getRecordType(),
				restDataRecordLink.getRecordId());
		return spiderDataRecordLink;
	}

}
