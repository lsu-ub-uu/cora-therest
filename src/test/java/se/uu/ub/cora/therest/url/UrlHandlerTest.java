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

import static org.testng.Assert.assertEquals;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.logger.LoggerProvider;
import se.uu.ub.cora.logger.spies.LoggerFactorySpy;

public class UrlHandlerTest {
	private LoggerFactorySpy loggerFactorySpy;
	private UrlHandler urlHandler;
	private HttpServletRequestSpy requestSpy;
	private String standardBaseUrlHttp = "http://cora.epc.ub.uu.se";
	private String standardBaseUrlHttps = "https://cora.epc.ub.uu.se";
	private String standardRestUrlHttp = "http://cora.epc.ub.uu.se/systemone/rest/";
	private String standardRestUrlHttps = "https://cora.epc.ub.uu.se/systemone/rest/";
	private String standardRestRecordUrlHttp = "http://cora.epc.ub.uu.se/systemone/rest/record/";
	private String standardRestRecordUrlHttps = "https://cora.epc.ub.uu.se/systemone/rest/record/";
	private String standardIiifUrlHttp = "http://cora.epc.ub.uu.se/systemone/iiif/";
	private String standardIiifUrlHttps = "https://cora.epc.ub.uu.se/systemone/iiif/";

	@BeforeMethod
	public void beforeMethod() {
		loggerFactorySpy = new LoggerFactorySpy();
		LoggerProvider.setLoggerFactory(loggerFactorySpy);
		requestSpy = new HttpServletRequestSpy();
		urlHandler = new UrlHandlerImp();

		requestSpy.MRV.setDefaultReturnValuesSupplier("getServerName", () -> "cora.epc.ub.uu.se");
		requestSpy.MRV.setDefaultReturnValuesSupplier("getPathInfo", () -> "/record/text/");
		requestSpy.MRV.setDefaultReturnValuesSupplier("getRequestURL",
				() -> new StringBuffer("http://cora.epc.ub.uu.se/systemone/rest/record/text/"));

		// Map<String, String> settings = new HashMap<>();
		// settings.put("theRestPublicPathToSystem", "/systemone/rest/");
		// settings.put("iiifPublicPathToSystem", "/systemone/iiif/");
		// SettingsProvider.setSettings(settings);
	}

	@Test
	public void testGetBaseUrl() {
		String baseUrl = urlHandler.getBaseUrl(requestSpy);

		assertEquals(baseUrl, standardBaseUrlHttp);
	}

	@Test
	public void testXForwardedProtoHttps() {
		requestSpy.MRV.setSpecificReturnValuesSupplier("getHeader", () -> "https",
				"X-Forwarded-Proto");
		String baseUrl = urlHandler.getBaseUrl(requestSpy);

		assertEquals(baseUrl, standardBaseUrlHttps);
	}

	@Test
	public void testXForwardedProtoEmpty() {
		requestSpy.MRV.setSpecificReturnValuesSupplier("getHeader", () -> "", "X-Forwarded-Proto");
		String baseUrl = urlHandler.getBaseUrl(requestSpy);

		assertEquals(baseUrl, standardBaseUrlHttp);
	}

	@Test
	public void testXForwardedProtoHttpsAlreadyHttps() {
		requestSpy.MRV.setDefaultReturnValuesSupplier("getRequestURL",
				() -> new StringBuffer("https://cora.epc.ub.uu.se/systemone/rest/record/text/"));
		requestSpy.MRV.setSpecificReturnValuesSupplier("getHeader", () -> "https",
				"X-Forwarded-Proto");
		String baseUrl = urlHandler.getBaseUrl(requestSpy);

		assertEquals(baseUrl, standardBaseUrlHttps);
	}

	@Test
	public void testGetRestUrl() {
		String baseUrl = urlHandler.getRestUrl(requestSpy);

		assertEquals(baseUrl, standardRestUrlHttp);
	}

	@Test
	public void testGetRestUrlXForwardedProtoHttps() {
		requestSpy.MRV.setSpecificReturnValuesSupplier("getHeader", () -> "https",
				"X-Forwarded-Proto");
		String baseUrl = urlHandler.getRestUrl(requestSpy);

		assertEquals(baseUrl, standardRestUrlHttps);
	}

	@Test
	public void testGetRestUrlXForwardedProtoEmpty() {
		requestSpy.MRV.setSpecificReturnValuesSupplier("getHeader", () -> "", "X-Forwarded-Proto");
		String baseUrl = urlHandler.getRestUrl(requestSpy);

		assertEquals(baseUrl, standardRestUrlHttp);
	}

	@Test
	public void testGetRestXForwardedProtoHttpsAlreadyHttps() {
		requestSpy.MRV.setDefaultReturnValuesSupplier("getRequestURL",
				() -> new StringBuffer("https://cora.epc.ub.uu.se/systemone/rest/record/text/"));
		requestSpy.MRV.setSpecificReturnValuesSupplier("getHeader", () -> "https",
				"X-Forwarded-Proto");
		String baseUrl = urlHandler.getRestUrl(requestSpy);

		assertEquals(baseUrl, standardRestUrlHttps);
	}

