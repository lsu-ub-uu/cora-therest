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
import se.uu.ub.cora.therest.data.RestDataResourceLink;

public class JsonDataResourceLinkToRestConverterTest {

	@Test
	public void testToInstance() {
		String json = "{\"children\":[{\"name\":\"streamId\",\"value\":\"aStreamId\"},{\"name\":\"filename\",\"value\":\"aFilename\"},{\"name\":\"filesize\",\"value\":\"12345\"},{\"name\":\"mimeType\",\"value\":\"application/png\"}],\"name\":\"someResourceLink\"}";
		RestDataResourceLink resourceLink = getConverterdLink(json);
		assertEquals(resourceLink.getNameInData(), "someResourceLink");

		RestDataAtomic streamId = (RestDataAtomic) resourceLink
				.getFirstChildWithNameInData("streamId");
		RestDataAtomic filename = (RestDataAtomic) resourceLink
				.getFirstChildWithNameInData("filename");
		RestDataAtomic filesize = (RestDataAtomic) resourceLink
				.getFirstChildWithNameInData("filesize");
		RestDataAtomic mimeType = (RestDataAtomic) resourceLink
				.getFirstChildWithNameInData("mimeType");

		assertEquals(streamId.getValue(), "aStreamId");
		assertEquals(filename.getValue(), "aFilename");
		assertEquals(filesize.getValue(), "12345");
		assertEquals(mimeType.getValue(), "application/png");
	}

	private RestDataResourceLink getConverterdLink(String json) {
		OrgJsonParser jsonParser = new OrgJsonParser();
		JsonValue jsonValue = jsonParser.parseString(json);
		JsonDataResourceLinkToRestConverter converter = JsonDataResourceLinkToRestConverter
				.forJsonObject((JsonObject) jsonValue);

		RestDataResourceLink dataLink = (RestDataResourceLink) converter.toInstance();
		return dataLink;
	}

	@Test
	public void testToClassWithRepeatId() {
		String json = "{\"children\":[{\"name\":\"streamId\",\"value\":\"aStreamId\"},{\"name\":\"filename\",\"value\":\"aFilename\"},{\"name\":\"filesize\",\"value\":\"12345\"},{\"name\":\"mimeType\",\"value\":\"application/png\"}],\"repeatId\":\"0\",\"name\":\"someResourceLink\"}";
		RestDataResourceLink dataLink = getConverterdLink(json);
		assertEquals(dataLink.getNameInData(), "someResourceLink");
		assertEquals(dataLink.getRepeatId(), "0");
	}

	@Test
	public void testToClassWithAttribute() {
		String json = "{\"children\":[{\"name\":\"streamId\",\"value\":\"aStreamId\"},{\"name\":\"filename\",\"value\":\"aFilename\"},{\"name\":\"filesize\",\"value\":\"12345\"},{\"name\":\"mimeType\",\"value\":\"application/png\"}],\"attributes\":{\"type\":\"someType\"},\"name\":\"someResourceLink\"}";
		RestDataResourceLink dataLink = getConverterdLink(json);

		assertEquals(dataLink.getNameInData(), "someResourceLink");
		String attributeValue = dataLink.getAttributes().get("type");
		assertEquals(attributeValue, "someType");
	}

	@Test
	public void testToClassWithRepeatIdAndAttribute() {
		String json = "{\"children\":[{\"name\":\"streamId\",\"value\":\"aStreamId\"},{\"name\":\"filename\",\"value\":\"aFilename\"},{\"name\":\"filesize\",\"value\":\"12345\"},{\"name\":\"mimeType\",\"value\":\"application/png\"}],\"repeatId\":\"0\",\"attributes\":{\"type\":\"someType\"},\"name\":\"someResourceLink\"}";
		RestDataResourceLink dataLink = getConverterdLink(json);

		assertEquals(dataLink.getNameInData(), "someResourceLink");
		String attributeValue = dataLink.getAttributes().get("type");
		assertEquals(attributeValue, "someType");
		assertEquals(dataLink.getRepeatId(), "0");
	}

