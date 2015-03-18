package epc.therest.json;

import java.util.Map.Entry;

import epc.therest.data.RestDataElement;
import epc.therest.data.RestDataGroup;
import epc.therest.jsonbuilder.JsonArrayBuilder;
import epc.therest.jsonbuilder.JsonBuilderFactory;
import epc.therest.jsonbuilder.JsonObjectBuilder;

public final class DataGroupToJsonConverter extends DataToJsonConverter {

	private RestDataGroup restDataGroup;
	private JsonObjectBuilder groupChildren;
	private JsonBuilderFactory factory;

	public static DataToJsonConverter forRestDataGroup(
			epc.therest.jsonbuilder.JsonBuilderFactory factory, RestDataGroup restDataGroup) {
		return new DataGroupToJsonConverter(factory, restDataGroup);
	}

	private DataGroupToJsonConverter(JsonBuilderFactory factory, RestDataGroup restDataGroup) {
		this.factory = factory;
		this.restDataGroup = restDataGroup;
		groupChildren = factory.createObjectBuilder();
	}

	@Override
	public String toJson() {
		JsonObjectBuilder group = toJsonObjectBuilder();
		return group.build().toJsonString();
	}

	@Override
	JsonObjectBuilder toJsonObjectBuilder() {
		if (hasAttributes()) {
			addAttributesToGroup();
		}
		if (hasChildren()) {
			addChildrenToGroup();
		}
		JsonObjectBuilder group = factory.createObjectBuilder();
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
		JsonObjectBuilder attributes = factory.createObjectBuilder();
		for (Entry<String, String> attributeEntry : restDataGroup.getAttributes().entrySet()) {
			attributes.add(attributeEntry.getKey(), attributeEntry.getValue());
		}
		groupChildren.add("attributes", attributes);
	}

	private void addChildrenToGroup() {
		DataToJsonConverterFactory dataToJsonConverterFactory = new DataToJsonConverterFactoryImp();
		JsonArrayBuilder childrenArray = factory.createArrayBuilder();
		for (RestDataElement restDataElement : restDataGroup.getChildren()) {
			childrenArray.add(dataToJsonConverterFactory.createForRestDataElement(factory,
					restDataElement).toJsonObjectBuilder());
		}
		groupChildren.add("children", childrenArray);
	}
}
