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

import jakarta.servlet.http.HttpServletRequest;
import se.uu.ub.cora.data.ExternallyConvertible;

public interface EndpointConverter {

	/**
	 * Converts a convertible object to a string representation based on the given accept header.
	 *
	 * @param request
	 *            The HttpServletRequest that is making the current request.
	 * @param accept
	 *            The accept header value indicating the desired format.
	 * @param convertible
	 *            The convertible object to be converted.
	 * @return A string representation of the convertible object in the specified format.
	 */
	public String convertConvertibleToString(HttpServletRequest request, String accept,
			ExternallyConvertible convertible);
}
