/*
 * Copyright 2015, 2016, 2019 Uppsala University Library
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

package se.uu.ub.cora.therest.data.converter.coradata;

import static org.testng.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.data.Action;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.therest.data.ActionLink;
import se.uu.ub.cora.therest.data.DataAtomicSpy;
import se.uu.ub.cora.therest.data.DataGroupSpy;
import se.uu.ub.cora.therest.data.RestDataAtomic;
import se.uu.ub.cora.therest.data.RestDataGroup;
import se.uu.ub.cora.therest.data.converter.ConverterInfo;

public class ActionSpiderToRestConverterTest {
	private ConverterInfo defaultConverterInfo;

	private List<Action> actions;

	@BeforeMethod
	public void setUp() {
		actions = new ArrayList<>();
		String recordId = "recordId";
		String recordType = "recordType";
		defaultConverterInfo = createConverterInfoWithTypeAndId(recordType, recordId);
	}

	private ConverterInfo createConverterInfoWithTypeAndId(String recordType, String recordId) {
		return ConverterInfo.withBaseURLAndRecordURLAndTypeAndId(
				"http://localhost:8080/therest/rest/record/",
				"http://localhost:8080/therest/rest/record/someRecordType/someRecordId", recordType,
				recordId);
	}

	@Test
	public void testToRestREAD() {
		Action action = Action.READ;
		actions.add(action);

		ActionDataToRestConverter actionSpiderToRestConverter = ActionDataToRestConverterImp
				.fromDataActionsWithConverterInfo(actions, defaultConverterInfo);
		Map<String, ActionLink> actionLinks = actionSpiderToRestConverter.toRest();
		ActionLink actionLink = actionLinks.get("read");
		assertEquals(actionLink.getAction(), Action.READ);
		assertEquals(actionLink.getURL(),
				"http://localhost:8080/therest/rest/record/recordType/recordId");
		assertEquals(actionLink.getRequestMethod(), "GET");
		assertEquals(actionLink.getAccept(), "application/vnd.uub.record+json");
		assertEquals(actionLink.getContentType(), null);
	}

	@Test
	public void testToRestReadIncomingList() {
		Action action = Action.READ_INCOMING_LINKS;
		actions.add(action);
		ActionDataToRestConverter actionSpiderToRestConverter = ActionDataToRestConverterImp
				.fromDataActionsWithConverterInfo(actions, defaultConverterInfo);
		Map<String, ActionLink> actionLinks = actionSpiderToRestConverter.toRest();
		ActionLink actionLink = actionLinks.get("read_incoming_links");
		assertEquals(actionLink.getAction(), Action.READ_INCOMING_LINKS);
		assertEquals(actionLink.getURL(),
				"http://localhost:8080/therest/rest/record/recordType/recordId/incomingLinks");
		assertEquals(actionLink.getRequestMethod(), "GET");
		assertEquals(actionLink.getAccept(), "application/vnd.uub.recordList+json");
		assertEquals(actionLink.getContentType(), null);
	}

	@Test
	public void testToRestWithActionLinkUPDATE() {
		Action action = Action.UPDATE;
		actions.add(action);
		ActionDataToRestConverter actionSpiderToRestConverter = ActionDataToRestConverterImp
				.fromDataActionsWithConverterInfo(actions, defaultConverterInfo);
		Map<String, ActionLink> actionLinks = actionSpiderToRestConverter.toRest();

		ActionLink actionLink = actionLinks.get("update");
		assertEquals(actionLink.getAction(), Action.UPDATE);
		assertEquals(actionLink.getURL(),
				"http://localhost:8080/therest/rest/record/recordType/recordId");
		assertEquals(actionLink.getRequestMethod(), "POST");
		assertEquals(actionLink.getAccept(), "application/vnd.uub.record+json");
		assertEquals(actionLink.getContentType(), "application/vnd.uub.record+json");
	}

	@Test
	public void testToRestWithActionLinkDELETE() {
		Action action = Action.DELETE;
		actions.add(action);

		ActionDataToRestConverter actionSpiderToRestConverter = ActionDataToRestConverterImp
				.fromDataActionsWithConverterInfo(actions, defaultConverterInfo);
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
		ConverterInfo converterInfo = createConverterInfoWithTypeAndId("recordType", "text");

		ActionDataToRestConverter actionSpiderToRestConverter = ActionDataToRestConverterImp
				.fromDataActionsWithConverterInfo(actions, converterInfo);
		Map<String, ActionLink> actionLinks = actionSpiderToRestConverter.toRest();

		ActionLink actionLink = actionLinks.get("create");
		assertEquals(actionLink.getAction(), Action.CREATE);
		assertEquals(actionLink.getURL(), "http://localhost:8080/therest/rest/record/text/");
		assertEquals(actionLink.getRequestMethod(), "POST");
		assertEquals(actionLink.getAccept(), "application/vnd.uub.record+json");
		assertEquals(actionLink.getContentType(), "application/vnd.uub.record+json");
	}

	@Test
	public void testToRestWithActionLinkUpload() {
		Action actionUpload = Action.UPLOAD;
		actions.add(actionUpload);

		ConverterInfo converterInfo = createConverterInfoWithTypeAndId("image", "image:0001");

		ActionDataToRestConverter actionSpiderToRestConverter = ActionDataToRestConverterImp
				.fromDataActionsWithConverterInfo(actions, converterInfo);
		Map<String, ActionLink> actionLinks = actionSpiderToRestConverter.toRest();

		ActionLink actionLink = actionLinks.get("upload");

		assertEquals(actionLink.getAction(), actionUpload);
		assertEquals(actionLink.getURL(),
				"http://localhost:8080/therest/rest/record/image/image:0001/master");
		assertEquals(actionLink.getRequestMethod(), "POST");
		assertEquals(actionLink.getAccept(), null);
		assertEquals(actionLink.getContentType(), "multipart/form-data");
	}

	@Test
	public void testToRestWithActionLinkLIST() {
		Action action = Action.LIST;
		actions.add(action);
		ConverterInfo converterInfo = createConverterInfoWithTypeAndId("recordType", "text");

		ActionDataToRestConverter actionSpiderToRestConverter = ActionDataToRestConverterImp
				.fromDataActionsWithConverterInfo(actions, converterInfo);
		Map<String, ActionLink> actionLinks = actionSpiderToRestConverter.toRest();

		ActionLink actionLink = actionLinks.get("list");
		assertEquals(actionLink.getAction(), Action.LIST);
		assertEquals(actionLink.getURL(), "http://localhost:8080/therest/rest/record/text/");
		assertEquals(actionLink.getRequestMethod(), "GET");
		assertEquals(actionLink.getAccept(), "application/vnd.uub.recordList+json");
		assertEquals(actionLink.getContentType(), null);
	}

	@Test
	public void testToRestWithActionLinkINDEX() {
		Action action = Action.INDEX;
		actions.add(action);
		ConverterInfo converterInfo = createConverterInfoWithTypeAndId("person", "somePersonId");

		ActionDataToRestConverter actionSpiderToRestConverter = ActionDataToRestConverterImp
				.fromDataActionsWithConverterInfo(actions, converterInfo);
		Map<String, ActionLink> actionLinks = actionSpiderToRestConverter.toRest();

		ActionLink actionLink = actionLinks.get("index");
		assertEquals(actionLink.getAction(), Action.INDEX);
		assertEquals(actionLink.getURL(), "http://localhost:8080/therest/rest/record/workOrder/");
		assertEquals(actionLink.getRequestMethod(), "POST");
		assertEquals(actionLink.getAccept(), "application/vnd.uub.record+json");
		assertEquals(actionLink.getContentType(), "application/vnd.uub.record+json");
		assertCorrectBodyPartOfActionLink(actionLink);
	}

	private void assertCorrectBodyPartOfActionLink(ActionLink actionLink) {
		RestDataGroup body = actionLink.getBody();
		assertEquals(body.getNameInData(), "workOrder");

		RestDataGroup recordType = (RestDataGroup) body.getFirstChildWithNameInData("recordType");
		assertEquals(((RestDataAtomic) recordType.getFirstChildWithNameInData("linkedRecordType"))
				.getValue(), "recordType");
		assertEquals(((RestDataAtomic) recordType.getFirstChildWithNameInData("linkedRecordId"))
				.getValue(), "person");

		RestDataAtomic type = (RestDataAtomic) body.getFirstChildWithNameInData("type");
		assertEquals(type.getValue(), "index");
		RestDataAtomic recordId = (RestDataAtomic) body.getFirstChildWithNameInData("recordId");
		assertEquals(recordId.getValue(), "somePersonId");
	}

	@Test
	public void testToRestWithActionLinkSEARCHForRecordTypeSearch() {
		Action action = Action.SEARCH;
		actions.add(action);

		ConverterInfo converterInfo = createConverterInfoWithTypeAndId("search", "aSearchId");

		ActionDataToRestConverter actionSpiderToRestConverter = ActionDataToRestConverterImp
				.fromDataActionsWithConverterInfo(actions, converterInfo);
		Map<String, ActionLink> actionLinks = actionSpiderToRestConverter.toRest();

		ActionLink actionLink = actionLinks.get("search");
		assertEquals(actionLink.getAction(), Action.SEARCH);
		assertEquals(actionLink.getURL(),
				"http://localhost:8080/therest/rest/record/searchResult/aSearchId");
		assertEquals(actionLink.getRequestMethod(), "GET");
		assertEquals(actionLink.getAccept(), "application/vnd.uub.recordList+json");
		assertEquals(actionLink.getContentType(), null);
	}

	@Test
	public void testToRestWithActionLinkSEARCHForRecordTypeRecordType() {
		Action action = Action.SEARCH;
		actions.add(action);

		DataGroup dataGroup = new DataGroupSpy("someRecordType");
		DataGroup search = new DataGroupSpy("search");
		search.addChild(new DataAtomicSpy("linkedRecordType", "search"));
		search.addChild(new DataAtomicSpy("linkedRecordId", "defaultSearchForRecordType"));
		dataGroup.addChild(search);
		ConverterInfo converterInfo = ConverterInfo.withBaseURLAndRecordURLAndTypeAndId(
				defaultConverterInfo.baseURL, defaultConverterInfo.recordURL, "recordType",
				"aRecordType");

		ActionDataToRestConverter actionSpiderToRestConverter = ActionDataToRestConverterImp
				.fromDataActionsWithConverterInfoAndDataGroup(actions, converterInfo, dataGroup);
		Map<String, ActionLink> actionLinks = actionSpiderToRestConverter.toRest();

		ActionLink actionLink = actionLinks.get("search");
		assertEquals(actionLink.getAction(), Action.SEARCH);
		assertEquals(actionLink.getURL(),
				"http://localhost:8080/therest/rest/record/searchResult/defaultSearchForRecordType");
		assertEquals(actionLink.getRequestMethod(), "GET");
		assertEquals(actionLink.getAccept(), "application/vnd.uub.recordList+json");
		assertEquals(actionLink.getContentType(), null);
	}

	@Test
	public void testToRestWithActionLinkSEARCHForRecordTypeRecordTypeNOSearchGroupInMetadata() {
		Action action = Action.SEARCH;
		actions.add(action);

		DataGroup dataGroup = new DataGroupSpy("someRecordType");

		ConverterInfo converterInfo = ConverterInfo.withBaseURLAndRecordURLAndTypeAndId(
				defaultConverterInfo.baseURL, defaultConverterInfo.recordURL, "recordType",
				"aRecordType");

		ActionDataToRestConverter actionSpiderToRestConverter = ActionDataToRestConverterImp
				.fromDataActionsWithConverterInfoAndDataGroup(actions, converterInfo, dataGroup);
		Map<String, ActionLink> actionLinks = actionSpiderToRestConverter.toRest();

		ActionLink actionLink = actionLinks.get("search");
		assertEquals(actionLink.getAction(), Action.SEARCH);
		assertEquals(actionLink.getURL(),
				"http://localhost:8080/therest/rest/record/searchResult/aRecordType");
		assertEquals(actionLink.getRequestMethod(), "GET");
		assertEquals(actionLink.getAccept(), "application/vnd.uub.recordList+json");
		assertEquals(actionLink.getContentType(), null);
	}

	@Test
	public void testToRestWithActionLinkVALIDATE() {
		Action action = Action.VALIDATE;
		actions.add(action);
		ActionDataToRestConverter actionSpiderToRestConverter = ActionDataToRestConverterImp
				.fromDataActionsWithConverterInfo(actions, defaultConverterInfo);
		Map<String, ActionLink> actionLinks = actionSpiderToRestConverter.toRest();

		ActionLink actionLink = actionLinks.get("validate");
		assertEquals(actionLink.getAction(), Action.VALIDATE);

		assertEquals(actionLink.getURL(), "http://localhost:8080/therest/rest/record/workOrder/");
		assertEquals(actionLink.getRequestMethod(), "POST");
		assertEquals(actionLink.getAccept(), "application/vnd.uub.record+json");
		assertEquals(actionLink.getContentType(), "application/vnd.uub.workorder+json");
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
		actions.add(Action.VALIDATE);

		ConverterInfo converterInfo = createConverterInfoWithTypeAndId("recordType", "text");
		ActionDataToRestConverter actionSpiderToRestConverter = ActionDataToRestConverterImp
				.fromDataActionsWithConverterInfo(actions, converterInfo);
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
		assertEquals(search.getURL(),
				"http://localhost:8080/therest/rest/record/searchResult/text");

		ActionLink createByUpload = actionLinks.get("upload");
		assertEquals(createByUpload.getURL(),
				"http://localhost:8080/therest/rest/record/recordType/text/master");

		ActionLink validate = actionLinks.get("validate");
		assertEquals(validate.getURL(), "http://localhost:8080/therest/rest/record/workOrder/");

	}
}
