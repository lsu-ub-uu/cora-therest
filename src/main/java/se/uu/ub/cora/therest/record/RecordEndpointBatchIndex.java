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

import java.net.URI;
import java.net.URISyntaxException;
import java.text.MessageFormat;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import se.uu.ub.cora.converter.ConverterException;
import se.uu.ub.cora.converter.ConverterProvider;
import se.uu.ub.cora.converter.ExternalUrls;
import se.uu.ub.cora.converter.ExternallyConvertibleToStringConverter;
import se.uu.ub.cora.converter.StringToExternallyConvertibleConverter;
import se.uu.ub.cora.data.Convertible;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataRecord;
import se.uu.ub.cora.data.ExternallyConvertible;
import se.uu.ub.cora.data.converter.ConversionException;
import se.uu.ub.cora.data.converter.DataToJsonConverter;
import se.uu.ub.cora.data.converter.DataToJsonConverterFactory;
import se.uu.ub.cora.data.converter.DataToJsonConverterProvider;
import se.uu.ub.cora.data.converter.JsonToDataConverter;
import se.uu.ub.cora.data.converter.JsonToDataConverterProvider;
import se.uu.ub.cora.initialize.SettingsProvider;
import se.uu.ub.cora.json.parser.JsonParseException;
import se.uu.ub.cora.json.parser.JsonParser;
import se.uu.ub.cora.json.parser.JsonValue;
import se.uu.ub.cora.json.parser.org.OrgJsonParser;
import se.uu.ub.cora.logger.Logger;
import se.uu.ub.cora.logger.LoggerProvider;
import se.uu.ub.cora.spider.authentication.AuthenticationException;
import se.uu.ub.cora.spider.authorization.AuthorizationException;
import se.uu.ub.cora.spider.binary.ArchiveDataIntergrityException;
import se.uu.ub.cora.spider.data.DataMissingException;
import se.uu.ub.cora.spider.dependency.SpiderInstanceProvider;
import se.uu.ub.cora.spider.record.ConflictException;
import se.uu.ub.cora.spider.record.DataException;
import se.uu.ub.cora.spider.record.MisuseException;
import se.uu.ub.cora.spider.record.RecordListIndexer;
import se.uu.ub.cora.spider.record.RecordNotFoundException;
import se.uu.ub.cora.spider.record.ResourceNotFoundException;
import se.uu.ub.cora.storage.RecordConflictException;

@Path("/")
public class RecordEndpointBatchIndex {
	private static final String APPLICATION_VND_UUB_RECORD_XML = "application/vnd.uub.record+xml";
	private static final String APPLICATION_VND_UUB_RECORD_JSON = "application/vnd.uub.record+json";
	private static final String APPLICATION_VND_UUB_RECORD_JSON_QS09 = "application/vnd.uub.record+json;qs=0.9";
	private static final String TEXT_PLAIN_CHARSET_UTF_8 = "text/plain; charset=utf-8";
	private static final String URL_DELIMITER = "/";
	private static final int AFTERHTTP = 10;
	HttpServletRequest request;
	private Logger log = LoggerProvider.getLoggerForClass(RecordEndpointBatchIndex.class);

	private JsonParser jsonParser = new OrgJsonParser();
	private ExternalUrls externalUrls;
	private se.uu.ub.cora.data.converter.ExternalUrls externalUrlsForJson;

