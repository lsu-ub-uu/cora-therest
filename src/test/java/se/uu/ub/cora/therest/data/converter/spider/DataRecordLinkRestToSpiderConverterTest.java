package se.uu.ub.cora.therest.data.converter.spider;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import se.uu.ub.cora.spider.data.SpiderDataRecordLink;
import se.uu.ub.cora.therest.data.RestDataRecordLink;

public class DataRecordLinkRestToSpiderConverterTest {
	@Test
	public void testToSpider() {
		RestDataRecordLink restDataRecordLink = RestDataRecordLink
				.withNameInDataAndRecordTypeAndRecordId("nameInData", "recordType", "recordId");
		DataRecordLinkRestToSpiderConverter dataRecordLinkRestToSpiderConverter = DataRecordLinkRestToSpiderConverter
				.fromRestDataRecordLink(restDataRecordLink);
		SpiderDataRecordLink spiderDataRecordLink = dataRecordLinkRestToSpiderConverter.toSpider();
		assertEquals(spiderDataRecordLink.getNameInData(), "nameInData");
		assertEquals(spiderDataRecordLink.getRecordType(), "recordType");
		assertEquals(spiderDataRecordLink.getRecordId(), "recordId");
	}
}
