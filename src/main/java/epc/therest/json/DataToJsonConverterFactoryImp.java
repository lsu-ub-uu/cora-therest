package epc.therest.json;

import epc.therest.data.RestDataAtomic;
import epc.therest.data.RestDataElement;
import epc.therest.data.RestDataGroup;

public class DataToJsonConverterFactoryImp implements DataToJsonConverterFactory {

	@Override
	public DataToJsonConverter createForRestDataElement(RestDataElement restDataElement) {

		if (restDataElement instanceof RestDataGroup) {
			return DataGroupToJsonConverter.forRestDataGroup((RestDataGroup) restDataElement);
		}
		return DataAtomicToJsonConverter.forRestDataAtomic((RestDataAtomic) restDataElement);
	}

}
