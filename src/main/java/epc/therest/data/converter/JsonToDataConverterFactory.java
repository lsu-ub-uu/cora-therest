package epc.therest.data.converter;

import epc.therest.json.parser.JsonValue;

public interface JsonToDataConverterFactory {

	JsonToDataConverter createForJsonObject(JsonValue jsonValue);

}
