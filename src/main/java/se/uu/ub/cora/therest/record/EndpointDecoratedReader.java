/*
 * Copyright 2025 Olov McKie
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

import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.core.Response;

public interface EndpointDecoratedReader {

	/**
	 * Reads a record from a REST endpoint and decorates it using the given parameters.
	 * 
	 * @param request
	 *            is the HTTP request object passed from the Rest call.
	 * @param accept
	 *            The Accept header value to be used in the request.
	 * @param authToken
	 *            The authorization token to be included in the request.
	 * @param type
	 *            The type of the record to be read.
	 * @param id
	 *            The ID of the record to be read.
	 *
	 * @return A {@link Response} object containing the decorated record.
	 */
	Response readAndDecorateRecord(HttpServletRequest request, String accept, String authToken,
			String type, String id);

}
