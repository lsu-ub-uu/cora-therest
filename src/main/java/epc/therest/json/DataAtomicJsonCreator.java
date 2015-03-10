package epc.therest.json;

import java.util.HashMap;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObjectBuilder;

import epc.therest.data.DataAtomicRest;

public class DataAtomicJsonCreator implements JsonCreator {

	private DataAtomicRest dataAtomicRest;

	public static JsonCreator forDataAtomicRest(DataAtomicRest dataAtomic) {
		return new DataAtomicJsonCreator(dataAtomic);
	}

	private DataAtomicJsonCreator(DataAtomicRest dataAtomic) {
		this.dataAtomicRest = dataAtomic;
	}

	@Override
	public String toJson() {
		JsonObjectBuilder atomic = toJsonObjectBuilder();
		return atomic.build().toString();
	}

	@Override
	public JsonObjectBuilder toJsonObjectBuilder() {
		Map<String, Object> config = new HashMap<>();
		JsonBuilderFactory factory = Json.createBuilderFactory(config);
		JsonObjectBuilder atomic = factory.createObjectBuilder();
		atomic.add(dataAtomicRest.getDataId(), dataAtomicRest.getValue());
		return atomic;
	}
}
