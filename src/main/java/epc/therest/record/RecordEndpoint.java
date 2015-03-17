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
import epc.therest.json.DataGroupToJsonConverter;
import epc.therest.json.DataToJsonConverter;

@Path("record")
public class RecordEndpoint {

	@GET
	@Path("001")
	// @Produces(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)
	public String createRecord() {
		SystemOneRecordHandler recordHandler = new SystemOneRecordHandlerImp();

		// this is temporary, it should take a record in containing this info
		SpiderDataGroup record = SpiderDataGroup.withDataId("authority");
		record.addAttributeByIdWithValue("type", "place");
		SpiderDataGroup recordOut = recordHandler.createRecord("userId", "place", record);

		RestDataGroup restDataGroup = RestDataGroup.fromDataGroup(recordOut);
		DataToJsonConverter dataToJsonConverter = DataGroupToJsonConverter.forRestDataGroup(restDataGroup);

		return dataToJsonConverter.toJson();

	}

	@GET
	@Path("{type}/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public String readRecord(@PathParam("type") String type, @PathParam("id") String id) {
		SystemOneRecordHandler recordHandler = new SystemOneRecordHandlerImp();
		SpiderDataGroup record = recordHandler.readRecord("userId", type, id);
		RestDataGroup restDataGroup = RestDataGroup.fromDataGroup(record);
		DataToJsonConverter dataToJsonConverter = DataGroupToJsonConverter.forRestDataGroup(restDataGroup);

		return dataToJsonConverter.toJson();
	}
}
