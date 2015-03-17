package epc.therest.data.converter;

import epc.therest.jsonparser.JsonValue;

public interface JsonToDataConverterFactory {

	JsonToDataConverter createForJsonObject(JsonValue jsonValue);

}
