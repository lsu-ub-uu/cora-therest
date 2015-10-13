package se.uu.ub.cora.therest.data.converter;

import se.uu.ub.cora.therest.json.parser.JsonObject;
import se.uu.ub.cora.therest.json.parser.JsonParseException;
import se.uu.ub.cora.therest.json.parser.JsonValue;

public class JsonToDataConverterFactoryImp implements JsonToDataConverterFactory {

	private JsonObject jsonObject;

	@Override
	public JsonToDataConverter createForJsonObject(JsonValue jsonValue) {
		if (!(jsonValue instanceof JsonObject)) {
			throw new JsonParseException("Json value is not an object, can not convert");
		}
		jsonObject = (JsonObject) jsonValue;

		if (isGroup()) {
			return JsonToDataGroupConverter.forJsonObject(jsonObject);
		}
		if (isAtomicData()) {
			return JsonToDataAtomicConverter.forJsonObject(jsonObject);
		}
		if (isRecordLink()) {
			return JsonToDataRecordLinkConverter.forJsonObject(jsonObject);
		}
		return JsonToDataAttributeConverter.forJsonObject(jsonObject);
	}

	private boolean isAtomicData() {
		return jsonObject.containsKey("value");
	}

	private boolean isGroup() {
		return jsonObject.containsKey("children");
	}

	private boolean isRecordLink() {
		return jsonObject.containsKey("recordType");
	}
}
