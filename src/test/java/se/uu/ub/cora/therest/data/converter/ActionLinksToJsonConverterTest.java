package se.uu.ub.cora.therest.data.converter;

import static org.testng.Assert.assertEquals;

import java.util.LinkedHashMap;
import java.util.Map;

import org.testng.annotations.Test;

import se.uu.ub.cora.spider.data.Action;
import se.uu.ub.cora.therest.data.ActionLink;
import se.uu.ub.cora.therest.json.builder.JsonBuilderFactory;
import se.uu.ub.cora.therest.json.builder.org.OrgJsonBuilderFactoryAdapter;

public class ActionLinksToJsonConverterTest {
	@Test
	public void testConvert() {
		ActionLink actionLink = createReadActionLink();
		Map<String, ActionLink> actionLinks = new LinkedHashMap<>();
		actionLinks.put("read", actionLink);

		JsonBuilderFactory jsonFactory = new OrgJsonBuilderFactoryAdapter();

		ActionLinksToJsonConverter converter = new ActionLinksToJsonConverter(jsonFactory,
				actionLinks);
		assertEquals(converter.toJson(),
				"{" + "\"read\":{\"requestMethod\":\"GET\",\"rel\":\"read\","
						+ "\"contentType\":\"application/metadata_record+json\","
						+ "\"url\":\"http://localhost:8080/therest/rest/record/place/place:0001\","
						+ "\"accept\":\"application/metadata_record+json\"}" + "}");
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
