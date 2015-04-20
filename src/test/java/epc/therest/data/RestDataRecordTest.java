package epc.therest.data;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.Map;
import java.util.Set;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import epc.spider.data.Action;

public class RestDataRecordTest {
	private RestDataRecord restDataRecord;

	@BeforeMethod
	public void beforeMethod() {
		RestDataGroup restDataGroup = RestDataGroup.withDataId("dataId");
		restDataRecord = RestDataRecord.withRestDataGroup(restDataGroup);
	}

	@Test
	public void testWithDataId() {
		String dataId = restDataRecord.getRestDataGroup().getDataId();
		assertEquals(dataId, "dataId");
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
