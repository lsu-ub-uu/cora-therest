package se.uu.ub.cora.therest.data.converter.spider;

import static org.testng.Assert.assertEquals;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.spider.data.Action;
import se.uu.ub.cora.therest.data.ActionLink;

public class ActionSpiderToRestConverterTest {
	private String baseURL = "http://localhost:8080/therest/rest/record/";
	private Set<Action> actions;

	@BeforeMethod
	public void setUP() {
		actions = new LinkedHashSet<>();

	}

	@Test
	public void testToRestREAD() {
		Action action = Action.READ;
		actions.add(action);

		ActionSpiderToRestConverter actionSpiderToRestConverter = ActionSpiderToRestConverter
				.fromSpiderActionsWithBaseURLAndRecordTypeAndRecordId(actions, baseURL,
						"recordType", "recordId");
		Map<String, ActionLink> actionLinks = actionSpiderToRestConverter.toRest();
		ActionLink actionLink = actionLinks.get("read");
		assertEquals(actionLink.getAction(), Action.READ);
		assertEquals(actionLink.getURL(),
				"http://localhost:8080/therest/rest/record/recordType/recordId");
		assertEquals(actionLink.getRequestMethod(), "GET");
	}

	@Test
	public void testToRestWithActionLinkUPDATE() {
		Action action = Action.UPDATE;
		actions.add(action);
		ActionSpiderToRestConverter actionSpiderToRestConverter = ActionSpiderToRestConverter
				.fromSpiderActionsWithBaseURLAndRecordTypeAndRecordId(actions, baseURL,
						"recordType", "recordId");
		Map<String, ActionLink> actionLinks = actionSpiderToRestConverter.toRest();

		ActionLink actionLink = actionLinks.get("update");
		assertEquals(actionLink.getAction(), Action.UPDATE);
		assertEquals(actionLink.getURL(),
				"http://localhost:8080/therest/rest/record/recordType/recordId");
		assertEquals(actionLink.getRequestMethod(), "POST");
	}

	@Test
	public void testToRestWithActionLinkDELETE() {
		Action action = Action.DELETE;
		actions.add(action);

		ActionSpiderToRestConverter actionSpiderToRestConverter = ActionSpiderToRestConverter
				.fromSpiderActionsWithBaseURLAndRecordTypeAndRecordId(actions, baseURL,
						"recordType", "recordId");
		Map<String, ActionLink> actionLinks = actionSpiderToRestConverter.toRest();

		ActionLink actionLink = actionLinks.get("delete");
		assertEquals(actionLink.getAction(), Action.DELETE);
		assertEquals(actionLink.getURL(),
				"http://localhost:8080/therest/rest/record/recordType/recordId");
		assertEquals(actionLink.getRequestMethod(), "DELETE");
	}

}
