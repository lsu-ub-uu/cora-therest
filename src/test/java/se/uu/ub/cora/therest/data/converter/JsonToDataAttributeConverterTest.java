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

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.json.parser.JsonObject;
import se.uu.ub.cora.json.parser.JsonParseException;
import se.uu.ub.cora.json.parser.JsonParser;
import se.uu.ub.cora.json.parser.JsonValue;
import se.uu.ub.cora.json.parser.org.OrgJsonParser;
import se.uu.ub.cora.therest.data.RestDataAttribute;
import se.uu.ub.cora.therest.data.RestDataElement;

public class JsonToDataAttributeConverterTest {
	private JsonParser jsonParser;

	@BeforeMethod
	public void beforeMethod() {
		jsonParser = new OrgJsonParser();
	}

	@Test
	public void testToClass() {
		String json = "{\"attributeNameInData\":\"attributeValue\"}";
		RestDataAttribute restDataAttribute = createRestDataAttributeForJsonString(json);
		Assert.assertEquals(restDataAttribute.getNameInData(), "attributeNameInData");
		Assert.assertEquals(restDataAttribute.getValue(), "attributeValue");
	}

	private RestDataAttribute createRestDataAttributeForJsonString(String json) {
		JsonValue jsonValue = jsonParser.parseString(json);
		JsonToDataConverter jsonToDataConverter = JsonToDataAttributeConverter
				.forJsonObject((JsonObject) jsonValue);
		RestDataElement restDataElement = jsonToDataConverter.toInstance();
		RestDataAttribute restDataAttribute = (RestDataAttribute) restDataElement;
		return restDataAttribute;
	}

	@Test
	public void testToClassEmptyValue() {
		String json = "{\"attributeNameInData\":\"\"}";
		RestDataAttribute restDataAttribute = createRestDataAttributeForJsonString(json);
		Assert.assertEquals(restDataAttribute.getNameInData(), "attributeNameInData");
		Assert.assertEquals(restDataAttribute.getValue(), "");
	}

	@Test(expectedExceptions = JsonParseException.class)
	public void testToClassWrongJson() {
		String json = "{\"id\":[]}";

		JsonValue jsonValue = jsonParser.parseString(json);
		JsonToDataConverter jsonToDataConverter = JsonToDataAttributeConverter
				.forJsonObject((JsonObject) jsonValue);
		jsonToDataConverter.toInstance();
	}

	@Test(expectedExceptions = JsonParseException.class)
	public void testToClassWrongJsonExtraKeyValuePair() {
		String json = "{\"attributeNameInData\":\"attributeValue\",\"id2\":\"value2\"}";
		JsonValue jsonValue = jsonParser.parseString(json);
		JsonToDataConverter jsonToDataConverter = JsonToDataAttributeConverter
				.forJsonObject((JsonObject) jsonValue);
		jsonToDataConverter.toInstance();
	}

	@Test(expectedExceptions = JsonParseException.class)
	public void testToClassWrongJsonExtraArray() {
		String json = "{\"attributeNameInData\":\"attributeValue\",\"id2\":[]}";
		JsonValue jsonValue = jsonParser.parseString(json);
		JsonToDataConverter jsonToDataConverter = JsonToDataAttributeConverter
				.forJsonObject((JsonObject) jsonValue);
		jsonToDataConverter.toInstance();
	}

}
