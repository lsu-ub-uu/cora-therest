/*
 * Copyright 2024 Uppsala University Library
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
package se.uu.ub.cora.therest.spy;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import jakarta.ws.rs.core.Cookie;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;
import se.uu.ub.cora.testutils.mcr.MethodCallRecorder;
import se.uu.ub.cora.testutils.mrv.MethodReturnValues;

public class HttpHeadersSpy implements HttpHeaders {

	public MethodCallRecorder MCR = new MethodCallRecorder();
	public MethodReturnValues MRV = new MethodReturnValues();

	public HttpHeadersSpy() {
		MCR.useMRV(MRV);
		MRV.setDefaultReturnValuesSupplier("getRequestHeader", () -> null);
		MRV.setDefaultReturnValuesSupplier("getRequestHeaders", () -> new MultivaluedHashMap<>());
	}

	@Override
	public List<String> getRequestHeader(String name) {
		return (List<String>) MCR.addCallAndReturnFromMRV("name", name);
	}

	@Override
	public String getHeaderString(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MultivaluedMap<String, String> getRequestHeaders() {
		return (MultivaluedMap<String, String>) MCR.addCallAndReturnFromMRV();
	}

	@Override
	public List<MediaType> getAcceptableMediaTypes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Locale> getAcceptableLanguages() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MediaType getMediaType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Locale getLanguage() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Cookie> getCookies() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Date getDate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getLength() {
		// TODO Auto-generated method stub
		return 0;
	}

}
