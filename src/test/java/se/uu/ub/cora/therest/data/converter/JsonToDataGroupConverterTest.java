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

import java.util.Iterator;

import org.testng.annotations.Test;

import se.uu.ub.cora.json.parser.JsonObject;
import se.uu.ub.cora.json.parser.JsonParseException;
import se.uu.ub.cora.json.parser.JsonValue;
import se.uu.ub.cora.json.parser.org.OrgJsonParser;
import se.uu.ub.cora.therest.data.RestDataAtomic;
import se.uu.ub.cora.therest.data.RestDataElement;
import se.uu.ub.cora.therest.data.RestDataGroup;

public class JsonToDataGroupConverterTest {
	@Test
	public void testToClass() {
		String json = "{\"name\":\"groupNameInData\", \"children\":[]}";
		RestDataGroup restDataGroup = createRestDataGroupForJsonString(json);
		assertEquals(restDataGroup.getNameInData(), "groupNameInData");
	}

	private RestDataGroup createRestDataGroupForJsonString(String json) {
		OrgJsonParser jsonParser = new OrgJsonParser();
		JsonValue jsonValue = jsonParser.parseString(json);
		JsonToDataConverter jsonToDataConverter = JsonToDataGroupConverter
				.forJsonObject((JsonObject) jsonValue);
		RestDataElement restDataElement = jsonToDataConverter.toInstance();
		RestDataGroup restDataGroup = (RestDataGroup) restDataElement;
		return restDataGroup;
	}

	@Test
	public void testToClassWithRepeatId() {
		String json = "{\"name\":\"groupNameInData\", \"children\":[],\"repeatId\":\"3\"}";
		RestDataGroup restDataGroup = createRestDataGroupForJsonString(json);
		assertEquals(restDataGroup.getNameInData(), "groupNameInData");
		assertEquals(restDataGroup.getRepeatId(), "3");
	}

	@Test
	public void testToClassWithAttribute() {
		String json = "{\"name\":\"groupNameInData\",\"attributes\":{\"attributeNameInData\":\"attributeValue\"}, \"children\":[]}";
		RestDataGroup restDataGroup = createRestDataGroupForJsonString(json);
		assertEquals(restDataGroup.getNameInData(), "groupNameInData");
		String attributeValue = restDataGroup.getAttributes().get("attributeNameInData");
		assertEquals(attributeValue, "attributeValue");
	}

	@Test
	public void testToClassWithRepeatIdAndAttribute() {
		String json = "{\"name\":\"groupNameInData\", \"children\":[],\"repeatId\":\"3\""
				+ ",\"attributes\":{\"attributeNameInData\":\"attributeValue\"}}";
		RestDataGroup restDataGroup = createRestDataGroupForJsonString(json);
		assertEquals(restDataGroup.getNameInData(), "groupNameInData");
		String attributeValue = restDataGroup.getAttributes().get("attributeNameInData");
		assertEquals(attributeValue, "attributeValue");
		assertEquals(restDataGroup.getRepeatId(), "3");
	}

	@Test(expectedExceptions = JsonParseException.class)
	public void testToClassWithRepeatIdAndAttributeAndExtra() {
		String json = "{\"name\":\"groupNameInData\", \"children\":[],\"repeatId\":\"3\""
				+ ",\"attributes\":{\"attributeNameInData\":\"attributeValue\"}"
				+ ",\"extraKey\":\"extra\"}";
		createRestDataGroupForJsonString(json);
	}

	@Test(expectedExceptions = JsonParseException.class)
	public void testToClassWithRepeatIdMissingAttribute() {
		String json = "{\"name\":\"groupNameInData\", \"children\":[],\"repeatId\":\"3\""
				+ ",\"NOTattributes\":{\"attributeNameInData\":\"attributeValue\"}}";
		createRestDataGroupForJsonString(json);
	}

