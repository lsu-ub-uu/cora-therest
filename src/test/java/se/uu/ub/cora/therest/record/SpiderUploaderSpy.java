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

import java.io.InputStream;

import se.uu.ub.cora.data.DataRecord;
import se.uu.ub.cora.spider.binary.Uploader;
import se.uu.ub.cora.testutils.mcr.MethodCallRecorder;

@Deprecated
public class SpiderUploaderSpy implements Uploader {

	MethodCallRecorder MCR = new MethodCallRecorder();

	public String authToken;
	public String type;
	public String id;
	public InputStream inputStream;
	public String fileName;

	@Override
	public DataRecord upload(String authToken, String type, String id, InputStream inputStream,
			String represntation) {
		return null;
	}
}
