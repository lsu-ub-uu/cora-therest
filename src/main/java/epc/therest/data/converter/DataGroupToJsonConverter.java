package epc.therest.data.converter;

import java.util.Map.Entry;

import epc.therest.data.RestDataElement;
import epc.therest.data.RestDataGroup;
import epc.therest.json.builder.JsonArrayBuilder;
import epc.therest.json.builder.JsonBuilderFactory;
import epc.therest.json.builder.JsonObjectBuilder;

public final class DataGroupToJsonConverter extends DataToJsonConverter {

	private RestDataGroup restDataGroup;
	private JsonObjectBuilder groupChildren;
	private JsonBuilderFactory factory;

	public static DataToJsonConverter forRestDataGroup(
			epc.therest.json.builder.JsonBuilderFactory factory, RestDataGroup restDataGroup) {
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
		return group.toJsonFormattedString();
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
		group.addKeyJsonObjectBuilder(restDataGroup.getDataId(), groupChildren);
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
			attributes.addKeyString(attributeEntry.getKey(), attributeEntry.getValue());
		}
		groupChildren.addKeyJsonObjectBuilder("attributes", attributes);
	}

	private void addChildrenToGroup() {
		DataToJsonConverterFactory dataToJsonConverterFactory = new DataToJsonConverterFactoryImp();
		JsonArrayBuilder childrenArray = factory.createArrayBuilder();
		for (RestDataElement restDataElement : restDataGroup.getChildren()) {
			childrenArray.addJsonObjectBuilder(dataToJsonConverterFactory.createForRestDataElement(
					factory, restDataElement).toJsonObjectBuilder());
		}
		groupChildren.addKeyJsonArrayBuilder("children", childrenArray);
	}
}
