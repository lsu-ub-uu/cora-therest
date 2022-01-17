/*
 * Copyright 2022 Uppsala University Library
 *
 * This file is part of Cora.
 *
 *     Cora is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Cora is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Cora.  If not, see <http://www.gnu.org/licenses/>.
 */

package se.uu.ub.cora.therest.record;

import se.uu.ub.cora.converter.ConverterException;
import se.uu.ub.cora.converter.StringToExternallyConvertibleConverter;
import se.uu.ub.cora.data.DataElement;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.testutils.mcr.MethodCallRecorder;
import se.uu.ub.cora.therest.coradata.DataGroupSpy;

public class StringToExternallyConvertibleConverterSpy
		implements StringToExternallyConvertibleConverter {

	MethodCallRecorder MCR = new MethodCallRecorder();
	public boolean throwExceptionOnConvert;

	@Override
	public DataElement convert(String dataString) {
		MCR.addCall("dataString", dataString);

		if (throwExceptionOnConvert) {
			throw new ConverterException("exception from spy");
		}
		DataGroup dataPartToReturn = new DataGroupSpy("dummyId");
		MCR.addReturned(dataPartToReturn);
		return dataPartToReturn;
	}

}
