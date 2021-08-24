/*
 * Copyright 2016 Uppsala University Library
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

package se.uu.ub.cora.therest.record;

import se.uu.ub.cora.data.DataList;
import se.uu.ub.cora.spider.authorization.AuthorizationException;
import se.uu.ub.cora.spider.record.IncomingLinksReader;
import se.uu.ub.cora.spider.record.MisuseException;
import se.uu.ub.cora.storage.RecordNotFoundException;
import se.uu.ub.cora.testutils.mcr.MethodCallRecorder;
import se.uu.ub.cora.therest.coradata.DataListSpy;

public class SpiderRecordIncomingLinksReaderSpy implements IncomingLinksReader {
	MethodCallRecorder MCR = new MethodCallRecorder();

	public String authToken;
	public String type;
	public String id;

	@Override
	public DataList readIncomingLinks(String authToken, String type, String id) {
		MCR.addCall("authToken", authToken, "type", type, "id", id);
		this.authToken = authToken;
		this.type = type;
		this.id = id;
		possiblyThrowExceptionForIncomingLinks(authToken, type, id);
		DataListSpy dataListSpy = new DataListSpy("someType");
		MCR.addReturned(dataListSpy);
		return dataListSpy;
	}

	private void possiblyThrowExceptionForIncomingLinks(String authToken, String type, String id) {
		if ("dummyNonAuthorizedToken".equals(authToken)) {
			throw new AuthorizationException("not authorized");
		}
		if ("place:0001_NOT_FOUND".equals(id)) {
			throw new RecordNotFoundException("no record exsist with id " + id);
		}
		if ("abstract".equals(type)) {
			throw new MisuseException("Reading for record: " + id + " on the abstract recordType:"
					+ type + " is not allowed");
		}
	}

}
