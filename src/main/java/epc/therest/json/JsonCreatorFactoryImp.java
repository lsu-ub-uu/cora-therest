package epc.therest.json;

import epc.therest.data.DataAtomicRest;
import epc.therest.data.DataElementRest;
import epc.therest.data.DataGroupRest;

public class JsonCreatorFactoryImp implements JsonCreatorFactory {

	@Override
	public JsonCreator factory(DataElementRest dataElementRest) {

		if (dataElementRest instanceof DataGroupRest) {
			return DataGroupJsonCreator.forDataGroupRest((DataGroupRest) dataElementRest);
		}
		return DataAtomicJsonCreator.forDataAtomicRest((DataAtomicRest) dataElementRest);
	}

}
