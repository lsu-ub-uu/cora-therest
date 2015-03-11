package epc.therest.json;

import java.util.Map.Entry;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonString;
import javax.json.JsonValue;

import epc.therest.data.DataElementRest;
import epc.therest.data.DataGroupRest;

public final class DataGroupClassCreator implements ClassCreator {

	private JsonObject jsonObject;

	public static DataGroupClassCreator forJsonObject(JsonObject jsonObject) {
		return new DataGroupClassCreator(jsonObject);
	}

	private DataGroupClassCreator(JsonObject jsonObject) {
		this.jsonObject = jsonObject;
	}

	@Override
	public DataElementRest toClass() {
		String dataId = jsonObject.keySet().iterator().next();
		DataGroupRest dataGroupRest = DataGroupRest.withDataId(dataId);
		JsonObject dataGroupChildren = jsonObject.getJsonObject(dataId);

		// attributes
		JsonObject attributes = dataGroupChildren.getJsonObject("attributes");
		if (null != attributes) {
			for (Entry<String, JsonValue> attribute : attributes.entrySet()) {
				String value = ((JsonString) attribute.getValue()).getString();
				dataGroupRest.addAttribute(attribute.getKey(), value);
			}
		}

		// children
		JsonArray children = dataGroupChildren.getJsonArray("children");
		if (null != children) {
			for (JsonValue jsonValue : children) {
				JsonObject jsonChildObject = (JsonObject) jsonValue;
				ClassCreator factorOnJsonObject = new ClassCreatorFactoryImp()
						.factorOnJsonObject(jsonChildObject);
				dataGroupRest.addChild(factorOnJsonObject.toClass());
			}
		}
		return dataGroupRest;
	}

}
