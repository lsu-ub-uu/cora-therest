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
import se.uu.ub.cora.data.ExternallyConvertible;
import se.uu.ub.cora.data.converter.DataToJsonConverterProvider;
import se.uu.ub.cora.data.converter.JsonToDataConverterProvider;
import se.uu.ub.cora.initialize.SettingsProvider;
import se.uu.ub.cora.logger.LoggerProvider;
import se.uu.ub.cora.logger.spies.LoggerFactorySpy;
import se.uu.ub.cora.therest.dependency.TheRestInstanceFactorySpy;
import se.uu.ub.cora.therest.dependency.TheRestInstanceProvider;
import se.uu.ub.cora.therest.record.ConverterFactorySpy;
import se.uu.ub.cora.therest.record.DataToJsonConverterFactoryCreatorSpy;
import se.uu.ub.cora.therest.record.DataToJsonConverterFactorySpy;
import se.uu.ub.cora.therest.record.DataToJsonConverterSpy;
import se.uu.ub.cora.therest.record.ExternallyConvertibleToStringConverterSpy;
import se.uu.ub.cora.therest.record.HttpServletRequestOldSpy;
import se.uu.ub.cora.therest.record.JsonToDataConverterFactorySpy;
import se.uu.ub.cora.therest.record.StringToExternallyConvertibleConverterSpy;
import se.uu.ub.cora.therest.url.UrlHandlerSpy;

public class EndpointConverterTest {
	private static final String ACCEPT_XML = "application/vnd.cora.record-decorated+xml";
	private static final String ACCEPT_JSON = "application/vnd.cora.record-decorated+json";
	private EndpointConverter converter;
	private HttpServletRequestOldSpy requestSpy;
	private DataToJsonConverterFactoryCreatorSpy converterFactoryCreatorSpy;
	private JsonToDataConverterFactorySpy jsonToDataConverterFactorySpy;
	private ConverterFactorySpy converterFactorySpy;
	private StringToExternallyConvertibleConverterSpy stringToExternallyConvertibleConverterSpy;
	private ExternallyConvertible externallyConvertable;
	private TheRestInstanceFactorySpy instanceFactory;

	@BeforeMethod
	public void beforeMethod() {
		LoggerFactorySpy loggerFactory = new LoggerFactorySpy();
		LoggerProvider.setLoggerFactory(loggerFactory);
		setupUrlHandler();

		Map<String, String> settings = new HashMap<>();
		settings.put("theRestPublicPathToSystem", "/systemone/rest/");
		settings.put("iiifPublicPathToSystem", "/systemone/iiif/");
		SettingsProvider.setSettings(settings);

		requestSpy = new HttpServletRequestOldSpy();

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

	private void setupUrlHandler() {
		instanceFactory = new TheRestInstanceFactorySpy();
		TheRestInstanceProvider.onlyForTestSetTheRestInstanceFactory(instanceFactory);
	}

	@Test
	public void testUrlsHandledByUrlHandler() {
		converter.convertConvertibleToString(requestSpy, ACCEPT_JSON, externallyConvertable);

		UrlHandlerSpy urlHandler = (UrlHandlerSpy) instanceFactory.MCR
				.getReturnValue("factorUrlHandler", 0);

		var restUrl = urlHandler.MCR.assertCalledParametersReturn("getRestUrl", requestSpy);
		var iiifUrl = urlHandler.MCR.assertCalledParametersReturn("getIiifUrl", requestSpy);

		assertEquals(restUrl, getRestUrlFromFactorUsingConvertibleAndExternalUrls());
		assertEquals(iiifUrl, getIiifUrlFromFactorUsingConvertibleAndExternalUrls());
	}

	private String getRestUrlFromFactorUsingConvertibleAndExternalUrls() {
		DataToJsonConverterFactorySpy converterFactory = (DataToJsonConverterFactorySpy) converterFactoryCreatorSpy.MCR
				.getReturnValue("createFactory", 0);
		se.uu.ub.cora.data.converter.ExternalUrls externalUrls = (se.uu.ub.cora.data.converter.ExternalUrls) converterFactory.MCR
				.getParameterForMethodAndCallNumberAndParameter(
						"factorUsingConvertibleAndExternalUrls", 0, "externalUrls");
		return externalUrls.getBaseUrl();
	}

	private String getIiifUrlFromFactorUsingConvertibleAndExternalUrls() {
		DataToJsonConverterFactorySpy converterFactory = (DataToJsonConverterFactorySpy) converterFactoryCreatorSpy.MCR
				.getReturnValue("createFactory", 0);
		se.uu.ub.cora.data.converter.ExternalUrls externalUrls = (se.uu.ub.cora.data.converter.ExternalUrls) converterFactory.MCR
				.getParameterForMethodAndCallNumberAndParameter(
						"factorUsingConvertibleAndExternalUrls", 0, "externalUrls");
		return externalUrls.getIfffUrl();
	}

	@Test
	public void testConvertedDataIsReturnedJson() {
		String convertedJson = converter.convertConvertibleToString(requestSpy, ACCEPT_JSON,
				externallyConvertable);

		assertConverterCalledAndReturnedJsonValueIs(convertedJson);
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
	public void testConvertedDataIsReturnedXML() {
		String convertedXML = converter.convertConvertibleToString(requestSpy, ACCEPT_XML,
				externallyConvertable);

		assertConverterCalledAndReturnedXMLValueIs(convertedXML);
	}

	private void assertConverterCalledAndReturnedXMLValueIs(String convertedXML) {
		var dataToXmlConverter = (ExternallyConvertibleToStringConverterSpy) converterFactorySpy.MCR
				.getReturnValue("factorExternallyConvertableToStringConverter", 0);
		dataToXmlConverter.MCR.assertParameter("convertWithLinks", 0, "externallyConvertible",
				externallyConvertable);
		dataToXmlConverter.MCR.assertReturn("convertWithLinks", 0, convertedXML);
	}
}
