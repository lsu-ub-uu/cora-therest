/*
 * Copyright 2015, 2016, 2018, 2021, 2024 Uppsala University Library
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

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.MessageFormat;

import org.glassfish.jersey.media.multipart.FormDataParam;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
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
import se.uu.ub.cora.data.DataList;
import se.uu.ub.cora.data.DataRecord;
import se.uu.ub.cora.data.ExternallyConvertible;
import se.uu.ub.cora.data.converter.ConversionException;
import se.uu.ub.cora.data.converter.DataToJsonConverter;
import se.uu.ub.cora.data.converter.DataToJsonConverterFactory;
import se.uu.ub.cora.data.converter.DataToJsonConverterProvider;
import se.uu.ub.cora.data.converter.JsonToDataConverter;
import se.uu.ub.cora.data.converter.JsonToDataConverterProvider;
import se.uu.ub.cora.initialize.SettingsProvider;
import se.uu.ub.cora.json.parser.JsonObject;
import se.uu.ub.cora.json.parser.JsonParseException;
import se.uu.ub.cora.json.parser.JsonParser;
import se.uu.ub.cora.json.parser.JsonValue;
import se.uu.ub.cora.json.parser.org.OrgJsonParser;
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
import se.uu.ub.cora.spider.record.RecordListIndexer;
import se.uu.ub.cora.spider.record.RecordNotFoundException;
import se.uu.ub.cora.spider.record.RecordValidator;
import se.uu.ub.cora.spider.record.ResourceNotFoundException;
import se.uu.ub.cora.storage.RecordConflictException;

@Path("/")
public class RecordEndpoint {
	private static final String APPLICATION_VND_UUB_WORKORDER_JSON = "application/vnd.uub.workorder+json";
	private static final String MULTIPART_FORM_DATA = "multipart/form-data";
	private static final String TEXT_PLAIN_CHARSET_UTF_8 = "text/plain; charset=utf-8";
	private static final String APPLICATION_VND_UUB_RECORD_LIST_XML = "application/vnd.uub.recordList+xml";
	private static final String APPLICATION_VND_UUB_RECORD_LIST_XML_QS09 = "application/vnd.uub.recordList+xml;qs=0.9";
	private static final String APPLICATION_VND_UUB_RECORD_LIST_JSON = "application/vnd.uub.recordList+json";
	private static final String APPLICATION_VND_UUB_RECORD_XML = "application/vnd.uub.record+xml";
	private static final String APPLICATION_VND_UUB_RECORD_XML_QS09 = "application/vnd.uub.record+xml;qs=0.9";
	private static final String APPLICATION_VND_UUB_RECORD_JSON = "application/vnd.uub.record+json";
	private static final String URL_DELIMITER = "/";
	private static final int AFTERHTTP = 10;
	HttpServletRequest request;
	private Logger log = LoggerProvider.getLoggerForClass(RecordEndpoint.class);

	private JsonParser jsonParser = new OrgJsonParser();
	private ExternalUrls externalUrls;
	private se.uu.ub.cora.data.converter.ExternalUrls externalUrlsForJson;

	public RecordEndpoint(@Context HttpServletRequest req) {
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

	/** fix: consumes is not a record, but a topDataGroup */
	@POST
	@Path("{type}")
	@Consumes({ APPLICATION_VND_UUB_RECORD_JSON })
	@Produces({ APPLICATION_VND_UUB_RECORD_JSON })
	public Response createRecordJsonJson(@HeaderParam("authToken") String headerAuthToken,
			@QueryParam("authToken") String queryAuthToken, @PathParam("type") String type,
			String inputRecord) {
		return createRecord(APPLICATION_VND_UUB_RECORD_JSON, APPLICATION_VND_UUB_RECORD_JSON,
				headerAuthToken, queryAuthToken, type, inputRecord);
	}

	@POST
	@Path("{type}")
	@Consumes(APPLICATION_VND_UUB_RECORD_JSON)
	@Produces(APPLICATION_VND_UUB_RECORD_XML_QS09)
	public Response createRecordJsonXml(@HeaderParam("authToken") String headerAuthToken,
			@QueryParam("authToken") String queryAuthToken, @PathParam("type") String type,
			String inputRecord) {
		return createRecord(APPLICATION_VND_UUB_RECORD_JSON, APPLICATION_VND_UUB_RECORD_XML,
				headerAuthToken, queryAuthToken, type, inputRecord);
	}

	@POST
	@Path("{type}")
	@Consumes(APPLICATION_VND_UUB_RECORD_XML_QS09)
	@Produces(APPLICATION_VND_UUB_RECORD_JSON)
	public Response createRecordXmlJson(@HeaderParam("authToken") String headerAuthToken,
			@QueryParam("authToken") String queryAuthToken, @PathParam("type") String type,
			String inputRecord) {
		return createRecord(APPLICATION_VND_UUB_RECORD_XML, APPLICATION_VND_UUB_RECORD_JSON,
				headerAuthToken, queryAuthToken, type, inputRecord);
	}

	@POST
	@Path("{type}")
	@Consumes(APPLICATION_VND_UUB_RECORD_XML_QS09)
	@Produces(APPLICATION_VND_UUB_RECORD_XML_QS09)
	public Response createRecordXmlXml(@HeaderParam("authToken") String headerAuthToken,
			@QueryParam("authToken") String queryAuthToken, @PathParam("type") String type,
			String inputRecord) {
		return createRecord(APPLICATION_VND_UUB_RECORD_XML, APPLICATION_VND_UUB_RECORD_XML,
				headerAuthToken, queryAuthToken, type, inputRecord);
	}

	private Response createRecord(String contentType, String accept, String headerAuthToken,
			String queryAuthToken, String type, String inputRecord) {
		String usedToken = getExistingTokenPreferHeader(headerAuthToken, queryAuthToken);
		return createRecordUsingAuthTokenWithRecord(contentType, accept, usedToken, type,
				inputRecord);
	}

	private Response createRecordUsingAuthTokenWithRecord(String contentType, String accept,
			String authToken, String type, String inputRecord) {
		try {
			return tryCreateRecord(contentType, accept, authToken, type, inputRecord);
		} catch (Exception error) {
			String errorFromCaller = "Error creating new record for recordType: {0}.";
			return handleError(authToken, error, MessageFormat.format(errorFromCaller, type));
		}
	}

	private Response tryCreateRecord(String contentType, String accept, String authToken,
			String type, String inputRecord) throws URISyntaxException {
		DataGroup dataRecord = convertStringToDataGroup(contentType, inputRecord);
		return createResponseForCreate(accept, authToken, type, dataRecord);
	}

	private Response createResponseForCreate(String accept, String authToken, String type,
			DataGroup dataRecord) throws URISyntaxException {
		DataRecord createdRecord = SpiderInstanceProvider.getRecordCreator()
				.createAndStoreRecord(authToken, type, dataRecord);

		URI uri = createUri(type, createdRecord);
		String outputRecord = convertConvertibleToString(accept, createdRecord);

		return Response.created(uri).entity(outputRecord).header(HttpHeaders.CONTENT_TYPE, accept)
				.build();
	}

	private URI createUri(String type, DataRecord createdRecord) throws URISyntaxException {
		String createdId = createdRecord.getId();
		return new URI(type + URL_DELIMITER + createdId);
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
					.entity(errorFromCaller + error.getMessage())
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
	@Path("{type}/")
	@Produces({ APPLICATION_VND_UUB_RECORD_LIST_JSON })
	public Response readRecordListJson(@HeaderParam("authToken") String headerAuthToken,
			@QueryParam("authToken") String queryAuthToken, @PathParam("type") String type,
			@QueryParam("filter") String filterAsJson) {
		return readRecordList(APPLICATION_VND_UUB_RECORD_LIST_JSON, headerAuthToken, queryAuthToken,
				type, filterAsJson);
	}

	@GET
	@Path("{type}/")
	@Produces(APPLICATION_VND_UUB_RECORD_LIST_XML_QS09)
	public Response readRecordListXml(@HeaderParam("authToken") String headerAuthToken,
			@QueryParam("authToken") String queryAuthToken, @PathParam("type") String type,
			@QueryParam("filter") String filterAsJson) {
		return readRecordList(APPLICATION_VND_UUB_RECORD_LIST_XML, headerAuthToken, queryAuthToken,
				type, filterAsJson);
	}

	private Response readRecordList(String accept, String headerAuthToken, String queryAuthToken,
			String type, String filterAsJson) {
		String usedToken = getExistingTokenPreferHeader(headerAuthToken, queryAuthToken);
		String filter = createEmptyFilterIfParameterDoesNotExist(filterAsJson);
		return readRecordListUsingAuthTokenByType(accept, usedToken, type, filter);
	}

	private String createEmptyFilterIfParameterDoesNotExist(String filterAsJson) {
		String filter = filterAsJson;
		if (filterAsJson == null || filterAsJson.isEmpty()) {
			filter = "{\"name\":\"filter\",\"children\":[]}";
		}
		return filter;
	}

	private Response readRecordListUsingAuthTokenByType(String accept, String authToken,
			String type, String filterAsJson) {
		try {
			return tryReadRecordList(accept, authToken, type, filterAsJson);
		} catch (Exception error) {
			String errorFromCaller = "Error reading records with recordType: {0}.";
			return handleError(authToken, error, MessageFormat.format(errorFromCaller, type));
		}
	}

	private Response tryReadRecordList(String accept, String authToken, String type,
			String filterAsString) {
		DataGroup filter = convertFilterStringToData(filterAsString);
		DataList readRecordList = SpiderInstanceProvider.getRecordListReader()
				.readRecordList(authToken, type, filter);
		String outputRecord = convertConvertibleToString(accept, readRecordList);
		return Response.status(Response.Status.OK).header(HttpHeaders.CONTENT_TYPE, accept)
				.entity(outputRecord).build();
	}

	private DataGroup convertFilterStringToData(String filterAsString) {
		String filterDataType = calculateSearchDataType(filterAsString);
		return convertStringToDataGroup(filterDataType, filterAsString);
	}

	@GET
	@Path("{type}/{id}")
	@Produces({ APPLICATION_VND_UUB_RECORD_JSON })
	public Response readRecordJson(@HeaderParam("authToken") String headerAuthToken,
			@QueryParam("authToken") String queryAuthToken, @PathParam("type") String type,
			@PathParam("id") String id) {
		return readRecord(APPLICATION_VND_UUB_RECORD_JSON, headerAuthToken, queryAuthToken, type,
				id);
	}

	@GET
	@Path("{type}/{id}")
	@Produces(APPLICATION_VND_UUB_RECORD_XML_QS09)
	public Response readRecordXml(@HeaderParam("authToken") String headerAuthToken,
			@QueryParam("authToken") String queryAuthToken, @PathParam("type") String type,
			@PathParam("id") String id) {
		return readRecord(APPLICATION_VND_UUB_RECORD_XML, headerAuthToken, queryAuthToken, type,
				id);
	}

	private Response readRecord(String accept, String headerAuthToken, String queryAuthToken,
			String type, String id) {
		String usedToken = getExistingTokenPreferHeader(headerAuthToken, queryAuthToken);
		return readRecordUsingAuthTokenByTypeAndId(accept, usedToken, type, id);
	}

	private Response readRecordUsingAuthTokenByTypeAndId(String accept, String authToken,
			String type, String id) {
		try {
			return tryReadRecord(accept, authToken, type, id);
		} catch (Exception error) {
			String errorFromCaller = "Error reading record with recordType: {0} and "
					+ "recordId: {1}.";
			return handleError(authToken, error, MessageFormat.format(errorFromCaller, type, id));
		}
	}

	private Response tryReadRecord(String accept, String authToken, String type, String id) {

		DataRecord dataRecord = SpiderInstanceProvider.getRecordReader().readRecord(authToken, type,
				id);

		String convertedDataRecord = convertConvertibleToString(accept, dataRecord);

		return Response.status(Response.Status.OK).header(HttpHeaders.CONTENT_TYPE, accept)
				.entity(convertedDataRecord).build();
	}

	private String convertConvertibleToString(String accept, ExternallyConvertible convertible) {
		if (accept.endsWith("+xml")) {
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

	/**
	 * fix: produces is not a list of records, but a list of recordLinks (the list of links could be
	 * made more efficient by not including the same from part in every link)
	 */
	@GET
	@Path("{type}/{id}/incomingLinks")
	@Produces({ APPLICATION_VND_UUB_RECORD_LIST_JSON })
	public Response readIncomingRecordLinksJson(@HeaderParam("authToken") String headerAuthToken,
			@QueryParam("authToken") String queryAuthToken, @PathParam("type") String type,
			@PathParam("id") String id) {
		return readIncomingRecordLink(APPLICATION_VND_UUB_RECORD_LIST_JSON, headerAuthToken,
				queryAuthToken, type, id);
	}

	@GET
	@Path("{type}/{id}/incomingLinks")
	@Produces(APPLICATION_VND_UUB_RECORD_LIST_XML_QS09)
	public Response readIncomingRecordLinksXml(@HeaderParam("authToken") String headerAuthToken,
			@QueryParam("authToken") String queryAuthToken, @PathParam("type") String type,
			@PathParam("id") String id) {
		return readIncomingRecordLink(APPLICATION_VND_UUB_RECORD_LIST_XML, headerAuthToken,
				queryAuthToken, type, id);
	}

	private Response readIncomingRecordLink(String accept, String headerAuthToken,
			String queryAuthToken, String type, String id) {
		String usedToken = getExistingTokenPreferHeader(headerAuthToken, queryAuthToken);
		return readIncomingRecordLinksUsingAuthTokenByTypeAndId(accept, usedToken, type, id);
	}

	private Response readIncomingRecordLinksUsingAuthTokenByTypeAndId(String accept,
			String authToken, String type, String id) {
		try {
			return tryReadIncomingRecordLinks(accept, authToken, type, id);
		} catch (Exception error) {
			return handleError(authToken, error, "Some error");
		}
	}

	private Response tryReadIncomingRecordLinks(String accept, String authToken, String type,
			String id) {
		DataList dataList = SpiderInstanceProvider.getIncomingLinksReader()
				.readIncomingLinks(authToken, type, id);
		String convertedDataList = convertConvertibleToString(accept, dataList);
		return Response.status(Response.Status.OK).header(HttpHeaders.CONTENT_TYPE, accept)
				.entity(convertedDataList).build();
	}

	@DELETE
	@Path("{type}/{id}")
	public Response deleteRecord(@HeaderParam("authToken") String headerAuthToken,
			@QueryParam("authToken") String queryAuthToken, @PathParam("type") String type,
			@PathParam("id") String id) {
		String usedToken = getExistingTokenPreferHeader(headerAuthToken, queryAuthToken);
		return deleteRecordUsingAuthTokenByTypeAndId(usedToken, type, id);
	}

	private Response deleteRecordUsingAuthTokenByTypeAndId(String authToken, String type,
			String id) {
		try {
			return tryDeleteRecord(authToken, type, id);
		} catch (Exception error) {
			return handleError(authToken, error, "Some error");
		}
	}

	private Response tryDeleteRecord(String authToken, String type, String id) {
		SpiderInstanceProvider.getRecordDeleter().deleteRecord(authToken, type, id);
		return Response.status(Response.Status.OK).build();
	}

	/** fix: consumes is not a record, but a topDataGroup */
	@POST
	@Path("{type}/{id}")
	@Consumes({ APPLICATION_VND_UUB_RECORD_JSON })
	@Produces({ APPLICATION_VND_UUB_RECORD_JSON })
	public Response updateRecordJsonJson(@HeaderParam("authToken") String headerAuthToken,
			@QueryParam("authToken") String queryAuthToken, @PathParam("type") String type,
			@PathParam("id") String id, String inputRecord) {
		return updateRecord(APPLICATION_VND_UUB_RECORD_JSON, APPLICATION_VND_UUB_RECORD_JSON,
				headerAuthToken, queryAuthToken, type, id, inputRecord);
	}

	@POST
	@Path("{type}/{id}")
	@Consumes(APPLICATION_VND_UUB_RECORD_JSON)
	@Produces(APPLICATION_VND_UUB_RECORD_XML_QS09)
	public Response updateRecordJsonXml(@HeaderParam("authToken") String headerAuthToken,
			@QueryParam("authToken") String queryAuthToken, @PathParam("type") String type,
			@PathParam("id") String id, String inputRecord) {
		return updateRecord(APPLICATION_VND_UUB_RECORD_JSON, APPLICATION_VND_UUB_RECORD_XML,
				headerAuthToken, queryAuthToken, type, id, inputRecord);
	}

	@POST
	@Path("{type}/{id}")
	@Consumes(APPLICATION_VND_UUB_RECORD_XML_QS09)
	@Produces(APPLICATION_VND_UUB_RECORD_JSON)
	public Response updateRecordXmlJson(@HeaderParam("authToken") String headerAuthToken,
			@QueryParam("authToken") String queryAuthToken, @PathParam("type") String type,
			@PathParam("id") String id, String inputRecord) {
		return updateRecord(APPLICATION_VND_UUB_RECORD_XML, APPLICATION_VND_UUB_RECORD_JSON,
				headerAuthToken, queryAuthToken, type, id, inputRecord);
	}

	@POST
	@Path("{type}/{id}")
	@Consumes(APPLICATION_VND_UUB_RECORD_XML_QS09)
	@Produces(APPLICATION_VND_UUB_RECORD_XML_QS09)
	public Response updateRecordXmlXml(@HeaderParam("authToken") String headerAuthToken,
			@QueryParam("authToken") String queryAuthToken, @PathParam("type") String type,
			@PathParam("id") String id, String inputRecord) {
		return updateRecord(APPLICATION_VND_UUB_RECORD_XML, APPLICATION_VND_UUB_RECORD_XML,
				headerAuthToken, queryAuthToken, type, id, inputRecord);
	}

	private Response updateRecord(String contentType, String accept, String headerAuthToken,
			String queryAuthToken, String type, String id, String inputRecord) {
		String usedToken = getExistingTokenPreferHeader(headerAuthToken, queryAuthToken);
		return updateRecordUsingAuthTokenWithRecord(contentType, accept, usedToken, type, id,
				inputRecord);
	}

	private Response updateRecordUsingAuthTokenWithRecord(String contentType, String accept,
			String authToken, String type, String id, String inputRecord) {
		try {
			return tryUpdateRecord(contentType, accept, authToken, type, id, inputRecord);
		} catch (Exception error) {
			String errorFromCaller = "Error updating record with recordType: {0} and "
					+ "recordId: {1}.";
			return handleError(authToken, error, MessageFormat.format(errorFromCaller, type, id));

		}
	}

	private Response tryUpdateRecord(String contentType, String accept, String authToken,
			String type, String id, String inputRecord) {

		DataGroup dataRecord = convertStringToDataGroup(contentType, inputRecord);
		DataRecord updatedRecord = SpiderInstanceProvider.getRecordUpdater().updateRecord(authToken,
				type, id, dataRecord);
		String outputRecord = convertConvertibleToString(accept, updatedRecord);

		return Response.status(Response.Status.OK).header(HttpHeaders.CONTENT_TYPE, accept)
				.entity(outputRecord).build();
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
			return handleError(authToken, error, "");
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

	@POST
	@Path("{type}/{id}/{resourceType}")
	@Consumes(MULTIPART_FORM_DATA)
	@Produces({ APPLICATION_VND_UUB_RECORD_JSON })
	public Response uploadResourceJson(@HeaderParam("authToken") String headerAuthToken,
			@QueryParam("authToken") String queryAuthToken, @PathParam("type") String type,
			@PathParam("id") String id, @FormDataParam("file") InputStream uploadedInputStream,
			@PathParam("resourceType") String resourceType) {
		return uploadResource(APPLICATION_VND_UUB_RECORD_JSON, headerAuthToken, queryAuthToken,
				type, id, uploadedInputStream, resourceType);
	}

	@POST
	@Path("{type}/{id}/{resourceType}")
	@Consumes(MULTIPART_FORM_DATA)
	@Produces(APPLICATION_VND_UUB_RECORD_XML_QS09)
	public Response uploadResourceXml(@HeaderParam("authToken") String headerAuthToken,
			@QueryParam("authToken") String queryAuthToken, @PathParam("type") String type,
			@PathParam("id") String id, @FormDataParam("file") InputStream uploadedInputStream,
			@PathParam("resourceType") String resourceType) {
		return uploadResource(APPLICATION_VND_UUB_RECORD_XML, headerAuthToken, queryAuthToken, type,
				id, uploadedInputStream, resourceType);
	}

	private Response uploadResource(String accept, String headerAuthToken, String queryAuthToken,
			String type, String id, InputStream uploadedInputStream, String resourceType) {
		String usedToken = getExistingTokenPreferHeader(headerAuthToken, queryAuthToken);
		return uploadResourceUsingAuthTokenWithStream(accept, usedToken, type, id,
				uploadedInputStream, resourceType);
	}

	Response uploadResourceUsingAuthTokenWithStream(String accept, String authToken, String type,
			String id, InputStream uploadedInputStream, String resourceType) {
		try {
			return tryUploadResource(accept, authToken, type, id, uploadedInputStream,
					resourceType);
		} catch (Exception error) {
			return handleError(authToken, error,
					"An error has ocurred while uploading a resource :" + error.getMessage());
		}
	}

	private Response tryUploadResource(String accept, String authToken, String type, String id,
			InputStream inputStream, String resourceType) {
		DataRecord updatedRecord = SpiderInstanceProvider.getUploader().upload(authToken, type, id,
				inputStream, resourceType);

		String convertedDataList = convertConvertibleToString(accept, updatedRecord);

		return Response.ok(convertedDataList).header(HttpHeaders.CONTENT_TYPE, accept).build();
	}

	@GET
	@Path("searchResult/{searchId}")
	@Produces({ APPLICATION_VND_UUB_RECORD_LIST_JSON })
	public Response searchRecordJson(@HeaderParam("authToken") String headerAuthToken,
			@QueryParam("authToken") String queryAuthToken, @PathParam("searchId") String searchId,
			@QueryParam("searchData") String searchDataAsString) {
		return searchRecord(APPLICATION_VND_UUB_RECORD_LIST_JSON, headerAuthToken, queryAuthToken,
				searchId, searchDataAsString);
	}

	@GET
	@Path("searchResult/{searchId}")
	@Produces(APPLICATION_VND_UUB_RECORD_LIST_XML_QS09)
	public Response searchRecordXml(@HeaderParam("authToken") String headerAuthToken,
			@QueryParam("authToken") String queryAuthToken, @PathParam("searchId") String searchId,
			@QueryParam("searchData") String searchDataAsString) {
		return searchRecord(APPLICATION_VND_UUB_RECORD_LIST_XML, headerAuthToken, queryAuthToken,
				searchId, searchDataAsString);
	}

	private Response searchRecord(String accept, String headerAuthToken, String queryAuthToken,
			String searchId, String searchDataAsString) {
		String usedToken = getExistingTokenPreferHeader(headerAuthToken, queryAuthToken);
		return searchRecordUsingAuthTokenBySearchData(accept, usedToken, searchId,
				searchDataAsString);
	}

	private Response searchRecordUsingAuthTokenBySearchData(String accept, String authToken,
			String searchId, String searchDataAsString) {
		try {
			return trySearchRecord(accept, authToken, searchId, searchDataAsString);
		} catch (Exception error) {
			String errorFromCaller = "Error searching record with searchId: {0}.";
			return handleError(authToken, error, MessageFormat.format(errorFromCaller, searchId));
		}
	}

	private Response trySearchRecord(String accept, String authToken, String searchId,
			String searchDataAsString) {
		DataGroup searchData = convertSearchStringToData(searchDataAsString);

		DataList searchRecordList = SpiderInstanceProvider.getRecordSearcher().search(authToken,
				searchId, searchData);

		String outputRecord = convertConvertibleToString(accept, searchRecordList);

		return Response.status(Response.Status.OK).header(HttpHeaders.CONTENT_TYPE, accept)
				.entity(outputRecord).build();
	}

	private DataGroup convertSearchStringToData(String searchDataAsString) {
		String searchDataType = calculateSearchDataType(searchDataAsString);
		return convertStringToDataGroup(searchDataType, searchDataAsString);
	}

	private String calculateSearchDataType(String searchDataAsString) {
		if (searchDataAsString.startsWith("<")) {
			return "+xml";
		}
		return "+json";
	}

	/**
	 * fix: workOrder as consumes here vs. workOrder recordType created as new records used for
	 * index, is easy to mix up.
	 * <p>
	 * json uses a non Cora json format with only order and record as top level children.
	 * <p>
	 * xml uses a Cora DataGroup with order and record as top level children
	 */
	@POST
	@Path("{type}")
	@Consumes({ APPLICATION_VND_UUB_WORKORDER_JSON })
	@Produces({ APPLICATION_VND_UUB_RECORD_JSON })
	public Response validateRecordJsonJson(@HeaderParam("authToken") String headerAuthToken,
			@QueryParam("authToken") String queryAuthToken, @PathParam("type") String type,
			String jsonValidationRecord) {
		return validateRecord(APPLICATION_VND_UUB_WORKORDER_JSON, APPLICATION_VND_UUB_RECORD_JSON,
				headerAuthToken, queryAuthToken, jsonValidationRecord);
	}

	@POST
	@Path("{type}")
	@Consumes(APPLICATION_VND_UUB_WORKORDER_JSON)
	@Produces(APPLICATION_VND_UUB_RECORD_XML_QS09)
	public Response validateRecordJsonXml(@HeaderParam("authToken") String headerAuthToken,
			@QueryParam("authToken") String queryAuthToken, @PathParam("type") String type,
			String jsonValidationRecord) {
		return validateRecord(APPLICATION_VND_UUB_WORKORDER_JSON, APPLICATION_VND_UUB_RECORD_XML,
				headerAuthToken, queryAuthToken, jsonValidationRecord);
	}

	@POST
	@Path("{type}")
	@Consumes("application/vnd.uub.workorder+xml" + ";qs=0.9")
	@Produces(APPLICATION_VND_UUB_RECORD_JSON)
	public Response validateRecordXmlJson(@HeaderParam("authToken") String headerAuthToken,
			@QueryParam("authToken") String queryAuthToken, @PathParam("type") String type,
			String jsonValidationRecord) {
		return validateRecord("application/vnd.uub.workorder+xml", APPLICATION_VND_UUB_RECORD_JSON,
				headerAuthToken, queryAuthToken, jsonValidationRecord);
	}

	@POST
	@Path("{type}")
	@Consumes("application/vnd.uub.workorder+xml" + ";qs=0.9")
	@Produces(APPLICATION_VND_UUB_RECORD_XML_QS09)
	public Response validateRecordXmlXml(@HeaderParam("authToken") String headerAuthToken,
			@QueryParam("authToken") String queryAuthToken, @PathParam("type") String type,
			String jsonValidationRecord) {
		return validateRecord("application/vnd.uub.workorder+xml", APPLICATION_VND_UUB_RECORD_XML,
				headerAuthToken, queryAuthToken, jsonValidationRecord);
	}

	private Response validateRecord(String contentType, String accept, String headerAuthToken,
			String queryAuthToken, String jsonValidationRecord) {
		String usedToken = getExistingTokenPreferHeader(headerAuthToken, queryAuthToken);
		String recordTypeToUse = "validationOrder";
		return validateRecordUsingAuthTokenWithRecord(contentType, accept, usedToken,
				recordTypeToUse, jsonValidationRecord);
	}

	private Response validateRecordUsingAuthTokenWithRecord(String contentType, String accept,
			String authToken, String type, String jsonRecord) {
		try {
			return tryValidateRecord(contentType, accept, authToken, type, jsonRecord);
		} catch (Exception error) {
			return handleError(authToken, error, "Some error");
		}
	}

	private Response tryValidateRecord(String contentType, String accept, String authToken,
			String type, String inputRecord) {
		DataGroup validationOrder = null;
		DataGroup recordToValidate = null;
		if (contentType.endsWith("+xml")) {
			DataGroup container = convertXmlToDataElement(inputRecord);
			validationOrder = container.getFirstGroupWithNameInData("order");
			recordToValidate = container.getFirstGroupWithNameInData("record");
		} else {
			JsonObject jsonObject = getJsonObjectFromJsonRecordString(inputRecord);
			validationOrder = getDataGroupFromJsonObjectUsingName(jsonObject, "order");
			recordToValidate = getDataGroupFromJsonObjectUsingName(jsonObject, "record");
		}

		RecordValidator recordValidator = SpiderInstanceProvider.getRecordValidator();
		DataRecord validationResult = recordValidator.validateRecord(authToken, type,
				validationOrder, recordToValidate);

		String outputRecord = convertConvertibleToString(accept, validationResult);
		return Response.status(Response.Status.OK).header(HttpHeaders.CONTENT_TYPE, accept)
				.entity(outputRecord).build();
	}

	private JsonObject getJsonObjectFromJsonRecordString(String jsonRecord) {
		JsonValue jsonValue = jsonParser.parseString(jsonRecord);
		return (JsonObject) jsonValue;
	}

	private DataGroup getDataGroupFromJsonObjectUsingName(JsonObject jsonObject, String name) {
		JsonValue jsonObjectForName = jsonObject.getValue(name);
		JsonToDataConverter jsonToDataConverter = JsonToDataConverterProvider
				.getConverterUsingJsonObject(jsonObjectForName);
		return (DataGroup) jsonToDataConverter.toInstance();
	}

	@POST
	@Path("index/{type}")
	@Consumes({ APPLICATION_VND_UUB_RECORD_JSON })
	@Produces({ APPLICATION_VND_UUB_RECORD_JSON })
	public Response batchIndexJsonJson(@HeaderParam("authToken") String headerAuthToken,
			@QueryParam("authToken") String queryAuthToken, @PathParam("type") String type,
			String indexSettingsAsJson) {
		return batchIndex(APPLICATION_VND_UUB_RECORD_JSON, APPLICATION_VND_UUB_RECORD_JSON,
				headerAuthToken, queryAuthToken, type, indexSettingsAsJson);
	}

	@POST
	@Path("index/{type}")
	@Consumes(APPLICATION_VND_UUB_RECORD_JSON)
	@Produces(APPLICATION_VND_UUB_RECORD_XML_QS09)
	public Response batchIndexJsonXml(@HeaderParam("authToken") String headerAuthToken,
			@QueryParam("authToken") String queryAuthToken, @PathParam("type") String type,
			String indexSettingsAsJson) {
		return batchIndex(APPLICATION_VND_UUB_RECORD_JSON, APPLICATION_VND_UUB_RECORD_XML,
				headerAuthToken, queryAuthToken, type, indexSettingsAsJson);
	}

	@POST
	@Path("index/{type}")
	@Consumes(APPLICATION_VND_UUB_RECORD_XML_QS09)
	@Produces(APPLICATION_VND_UUB_RECORD_JSON)
	public Response batchIndexXmlJson(@HeaderParam("authToken") String headerAuthToken,
			@QueryParam("authToken") String queryAuthToken, @PathParam("type") String type,
			String indexSettingsAsJson) {
		return batchIndex(APPLICATION_VND_UUB_RECORD_XML, APPLICATION_VND_UUB_RECORD_JSON,
				headerAuthToken, queryAuthToken, type, indexSettingsAsJson);
	}

	@POST
	@Path("index/{type}")
	@Consumes(APPLICATION_VND_UUB_RECORD_XML_QS09)
	@Produces(APPLICATION_VND_UUB_RECORD_XML_QS09)
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
