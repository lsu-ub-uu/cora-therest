package epc.therest.json;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObjectBuilder;

import epc.therest.data.DataElementRest;
import epc.therest.data.DataGroupRest;

public final class DataGroupJsonCreator implements JsonCreator {

	private DataGroupRest dataGroupRest;
	private JsonBuilderFactory jsonBuilderFactory;
	private JsonObjectBuilder groupChildren;

	public static JsonCreator forDataGroupRest(DataGroupRest dataGroupRest) {
		return new DataGroupJsonCreator(dataGroupRest);
	}

	private DataGroupJsonCreator(DataGroupRest dataGroupRest) {
		this.dataGroupRest = dataGroupRest;
		Map<String, Object> config = new HashMap<>();
		jsonBuilderFactory = Json.createBuilderFactory(config);
		groupChildren = jsonBuilderFactory.createObjectBuilder();
	}

	@Override
	public String toJson() {
		JsonObjectBuilder group = toJsonObjectBuilder();
		return group.build().toString();
	}

	@Override
	public JsonObjectBuilder toJsonObjectBuilder() {
		if (hasAttributes()) {
			addAttributesToGroup();
		}
		if (hasChildren()) {
			addChildrenToGroup();
		}
		JsonObjectBuilder group = jsonBuilderFactory.createObjectBuilder();
		group.add(dataGroupRest.getDataId(), groupChildren);
		return group;
	}

	private boolean hasChildren() {
		return !dataGroupRest.getChildren().isEmpty();
	}

	private boolean hasAttributes() {
		return !dataGroupRest.getAttributes().isEmpty();
	}

	private void addAttributesToGroup() {
		JsonObjectBuilder attributes = jsonBuilderFactory.createObjectBuilder();
		for (Entry<String, String> attributeEntry : dataGroupRest.getAttributes().entrySet()) {
			attributes.add(attributeEntry.getKey(), attributeEntry.getValue());
		}
		groupChildren.add("attributes", attributes);
	}

	private void addChildrenToGroup() {
		JsonCreatorFactory jsonCreatorFactory = new JsonCreatorFactoryImp();
		JsonArrayBuilder childrenArray = jsonBuilderFactory.createArrayBuilder();
		for (DataElementRest dataElementRest : dataGroupRest.getChildren()) {
			childrenArray.add(jsonCreatorFactory.factory(dataElementRest).toJsonObjectBuilder());
		}
		groupChildren.add("children", childrenArray);
	}
}
