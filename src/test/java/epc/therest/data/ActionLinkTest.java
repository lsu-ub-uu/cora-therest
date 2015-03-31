package epc.therest.data;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import epc.spider.data.Action;

public class ActionLinkTest {
	@Test
	public void testInit() {
		ActionLink actionLink = ActionLink.withAction(Action.READ);
		assertEquals(actionLink.getAction(), Action.READ);
	}

	@Test
	public void testURL() {
		ActionLink actionLink = ActionLink.withAction(Action.READ);
		actionLink.setURL("http://test.org/test/test:001");
		assertEquals(actionLink.getURL(), "http://test.org/test/test:001");
	}

	@Test
	public void testRequestMethod() {
		ActionLink actionLink = ActionLink.withAction(Action.READ);
		actionLink.setRequestMethod("GET");
		assertEquals(actionLink.getRequestMethod(), "GET");
	}

	@Test
	public void testAccept() {
		ActionLink actionLink = ActionLink.withAction(Action.READ);
		actionLink.setAccept("application/metadata_record+json");
		assertEquals(actionLink.getAccept(), "application/metadata_record+json");
	}

	@Test
	public void testContentType() {
		ActionLink actionLink = ActionLink.withAction(Action.READ);
		actionLink.setContentType("application/metadata_record+json");
		assertEquals(actionLink.getContentType(), "application/metadata_record+json");
	}
}
