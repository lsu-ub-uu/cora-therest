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

/**
 * APIUrls can hold the public urls for the system
 */
public record APIUrls(
		/**
		 * baseUrl is the base url where the system can be reached
		 */
		String baseUrl,
		/**
		 * restUrl is the url where the REST API of the system can be reached
		 */
		String restUrl,
		/**
		 * iifUrl is the url where the IIIF API of the system can be reached
		 */
		String iiifUrl) {
}
