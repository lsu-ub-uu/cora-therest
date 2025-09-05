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

import se.uu.ub.cora.json.parser.JsonParser;
import se.uu.ub.cora.json.parser.org.OrgJsonParser;
import se.uu.ub.cora.therest.converter.EndpointIncomingConverter;
import se.uu.ub.cora.therest.converter.EndpointIncomingConverterImp;
import se.uu.ub.cora.therest.converter.EndpointOutgoingConverter;
import se.uu.ub.cora.therest.converter.EndpointOutgoingConverterImp;
import se.uu.ub.cora.therest.error.ErrorHandler;
import se.uu.ub.cora.therest.error.ErrorHandlerImp;
import se.uu.ub.cora.therest.url.UrlHandler;
import se.uu.ub.cora.therest.url.UrlHandlerImp;

public final class TheRestInstanceFactoryImp implements TheRestInstanceFactory {

	@Override
	public UrlHandler factorUrlHandler() {
		return new UrlHandlerImp();
	}

	@Override
	public ErrorHandler factorErrorHandler() {
		return new ErrorHandlerImp();
	}

	@Override
	public EndpointOutgoingConverter factorEndpointOutgoingConverter() {
		return new EndpointOutgoingConverterImp();
	}

	@Override
	public EndpointIncomingConverter factorEndpointIncomingConverter() {
		JsonParser jsonParser = new OrgJsonParser();
		return new EndpointIncomingConverterImp(jsonParser);
	}

}
