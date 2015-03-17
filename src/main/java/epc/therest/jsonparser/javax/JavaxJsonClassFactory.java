package epc.therest.jsonparser.javax;

import epc.therest.jsonparser.JsonValue;

public interface JavaxJsonClassFactory {

	JsonValue createFromJavaxJsonValue(javax.json.JsonValue javaxJsonValue);

}
