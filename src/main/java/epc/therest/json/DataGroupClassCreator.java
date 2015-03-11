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
		try {
			validateData();
			return tryToClass();
		} catch (Exception e) {
			throw new JsonParseException("Error parsing jsonObject", e);
		}
	}

	private void validateData() {
		validateOnlyOneKeyValuePairAtTopLevel();
		validateGroupOnlyContainsAttributesOrChildren();
	}

	private void validateOnlyOneKeyValuePairAtTopLevel() {
		if (jsonObject.size() != 1) {
			throw new JsonParseException("Group data can only contain one key value pair");
		}
	}

	private void validateGroupOnlyContainsAttributesOrChildren() {
		String dataId = getDataId();
		JsonObject dataGroupChildren2 = jsonObject.getJsonObject(dataId);
		for (Entry<String, JsonValue> childEntry : dataGroupChildren2.entrySet()) {
			validateChildEntryIsAttributesOrChildren(childEntry);
		}
	}

	private void validateChildEntryIsAttributesOrChildren(Entry<String, JsonValue> childEntry) {
		String key = childEntry.getKey();
		if (!"attributes".equals(key) && !"children".equals(key)) {
			throw new JsonParseException("Group data can only contain attributes or children");
		}
	}

	private DataElementRest tryToClass() {
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
		for (Entry<String, JsonValue> attributeEntry : attributes.entrySet()) {
			addAttributeToGroup(attributeEntry);
		}
	}

	private void addAttributeToGroup(Entry<String, JsonValue> attributeEntry) {
		String value = ((JsonString) attributeEntry.getValue()).getString();
		dataGroupRest.addAttribute(attributeEntry.getKey(), value);
	}

	private boolean hasChildren() {
		return null != dataGroupChildren.getJsonArray("children");
	}

	private void addChildrenToGroup() {
		JsonArray children = dataGroupChildren.getJsonArray("children");
		for (JsonValue child : children) {
			addChildToGroup(child);
		}
	}

	private void addChildToGroup(JsonValue child) {
		ClassCreatorFactoryImp classCreatorFactoryImp = new ClassCreatorFactoryImp();
		JsonObject jsonChildObject = (JsonObject) child;
		ClassCreator childClassCreator = classCreatorFactoryImp.factorOnJsonObject(jsonChildObject);
		dataGroupRest.addChild(childClassCreator.toClass());
	}
}
