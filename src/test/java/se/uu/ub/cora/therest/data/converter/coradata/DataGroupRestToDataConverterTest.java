/*
 * Copyright 2015 Uppsala University Library
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

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.data.DataAtomic;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataGroupFactory;
import se.uu.ub.cora.data.DataGroupProvider;
import se.uu.ub.cora.data.DataRecordLink;
import se.uu.ub.cora.data.DataRecordLinkFactory;
import se.uu.ub.cora.data.DataRecordLinkProvider;
import se.uu.ub.cora.data.DataResourceLink;
import se.uu.ub.cora.data.DataResourceLinkFactory;
import se.uu.ub.cora.data.DataResourceLinkProvider;
import se.uu.ub.cora.therest.data.RestDataAtomic;
import se.uu.ub.cora.therest.data.RestDataAttribute;
import se.uu.ub.cora.therest.data.RestDataGroup;
import se.uu.ub.cora.therest.data.RestDataRecordLink;
import se.uu.ub.cora.therest.data.RestDataResourceLink;
import se.uu.ub.cora.therest.data.converter.ConverterException;
import se.uu.ub.cora.therest.data.converter.RestToDataConverter;

public class DataGroupRestToDataConverterTest {
	private RestDataGroup restDataGroup;
	private RestToDataConverter dataGroupRestToSpiderConverter;
	private DataGroupFactory dataGroupFactory;
	private DataRecordLinkFactory dataRecordLinkFactory;
	private DataResourceLinkFactory dataResourceLinkFactory;

	@BeforeMethod
	public void setUp() {
		setUpFactoriesAndProviders();
		restDataGroup = RestDataGroup.withNameInData("nameInData");
		dataGroupRestToSpiderConverter = DataGroupRestToDataConverter
				.fromRestDataGroup(restDataGroup);
	}

	private void setUpFactoriesAndProviders() {
		dataGroupFactory = new DataGroupFactorySpy();
		DataGroupProvider.setDataGroupFactory(dataGroupFactory);
		dataRecordLinkFactory = new DataRecordLinkFactorySpy();
		DataRecordLinkProvider.setDataRecordLinkFactory(dataRecordLinkFactory);
		dataResourceLinkFactory = new DataResourceLinkFactorySpy();
		DataResourceLinkProvider.setDataResourceLinkFactory(dataResourceLinkFactory);
	}

	@Test
	public void testToData() {
		DataGroup dataGroup = dataGroupRestToSpiderConverter.convert();
		assertEquals(dataGroup.getNameInData(), "nameInData");
	}

	@Test
	public void testToDataWithRepeatId() {
		restDataGroup.setRepeatId("34");
		DataGroup dataGroup = dataGroupRestToSpiderConverter.convert();
		assertEquals(dataGroup.getNameInData(), "nameInData");
		assertEquals(dataGroup.getRepeatId(), "34");
	}

	@Test
	public void testToDataWithAttribute() {
		restDataGroup.addAttributeByIdWithValue("attributeId", "attributeValue");

		DataGroup dataGroup = dataGroupRestToSpiderConverter.convert();

		String attributeValue = dataGroup.getAttribute("attributeId").getValue();
		assertEquals(attributeValue, "attributeValue");
	}

	@Test
	public void testToDataWithAtomicChild() {
		restDataGroup.addChild(RestDataAtomic.withNameInDataAndValue("atomicId", "atomicValue"));

		DataGroup dataGroup = dataGroupRestToSpiderConverter.convert();

		DataAtomic atomicChild = (DataAtomic) dataGroup.getChildren().get(0);
		assertEquals(atomicChild.getNameInData(), "atomicId");
		assertEquals(atomicChild.getValue(), "atomicValue");
	}

	@Test
	public void testToDataWithRecordLinkChild() {
		RestDataRecordLink restDataRecordLink = RestDataRecordLink.withNameInData("aLink");
		RestDataAtomic linkedRecordTypeChild = RestDataAtomic
				.withNameInDataAndValue("linkedRecordType", "someRecordType");
		restDataRecordLink.addChild(linkedRecordTypeChild);

		RestDataAtomic linkedRecordIdChild = RestDataAtomic.withNameInDataAndValue("linkedRecordId",
				"someRecordId");
		restDataRecordLink.addChild(linkedRecordIdChild);
		restDataGroup.addChild(restDataRecordLink);

		DataGroup dataGroup = dataGroupRestToSpiderConverter.convert();

		DataRecordLink dataRecordLink = (DataRecordLink) dataGroup
				.getFirstChildWithNameInData("aLink");
		assertEquals(dataRecordLink.getNameInData(), "aLink");

		DataAtomic linkedRecordType = (DataAtomic) dataRecordLink
				.getFirstChildWithNameInData("linkedRecordType");
		DataAtomic linkedRecordId = (DataAtomic) dataRecordLink
				.getFirstChildWithNameInData("linkedRecordId");

		assertEquals(linkedRecordType.getValue(), "someRecordType");
		assertEquals(linkedRecordId.getValue(), "someRecordId");

		assertEquals(dataGroup.getAllGroupsWithNameInData("aLink").size(), 1);
	}

	@Test
	public void testToDataWithResourceLinkChild() {
		createResourceLink();

		DataGroup dataGroup = dataGroupRestToSpiderConverter.convert();
		DataResourceLink dataRecordLink = (DataResourceLink) dataGroup
				.getFirstChildWithNameInData("aLink");
		assertEquals(dataRecordLink.getNameInData(), "aLink");

		DataAtomic streamId = (DataAtomic) dataRecordLink.getFirstChildWithNameInData("streamId");
		DataAtomic filename = (DataAtomic) dataRecordLink.getFirstChildWithNameInData("filename");
		DataAtomic filesize = (DataAtomic) dataRecordLink.getFirstChildWithNameInData("filesize");
		DataAtomic mimeType = (DataAtomic) dataRecordLink.getFirstChildWithNameInData("mimeType");

		assertEquals(streamId.getValue(), "someStreamId");
		assertEquals(filename.getValue(), "someFilename");
		assertEquals(filesize.getValue(), "someFilesize");
		assertEquals(mimeType.getValue(), "someMimeType");
	}

	private void createResourceLink() {
		RestDataResourceLink restDataResourceLink = RestDataResourceLink.withNameInData("aLink");
		RestDataAtomic streamId = RestDataAtomic.withNameInDataAndValue("streamId", "someStreamId");
		restDataResourceLink.addChild(streamId);

		RestDataAtomic filename = RestDataAtomic.withNameInDataAndValue("filename", "someFilename");
		restDataResourceLink.addChild(filename);
		RestDataAtomic filesize = RestDataAtomic.withNameInDataAndValue("filesize", "someFilesize");
		restDataResourceLink.addChild(filesize);
		RestDataAtomic mimeType = RestDataAtomic.withNameInDataAndValue("mimeType", "someMimeType");
		restDataResourceLink.addChild(mimeType);
		restDataGroup.addChild(restDataResourceLink);
	}

	@Test
	public void testToDataWithGroupChild() {
		restDataGroup.addChild(RestDataGroup.withNameInData("groupId"));
		DataGroup dataGroup = dataGroupRestToSpiderConverter.convert();

		DataGroup groupChild = (DataGroup) dataGroup.getChildren().get(0);
		assertEquals(groupChild.getNameInData(), "groupId");
	}

	@Test
	public void testToDataWithGroupChildWithAtomicChild() {
		RestDataGroup restGroupChild = RestDataGroup.withNameInData("groupId");
		restGroupChild.addChild(RestDataAtomic.withNameInDataAndValue("grandChildAtomicId",
				"grandChildAtomicValue"));
		restGroupChild.addAttributeByIdWithValue("groupChildAttributeId",
				"groupChildAttributeValue");
		restDataGroup.addChild(restGroupChild);

		DataGroup dataGroup = dataGroupRestToSpiderConverter.convert();

		DataGroup groupChild = (DataGroup) dataGroup.getChildren().get(0);
		DataAtomic grandChildAtomic = (DataAtomic) groupChild.getChildren().get(0);
		assertEquals(grandChildAtomic.getNameInData(), "grandChildAtomicId");
		String groupChildAttributeValue = restGroupChild.getAttributes()
				.get("groupChildAttributeId");
		assertEquals(groupChildAttributeValue, "groupChildAttributeValue");
	}

	@Test(expectedExceptions = ConverterException.class)
	public void testToDataWithAttributeAsChild() {
		restDataGroup.addChild(RestDataAttribute.withNameInDataAndValue("atomicId", "atomicValue"));

		DataGroup dataGroup = dataGroupRestToSpiderConverter.convert();

		DataAtomic atomicChild = (DataAtomic) dataGroup.getChildren().get(0);
		assertEquals(atomicChild.getNameInData(), "atomicId");
		assertEquals(atomicChild.getValue(), "atomicValue");
	}
}