	public RecordEndpointBatchIndex(@Context HttpServletRequest req) {
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

	private DataGroup convertStringToDataGroup(String accept, String input) {
		if (accept.endsWith("+xml")) {
			return convertXmlToDataElement(input);
		} else {
			return convertJsonStringToDataGroup(input);
		}
	}

	private DataGroup convertXmlToDataElement(String input) {
		StringToExternallyConvertibleConverter xmlToConvertibleConverter = ConverterProvider
				.getStringToExternallyConvertibleConverter("xml");
		return (DataGroup) xmlToConvertibleConverter.convert(input);
	}

	private DataGroup convertJsonStringToDataGroup(String jsonRecord) {
		JsonValue jsonValue = jsonParser.parseString(jsonRecord);
		JsonToDataConverter jsonToDataConverter = JsonToDataConverterProvider
				.getConverterUsingJsonObject(jsonValue);
		return (DataGroup) jsonToDataConverter.toInstance();
	}

	private String convertDataToJson(ExternallyConvertible convertible) {
		DataToJsonConverterFactory dataToJsonConverterFactory = DataToJsonConverterProvider
				.createImplementingFactory();
		DataToJsonConverter converter = dataToJsonConverterFactory
				.factorUsingConvertibleAndExternalUrls((Convertible) convertible,
						externalUrlsForJson);
		return converter.toJsonCompactFormat();
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

	private String convertConvertibleToString(String accept, ExternallyConvertible convertible) {
		if (accept.endsWith("xml")) {
			return convertDataToXml(convertible);
		} else {
			return convertDataToJson(convertible);
		}
	}

	private String convertDataToXml(ExternallyConvertible convertible) {
		ExternallyConvertibleToStringConverter convertibleToXmlConverter = ConverterProvider
				.getExternallyConvertibleToStringConverter("xml");

		return convertibleToXmlConverter.convertWithLinks(convertible, externalUrls);
	}

	private String getExistingTokenPreferHeader(String headerAuthToken, String queryAuthToken) {
		return headerAuthToken != null ? headerAuthToken : queryAuthToken;
	}

	@POST
	@Path("index/{type}")
	@Consumes({ APPLICATION_VND_UUB_RECORD_JSON })
	@Produces({ APPLICATION_VND_UUB_RECORD_JSON_QS09 })
	public Response batchIndexJsonJson(@HeaderParam("authToken") String headerAuthToken,
			@QueryParam("authToken") String queryAuthToken, @PathParam("type") String type,
			String indexSettingsAsJson) {
		return batchIndex(APPLICATION_VND_UUB_RECORD_JSON, APPLICATION_VND_UUB_RECORD_JSON,
				headerAuthToken, queryAuthToken, type, indexSettingsAsJson);
	}

	@POST
	@Path("index/{type}")
	@Consumes(APPLICATION_VND_UUB_RECORD_JSON)
	@Produces(APPLICATION_VND_UUB_RECORD_XML)
	public Response batchIndexJsonXml(@HeaderParam("authToken") String headerAuthToken,
			@QueryParam("authToken") String queryAuthToken, @PathParam("type") String type,
			String indexSettingsAsJson) {
		return batchIndex(APPLICATION_VND_UUB_RECORD_JSON, APPLICATION_VND_UUB_RECORD_XML,
				headerAuthToken, queryAuthToken, type, indexSettingsAsJson);
	}

	@POST
	@Path("index/{type}")
	@Consumes(APPLICATION_VND_UUB_RECORD_XML)
	@Produces(APPLICATION_VND_UUB_RECORD_JSON_QS09)
	public Response batchIndexXmlJson(@HeaderParam("authToken") String headerAuthToken,
			@QueryParam("authToken") String queryAuthToken, @PathParam("type") String type,
			String indexSettingsAsJson) {
		return batchIndex(APPLICATION_VND_UUB_RECORD_XML, APPLICATION_VND_UUB_RECORD_JSON,
				headerAuthToken, queryAuthToken, type, indexSettingsAsJson);
	}

	@POST
	@Path("index/{type}")
	@Consumes(APPLICATION_VND_UUB_RECORD_XML)
	@Produces(APPLICATION_VND_UUB_RECORD_XML)
	public Response batchIndexXmlXml(@HeaderParam("authToken") String headerAuthToken,
			@QueryParam("authToken") String queryAuthToken, @PathParam("type") String type,
			String indexSettingsAsJson) {
		return batchIndex(APPLICATION_VND_UUB_RECORD_XML, APPLICATION_VND_UUB_RECORD_XML,
				headerAuthToken, queryAuthToken, type, indexSettingsAsJson);
	}

	private Response batchIndex(String contentType, String accept, String headerAuthToken,
			String queryAuthToken, String type, String indexSettingsAsJson) {
		String usedToken = getExistingTokenPreferHeader(headerAuthToken, queryAuthToken);

		String resolvedContentType = calculateContentTypeToUse(contentType, indexSettingsAsJson);
		String jsonIndexSettings = createEmptyIndexSettingIfParameterDoesNotExist(
				indexSettingsAsJson);

		return indexRecordListUsingAuthTokenByType(resolvedContentType, accept, usedToken, type,
				jsonIndexSettings);
	}

	private String calculateContentTypeToUse(String contentType, String indexSettingsAsJson) {
		if (indexSettingsAsJson == null || indexSettingsAsJson.isEmpty()) {
			return APPLICATION_VND_UUB_RECORD_JSON;
		}
		return contentType;
	}

	private String createEmptyIndexSettingIfParameterDoesNotExist(String indexSettingsAsJson) {
		if (indexSettingsAsJson == null || indexSettingsAsJson.isEmpty()) {
			return "{\"name\":\"indexSettings\",\"children\":[]}";
		}
		return indexSettingsAsJson;
	}

	private Response indexRecordListUsingAuthTokenByType(String contentType, String accept,
			String authToken, String type, String filterAsJson) {
		try {
			return tryIndexRecordList(contentType, accept, authToken, type, filterAsJson);

		} catch (Exception error) {
			String errorFromCaller = "Error indexing records with recordType: {0}.";
			return handleError(authToken, error, MessageFormat.format(errorFromCaller, type));
		}
	}

	private Response tryIndexRecordList(String contentType, String accept, String authToken,
			String type, String jsonIndexSettings) throws URISyntaxException {
		DataGroup indexSettings = convertStringToDataGroup(contentType, jsonIndexSettings);

		RecordListIndexer indexBatchJobCreator = SpiderInstanceProvider.getRecordListIndexer();
		DataRecord indexBatchJob = indexBatchJobCreator.indexRecordList(authToken, type,
				indexSettings);

		String createdId = indexBatchJob.getId();

		String outputRecord = convertConvertibleToString(accept, indexBatchJob);

		URI uri = new URI("indexBatchJob" + URL_DELIMITER + createdId);
		return Response.created(uri).header(HttpHeaders.CONTENT_TYPE, accept).entity(outputRecord)
				.build();
	}

	JsonParser getJsonParser() {
		return jsonParser;
	}

	void setJsonParser(JsonParser jsonParser) {
		this.jsonParser = jsonParser;
	}
}
