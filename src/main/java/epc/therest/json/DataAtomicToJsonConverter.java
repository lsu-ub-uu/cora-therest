package epc.therest.json;

import java.util.HashMap;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObjectBuilder;

import epc.therest.data.RestDataAtomic;

public final class DataAtomicToJsonConverter extends DataToJsonConverter {

	private RestDataAtomic restDataAtomic;

	public static DataToJsonConverter forRestDataAtomic(RestDataAtomic dataAtomic) {
		return new DataAtomicToJsonConverter(dataAtomic);
	}

	private DataAtomicToJsonConverter(RestDataAtomic dataAtomic) {
		this.restDataAtomic = dataAtomic;
	}

	@Override
	public String toJson() {
		JsonObjectBuilder atomic = toJsonObjectBuilder();
		return atomic.build().toString();
	}

	@Override
	JsonObjectBuilder toJsonObjectBuilder() {
		Map<String, Object> config = new HashMap<>();
		JsonBuilderFactory factory = Json.createBuilderFactory(config);
		JsonObjectBuilder atomic = factory.createObjectBuilder();
		atomic.add(restDataAtomic.getDataId(), restDataAtomic.getValue());
		return atomic;
	}
}
