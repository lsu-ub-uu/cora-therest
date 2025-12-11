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

public class UrlHandlerImp implements UrlHandler {
	private HttpServletRequest request;
	private static final int AFTERHTTP = 10;

	@Override
	public String getBaseUrl(HttpServletRequest request) {
		this.request = request;
		return getBaseURLFromRequest();
	}

	private final String getBaseURLFromRequest() {
		String tempUrl = request.getRequestURL().toString();
		int indexOfFirstSlashAfterHttp = tempUrl.indexOf('/', AFTERHTTP);
		String baseURL = tempUrl.substring(0, indexOfFirstSlashAfterHttp);
		return changeHttpToHttpsIfHeaderSaysSo(baseURL);
	}

	private String changeHttpToHttpsIfHeaderSaysSo(String baseURI) {
		String forwardedProtocol = request.getHeader("X-Forwarded-Proto");

		if (ifForwardedProtocolExists(forwardedProtocol)) {
			return baseURI.replace("http:", forwardedProtocol + ":");
		}
		return baseURI;
	}

	private final String getDeploymentURLFromRequest() {
		String tempUrl = request.getRequestURL().toString();
		int indexOfFirstSlashAfterHttp = tempUrl.indexOf("/rest");
		String baseURL = tempUrl.substring(0, indexOfFirstSlashAfterHttp);
		return changeHttpToHttpsIfHeaderSaysSo(baseURL);
	}

	private boolean ifForwardedProtocolExists(String forwardedProtocol) {
		return null != forwardedProtocol && !"".equals(forwardedProtocol);
	}

	@Override
	public String getRestUrl(HttpServletRequest request) {
		this.request = request;
		return getDeploymentURLFromRequest() + "/rest/";
	}

	@Override
	public String getRestRecordUrl(HttpServletRequest request) {
		this.request = request;
		return getDeploymentURLFromRequest() + "/rest/record/";
	}

	@Override
	public String getIiifUrl(HttpServletRequest request) {
		this.request = request;
		// String baseURL = getBaseURLFromRequest();
		// baseURL += SettingsProvider.getSetting("iiifPublicPathToSystem");
		// return baseURL;
		return getDeploymentURLFromRequest() + "/iiif/";
	}

	@Override
	public APIUrls getAPIUrls(HttpServletRequest request) {
		// TODO SPIKE, add tests, add restUrl
		this.request = request;
		String baseUrl = getBaseURLFromRequest();
		String restRecordUrl = getRestRecordUrl(request);
		String iiifUrl = getIiifUrl(request);

		return new APIUrls(baseUrl, restRecordUrl, iiifUrl);
	}

}