	@Test
	public void testToClassWithAttributes() {
		String json = "{\"name\":\"groupNameInData\",\"attributes\":{"
				+ "\"attributeNameInData\":\"attributeValue\","
				+ "\"attributeNameInData2\":\"attributeValue2\"" + "},\"children\":[]}";

		RestDataGroup restDataGroup = createRestDataGroupForJsonString(json);
		assertEquals(restDataGroup.getNameInData(), "groupNameInData");
		String attributeValue = restDataGroup.getAttributes().get("attributeNameInData");
		assertEquals(attributeValue, "attributeValue");
		String attributeValue2 = restDataGroup.getAttributes().get("attributeNameInData2");
		assertEquals(attributeValue2, "attributeValue2");
	}

	@Test
	public void testToClassWithAtomicChild() {
		String json = "{\"name\":\"groupNameInData\","
				+ "\"children\":[{\"name\":\"atomicNameInData\",\"value\":\"atomicValue\"}]}";

		RestDataGroup restDataGroup = createRestDataGroupForJsonString(json);
		assertEquals(restDataGroup.getNameInData(), "groupNameInData");
		RestDataAtomic child = (RestDataAtomic) restDataGroup.getChildren().iterator().next();
		assertEquals(child.getNameInData(), "atomicNameInData");
		assertEquals(child.getValue(), "atomicValue");
	}

	@Test
	public void testToClassGroupWithAtomicChildAndGroupChildWithAtomicChild() {
		String json = "{";
		json += "\"name\":\"groupNameInData\",";
		json += "\"children\":[";
		json += "{\"name\":\"atomicNameInData\",\"value\":\"atomicValue\"},";
		json += "{\"name\":\"groupNameInData2\","
				+ "\"children\":[{\"name\":\"atomicNameInData2\",\"value\":\"atomicValue2\"}]}";
		json += "]";
		json += "}";

		RestDataGroup restDataGroup = createRestDataGroupForJsonString(json);
		assertEquals(restDataGroup.getNameInData(), "groupNameInData");
		Iterator<RestDataElement> iterator = restDataGroup.getChildren().iterator();
		RestDataAtomic child = (RestDataAtomic) iterator.next();
		assertEquals(child.getNameInData(), "atomicNameInData");
		assertEquals(child.getValue(), "atomicValue");
		RestDataGroup child2 = (RestDataGroup) iterator.next();
		assertEquals(child2.getNameInData(), "groupNameInData2");
		RestDataAtomic subChild = (RestDataAtomic) child2.getChildren().iterator().next();
		assertEquals(subChild.getNameInData(), "atomicNameInData2");
		assertEquals(subChild.getValue(), "atomicValue2");
	}

	@Test
	public void testToClassGroupWithAttributesAndAtomicChildAndGroupChildWithAtomicChild() {
		String json = "{";
		json += "\"name\":\"groupNameInData\",";
		json += "\"attributes\":{" + "\"attributeNameInData\":\"attributeValue\","
				+ "\"attributeNameInData2\":\"attributeValue2\"" + "},";
		json += "\"children\":[";
		json += "{\"name\":\"atomicNameInData\",\"value\":\"atomicValue\"},";
		json += "{\"name\":\"groupNameInData2\",";
		json += "\"attributes\":{\"g2AttributeNameInData\":\"g2AttributeValue\"},";
		json += "\"children\":[{\"name\":\"atomicNameInData2\",\"value\":\"atomicValue2\"}]}";
		json += "]";
		json += "}";

		RestDataGroup restDataGroup = createRestDataGroupForJsonString(json);
		assertEquals(restDataGroup.getNameInData(), "groupNameInData");

		String attributeValue2 = restDataGroup.getAttributes().get("attributeNameInData");
		assertEquals(attributeValue2, "attributeValue");

		Iterator<RestDataElement> iterator = restDataGroup.getChildren().iterator();
		RestDataAtomic child = (RestDataAtomic) iterator.next();
		assertEquals(child.getNameInData(), "atomicNameInData");
		assertEquals(child.getValue(), "atomicValue");
		RestDataGroup child2 = (RestDataGroup) iterator.next();
		assertEquals(child2.getNameInData(), "groupNameInData2");
		RestDataAtomic subChild = (RestDataAtomic) child2.getChildren().iterator().next();
		assertEquals(subChild.getNameInData(), "atomicNameInData2");
		assertEquals(subChild.getValue(), "atomicValue2");

		String attributeValue = child2.getAttributes().get("g2AttributeNameInData");
		assertEquals(attributeValue, "g2AttributeValue");
	}

