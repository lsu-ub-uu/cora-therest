package se.uu.ub.cora.therest.data.converter.spider;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.spider.data.SpiderDataRecordLink;
import se.uu.ub.cora.therest.data.RestDataRecordLink;

public class DataRecordLinkRestToSpiderConverterTest {
	private RestDataRecordLink restDataRecordLink;
	private DataRecordLinkRestToSpiderConverter converter;

	@BeforeMethod
	public void setUp() {
		restDataRecordLink = RestDataRecordLink.withNameInDataAndRecordTypeAndRecordId("nameInData",
				"recordType", "recordId");
		converter = DataRecordLinkRestToSpiderConverter.fromRestDataRecordLink(restDataRecordLink);

	}

	@Test
	public void testToSpider() {
		SpiderDataRecordLink spiderDataRecordLink = converter.toSpider();
		assertEquals(spiderDataRecordLink.getNameInData(), "nameInData");
		assertEquals(spiderDataRecordLink.getRecordType(), "recordType");
		assertEquals(spiderDataRecordLink.getRecordId(), "recordId");
	}

	@Test
	public void testToSpiderWithRepeatId() {
		restDataRecordLink.setRepeatId("45");
		SpiderDataRecordLink spiderDataRecordLink = converter.toSpider();
		assertEquals(spiderDataRecordLink.getNameInData(), "nameInData");
		assertEquals(spiderDataRecordLink.getRecordType(), "recordType");
		assertEquals(spiderDataRecordLink.getRecordId(), "recordId");
		assertEquals(spiderDataRecordLink.getRepeatId(), "45");
	}
}
