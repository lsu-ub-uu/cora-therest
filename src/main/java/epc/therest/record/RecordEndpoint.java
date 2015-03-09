package epc.therest.record;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import epc.metadataformat.data.DataGroup;
import epc.systemone.record.SystemOneRecordHandlerImp;
import epc.systemone.record.SystemOneRecordHandler;
import epc.therest.data.DataGroupRest;

@Path("record")
public class RecordEndpoint {

	@GET
	@Path("001")
	// @Produces(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)
	public DataGroupRest createRecord() {
		SystemOneRecordHandler recordHandler = new SystemOneRecordHandlerImp();
		DataGroup record = DataGroup.withDataId("authority");
		DataGroup recordOut = recordHandler.createRecord("userId", "type", record);

		// DataGroup recordInfo = (DataGroup) recordOut.getChildren().stream()
		// .filter(p -> p.getDataId().equals("recordInfo")).findFirst()
		// .get();
		// DataAtomic recordId = (DataAtomic) recordInfo.getChildren().stream()
		// .filter(p -> p.getDataId().equals("id")).findFirst().get();
		// return recordId.getValue();

		// return new DataGroupRest(recordOut);

		DataGroupRest dataGroupRest = DataGroupRest.fromDataGroup(recordOut);
		return dataGroupRest;

	}

	@GET
	@Path("{type}/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	// public Response readRecord(@PathParam("type") String type,
	public DataGroupRest readRecord(@PathParam("type") String type,
	// public String readRecord(@PathParam("type") String type,
			@PathParam("id") String id) {
		SystemOneRecordHandler recordHandler = new SystemOneRecordHandlerImp();
		DataGroup record = recordHandler.readRecord("userId", type, id);
		return DataGroupRest.fromDataGroup(record);
		// return Response.status(Response.Status.OK).entity(record).build();
	}

	@GET
	@Path("002")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, String> readRecord() {
		Map<String, String> out = new HashMap<>();
		out.put("key", "value");
		return out;
	}

	@GET
	@Path("003")
	@Produces(MediaType.APPLICATION_JSON)
	public Dummy readRecordDummy() {
		Dummy dummy = new Dummy("ourDataId");
		return dummy;
	}

	@GET
	@Path("004")
	@Produces(MediaType.APPLICATION_JSON)
	public DataGroup readRecordDataGroup() {
		DataGroup dataGroup = DataGroup.withDataId("dataId");
		return dataGroup;
	}

}