	@Test(expectedExceptions = JsonParseException.class)
	public void testToClassWrongJsonTopLevelNoName() {
		String json = "{\"children\":[],\"extra\":{\"id2\":\"value2\"}}";
		createRestDataGroupForJsonString(json);
	}

	@Test(expectedExceptions = JsonParseException.class)
	public void testToClassWrongJsonTopLevelNoChildren() {
		String json = "{\"name\":\"id\",\"attributes\":{}}";
		createRestDataGroupForJsonString(json);
	}

	@Test(expectedExceptions = JsonParseException.class)
	public void testToClassWrongJsonKeyTopLevel() {
		String json = "{\"name\":\"id\",\"children\":[],\"extra\":{\"id2\":\"value2\"}}";
		createRestDataGroupForJsonString(json);
	}

	@Test(expectedExceptions = JsonParseException.class)
	public void testToClassWrongJsonKeyTopLevelWithAttributes() {
		String json = "{\"name\":\"id\",\"children\":[], \"attributes\":{},\"extra\":{\"id2\":\"value2\"}}";
		createRestDataGroupForJsonString(json);
	}

	@Test(expectedExceptions = JsonParseException.class)
	public void testToClassWrongJsonAttributesIsGroup() {
		String json = "{\"name\":\"groupNameInData\", \"attributes\":{\"attributeNameInData\":\"attributeValue\",\"bla\":{} }}";
		createRestDataGroupForJsonString(json);
	}

	@Test(expectedExceptions = JsonParseException.class)
	public void testToClassWrongJsonTwoAttributes() {
		String json = "{\"name\":\"groupNameInData\",\"children\":[],\"attributes\":{\"attributeNameInData\":\"attributeValue\"}"
				+ ",\"attributes\":{\"attributeNameInData2\":\"attributeValue2\"}}";
		createRestDataGroupForJsonString(json);
	}

	@Test(expectedExceptions = JsonParseException.class)
	public void testToClassWrongJsonOneAttributesIsArray() {
		String json = "{\"name\":\"groupNameInData\",\"children\":[],\"attributes\":{\"attributeNameInData\":\"attributeValue\",\"bla\":[true] }}";
		createRestDataGroupForJsonString(json);
	}

	@Test(expectedExceptions = JsonParseException.class)
	public void testToClassWrongJsonAttributesIsArray() {
		String json = "{\"name\":\"groupNameInData\",\"children\":[],\"attributes\":[{\"attributeNameInData\":\"attributeValue\"}]}";
		createRestDataGroupForJsonString(json);
	}

	@Test(expectedExceptions = JsonParseException.class)
	public void testToClassWrongJsonOneChildIsArray() {
		String json = "{\"name\":\"groupNameInData\",\"children\":[{\"atomicNameInData\":\"atomicValue\"},[]]}";
		createRestDataGroupForJsonString(json);
	}

	@Test(expectedExceptions = JsonParseException.class)
	public void testToClassWrongJsonOneChildIsString() {
		String json = "{\"name\":\"groupNameInData\",\"children\":[{\"atomicNameInData\":\"atomicValue\"},\"string\"]}";
		createRestDataGroupForJsonString(json);
	}

	@Test(expectedExceptions = JsonParseException.class)
	public void testToClassWrongJsonChildrenIsNotCorrectObject() {
		String json = "{\"name\":\"groupNameInData\",\"children\":[{\"atomicNameInData\":\"atomicValue\""
				+ ",\"atomicNameInData2\":\"atomicValue2\"}]}";
		createRestDataGroupForJsonString(json);
	}
}
