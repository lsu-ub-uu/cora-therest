/*
 * Copyright 2015, 2016, 2018, 2021 Uppsala University Library
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

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
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
import se.uu.ub.cora.converter.ExternallyConvertibleToStringConverter;
import se.uu.ub.cora.converter.StringToExternallyConvertibleConverter;
import se.uu.ub.cora.data.Convertible;
import se.uu.ub.cora.data.DataElement;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataList;
import se.uu.ub.cora.data.DataPart;
import se.uu.ub.cora.data.DataRecord;
import se.uu.ub.cora.data.ExternallyConvertible;
import se.uu.ub.cora.data.converter.ConversionException;
import se.uu.ub.cora.data.converter.DataToJsonConverter;
import se.uu.ub.cora.data.converter.DataToJsonConverterFactory;
import se.uu.ub.cora.data.converter.DataToJsonConverterProvider;
import se.uu.ub.cora.data.converter.JsonToDataConverter;
import se.uu.ub.cora.data.converter.JsonToDataConverterProvider;
import se.uu.ub.cora.json.parser.JsonObject;
import se.uu.ub.cora.json.parser.JsonParseException;
import se.uu.ub.cora.json.parser.JsonParser;
import se.uu.ub.cora.json.parser.JsonValue;
import se.uu.ub.cora.json.parser.org.OrgJsonParser;
import se.uu.ub.cora.logger.Logger;
import se.uu.ub.cora.logger.LoggerProvider;
import se.uu.ub.cora.spider.authentication.AuthenticationException;
import se.uu.ub.cora.spider.authorization.AuthorizationException;
import se.uu.ub.cora.spider.data.DataMissingException;
import se.uu.ub.cora.spider.data.SpiderInputStream;
import se.uu.ub.cora.spider.dependency.SpiderInstanceProvider;
import se.uu.ub.cora.spider.record.DataException;
import se.uu.ub.cora.spider.record.MisuseException;
import se.uu.ub.cora.spider.record.RecordListIndexer;
import se.uu.ub.cora.spider.record.RecordValidator;
import se.uu.ub.cora.storage.RecordConflictException;
import se.uu.ub.cora.storage.RecordNotFoundException;

@Path("/")
public class RecordEndpoint {
	private static final String URL_DELIMITER = "/";
	private static final int AFTERHTTP = 10;
	private String baseUrl;
	HttpServletRequest request;
	private Logger log = LoggerProvider.getLoggerForClass(RecordEndpoint.class);

	private JsonParser jsonParser = new OrgJsonParser();

	public RecordEndpoint(@Context HttpServletRequest req) {
		request = req;
		baseUrl = getBaseURLFromURI();
	}

	private final String getBaseURLFromURI() {
		String baseURL = getBaseURLFromRequest();
		return changeHttpToHttpsIfHeaderSaysSo(baseURL);
	}

	private final String getBaseURLFromRequest() {
		String tempUrl = request.getRequestURL().toString();
		int indexOfFirstSlashAfterHttp = tempUrl.indexOf('/', AFTERHTTP);
		String baseURL = tempUrl.substring(0, indexOfFirstSlashAfterHttp);
		baseURL += SpiderInstanceProvider.getInitInfo().get("theRestPublicPathToSystem");
		baseURL += "record/";
		return baseURL;
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
	@Consumes({ "application/vnd.uub.record+json", "*/*" })
	@Produces({ "application/vnd.uub.record+json", "*/*" })
	public Response createRecordJsonJson(@HeaderParam("authToken") String headerAuthToken,
			@QueryParam("authToken") String queryAuthToken, @PathParam("type") String type,
			String inputRecord) {
		return createRecord("application/vnd.uub.record+json", "application/vnd.uub.record+json",
				headerAuthToken, queryAuthToken, type, inputRecord);
	}

	@POST
	@Path("{type}")
	@Consumes("application/vnd.uub.record+json")
	@Produces("application/vnd.uub.record+xml")
	public Response createRecordJsonXml(@HeaderParam("authToken") String headerAuthToken,
			@QueryParam("authToken") String queryAuthToken, @PathParam("type") String type,
			String inputRecord) {
		return createRecord("application/vnd.uub.record+json", "application/vnd.uub.record+xml",
				headerAuthToken, queryAuthToken, type, inputRecord);
	}

	@POST
	@Path("{type}")
	@Consumes("application/vnd.uub.record+xml")
	@Produces("application/vnd.uub.record+json")
	public Response createRecordXmlJson(@HeaderParam("authToken") String headerAuthToken,
			@QueryParam("authToken") String queryAuthToken, @PathParam("type") String type,
			String inputRecord) {
		return createRecord("application/vnd.uub.record+xml", "application/vnd.uub.record+json",
				headerAuthToken, queryAuthToken, type, inputRecord);
	}

	@POST
	@Path("{type}")
	@Consumes("application/vnd.uub.record+xml")
	@Produces("application/vnd.uub.record+xml")
	public Response createRecordXmlXml(@HeaderParam("authToken") String headerAuthToken,
			@QueryParam("authToken") String queryAuthToken, @PathParam("type") String type,
			String inputRecord) {
		return createRecord("application/vnd.uub.record+xml", "application/vnd.uub.record+xml",
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
			return handleError(authToken, error);
		}
	}

	private Response tryCreateRecord(String contentType, String accept, String authToken,
			String type, String inputRecord) throws URISyntaxException {
		DataGroup dataRecord = (DataGroup) convertStringToData(contentType, inputRecord);
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

	private DataElement convertStringToData(String accept, String input) {
		if (accept.endsWith("+xml")) {
			return convertXmlToDataElement(input);
		} else {
			return convertJsonStringToDataGroup(input);
		}
	}

	private DataElement convertXmlToDataElement(String input) {
		StringToExternallyConvertibleConverter xmlToConvertibleConverter = ConverterProvider
				.getStringToExternallyConvertibleConverter("xml");
		return xmlToConvertibleConverter.convert(input);
	}

	private DataGroup convertJsonStringToDataGroup(String jsonRecord) {
		JsonValue jsonValue = jsonParser.parseString(jsonRecord);
		JsonToDataConverter jsonToDataConverter = JsonToDataConverterProvider
				.getConverterUsingJsonObject(jsonValue);
		DataPart dataPart = jsonToDataConverter.toInstance();
		return (DataGroup) dataPart;
	}

	private String convertDataToJson(ExternallyConvertible convertible) {
		DataToJsonConverterFactory dataToJsonConverterFactory = DataToJsonConverterProvider
				.createImplementingFactory();
		DataToJsonConverter converter = dataToJsonConverterFactory
				.factorUsingBaseUrlAndConvertible(baseUrl, (Convertible) convertible);
		return converter.toJsonCompactFormat();
	}

	private Response handleError(String authToken, Exception error) {
		if (error instanceof RecordConflictException) {
			return buildResponseIncludingMessage(error, Response.Status.CONFLICT);
		}

		if (error instanceof MisuseException) {
			return buildResponseIncludingMessage(error, Response.Status.METHOD_NOT_ALLOWED);
		}

		if (errorIsCausedByDataProblem(error)) {
			return buildResponseIncludingMessage(error, Response.Status.BAD_REQUEST);
		}

		if (error instanceof RecordNotFoundException) {
			return buildResponseIncludingMessage(error, Response.Status.NOT_FOUND);
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
				|| error instanceof DataException || error instanceof DataMissingException;
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
		return Response.status(status).entity(error.getMessage()).build();
	}

	private Response buildResponse(Status status) {
		return Response.status(status).build();
	}

	@GET
	@Path("{type}/")
	@Produces({ "application/vnd.uub.recordList+json", "*/*" })
	public Response readRecordListJson(@HeaderParam("authToken") String headerAuthToken,
			@QueryParam("authToken") String queryAuthToken, @PathParam("type") String type,
			@QueryParam("filter") String filterAsJson) {
		return readRecordList("application/vnd.uub.recordList+json", headerAuthToken,
				queryAuthToken, type, filterAsJson);
	}

	@GET
	@Path("{type}/")
	@Produces("application/vnd.uub.recordList+xml")
	public Response readRecordListXml(@HeaderParam("authToken") String headerAuthToken,
			@QueryParam("authToken") String queryAuthToken, @PathParam("type") String type,
			@QueryParam("filter") String filterAsJson) {
		return readRecordList("application/vnd.uub.recordList+xml", headerAuthToken, queryAuthToken,
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
			return handleError(authToken, error);
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
		return (DataGroup) convertStringToData(filterDataType, filterAsString);
	}

	@GET
	@Path("{type}/{id}")
	@Produces({ "application/vnd.uub.record+json", "*/*" })
	public Response readRecordJson(@HeaderParam("authToken") String headerAuthToken,
			@QueryParam("authToken") String queryAuthToken, @PathParam("type") String type,
			@PathParam("id") String id) {
		return readRecord("application/vnd.uub.record+json", headerAuthToken, queryAuthToken, type,
				id);
	}

	@GET
	@Path("{type}/{id}")
	@Produces("application/vnd.uub.record+xml")
	public Response readRecordXml(@HeaderParam("authToken") String headerAuthToken,
			@QueryParam("authToken") String queryAuthToken, @PathParam("type") String type,
			@PathParam("id") String id) {
		return readRecord("application/vnd.uub.record+xml", headerAuthToken, queryAuthToken, type,
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
			return handleError(authToken, error);
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
		String stringToReturn;
		ExternallyConvertibleToStringConverter convertibleToXmlConverter = ConverterProvider
				.getExternallyConvertibleToStringConverter("xml");

		stringToReturn = convertibleToXmlConverter.convertWithLinks(convertible, baseUrl);
		return stringToReturn;
	}

	/**
	 * fix: produces is not a list of records, but a list of recordLinks (the list of links could be
	 * made more efficient by not including the same from part in every link)
	 */
	@GET
	@Path("{type}/{id}/incomingLinks")
	@Produces({ "application/vnd.uub.recordList+json", "*/*" })
	public Response readIncomingRecordLinksJson(@HeaderParam("authToken") String headerAuthToken,
			@QueryParam("authToken") String queryAuthToken, @PathParam("type") String type,
			@PathParam("id") String id) {
		return readIncomingRecordLink("application/vnd.uub.recordList+json", headerAuthToken,
				queryAuthToken, type, id);
	}

	@GET
	@Path("{type}/{id}/incomingLinks")
	@Produces("application/vnd.uub.recordList+xml")
	public Response readIncomingRecordLinksXml(@HeaderParam("authToken") String headerAuthToken,
			@QueryParam("authToken") String queryAuthToken, @PathParam("type") String type,
			@PathParam("id") String id) {
		return readIncomingRecordLink("application/vnd.uub.recordList+xml", headerAuthToken,
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
			return handleError(authToken, error);
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
			return handleError(authToken, error);
		}
	}

	private Response tryDeleteRecord(String authToken, String type, String id) {
		SpiderInstanceProvider.getRecordDeleter().deleteRecord(authToken, type, id);
		return Response.status(Response.Status.OK).build();
	}

	/** fix: consumes is not a record, but a topDataGroup */
	@POST
	@Path("{type}/{id}")
	@Consumes({ "application/vnd.uub.record+json", "*/*" })
	@Produces({ "application/vnd.uub.record+json", "*/*" })
	public Response updateRecordJsonJson(@HeaderParam("authToken") String headerAuthToken,
			@QueryParam("authToken") String queryAuthToken, @PathParam("type") String type,
			@PathParam("id") String id, String inputRecord) {
		return updateRecord("application/vnd.uub.record+json", "application/vnd.uub.record+json",
				headerAuthToken, queryAuthToken, type, id, inputRecord);
	}

	@POST
	@Path("{type}/{id}")
	@Consumes("application/vnd.uub.record+json")
	@Produces("application/vnd.uub.record+xml")
	public Response updateRecordJsonXml(@HeaderParam("authToken") String headerAuthToken,
			@QueryParam("authToken") String queryAuthToken, @PathParam("type") String type,
			@PathParam("id") String id, String inputRecord) {
		return updateRecord("application/vnd.uub.record+json", "application/vnd.uub.record+xml",
				headerAuthToken, queryAuthToken, type, id, inputRecord);
	}

	@POST
	@Path("{type}/{id}")
	@Consumes("application/vnd.uub.record+xml")
	@Produces("application/vnd.uub.record+json")
	public Response updateRecordXmlJson(@HeaderParam("authToken") String headerAuthToken,
			@QueryParam("authToken") String queryAuthToken, @PathParam("type") String type,
			@PathParam("id") String id, String inputRecord) {
		return updateRecord("application/vnd.uub.record+xml", "application/vnd.uub.record+json",
				headerAuthToken, queryAuthToken, type, id, inputRecord);
	}

	@POST
	@Path("{type}/{id}")
	@Consumes("application/vnd.uub.record+xml")
	@Produces("application/vnd.uub.record+xml")
	public Response updateRecordXmlXml(@HeaderParam("authToken") String headerAuthToken,
			@QueryParam("authToken") String queryAuthToken, @PathParam("type") String type,
			@PathParam("id") String id, String inputRecord) {
		return updateRecord("application/vnd.uub.record+xml", "application/vnd.uub.record+xml",
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
			return handleError(authToken, error);
		}
	}

	private Response tryUpdateRecord(String contentType, String accept, String authToken,
			String type, String id, String inputRecord) {

		DataGroup dataRecord = (DataGroup) convertStringToData(contentType, inputRecord);
		DataRecord updatedRecord = SpiderInstanceProvider.getRecordUpdater().updateRecord(authToken,
				type, id, dataRecord);
		String outputRecord = convertConvertibleToString(accept, updatedRecord);

		return Response.status(Response.Status.OK).header(HttpHeaders.CONTENT_TYPE, accept)
				.entity(outputRecord).build();
	}

	@GET
	@Path("{type}/{id}/{streamId}")
	public Response downloadFile(@HeaderParam("authToken") String headerAuthToken,
			@QueryParam("authToken") String queryAuthToken, @PathParam("type") String type,
			@PathParam("id") String id, @PathParam("streamId") String streamId) {
		String usedToken = getExistingTokenPreferHeader(headerAuthToken, queryAuthToken);
		return downloadFileUsingAuthTokenWithStream(usedToken, type, id, streamId);
	}

	private String getExistingTokenPreferHeader(String headerAuthToken, String queryAuthToken) {
		return headerAuthToken != null ? headerAuthToken : queryAuthToken;
	}

	Response downloadFileUsingAuthTokenWithStream(String authToken, String type, String id,
			String streamId) {
		try {
			return tryDownloadFile(authToken, type, id, streamId);
		} catch (Exception error) {
			return handleError(authToken, error);
		}
	}

	private Response tryDownloadFile(String authToken, String type, String id, String streamId) {
		SpiderInputStream streamOut = SpiderInstanceProvider.getDownloader().download(authToken,
				type, id, streamId);
		/*
		 * when we detect and store type of file in spider set it like this return
		 * Response.ok(streamOut.stream).type("application/octet-stream")
		 */
		return Response.ok(streamOut.stream).type(streamOut.mimeType)
				.header("Content-Disposition", "attachment; filename=" + streamOut.name)
				.header("Content-Length", streamOut.size).build();
	}

	@POST
	@Path("{type}/{id}/{streamId}")
	@Consumes("multipart/form-data")
	@Produces({ "application/vnd.uub.record+json", "*/*" })
	public Response uploadFileJson(@HeaderParam("authToken") String headerAuthToken,
			@QueryParam("authToken") String queryAuthToken, @PathParam("type") String type,
			@PathParam("id") String id, @FormDataParam("file") InputStream uploadedInputStream,
			@FormDataParam("file") FormDataContentDisposition fileDetail) {
		return uploadFile("application/vnd.uub.record+json", headerAuthToken, queryAuthToken, type,
				id, uploadedInputStream, fileDetail);
	}

	@POST
	@Path("{type}/{id}/{streamId}")
	@Consumes("multipart/form-data")
	@Produces("application/vnd.uub.record+xml")
	public Response uploadFileXml(@HeaderParam("authToken") String headerAuthToken,
			@QueryParam("authToken") String queryAuthToken, @PathParam("type") String type,
			@PathParam("id") String id, @FormDataParam("file") InputStream uploadedInputStream,
			@FormDataParam("file") FormDataContentDisposition fileDetail) {
		return uploadFile("application/vnd.uub.record+xml", headerAuthToken, queryAuthToken, type,
				id, uploadedInputStream, fileDetail);
	}

	private Response uploadFile(String accept, String headerAuthToken, String queryAuthToken,
			String type, String id, InputStream uploadedInputStream,
			FormDataContentDisposition fileDetail) {
		String fileName = fileDetail.getFileName();
		String usedToken = getExistingTokenPreferHeader(headerAuthToken, queryAuthToken);
		return uploadFileUsingAuthTokenWithStream(accept, usedToken, type, id, uploadedInputStream,
				fileName);
	}

	Response uploadFileUsingAuthTokenWithStream(String accept, String authToken, String type,
			String id, InputStream uploadedInputStream, String fileName) {
		try {
			return tryUploadFile(accept, authToken, type, id, uploadedInputStream, fileName);
		} catch (Exception error) {
			return handleError(authToken, error);
		}
	}

	private Response tryUploadFile(String accept, String authToken, String type, String id,
			InputStream inputStream, String fileName) {
		DataRecord updatedRecord = SpiderInstanceProvider.getUploader().upload(authToken, type, id,
				inputStream, fileName);

		String convertedDataList = convertConvertibleToString(accept, updatedRecord);

		return Response.ok(convertedDataList).header(HttpHeaders.CONTENT_TYPE, accept).build();
	}

	@GET
	@Path("searchResult/{searchId}")
	@Produces({ "application/vnd.uub.recordList+json", "*/*" })
	public Response searchRecordJson(@HeaderParam("authToken") String headerAuthToken,
			@QueryParam("authToken") String queryAuthToken, @PathParam("searchId") String searchId,
			@QueryParam("searchData") String searchDataAsString) {
		return searchRecord("application/vnd.uub.recordList+json", headerAuthToken, queryAuthToken,
				searchId, searchDataAsString);
	}

	@GET
	@Path("searchResult/{searchId}")
	@Produces("application/vnd.uub.recordList+xml")
	public Response searchRecordXml(@HeaderParam("authToken") String headerAuthToken,
			@QueryParam("authToken") String queryAuthToken, @PathParam("searchId") String searchId,
			@QueryParam("searchData") String searchDataAsString) {
		return searchRecord("application/vnd.uub.recordList+xml", headerAuthToken, queryAuthToken,
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
			return handleError(authToken, error);
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
		return (DataGroup) convertStringToData(searchDataType, searchDataAsString);
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
	@Consumes({ "application/vnd.uub.workorder+json", "*/*" })
	@Produces({ "application/vnd.uub.record+json", "*/*" })
	public Response validateRecordJsonJson(@HeaderParam("authToken") String headerAuthToken,
			@QueryParam("authToken") String queryAuthToken, @PathParam("type") String type,
			String jsonValidationRecord) {
		return validateRecord("application/vnd.uub.workorder+json",
				"application/vnd.uub.record+json", headerAuthToken, queryAuthToken,
				jsonValidationRecord);
	}

	@POST
	@Path("{type}")
	@Consumes("application/vnd.uub.workorder+json")
	@Produces("application/vnd.uub.record+xml")
	public Response validateRecordJsonXml(@HeaderParam("authToken") String headerAuthToken,
			@QueryParam("authToken") String queryAuthToken, @PathParam("type") String type,
			String jsonValidationRecord) {
		return validateRecord("application/vnd.uub.workorder+json",
				"application/vnd.uub.record+xml", headerAuthToken, queryAuthToken,
				jsonValidationRecord);
	}

	@POST
	@Path("{type}")
	@Consumes("application/vnd.uub.workorder+xml")
	@Produces("application/vnd.uub.record+json")
	public Response validateRecordXmlJson(@HeaderParam("authToken") String headerAuthToken,
			@QueryParam("authToken") String queryAuthToken, @PathParam("type") String type,
			String jsonValidationRecord) {
		return validateRecord("application/vnd.uub.workorder+xml",
				"application/vnd.uub.record+json", headerAuthToken, queryAuthToken,
				jsonValidationRecord);
	}

	@POST
	@Path("{type}")
	@Consumes("application/vnd.uub.workorder+xml")
	@Produces("application/vnd.uub.record+xml")
	public Response validateRecordXmlXml(@HeaderParam("authToken") String headerAuthToken,
			@QueryParam("authToken") String queryAuthToken, @PathParam("type") String type,
			String jsonValidationRecord) {
		return validateRecord("application/vnd.uub.workorder+xml", "application/vnd.uub.record+xml",
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
			return handleError(authToken, error);
		}
	}

	private Response tryValidateRecord(String contentType, String accept, String authToken,
			String type, String inputRecord) {
		DataGroup validationOrder = null;
		DataGroup recordToValidate = null;
		if (contentType.endsWith("+xml")) {
			DataGroup container = (DataGroup) convertXmlToDataElement(inputRecord);
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
		DataPart dataPart = jsonToDataConverter.toInstance();
		return (DataGroup) dataPart;
	}

	@POST
	@Path("index/{type}")
	@Consumes({ "application/vnd.uub.record+json", "*/*" })
	@Produces({ "application/vnd.uub.record+json", "*/*" })
	public Response batchIndexJsonJson(@HeaderParam("authToken") String headerAuthToken,
			@QueryParam("authToken") String queryAuthToken, @PathParam("type") String type,
			String indexSettingsAsJson) {
		return batchIndex("application/vnd.uub.record+json", "application/vnd.uub.record+json",
				headerAuthToken, queryAuthToken, type, indexSettingsAsJson);
	}

	@POST
	@Path("index/{type}")
	@Consumes("application/vnd.uub.record+json")
	@Produces("application/vnd.uub.record+xml")
	public Response batchIndexJsonXml(@HeaderParam("authToken") String headerAuthToken,
			@QueryParam("authToken") String queryAuthToken, @PathParam("type") String type,
			String indexSettingsAsJson) {
		return batchIndex("application/vnd.uub.record+json", "application/vnd.uub.record+xml",
				headerAuthToken, queryAuthToken, type, indexSettingsAsJson);
	}

	@POST
	@Path("index/{type}")
	@Consumes("application/vnd.uub.record+xml")
	@Produces("application/vnd.uub.record+json")
	public Response batchIndexXmlJson(@HeaderParam("authToken") String headerAuthToken,
			@QueryParam("authToken") String queryAuthToken, @PathParam("type") String type,
			String indexSettingsAsJson) {
		return batchIndex("application/vnd.uub.record+xml", "application/vnd.uub.record+json",
				headerAuthToken, queryAuthToken, type, indexSettingsAsJson);
	}

	@POST
	@Path("index/{type}")
	@Consumes("application/vnd.uub.record+xml")
	@Produces("application/vnd.uub.record+xml")
	public Response batchIndexXmlXml(@HeaderParam("authToken") String headerAuthToken,
			@QueryParam("authToken") String queryAuthToken, @PathParam("type") String type,
			String indexSettingsAsJson) {
		return batchIndex("application/vnd.uub.record+xml", "application/vnd.uub.record+xml",
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
			return "application/vnd.uub.record+json";
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
			return handleError(authToken, error);
		}
	}

	private Response tryIndexRecordList(String contentType, String accept, String authToken,
			String type, String jsonIndexSettings) throws URISyntaxException {
		DataGroup indexSettings = (DataGroup) convertStringToData(contentType, jsonIndexSettings);

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
