package se.uu.ub.cora.therest.record;

import se.uu.ub.cora.data.Convertible;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.converter.ConversionException;
import se.uu.ub.cora.data.converter.JsonToDataConverter;
import se.uu.ub.cora.therest.coradata.DataGroupSpy;

public class JsonToDataConverterSpy implements JsonToDataConverter {
	public DataGroup dataPartToReturn;
	public boolean throwError = false;

	@Override
	public Convertible toInstance() {
		if (throwError) {
			throw new ConversionException("Error from converter spy");
		}
		dataPartToReturn = new DataGroupSpy("dummyId");
		return dataPartToReturn;
	}

}
