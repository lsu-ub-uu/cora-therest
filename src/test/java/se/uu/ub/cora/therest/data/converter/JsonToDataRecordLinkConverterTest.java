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

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import se.uu.ub.cora.therest.data.RestDataAtomic;
import se.uu.ub.cora.therest.data.RestDataElement;
import se.uu.ub.cora.therest.data.RestDataRecordLink;
import se.uu.ub.cora.therest.json.parser.JsonObject;
import se.uu.ub.cora.therest.json.parser.JsonParseException;
import se.uu.ub.cora.therest.json.parser.JsonValue;
import se.uu.ub.cora.therest.json.parser.org.OrgJsonParser;

import static org.testng.Assert.assertEquals;

public class JsonToDataRecordLinkConverterTest {
	private OrgJsonParser jsonParser;

	@BeforeMethod
	public void beforeMethod() {
		jsonParser = new OrgJsonParser();
	}

	@Test
	public void testToClass() {
		String json = "{\"name\":\"nameInData\", \"children\":["  +
				"{\"name\":\"linkedRecordId\",\"value\":\"bush1\"}]}";
		RestDataRecordLink restDataRecordLink = createRestDataRecordLinkForJsonString(json);
		assertEquals(restDataRecordLink.getNameInData(), "nameInData");
	}

	private RestDataRecordLink createRestDataRecordLinkForJsonString(String json) {
		JsonValue jsonValue = jsonParser.parseString(json);
		JsonToDataConverter jsonToDataConverter = JsonToDataRecordLinkConverter
				.forJsonObject((JsonObject) jsonValue);
		RestDataElement restDataElement = jsonToDataConverter.toInstance();
		RestDataRecordLink restDataRecordLink = (RestDataRecordLink) restDataElement;
		return restDataRecordLink;
	}

	@Test
	public void testToClassWithRepeatId() {
		String json = "{\"name\":\"nameInData\",\"repeatId\":\"7\", \"children\":["  +
				"{\"name\":\"linkedRecordId\",\"value\":\"aRecordId\"}]}";
		RestDataRecordLink restDataRecordLink = createRestDataRecordLinkForJsonString(json);
		assertEquals(restDataRecordLink.getNameInData(), "nameInData");
		assertEquals(restDataRecordLink.getRepeatId(), "7");
	}

	@Test
	public void testToClassWithLinkedRepeatId(){
		String json = "{\"name\":\"nameInData\", \"children\":["  +
				"{\"name\":\"linkedRecordId\",\"value\":\"aRecordId\"}," +
				"{\"name\":\"linkedRepeatId\",\"value\":\"linkedOne\"}]}";
		
		RestDataRecordLink restDataRecordLink = createRestDataRecordLinkForJsonString(json);

		RestDataAtomic linkedRepeatId = (RestDataAtomic) restDataRecordLink.getFirstChildWithNameInData("linkedRepeatId");
		assertEquals(linkedRepeatId.getValue(), "linkedOne");
	}

	@Test
	public void testToClassAnActionLink() {
		String json = "{\"name\":\"nameInData\", \"children\":["  +
				"{\"name\":\"linkedRecordId\",\"value\":\"aRecordId\"}" +
				"],\"actionLinks\":\"someActionLink\"}";

		RestDataRecordLink restDataRecordLink = createRestDataRecordLinkForJsonString(json);
		assertEquals(restDataRecordLink.getNameInData(), "nameInData");
	}

	@Test
	public void testToClassWithRepeatIdAndActionLink() {
		String json = "{\"name\":\"nameInData\", \"children\":["  +
				"{\"name\":\"linkedRecordId\",\"value\":\"aRecordId\"}" +
				"],\"actionLinks\":\"someActionLink\",\"repeatId\":\"7\"}";

		RestDataRecordLink restDataRecordLink = createRestDataRecordLinkForJsonString(json);
		assertEquals(restDataRecordLink.getNameInData(), "nameInData");
		assertEquals(restDataRecordLink.getRepeatId(), "7");
	}

	@Test(expectedExceptions = JsonParseException.class)
	public void testToClassWithLinkedRecordTypeNotAllowed() {
		String json = "{\"name\":\"nameInData\", \"children\":["  +
				"{\"name\":\"linkedRecordId\",\"value\":\"aRecordId\"}," +
				"{\"name\":\"linkedRecordType\",\"value\":\"aRecordType\"}" +
				"],\"actionLinks\":\"someActionLink\",\"repeatId\":\"7\"}";

		createRestDataRecordLinkForJsonString(json);
	}

