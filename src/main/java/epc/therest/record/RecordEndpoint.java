package epc.therest.record;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import epc.metadataformat.data.DataAtomic;
import epc.metadataformat.data.DataGroup;
import epc.systemone.record.SystemOneRecordHandler;
import epc.systemone.record.SystemOneRecordInputBoundary;

@Path("record")
public class RecordEndpoint {

	@GET
	@Path("001")
	@Produces(MediaType.TEXT_PLAIN)
	public String createRecord() {
		SystemOneRecordInputBoundary inputBoundary = new SystemOneRecordHandler();
		DataGroup record = new DataGroup("authority");
		DataGroup recordOut = inputBoundary.createRecord("userId", "type", record);

		DataGroup recordInfo = (DataGroup) recordOut.getChildren().stream()
				.filter(p -> p.getDataId().equals("recordInfo")).findFirst()
				.get();
		DataAtomic recordId = (DataAtomic) recordInfo.getChildren().stream()
				.filter(p -> p.getDataId().equals("id")).findFirst().get();
//		return "Hellllo woo0orld, recordInfo:" + recordInfo.getDataId()
//				+ " recordId:" + recordId.getValue();
		return recordId.getValue();
	}

}
