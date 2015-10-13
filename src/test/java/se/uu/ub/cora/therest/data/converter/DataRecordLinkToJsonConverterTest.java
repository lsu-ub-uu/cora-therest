package se.uu.ub.cora.therest.data.converter;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import se.uu.ub.cora.therest.data.RestDataRecordLink;
import se.uu.ub.cora.therest.json.builder.JsonBuilderFactory;
import se.uu.ub.cora.therest.json.builder.org.OrgJsonBuilderFactoryAdapter;

public class DataRecordLinkToJsonConverterTest {
	@Test
	public void testToJson() {
		RestDataRecordLink dataLink = RestDataRecordLink.withNameInData("nameInData");
		dataLink.setRecordType("aRecordType");
		dataLink.setRecordId("aRecordId");

		JsonBuilderFactory jsonFactory = new OrgJsonBuilderFactoryAdapter();

		DataRecordLinkToJsonConverter dataRecordLinkToJsonConverter = DataRecordLinkToJsonConverter
				.usingJsonFactoryForRestDataLink(jsonFactory, dataLink);

		String jsonString = dataRecordLinkToJsonConverter.toJson();

		assertEquals(jsonString,
				"{\"name\":\"nameInData\",\"identifier\":{" + "\"recordType\":\"aRecordType\""
						+ "\"recordId\":\"aRecordId\"}" + ",\"actionLinks\":{"
						+ "\"read\":{\"requestMethod\":\"GET\",\"rel\":\"read\","
						+ "\"contentType\":\"application/metadata_record+json\","
						+ "\"url\":\"http://localhost:8080/therest/rest/record/aRecordType/aRecordId\","
						+ "\"accept\":\"application/metadata_record+json\"}}");
	}

}
