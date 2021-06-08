package se.uu.ub.cora.therest.record;

import java.util.ArrayList;
import java.util.List;

import se.uu.ub.cora.data.converter.JsonToDataConverter;
import se.uu.ub.cora.data.converter.JsonToDataConverterFactory;
import se.uu.ub.cora.json.parser.JsonValue;

public class JsonToDataConverterFactorySpy implements JsonToDataConverterFactory {

	public JsonToDataConverterSpy jsonToDataConverterSpy;
	public List<JsonToDataConverterSpy> jsonToDataConverterSpies = new ArrayList<>();
	public boolean throwError = false;
	public JsonValue jsonValue;
	public List<JsonValue> jsonValues = new ArrayList<>();

	@Override
	public JsonToDataConverter createForJsonObject(JsonValue jsonValue) {
		jsonToDataConverterSpy = new JsonToDataConverterSpy();
		jsonToDataConverterSpies.add(jsonToDataConverterSpy);
		jsonValues.add(jsonValue);
		this.jsonValue = jsonValue;
		jsonToDataConverterSpy.throwError = throwError;
		return jsonToDataConverterSpy;
	}

}
