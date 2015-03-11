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
	private DataGroupRest dataGroupRest;
	private JsonObject dataGroupChildren;

	public static DataGroupClassCreator forJsonObject(JsonObject jsonObject) {
		return new DataGroupClassCreator(jsonObject);
	}

	private DataGroupClassCreator(JsonObject jsonObject) {
		this.jsonObject = jsonObject;
	}

	@Override
	public DataElementRest toClass() {
		String dataId = getDataId();
		dataGroupRest = DataGroupRest.withDataId(dataId);
		dataGroupChildren = jsonObject.getJsonObject(dataId);
		if (hasAttributes()) {
			addAttributesToGroup();
		}
		if (hasChildren()) {
			addChildrenToGroup();
		}
		return dataGroupRest;
	}

	private String getDataId() {
		return jsonObject.keySet().iterator().next();
	}

	private boolean hasAttributes() {
		return null != dataGroupChildren.getJsonObject("attributes");
	}

	private void addAttributesToGroup() {
		JsonObject attributes = dataGroupChildren.getJsonObject("attributes");
		for (Entry<String, JsonValue> attribute : attributes.entrySet()) {
			String value = ((JsonString) attribute.getValue()).getString();
			dataGroupRest.addAttribute(attribute.getKey(), value);
		}
	}

	private boolean hasChildren() {
		return null != dataGroupChildren.getJsonArray("children");
	}

	private void addChildrenToGroup() {
		ClassCreatorFactoryImp classCreatorFactoryImp = new ClassCreatorFactoryImp();
		JsonArray children = dataGroupChildren.getJsonArray("children");
		for (JsonValue jsonValue : children) {
			JsonObject jsonChildObject = (JsonObject) jsonValue;
			ClassCreator factorOnJsonObject = classCreatorFactoryImp
					.factorOnJsonObject(jsonChildObject);
			dataGroupRest.addChild(factorOnJsonObject.toClass());
		}
	}
}
