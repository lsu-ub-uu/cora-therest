package se.uu.ub.cora.therest.data.converter.spider;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.spider.data.Action;
import se.uu.ub.cora.spider.data.SpiderDataRecordLink;
import se.uu.ub.cora.therest.data.ActionLink;
import se.uu.ub.cora.therest.data.RestDataRecordLink;

public class DataRecordLinkSpiderToRestConverterTest {
	private String baseURL = "http://localhost:8080/therest/rest/record/";
	private SpiderDataRecordLink spiderDataRecordLink;
	private DataRecordLinkSpiderToRestConverter dataRecordLinkSpiderToRestConverter;

	@BeforeMethod
	public void setUp() {
		spiderDataRecordLink = SpiderDataRecordLink
				.withNameInDataAndRecordTypeAndRecordId("nameInData", "recordType", "recordId");
		dataRecordLinkSpiderToRestConverter = DataRecordLinkSpiderToRestConverter
				.fromSpiderDataRecordLinkWithBaseURL(spiderDataRecordLink, baseURL);

	}

	@Test
	public void testToRest() {
		RestDataRecordLink restDataRecordLink = dataRecordLinkSpiderToRestConverter.toRest();
		assertEquals(restDataRecordLink.getNameInData(), "nameInData");
		assertEquals(restDataRecordLink.getRecordType(), "recordType");
		assertEquals(restDataRecordLink.getRecordId(), "recordId");
	}

	@Test
	public void testToRestWithRepeatId() {
		spiderDataRecordLink.setRepeatId("j");
		RestDataRecordLink restDataRecordLink = dataRecordLinkSpiderToRestConverter.toRest();
		assertEquals(restDataRecordLink.getNameInData(), "nameInData");
		assertEquals(restDataRecordLink.getRecordType(), "recordType");
		assertEquals(restDataRecordLink.getRecordId(), "recordId");
		assertEquals(restDataRecordLink.getRepeatId(), "j");
	}

	@Test
	public void testToRestWithAction() {
		spiderDataRecordLink.addAction(Action.READ);
		RestDataRecordLink restDataRecordLink = dataRecordLinkSpiderToRestConverter.toRest();
		assertEquals(restDataRecordLink.getNameInData(), "nameInData");
		assertEquals(restDataRecordLink.getRecordType(), "recordType");
		assertEquals(restDataRecordLink.getRecordId(), "recordId");
		ActionLink actionLink = restDataRecordLink.getActionLink("read");
		assertEquals(actionLink.getAction(), Action.READ);
		assertEquals(actionLink.getURL(),
				"http://localhost:8080/therest/rest/record/recordType/recordId");
		assertEquals(actionLink.getRequestMethod(), "GET");
	}

}
