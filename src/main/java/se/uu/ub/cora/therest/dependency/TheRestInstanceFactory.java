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

import se.uu.ub.cora.therest.converter.EndpointIncomingConverter;
import se.uu.ub.cora.therest.converter.EndpointOutgoingConverter;
import se.uu.ub.cora.therest.error.ErrorHandler;
import se.uu.ub.cora.therest.record.EndpointSearch;
import se.uu.ub.cora.therest.url.UrlHandler;

public interface TheRestInstanceFactory {

	/**
	 * factorUrlHandler factors a new UrlHandler
	 * 
	 * @return the newly created UrlHandler
	 */
	UrlHandler factorUrlHandler();

	/**
	 * factorErrorHandler factors a new ErrorHandler
	 * 
	 * @return the newly created ErrorHandler
	 */
	ErrorHandler factorErrorHandler();

	/**
	 * factorEndpointOutgoingConverter factors a new EndpointConverter
	 * 
	 * @return the newly created EndpointConverter
	 */
	EndpointOutgoingConverter factorEndpointOutgoingConverter();

	/**
	 * factorEndpointIncomingConverter factors a new EndpointConverter
	 * 
	 * @return the newly created EndpointConverter
	 */
	EndpointIncomingConverter factorEndpointIncomingConverter();

	/**
	 * factorEndpointSearch factors a new EndpointSearch
	 * 
	 * @return the newly created EndpointSearch
	 */
	EndpointSearch factorEndpointSearch();

}