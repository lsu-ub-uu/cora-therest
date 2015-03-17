package epc.therest.json;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObjectBuilder;

import epc.therest.data.RestDataElement;
import epc.therest.data.RestDataGroup;

public final class DataGroupToJsonConverter extends DataToJsonConverter {

	private RestDataGroup restDataGroup;
	private JsonBuilderFactory jsonBuilderFactory;
	private JsonObjectBuilder groupChildren;

	public static DataToJsonConverter forRestDataGroup(RestDataGroup restDataGroup) {
		return new DataGroupToJsonConverter(restDataGroup);
	}

	private DataGroupToJsonConverter(RestDataGroup restDataGroup) {
		this.restDataGroup = restDataGroup;
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
	JsonObjectBuilder toJsonObjectBuilder() {
		if (hasAttributes()) {
			addAttributesToGroup();
		}
		if (hasChildren()) {
			addChildrenToGroup();
		}
		JsonObjectBuilder group = jsonBuilderFactory.createObjectBuilder();
		group.add(restDataGroup.getDataId(), groupChildren);
		return group;
	}

	private boolean hasChildren() {
		return !restDataGroup.getChildren().isEmpty();
	}

	private boolean hasAttributes() {
		return !restDataGroup.getAttributes().isEmpty();
	}

	private void addAttributesToGroup() {
		JsonObjectBuilder attributes = jsonBuilderFactory.createObjectBuilder();
		for (Entry<String, String> attributeEntry : restDataGroup.getAttributes().entrySet()) {
			attributes.add(attributeEntry.getKey(), attributeEntry.getValue());
		}
		groupChildren.add("attributes", attributes);
	}

	private void addChildrenToGroup() {
		DataToJsonConverterFactory dataToJsonConverterFactory = new DataToJsonConverterFactoryImp();
		JsonArrayBuilder childrenArray = jsonBuilderFactory.createArrayBuilder();
		for (RestDataElement restDataElement : restDataGroup.getChildren()) {
			childrenArray.add(dataToJsonConverterFactory.createForRestDataElement(restDataElement)
					.toJsonObjectBuilder());
		}
		groupChildren.add("children", childrenArray);
	}
}
