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

import se.uu.ub.cora.converter.ConverterProvider;
import se.uu.ub.cora.converter.ExternalUrls;
import se.uu.ub.cora.converter.ExternallyConvertibleToStringConverter;
import se.uu.ub.cora.data.Convertible;
import se.uu.ub.cora.data.ExternallyConvertible;
import se.uu.ub.cora.data.converter.DataToJsonConverter;
import se.uu.ub.cora.data.converter.DataToJsonConverterFactory;
import se.uu.ub.cora.data.converter.DataToJsonConverterProvider;
import se.uu.ub.cora.therest.url.APIUrls;

public class EndpointConverterImp implements EndpointConverter {
	private String restUrl;
	private String iiifUrl;

	@Override
	public String convertConvertibleToString(APIUrls apiUrls, String accept,
			ExternallyConvertible convertible) {
		calculateUrls(apiUrls);
		if (accept.endsWith("json")) {
			return convertDataToJson(convertible);
		}
		return convertDataToXml(convertible);
	}

	public void calculateUrls(APIUrls apiUrls) {
		restUrl = apiUrls.restUrl();
		iiifUrl = apiUrls.iiifUrl();
	}

	private String convertDataToJson(ExternallyConvertible convertible) {
		DataToJsonConverterFactory dataToJsonConverterFactory = DataToJsonConverterProvider
				.createImplementingFactory();

		se.uu.ub.cora.data.converter.ExternalUrls externalUrlsForJson = getExternalUrlsForJsonConverter();

		DataToJsonConverter converter = dataToJsonConverterFactory
				.factorUsingConvertibleAndExternalUrls((Convertible) convertible,
						externalUrlsForJson);
		return converter.toJsonCompactFormat();
	}

	private se.uu.ub.cora.data.converter.ExternalUrls getExternalUrlsForJsonConverter() {
		se.uu.ub.cora.data.converter.ExternalUrls externalUrlsForJson = new se.uu.ub.cora.data.converter.ExternalUrls();
		externalUrlsForJson.setBaseUrl(restUrl);
		externalUrlsForJson.setIfffUrl(iiifUrl);
		return externalUrlsForJson;
	}

	private String convertDataToXml(ExternallyConvertible convertible) {
		ExternallyConvertibleToStringConverter convertibleToXmlConverter = ConverterProvider
				.getExternallyConvertibleToStringConverter("xml");
		ExternalUrls externalUrls = getExternalUrlsForXmlConverter();
		return convertibleToXmlConverter.convertWithLinks(convertible, externalUrls);
	}

	private ExternalUrls getExternalUrlsForXmlConverter() {
		ExternalUrls externalUrls = new ExternalUrls();
		externalUrls.setBaseUrl(restUrl);
		externalUrls.setIfffUrl(iiifUrl);
		return externalUrls;
	}

}
