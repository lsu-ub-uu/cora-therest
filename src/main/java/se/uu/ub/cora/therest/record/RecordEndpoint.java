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

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataList;
import se.uu.ub.cora.data.DataRecord;
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
import se.uu.ub.cora.therest.converter.ConverterException;
import se.uu.ub.cora.therest.converter.coratorest.CoraToRestConverter;
import se.uu.ub.cora.therest.converter.coratorest.CoraToRestConverterFactory;
import se.uu.ub.cora.therest.converter.coratorest.CoraToRestConverterFactoryImp;
import se.uu.ub.cora.therest.converter.jsontorest.JsonToDataConverter;
import se.uu.ub.cora.therest.converter.jsontorest.JsonToDataConverterFactory;
import se.uu.ub.cora.therest.converter.jsontorest.JsonToDataConverterFactoryImp;
import se.uu.ub.cora.therest.converter.resttocora.RestToDataConverter;
import se.uu.ub.cora.therest.converter.resttocora.RestToDataConverterFactory;
import se.uu.ub.cora.therest.converter.resttocora.RestToDataConverterFactoryImp;
import se.uu.ub.cora.therest.converter.resttojson.RestDataToJsonConverter;
import se.uu.ub.cora.therest.converter.resttojson.RestDataToJsonConverterFactory;
import se.uu.ub.cora.therest.converter.resttojson.RestDataToJsonConverterFactoryImp;
import se.uu.ub.cora.therest.data.RestData;
import se.uu.ub.cora.therest.data.RestDataGroup;
import se.uu.ub.cora.therest.data.RestDataRecord;

@Path("record")
public class RecordEndpoint {
	private static final int AFTERHTTP = 10;
	private String url;
	HttpServletRequest request;
	private Logger log = LoggerProvider.getLoggerForClass(RecordEndpoint.class);

	private CoraToRestConverterFactory dataToRestConverterFactory = new CoraToRestConverterFactoryImp();
	private RestDataToJsonConverterFactory restDataToJsonConverterFactory = new RestDataToJsonConverterFactoryImp();
	private JsonToDataConverterFactory jsonToDataConverterFactory = new JsonToDataConverterFactoryImp();
	private JsonParser jsonParser = new OrgJsonParser();
	private RestToDataConverterFactory restToDataConverterFactory = new RestToDataConverterFactoryImp();

	public RecordEndpoint(@Context HttpServletRequest req) {
		request = req;
		url = getBaseURLFromURI();
	}

	private final String getBaseURLFromURI() {
		String baseURL = getBaseURLFromRequest();

		baseURL = changeHttpToHttpsIfHeaderSaysSo(baseURL);

		return baseURL;
	}

