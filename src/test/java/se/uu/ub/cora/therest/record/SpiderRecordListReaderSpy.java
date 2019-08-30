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
import se.uu.ub.cora.spider.data.SpiderDataList;
import se.uu.ub.cora.spider.record.SpiderRecordListReader;
import se.uu.ub.cora.storage.RecordNotFoundException;

public class SpiderRecordListReaderSpy implements SpiderRecordListReader {

	public String authToken;
	public String type;
	public SpiderDataGroup filter;

	@Override
	public SpiderDataList readRecordList(String authToken, String type, SpiderDataGroup filter) {
		this.authToken = authToken;
		this.type = type;
		this.filter = filter;
		possiblyThrowException(authToken, type);
		return SpiderDataList.withContainDataOfType(type);
	}

	private void possiblyThrowException(String authToken, String type) {
		if ("place_NOT_FOUND".equals(type)) {
			throw new RecordNotFoundException("Record not found");
		}
		if ("dummyNonAuthorizedToken".equals(authToken) || authToken == null) {
			throw new AuthorizationException("not authorized");
		}
	}

}
