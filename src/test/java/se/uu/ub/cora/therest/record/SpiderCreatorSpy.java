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
import se.uu.ub.cora.spider.record.DataException;
import se.uu.ub.cora.spider.record.MisuseException;
import se.uu.ub.cora.spider.record.SpiderRecordCreator;
import se.uu.ub.cora.spider.record.storage.RecordConflictException;
import se.uu.ub.cora.spider.record.storage.RecordNotFoundException;
import se.uu.ub.cora.therest.testdata.DataCreator;

public class SpiderCreatorSpy implements SpiderRecordCreator {

	public String authToken;
	public String type;
	public SpiderDataGroup record;

	@Override
	public SpiderDataRecord createAndStoreRecord(String authToken, String type,
			SpiderDataGroup record) {
		this.authToken = authToken;
		this.type = type;
		this.record = record;
		if("dummyNonAuthorizedToken".equals(authToken)){
			throw new AuthorizationException("not authorized");
		}
		if("recordType_NON_EXISTING".equals(type)){
			throw new RecordNotFoundException("no record exist with type " + type);
		}else if("place_NON_VALID".equals(type)){
			throw new DataException("Data is not valid");
		}else if ("abstract".equals(type)) {
			throw new MisuseException(
					"Data creation on abstract recordType:" + type + " is not allowed");
		}else if("place_duplicate".equals(type)){
			throw new RecordConflictException("Record already exists");
		}else if("place_unexpected_error".equals(type)){
			throw new NullPointerException("Some error");
		}
		return SpiderDataRecord.withSpiderDataGroup(
				DataCreator.createRecordWithNameInDataAndIdAndTypeAndLinkedRecordId("nameInData",
						"someId", type, "linkedRecordId"));
	}

}
