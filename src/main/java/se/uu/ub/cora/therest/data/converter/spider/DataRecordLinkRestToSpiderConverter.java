package se.uu.ub.cora.therest.data.converter.spider;

import se.uu.ub.cora.spider.data.SpiderDataRecordLink;
import se.uu.ub.cora.therest.data.RestDataRecordLink;

public final class DataRecordLinkRestToSpiderConverter {
	private RestDataRecordLink restDataRecordLink;

	public static DataRecordLinkRestToSpiderConverter fromRestDataRecordLink(
			RestDataRecordLink restDataRecordLink) {
		return new DataRecordLinkRestToSpiderConverter(restDataRecordLink);
	}

	private DataRecordLinkRestToSpiderConverter(RestDataRecordLink restDataRecordLink) {
		this.restDataRecordLink = restDataRecordLink;
	}

	public SpiderDataRecordLink toSpider() {
		return SpiderDataRecordLink
				.withNameInDataAndRecordTypeAndRecordId(restDataRecordLink.getNameInData(),
						restDataRecordLink.getRecordType(), restDataRecordLink.getRecordId());
	}

}
