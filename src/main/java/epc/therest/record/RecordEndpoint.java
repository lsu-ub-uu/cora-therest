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
import epc.spider.data.SpiderDataRecord;
import epc.spider.data.SpiderRecordList;
import epc.spider.dependency.SpiderInstanceProvider;
import epc.spider.record.AuthorizationException;
import epc.spider.record.DataException;
import epc.spider.record.storage.RecordNotFoundException;
import epc.therest.data.RestDataElement;
import epc.therest.data.RestDataGroup;
import epc.therest.data.RestDataRecord;
import epc.therest.data.RestRecordList;
import epc.therest.data.converter.DataRecordToJsonConterter;
import epc.therest.data.converter.JsonToDataConverter;
import epc.therest.data.converter.JsonToDataConverterFactory;
import epc.therest.data.converter.JsonToDataConverterFactoryImp;
import epc.therest.data.converter.RecordListToJsonConverter;
import epc.therest.data.converter.spider.DataGroupRestToSpiderConverter;
import epc.therest.data.converter.spider.DataRecordSpiderToRestConverter;
import epc.therest.data.converter.spider.RecordListSpiderToRestConverter;
import epc.therest.json.builder.JsonBuilderFactory;
import epc.therest.json.builder.org.OrgJsonBuilderFactoryAdapter;
import epc.therest.json.parser.JsonParseException;
import epc.therest.json.parser.JsonParser;
import epc.therest.json.parser.JsonValue;
import epc.therest.json.parser.org.OrgJsonParser;

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
		} catch (JsonParseException | DataException e) {
			return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		} catch (AuthorizationException e) {
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}
	}

	private Response tryCreateRecord(String userId, String type, String jsonRecord) {
		SpiderDataGroup record = convertJsonStringToSpiderDataGroup(jsonRecord);
		SpiderDataRecord createdRecord = SpiderInstanceProvider.getSpiderRecordCreator()
				.createAndStoreRecord(userId, type, record);

		SpiderDataGroup createdGroup = createdRecord.getSpiderDataGroup();
		SpiderDataGroup recordInfo = createdGroup.extractGroup("recordInfo");
		String createdId = recordInfo.extractAtomicValue("id");

		String json = convertSpiderDataRecordToJsonString(createdRecord);

		URI uri = null;
		try {
			uri = new URI("record/" + type + "/" + createdId);
		} catch (URISyntaxException e) {
			return Response.status(Response.Status.BAD_REQUEST).build();
		}
		return Response.created(uri).entity(json).build();
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
		SpiderRecordList readRecordList = SpiderInstanceProvider.getSpiderRecordReader()
				.readRecordList(userId, type);
		String json = convertSpiderRecordListToJsonString(readRecordList);
		return Response.status(Response.Status.OK).entity(json).build();
	}

	private String convertSpiderRecordListToJsonString(SpiderRecordList readRecordList) {
		RecordListSpiderToRestConverter listSpiderToRestConverter = RecordListSpiderToRestConverter
				.fromSpiderRecordListWithBaseURL(readRecordList, url);
		RestRecordList restRecordList = listSpiderToRestConverter.toRest();

		JsonBuilderFactory jsonBuilderFactory = new OrgJsonBuilderFactoryAdapter();
		RecordListToJsonConverter recordListToJsonConverter = new RecordListToJsonConverter(
				jsonBuilderFactory, restRecordList);
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
		} catch (RecordNotFoundException e) {
			return Response.status(Response.Status.NOT_FOUND).build();
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

	@DELETE
	@Path("{type}/{id}")
	public Response deleteRecord(@PathParam("type") String type, @PathParam("id") String id) {
		// set user directly here until we have decided how to authenticate user
		return deleteRecordAsUserIdByTypeAndId(USER_ID, type, id);
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
		} catch (JsonParseException | DataException e) {
			return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		} catch (RecordNotFoundException e) {
			return Response.status(Response.Status.NOT_FOUND).build();
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
		DataRecordToJsonConterter dataToJsonConverter = convertRestDataGroupToJson(restDataRecord);
		return dataToJsonConverter.toJson();
	}

	private RestDataRecord convertSpiderDataRecordToRestDataRecord(SpiderDataRecord record) {

		DataRecordSpiderToRestConverter converter = DataRecordSpiderToRestConverter
				.fromSpiderDataRecordWithBaseURL(record, url);
		return converter.toRest();
	}

	private DataRecordToJsonConterter convertRestDataGroupToJson(RestDataRecord restDataRecord) {
		JsonBuilderFactory jsonBuilderFactory = new OrgJsonBuilderFactoryAdapter();
		return DataRecordToJsonConterter.usingJsonFactoryForRestDataRecord(jsonBuilderFactory,
				restDataRecord);
	}

}
