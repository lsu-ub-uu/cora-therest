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

package se.uu.ub.cora.therest.initialize;

import java.util.List;
import java.util.Map;
import java.util.Set;

import se.uu.ub.cora.beefeater.Authorizator;
import se.uu.ub.cora.beefeater.authentication.User;

public class AlwaysAuthorized implements Authorizator {

	@Override
	public boolean isAuthorized(User user, Set<String> recordCalculateKeys) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean providedRulesSatisfiesRequiredRules(List<Map<String, Set<String>>> providedRules,
			List<Map<String, Set<String>>> requiredRules) {
		// TODO Auto-generated method stub
		return true;
	}

}
