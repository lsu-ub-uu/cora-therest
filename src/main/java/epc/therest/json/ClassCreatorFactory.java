package epc.therest.json;

import javax.json.JsonObject;

public interface ClassCreatorFactory {

	ClassCreator factorOnJsonString(String json);

	ClassCreator factorOnJsonObject(JsonObject jsonObject);

}
