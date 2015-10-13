package se.uu.ub.cora.therest.data;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

import org.testng.annotations.Test;

import se.uu.ub.cora.spider.data.Action;

public class RestDataRecordLinkTest {
	@Test
	public void testInit() {
		RestDataRecordLink recordLink = RestDataRecordLink
				.withNameInDataAndRecordTypeAndRecordId("nameInData", "aRecordType", "aRecordId");
		assertEquals(recordLink.getRecordType(), "aRecordType");
		assertEquals(recordLink.getRecordId(), "aRecordId");

	}

	@Test
	public void testWithActionLinks() {
		RestDataRecordLink recordLink = RestDataRecordLink
				.withNameInDataAndRecordTypeAndRecordId("nameInData", "aRecordType", "aRecordId");
		ActionLink actionLink = ActionLink.withAction(Action.READ);
		recordLink.addActionLink("read", actionLink);
		assertEquals(recordLink.getActionLink("read"), actionLink);
		assertEquals(recordLink.getActionLinks().get("read"), actionLink);
		assertNull(recordLink.getActionLink("notAnAction"));
	}
}
