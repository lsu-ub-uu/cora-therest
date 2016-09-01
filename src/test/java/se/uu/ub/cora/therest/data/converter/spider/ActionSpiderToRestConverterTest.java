/*
 * Copyright 2015, 2016 Uppsala University Library
 *
 * This file is part of Cora.
 *
 *     Cora is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Cora is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Cora.  If not, see <http://www.gnu.org/licenses/>.
 */

package se.uu.ub.cora.therest.data.converter.spider;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import se.uu.ub.cora.spider.data.Action;
import se.uu.ub.cora.therest.data.ActionLink;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.testng.Assert.assertEquals;

public class ActionSpiderToRestConverterTest {
	private String baseURL = "http://localhost:8080/therest/rest/record/";
	private List<Action> actions;

	@BeforeMethod
	public void setUp() {
		actions = new ArrayList<>();
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
		assertEquals(actionLink.getAccept(), "application/uub+record+json");
		assertEquals(actionLink.getContentType(), null);
	}

	@Test
	public void testToRestReadIncomingList() {
		Action action = Action.READ_INCOMING_LINKS;
		actions.add(action);
		ActionSpiderToRestConverter actionSpiderToRestConverter = ActionSpiderToRestConverter
				.fromSpiderActionsWithBaseURLAndRecordTypeAndRecordId(actions, baseURL,
						"recordType", "recordId");
		Map<String, ActionLink> actionLinks = actionSpiderToRestConverter.toRest();
		ActionLink actionLink = actionLinks.get("read_incoming_links");
		assertEquals(actionLink.getAction(), Action.READ_INCOMING_LINKS);
		assertEquals(actionLink.getURL(),
				"http://localhost:8080/therest/rest/record/recordType/recordId/incomingLinks");
		assertEquals(actionLink.getRequestMethod(), "GET");
		assertEquals(actionLink.getAccept(), "application/uub+recordList+json");
		assertEquals(actionLink.getContentType(), null);
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
		assertEquals(actionLink.getAccept(), "application/uub+record+json");
		assertEquals(actionLink.getContentType(), "application/uub+record+json");
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
		assertEquals(actionLink.getAccept(), null);
		assertEquals(actionLink.getContentType(), null);
	}

	@Test
	public void testToRestWithActionLinkCREATE() {
		Action action = Action.CREATE;
		actions.add(action);

		ActionSpiderToRestConverter actionSpiderToRestConverter = ActionSpiderToRestConverter
				.fromSpiderActionsWithBaseURLAndRecordTypeAndRecordId(actions, baseURL,
						"recordType", "text");
		Map<String, ActionLink> actionLinks = actionSpiderToRestConverter.toRest();

		ActionLink actionLink = actionLinks.get("create");
		assertEquals(actionLink.getAction(), Action.CREATE);
		assertEquals(actionLink.getURL(), "http://localhost:8080/therest/rest/record/text/");
		assertEquals(actionLink.getRequestMethod(), "POST");
		assertEquals(actionLink.getAccept(), "application/uub+record+json");
		assertEquals(actionLink.getContentType(), "application/uub+record+json");
	}

	@Test
	public void testToRestWithActionLinkUpload() {
		Action actionUpload = Action.UPLOAD;
		actions.add(actionUpload);
		
		ActionSpiderToRestConverter actionSpiderToRestConverter = ActionSpiderToRestConverter
				.fromSpiderActionsWithBaseURLAndRecordTypeAndRecordId(actions, baseURL,
						"image", "image:0001");
		Map<String, ActionLink> actionLinks = actionSpiderToRestConverter.toRest();

		ActionLink actionLink = actionLinks.get("upload");
		
		assertEquals(actionLink.getAction(), actionUpload);
		assertEquals(actionLink.getURL(), "http://localhost:8080/therest/rest/record/image/image:0001/upload");
		assertEquals(actionLink.getRequestMethod(), "POST");
		assertEquals(actionLink.getAccept(), null);
		assertEquals(actionLink.getContentType(), "multipart/form-data");
	}

