package epc.therest.json;

import java.util.Map.Entry;

import epc.therest.data.DataElementRest;
import epc.therest.data.RestDataGroup;
import epc.therest.jsonparser.JsonArray;
import epc.therest.jsonparser.JsonObject;
import epc.therest.jsonparser.JsonParseException;
import epc.therest.jsonparser.JsonString;
import epc.therest.jsonparser.JsonValue;

public final class DataGroupClassCreator implements ClassCreator {

	private JsonObject jsonObject;
	private RestDataGroup restDataGroup;
	private JsonObject jsonGroupChildren;

	static DataGroupClassCreator forJsonObject(JsonObject jsonObject) {
		return new DataGroupClassCreator(jsonObject);
	}

	private DataGroupClassCreator(JsonObject jsonObject) {
		this.jsonObject = jsonObject;
	}

	@Override
	public DataElementRest toInstance() {
		try {
			return tryToInstanciate();
		} catch (Exception e) {
			throw new JsonParseException("Error parsing jsonObject", e);
		}
	}

	private DataElementRest tryToInstanciate() {
		String dataId = getDataIdFromJsonObject();
		jsonGroupChildren = jsonObject.getObject(dataId);
		validateJsonData();
		return createDataGroupInstance();
	}

	private String getDataIdFromJsonObject() {
		return jsonObject.keySet().iterator().next();
	}

	private void validateJsonData() {
		validateOnlyOneKeyValuePairAtTopLevel();
		validateGroupOnlyContainsAttributesOrChildren();
	}

	private void validateOnlyOneKeyValuePairAtTopLevel() {
		if (jsonObject.size() != 1) {
			throw new JsonParseException("Group data can only contain one key value pair");
		}
	}

	private void validateGroupOnlyContainsAttributesOrChildren() {
		for (Entry<String, JsonValue> childEntry : jsonGroupChildren.entrySet()) {
			validateChildEntryIsAttributesOrChildren(childEntry);
		}
	}

	private void validateChildEntryIsAttributesOrChildren(Entry<String, JsonValue> childEntry) {
		String key = childEntry.getKey();
		if (!"attributes".equals(key) && !"children".equals(key)) {
			throw new JsonParseException("Group data can only contain attributes or children");
		}
	}

	private DataElementRest createDataGroupInstance() {
		String dataId2 = getDataIdFromJsonObject();
		restDataGroup = RestDataGroup.withDataId(dataId2);
		if (hasAttributes()) {
			addAttributesToGroup();
		}
		if (hasChildren()) {
			addChildrenToGroup();
		}
		return restDataGroup;
	}

	private boolean hasAttributes() {
		return jsonGroupChildren.containsKey("attributes");
	}

	private void addAttributesToGroup() {
		JsonObject attributes = jsonGroupChildren.getObject("attributes");
		for (Entry<String, JsonValue> attributeEntry : attributes.entrySet()) {
			addAttributeToGroup(attributeEntry);
		}
	}

	private void addAttributeToGroup(Entry<String, JsonValue> attributeEntry) {
		String value = ((JsonString) attributeEntry.getValue()).getStringValue();
		restDataGroup.addAttribute(attributeEntry.getKey(), value);
	}

	private boolean hasChildren() {
		return jsonGroupChildren.containsKey("children");
	}

	private void addChildrenToGroup() {
		JsonArray children = jsonGroupChildren.getArray("children");
		for (JsonValue child : children) {
			addChildToGroup(child);
		}
	}

	private void addChildToGroup(JsonValue child) {
		ClassCreatorFactoryImp classCreatorFactoryImp = new ClassCreatorFactoryImp();
		JsonObject jsonChildObject = (JsonObject) child;
		ClassCreator childClassCreator = classCreatorFactoryImp
				.createForJsonObject(jsonChildObject);
		restDataGroup.addChild(childClassCreator.toInstance());
	}
}
