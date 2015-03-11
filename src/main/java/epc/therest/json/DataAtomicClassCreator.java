package epc.therest.json;

import javax.json.JsonObject;

import epc.therest.data.DataAtomicRest;
import epc.therest.data.DataElementRest;

public final class DataAtomicClassCreator implements ClassCreator {
	private JsonObject jsonObject;

	public static DataAtomicClassCreator forJsonObject(JsonObject jsonObject) {
		return new DataAtomicClassCreator(jsonObject);
	}

	private DataAtomicClassCreator(JsonObject jsonObject) {
		this.jsonObject = jsonObject;
	}

	@Override
	public DataElementRest toClass() {
		try {
			validateData();
			return tryToClass();
		} catch (Exception e) {
			throw new JsonParseException("Error parsing jsonObject", e);
		}
	}

	private void validateData() {
		if (jsonObject.size() != 1) {
			throw new JsonParseException("Atomic data can only contain one key value pair");
		}
	}

	private DataElementRest tryToClass() {
		String dataId = jsonObject.keySet().iterator().next();
		String value = jsonObject.getString(dataId);
		return DataAtomicRest.withDataIdAndValue(dataId, value);
	}
}
