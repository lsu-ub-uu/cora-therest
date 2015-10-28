package se.uu.ub.cora.therest.data.converter.spider;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import se.uu.ub.cora.spider.data.SpiderDataRecordLink;
import se.uu.ub.cora.therest.data.RestDataGroup;
import se.uu.ub.cora.therest.data.RestDataRecordLink;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

public class DataRecordLinkRestToSpiderConverterTest {
	private RestDataRecordLink restDataRecordLink;
	private DataRecordLinkRestToSpiderConverter converter;

	@BeforeMethod
	public void setUp() {
		restDataRecordLink = RestDataRecordLink.withNameInDataAndLinkedRecordTypeAndLinkedRecordId("nameInData",
				"linkedRecordType", "linkedRecordId");
		converter = DataRecordLinkRestToSpiderConverter.fromRestDataRecordLink(restDataRecordLink);

	}

	@Test
	public void testToSpider() {
		SpiderDataRecordLink spiderDataRecordLink = converter.toSpider();
		assertEquals(spiderDataRecordLink.getNameInData(), "nameInData");
		assertEquals(spiderDataRecordLink.getLinkedRecordType(), "linkedRecordType");
		assertEquals(spiderDataRecordLink.getLinkedRecordId(), "linkedRecordId");
		assertNull(spiderDataRecordLink.getLinkedPath());
	}

	@Test
	public void testToSpiderWithRepeatId() {
		restDataRecordLink.setRepeatId("45");
		SpiderDataRecordLink spiderDataRecordLink = converter.toSpider();
		assertEquals(spiderDataRecordLink.getNameInData(), "nameInData");
		assertEquals(spiderDataRecordLink.getLinkedRecordType(), "linkedRecordType");
		assertEquals(spiderDataRecordLink.getLinkedRecordId(), "linkedRecordId");
		assertEquals(spiderDataRecordLink.getRepeatId(), "45");
	}

	@Test
	public void testToSpiderWithLinkedRepeatId(){
		restDataRecordLink.setLinkedRepeatId("linkedOne");
		SpiderDataRecordLink spiderDataRecordLink = converter.toSpider();
		assertEquals(spiderDataRecordLink.getLinkedRepeatId(), "linkedOne");
	}

	@Test
	public void testToSpiderWithLinkedPath(){
		restDataRecordLink.setLinkedPath(RestDataGroup.withNameInData("linkedPath"));
		SpiderDataRecordLink spiderDataRecordLink = converter.toSpider();
		assertEquals(spiderDataRecordLink.getLinkedPath().getNameInData(), "linkedPath");
	}
}
