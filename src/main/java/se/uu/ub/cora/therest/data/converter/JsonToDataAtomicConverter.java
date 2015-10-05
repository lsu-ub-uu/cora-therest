package se.uu.ub.cora.therest.data.converter;

import se.uu.ub.cora.therest.data.RestDataAtomic;
import se.uu.ub.cora.therest.data.RestDataElement;
import se.uu.ub.cora.therest.json.parser.JsonObject;
import se.uu.ub.cora.therest.json.parser.JsonParseException;
import se.uu.ub.cora.therest.json.parser.JsonString;
import se.uu.ub.cora.therest.json.parser.JsonValue;

public final class JsonToDataAtomicConverter implements JsonToDataConverter
{
    private JsonObject jsonObject;

    static JsonToDataAtomicConverter forJsonObject(JsonObject jsonObject)
    {
        return new JsonToDataAtomicConverter(jsonObject);
    }

    private JsonToDataAtomicConverter(JsonObject jsonObject)
    {
        this.jsonObject = jsonObject;
    }

    @Override
    public RestDataElement toInstance()
    {
        try {
            return tryToInstantiate();
        } catch (Exception e) {
            throw new JsonParseException("Error parsing jsonObject: " + e.getMessage(), e);
        }
    }

    private RestDataElement tryToInstantiate()
    {
        validateJsonData();
        String nameInData = getNameInDataFromJsonObject();
        JsonString value = (JsonString) jsonObject.getValue(nameInData);
        return RestDataAtomic.withNameInDataAndValue(nameInData, value.getStringValue());
    }

    private String getNameInDataFromJsonObject()
    {
        return jsonObject.keySet().iterator().next();
    }

    private void validateJsonData()
    {
        validateOnlyOneKeyValuePairAtTopLevel();
        validateNameInDataValueIsString();
    }

    private void validateOnlyOneKeyValuePairAtTopLevel()
    {
        if (jsonObject.size() != 1) {
            throw new JsonParseException("Atomic data can only contain one key value pair");
        }
    }

    private void validateNameInDataValueIsString()
    {
        String nameInData = getNameInDataFromJsonObject();
        JsonValue value = jsonObject.getValue(nameInData);
        if (!(value instanceof JsonString)) {
            throw new JsonParseException("Value of atomic data \"" + nameInData + "\" must be a String");
        }
    }
}
