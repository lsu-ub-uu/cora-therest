package epc.therest.json;

import epc.therest.data.RestDataAtomic;
import epc.therest.data.DataElementRest;
import epc.therest.data.RestDataGroup;

public class JsonCreatorFactoryImp implements JsonCreatorFactory {

	@Override
	public JsonCreator createForDataElementRest(DataElementRest dataElementRest) {

		if (dataElementRest instanceof RestDataGroup) {
			return DataGroupJsonCreator.forRestDataGroup((RestDataGroup) dataElementRest);
		}
		return DataAtomicJsonCreator.forRestDataAtomic((RestDataAtomic) dataElementRest);
	}

}
