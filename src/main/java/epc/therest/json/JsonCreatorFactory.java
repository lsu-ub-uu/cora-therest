package epc.therest.json;

import epc.therest.data.RestDataElement;

public interface JsonCreatorFactory {

	JsonCreator createForRestDataElement(RestDataElement restDataElement);

}
