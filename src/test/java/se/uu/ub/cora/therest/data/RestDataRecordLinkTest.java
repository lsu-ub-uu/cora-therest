package se.uu.ub.cora.therest.data;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

import org.testng.annotations.Test;

import se.uu.ub.cora.spider.data.Action;

public class RestDataRecordLinkTest {
	@Test
	public void testInit() {
		String nameInData = "nameInData";
		RestDataRecordLink recordLink = RestDataRecordLink.withNameInData(nameInData);
		recordLink.setRecordType("aRecordType");
		recordLink.setRecordId("aRecordId");
		assertEquals(recordLink.getRecordType(), "aRecordType");
		assertEquals(recordLink.getRecordId(), "aRecordId");

	}

	@Test
	public void testWithActionLinks() {
		RestDataRecordLink recordLink = RestDataRecordLink.withNameInData("nameInData");
		ActionLink actionLink = ActionLink.withAction(Action.READ);
		recordLink.addActionLink("read", actionLink);
		assertEquals(recordLink.getActionLink("read"), actionLink);
		assertEquals(recordLink.getActionLinks().get("read"), actionLink);
		assertNull(recordLink.getActionLink("notAnAction"));
	}
}
