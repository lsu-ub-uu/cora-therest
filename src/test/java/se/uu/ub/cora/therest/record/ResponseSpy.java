/*
 * Copyright 2025 Uppsala University Library
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

import java.lang.annotation.Annotation;
import java.net.URI;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import jakarta.ws.rs.core.EntityTag;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.Link;
import jakarta.ws.rs.core.Link.Builder;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;

public class ResponseSpy extends Response {

	@Override
	public int getStatus() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public StatusType getStatusInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getEntity() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T readEntity(Class<T> entityType) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T readEntity(GenericType<T> entityType) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T readEntity(Class<T> entityType, Annotation[] annotations) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T readEntity(GenericType<T> entityType, Annotation[] annotations) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasEntity() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean bufferEntity() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub

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
	public int getLength() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Set<String> getAllowedMethods() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, NewCookie> getCookies() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EntityTag getEntityTag() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Date getDate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Date getLastModified() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public URI getLocation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Link> getLinks() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasLink(String relation) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Link getLink(String relation) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Builder getLinkBuilder(String relation) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MultivaluedMap<String, Object> getMetadata() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MultivaluedMap<String, String> getStringHeaders() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getHeaderString(String name) {
		// TODO Auto-generated method stub
		return null;
	}

}
