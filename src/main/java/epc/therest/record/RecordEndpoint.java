package epc.therest.record;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

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
import epc.therest.json.parser.JsonParser;
import epc.therest.json.parser.JsonValue;
import epc.therest.json.parser.org.OrgJsonParser;

@Path("record")
public class RecordEndpoint {

	private SystemOneRecordHandler recordHandler = new SystemOneRecordHandlerImp();

	@GET
	@Path("001")
	// @Consumes
	@Produces(MediaType.APPLICATION_JSON)
	public String createRecord() {
		// this is temporary, it should take a record in containing this info
		SpiderDataGroup record = SpiderDataGroup.withDataId("authority");
		record.addAttributeByIdWithValue("type", "place");
		SpiderDataGroup createdRecord = recordHandler.createRecord("userId", "place", record);

		return convertRecord(createdRecord);
	}

	private String convertRecord(SpiderDataGroup record) {
		RestDataGroup restDataGroup = convertToRest(record);

		DataToJsonConverter dataToJsonConverter = convertToJson(restDataGroup);
		return dataToJsonConverter.toJson();
	}

	private RestDataGroup convertToRest(SpiderDataGroup record) {
		DataGroupSpiderToRestConverter converter = DataGroupSpiderToRestConverter
				.fromSpiderDataGroup(record);
		return converter.toRest();
	}

	private DataToJsonConverter convertToJson(RestDataGroup restDataGroup) {
		JsonBuilderFactory jsonBuilderFactory = new OrgJsonBuilderFactoryAdapter();
		return DataGroupToJsonConverter.forRestDataGroup(
				jsonBuilderFactory, restDataGroup);
	}

	@GET
	@Path("{type}/{id}")
	@Produces("application/uub+record+json")
	public Response readRecord(@PathParam("type") String type, @PathParam("id") String id) {
		// @QueryParam
		// set user directly here until we have decided how to authenticate user
		String userId = "userId";
		// String userId = "unauthorizedUserId";
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
		String json = convertRecord(record);
		return Response.status(Response.Status.OK).entity(json).build();
	}

	@DELETE
	@Path("{type}/{id}")
	public Response deleteRecord(@PathParam("type") String type, @PathParam("id") String id) {
		// set user directly here until we have decided how to authenticate user
		String userId = "userId";
		// String userId = "unauthorizedUserId";
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
		// String userId = "unauthorizedUserId";

		return updateRecordAsUserIdWithRecord(userId, type, id, jsonRecord);
	}

	public Response updateRecordAsUserIdWithRecord(String userId, String type, String id,
			String jsonRecord) {
		try {
			return tryUpdateRecord(userId, type, id, jsonRecord);
		} catch (RecordNotFoundException e) {
			return Response.status(Response.Status.NOT_FOUND).build();
		} catch (AuthorizationException e) {
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}
	}

	private Response tryUpdateRecord(String userId, String type, String id, String jsonRecord) {
		JsonParser jsonParser = new OrgJsonParser();
		JsonValue jsonValue = jsonParser.parseString(jsonRecord);
		JsonToDataConverterFactory jsonToDataConverterFactory = new JsonToDataConverterFactoryImp();
		JsonToDataConverter jsonToDataConverter = jsonToDataConverterFactory
				.createForJsonObject(jsonValue);
		RestDataElement restDataElement = null;
		try {
			restDataElement = jsonToDataConverter.toInstance();
		} catch (Exception e) {
			return Response.status(Response.Status.BAD_REQUEST).build();
		}

		RestDataGroup restDataGroup = (RestDataGroup) restDataElement;

		SpiderDataGroup record = DataGroupRestToSpiderConverter.fromRestDataGroup(restDataGroup)
				.toSpider();

		SpiderDataGroup updatedRecord = recordHandler.updateRecord(userId, type, id, record);

		String json = convertRecord(updatedRecord);
		return Response.status(Response.Status.OK).entity(json).build();
	}
}
