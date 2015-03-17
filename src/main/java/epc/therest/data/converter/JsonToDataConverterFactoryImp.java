package epc.therest.data.converter;

import java.util.Map.Entry;

import epc.therest.jsonparser.JsonObject;
import epc.therest.jsonparser.JsonParseException;
import epc.therest.jsonparser.JsonValue;
import epc.therest.jsonparser.JsonValueType;

public class JsonToDataConverterFactoryImp implements JsonToDataConverterFactory {

	@Override
	public JsonToDataConverter createForJsonObject(JsonValue jsonValue) {
		if (!(jsonValue instanceof JsonObject)) {
			throw new JsonParseException("Json value is not an object, can not convert");
		}
		JsonObject jsonObject = (JsonObject) jsonValue;
		Entry<String, JsonValue> entry = jsonObject.entrySet().iterator().next();
		if (JsonValueType.OBJECT.equals(entry.getValue().getValueType())) {
			return JsonToDataGroupConverter.forJsonObject(jsonObject);
		}
		return JsonToDataAtomicConverter.forJsonObject(jsonObject);
	}
}
