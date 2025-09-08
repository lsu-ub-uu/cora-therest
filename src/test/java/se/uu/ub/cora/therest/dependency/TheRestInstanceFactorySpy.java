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
package se.uu.ub.cora.therest.dependency;

import se.uu.ub.cora.testutils.mcr.MethodCallRecorder;
import se.uu.ub.cora.testutils.mrv.MethodReturnValues;
import se.uu.ub.cora.therest.converter.EndpointIncomingConverter;
import se.uu.ub.cora.therest.converter.EndpointOutgoingConverter;
import se.uu.ub.cora.therest.error.ErrorHandler;
import se.uu.ub.cora.therest.record.EndpointSearch;
import se.uu.ub.cora.therest.record.EndpointSearchSpy;
import se.uu.ub.cora.therest.spy.EndpointIncomingConverterSpy;
import se.uu.ub.cora.therest.spy.EndpointOutgoingConverterSpy;
import se.uu.ub.cora.therest.spy.ErrorHandlerSpy;
import se.uu.ub.cora.therest.url.UrlHandler;
import se.uu.ub.cora.therest.url.UrlHandlerSpy;

public class TheRestInstanceFactorySpy implements TheRestInstanceFactory {
	public MethodCallRecorder MCR = new MethodCallRecorder();
	public MethodReturnValues MRV = new MethodReturnValues();

	public TheRestInstanceFactorySpy() {
		MCR.useMRV(MRV);
		MRV.setDefaultReturnValuesSupplier("factorUrlHandler", UrlHandlerSpy::new);
		MRV.setDefaultReturnValuesSupplier("factorErrorHandler", ErrorHandlerSpy::new);
		MRV.setDefaultReturnValuesSupplier("factorEndpointOutgoingConverter",
				EndpointOutgoingConverterSpy::new);
		MRV.setDefaultReturnValuesSupplier("factorEndpointIncomingConverter",
				EndpointIncomingConverterSpy::new);
		MRV.setDefaultReturnValuesSupplier("factorEndpointSearch", EndpointSearchSpy::new);
	}

	@Override
	public UrlHandler factorUrlHandler() {
		return (UrlHandler) MCR.addCallAndReturnFromMRV();
	}

	@Override
	public EndpointOutgoingConverter factorEndpointOutgoingConverter() {
		return (EndpointOutgoingConverter) MCR.addCallAndReturnFromMRV();
	}

	@Override
	public ErrorHandler factorErrorHandler() {
		return (ErrorHandler) MCR.addCallAndReturnFromMRV();
	}

	@Override
	public EndpointIncomingConverter factorEndpointIncomingConverter() {
		return (EndpointIncomingConverter) MCR.addCallAndReturnFromMRV();
	}

	@Override
	public EndpointSearch factorEndpointSearch() {
		return (EndpointSearch) MCR.addCallAndReturnFromMRV();
	}

}