	private final String getBaseURLFromRequest() {
		String tempUrl = request.getRequestURL().toString();
		String baseURL = tempUrl.substring(0, tempUrl.indexOf('/', AFTERHTTP));
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

	@POST
	@Path("{type}")
	@Consumes("application/vnd.uub.record+json")
	@Produces("application/vnd.uub.record+json")
	public Response createRecord(@HeaderParam("authToken") String headerAuthToken,
			@QueryParam("authToken") String queryAuthToken, @PathParam("type") String type,
			String jsonRecord) {
		String usedToken = getExistingTokenPreferHeader(headerAuthToken, queryAuthToken);
		return createRecordUsingAuthTokenWithRecord(usedToken, type, jsonRecord);
	}

	public Response createRecordUsingAuthTokenWithRecord(String authToken, String type,
			String jsonRecord) {
		try {
			return tryCreateRecord(authToken, type, jsonRecord);
		} catch (Exception error) {
			return handleError(authToken, error);
		}
	}

	private Response tryCreateRecord(String authToken, String type, String jsonRecord)
			throws URISyntaxException {
		DataGroup record = convertJsonStringToDataGroup(jsonRecord);
		DataRecord createdRecord = SpiderInstanceProvider.getRecordCreator()
				.createAndStoreRecord(authToken, type, record);

		DataGroup createdGroup = createdRecord.getDataGroup();
		DataGroup recordInfo = createdGroup.getFirstGroupWithNameInData("recordInfo");
		String createdId = recordInfo.getFirstAtomicValueWithNameInData("id");

		String json = convertDataRecordToJsonString(createdRecord);

		String urlDelimiter = "/";
		URI uri = new URI("record/" + type + urlDelimiter + createdId);
		return Response.created(uri).entity(json).build();
	}

	private DataGroup convertJsonStringToDataGroup(String jsonRecord) {
		RestDataGroup restDataGroup = convertJsonStringToRestDataGroup(jsonRecord);
		RestToDataConverter converter = restToDataConverterFactory.factor(restDataGroup);
		return converter.convert();
	}

	private RestDataGroup convertJsonStringToRestDataGroup(String jsonRecord) {
		JsonValue jsonValue = jsonParser.parseString(jsonRecord);
		JsonToDataConverter jsonToDataConverter = jsonToDataConverterFactory
				.createForJsonObject(jsonValue);
		return (RestDataGroup) jsonToDataConverter.toInstance();
	}

	private String convertDataRecordToJsonString(DataRecord record) {
		RestDataRecord restDataRecord = convertDataRecordToRestDataRecord(record);
		RestDataToJsonConverter dataToJsonConverter = getRestToJsonConverter(restDataRecord);
		return dataToJsonConverter.toJson();
	}

	private RestDataRecord convertDataRecordToRestDataRecord(DataRecord record) {
		CoraToRestConverter toRestConverter = dataToRestConverterFactory.factorForDataRecord(record,
				url);
		return (RestDataRecord) toRestConverter.toRest();

	}

	private RestDataToJsonConverter getRestToJsonConverter(RestDataRecord restDataRecord) {
		return restDataToJsonConverterFactory.createForRestData(restDataRecord);
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
		return error instanceof JsonParseException || error instanceof DataException
				|| error instanceof ConverterException || error instanceof DataMissingException;
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
	@Produces("application/vnd.uub.recordList+json")
	public Response readRecordList(@HeaderParam("authToken") String headerAuthToken,
			@QueryParam("authToken") String queryAuthToken, @PathParam("type") String type,
			@QueryParam("filter") String filterAsJson) {
		String usedToken = getExistingTokenPreferHeader(headerAuthToken, queryAuthToken);
		String filter = createEmptyFilterIfParameterDoesNotExist(filterAsJson);
		return readRecordListUsingAuthTokenByType(usedToken, type, filter);
	}

	private String createEmptyFilterIfParameterDoesNotExist(String filterAsJson) {
		String filter = filterAsJson;
		if (filterAsJson == null) {
			filter = "{\"name\":\"filter\",\"children\":[]}";
		}
		return filter;
	}

	Response readRecordListUsingAuthTokenByType(String authToken, String type,
			String filterAsJson) {
		try {
			return tryReadRecordList(authToken, type, filterAsJson);
		} catch (Exception error) {
			return handleError(authToken, error);
		}
	}

	private Response tryReadRecordList(String authToken, String type, String filterAsJson) {
		DataGroup filter = convertJsonStringToDataGroup(filterAsJson);
		DataList readRecordList = SpiderInstanceProvider.getRecordListReader()
				.readRecordList(authToken, type, filter);
		String json = convertRecordListToJsonString(readRecordList);
		return Response.status(Response.Status.OK).entity(json).build();
	}

	private String convertRecordListToJsonString(DataList readRecordList) {
		CoraToRestConverter listSpiderToRestConverter = dataToRestConverterFactory
				.factorForDataList(readRecordList, url);

		RestData restDataList = listSpiderToRestConverter.toRest();

		RestDataToJsonConverter recordListToJsonConverter = restDataToJsonConverterFactory
				.createForRestData(restDataList);
		return recordListToJsonConverter.toJson();
	}

	@GET
	@Path("{type}/{id}")
	@Produces("application/vnd.uub.record+json")
	public Response readRecord(@HeaderParam("authToken") String headerAuthToken,
			@QueryParam("authToken") String queryAuthToken, @PathParam("type") String type,
			@PathParam("id") String id) {
		String usedToken = getExistingTokenPreferHeader(headerAuthToken, queryAuthToken);
		return readRecordUsingAuthTokenByTypeAndId(usedToken, type, id);
	}

	public Response readRecordUsingAuthTokenByTypeAndId(String authToken, String type, String id) {
		try {
			return tryReadRecord(authToken, type, id);
		} catch (Exception error) {
			return handleError(authToken, error);
		}
	}

	private Response tryReadRecord(String authToken, String type, String id) {
		DataRecord record = SpiderInstanceProvider.getRecordReader().readRecord(authToken, type,
				id);
		String json = convertDataRecordToJsonString(record);
		return Response.status(Response.Status.OK).entity(json).build();
	}

	@GET
	@Path("{type}/{id}/incomingLinks")
	@Produces("application/vnd.uub.recordList+json")
	public Response readIncomingRecordLinks(@HeaderParam("authToken") String headerAuthToken,
			@QueryParam("authToken") String queryAuthToken, @PathParam("type") String type,
			@PathParam("id") String id) {
		String usedToken = getExistingTokenPreferHeader(headerAuthToken, queryAuthToken);
		return readIncomingRecordLinksUsingAuthTokenByTypeAndId(usedToken, type, id);
	}

	Response readIncomingRecordLinksUsingAuthTokenByTypeAndId(String authToken, String type,
			String id) {
		try {
			return tryReadIncomingRecordLinks(authToken, type, id);
		} catch (Exception error) {
			return handleError(authToken, error);
		}
	}

	private Response tryReadIncomingRecordLinks(String authToken, String type, String id) {
		DataList dataList = SpiderInstanceProvider.getIncomingLinksReader()
				.readIncomingLinks(authToken, type, id);
		String json = convertRecordListToJsonString(dataList);
		return Response.status(Response.Status.OK).entity(json).build();
	}

	@DELETE
	@Path("{type}/{id}")
	public Response deleteRecord(@HeaderParam("authToken") String headerAuthToken,
			@QueryParam("authToken") String queryAuthToken, @PathParam("type") String type,
			@PathParam("id") String id) {
		String usedToken = getExistingTokenPreferHeader(headerAuthToken, queryAuthToken);
		return deleteRecordUsingAuthTokenByTypeAndId(usedToken, type, id);
	}

	public Response deleteRecordUsingAuthTokenByTypeAndId(String authToken, String type,
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

	@POST
	@Path("{type}/{id}")
	@Consumes("application/vnd.uub.record+json")
	@Produces("application/vnd.uub.record+json")
	public Response updateRecord(@HeaderParam("authToken") String headerAuthToken,
			@QueryParam("authToken") String queryAuthToken, @PathParam("type") String type,
			@PathParam("id") String id, String jsonRecord) {
		String usedToken = getExistingTokenPreferHeader(headerAuthToken, queryAuthToken);
		return updateRecordUsingAuthTokenWithRecord(usedToken, type, id, jsonRecord);
	}

	public Response updateRecordUsingAuthTokenWithRecord(String authToken, String type, String id,
			String jsonRecord) {
		try {
			return tryUpdateRecord(authToken, type, id, jsonRecord);
		} catch (Exception error) {
			return handleError(authToken, error);
		}
	}

	private Response tryUpdateRecord(String authToken, String type, String id, String jsonRecord) {
		DataGroup record = convertJsonStringToDataGroup(jsonRecord);
		DataRecord updatedRecord = SpiderInstanceProvider.getRecordUpdater()
				.updateRecord(authToken, type, id, record);
		String json = convertDataRecordToJsonString(updatedRecord);
		return Response.status(Response.Status.OK).entity(json).build();
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
	@Produces("application/vnd.uub.record+json")
	public Response uploadFile(@HeaderParam("authToken") String headerAuthToken,
			@QueryParam("authToken") String queryAuthToken, @PathParam("type") String type,
			@PathParam("id") String id, @FormDataParam("file") InputStream uploadedInputStream,
			@FormDataParam("file") FormDataContentDisposition fileDetail) {
		String fileName = fileDetail.getFileName();
		String usedToken = getExistingTokenPreferHeader(headerAuthToken, queryAuthToken);
		return uploadFileUsingAuthTokenWithStream(usedToken, type, id, uploadedInputStream,
				fileName);
	}

	Response uploadFileUsingAuthTokenWithStream(String authToken, String type, String id,
			InputStream uploadedInputStream, String fileName) {
		try {
			return tryUploadFile(authToken, type, id, uploadedInputStream, fileName);
		} catch (Exception error) {
			return handleError(authToken, error);
		}
	}

	private Response tryUploadFile(String authToken, String type, String id,
			InputStream inputStream, String fileName) {
		DataRecord updatedRecord = SpiderInstanceProvider.getUploader().upload(authToken, type, id,
				inputStream, fileName);
		String json = convertDataRecordToJsonString(updatedRecord);
		return Response.ok(json).build();
	}

	@GET
	@Path("searchResult/{searchId}")
	@Produces("application/vnd.uub.recordList+json")
	public Response searchRecord(@HeaderParam("authToken") String headerAuthToken,
			@QueryParam("authToken") String queryAuthToken, @PathParam("searchId") String searchId,
			@QueryParam("searchData") String searchDataAsJson) {
		String usedToken = getExistingTokenPreferHeader(headerAuthToken, queryAuthToken);
		return searchRecordUsingAuthTokenBySearchData(usedToken, searchId, searchDataAsJson);
	}

	private Response searchRecordUsingAuthTokenBySearchData(String authToken, String searchId,
			String searchDataAsJson) {
		try {
			return trySearchRecord(authToken, searchId, searchDataAsJson);
		} catch (Exception error) {
			return handleError(authToken, error);
		}
	}

	private Response trySearchRecord(String authToken, String searchId, String searchDataAsJson) {
		DataGroup searchData = convertJsonStringToDataGroup(searchDataAsJson);

		DataList searchRecordList = SpiderInstanceProvider.getRecordSearcher().search(authToken,
				searchId, searchData);
		String json = convertRecordListToJsonString(searchRecordList);
		return Response.status(Response.Status.OK).entity(json).build();
	}

	@POST
	@Path("{type}")
	@Consumes("application/vnd.uub.workorder+json")
	@Produces("application/vnd.uub.record+json")
	public Response validateRecord(@HeaderParam("authToken") String headerAuthToken,
			@QueryParam("authToken") String queryAuthToken, @PathParam("type") String type,
			String jsonValidationRecord) {
		String usedToken = getExistingTokenPreferHeader(headerAuthToken, queryAuthToken);
		String recordTypeToUse = "validationOrder";
		return validateRecordUsingAuthTokenWithRecord(usedToken, recordTypeToUse,
				jsonValidationRecord);
	}

	public Response validateRecordUsingAuthTokenWithRecord(String authToken, String type,
			String jsonRecord) {
		try {
			return tryValidateRecord(authToken, type, jsonRecord);
		} catch (Exception error) {
			return handleError(authToken, error);
		}
	}

	private Response tryValidateRecord(String authToken, String type, String jsonRecord) {
		JsonObject jsonObject = getJsonObjectFromJsonRecordString(jsonRecord);
		DataGroup validationOrder = getDataGroupFromJsonObjectUsingName(jsonObject, "order");
		DataGroup recordToValidate = getDataGroupFromJsonObjectUsingName(jsonObject, "record");

		RecordValidator recordValidator = SpiderInstanceProvider.getRecordValidator();
		DataRecord validationResult = recordValidator.validateRecord(authToken, type,
				validationOrder, recordToValidate);

		String json = convertDataRecordToJsonString(validationResult);
		return Response.status(Response.Status.OK).entity(json).build();
	}

	private JsonObject getJsonObjectFromJsonRecordString(String jsonRecord) {
		JsonValue jsonValue = jsonParser.parseString(jsonRecord);
		return (JsonObject) jsonValue;
	}

	private DataGroup getDataGroupFromJsonObjectUsingName(JsonObject jsonObject, String name) {
		JsonToDataConverter jsonToDataConverter = createConverter(jsonObject, name);
		RestDataGroup restDataGroup = (RestDataGroup) jsonToDataConverter.toInstance();
		RestToDataConverter converter = restToDataConverterFactory.factor(restDataGroup);
		return converter.convert();
	}

	private JsonToDataConverter createConverter(JsonObject jsonObject, String name) {
		JsonValue validationInfoJson = jsonObject.getValue(name);
		return jsonToDataConverterFactory.createForJsonObject(validationInfoJson);
	}

	@POST
	@Path("index/{type}")
	@Produces("application/vnd.uub.record+json")
	public Response indexRecordList(@HeaderParam("authToken") String headerAuthToken,
			@QueryParam("authToken") String queryAuthToken, @PathParam("type") String type,
			@QueryParam("filter") String filterAsJson) {
		String usedToken = getExistingTokenPreferHeader(headerAuthToken, queryAuthToken);
		String jsonFilter = createEmptyFilterIfParameterDoesNotExist(filterAsJson);

		return indexRecordListUsingAuthTokenByType(usedToken, type, jsonFilter);
	}

	Response indexRecordListUsingAuthTokenByType(String authToken, String type,
			String filterAsJson) {
		try {
			return tryIndexRecordList(authToken, type, filterAsJson);

		} catch (Exception error) {
			return handleError(authToken, error);
		}
	}

	private Response tryIndexRecordList(String authToken, String type, String filterAsJson) {
		DataGroup filter = convertJsonStringToDataGroup(filterAsJson);
		RecordListIndexer indexBatchJobCreator = SpiderInstanceProvider.getRecordListIndexer();
		DataRecord indexBatchJob = indexBatchJobCreator.indexRecordList(authToken, type, filter);
		String json = convertDataRecordToJsonString(indexBatchJob);

		return Response.status(Response.Status.OK).entity(json).build();
	}

	CoraToRestConverterFactory getDataToRestConverterFactory() {
		return dataToRestConverterFactory;
	}

	void setDataToRestConverterFactory(CoraToRestConverterFactory converterFactory) {
		this.dataToRestConverterFactory = converterFactory;
	}

	RestDataToJsonConverterFactory getRestDataToJsonConverterFactory() {
		return restDataToJsonConverterFactory;
	}

	void setRestRecordToJsonConverterFactory(
			RestDataToJsonConverterFactory restRecordToJsonConverterFactory) {
		this.restDataToJsonConverterFactory = restRecordToJsonConverterFactory;

	}

	JsonToDataConverterFactory getJsonToDataConverterFactory() {
		return jsonToDataConverterFactory;
	}

	JsonParser getJsonParser() {
		return jsonParser;
	}

	void setJsonToDataConverterFactory(JsonToDataConverterFactory jsonToDataConverterFactory) {
		this.jsonToDataConverterFactory = jsonToDataConverterFactory;

	}

	void setJsonParser(JsonParser jsonParser) {
		this.jsonParser = jsonParser;

	}

	RestToDataConverterFactory getRestToDataConverterFactory() {
		return restToDataConverterFactory;
	}

	void setRestToDataConverterFactory(RestToDataConverterFactory restToDataConverterFactory) {
		this.restToDataConverterFactory = restToDataConverterFactory;

	}

}
