/*
 * Copyright 2019 Uppsala University Library
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

package se.uu.ub.cora.therest.converter.jsontorest;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import se.uu.ub.cora.json.parser.JsonObject;
import se.uu.ub.cora.json.parser.JsonParseException;
import se.uu.ub.cora.json.parser.JsonValue;
import se.uu.ub.cora.json.parser.org.OrgJsonParser;
import se.uu.ub.cora.therest.data.RestDataAtomic;
import se.uu.ub.cora.therest.data.RestDataElement;
import se.uu.ub.cora.therest.data.RestDataGroup;
import se.uu.ub.cora.therest.data.RestDataRecordLink;

public class JsonDataRecordLinkToRestConverterTest {
	@Test
	public void testToClass() {
		String json = "{\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"system\"},{\"name\":\"linkedRecordId\",\"value\":\"testSystem\"}],\"name\":\"dataDivider\"}";
		RestDataRecordLink restDataRecordLink = createRestDataRecordLinkForJsonString(json);
		assertEquals(restDataRecordLink.getNameInData(), "dataDivider");
	}

	private RestDataRecordLink createRestDataRecordLinkForJsonString(String json) {
		OrgJsonParser jsonParser = new OrgJsonParser();
		JsonValue jsonValue = jsonParser.parseString(json);
		JsonToRestConverter jsonToDataConverter = JsonDataRecordLinkToRestConverter
				.forJsonObject((JsonObject) jsonValue);
		RestDataElement restDataElement = jsonToDataConverter.toInstance();
		RestDataRecordLink restDataRecordLink = (RestDataRecordLink) restDataElement;
		return restDataRecordLink;
	}

	@Test
	public void testToInstanceWithLinkedRepeatId() {
		String json = "{\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"recordType\"},{\"name\":\"linkedRecordId\",\"value\":\"place\"},{\"name\":\"linkedRepeatId\",\"value\":\"one\"}],\"name\":\"someLink\"}";
		RestDataRecordLink dataLink = createRestDataRecordLinkForJsonString(json);
		assertEquals(dataLink.getNameInData(), "someLink");

		RestDataAtomic linkedRecordType = (RestDataAtomic) dataLink
				.getFirstChildWithNameInData("linkedRecordType");
		assertEquals(linkedRecordType.getValue(), "recordType");

		RestDataAtomic linkedRecordId = (RestDataAtomic) dataLink
				.getFirstChildWithNameInData("linkedRecordId");
		assertEquals(linkedRecordId.getValue(), "place");

		RestDataAtomic linkedRepeatId = (RestDataAtomic) dataLink
				.getFirstChildWithNameInData("linkedRepeatId");
		assertEquals(linkedRepeatId.getValue(), "one");

	}

	@Test
	public void testToClassWithRepeatId() {
		String json = "{\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"system\"},{\"name\":\"linkedRecordId\",\"value\":\"testSystem\"}],\"repeatId\":\"3\",\"name\":\"dataDivider\"}";
		RestDataGroup restDataRecordLink = createRestDataRecordLinkForJsonString(json);
		assertEquals(restDataRecordLink.getNameInData(), "dataDivider");
		assertEquals(restDataRecordLink.getRepeatId(), "3");
	}

	@Test
	public void testToClassWithAttribute() {
		String json = "{\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"system\"},{\"name\":\"linkedRecordId\",\"value\":\"testSystem\"}],\"attributes\":{\"type\":\"alternative\"},\"name\":\"dataDivider\"}";
		RestDataGroup restDataRecordLink = createRestDataRecordLinkForJsonString(json);
		assertEquals(restDataRecordLink.getNameInData(), "dataDivider");
		String attributeValue = restDataRecordLink.getAttributes().get("type");
		assertEquals(attributeValue, "alternative");
	}

	@Test
	public void testToClassWithRepeatIdAndAttribute() {
		String json = "{\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"system\"},{\"name\":\"linkedRecordId\",\"value\":\"testSystem\"}],\"repeatId\":\"3\",\"attributes\":{\"type\":\"alternative\"},\"name\":\"dataDivider\"}";
		RestDataRecordLink restDataRecordLink = createRestDataRecordLinkForJsonString(json);
		assertEquals(restDataRecordLink.getNameInData(), "dataDivider");
		String attributeValue = restDataRecordLink.getAttributes().get("type");
		assertEquals(attributeValue, "alternative");
		assertEquals(restDataRecordLink.getRepeatId(), "3");
	}

	@Test(expectedExceptions = JsonParseException.class)
	public void testToClassWithRepeatIdAndAttributeAndExtra() {
		String json = "{\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"system\"},"
				+ "{\"name\":\"linkedRecordId\",\"value\":\"testSystem\"}],\"repeatId\":\"3\",\"extraKey\":\"extra\",\"attributes\":{\"type\":\"alternative\"},\"name\":\"dataDivider\"}";
		createRestDataRecordLinkForJsonString(json);
	}

	@Test(expectedExceptions = JsonParseException.class)
	public void testToClassWithRepeatIdMissingAttribute() {
		String json = "{\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"system\"},"
				+ "{\"name\":\"linkedRecordId\",\"value\":\"testSystem\"}],\"repeatId\":\"3\",\"NOTattributes\":{\"type\":\"alternative\"},\"name\":\"dataDivider\"}";
		createRestDataRecordLinkForJsonString(json);
	}

	@Test
	public void testToClassWithAttributes() {
		String json = "{\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"system\"},{\"name\":\"linkedRecordId\",\"value\":\"testSystem\"}],\"attributes\":{\"type\":\"alternative\",\"attribute2\":\"someOtherAttribute\"},\"name\":\"dataDivider\"}";

		RestDataRecordLink restDataRecordLink = createRestDataRecordLinkForJsonString(json);
		assertEquals(restDataRecordLink.getNameInData(), "dataDivider");
		String attributeValue = restDataRecordLink.getAttributes().get("type");
		assertEquals(attributeValue, "alternative");
		String attributeValue2 = restDataRecordLink.getAttributes().get("attribute2");
		assertEquals(attributeValue2, "someOtherAttribute");
	}

	@Test
	public void testToClassWithCorrectChildren() {
		String json = "{\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"system\"},{\"name\":\"linkedRecordId\",\"value\":\"testSystem\"}],\"repeatId\":\"3\",\"name\":\"dataDivider\"}";

		RestDataRecordLink restDataRecordLink = createRestDataRecordLinkForJsonString(json);
		assertEquals(restDataRecordLink.getNameInData(), "dataDivider");
		RestDataAtomic linkedRecordType = (RestDataAtomic) restDataRecordLink
				.getFirstChildWithNameInData("linkedRecordType");
		assertEquals(linkedRecordType.getValue(), "system");

		RestDataAtomic linkedRecordId = (RestDataAtomic) restDataRecordLink
				.getFirstChildWithNameInData("linkedRecordId");
		assertEquals(linkedRecordId.getValue(), "testSystem");

	}

	@Test(expectedExceptions = JsonParseException.class)
	public void testToClassTooManyChildren() {
		String json = "{\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"system\"},{\"name\":\"linkedRecordId\",\"value\":\"testSystem\"},{\"name\":\"someOtherChild\",\"value\":\"extraChild\"}],\"name\":\"dataDivider\"}";
		createRestDataRecordLinkForJsonString(json);
	}

	@Test(expectedExceptions = JsonParseException.class)
	public void testToClassNoLinkedRecordType() {
		String json = "{\"children\":[{\"name\":\"NOTlinkedRecordType\",\"value\":\"system\"},{\"name\":\"linkedRecordId\",\"value\":\"testSystem\"}],\"name\":\"dataDivider\"}";
		createRestDataRecordLinkForJsonString(json);
	}

	@Test(expectedExceptions = JsonParseException.class)
	public void testToClassNoLinkedRecordId() {
		String json = "{\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"system\"},{\"name\":\"NOTlinkedRecordId\",\"value\":\"testSystem\"}],\"name\":\"dataDivider\"}";
		createRestDataRecordLinkForJsonString(json);
	}

	@Test(expectedExceptions = JsonParseException.class)
	public void testToClassWrongJsonTopLevelNoName() {
		String json = "{\"children\":[],\"extra\":{\"id2\":\"value2\"}}";
		createRestDataRecordLinkForJsonString(json);
	}

	@Test(expectedExceptions = JsonParseException.class)
	public void testToClassWrongJsonTopLevelNoChildren() {
		String json = "{\"name\":\"id\",\"attributes\":{}}";
		createRestDataRecordLinkForJsonString(json);
	}

	@Test(expectedExceptions = JsonParseException.class)
	public void testToClassWrongJsonKeyTopLevel() {
		String json = "{\"name\":\"id\",\"children\":[],\"extra\":{\"id2\":\"value2\"}}";
		createRestDataRecordLinkForJsonString(json);
	}

	@Test(expectedExceptions = JsonParseException.class)
	public void testToClassWrongJsonKeyTopLevelWithAttributes() {
		String json = "{\"name\":\"id\",\"children\":[], \"attributes\":{},\"extra\":{\"id2\":\"value2\"}}";
		createRestDataRecordLinkForJsonString(json);
	}

	@Test(expectedExceptions = JsonParseException.class)
	public void testToClassWrongJsonAttributesIsObject() {
		String json = "{\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"system\"},{\"name\":\"linkedRecordId\",\"value\":\"testSystem\"}],\"attributes\":{\"type\":{}},\"name\":\"dataDivider\"}";
		createRestDataRecordLinkForJsonString(json);
	}

	@Test(expectedExceptions = JsonParseException.class)
	public void testToClassWrongJsonOneAttributesIsArray() {
		String json = "{\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"system\"},{\"name\":\"linkedRecordId\",\"value\":\"testSystem\"}],\"attributes\":{\"type\":[true]},\"name\":\"dataDivider\"}";
		createRestDataRecordLinkForJsonString(json);
	}

	@Test(expectedExceptions = JsonParseException.class, expectedExceptionsMessageRegExp = ""
			+ "RecordLinkData must contain children with name linkedRecordType and linkedRecordId "
			+ "and might contain child with name linkedRepeatId")
	public void testToClassWithNoLinkedRecordType() {
		String json = "{\"children\":[{\"name\":\"linkedRecordId\",\"value\":\"place\"}],\"name\":\"someLink\"}";
		createRestDataRecordLinkForJsonString(json);
	}

	@Test(expectedExceptions = JsonParseException.class, expectedExceptionsMessageRegExp = ""
			+ "RecordLinkData must contain children with name linkedRecordType and linkedRecordId "
			+ "and might contain child with name linkedRepeatId")
	public void testToClassWithNoLinkedRecordTypeButOtherChild() {
		String json = "{\"children\":[{\"name\":\"NOTlinkedRecordType\",\"value\":\"recordType\"},{\"name\":\"linkedRecordId\",\"value\":\"place\"}],\"name\":\"someLink\"}";
		createRestDataRecordLinkForJsonString(json);
	}

	@Test(expectedExceptions = JsonParseException.class, expectedExceptionsMessageRegExp = ""
			+ "RecordLinkData must contain children with name linkedRecordType and linkedRecordId "
			+ "and might contain child with name linkedRepeatId")
	public void testToClassWithNoLinkedRecordId() {
		String json = "{\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"recordType\"}],\"name\":\"someLink\"}";
		createRestDataRecordLinkForJsonString(json);
	}

	@Test(expectedExceptions = JsonParseException.class, expectedExceptionsMessageRegExp = ""
			+ "RecordLinkData must contain children with name linkedRecordType and linkedRecordId "
			+ "and might contain child with name linkedRepeatId")
	public void testToClassWithNoLinkedRecordIdButOtherChild() {
		String json = "{\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"recordType\"},{\"name\":\"NOTlinkedRecordId\",\"value\":\"place\"}],\"name\":\"someLink\"}";
		createRestDataRecordLinkForJsonString(json);
	}

	@Test(expectedExceptions = JsonParseException.class, expectedExceptionsMessageRegExp = ""
			+ "RecordLinkData must contain children with name linkedRecordType and linkedRecordId "
			+ "and might contain child with name linkedRepeatId")
	public void testToClassWithTooManyChildren() {
		String json = "{\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"recordType\"},{\"name\":\"linkedRecordId\",\"value\":\"place\"},{\"name\":\"linkedRepeatId\",\"value\":\"one\"},{\"name\":\"someExtra\",\"value\":\"one\"}],\"name\":\"someLink\"}";
		createRestDataRecordLinkForJsonString(json);
	}

	@Test(expectedExceptions = JsonParseException.class, expectedExceptionsMessageRegExp = ""
			+ "RecordLinkData must contain children with name linkedRecordType and linkedRecordId "
			+ "and might contain child with name linkedRepeatId")
	public void testToClassWithMaxNumberOfChildrenButNoLinkedRepeatId() {
		String json = "{\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"recordType\"},{\"name\":\"linkedRecordId\",\"value\":\"place\"},{\"name\":\"someExtra\",\"value\":\"one\"}],\"name\":\"someLink\"}";
		createRestDataRecordLinkForJsonString(json);
	}
}
