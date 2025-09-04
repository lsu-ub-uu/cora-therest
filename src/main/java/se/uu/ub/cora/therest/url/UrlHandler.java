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
package se.uu.ub.cora.therest.url;

import jakarta.servlet.http.HttpServletRequest;

/**
 * UrlHandler creates the Urls where different parts of the system can be reached
 */
public interface UrlHandler {

	/**
	 * getBaseUrl is the base url where the system can be reached
	 * 
	 * @param request
	 *            an HttpServlet request to read url info from
	 * @return A String with the baseUrl
	 */
	String getBaseUrl(HttpServletRequest request);

	/**
	 * getRestUrl is the url where the REST API of the system can be reached
	 * 
	 * @param request
	 *            an HttpServlet request to read url info from
	 * @return A String with the restUrl
	 */
	String getRestUrl(HttpServletRequest request);

	/**
	 * getIifUrl is the url where the IIIF API of the system can be reached
	 * 
	 * @param request
	 *            an HttpServlet request to read url info from
	 * @return A String with the iiifUrl
	 */
	String getIiifUrl(HttpServletRequest request);

	/**
	 * getAPIUrls returns all Urls where the system can be reached
	 * 
	 * @param request
	 *            an HttpServlet request to read url info from
	 * @return A Record with the base, rest and iiifUrl
	 */
	APIUrls getAPIUrls(HttpServletRequest request);

}
