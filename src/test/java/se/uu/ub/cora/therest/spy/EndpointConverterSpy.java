/*
 * Copyright 2025 Uppsala University Library
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
package se.uu.ub.cora.therest.spy;

import se.uu.ub.cora.data.ExternallyConvertible;
import se.uu.ub.cora.testutils.mcr.MethodCallRecorder;
import se.uu.ub.cora.testutils.mrv.MethodReturnValues;
import se.uu.ub.cora.therest.converter.EndpointConverter;
import se.uu.ub.cora.therest.url.APIUrls;

public class EndpointConverterSpy implements EndpointConverter {
	public MethodCallRecorder MCR = new MethodCallRecorder();
	public MethodReturnValues MRV = new MethodReturnValues();

	public EndpointConverterSpy() {
		MCR.useMRV(MRV);
		MRV.setDefaultReturnValuesSupplier("convertConvertibleToString",
				() -> "someConvertedRecord");
	}

	@Override
	public String convertConvertibleToString(APIUrls apiUrls, String accept,
			ExternallyConvertible convertible) {
		return (String) MCR.addCallAndReturnFromMRV("apiUrls", apiUrls, "accept", accept,
				"convertible", convertible);
	}

}
