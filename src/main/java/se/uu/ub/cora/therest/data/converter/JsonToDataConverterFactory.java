package se.uu.ub.cora.therest.data.converter;

import se.uu.ub.cora.therest.json.parser.JsonValue;

public interface JsonToDataConverterFactory {

	JsonToDataConverter createForJsonObject(JsonValue jsonValue);

}
