/*
 * Copyright 2015 Uppsala University Library
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

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import se.uu.ub.cora.spider.data.DataMissingException;
import se.uu.ub.cora.spider.data.SpiderDataGroup;
import se.uu.ub.cora.spider.data.SpiderDataList;
import se.uu.ub.cora.spider.data.SpiderDataRecord;
import se.uu.ub.cora.spider.dependency.SpiderInstanceProvider;
import se.uu.ub.cora.spider.record.AuthorizationException;
import se.uu.ub.cora.spider.record.DataException;
import se.uu.ub.cora.spider.record.MisuseException;
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
import se.uu.ub.cora.therest.json.builder.JsonBuilderFactory;
import se.uu.ub.cora.therest.json.builder.org.OrgJsonBuilderFactoryAdapter;
import se.uu.ub.cora.therest.json.parser.JsonParseException;
import se.uu.ub.cora.therest.json.parser.JsonParser;
import se.uu.ub.cora.therest.json.parser.JsonValue;
import se.uu.ub.cora.therest.json.parser.org.OrgJsonParser;

@Path("record")
public class RecordEndpoint {

	private static final String USER_ID = "userId";
	private UriInfo uriInfo;
	private String url;

	public RecordEndpoint(@Context UriInfo uriInfo) {
		this.uriInfo = uriInfo;
		url = getBaseURLFromURI();
	}

	private String getBaseURLFromURI() {
		String baseURI = uriInfo.getBaseUri().toString();
		return baseURI + "record/";
	}

	@POST
	@Path("{type}")
	@Consumes("application/uub+record+json")
	@Produces("application/uub+record+json")
	public Response createRecord(@PathParam("type") String type, String jsonRecord) {
		// set user directly here until we have decided how to authenticate user
		return createRecordAsUserIdWithRecord(USER_ID, type, jsonRecord);
	}

	public Response createRecordAsUserIdWithRecord(String userId, String type, String jsonRecord) {
		try {
			return tryCreateRecord(userId, type, jsonRecord);
		} catch (Exception error) {
			return handleError(error);
		}
	}

	private Response tryCreateRecord(String userId, String type, String jsonRecord)
			throws URISyntaxException {
		SpiderDataGroup record = convertJsonStringToSpiderDataGroup(jsonRecord);
		SpiderDataRecord createdRecord = SpiderInstanceProvider.getSpiderRecordCreator()
				.createAndStoreRecord(userId, type, record);

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

	private Response handleError(Exception error) {
		Response response = buildResponse(Status.INTERNAL_SERVER_ERROR);

		if (error instanceof RecordConflictException) {
			response = buildResponseIncludingMessage(error, Response.Status.CONFLICT);
		} else if (error instanceof MisuseException) {
			response = buildResponseIncludingMessage(error, Response.Status.METHOD_NOT_ALLOWED);
		} else if (error instanceof JsonParseException || error instanceof DataException
				|| error instanceof ConverterException) {
			response = buildResponseIncludingMessage(error, Response.Status.BAD_REQUEST);
		} else if (error instanceof URISyntaxException) {
			response = buildResponse(Response.Status.BAD_REQUEST);
		} else if (error instanceof AuthorizationException) {
			response = buildResponse(Response.Status.UNAUTHORIZED);
		} else if (error instanceof RecordNotFoundException) {
			response = buildResponseIncludingMessage(error, Response.Status.NOT_FOUND);
		}
		return response;
	}

	private Response buildResponseIncludingMessage(Exception error, Status status) {
		return Response.status(status).entity(error.getMessage()).build();
	}

	private Response buildResponse(Status status) {
		return Response.status(status).build();
	}

	@GET
	@Path("{type}/")
	@Produces("application/uub+recordList+json")
	public Response readRecordList(@PathParam("type") String type) {
		return readRecordListAsUserIdByType(USER_ID, type);
	}

	Response readRecordListAsUserIdByType(String userId, String type) {
		try {
			return tryReadRecordList(userId, type);
		} catch (RecordNotFoundException e) {
			return Response.status(Response.Status.NOT_FOUND).build();
		} catch (AuthorizationException e) {
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}
	}

	private Response tryReadRecordList(String userId, String type) {
		SpiderDataList readRecordList = SpiderInstanceProvider.getSpiderRecordListReader()
				.readRecordList(userId, type);
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
	@Produces("application/uub+record+json")
	public Response readRecord(@PathParam("type") String type, @PathParam("id") String id) {
		// set user directly here until we have decided how to authenticate user
		return readRecordAsUserIdByTypeAndId(USER_ID, type, id);
	}

	Response readRecordAsUserIdByTypeAndId(String userId, String type, String id) {
		try {
			return tryReadRecord(userId, type, id);
		} catch (MisuseException e) {
			return Response.status(Response.Status.METHOD_NOT_ALLOWED).entity(e.getMessage())
					.build();
		} catch (RecordNotFoundException e) {
			return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
		} catch (AuthorizationException e) {
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}
	}

	private Response tryReadRecord(String userId, String type, String id) {
		SpiderDataRecord record = SpiderInstanceProvider.getSpiderRecordReader().readRecord(userId,
				type, id);
		String json = convertSpiderDataRecordToJsonString(record);
		return Response.status(Response.Status.OK).entity(json).build();
	}

	@GET
	@Path("{type}/{id}/incomingLinks")
	@Produces("application/uub+recordList+json")
	public Response readIncomingRecordLinks(@PathParam("type") String type,
			@PathParam("id") String id) {
		// set user directly here until we have decided how to authenticate user
		return readIncomingRecordLinksAsUserIdByTypeAndId(USER_ID, type, id);
	}

	Response readIncomingRecordLinksAsUserIdByTypeAndId(String userId, String type, String id) {
		try {
			return tryReadIncomingRecordLinks(userId, type, id);
		} catch (MisuseException e) {
			return Response.status(Response.Status.METHOD_NOT_ALLOWED).entity(e.getMessage())
					.build();
		} catch (RecordNotFoundException e) {
			return Response.status(Response.Status.NOT_FOUND).build();
		} catch (AuthorizationException e) {
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}
	}

	private Response tryReadIncomingRecordLinks(String userId, String type, String id) {
		SpiderDataList dataList = SpiderInstanceProvider.getSpiderRecordReader()
				.readIncomingLinks(userId, type, id);
		String json = convertSpiderRecordListToJsonString(dataList);
		return Response.status(Response.Status.OK).entity(json).build();
	}

	@DELETE
	@Path("{type}/{id}")
	public Response deleteRecord(@PathParam("type") String type, @PathParam("id") String id) {
		// set user directly here until we have decided how to authenticate user
		return deleteRecordAsUserIdByTypeAndId(USER_ID, type, id);
	}

	public Response deleteRecordAsUserIdByTypeAndId(String userId, String type, String id) {
		try {
			return tryDeleteRecord(userId, type, id);
		} catch (MisuseException e) {
			return Response.status(Response.Status.METHOD_NOT_ALLOWED).entity(e.getMessage())
					.build();
		} catch (RecordNotFoundException e) {
			return Response.status(Response.Status.NOT_FOUND).build();
		} catch (AuthorizationException e) {
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}
	}

	private Response tryDeleteRecord(String userId, String type, String id) {
		SpiderInstanceProvider.getSpiderRecordDeleter().deleteRecord(userId, type, id);
		return Response.status(Response.Status.OK).build();
	}

	@POST
	@Path("{type}/{id}")
	@Consumes("application/uub+record+json")
	@Produces("application/uub+record+json")
	public Response updateRecord(@PathParam("type") String type, @PathParam("id") String id,
			String jsonRecord) {
		return updateRecordAsUserIdWithRecord(USER_ID, type, id, jsonRecord);
	}

	public Response updateRecordAsUserIdWithRecord(String userId, String type, String id,
			String jsonRecord) {
		try {
			return tryUpdateRecord(userId, type, id, jsonRecord);
		} catch (MisuseException e) {
			return Response.status(Response.Status.METHOD_NOT_ALLOWED).entity(e.getMessage())
					.build();
		} catch (JsonParseException | DataException | DataMissingException | ConverterException e) {
			return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		} catch (RecordNotFoundException e) {
			return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
		} catch (AuthorizationException e) {
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}
	}

	private Response tryUpdateRecord(String userId, String type, String id, String jsonRecord) {
		SpiderDataGroup record = convertJsonStringToSpiderDataGroup(jsonRecord);
		SpiderDataRecord updatedRecord = SpiderInstanceProvider.getSpiderRecordUpdater()
				.updateRecord(userId, type, id, record);
		String json = convertSpiderDataRecordToJsonString(updatedRecord);
		return Response.status(Response.Status.OK).entity(json).build();
	}

}
