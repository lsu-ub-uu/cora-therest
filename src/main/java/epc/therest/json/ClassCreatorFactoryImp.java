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
	public ClassCreator factorOnJsonString(String json) {
		Map<String, Object> config = new HashMap<>();
		JsonReaderFactory jsonReaderFactory = Json.createReaderFactory(config);
		JsonReader jsonReader = jsonReaderFactory.createReader(new StringReader(json));
		JsonObject jsonObject = jsonReader.readObject();
		return factorOnJsonObject(jsonObject);
	}

	@Override
	public ClassCreator factorOnJsonObject(JsonObject jsonObject) {
		JsonValue jsonValue = jsonObject.values().iterator().next();
		if (JsonValue.ValueType.OBJECT.equals(jsonValue.getValueType())) {
			return DataGroupClassCreator.forJsonObject(jsonObject);
		}
		return DataAtomicClassCreator.forJsonObject(jsonObject);
	}
}
