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

import static org.testng.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.converter.ConverterProvider;
import se.uu.ub.cora.converter.ExternalUrls;
import se.uu.ub.cora.data.ExternallyConvertible;
import se.uu.ub.cora.data.converter.DataToJsonConverterProvider;
import se.uu.ub.cora.data.converter.JsonToDataConverterProvider;
import se.uu.ub.cora.initialize.SettingsProvider;
import se.uu.ub.cora.logger.LoggerProvider;
import se.uu.ub.cora.logger.spies.LoggerFactorySpy;
import se.uu.ub.cora.therest.record.ConverterFactorySpy;
import se.uu.ub.cora.therest.record.DataToJsonConverterFactoryCreatorSpy;
import se.uu.ub.cora.therest.record.DataToJsonConverterFactorySpy;
import se.uu.ub.cora.therest.record.DataToJsonConverterSpy;
import se.uu.ub.cora.therest.record.ExternallyConvertibleToStringConverterSpy;
import se.uu.ub.cora.therest.record.HttpServletRequestSpy;
import se.uu.ub.cora.therest.record.JsonToDataConverterFactorySpy;
import se.uu.ub.cora.therest.record.StringToExternallyConvertibleConverterSpy;

public class EndpointConverterTest {
	private static final String ACCEPT_XML = "application/vnd.cora.record-decorated+xml";
	private static final String ACCEPT_JSON = "application/vnd.cora.record-decorated+json";
	private EndpointConverter converter;
	private HttpServletRequestSpy requestSpy;
	private DataToJsonConverterFactoryCreatorSpy converterFactoryCreatorSpy;
	private JsonToDataConverterFactorySpy jsonToDataConverterFactorySpy;
	private ConverterFactorySpy converterFactorySpy;
	private String standardBaseUrlHttp = "http://cora.epc.ub.uu.se/systemone/rest/record/";
	private String standardBaseUrlHttps = "https://cora.epc.ub.uu.se/systemone/rest/record/";
	private String standardIffUrlHttp = "http://cora.epc.ub.uu.se/systemone/iiif/";
	private String standardIffUrlHttps = "https://cora.epc.ub.uu.se/systemone/iiif/";
	private StringToExternallyConvertibleConverterSpy stringToExternallyConvertibleConverterSpy;
	private ExternallyConvertible externallyConvertable;

	@BeforeMethod
	public void beforeMethod() {
		LoggerFactorySpy loggerFactory = new LoggerFactorySpy();
		LoggerProvider.setLoggerFactory(loggerFactory);

		Map<String, String> settings = new HashMap<>();
		settings.put("theRestPublicPathToSystem", "/systemone/rest/");
		settings.put("iiifPublicPathToSystem", "/systemone/iiif/");
		SettingsProvider.setSettings(settings);

		requestSpy = new HttpServletRequestSpy();

		converterFactoryCreatorSpy = new DataToJsonConverterFactoryCreatorSpy();
		DataToJsonConverterProvider
				.setDataToJsonConverterFactoryCreator(converterFactoryCreatorSpy);

		stringToExternallyConvertibleConverterSpy = new StringToExternallyConvertibleConverterSpy();
		converterFactorySpy = new ConverterFactorySpy();
		converterFactorySpy.MRV.setDefaultReturnValuesSupplier(
				"factorStringToExternallyConvertableConverter",
				() -> stringToExternallyConvertibleConverterSpy);
		ConverterProvider.setConverterFactory("xml", converterFactorySpy);

		jsonToDataConverterFactorySpy = new JsonToDataConverterFactorySpy();
		JsonToDataConverterProvider.setJsonToDataConverterFactory(jsonToDataConverterFactorySpy);

		externallyConvertable = new ExternallyConvertibleSpy();

		converter = new EndpointConverterImp();
	}

	@Test
	public void testXForwardedProtoHttpsJson() {
		requestSpy.headers.put("X-Forwarded-Proto", "https");

		converter.convertConvertibleToString(requestSpy, ACCEPT_JSON, externallyConvertable);

		assertHttpsInUrlsForJsonConverter();
	}

	@Test
	public void testXForwardedProtoHttpsWhenAlreadyHttpsInRequestUrlJson() {
		requestSpy.headers.put("X-Forwarded-Proto", "https");
		requestSpy.requestURL = new StringBuffer(
				"https://cora.epc.ub.uu.se/systemone/rest/record/text/");

		converter.convertConvertibleToString(requestSpy, ACCEPT_JSON, externallyConvertable);

		assertHttpsInUrlsForJsonConverter();
	}

	@Test
	public void testXForwardedProtoEmptyJson() {
		requestSpy.headers.put("X-Forwarded-Proto", "");

		converter.convertConvertibleToString(requestSpy, ACCEPT_JSON, externallyConvertable);

		assertHttpInUrlsForJsonConverter();
	}

	@Test
	public void testXForwardedProtoMissingJson() {
		converter.convertConvertibleToString(requestSpy, ACCEPT_JSON, externallyConvertable);

		assertHttpInUrlsForJsonConverter();
	}

	@Test
	public void testConvertedDataIsReturnedJson() {
		String convertedJson = converter.convertConvertibleToString(requestSpy, ACCEPT_JSON,
				externallyConvertable);

		assertConverterCalledAndReturnedJsonValueIs(convertedJson);
	}

	private void assertHttpsInUrlsForJsonConverter() {
		assertEquals(getBaseUrlsFromJsonFactorUsingConvertibleAndExternalUrls(),
				standardBaseUrlHttps);
		assertEquals(getIiifUrlFromJsonFactorUsingConvertibleAndExternalUrls(),
				standardIffUrlHttps);
	}

