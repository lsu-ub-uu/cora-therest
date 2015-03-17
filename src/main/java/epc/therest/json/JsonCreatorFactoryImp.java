package epc.therest.json;

import epc.therest.data.RestDataAtomic;
import epc.therest.data.RestDataElement;
import epc.therest.data.RestDataGroup;

public class JsonCreatorFactoryImp implements JsonCreatorFactory {

	@Override
	public JsonCreator createForRestDataElement(RestDataElement restDataElement) {

		if (restDataElement instanceof RestDataGroup) {
			return DataGroupJsonCreator.forRestDataGroup((RestDataGroup) restDataElement);
		}
		return DataAtomicJsonCreator.forRestDataAtomic((RestDataAtomic) restDataElement);
	}

}
