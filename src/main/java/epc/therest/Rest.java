package epc.therest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import epc.metadataformat.data.DataAtomic;
import epc.metadataformat.data.DataGroup;
import epc.systemone.record.SystemOneRecordHandler;
import epc.systemone.record.SystemOneRecordInputBoundary;

@Path("greetings")
public class Rest {
	@GET
	@Path("hello")
	@Produces(MediaType.TEXT_PLAIN)
	public String greet(){
		SystemOneRecordInputBoundary input= new SystemOneRecordHandler();
		DataGroup record = new DataGroup("authority");
		DataGroup recordOut = input.createRecord("userId", "type", record);
		
		DataGroup recordInfo = (DataGroup) recordOut.getChildren().stream()
				.filter(p -> p.getDataId().equals("recordInfo")).findFirst()
				.get();
		DataAtomic recordId = (DataAtomic) recordInfo.getChildren().stream()
				.filter(p -> p.getDataId().equals("id")).findFirst().get();
		return "Hellllo woo0orld, recordInfo:"+recordInfo.getDataId()+" recordId:"+recordId.getValue();
	}
}
