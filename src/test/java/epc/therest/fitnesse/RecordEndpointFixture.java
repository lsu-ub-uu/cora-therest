package epc.therest.fitnesse;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.StatusType;
import javax.ws.rs.core.UriInfo;

import epc.spider.dependency.SpiderInstanceProvider;
import epc.systemone.SystemOneDependencyProvider;
import epc.therest.record.RecordEndpoint;
import epc.therest.record.TestUri;

public class RecordEndpointFixture {
	private String id;
	private String type;
	private StatusType statusType;

	public void setType(String type) {
		this.type = type;
	}

	public void setId(String id) {
		this.id = id;
	}

	public StatusType getStatusType() {
		return statusType;
	}

	public String testReadRecord() {
		UriInfo uriInfo = new TestUri();
		SpiderInstanceProvider.setSpiderDependencyProvider(new SystemOneDependencyProvider());
		RecordEndpoint recordEndpoint = new RecordEndpoint(uriInfo);
		Response response = recordEndpoint.readRecord(type, id);
		String entity = (String) response.getEntity();
		statusType = response.getStatusInfo();
		return entity;
	}

}
