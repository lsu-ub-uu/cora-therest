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
		ActionLink actionLink = ActionLink.withAction(Action.READ);
		Map<String, ActionLink> actionLinks = new LinkedHashMap<>();
		actionLinks.put("read", actionLink);

		JsonBuilderFactory jsonFactory = new OrgJsonBuilderFactoryAdapter();

		ActionLinksToJsonConverter converter = new ActionLinksToJsonConverter(jsonFactory,
				actionLinks);
		assertEquals(converter.toJson(), "");
	}
}
