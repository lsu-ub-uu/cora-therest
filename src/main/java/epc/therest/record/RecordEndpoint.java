package epc.therest.record;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import epc.spider.data.SpiderDataGroup;
import epc.systemone.record.SystemOneRecordHandler;
import epc.systemone.record.SystemOneRecordHandlerImp;
import epc.therest.data.RestDataGroup;
import epc.therest.data.converter.DataGroupToJsonConverter;
import epc.therest.data.converter.DataToJsonConverter;
import epc.therest.data.converter.spider.DataGroupSpiderToRestConverter;
import epc.therest.json.builder.JsonBuilderFactory;
import epc.therest.json.builder.org.OrgJsonBuilderFactoryAdapter;

@Path("record")
public class RecordEndpoint {

	private SystemOneRecordHandler recordHandler = new SystemOneRecordHandlerImp();

	@GET
	@Path("001")
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
		DataToJsonConverter dataToJsonConverter = DataGroupToJsonConverter.forRestDataGroup(
				jsonBuilderFactory, restDataGroup);
		return dataToJsonConverter;
	}

	@GET
	@Path("{type}/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public String readRecord(@PathParam("type") String type, @PathParam("id") String id) {
		// @QueryParam
		String userId = "userId";
		SpiderDataGroup record = recordHandler.readRecord(userId, type, id);
		return convertRecord(record);
	}

}
