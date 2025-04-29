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
package se.uu.ub.cora.therest.error;

import static org.testng.Assert.assertEquals;

import java.net.URISyntaxException;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import se.uu.ub.cora.converter.ConverterException;
import se.uu.ub.cora.data.converter.ConversionException;
import se.uu.ub.cora.json.parser.JsonParseException;
import se.uu.ub.cora.logger.LoggerProvider;
import se.uu.ub.cora.logger.spies.LoggerFactorySpy;
import se.uu.ub.cora.logger.spies.LoggerSpy;
import se.uu.ub.cora.spider.authentication.AuthenticationException;
import se.uu.ub.cora.spider.authorization.AuthorizationException;
import se.uu.ub.cora.spider.binary.ArchiveDataIntergrityException;
import se.uu.ub.cora.spider.data.DataMissingException;
import se.uu.ub.cora.spider.record.ConflictException;
import se.uu.ub.cora.spider.record.DataException;
import se.uu.ub.cora.spider.record.MisuseException;
import se.uu.ub.cora.spider.record.RecordNotFoundException;
import se.uu.ub.cora.spider.record.ResourceNotFoundException;
import se.uu.ub.cora.storage.RecordConflictException;

public class ErrorHandlerTest {

	private static final String MESSAGE_FROM_EXCEPTION = "someExeption";
	private static final String MESSAGE_FROM_CALLER = "someMessage";
	private static final String TEXT_PLAIN_CHARSET_UTF_8 = "text/plain; charset=utf-8";
	private ErrorHandlerImp error;
	private LoggerFactorySpy loggerFactory;

	@BeforeMethod
	private void beforeMethod() {
		loggerFactory = new LoggerFactorySpy();
		LoggerProvider.setLoggerFactory(loggerFactory);

		error = new ErrorHandlerImp();
	}

	@Test
	public void testInit() {
		loggerFactory.MCR.assertParameters("factorForClass", 0, ErrorHandlerImp.class);
	}

	// TODO: why we do not used the message passed from the caller??? instead we use/expose and
	// internal error message.
	@Test
	public void test_InternalServerError() {
		Exception undefinedException = new RuntimeException(MESSAGE_FROM_EXCEPTION);

		Response response = error.handleError("someAuthToken", undefinedException,
				MESSAGE_FROM_CALLER);

		var logger = (LoggerSpy) loggerFactory.MCR.getReturnValue("factorForClass", 0);
		logger.MCR.assertParameters("logErrorUsingMessageAndException", 0,
				"Error handling request: " + MESSAGE_FROM_EXCEPTION, undefinedException);
		assertResponse(response, Response.Status.INTERNAL_SERVER_ERROR, MESSAGE_FROM_EXCEPTION);
	}

	@Test
	public void test_Conflict_ConflictException() {
		Response response = error.handleError("someAuthToken",
				ConflictException.withMessage(MESSAGE_FROM_EXCEPTION), MESSAGE_FROM_CALLER);

		assertResponse(response, Response.Status.CONFLICT, MESSAGE_FROM_EXCEPTION);
	}

	@Test
	public void test_Conflict_RecordConflictException() {
		Response response = error.handleError("someAuthToken",
				RecordConflictException.withMessage(MESSAGE_FROM_EXCEPTION), MESSAGE_FROM_CALLER);

		assertResponse(response, Response.Status.CONFLICT, MESSAGE_FROM_EXCEPTION);
	}

	@Test
	public void test_MethodNotAllowed() {
		Response response = error.handleError("someAuthToken",
				new MisuseException(MESSAGE_FROM_EXCEPTION), MESSAGE_FROM_CALLER);

		assertResponse(response, Response.Status.METHOD_NOT_ALLOWED, MESSAGE_FROM_EXCEPTION);
	}

	@Test
	public void test_Forbidden_AuthorizationException() {
		Response response = error.handleError("someAuthToken",
				new AuthorizationException(MESSAGE_FROM_EXCEPTION), MESSAGE_FROM_CALLER);

		assertResponse(response, Response.Status.FORBIDDEN, null);
	}

	@Test
	public void test_Unauthorized_AuthorizationException() {
		Response response = error.handleError(null,
				new AuthorizationException(MESSAGE_FROM_EXCEPTION), MESSAGE_FROM_CALLER);

		assertResponse(response, Response.Status.UNAUTHORIZED, null);
	}

