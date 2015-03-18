package epc.therest.data.converter;

import epc.therest.data.RestDataElement;
import epc.therest.json.builder.JsonBuilderFactory;

public interface DataToJsonConverterFactory {

	DataToJsonConverter createForRestDataElement(JsonBuilderFactory factory,
			RestDataElement restDataElement);

}
