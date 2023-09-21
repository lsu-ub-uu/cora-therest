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
import se.uu.ub.cora.spider.record.MisuseException;
import se.uu.ub.cora.spider.record.RecordDeleter;
import se.uu.ub.cora.storage.RecordNotFoundException;

public class SpiderRecordDeleterSpy implements RecordDeleter {

	public String authToken;
	public String type;
	public String id;

	@Override
	public void deleteRecord(String authToken, String type, String id) {
		this.authToken = authToken;
		this.type = type;
		this.id = id;
		possiblyThrowException(authToken, id);
	}

	private void possiblyThrowException(String authToken, String id) {
		if ("dummyNonAuthorizedToken".equals(authToken)) {
			throw new AuthorizationException("not authorized");
		}
		if ("place:0001".equals(id)) {
			throw new MisuseException("Deleting record: " + id
					+ " is not allowed since other records are linking to it");
		} else if ("place:0001_NOT_FOUND".equals(id)) {
			throw RecordNotFoundException.withMessage("no record exist with id " + id);
		}
	}

}
