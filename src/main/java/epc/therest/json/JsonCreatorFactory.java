package epc.therest.json;

import epc.therest.data.DataElementRest;

public interface JsonCreatorFactory {

	JsonCreator factorOnDataElementRest(DataElementRest dataElementRest);

}
