package se.uu.ub.cora.therest.data;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.spider.data.Action;

public class RestDataRecordLinkTest {
	private RestDataRecordLink recordLink;

	@BeforeMethod
	public void setUp() {
		recordLink = RestDataRecordLink.withNameInDataAndRecordTypeAndRecordId("nameInData",
				"aRecordType", "aRecordId");
	}

	@Test
	public void testInit() {
		assertEquals(recordLink.getRecordType(), "aRecordType");
		assertEquals(recordLink.getRecordId(), "aRecordId");
	}

	@Test
	public void testWithActionLinks() {
		ActionLink actionLink = ActionLink.withAction(Action.READ);
		recordLink.addActionLink("read", actionLink);
		assertEquals(recordLink.getActionLink("read"), actionLink);
		assertEquals(recordLink.getActionLinks().get("read"), actionLink);
		assertNull(recordLink.getActionLink("notAnAction"));
	}

	@Test
	public void testWithRepeatId() {
		recordLink.setRepeatId("x2");
		assertEquals(recordLink.getRepeatId(), "x2");
	}
}
