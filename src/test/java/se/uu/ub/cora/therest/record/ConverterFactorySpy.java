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

import se.uu.ub.cora.converter.ConverterFactory;
import se.uu.ub.cora.converter.ExternallyConvertibleToStringConverter;
import se.uu.ub.cora.converter.StringToExternallyConvertibleConverter;
import se.uu.ub.cora.testutils.mcr.MethodCallRecorder;

public class ConverterFactorySpy implements ConverterFactory {
	MethodCallRecorder MCR = new MethodCallRecorder();

	@Override
	public ExternallyConvertibleToStringConverter factorExternallyConvertableToStringConverter() {
		MCR.addCall();

		ExternallyConvertibleToStringConverter converterSpy = new ExternallyConvertibleToStringConverterSpy();

		MCR.addReturned(converterSpy);
		return converterSpy;
	}

	@Override
	public StringToExternallyConvertibleConverter factorStringToExternallyConvertableConverter() {
		MCR.addCall();

		StringToExternallyConvertibleConverter converterSpy = new StringToExternallyConvertibleConverterSpy();

		MCR.addReturned(converterSpy);
		return converterSpy;
	}

	@Override
	public String getName() {
		MCR.addCall();
		String out = "spy";
		MCR.addReturned(out);
		return out;
	}

}
