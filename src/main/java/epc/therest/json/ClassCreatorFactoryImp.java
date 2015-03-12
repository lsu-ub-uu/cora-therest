package epc.therest.json;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonReaderFactory;
import javax.json.JsonValue;

public class ClassCreatorFactoryImp implements ClassCreatorFactory {

	@Override
	public ClassCreator createForJsonString(String json) {
		try {
			return tryToCreateForJsonString(json);
		} catch (Exception e) {
			throw new JsonParseException("Error parsing json", e);
		}
	}

	private ClassCreator tryToCreateForJsonString(String json) {
		Map<String, Object> config = new HashMap<>();
		JsonReaderFactory jsonReaderFactory = Json.createReaderFactory(config);
		JsonReader jsonReader = jsonReaderFactory.createReader(new StringReader(json));
		JsonObject jsonObject = jsonReader.readObject();
		return tryToCreateForJsonObject(jsonObject);
	}

	@Override
	public ClassCreator createForJsonObject(JsonObject jsonObject) {
		try {
			return tryToCreateForJsonObject(jsonObject);
		} catch (Exception e) {
			throw new JsonParseException("Error parsing json", e);
		}
	}

	private ClassCreator tryToCreateForJsonObject(JsonObject jsonObject) {
		JsonValue jsonValue = jsonObject.values().iterator().next();
		if (JsonValue.ValueType.OBJECT.equals(jsonValue.getValueType())) {
			return DataGroupClassCreator.forJsonObject(jsonObject);
		}
		return DataAtomicClassCreator.forJsonObject(jsonObject);
	}
}
