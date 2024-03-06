/*
 * Copyright 2024 Uppsala University Library
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

import java.util.Date;
import java.util.List;

import jakarta.ws.rs.core.EntityTag;
import jakarta.ws.rs.core.Request;
import jakarta.ws.rs.core.Response.ResponseBuilder;
import jakarta.ws.rs.core.Variant;
import se.uu.ub.cora.testutils.mcr.MethodCallRecorder;
import se.uu.ub.cora.testutils.mrv.MethodReturnValues;

public class RequestSpy implements Request {

	public MethodCallRecorder MCR = new MethodCallRecorder();
	public MethodReturnValues MRV = new MethodReturnValues();

	public RequestSpy() {
		MCR.useMRV(MRV);
		MRV.setDefaultReturnValuesSupplier("getMethod", () -> "aMethod");
	}

	@Override
	public String getMethod() {
		return (String) MCR.addCallAndReturnFromMRV();
	}

	@Override
	public Variant selectVariant(List<Variant> variants) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResponseBuilder evaluatePreconditions(EntityTag eTag) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResponseBuilder evaluatePreconditions(Date lastModified) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResponseBuilder evaluatePreconditions(Date lastModified, EntityTag eTag) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResponseBuilder evaluatePreconditions() {
		// TODO Auto-generated method stub
		return null;
	}

}
