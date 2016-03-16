/*
 * Copyright 2015 Uppsala University Library
 *
 * This file is part of Cora.
 *
 *     Cora is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Cora is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Cora.  If not, see <http://www.gnu.org/licenses/>.
 */

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
	private String createdId;

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

	public String getCreatedId() {
		return createdId;
	}

	public String resetDependencyProvider() {
		DependencyProviderForMultipleTestsWorkingTogether.spiderDependencyProvider = new SystemOneDependencyProvider();
		return "OK";
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

	public String testReadIncomingLinks() {
		UriInfo uriInfo = new TestUri();
		SpiderInstanceProvider.setSpiderDependencyProvider(
				DependencyProviderForMultipleTestsWorkingTogether.spiderDependencyProvider);
		RecordEndpoint recordEndpoint = new RecordEndpoint(uriInfo);
		Response response = recordEndpoint.readIncomingRecordLinks(type, id);
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
		statusType = response.getStatusInfo();
		String entity = (String) response.getEntity();
		createdId = tryToFindCreatedId(entity);
		return entity;
	}

	private String tryToFindCreatedId(String entity) {
		try {
			return findCreatedId(entity);
		} catch (Exception e) {
			return "";
		}
	}

	private String findCreatedId(String entity) {
		return entity.substring(entity.lastIndexOf("/") + 1, entity.lastIndexOf("\""));
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
		if (null == response.getEntity()) {
			return "";
		}
		return (String) response.getEntity();
	}

}
