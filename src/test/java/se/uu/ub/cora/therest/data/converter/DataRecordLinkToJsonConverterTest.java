package se.uu.ub.cora.therest.data.converter;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import se.uu.ub.cora.spider.data.Action;
import se.uu.ub.cora.therest.data.ActionLink;
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

		assertEquals(jsonString, "{\"recordId\":\"aRecordId\",\"recordType\":\"aRecordType\""
				+ ",\"name\":\"nameInData\"}");

	}

	@Test
	public void testToJsonWithActionLink() {
		RestDataRecordLink dataLink = RestDataRecordLink.withNameInData("nameInData");
		dataLink.setRecordType("aRecordType");
		dataLink.setRecordId("aRecordId");
		dataLink.addActionLink("read", createReadActionLink());

		JsonBuilderFactory jsonFactory = new OrgJsonBuilderFactoryAdapter();

		DataRecordLinkToJsonConverter dataRecordLinkToJsonConverter = DataRecordLinkToJsonConverter
				.usingJsonFactoryForRestDataLink(jsonFactory, dataLink);

		String jsonString = dataRecordLinkToJsonConverter.toJson();

		assertEquals(jsonString, "{\"recordId\":\"aRecordId\",\"recordType\":\"aRecordType\""
				+ ",\"actionLinks\":{\"read\":{\"requestMethod\":\"GET\",\"rel\":\"read\""
				+ ",\"contentType\":\"application/metadata_record+json\""
				+ ",\"url\":\"http://localhost:8080/therest/rest/record/place/place:0001\""
				+ ",\"accept\":\"application/metadata_record+json\"}},\"name\":\"nameInData\"}");

	}

	private ActionLink createReadActionLink() {
		ActionLink actionLink = ActionLink.withAction(Action.READ);
		actionLink.setAccept("application/metadata_record+json");
		actionLink.setContentType("application/metadata_record+json");
		actionLink.setRequestMethod("GET");
		actionLink.setURL("http://localhost:8080/therest/rest/record/place/place:0001");
		return actionLink;
	}
}
