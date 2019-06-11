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

package se.uu.ub.cora.therest.data.converter;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.json.builder.JsonBuilderFactory;
import se.uu.ub.cora.json.builder.org.OrgJsonBuilderFactoryAdapter;
import se.uu.ub.cora.therest.data.RestDataAtomic;
import se.uu.ub.cora.therest.data.RestDataGroup;

public class DataGroupToJsonConverterTest {
	private DataToJsonConverterFactory dataToJsonConverterFactory;
	private JsonBuilderFactory factory;
	private RestDataGroup restDataGroup;

	@BeforeMethod
	public void beforeMethod() {
		dataToJsonConverterFactory = new DataToJsonConverterFactoryImp();
		factory = new OrgJsonBuilderFactoryAdapter();
		restDataGroup = RestDataGroup.withNameInData("groupNameInData");
	}

	@Test
	public void testToJson() {
		DataToJsonConverter dataToJsonConverter = dataToJsonConverterFactory
				.createForRestDataElement(factory, restDataGroup);
		String json = dataToJsonConverter.toJson();

		assertEquals(json, "{\"name\":\"groupNameInData\"}");
	}

	@Test
	public void testToJsonWithRepeatId() {
		restDataGroup.setRepeatId("4");
		DataToJsonConverter dataToJsonConverter = dataToJsonConverterFactory
				.createForRestDataElement(factory, restDataGroup);
		String json = dataToJsonConverter.toJson();

		assertEquals(json, "{\"repeatId\":\"4\",\"name\":\"groupNameInData\"}");
	}

	@Test
	public void testToJsonWithEmptyRepeatId() {
		restDataGroup.setRepeatId("");
		DataToJsonConverter dataToJsonConverter = dataToJsonConverterFactory
				.createForRestDataElement(factory, restDataGroup);
		String json = dataToJsonConverter.toJson();

		assertEquals(json, "{\"name\":\"groupNameInData\"}");
	}

	@Test
	public void testToJsonGroupWithAttribute() {
		restDataGroup.addAttributeByIdWithValue("attributeNameInData", "attributeValue");

		DataToJsonConverter dataToJsonConverter = dataToJsonConverterFactory
				.createForRestDataElement(factory, restDataGroup);
		String json = dataToJsonConverter.toJson();

		assertEquals(json,
				"{\"name\":\"groupNameInData\",\"attributes\":{\"attributeNameInData\":\"attributeValue\"}}");
	}

	@Test
	public void testToJsonGroupWithAttributes() {
		restDataGroup.addAttributeByIdWithValue("attributeNameInData", "attributeValue");
		restDataGroup.addAttributeByIdWithValue("attributeNameInData2", "attributeValue2");

		DataToJsonConverter dataToJsonConverter = dataToJsonConverterFactory
				.createForRestDataElement(factory, restDataGroup);
		String json = dataToJsonConverter.toJson();

		assertEquals(json,
				"{\"name\":\"groupNameInData\",\"attributes\":{"
						+ "\"attributeNameInData2\":\"attributeValue2\","
						+ "\"attributeNameInData\":\"attributeValue\"" + "}}");
	}

	@Test
	public void testToJsonGroupWithAtomicChild() {
		restDataGroup
				.addChild(RestDataAtomic.withNameInDataAndValue("atomicNameInData", "atomicValue"));

		DataToJsonConverter dataToJsonConverter = dataToJsonConverterFactory
				.createForRestDataElement(factory, restDataGroup);
		String json = dataToJsonConverter.toJson();

		assertEquals(json,
				"{\"children\":[{\"name\":\"atomicNameInData\",\"value\":\"atomicValue\"}],\"name\":\"groupNameInData\"}");
	}

	@Test
	public void testToJsonGroupWithAtomicChildAndGroupChildWithAtomicChild() {
		restDataGroup
				.addChild(RestDataAtomic.withNameInDataAndValue("atomicNameInData", "atomicValue"));

		RestDataGroup restDataGroup2 = RestDataGroup.withNameInData("groupNameInData2");
		restDataGroup.addChild(restDataGroup2);

		restDataGroup2.addChild(
				RestDataAtomic.withNameInDataAndValue("atomicNameInData2", "atomicValue2"));

		DataToJsonConverter dataToJsonConverter = dataToJsonConverterFactory
				.createForRestDataElement(factory, restDataGroup);
		String json = dataToJsonConverter.toJson();

		String expectedJson = "{";
		expectedJson += "\"children\":[";
		expectedJson += "{\"name\":\"atomicNameInData\",\"value\":\"atomicValue\"},";
		expectedJson += "{\"children\":[{\"name\":\"atomicNameInData2\",\"value\":\"atomicValue2\"}]";
		expectedJson += ",\"name\":\"groupNameInData2\"}]";
		expectedJson += ",\"name\":\"groupNameInData\"}";

		assertEquals(json, expectedJson);
	}

	@Test
	public void testToJsonGroupWithAttributesAndAtomicChildAndGroupChildWithAtomicChild() {
		restDataGroup.addAttributeByIdWithValue("attributeNameInData", "attributeValue");
		restDataGroup.addAttributeByIdWithValue("attributeNameInData2", "attributeValue2");

		RestDataGroup recordInfo = RestDataGroup.withNameInData("recordInfo");
		recordInfo.addChild(RestDataAtomic.withNameInDataAndValue("id", "place:0001"));
		RestDataGroup type = RestDataGroup.withNameInData("type");
		type.addChild(RestDataAtomic.withNameInDataAndValue("linkedRecordType", "recordType"));
		type.addChild(RestDataAtomic.withNameInDataAndValue("linkedRecordId", "place"));
		recordInfo.addChild(type);

		recordInfo.addChild(RestDataAtomic.withNameInDataAndValue("createdBy", "userId"));
		restDataGroup.addChild(recordInfo);

		restDataGroup
				.addChild(RestDataAtomic.withNameInDataAndValue("atomicNameInData", "atomicValue"));

		RestDataGroup dataGroup2 = RestDataGroup.withNameInData("groupNameInData2");
		dataGroup2.addAttributeByIdWithValue("g2AttributeNameInData", "g2AttributeValue");
		restDataGroup.addChild(dataGroup2);

		dataGroup2.addChild(
				RestDataAtomic.withNameInDataAndValue("atomicNameInData2", "atomicValue2"));

		DataToJsonConverter dataToJsonConverter = dataToJsonConverterFactory
				.createForRestDataElement(factory, restDataGroup);
		String json = dataToJsonConverter.toJson();
		String expectedJson = "{\"children\":[{\"children\":[{\"name\":\"id\",\"value\":\"place:0001\"},"
				+ "{\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"recordType\"},"
				+ "{\"name\":\"linkedRecordId\",\"value\":\"place\"}],\"name\":\"type\"},"
				+ "{\"name\":\"createdBy\",\"value\":\"userId\"}],\"name\":\"recordInfo\"},"
				+ "{\"name\":\"atomicNameInData\",\"value\":\"atomicValue\"},{\"children\":["
				+ "{\"name\":\"atomicNameInData2\",\"value\":\"atomicValue2\"}],"
				+ "\"name\":\"groupNameInData2\",\"attributes\":{\"g2AttributeNameInData\":\"g2AttributeValue\"}}],"
				+ "\"name\":\"groupNameInData\",\"attributes\":{\"attributeNameInData2\":\"attributeValue2\","
				+ "\"attributeNameInData\":\"attributeValue\"}}";

		assertEquals(json, expectedJson);
	}

}
