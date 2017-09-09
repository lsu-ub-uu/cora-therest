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

import se.uu.ub.cora.spider.authorization.AuthorizationException;
import se.uu.ub.cora.spider.data.SpiderDataGroup;
import se.uu.ub.cora.spider.data.SpiderDataRecord;
import se.uu.ub.cora.spider.record.SpiderRecordUpdater;
import se.uu.ub.cora.spider.record.storage.RecordNotFoundException;
import se.uu.ub.cora.therest.testdata.DataCreator;

public class SpiderRecordUpdaterSpy implements SpiderRecordUpdater {

	public String authToken;
	public String type;
	public String id;
	public SpiderDataGroup record;

	@Override
	public SpiderDataRecord updateRecord(String authToken, String type, String id,
			SpiderDataGroup record) {
		this.authToken = authToken;
		this.type = type;
		this.id = id;
		this.record = record;
		if("dummyNonAuthorizedToken".equals(authToken)){
			throw new AuthorizationException("not authorized");
		}
		if("place:0001_NOT_FOUND".equals(id)){
			throw new RecordNotFoundException("no record exist with id " + id);
		}
		if("place_NOT_FOUND".equals(type)){
			throw new RecordNotFoundException("no record exist with type " + type);
		}
		return SpiderDataRecord.withSpiderDataGroup(
				DataCreator.createRecordWithNameInDataAndIdAndTypeAndLinkedRecordId("nameInData",
						id, type, "linkedRecordId"));
	}

}
