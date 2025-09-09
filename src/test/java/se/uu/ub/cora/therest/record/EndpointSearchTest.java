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
package se.uu.ub.cora.therest.record;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertSame;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataList;
import se.uu.ub.cora.spider.dependency.SpiderInstanceProvider;
import se.uu.ub.cora.spider.record.RecordSearcher;
import se.uu.ub.cora.spider.spies.RecordSearcherSpy;
import se.uu.ub.cora.spider.spies.SpiderInstanceFactorySpy;
import se.uu.ub.cora.therest.dependency.TheRestInstanceFactorySpy;
import se.uu.ub.cora.therest.dependency.TheRestInstanceProvider;
import se.uu.ub.cora.therest.spy.EndpointIncomingConverterSpy;
import se.uu.ub.cora.therest.spy.EndpointOutgoingConverterSpy;
import se.uu.ub.cora.therest.spy.ErrorHandlerSpy;
import se.uu.ub.cora.therest.url.APIUrls;
import se.uu.ub.cora.therest.url.HttpServletRequestSpy;
import se.uu.ub.cora.therest.url.UrlHandlerSpy;

public class EndpointSearchTest {
	private static final String SEARCH_ID = "aSearchId";
	private static final String ACCEPT = "someAccept";
	private static final String QUERY_AUTH_TOKEN = "queryAuthToken";
	private static final String HEADER_AUTH_TOKEN = "headerAuthToken";
	private static final String SEARCH_DATA = "someSearchData";

	private SpiderInstanceFactorySpy spiderInstanceFactorySpy;
	private Response response;
	private HttpServletRequestSpy requestSpy;

	private TheRestInstanceFactorySpy instanceFactory;
	String headerAuthToken = "";
	String queryAuthToken = "";
	String searchId = "";
	String searchDataAsString = "";
	private EndpointSearch endpointSearch;
	private RecordSearcherSpy recordSearcher;

	@BeforeMethod
	private void beforeMethod() {
		spiderInstanceFactorySpy = new SpiderInstanceFactorySpy();
		SpiderInstanceProvider.setSpiderInstanceFactory(spiderInstanceFactorySpy);

		instanceFactory = new TheRestInstanceFactorySpy();
		TheRestInstanceProvider.onlyForTestSetTheRestInstanceFactory(instanceFactory);

		requestSpy = new HttpServletRequestSpy();

		recordSearcher = new RecordSearcherSpy();
		endpointSearch = new EndpointSearchImp(recordSearcher);
	}

	@AfterMethod
	public void afterMethod() {
		TheRestInstanceProvider.onlyForTestResetTheRestInstanceFactory();
	}

	@Test
	public void testSearchRecordRightTokenSentAsHeaderParam() {
		response = endpointSearch.searchRecord(requestSpy, ACCEPT, HEADER_AUTH_TOKEN, null,
				SEARCH_ID, SEARCH_DATA);
		assertAuthTokenSentToRecordSearch(HEADER_AUTH_TOKEN);
	}

	@Test
	public void testSearchRecordRightTokenSentAsQueryParam() {
		response = endpointSearch.searchRecord(requestSpy, ACCEPT, null, QUERY_AUTH_TOKEN,
				SEARCH_ID, SEARCH_DATA);
		assertAuthTokenSentToRecordSearch(QUERY_AUTH_TOKEN);
	}

	@Test
	public void testSearchRecordRightTokenSentAsBothHeaderAndQueryParam() {
		response = endpointSearch.searchRecord(requestSpy, ACCEPT, HEADER_AUTH_TOKEN,
				QUERY_AUTH_TOKEN, SEARCH_ID, SEARCH_DATA);
		assertAuthTokenSentToRecordSearch(HEADER_AUTH_TOKEN);
	}

	private void assertAuthTokenSentToRecordSearch(String authToken) {
		assertNotNull(authToken);
		recordSearcher.MCR.assertParameter("search", 0, "authToken", authToken);
	}