	@Test
	public void testToRestWithActionLinkLIST() {
		Action action = Action.LIST;
		actions.add(action);

		ActionSpiderToRestConverter actionSpiderToRestConverter = ActionSpiderToRestConverter
				.fromSpiderActionsWithBaseURLAndRecordTypeAndRecordId(actions, baseURL,
						"recordType", "text");
		Map<String, ActionLink> actionLinks = actionSpiderToRestConverter.toRest();

		ActionLink actionLink = actionLinks.get("list");
		assertEquals(actionLink.getAction(), Action.LIST);
		assertEquals(actionLink.getURL(), "http://localhost:8080/therest/rest/record/text/");
		assertEquals(actionLink.getRequestMethod(), "GET");
		assertEquals(actionLink.getAccept(), "application/uub+recordList+json");
		assertEquals(actionLink.getContentType(), null);
	}

	@Test
	public void testToRestWithActionLinkSEARCH() {
		Action action = Action.SEARCH;
		actions.add(action);

		ActionSpiderToRestConverter actionSpiderToRestConverter = ActionSpiderToRestConverter
				.fromSpiderActionsWithBaseURLAndRecordTypeAndRecordId(actions, baseURL,
						"recordType", "text");
		Map<String, ActionLink> actionLinks = actionSpiderToRestConverter.toRest();

		ActionLink actionLink = actionLinks.get("search");
		assertEquals(actionLink.getAction(), Action.SEARCH);
		assertEquals(actionLink.getURL(), "http://localhost:8080/therest/rest/record/text/");
		assertEquals(actionLink.getRequestMethod(), "GET");
		assertEquals(actionLink.getAccept(), "application/uub+recordList+json");
		assertEquals(actionLink.getContentType(), null);
	}

	@Test
	public void testToRestURLsWithAllActions() {
		actions.add(Action.READ_INCOMING_LINKS);
		actions.add(Action.READ);
		actions.add(Action.DELETE);
		actions.add(Action.UPDATE);
		actions.add(Action.CREATE);
		actions.add(Action.LIST);
		actions.add(Action.SEARCH);
		actions.add(Action.UPLOAD);

		ActionSpiderToRestConverter actionSpiderToRestConverter = ActionSpiderToRestConverter
				.fromSpiderActionsWithBaseURLAndRecordTypeAndRecordId(actions, baseURL,
						"recordType", "text");
		Map<String, ActionLink> actionLinks = actionSpiderToRestConverter.toRest();

		ActionLink delete = actionLinks.get("delete");
		assertEquals(delete.getURL(), "http://localhost:8080/therest/rest/record/recordType/text");

		ActionLink readIncomingLinks = actionLinks.get("read_incoming_links");
		assertEquals(readIncomingLinks.getURL(),
				"http://localhost:8080/therest/rest/record/recordType/text/incomingLinks");

		ActionLink update = actionLinks.get("update");
		assertEquals(update.getURL(), "http://localhost:8080/therest/rest/record/recordType/text");

		ActionLink read = actionLinks.get("read");
		assertEquals(read.getURL(), "http://localhost:8080/therest/rest/record/recordType/text");

		ActionLink create = actionLinks.get("create");
		assertEquals(create.getURL(), "http://localhost:8080/therest/rest/record/text/");

		ActionLink list = actionLinks.get("list");
		assertEquals(list.getURL(), "http://localhost:8080/therest/rest/record/text/");

		ActionLink search = actionLinks.get("search");
		assertEquals(search.getURL(), "http://localhost:8080/therest/rest/record/text/");

		ActionLink createByUpload = actionLinks.get("upload");
		assertEquals(createByUpload.getURL(),  "http://localhost:8080/therest/rest/record/recordType/text/upload");
	}
}
