package epc.therest.json;

import javax.json.JsonObject;

public interface ClassCreatorFactory {

	ClassCreator createForJsonString(String json);

	ClassCreator createForJsonObject(JsonObject jsonObject);

}