	@Test
	public void test_Unauthorized_AuthenticationException() {
		Response response = error.handleError("someAuthToken",
				new AuthenticationException(MESSAGE_FROM_EXCEPTION), MESSAGE_FROM_CALLER);

		assertResponse(response, Response.Status.UNAUTHORIZED, null);
	}

	@Test
	public void test_BadRequest_URISyntaxException() {
		Response response = error.handleError("someAuthToken",
				new URISyntaxException("input", MESSAGE_FROM_EXCEPTION), MESSAGE_FROM_CALLER);

		assertResponse(response, Response.Status.BAD_REQUEST, null);
	}

	@Test
	public void test_BadRequest_AuthenticationException() {
		Response response = error.handleError("someAuthToken",
				new ConverterException(MESSAGE_FROM_EXCEPTION), MESSAGE_FROM_CALLER);

		assertResponse(response, Response.Status.BAD_REQUEST,
				MESSAGE_FROM_CALLER + " " + MESSAGE_FROM_EXCEPTION);
	}

	@Test
	public void test_BadRequest_DataException() {
		Response response = error.handleError("someAuthToken",
				new DataException(MESSAGE_FROM_EXCEPTION), MESSAGE_FROM_CALLER);

		assertResponse(response, Response.Status.BAD_REQUEST,
				MESSAGE_FROM_CALLER + " " + MESSAGE_FROM_EXCEPTION);
	}

	@Test
	public void test_BadRequest_DataMissingException() {
		Response response = error.handleError("someAuthToken",
				new DataMissingException(MESSAGE_FROM_EXCEPTION), MESSAGE_FROM_CALLER);

		assertResponse(response, Response.Status.BAD_REQUEST,
				MESSAGE_FROM_CALLER + " " + MESSAGE_FROM_EXCEPTION);
	}

	@Test
	public void test_BadRequest_ArchiveDataIntergrityException() {
		Response response = error.handleError("someAuthToken",
				new ArchiveDataIntergrityException(MESSAGE_FROM_EXCEPTION), MESSAGE_FROM_CALLER);

		assertResponse(response, Response.Status.BAD_REQUEST,
				MESSAGE_FROM_CALLER + " " + MESSAGE_FROM_EXCEPTION);
	}

	@Test
	public void test_BadRequest_JsonParseException() {
		Response response = error.handleError("someAuthToken",
				new JsonParseException(MESSAGE_FROM_EXCEPTION), MESSAGE_FROM_CALLER);

		assertResponse(response, Response.Status.BAD_REQUEST,
				MESSAGE_FROM_CALLER + " " + MESSAGE_FROM_EXCEPTION);
	}

	@Test
	public void test_BadRequest_ConversionException() {
		Response response = error.handleError("someAuthToken",
				new ConversionException(MESSAGE_FROM_EXCEPTION), MESSAGE_FROM_CALLER);

		assertResponse(response, Response.Status.BAD_REQUEST,
				MESSAGE_FROM_CALLER + " " + MESSAGE_FROM_EXCEPTION);
	}

	@Test
	public void test_NotFound_RecordNotFoundException() {
		Response response = error.handleError("someAuthToken",
				RecordNotFoundException.withMessage(MESSAGE_FROM_EXCEPTION), MESSAGE_FROM_CALLER);

		assertResponse(response, Response.Status.NOT_FOUND,
				MESSAGE_FROM_CALLER + " " + MESSAGE_FROM_EXCEPTION);
	}

	@Test
	public void test_NotFound_StorageRecordNotFoundException() {
		Response response = error.handleError("someAuthToken",
				se.uu.ub.cora.storage.RecordNotFoundException.withMessage(MESSAGE_FROM_EXCEPTION),
				MESSAGE_FROM_CALLER);

		assertResponse(response, Response.Status.NOT_FOUND,
				MESSAGE_FROM_CALLER + " " + MESSAGE_FROM_EXCEPTION);
	}

	@Test
	public void test_NotFound_ResourceNotFoundException() {
		Response response = error.handleError("someAuthToken",
				ResourceNotFoundException.withMessage(MESSAGE_FROM_EXCEPTION), MESSAGE_FROM_CALLER);

		assertResponse(response, Response.Status.NOT_FOUND,
				MESSAGE_FROM_CALLER + " " + MESSAGE_FROM_EXCEPTION);
	}

	private void assertResponse(Response response, Status status, String entityMessage) {
		assertEquals(response.getStatusInfo(), status);
		assertEquals(response.getEntity(), entityMessage);
		assertEquals(response.getHeaderString(HttpHeaders.CONTENT_TYPE), TEXT_PLAIN_CHARSET_UTF_8);
	}

}
