package epc.therest.json;

import epc.therest.data.DataElementRest;

public interface JsonCreatorFactory {

	JsonCreator factory(DataElementRest dataElementRest);

}