	@Test
	public void testGetRestRecordUrl() {
		String baseUrl = urlHandler.getRestRecordUrl(requestSpy);

		assertEquals(baseUrl, standardRestRecordUrlHttp);
	}

	@Test
	public void testGetRestRecordUrlXForwardedProtoHttps() {
		requestSpy.MRV.setSpecificReturnValuesSupplier("getHeader", () -> "https",
				"X-Forwarded-Proto");
		String baseUrl = urlHandler.getRestRecordUrl(requestSpy);

		assertEquals(baseUrl, standardRestRecordUrlHttps);
	}

	@Test
	public void testGetRestUrlRecordXForwardedProtoEmpty() {
		requestSpy.MRV.setSpecificReturnValuesSupplier("getHeader", () -> "", "X-Forwarded-Proto");
		String baseUrl = urlHandler.getRestRecordUrl(requestSpy);

		assertEquals(baseUrl, standardRestRecordUrlHttp);
	}

	@Test
	public void testGetRestRecordXForwardedProtoHttpsAlreadyHttps() {
		requestSpy.MRV.setDefaultReturnValuesSupplier("getRequestURL",
				() -> new StringBuffer("https://cora.epc.ub.uu.se/systemone/rest/record/text/"));
		requestSpy.MRV.setSpecificReturnValuesSupplier("getHeader", () -> "https",
				"X-Forwarded-Proto");
		String baseUrl = urlHandler.getRestRecordUrl(requestSpy);

		assertEquals(baseUrl, standardRestRecordUrlHttps);
	}

	@Test
	public void testGetIiiFUrl() {
		String baseUrl = urlHandler.getIiifUrl(requestSpy);

		assertEquals(baseUrl, standardIiifUrlHttp);
	}

	@Test
	public void testGetIiiFUrlXForwardedProtoHttps() {
		requestSpy.MRV.setSpecificReturnValuesSupplier("getHeader", () -> "https",
				"X-Forwarded-Proto");
		String baseUrl = urlHandler.getIiifUrl(requestSpy);

		assertEquals(baseUrl, standardIiifUrlHttps);
	}

	@Test
	public void testGetIiifUrlXForwardedProtoEmpty() {
		requestSpy.MRV.setSpecificReturnValuesSupplier("getHeader", () -> "", "X-Forwarded-Proto");
		String baseUrl = urlHandler.getIiifUrl(requestSpy);

		assertEquals(baseUrl, standardIiifUrlHttp);
	}

	@Test
	public void testGetIiifXForwardedProtoHttpsAlreadyHttps() {
		requestSpy.MRV.setDefaultReturnValuesSupplier("getRequestURL",
				() -> new StringBuffer("https://cora.epc.ub.uu.se/systemone/rest/record/text/"));
		requestSpy.MRV.setSpecificReturnValuesSupplier("getHeader", () -> "https",
				"X-Forwarded-Proto");
		String baseUrl = urlHandler.getIiifUrl(requestSpy);

		assertEquals(baseUrl, standardIiifUrlHttps);
	}

	@Test
	public void testGetAPIUrls() {
		APIUrls apiUrls = urlHandler.getAPIUrls(requestSpy);

		assertEquals(apiUrls.baseUrl(), standardBaseUrlHttp);
		assertEquals(apiUrls.restRecordUrl(), standardRestRecordUrlHttp);
		assertEquals(apiUrls.iiifUrl(), standardIiifUrlHttp);
	}

	@Test
	public void testGetAPIUrlsXForwardedProtoHttps() {
		requestSpy.MRV.setSpecificReturnValuesSupplier("getHeader", () -> "https",
				"X-Forwarded-Proto");
		APIUrls apiUrls = urlHandler.getAPIUrls(requestSpy);

		assertEquals(apiUrls.baseUrl(), standardBaseUrlHttps);
		assertEquals(apiUrls.restRecordUrl(), standardRestRecordUrlHttps);
		assertEquals(apiUrls.iiifUrl(), standardIiifUrlHttps);
	}

	@Test
	public void testGetAPIUrlsXForwardedProtoEmpty() {
		requestSpy.MRV.setSpecificReturnValuesSupplier("getHeader", () -> "", "X-Forwarded-Proto");
		APIUrls apiUrls = urlHandler.getAPIUrls(requestSpy);

		assertEquals(apiUrls.baseUrl(), standardBaseUrlHttp);
		assertEquals(apiUrls.restRecordUrl(), standardRestRecordUrlHttp);
		assertEquals(apiUrls.iiifUrl(), standardIiifUrlHttp);
	}

	@Test
	public void testGetAPIUrlsXForwardedProtoHttpsAlreadyHttps() {
		requestSpy.MRV.setDefaultReturnValuesSupplier("getRequestURL",
				() -> new StringBuffer("https://cora.epc.ub.uu.se/systemone/rest/record/text/"));
		requestSpy.MRV.setSpecificReturnValuesSupplier("getHeader", () -> "https",
				"X-Forwarded-Proto");
		APIUrls apiUrls = urlHandler.getAPIUrls(requestSpy);

		assertEquals(apiUrls.baseUrl(), standardBaseUrlHttps);
		assertEquals(apiUrls.restRecordUrl(), standardRestRecordUrlHttps);
		assertEquals(apiUrls.iiifUrl(), standardIiifUrlHttps);
	}
}
