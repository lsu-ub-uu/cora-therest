package epc.therest.json;

import epc.therest.data.RestDataElement;

public interface DataToJsonConverterFactory {

	DataToJsonConverter createForRestDataElement(RestDataElement restDataElement);

}
