package epc.therest.json;

import epc.therest.data.RestDataAtomic;
import epc.therest.data.RestDataElement;
import epc.therest.data.RestDataGroup;
import epc.therest.jsonbuilder.JsonBuilderFactory;

public class DataToJsonConverterFactoryImp implements DataToJsonConverterFactory {

	@Override
	public DataToJsonConverter createForRestDataElement(JsonBuilderFactory factory,
			RestDataElement restDataElement) {

		if (restDataElement instanceof RestDataGroup) {
			return DataGroupToJsonConverter.forRestDataGroup(factory,
					(RestDataGroup) restDataElement);
		}
		return DataAtomicToJsonConverter.forRestDataAtomic(factory,
				(RestDataAtomic) restDataElement);
	}
}
