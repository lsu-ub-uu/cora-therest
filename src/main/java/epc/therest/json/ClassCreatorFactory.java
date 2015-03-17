package epc.therest.json;

import epc.therest.jsonparser.JsonValue;

public interface ClassCreatorFactory {

	ClassCreator createForJsonObject(JsonValue jsonValue);

}