	@Test(expectedExceptions = JsonParseException.class, expectedExceptionsMessageRegExp = ""
			+ "Error parsing jsonObject: Group data can only contain keys: name, children and attributes")
	public void testToClassWithRepeatIdAndAttributeAndExtra() {
		String json = "{\"children\":[{\"name\":\"streamId\",\"value\":\"aStreamId\"},{\"name\":\"filename\",\"value\":\"aFilename\"},{\"name\":\"filesize\",\"value\":\"12345\"},{\"name\":\"mimeType\",\"value\":\"application/png\"}],\"repeatId\":\"0\",\"attributes\":{\"type\":\"someType\"}, \"extra\":\"extraValue\", \"name\":\"someResourceLink\"}";
		getConverterdLink(json);
	}

	@Test(expectedExceptions = JsonParseException.class, expectedExceptionsMessageRegExp = ""
			+ "Error parsing jsonObject: Group data must contain name and children, and may contain attributes or repeatId")
	public void testToClassWithIncorrectAttributeNameInData() {
		String json = "{\"children\":[{\"name\":\"streamId\",\"value\":\"aStreamId\"},{\"name\":\"filename\",\"value\":\"aFilename\"},{\"name\":\"filesize\",\"value\":\"12345\"},{\"name\":\"mimeType\",\"value\":\"application/png\"}],\"NOTattributes\":{\"type\":\"someType\"},\"name\":\"someResourceLink\"}";
		getConverterdLink(json);
	}

	@Test(expectedExceptions = JsonParseException.class, expectedExceptionsMessageRegExp = ""
			+ "ResourceLinkData must and can only contain children with name streamId and filename and filesize and mimeType")
	public void testToClassWithNoStreamId() {
		String json = "{\"children\":[{\"name\":\"filename\",\"value\":\"aFilename\"},{\"name\":\"filesize\",\"value\":\"12345\"},{\"name\":\"mimeType\",\"value\":\"application/png\"}],\"name\":\"someResourceLink\"}";
		getConverterdLink(json);
	}

	@Test(expectedExceptions = JsonParseException.class, expectedExceptionsMessageRegExp = ""
			+ "ResourceLinkData must and can only contain children with name streamId and filename and filesize and mimeType")
	public void testToClassWithNoStreamIdButOtherChild() {
		String json = "{\"children\":[{\"name\":\"NOTstreamId\",\"value\":\"aStreamId\"},{\"name\":\"filename\",\"value\":\"aFilename\"},{\"name\":\"filesize\",\"value\":\"12345\"},{\"name\":\"mimeType\",\"value\":\"application/png\"}],\"name\":\"someResourceLink\"}";
		getConverterdLink(json);
	}

	@Test(expectedExceptions = JsonParseException.class, expectedExceptionsMessageRegExp = ""
			+ "ResourceLinkData must and can only contain children with name streamId and filename and filesize and mimeType")
	public void testToClassWithNoFilenameButOtherChild() {
		String json = "{\"children\":[{\"name\":\"streamId\",\"value\":\"aStreamId\"},{\"name\":\"NOTfilename\",\"value\":\"aFilename\"},{\"name\":\"filesize\",\"value\":\"12345\"},{\"name\":\"mimeType\",\"value\":\"application/png\"}],\"name\":\"someResourceLink\"}";
		getConverterdLink(json);
	}

	@Test(expectedExceptions = JsonParseException.class, expectedExceptionsMessageRegExp = ""
			+ "ResourceLinkData must and can only contain children with name streamId and filename and filesize and mimeType")
	public void testToClassWithNoFilesizeButOtherChild() {
		String json = "{\"children\":[{\"name\":\"streamId\",\"value\":\"aStreamId\"},{\"name\":\"filename\",\"value\":\"aFilename\"},{\"name\":\"NOTfilesize\",\"value\":\"12345\"},{\"name\":\"mimeType\",\"value\":\"application/png\"}],\"name\":\"someResourceLink\"}";
		getConverterdLink(json);
	}

	@Test(expectedExceptions = JsonParseException.class, expectedExceptionsMessageRegExp = ""
			+ "ResourceLinkData must and can only contain children with name streamId and filename and filesize and mimeType")
	public void testToClassWithNoMimeTypeButOtherChild() {
		String json = "{\"children\":[{\"name\":\"streamId\",\"value\":\"aStreamId\"},{\"name\":\"filename\",\"value\":\"aFilename\"},{\"name\":\"filesize\",\"value\":\"12345\"},{\"name\":\"NOTmimeType\",\"value\":\"application/png\"}],\"name\":\"someResourceLink\"}";
		getConverterdLink(json);
	}
}
