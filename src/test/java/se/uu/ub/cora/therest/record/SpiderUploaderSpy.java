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

import se.uu.ub.cora.spider.authorization.AuthorizationException;
import se.uu.ub.cora.spider.data.DataMissingException;
import se.uu.ub.cora.spider.data.SpiderDataRecord;
import se.uu.ub.cora.spider.record.MisuseException;
import se.uu.ub.cora.spider.record.SpiderUploader;
import se.uu.ub.cora.spider.record.storage.RecordNotFoundException;
import se.uu.ub.cora.therest.testdata.DataCreator;

public class SpiderUploaderSpy implements SpiderUploader {

	public String authToken;
	public String type;
	public String id;
	public InputStream inputStream;
	public String fileName;

	@Override
	public SpiderDataRecord upload(String authToken, String type, String id,
			InputStream inputStream, String fileName) {
		this.authToken = authToken;
		this.type = type;
		this.id = id;
		this.inputStream = inputStream;
		this.fileName = fileName;

		possiblyThrowException(authToken, type, id, inputStream);
		return SpiderDataRecord.withSpiderDataGroup(
				DataCreator.createRecordWithNameInDataAndIdAndTypeAndLinkedRecordId("nameInData",
						"someId", type, "linkedRecordId"));
	}

	private void possiblyThrowException(String authToken, String type, String id, InputStream inputStream) {
		if("dummyNonAuthorizedToken".equals(authToken)){
			throw new AuthorizationException("not authorized");
		}
		if("image:123456789_NOT_FOUND".equals(id)){
			throw new RecordNotFoundException("No record exists with recordId: " + id);
		}

		if("not_child_of_binary_type".equals(type)){
			throw new MisuseException(
					"It is only possible to upload files to recordTypes that are children of binary");
		}

		if(inputStream == null){
			throw new DataMissingException("No stream to store");
		}
	}

}