	private void assertHttpInUrlsForJsonConverter() {
		assertEquals(getBaseUrlsFromJsonFactorUsingConvertibleAndExternalUrls(),
				standardBaseUrlHttp);
		assertEquals(getIiifUrlFromJsonFactorUsingConvertibleAndExternalUrls(), standardIffUrlHttp);
	}

	private String getBaseUrlsFromJsonFactorUsingConvertibleAndExternalUrls() {
		se.uu.ub.cora.data.converter.ExternalUrls externalUrls = getExternalUrlsForJson();
		return externalUrls.getBaseUrl();
	}

	private se.uu.ub.cora.data.converter.ExternalUrls getExternalUrlsForJson() {
		var converterFactory = (DataToJsonConverterFactorySpy) converterFactoryCreatorSpy.MCR
				.getReturnValue("createFactory", 0);
		return (se.uu.ub.cora.data.converter.ExternalUrls) converterFactory.MCR
				.getParameterForMethodAndCallNumberAndParameter(
						"factorUsingConvertibleAndExternalUrls", 0, "externalUrls");
	}

	private String getIiifUrlFromJsonFactorUsingConvertibleAndExternalUrls() {
		se.uu.ub.cora.data.converter.ExternalUrls externalUrls = getExternalUrlsForJson();
		return externalUrls.getIfffUrl();
	}

	private void assertConverterCalledAndReturnedJsonValueIs(String convertedJson) {
		var converterFactory = (DataToJsonConverterFactorySpy) converterFactoryCreatorSpy.MCR
				.getReturnValue("createFactory", 0);
		converterFactory.MCR.assertParameter("factorUsingConvertibleAndExternalUrls", 0,
				"convertible", externallyConvertable);
		var jsonConverter = (DataToJsonConverterSpy) converterFactory.MCR
				.getReturnValue("factorUsingConvertibleAndExternalUrls", 0);
		jsonConverter.MCR.assertReturn("toJsonCompactFormat", 0, convertedJson);
	}

	@Test
	public void testXForwardedProtoHttpsXML() {
		requestSpy.headers.put("X-Forwarded-Proto", "https");

		converter.convertConvertibleToString(requestSpy, ACCEPT_XML, externallyConvertable);

		assertHttpsInUrlsForXMLConverter();
	}

	@Test
	public void testXForwardedProtoHttpsWhenAlreadyHttpsInRequestUrlXML() {
		requestSpy.headers.put("X-Forwarded-Proto", "https");
		requestSpy.requestURL = new StringBuffer(
				"https://cora.epc.ub.uu.se/systemone/rest/record/text/");

		converter.convertConvertibleToString(requestSpy, ACCEPT_XML, externallyConvertable);

		assertHttpsInUrlsForXMLConverter();
	}

	@Test
	public void testXForwardedProtoEmptyXML() {
		requestSpy.headers.put("X-Forwarded-Proto", "");

		converter.convertConvertibleToString(requestSpy, ACCEPT_XML, externallyConvertable);

		assertHttpInUrlsForXMLConverter();
	}

	@Test
	public void testXForwardedProtoMissingXML() {
		converter.convertConvertibleToString(requestSpy, ACCEPT_XML, externallyConvertable);

		assertHttpInUrlsForXMLConverter();
	}

	@Test
	public void testConvertedDataIsReturnedXML() {
		String convertedXML = converter.convertConvertibleToString(requestSpy, ACCEPT_XML,
				externallyConvertable);

		assertConverterCalledAndReturnedXMLValueIs(convertedXML);
	}

	private void assertHttpsInUrlsForXMLConverter() {
		assertEquals(getBaseUrlsFromXMLFactorUsingConvertibleAndExternalUrls(),
				standardBaseUrlHttps);
		assertEquals(getIiifUrlFromXMLFactorUsingConvertibleAndExternalUrls(), standardIffUrlHttps);
	}

	private void assertHttpInUrlsForXMLConverter() {
		assertEquals(getBaseUrlsFromXMLFactorUsingConvertibleAndExternalUrls(),
				standardBaseUrlHttp);
		assertEquals(getIiifUrlFromXMLFactorUsingConvertibleAndExternalUrls(), standardIffUrlHttp);
	}

	private String getBaseUrlsFromXMLFactorUsingConvertibleAndExternalUrls() {
		ExternalUrls externalUrls = getExternalUrlsForXML();
		return externalUrls.getBaseUrl();
	}

	private ExternalUrls getExternalUrlsForXML() {
		ExternallyConvertibleToStringConverterSpy dataToXmlConverter = (ExternallyConvertibleToStringConverterSpy) converterFactorySpy.MCR
				.getReturnValue("factorExternallyConvertableToStringConverter", 0);

		return (ExternalUrls) dataToXmlConverter.MCR.getParameterForMethodAndCallNumberAndParameter(
				"convertWithLinks", 0, "externalUrls");
	}

	private String getIiifUrlFromXMLFactorUsingConvertibleAndExternalUrls() {
		ExternalUrls externalUrls = getExternalUrlsForXML();
		return externalUrls.getIfffUrl();
	}

	private void assertConverterCalledAndReturnedXMLValueIs(String convertedXML) {
		var dataToXmlConverter = (ExternallyConvertibleToStringConverterSpy) converterFactorySpy.MCR
				.getReturnValue("factorExternallyConvertableToStringConverter", 0);
		dataToXmlConverter.MCR.assertParameter("convertWithLinks", 0, "externallyConvertible",
				externallyConvertable);
		dataToXmlConverter.MCR.assertReturn("convertWithLinks", 0, convertedXML);
	}
}
