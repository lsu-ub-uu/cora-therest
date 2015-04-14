package epc.therest.record;

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
import javax.ws.rs.core.UriInfo;

import epc.spider.data.SpiderDataGroup;
import epc.spider.record.AuthorizationException;
import epc.spider.record.storage.RecordNotFoundException;
import epc.systemone.record.SystemOneRecordHandler;
import epc.systemone.record.SystemOneRecordHandlerImp;
import epc.therest.data.RestDataElement;
import epc.therest.data.RestDataGroup;
import epc.therest.data.converter.DataGroupToJsonConverter;
import epc.therest.data.converter.DataToJsonConverter;
import epc.therest.data.converter.JsonToDataConverter;
import epc.therest.data.converter.JsonToDataConverterFactory;
import epc.therest.data.converter.JsonToDataConverterFactoryImp;
import epc.therest.data.converter.spider.DataGroupRestToSpiderConverter;
import epc.therest.data.converter.spider.DataGroupSpiderToRestConverter;
import epc.therest.json.builder.JsonBuilderFactory;
import epc.therest.json.builder.org.OrgJsonBuilderFactoryAdapter;
import epc.therest.json.parser.JsonParseException;
import epc.therest.json.parser.JsonParser;
import epc.therest.json.parser.JsonValue;
import epc.therest.json.parser.org.OrgJsonParser;

@Path("record")
public class RecordEndpoint {

	private SystemOneRecordHandler recordHandler = new SystemOneRecordHandlerImp();
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
		String userId = "userId";
		return createRecordAsUserIdWithRecord(userId, type, jsonRecord);
	}

	public Response createRecordAsUserIdWithRecord(String userId, String type, String jsonRecord) {
		try {
			return tryCreateRecord(userId, type, jsonRecord);
		} catch (JsonParseException e) {
			return Response.status(Response.Status.BAD_REQUEST).build();
		} catch (AuthorizationException e) {
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}
	}

	private Response tryCreateRecord(String userId, String type, String jsonRecord) {
		SpiderDataGroup record = convertJsonStringToSpiderDataGroup(jsonRecord);
		SpiderDataGroup createdRecord = recordHandler.createRecord(userId, type, record);

		SpiderDataGroup recordInfo = createdRecord.extractGroup("recordInfo");
		String createdId = recordInfo.extractAtomicValue("id");

		String json = convertSpiderDataGroupToJsonString(createdRecord);

		URI uri = null;
		try {
			uri = new URI("record/" + type + "/" + createdId);
		} catch (URISyntaxException e) {
			return Response.status(Response.Status.BAD_REQUEST).build();
		}
		return Response.created(uri).entity(json).build();
	}

	@GET
	@Path("{type}/{id}")
	@Produces("application/uub+record+json")
	public Response readRecord(@PathParam("type") String type, @PathParam("id") String id) {
		// set user directly here until we have decided how to authenticate user
		String userId = "userId";
		return readRecordAsUserIdByTypeAndId(userId, type, id);
	}

	Response readRecordAsUserIdByTypeAndId(String userId, String type, String id) {
		try {
			return tryReadRecord(userId, type, id);
		} catch (RecordNotFoundException e) {
			return Response.status(Response.Status.NOT_FOUND).build();
		} catch (AuthorizationException e) {
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}
	}

	private Response tryReadRecord(String userId, String type, String id) {
		SpiderDataGroup record = recordHandler.readRecord(userId, type, id);
		String json = convertSpiderDataGroupToJsonString(record);
		return Response.status(Response.Status.OK).entity(json).build();
	}

	@DELETE
	@Path("{type}/{id}")
	public Response deleteRecord(@PathParam("type") String type, @PathParam("id") String id) {
		// set user directly here until we have decided how to authenticate user
		String userId = "userId";
		return deleteRecordAsUserIdByTypeAndId(userId, type, id);
	}

	public Response deleteRecordAsUserIdByTypeAndId(String userId, String type, String id) {
		try {
			return tryDeleteRecord(userId, type, id);
		} catch (RecordNotFoundException e) {
			return Response.status(Response.Status.NOT_FOUND).build();
		} catch (AuthorizationException e) {
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}
	}

	private Response tryDeleteRecord(String userId, String type, String id) {
		recordHandler.deleteRecord(userId, type, id);
		return Response.status(Response.Status.OK).build();
	}

	@POST
	@Path("{type}/{id}")
	@Consumes("application/uub+record+json")
	@Produces("application/uub+record+json")
	public Response updateRecord(@PathParam("type") String type, @PathParam("id") String id,
			String jsonRecord) {
		String userId = "userId";
		return updateRecordAsUserIdWithRecord(userId, type, id, jsonRecord);
	}

	public Response updateRecordAsUserIdWithRecord(String userId, String type, String id,
			String jsonRecord) {
		try {
			return tryUpdateRecord(userId, type, id, jsonRecord);
		} catch (JsonParseException e) {
			return Response.status(Response.Status.BAD_REQUEST).build();
		} catch (RecordNotFoundException e) {
			return Response.status(Response.Status.NOT_FOUND).build();
		} catch (AuthorizationException e) {
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}
	}

	private Response tryUpdateRecord(String userId, String type, String id, String jsonRecord) {
		SpiderDataGroup record = convertJsonStringToSpiderDataGroup(jsonRecord);
		SpiderDataGroup updatedRecord = recordHandler.updateRecord(userId, type, id, record);
		String json = convertSpiderDataGroupToJsonString(updatedRecord);
		return Response.status(Response.Status.OK).entity(json).build();
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

	private String convertSpiderDataGroupToJsonString(SpiderDataGroup record) {
		RestDataGroup restDataGroup = convertSpiderDataGroupToRestDataGroup(record);
		DataToJsonConverter dataToJsonConverter = convertRestDataGroupToJson(restDataGroup);
		return dataToJsonConverter.toJson();
	}

	private RestDataGroup convertSpiderDataGroupToRestDataGroup(SpiderDataGroup record) {

		DataGroupSpiderToRestConverter converter = DataGroupSpiderToRestConverter
				.fromSpiderDataGroupWithBaseURL(record, url);
		return converter.toRest();
	}

	private DataToJsonConverter convertRestDataGroupToJson(RestDataGroup restDataGroup) {
		JsonBuilderFactory jsonBuilderFactory = new OrgJsonBuilderFactoryAdapter();
		return DataGroupToJsonConverter.forRestDataGroup(jsonBuilderFactory, restDataGroup);
	}
}
