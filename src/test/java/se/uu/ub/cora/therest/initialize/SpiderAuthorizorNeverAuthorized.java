/*
 * Copyright 2016, 2018 Uppsala University Library
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

package se.uu.ub.cora.therest.initialize;

import java.util.List;
import java.util.Set;

import se.uu.ub.cora.beefeater.authentication.User;
import se.uu.ub.cora.data.collected.PermissionTerm;
import se.uu.ub.cora.spider.authorization.AuthorizationException;
import se.uu.ub.cora.spider.authorization.SpiderAuthorizator;

public class SpiderAuthorizorNeverAuthorized implements SpiderAuthorizator {

	@Override
	public void checkUserIsAuthorizedForActionOnRecordType(User user, String action,
			String recordType) {
		throw new AuthorizationException("not authorized");
	}

	@Override
	public boolean userIsAuthorizedForActionOnRecordType(User user, String action,
			String recordType) {
		return false;
	}

	@Override
	public boolean userIsAuthorizedForActionOnRecordTypeAndCollectedData(User user, String action,
			String recordType, List<PermissionTerm> permissionTerms) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void checkUserIsAuthorizedForActionOnRecordTypeAndCollectedData(User user, String action,
			String recordType, List<PermissionTerm> permissionTerms) {
		// TODO Auto-generated method stub

	}

	@Override
	public Set<String> checkGetUsersMatchedRecordPartPermissionsForActionOnRecordTypeAndCollectedData(
			User user, String action, String recordType, List<PermissionTerm> permissionTerms,
			boolean calculateRecordPartPermissions) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void checkUserIsAuthorizedForPemissionUnit(User user, String recordPermissionUnit) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean getUserIsAuthorizedForPemissionUnit(User user, String string) {
		// TODO Auto-generated method stub
		return false;
	}

}
