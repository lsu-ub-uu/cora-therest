package se.uu.ub.cora.therest.data.converter;

import se.uu.ub.cora.therest.data.RestDataAtomic;
import se.uu.ub.cora.therest.data.RestDataAttribute;
import se.uu.ub.cora.therest.data.RestDataElement;
import se.uu.ub.cora.therest.data.RestDataGroup;
import se.uu.ub.cora.therest.data.RestDataRecordLink;
import se.uu.ub.cora.therest.json.builder.JsonBuilderFactory;

public class DataToJsonConverterFactoryImp implements DataToJsonConverterFactory {

	@Override
	public DataToJsonConverter createForRestDataElement(JsonBuilderFactory factory,
			RestDataElement restDataElement) {

		if (restDataElement instanceof RestDataGroup) {
			return DataGroupToJsonConverter.usingJsonFactoryForRestDataGroup(factory,
					(RestDataGroup) restDataElement);
		}
		if (restDataElement instanceof RestDataAtomic) {
			return DataAtomicToJsonConverter.usingJsonFactoryForRestDataAtomic(factory,
					(RestDataAtomic) restDataElement);
		}
		if (restDataElement instanceof RestDataRecordLink) {
			return DataRecordLinkToJsonConverter.usingJsonFactoryForRestDataLink(factory,
					(RestDataRecordLink) restDataElement);
		}
		return DataAttributeToJsonConverter.usingJsonFactoryForRestDataAttribute(factory,
				(RestDataAttribute) restDataElement);
	}
}
