package epc.therest.json.parser.javax;

import epc.therest.json.parser.JsonValue;

public interface JavaxJsonClassFactory {

	JsonValue createFromJavaxJsonValue(javax.json.JsonValue javaxJsonValue);

}
