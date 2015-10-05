package se.uu.ub.cora.therest.data.converter;

import se.uu.ub.cora.therest.data.RestDataElement;
import se.uu.ub.cora.therest.json.builder.JsonBuilderFactory;

public interface DataToJsonConverterFactory {

	DataToJsonConverter createForRestDataElement(JsonBuilderFactory factory,
			RestDataElement restDataElement);

}
