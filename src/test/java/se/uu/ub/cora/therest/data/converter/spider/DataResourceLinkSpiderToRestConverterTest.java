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

package se.uu.ub.cora.therest.data.converter.spider;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.data.Action;
import se.uu.ub.cora.data.DataResourceLink;
import se.uu.ub.cora.therest.data.ActionLink;
import se.uu.ub.cora.therest.data.DataAtomicSpy;
import se.uu.ub.cora.therest.data.DataResourceLinkSpy;
import se.uu.ub.cora.therest.data.RestDataAtomic;
import se.uu.ub.cora.therest.data.RestDataResourceLink;
import se.uu.ub.cora.therest.data.converter.ConverterInfo;

public class DataResourceLinkSpiderToRestConverterTest {
	private ConverterInfo converterInfo = ConverterInfo.withBaseURLAndRecordURLAndTypeAndId(
			"http://localhost:8080/therest/rest/record/",
			"http://localhost:8080/therest/rest/record/image/someImageId", "image", "someImageId");
	private DataResourceLink spiderDataResourceLink;
	private DataResourceLinkSpiderToRestConverter dataResourceLinkSpiderToRestConverter;

	@BeforeMethod
	public void setUp() {
		spiderDataResourceLink = new DataResourceLinkSpy("master");

		spiderDataResourceLink.addChild(new DataAtomicSpy("streamId", "aStreamId"));
		spiderDataResourceLink.addChild(new DataAtomicSpy("mimeType", "application/png"));

		dataResourceLinkSpiderToRestConverter = DataResourceLinkSpiderToRestConverter
				.fromDataResourceLinkWithConverterInfo(spiderDataResourceLink, converterInfo);

	}

	@Test
	public void testToRest() {
		RestDataResourceLink restDataResourceLink = dataResourceLinkSpiderToRestConverter.toRest();
		assertEquals(restDataResourceLink.getNameInData(), "master");

		RestDataAtomic streamId = (RestDataAtomic) restDataResourceLink
				.getFirstChildWithNameInData("streamId");

		assertEquals(streamId.getValue(), "aStreamId");
	}

	@Test
	public void testToRestWithRepeatId() {
		spiderDataResourceLink.setRepeatId("j");
		RestDataResourceLink restDataResourceLink = dataResourceLinkSpiderToRestConverter.toRest();
		assertEquals(restDataResourceLink.getNameInData(), "master");

		RestDataAtomic streamId = (RestDataAtomic) restDataResourceLink
				.getFirstChildWithNameInData("streamId");
		assertEquals(streamId.getValue(), "aStreamId");

		assertEquals(restDataResourceLink.getRepeatId(), "j");
	}

	@Test
	public void testToRestWithAction() {
		spiderDataResourceLink.addAction(Action.READ);
		RestDataResourceLink restDataResourceLink = dataResourceLinkSpiderToRestConverter.toRest();
		assertEquals(restDataResourceLink.getNameInData(), "master");

		RestDataAtomic streamId = (RestDataAtomic) restDataResourceLink
				.getFirstChildWithNameInData("streamId");
		assertEquals(streamId.getValue(), "aStreamId");
		ActionLink actionLink = restDataResourceLink.getActionLink("read");
		assertEquals(actionLink.getAction(), Action.READ);
		assertEquals(actionLink.getURL(),
				"http://localhost:8080/therest/rest/record/image/someImageId/master");
		assertEquals(actionLink.getRequestMethod(), "GET");
		assertEquals(actionLink.getAccept(), "application/png");
	}

}
