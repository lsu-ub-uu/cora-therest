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
package se.uu.ub.cora.therest.converter;

import se.uu.ub.cora.data.ExternallyConvertible;
import se.uu.ub.cora.therest.url.APIUrls;

/**
 * EndpointOutgoingConverter converts ExternallyConvertibles to String
 */
public interface EndpointOutgoingConverter {

	/**
	 * Converts a convertible object to a string representation based on the given accept header.
	 *
	 * @param apiUrls
	 *            An APIUrls populated with the urls to reach the running system
	 * @param accept
	 *            The accept header value indicating the desired format.
	 * @param convertible
	 *            The convertible object to be converted.
	 * @return A string representation of the convertible object in the specified format.
	 */
	public String convertConvertibleToString(APIUrls apiUrls, String accept,
			ExternallyConvertible convertible);
}
