package se.uu.ub.cora.therest.fitnesse;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.StatusType;
import javax.ws.rs.core.UriInfo;

import se.uu.ub.cora.spider.dependency.SpiderInstanceProvider;
import se.uu.ub.cora.systemone.SystemOneDependencyProvider;
import se.uu.ub.cora.therest.record.RecordEndpoint;
import se.uu.ub.cora.therest.record.TestUri;

public class RecordEndpointFixture {
	private String id;
	private String type;
	private String json;
	private StatusType statusType;

	public void setType(String type) {
		this.type = type;
	}

	public void setJson(String json) {
		this.json = json;
	}

	public void setId(String id) {
		this.id = id;
	}

	public StatusType getStatusType() {
		return statusType;
	}

	public String testReadRecord() {
		UriInfo uriInfo = new TestUri();
		SpiderInstanceProvider.setSpiderDependencyProvider(
				DependencyProviderForMultipleTestsWorkingTogether.spiderDependencyProvider);
		RecordEndpoint recordEndpoint = new RecordEndpoint(uriInfo);
		Response response = recordEndpoint.readRecord(type, id);
		String entity = (String) response.getEntity();
		statusType = response.getStatusInfo();
		return entity;
	}

	public String testReadRecordList() {
		UriInfo uriInfo = new TestUri();
		SpiderInstanceProvider.setSpiderDependencyProvider(
				DependencyProviderForMultipleTestsWorkingTogether.spiderDependencyProvider);
		RecordEndpoint recordEndpoint = new RecordEndpoint(uriInfo);
		Response response = recordEndpoint.readRecordList(type);
		String entity = (String) response.getEntity();
		statusType = response.getStatusInfo();
		return entity;
	}

	public String testCreateRecord() {
		UriInfo uriInfo = new TestUri();
		SpiderInstanceProvider.setSpiderDependencyProvider(
				DependencyProviderForMultipleTestsWorkingTogether.spiderDependencyProvider);
		RecordEndpoint recordEndpoint = new RecordEndpoint(uriInfo);
		Response response = recordEndpoint.createRecord(type, json);
		String entity = (String) response.getEntity();
		statusType = response.getStatusInfo();
		return entity;
	}

	public String testUpdateRecord() {
		UriInfo uriInfo = new TestUri();
		SpiderInstanceProvider.setSpiderDependencyProvider(
				DependencyProviderForMultipleTestsWorkingTogether.spiderDependencyProvider);
		RecordEndpoint recordEndpoint = new RecordEndpoint(uriInfo);
		Response response = recordEndpoint.updateRecord(type, id, json);
		String entity = (String) response.getEntity();
		statusType = response.getStatusInfo();
		return entity;
	}

	public String testDeleteRecord() {
		UriInfo uriInfo = new TestUri();
		SpiderInstanceProvider.setSpiderDependencyProvider(
				DependencyProviderForMultipleTestsWorkingTogether.spiderDependencyProvider);
		RecordEndpoint recordEndpoint = new RecordEndpoint(uriInfo);
		Response response = recordEndpoint.deleteRecord(type, id);
		statusType = response.getStatusInfo();
		if(null==response.getEntity()){
			return "";
		}
		return (String) response.getEntity();
	}

}
