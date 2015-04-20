package epc.therest.data.converter;

import java.util.Map.Entry;

import epc.therest.data.RestDataElement;
import epc.therest.data.RestDataGroup;
import epc.therest.json.builder.JsonArrayBuilder;
import epc.therest.json.builder.JsonBuilderFactory;
import epc.therest.json.builder.JsonObjectBuilder;

public final class DataGroupToJsonConverter extends DataToJsonConverter {

	private RestDataGroup restDataGroup;
	private JsonObjectBuilder dataGroupJsonObjectBuilder;
	private JsonBuilderFactory jsonBuilderFactory;

	public static DataToJsonConverter forRestDataGroup(
			epc.therest.json.builder.JsonBuilderFactory factory, RestDataGroup restDataGroup) {
		return new DataGroupToJsonConverter(factory, restDataGroup);
	}

	private DataGroupToJsonConverter(JsonBuilderFactory factory, RestDataGroup restDataGroup) {
		this.jsonBuilderFactory = factory;
		this.restDataGroup = restDataGroup;
		dataGroupJsonObjectBuilder = factory.createObjectBuilder();
	}

	@Override
	public String toJson() {
		JsonObjectBuilder rootWrappingJsonObjectBuilder = toJsonObjectBuilder();
		return rootWrappingJsonObjectBuilder.toJsonFormattedString();
	}

	@Override
	JsonObjectBuilder toJsonObjectBuilder() {
		if (hasAttributes()) {
			addAttributesToGroup();
		}
		if (hasChildren()) {
			addChildrenToGroup();
		}
		JsonObjectBuilder rootWrappingJsonObjectBuilder = jsonBuilderFactory.createObjectBuilder();
		rootWrappingJsonObjectBuilder.addKeyJsonObjectBuilder(restDataGroup.getDataId(),
				dataGroupJsonObjectBuilder);
		return rootWrappingJsonObjectBuilder;
	}

	private boolean hasAttributes() {
		return !restDataGroup.getAttributes().isEmpty();
	}

	private void addAttributesToGroup() {
		JsonObjectBuilder attributes = jsonBuilderFactory.createObjectBuilder();
		for (Entry<String, String> attributeEntry : restDataGroup.getAttributes().entrySet()) {
			attributes.addKeyString(attributeEntry.getKey(), attributeEntry.getValue());
		}
		dataGroupJsonObjectBuilder.addKeyJsonObjectBuilder("attributes", attributes);
	}

	private boolean hasChildren() {
		return !restDataGroup.getChildren().isEmpty();
	}

	private void addChildrenToGroup() {
		DataToJsonConverterFactory dataToJsonConverterFactory = new DataToJsonConverterFactoryImp();
		JsonArrayBuilder childrenArray = jsonBuilderFactory.createArrayBuilder();
		for (RestDataElement restDataElement : restDataGroup.getChildren()) {
			childrenArray.addJsonObjectBuilder(dataToJsonConverterFactory.createForRestDataElement(
					jsonBuilderFactory, restDataElement).toJsonObjectBuilder());
		}
		dataGroupJsonObjectBuilder.addKeyJsonArrayBuilder("children", childrenArray);
	}
}
