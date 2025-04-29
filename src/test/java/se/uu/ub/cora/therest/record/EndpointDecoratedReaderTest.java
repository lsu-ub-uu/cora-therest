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

import static org.testng.Assert.assertEquals;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import se.uu.ub.cora.spider.dependency.SpiderInstanceProvider;
import se.uu.ub.cora.spider.spies.DecoratedRecordReaderSpy;
import se.uu.ub.cora.spider.spies.SpiderInstanceFactorySpy;
import se.uu.ub.cora.therest.spy.EndpointConverterSpy;
import se.uu.ub.cora.therest.spy.ErrorHandlerSpy;

public class EndpointDecoratedReaderTest {

	private EndpointDecoratedReader reader;
	private SpiderInstanceFactorySpy spiderInstanceFactorySpy;
	private EndpointConverterSpy endpointConverter;
	private ErrorHandlerSpy errorHandler;
	private HttpServletRequestSpy someRequest;

	@BeforeMethod
	private void beforeMethod() {
		spiderInstanceFactorySpy = new SpiderInstanceFactorySpy();
		endpointConverter = new EndpointConverterSpy();
		errorHandler = new ErrorHandlerSpy();
		SpiderInstanceProvider.setSpiderInstanceFactory(spiderInstanceFactorySpy);
		someRequest = new HttpServletRequestSpy();

		reader = new EndpointDecoratedReaderImp(endpointConverter, errorHandler);
	}

	@Test
	public void testReadAndDecorateRecord_goesWrong() {
		RuntimeException thrownError = new RuntimeException();
		spiderInstanceFactorySpy.MRV.setAlwaysThrowException("factorDecoratedRecordReader",
				thrownError);

		Response responseNOK = reader.readAndDecorateRecord(someRequest, "someAccept",
				"someAuthToken", "sometype", "someId");

		errorHandler.MCR.assertParameters("handleError", 0, "someAuthToken", thrownError,
				"Error reading record with recordType: sometype and " + "recordId: someId.");
		errorHandler.MCR.assertReturn("handleError", 0, responseNOK);

	}

	@Test
	public void testInti() {
		Response responseOK = reader.readAndDecorateRecord(someRequest, "someAccept",
				"someAuthToken", "sometype", "someId");

		var decoratedRecordReader = (DecoratedRecordReaderSpy) spiderInstanceFactorySpy.MCR
				.assertCalledParametersReturn("factorDecoratedRecordReader");
		var dataRecord = decoratedRecordReader.MCR.assertCalledParametersReturn(
				"readDecoratedRecord", "someAuthToken", "sometype", "someId");
		endpointConverter.MCR.assertParameters("convertConvertibleToString", 0, someRequest,
				"someAccept", dataRecord);
		endpointConverter.MCR.assertReturn("convertConvertibleToString", 0, responseOK.getEntity());

		assertEquals(responseOK.getStatusInfo(), Response.Status.OK);
		assertEquals(responseOK.getHeaderString(HttpHeaders.CONTENT_TYPE), "someAccept");
	}

}