	@Test
	public void testSearchRecord() {
		response = endpointSearch.searchRecord(requestSpy, ACCEPT, HEADER_AUTH_TOKEN,
				QUERY_AUTH_TOKEN, SEARCH_ID, SEARCH_DATA);

		DataGroup searchData = assertConversionOfAndReturnSearchDataAsElement(SEARCH_DATA);
		DataList dataRecordList = assertSearchCalledAndReturnResult(searchData);
		assertComponentsCalledCorrectlyForContentType(ACCEPT, response, dataRecordList);
	}

	private DataGroup assertConversionOfAndReturnSearchDataAsElement(String string) {
		var endpointConverter = (EndpointIncomingConverterSpy) instanceFactory.MCR
				.getReturnValue("factorEndpointIncomingConverter", 0);
		return (DataGroup) endpointConverter.MCR
				.assertCalledParametersReturn("convertStringToConvertible", string);
	}

	private void assertComponentsCalledCorrectlyForContentType(String contentType,
			Response response, DataList dataRecordList) {
		APIUrls apiUrls = assertUrlsCalculatedFromRequestAndReturn();
		String result = assertConverterCalledAndReturn(contentType, dataRecordList, apiUrls);
		assertResponse(contentType, response, result);
	}

	private DataList assertSearchCalledAndReturnResult(DataGroup searchData) {
		return (DataList) recordSearcher.MCR.assertCalledParametersReturn("search",
				HEADER_AUTH_TOKEN, SEARCH_ID, searchData);
	}

	private APIUrls assertUrlsCalculatedFromRequestAndReturn() {
		var urlHandler = (UrlHandlerSpy) instanceFactory.MCR.getReturnValue("factorUrlHandler", 0);
		return (APIUrls) urlHandler.MCR.assertCalledParametersReturn("getAPIUrls", requestSpy);
	}

	private String assertConverterCalledAndReturn(String contentType, DataList dataRecordList,
			APIUrls apiUrls) {
		var endpointConverter = (EndpointOutgoingConverterSpy) instanceFactory.MCR
				.getReturnValue("factorEndpointOutgoingConverter", 0);
		endpointConverter.MCR.assertParameters("convertConvertibleToString", 0, apiUrls,
				contentType, dataRecordList);
		return (String) endpointConverter.MCR.getReturnValue("convertConvertibleToString", 0);
	}

	private void assertResponse(String contentType, Response response, String result) {
		assertEquals(response.getStatusInfo(), Response.Status.OK);
		assertEquals(response.getHeaderString(HttpHeaders.CONTENT_TYPE), contentType);
		assertEquals(response.getEntity(), result);
	}

	@Test
	public void testSearchRecord_goesWrong() {
		RuntimeException thrownError = new RuntimeException();
		recordSearcher.MRV.setAlwaysThrowException("search", thrownError);

		response = endpointSearch.searchRecord(requestSpy, ACCEPT, HEADER_AUTH_TOKEN,
				QUERY_AUTH_TOKEN, SEARCH_ID, SEARCH_DATA);

		assertErrorHandler(thrownError);
	}

	private void assertErrorHandler(RuntimeException thrownError) {
		ErrorHandlerSpy errorHandler = (ErrorHandlerSpy) instanceFactory.MCR
				.getReturnValue("factorErrorHandler", 0);

		errorHandler.MCR.assertParameters("handleError", 0, HEADER_AUTH_TOKEN, thrownError,
				"Error searching record with searchId: aSearchId.");
		errorHandler.MCR.assertReturn("handleError", 0, response);
	}

	@Test
	public void testOnlyForTestGetRecordSearcher() {
		RecordSearcher returnedRecordSearcher = ((EndpointSearchImp) endpointSearch)
				.onlyForTestGetRecordSearcher();
		assertSame(returnedRecordSearcher, recordSearcher);
	}

}
