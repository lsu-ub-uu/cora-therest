package epc.therest.record;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import epc.metadataformat.data.DataGroup;
import epc.systemone.record.SystemOneRecordHandler;
import epc.systemone.record.SystemOneRecordHandlerImp;
import epc.therest.data.RestDataGroup;
import epc.therest.json.DataGroupJsonCreator;
import epc.therest.json.JsonCreator;

@Path("record")
public class RecordEndpoint {

	@GET
	@Path("001")
	// @Produces(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)
	public String createRecord() {
		SystemOneRecordHandler recordHandler = new SystemOneRecordHandlerImp();

		// this is temporary, it should take a record in containing this info
		DataGroup record = DataGroup.withDataId("authority");
		record.addAttributeByIdWithValue("type", "place");
		DataGroup recordOut = recordHandler.createRecord("userId", "place", record);

		RestDataGroup restDataGroup = RestDataGroup.fromDataGroup(recordOut);
		JsonCreator jsonCreator = DataGroupJsonCreator.forRestDataGroup(restDataGroup);

		return jsonCreator.toJson();

	}

	@GET
	@Path("{type}/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public String readRecord(@PathParam("type") String type, @PathParam("id") String id) {
		SystemOneRecordHandler recordHandler = new SystemOneRecordHandlerImp();
		DataGroup record = recordHandler.readRecord("userId", type, id);
		RestDataGroup restDataGroup = RestDataGroup.fromDataGroup(record);
		JsonCreator jsonCreator = DataGroupJsonCreator.forRestDataGroup(restDataGroup);

		return jsonCreator.toJson();
	}
}
