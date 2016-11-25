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

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import se.uu.ub.cora.spider.data.SpiderInputStream;
import se.uu.ub.cora.spider.record.SpiderDownloader;

public class SpiderDownloaderSpy implements SpiderDownloader {

	public String authToken;
	public String type;
	public String id;
	public String resource;

	@Override
	public SpiderInputStream download(String authToken, String type, String id, String resource) {
		this.authToken = authToken;
		this.type = type;
		this.id = id;
		this.resource = resource;
		return SpiderInputStream.withNameSizeInputStream("someFile", 12, "application/octet-stream",
				new ByteArrayInputStream("a string out".getBytes(StandardCharsets.UTF_8)));
	}

}
