package se.uu.ub.cora.therest.record;

import se.uu.ub.cora.converter.StringToExternallyConvertibleConverter;
import se.uu.ub.cora.data.DataElement;
import se.uu.ub.cora.testutils.mcr.MethodCallRecorder;

public class StringToExternallyConvertibleConverterSpy
		implements StringToExternallyConvertibleConverter {

	MethodCallRecorder MCR = new MethodCallRecorder();

	@Override
	public DataElement convert(String dataString) {
		MCR.addCall("dataString", dataString);

		return null;
	}

}
