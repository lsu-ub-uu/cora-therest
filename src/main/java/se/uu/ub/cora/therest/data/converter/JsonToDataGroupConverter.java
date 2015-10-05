package se.uu.ub.cora.therest.data.converter;

import java.util.Map.Entry;

import se.uu.ub.cora.therest.data.RestDataElement;
import se.uu.ub.cora.therest.data.RestDataGroup;
import se.uu.ub.cora.therest.json.parser.*;

public final class JsonToDataGroupConverter implements JsonToDataConverter {

	private static final String CHILDREN = "children";
	private static final String ATTRIBUTES = "attributes";
	private static final int NUM_OF_ALLOWED_KEYS_AT_TOP_LEVEL = 3;
	private JsonObject jsonObject;
	private RestDataGroup restDataGroup;

	static JsonToDataGroupConverter forJsonObject(JsonObject jsonObject) {
		return new JsonToDataGroupConverter(jsonObject);
	}

	private JsonToDataGroupConverter(JsonObject jsonObject) {
		this.jsonObject = jsonObject;
	}

	@Override
	public RestDataElement toInstance() {
		try {
			return tryToInstanciate();
		} catch (Exception e) {
			throw new JsonParseException("Error parsing jsonObject: "+e.getMessage(), e);
		}
	}

	private RestDataElement tryToInstanciate() {
		validateOnlyCorrectKeysAtTopLevel();
		return createDataGroupInstance();
	}

	private String getNameInDataFromJsonObject() {
		return jsonObject.getValueAsJsonString("name").getStringValue();
	}

	private void validateOnlyCorrectKeysAtTopLevel() {

		if (!jsonObject.containsKey("name")) {
			throw new JsonParseException("Group data must contain key \"name\"");
		}

		if (!hasChildren()) {
			throw new JsonParseException("Group data must contain key \""+CHILDREN+"\"");
		}

		if(jsonObject.keySet().size() == NUM_OF_ALLOWED_KEYS_AT_TOP_LEVEL && !hasAttributes()){
			throw new JsonParseException("Group data must contain key \""+ATTRIBUTES+"\"");
		}

		if(jsonObject.keySet().size() > NUM_OF_ALLOWED_KEYS_AT_TOP_LEVEL){
			throw new JsonParseException("Group data can only contain keys \"name\", \""+CHILDREN+"\" and \""+ATTRIBUTES+"\"");
		}
	}

	private RestDataElement createDataGroupInstance() {
		String nameInData = getNameInDataFromJsonObject();
		restDataGroup = RestDataGroup.withNameInData(nameInData);
		if (hasAttributes()) {
			addAttributesToGroup();
		}
		addChildrenToGroup();
		return restDataGroup;
	}

	private boolean hasAttributes() {
		return jsonObject.containsKey(ATTRIBUTES);
	}

	private void addAttributesToGroup() {
		JsonObject attributes = jsonObject.getValueAsJsonObject(ATTRIBUTES);
		for (Entry<String, JsonValue> attributeEntry : attributes.entrySet()) {
			addAttributeToGroup(attributeEntry);
		}
	}

	private void addAttributeToGroup(Entry<String, JsonValue> attributeEntry) {
		String value = ((JsonString) attributeEntry.getValue()).getStringValue();
		restDataGroup.addAttributeByIdWithValue(attributeEntry.getKey(), value);
	}

	private boolean hasChildren() {
		return jsonObject.containsKey(CHILDREN);
	}

	private void addChildrenToGroup() {
		JsonArray children = jsonObject.getValueAsJsonArray(CHILDREN);
		for (JsonValue child : children) {
			addChildToGroup(child);
		}
	}

	private void addChildToGroup(JsonValue child) {
		JsonToDataConverterFactoryImp jsonToDataConverterFactoryImp = new JsonToDataConverterFactoryImp();
		JsonObject jsonChildObject = (JsonObject) child;
		JsonToDataConverter childJsonToDataConverter = jsonToDataConverterFactoryImp
				.createForJsonObject(jsonChildObject);
		restDataGroup.addChild(childJsonToDataConverter.toInstance());
	}
}
