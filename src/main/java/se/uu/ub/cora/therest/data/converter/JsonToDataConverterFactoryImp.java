package se.uu.ub.cora.therest.data.converter;

import se.uu.ub.cora.therest.json.parser.JsonObject;
import se.uu.ub.cora.therest.json.parser.JsonParseException;
import se.uu.ub.cora.therest.json.parser.JsonValue;

public class JsonToDataConverterFactoryImp implements JsonToDataConverterFactory {

	@Override
	public JsonToDataConverter createForJsonObject(JsonValue jsonValue) {
		if (!(jsonValue instanceof JsonObject)) {
			throw new JsonParseException("Json value is not an object, can not convert");
		}
		JsonObject jsonObject = (JsonObject) jsonValue;

		boolean hasChildren = jsonObject.containsKey("children");

		if (hasChildren) {
			return JsonToDataGroupConverter.forJsonObject(jsonObject);
		}
		return JsonToDataAtomicConverter.forJsonObject(jsonObject);
	}
}
