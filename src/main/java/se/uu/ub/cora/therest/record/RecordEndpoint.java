/*
 * Copyright 2015, 2016, 2018 Uppsala University Library
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

import se.uu.ub.cora.json.builder.JsonBuilderFactory;
import se.uu.ub.cora.json.builder.org.OrgJsonBuilderFactoryAdapter;
import se.uu.ub.cora.json.parser.JsonObject;
import se.uu.ub.cora.json.parser.JsonParseException;
import se.uu.ub.cora.json.parser.JsonParser;
import se.uu.ub.cora.json.parser.JsonValue;
import se.uu.ub.cora.json.parser.org.OrgJsonParser;
import se.uu.ub.cora.spider.authentication.AuthenticationException;
import se.uu.ub.cora.spider.authorization.AuthorizationException;
import se.uu.ub.cora.spider.data.DataMissingException;
import se.uu.ub.cora.spider.data.SpiderDataGroup;
import se.uu.ub.cora.spider.data.SpiderDataList;
import se.uu.ub.cora.spider.data.SpiderDataRecord;
import se.uu.ub.cora.spider.data.SpiderInputStream;
import se.uu.ub.cora.spider.dependency.SpiderInstanceProvider;
import se.uu.ub.cora.spider.record.DataException;
import se.uu.ub.cora.spider.record.MisuseException;
import se.uu.ub.cora.spider.record.ValidationResult;
import se.uu.ub.cora.spider.record.storage.RecordConflictException;
import se.uu.ub.cora.spider.record.storage.RecordNotFoundException;
import se.uu.ub.cora.therest.data.RestDataElement;
import se.uu.ub.cora.therest.data.RestDataGroup;
import se.uu.ub.cora.therest.data.RestDataList;
import se.uu.ub.cora.therest.data.RestDataRecord;
import se.uu.ub.cora.therest.data.converter.ConverterException;
import se.uu.ub.cora.therest.data.converter.DataListToJsonConverter;
import se.uu.ub.cora.therest.data.converter.DataRecordToJsonConverter;
import se.uu.ub.cora.therest.data.converter.JsonToDataConverter;
import se.uu.ub.cora.therest.data.converter.JsonToDataConverterFactory;
import se.uu.ub.cora.therest.data.converter.JsonToDataConverterFactoryImp;
import se.uu.ub.cora.therest.data.converter.spider.DataGroupRestToSpiderConverter;
import se.uu.ub.cora.therest.data.converter.spider.DataListSpiderToRestConverter;
import se.uu.ub.cora.therest.data.converter.spider.DataRecordSpiderToRestConverter;

@Path("record")
public class RecordEndpoint {
	private static final int AFTERHTTP = 10;
	private String url;
	HttpServletRequest request;

	public RecordEndpoint(@Context HttpServletRequest req) {
		request = req;
		url = getBaseURLFromURI();
	}

	private String getBaseURLFromURI() {
		String baseURL = getBaseURLFromRequest();

		baseURL = changeHttpToHttpsIfHeaderSaysSo(baseURL);

		return baseURL;
	}

	private String getBaseURLFromRequest() {
		String tempUrl = request.getRequestURL().toString();
		String baseURL = tempUrl.substring(0, tempUrl.indexOf('/', AFTERHTTP));
		baseURL += SpiderInstanceProvider.getInitInfo().get("theRestPublicPathToSystem");
		baseURL += "record/";
		return baseURL;
	}

	private String changeHttpToHttpsIfHeaderSaysSo(String baseURI) {
		String forwardedProtocol = request.getHeader("X-Forwarded-Proto");

		if (ifForwardedProtocolExists(forwardedProtocol)) {
			return baseURI.replaceAll("http:", forwardedProtocol + ":");
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
		SpiderDataGroup record = convertJsonStringToSpiderDataGroup(jsonRecord);
		SpiderDataRecord createdRecord = SpiderInstanceProvider.getSpiderRecordCreator()
				.createAndStoreRecord(authToken, type, record);

		SpiderDataGroup createdGroup = createdRecord.getSpiderDataGroup();
		SpiderDataGroup recordInfo = createdGroup.extractGroup("recordInfo");
		String createdId = recordInfo.extractAtomicValue("id");

		String json = convertSpiderDataRecordToJsonString(createdRecord);

		URI uri = new URI("record/" + type + "/" + createdId);
		return Response.created(uri).entity(json).build();
	}

	private SpiderDataGroup convertJsonStringToSpiderDataGroup(String jsonRecord) {
		RestDataGroup restDataGroup = convertJsonStringToRestDataGroup(jsonRecord);
		return DataGroupRestToSpiderConverter.fromRestDataGroup(restDataGroup).toSpider();
	}

	private RestDataGroup convertJsonStringToRestDataGroup(String jsonRecord) {
		JsonParser jsonParser = new OrgJsonParser();
		JsonValue jsonValue = jsonParser.parseString(jsonRecord);
		JsonToDataConverterFactory jsonToDataConverterFactory = new JsonToDataConverterFactoryImp();
		JsonToDataConverter jsonToDataConverter = jsonToDataConverterFactory
				.createForJsonObject(jsonValue);
		RestDataElement restDataElement = jsonToDataConverter.toInstance();
		return (RestDataGroup) restDataElement;
	}

	private String convertSpiderDataRecordToJsonString(SpiderDataRecord record) {
		RestDataRecord restDataRecord = convertSpiderDataRecordToRestDataRecord(record);
		DataRecordToJsonConverter dataToJsonConverter = convertRestDataGroupToJson(restDataRecord);
		return dataToJsonConverter.toJson();
	}

	private RestDataRecord convertSpiderDataRecordToRestDataRecord(SpiderDataRecord record) {

		DataRecordSpiderToRestConverter converter = DataRecordSpiderToRestConverter
				.fromSpiderDataRecordWithBaseURL(record, url);
		return converter.toRest();
	}

	private DataRecordToJsonConverter convertRestDataGroupToJson(RestDataRecord restDataRecord) {
		JsonBuilderFactory jsonBuilderFactory = new OrgJsonBuilderFactoryAdapter();
		return DataRecordToJsonConverter.usingJsonFactoryForRestDataRecord(jsonBuilderFactory,
				restDataRecord);
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
		SpiderDataGroup filter = convertJsonStringToSpiderDataGroup(filterAsJson);
		SpiderDataList readRecordList = SpiderInstanceProvider.getSpiderRecordListReader()
				.readRecordList(authToken, type, filter);
		String json = convertSpiderRecordListToJsonString(readRecordList);
		return Response.status(Response.Status.OK).entity(json).build();
	}

	private String convertSpiderRecordListToJsonString(SpiderDataList readRecordList) {
		DataListSpiderToRestConverter listSpiderToRestConverter = DataListSpiderToRestConverter
				.fromSpiderDataListWithBaseURL(readRecordList, url);
		RestDataList restRecordList = listSpiderToRestConverter.toRest();

		JsonBuilderFactory jsonBuilderFactory = new OrgJsonBuilderFactoryAdapter();
		DataListToJsonConverter recordListToJsonConverter = DataListToJsonConverter
				.usingJsonFactoryForRestDataList(jsonBuilderFactory, restRecordList);
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
		SpiderDataRecord record = SpiderInstanceProvider.getSpiderRecordReader()
				.readRecord(authToken, type, id);
		String json = convertSpiderDataRecordToJsonString(record);
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
		SpiderDataList dataList = SpiderInstanceProvider.getSpiderRecordIncomingLinksReader()
				.readIncomingLinks(authToken, type, id);
		String json = convertSpiderRecordListToJsonString(dataList);
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
		SpiderInstanceProvider.getSpiderRecordDeleter().deleteRecord(authToken, type, id);
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
		SpiderDataGroup record = convertJsonStringToSpiderDataGroup(jsonRecord);
		SpiderDataRecord updatedRecord = SpiderInstanceProvider.getSpiderRecordUpdater()
				.updateRecord(authToken, type, id, record);
		String json = convertSpiderDataRecordToJsonString(updatedRecord);
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
		SpiderInputStream streamOut = SpiderInstanceProvider.getSpiderDownloader()
				.download(authToken, type, id, streamId);
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
		SpiderDataRecord updatedRecord = SpiderInstanceProvider.getSpiderUploader()
				.upload(authToken, type, id, inputStream, fileName);
		String json = convertSpiderDataRecordToJsonString(updatedRecord);
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
		SpiderDataGroup searchData = convertJsonStringToSpiderDataGroup(searchDataAsJson);

		SpiderDataList searchRecordList = SpiderInstanceProvider.getSpiderRecordSearcher()
				.search(authToken, searchId, searchData);
		String json = convertSpiderRecordListToJsonString(searchRecordList);
		return Response.status(Response.Status.OK).entity(json).build();
	}

	@POST
	@Path("{type}")
	@Consumes("application/vnd.uub.validationrecord+json")
	@Produces("application/vnd.uub.validationrecord+json")
	public Response validateRecord(@HeaderParam("authToken") String headerAuthToken,
			@QueryParam("authToken") String queryAuthToken, @PathParam("type") String type,
			String jsonValidationRecord) {
		String usedToken = getExistingTokenPreferHeader(headerAuthToken, queryAuthToken);
		// SpiderDataGroup validationRecord =
		// convertJsonStringToSpiderDataGroup(jsonValidationRecord);
		// SpiderDataGroup recordToValidate =
		// convertJsonStringToSpiderDataGroup(jsonValidationRecord);

		// return Response.status(Response.Status.OK).entity("ok from validation " +
		// actionToPerform)
		// .build();
		return validateRecordUsingAuthTokenWithRecord(usedToken, type, jsonValidationRecord);
		// return createRecordUsingAuthTokenWithRecord(usedToken, type, jsonRecord);
	}

	public Response validateRecordUsingAuthTokenWithRecord(String authToken, String type,
			String jsonRecord) {
		try {
			return tryValidateRecord(authToken, type, jsonRecord);
		} catch (Exception error) {
			return handleError(authToken, error);
		}
	}

	private Response tryValidateRecord(String authToken, String type, String jsonRecord)
			throws URISyntaxException {
		JsonParser jsonParser = new OrgJsonParser();
		JsonValue jsonValue = jsonParser.parseString(jsonRecord);
		JsonObject jsonObject = (JsonObject) jsonValue;
		SpiderDataGroup validationOrder = getDataGroupFromJsonObjectUsingName(jsonObject,
				"validationInfo");

		// JsonObject recordToValidate = jsonObject.getValueAsJsonObject("record");

		SpiderDataGroup recordToValidate = getDataGroupFromJsonObjectUsingName(jsonObject,
				"record");

		ValidationResult validationResult = SpiderInstanceProvider.getSpiderRecordValidator()
				.validateRecord(authToken, type, validationOrder, recordToValidate);

		//
		// String json = convertSpiderDataRecordToJsonString(createdRecord);
		//
		// URI uri = new URI("record/" + type + "/" + createdId);
		// return Response.created(uri).entity(json).build();
		return Response.status(Response.Status.OK).entity("ok from validation ").build();
	}

	private SpiderDataGroup getDataGroupFromJsonObjectUsingName(JsonObject jsonObject,
			String name) {
		JsonValue validationInfoJson = jsonObject.getValue(name);

		JsonToDataConverterFactory jsonToDataConverterFactory = new JsonToDataConverterFactoryImp();
		JsonToDataConverter jsonToDataConverter = jsonToDataConverterFactory
				.createForJsonObject(validationInfoJson);

		RestDataGroup restDataGroup = (RestDataGroup) jsonToDataConverter.toInstance();
		return DataGroupRestToSpiderConverter.fromRestDataGroup(restDataGroup).toSpider();
	}

}
