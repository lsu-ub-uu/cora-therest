package epc.therest.json;

import epc.therest.data.RestDataElement;
import epc.therest.jsonbuilder.JsonBuilderFactory;

public interface DataToJsonConverterFactory {

	DataToJsonConverter createForRestDataElement(JsonBuilderFactory factory,
			RestDataElement restDataElement);

}
