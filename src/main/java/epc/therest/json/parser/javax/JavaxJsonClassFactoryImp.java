package epc.therest.json.parser.javax;

import javax.json.JsonString;
import javax.json.JsonValue.ValueType;

import epc.therest.json.parser.JsonValue;

public class JavaxJsonClassFactoryImp implements JavaxJsonClassFactory {

	@Override
	public JsonValue createFromJavaxJsonValue(javax.json.JsonValue jsonValue) {
		if (ValueType.OBJECT.equals(jsonValue.getValueType())) {
			return JavaxJsonObject.usingJavaxJsonObject(this, (javax.json.JsonObject) jsonValue);
		}
		if (ValueType.ARRAY.equals(jsonValue.getValueType())) {
			return JavaxJsonArray.usingJavaxJsonArray(this, (javax.json.JsonArray) jsonValue);
		}
		return JavaxJsonString.usingJavaxJsonString((JsonString) jsonValue);
	}
}
