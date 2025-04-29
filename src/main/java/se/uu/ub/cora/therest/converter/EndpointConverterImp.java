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
import se.uu.ub.cora.converter.ConverterProvider;
import se.uu.ub.cora.converter.ExternalUrls;
import se.uu.ub.cora.converter.ExternallyConvertibleToStringConverter;
import se.uu.ub.cora.data.Convertible;
import se.uu.ub.cora.data.ExternallyConvertible;
import se.uu.ub.cora.data.converter.DataToJsonConverter;
import se.uu.ub.cora.data.converter.DataToJsonConverterFactory;
import se.uu.ub.cora.data.converter.DataToJsonConverterProvider;
import se.uu.ub.cora.initialize.SettingsProvider;

public class EndpointConverterImp implements EndpointConverter {
	private static final int AFTERHTTP = 10;
	private ExternalUrls externalUrls;
	private se.uu.ub.cora.data.converter.ExternalUrls externalUrlsForJson;
	private HttpServletRequest request;

	@Override
	public String convertConvertibleToString(HttpServletRequest request, String accept,
			ExternallyConvertible convertible) {
		calculateUrls(request);
		if (accept.endsWith("json")) {
			return convertDataToJson(convertible);
		}
		return convertDataToXml(convertible);
	}

	public void calculateUrls(HttpServletRequest req) {
		request = req;
		String baseUrl = getBaseURLFromURI();
		String iiifUrl = getIiifURLFromURI();

		setExternalUrlsForJsonConverter(baseUrl, iiifUrl);
		setExternalUrlsForXmlConverter(baseUrl, iiifUrl);
	}

	private void setExternalUrlsForJsonConverter(String baseUrl, String iiifUrl) {
		externalUrlsForJson = new se.uu.ub.cora.data.converter.ExternalUrls();
		externalUrlsForJson.setBaseUrl(baseUrl);
		externalUrlsForJson.setIfffUrl(iiifUrl);
	}

	private void setExternalUrlsForXmlConverter(String baseUrl, String iiifUrl) {
		externalUrls = new ExternalUrls();
		externalUrls.setBaseUrl(baseUrl);
		externalUrls.setIfffUrl(iiifUrl);
	}

	private final String getBaseURLFromURI() {
		String baseURL = getBaseURLFromRequest();
		baseURL += SettingsProvider.getSetting("theRestPublicPathToSystem");
		baseURL += "record/";
		return changeHttpToHttpsIfHeaderSaysSo(baseURL);
	}

	private final String getIiifURLFromURI() {
		String baseURL = getBaseURLFromRequest();
		baseURL += SettingsProvider.getSetting("iiifPublicPathToSystem");
		return changeHttpToHttpsIfHeaderSaysSo(baseURL);
	}

	private final String getBaseURLFromRequest() {
		String tempUrl = request.getRequestURL().toString();
		int indexOfFirstSlashAfterHttp = tempUrl.indexOf('/', AFTERHTTP);
		return tempUrl.substring(0, indexOfFirstSlashAfterHttp);
	}

	private String changeHttpToHttpsIfHeaderSaysSo(String baseURI) {
		String forwardedProtocol = request.getHeader("X-Forwarded-Proto");

		if (ifForwardedProtocolExists(forwardedProtocol)) {
			return baseURI.replace("http:", forwardedProtocol + ":");
		}
		return baseURI;
	}

	private boolean ifForwardedProtocolExists(String forwardedProtocol) {
		return null != forwardedProtocol && !"".equals(forwardedProtocol);
	}

	private String convertDataToXml(ExternallyConvertible convertible) {
		ExternallyConvertibleToStringConverter convertibleToXmlConverter = ConverterProvider
				.getExternallyConvertibleToStringConverter("xml");

		return convertibleToXmlConverter.convertWithLinks(convertible, externalUrls);
	}

	private String convertDataToJson(ExternallyConvertible convertible) {
		DataToJsonConverterFactory dataToJsonConverterFactory = DataToJsonConverterProvider
				.createImplementingFactory();
		DataToJsonConverter converter = dataToJsonConverterFactory
				.factorUsingConvertibleAndExternalUrls((Convertible) convertible,
						externalUrlsForJson);
		return converter.toJsonCompactFormat();
	}

}
