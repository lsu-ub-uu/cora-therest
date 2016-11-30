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

import se.uu.ub.cora.beefeater.authentication.User;
import se.uu.ub.cora.spider.authentication.AuthenticationException;
import se.uu.ub.cora.spider.authentication.Authenticator;

public class AuthenticatorForTest implements Authenticator {

	@Override
	public User getUserForToken(String authToken) {
		if ("dummyNonAuthenticatedToken".equals(authToken)) {
			throw new AuthenticationException("token not valid");
		}
		if ("dummyNonAuthorizedToken".equals(authToken)) {
			User user = new User("99999");
			user.loginId = "unauthorizedUserId";
			user.loginDomain = "system";
			return user;
		}
		User user = new User("12345");
		user.loginId = "knownUser";
		user.loginDomain = "system";
		user.roles.add("guest");
		return user;
	}

}
