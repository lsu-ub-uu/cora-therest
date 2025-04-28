/*
 * Copyright 2015, 2016, 2018, 2021, 2024, 2025 Uppsala University Library
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

import java.net.URISyntaxException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import se.uu.ub.cora.converter.ConverterException;
import se.uu.ub.cora.converter.ExternalUrls;
import se.uu.ub.cora.data.converter.ConversionException;
import se.uu.ub.cora.initialize.SettingsProvider;
import se.uu.ub.cora.json.parser.JsonParseException;
import se.uu.ub.cora.logger.Logger;
import se.uu.ub.cora.logger.LoggerProvider;
import se.uu.ub.cora.spider.authentication.AuthenticationException;
import se.uu.ub.cora.spider.authorization.AuthorizationException;
import se.uu.ub.cora.spider.binary.ArchiveDataIntergrityException;
import se.uu.ub.cora.spider.binary.ResourceInputStream;
import se.uu.ub.cora.spider.data.DataMissingException;
import se.uu.ub.cora.spider.dependency.SpiderInstanceProvider;
import se.uu.ub.cora.spider.record.ConflictException;
import se.uu.ub.cora.spider.record.DataException;
import se.uu.ub.cora.spider.record.MisuseException;
import se.uu.ub.cora.spider.record.RecordNotFoundException;
import se.uu.ub.cora.spider.record.ResourceNotFoundException;
import se.uu.ub.cora.storage.RecordConflictException;

@Path("/")
public class RecordEndpointDownload {
	private static final String TEXT_PLAIN_CHARSET_UTF_8 = "text/plain; charset=utf-8";
	private static final int AFTERHTTP = 10;
	HttpServletRequest request;
	private Logger log = LoggerProvider.getLoggerForClass(RecordEndpointDownload.class);

	private ExternalUrls externalUrls;
	private se.uu.ub.cora.data.converter.ExternalUrls externalUrlsForJson;

	public RecordEndpointDownload(@Context HttpServletRequest req) {
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

	private Response handleError(String authToken, Exception error, String errorFromCaller) {
		if (error instanceof ConflictException || error instanceof RecordConflictException) {
			return buildResponseIncludingMessage(error, Response.Status.CONFLICT);
		}

		if (error instanceof MisuseException) {
			return buildResponseIncludingMessage(error, Response.Status.METHOD_NOT_ALLOWED);
		}

		if (errorIsCausedByDataProblem(error)) {
			return Response.status(Response.Status.BAD_REQUEST)
					.entity(errorFromCaller + " " + error.getMessage())
					.header(HttpHeaders.CONTENT_TYPE, TEXT_PLAIN_CHARSET_UTF_8).build();
		}

		if (error instanceof se.uu.ub.cora.storage.RecordNotFoundException
				|| error instanceof RecordNotFoundException
				|| error instanceof ResourceNotFoundException) {
			return Response.status(Response.Status.NOT_FOUND)
					.entity(errorFromCaller + " " + error.getMessage())
					.header(HttpHeaders.CONTENT_TYPE, TEXT_PLAIN_CHARSET_UTF_8).build();
		}

		if (error instanceof URISyntaxException) {
			return buildResponse(Response.Status.BAD_REQUEST);
		}

		if (error instanceof AuthorizationException) {
			return handleAuthorizationException(authToken);
		}

		if (error instanceof AuthenticationException) {
			return buildResponse(Response.Status.UNAUTHORIZED);
		}
		log.logErrorUsingMessageAndException("Error handling request: " + error.getMessage(),
				error);
		return buildResponseIncludingMessage(error, Response.Status.INTERNAL_SERVER_ERROR);
	}

	private boolean errorIsCausedByDataProblem(Exception error) {
		return error instanceof ConverterException || errorDuringJsonConversion(error)
				|| error instanceof DataException || error instanceof DataMissingException
				|| error instanceof ArchiveDataIntergrityException;
	}

	private boolean errorDuringJsonConversion(Exception error) {
		return error instanceof JsonParseException || error instanceof ConversionException;
	}

	private Response handleAuthorizationException(String authToken) {
		if (authToken == null) {
			return buildResponse(Response.Status.UNAUTHORIZED);
		}
		return buildResponse(Response.Status.FORBIDDEN);
	}

	private Response buildResponseIncludingMessage(Exception error, Status status) {
		return Response.status(status).entity(error.getMessage())
				.header(HttpHeaders.CONTENT_TYPE, TEXT_PLAIN_CHARSET_UTF_8).build();
	}

	private Response buildResponse(Status status) {
		return Response.status(status).header(HttpHeaders.CONTENT_TYPE, TEXT_PLAIN_CHARSET_UTF_8)
				.build();
	}

	@GET
	@Path("{type}/{id}/{resourceType}")
	public Response downloadResource(@HeaderParam("authToken") String headerAuthToken,
			@QueryParam("authToken") String queryAuthToken, @PathParam("type") String type,
			@PathParam("id") String id, @PathParam("resourceType") String resourceType) {
		String usedToken = getExistingTokenPreferHeader(headerAuthToken, queryAuthToken);
		return downloadResourceUsingAuthTokenWithStream(usedToken, type, id, resourceType);
	}

	private String getExistingTokenPreferHeader(String headerAuthToken, String queryAuthToken) {
		return headerAuthToken != null ? headerAuthToken : queryAuthToken;
	}

	Response downloadResourceUsingAuthTokenWithStream(String authToken, String type, String id,
			String resourceType) {
		try {
			return tryDownloadResource(authToken, type, id, resourceType);
		} catch (Exception error) {
			return handleError(authToken, error,
					"An error has ocurred while downloading a resource:");
		}
	}

	private Response tryDownloadResource(String authToken, String type, String id,
			String resourceType) {
		ResourceInputStream streamOut = SpiderInstanceProvider.getDownloader().download(authToken,
				type, id, resourceType);
		/*
		 * when we detect and store type of file in spider set it like this return
		 * Response.ok(streamOut.stream).type("application/octet-stream")
		 */
		return Response.ok(streamOut.stream).type(streamOut.mimeType)
				.header("Content-Disposition", "attachment; filename=" + streamOut.name)
				.header("Content-Length", streamOut.size).build();
	}
}