	@Test(expectedExceptions = JsonParseException.class)
	public void testToClassWithLinkedPathNotAllowed() {
		
		String json = "{\"name\":\"nameInData\", \"children\":["  +
				"{\"name\":\"linkedRecordId\",\"value\":\"aRecordId\"}," +
				"{\"linkedPath\":{\"name\":\"someLinkedPath\"}}," +
				"],\"actionLinks\":\"someActionLink\",\"repeatId\":\"7\"}";

		createRestDataRecordLinkForJsonString(json);
	}

	@Test(expectedExceptions = JsonParseException.class)
	public void testToClassMaxNoOfKeysButRepeatIdMissing() {
		String json = "{\"name\":\"nameInData\", \"children\":["  +
				"{\"name\":\"linkedRecordId\",\"value\":\"aRecordId\"}" +
				"],\"actionLinks\":\"someActionLink\",\"NOTrepeatId\":\"7\"}";

		createRestDataRecordLinkForJsonString(json);
	}

	@Test(expectedExceptions = JsonParseException.class)
	public void testToClassMaxNoOfKeysButActionLinksMissing() {
		String json = "{\"name\":\"nameInData\", \"children\":["  +
				"{\"name\":\"linkedRecordId\",\"value\":\"aRecordId\"}" +
				"],\"NOTactionLinks\":\"someActionLink\",\"repeatId\":\"7\"}";

		createRestDataRecordLinkForJsonString(json);
	}
	
	@Test(expectedExceptions = JsonParseException.class)
	public void testToClassMaxNoOfKeysButChildrenMissing() {
		String json = "{\"name\":\"nameInData\", \"NOTchildren\":["  +
				"{\"name\":\"linkedRecordId\",\"value\":\"aRecordId\"}" +
				"],\"actionLinks\":\"someActionLink\",\"repeatId\":\"7\"}";

		createRestDataRecordLinkForJsonString(json);
	}

	@Test(expectedExceptions = JsonParseException.class)
	public void testToClassMoreThanoneChildButLinkedRepeatIdMissing(){
		String json = "{\"name\":\"nameInData\", \"children\":["  +
				"{\"name\":\"linkedRecordId\",\"value\":\"aRecordId\"}," +
				"{\"name\":\"NOTlinkedRepeatId\",\"value\":\"one\"}" +
				"],\"actionLinks\":\"someActionLink\",\"repeatId\":\"7\"}";
		
		createRestDataRecordLinkForJsonString(json);
	}

	@Test(expectedExceptions = JsonParseException.class)
	public void testToClassMaxNoOfKeysButAllOptionalKeysMissing(){
		String json = "{\"name\":\"nameInData\", \"children\":["  +
				"{\"name\":\"linkedRecordId\",\"value\":\"aRecordId\"}" +
				"],\"NOTactionLinks\":\"someActionLink\",\"NOTrepeatId\":\"7\"}";

		createRestDataRecordLinkForJsonString(json);
	}

	@Test(expectedExceptions = JsonParseException.class)
	public void testToClassNoName() {
		String json = "{ \"children\":["  +
				"{\"name\":\"linkedRecordId\",\"value\":\"bush1\"}]}";
		createRestDataRecordLinkForJsonString(json);
	}

	@Test(expectedExceptions = JsonParseException.class)
	public void testToClassNoLinkedRecordId() {
		String json = "{\"name\":\"nameInData\"}";
		createRestDataRecordLinkForJsonString(json);
	}


//	@Test(expectedExceptions = JsonParseException.class)
//	public void testToClassNotAnActionLink() {
//		String json = "{\"linkedRecordId\":\"aRecordId\",\"linkedRecordType\":\"aRecordType\""
//				+ ",\"name\":\"nameInData\",\"notActionLinks\":\"somethingElse\"}";
//		createRestDataRecordLinkForJsonString(json);
//	}

//	@Test(expectedExceptions = JsonParseException.class)
//	public void testToClassTwoMoreKeysThanMandatoryButNoOptionalKeyPresent() {
//		String json = "{\"recordId\":\"aRecordId\",\"repeatIdWrong\":\"x\""
//				+ ",\"name\":\"nameInData\",\"notAnActionLink\":\"somethingElse\"}";
//		createRestDataRecordLinkForJsonString(json);
//	}

	@Test(expectedExceptions = JsonParseException.class)
	public void testToClassMoreKeysThanAllowed() {
		String json = "{\"name\":\"nameInData\", \"children\":["  +
				"{\"name\":\"linkedRecordId\",\"value\":\"aRecordId\"}" +
				"],\"actionLinks\":\"someActionLink\",\"repeatId\":\"7\", \"repeatId2\":\"7\"}";
		
		createRestDataRecordLinkForJsonString(json);
	}

}
