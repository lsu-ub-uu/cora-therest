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

public class DataGroupJsonCreator implements JsonCreator {

	private DataGroupRest dataGroupRest;

	public static JsonCreator forDataGroupRest(DataGroupRest dataGroupRest) {
		return new DataGroupJsonCreator(dataGroupRest);
	}

	private DataGroupJsonCreator(DataGroupRest dataGroupRest) {
		this.dataGroupRest = dataGroupRest;
	}

	@Override
	public String toJson() {
		JsonObjectBuilder group = toJsonObjectBuilder();
		return group.build().toString();
	}

	@Override
	public JsonObjectBuilder toJsonObjectBuilder() {
		Map<String, Object> config = new HashMap<>();
		JsonBuilderFactory jsonBuilderFactory = Json.createBuilderFactory(config);
		JsonObjectBuilder group = jsonBuilderFactory.createObjectBuilder();
		JsonObjectBuilder groupChildren = jsonBuilderFactory.createObjectBuilder();

		// attributes
		JsonObjectBuilder attributes = jsonBuilderFactory.createObjectBuilder();
		JsonCreatorFactory jsonCreatorFactory = new JsonCreatorFactoryImp();

		for (Entry<String, String> attributeEntry : dataGroupRest.getAttributes().entrySet()) {
			attributes.add(attributeEntry.getKey(), attributeEntry.getValue());
		}

		if (!dataGroupRest.getAttributes().isEmpty()) {
			groupChildren.add("attributes", attributes);
		}

		// children
		JsonArrayBuilder childrenArray = jsonBuilderFactory.createArrayBuilder();
		for (DataElementRest dataElementRest : dataGroupRest.getChildren()) {
			childrenArray.add(jsonCreatorFactory.factory(dataElementRest).toJsonObjectBuilder());
		}

		if (!dataGroupRest.getChildren().isEmpty()) {
			groupChildren.add("children", childrenArray);
		}

		group.add(dataGroupRest.getDataId(), groupChildren);
		return group;
	}
}
