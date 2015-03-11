package epc.therest.json;

import javax.json.JsonObject;

import epc.therest.data.DataAtomicRest;
import epc.therest.data.DataElementRest;

public final class DataAtomicClassCreator implements ClassCreator {

	public static DataAtomicClassCreator forJsonObject(JsonObject jsonObject) {
		return new DataAtomicClassCreator(jsonObject);
	}

	private JsonObject jsonObject;

	private DataAtomicClassCreator(JsonObject jsonObject) {
		this.jsonObject = jsonObject;
	}

	@Override
	public DataElementRest toClass() {
		String dataId = jsonObject.keySet().iterator().next();
		String value = jsonObject.getString(dataId);
		return DataAtomicRest.withDataIdAndValue(dataId, value);
	}
}
