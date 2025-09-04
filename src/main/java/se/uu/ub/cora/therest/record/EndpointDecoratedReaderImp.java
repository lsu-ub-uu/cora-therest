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

import java.text.MessageFormat;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import se.uu.ub.cora.data.DataRecord;
import se.uu.ub.cora.spider.dependency.SpiderInstanceProvider;
import se.uu.ub.cora.therest.converter.EndpointConverter;
import se.uu.ub.cora.therest.error.ErrorHandler;

public class EndpointDecoratedReaderImp implements EndpointDecoratedReader {

	private EndpointConverter endpointConverter;
	private ErrorHandler errorHandler;

	public EndpointDecoratedReaderImp(EndpointConverter endpointConverter,
			ErrorHandler errorHandler) {
		this.endpointConverter = endpointConverter;
		this.errorHandler = errorHandler;
	}

	@Override
	public Response readAndDecorateRecord(HttpServletRequest request, String accept,
			String authToken, String type, String id) {
		try {
			return tryReadRecord(request, accept, authToken, type, id);
		} catch (Exception error) {
			String errorFromCaller = "Error reading record with recordType: {0} and "
					+ "recordId: {1}.";
			return errorHandler.handleError(authToken, error,
					MessageFormat.format(errorFromCaller, type, id));
		}
	}

	private Response tryReadRecord(HttpServletRequest request, String accept, String authToken,
			String type, String id) {
		var decoratedRecordReader = SpiderInstanceProvider.getDecoratedRecordReader();
		DataRecord dataRecord = decoratedRecordReader.readDecoratedRecord(authToken, type, id);
		String convertedDataRecord = endpointConverter.convertConvertibleToString(request, accept,
				dataRecord);
		return Response.status(Response.Status.OK).header(HttpHeaders.CONTENT_TYPE, accept)
				.entity(convertedDataRecord).build();
	}

	public EndpointConverter onlyForTestGetEndpointConverter() {
		return endpointConverter;
	}

	public ErrorHandler onlyForTestGetErrorHandler() {
		return errorHandler;
	}
}
