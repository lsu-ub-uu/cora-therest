package se.uu.ub.cora.therest.data;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.Map;
import java.util.Set;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.spider.data.Action;
import se.uu.ub.cora.therest.data.ActionLink;
import se.uu.ub.cora.therest.data.RestDataGroup;
import se.uu.ub.cora.therest.data.RestDataRecord;

public class RestDataRecordTest {
	private RestDataRecord restDataRecord;

	@BeforeMethod
	public void beforeMethod() {
		RestDataGroup restDataGroup = RestDataGroup.withNameInData("nameInData");
		restDataRecord = RestDataRecord.withRestDataGroup(restDataGroup);
	}

	@Test
	public void testWithNameInData() {
		String nameInData = restDataRecord.getRestDataGroup().getNameInData();
		assertEquals(nameInData, "nameInData");
	}

	@Test
	public void testKeys() {
		restDataRecord.addKey("KEY1");
		restDataRecord.addKey("KEY2");
		Set<String> keys = restDataRecord.getKeys();
		assertTrue(keys.contains("KEY1"));
		assertTrue(keys.contains("KEY2"));
	}

	@Test
	public void testActionLinks() {
		restDataRecord.addActionLink("read", ActionLink.withAction(Action.READ));
		Map<String, ActionLink> actionLinks = restDataRecord.getActionLinks();
		ActionLink actionLinkOut = actionLinks.get("read");
		assertEquals(actionLinkOut.getAction(), Action.READ);
	}

	@Test
	public void testActionLinksGet() {
		restDataRecord.addActionLink("read", ActionLink.withAction(Action.READ));
		ActionLink actionLinkOut = restDataRecord.getActionLink("read");
		assertEquals(actionLinkOut.getAction(), Action.READ);
	}

}
