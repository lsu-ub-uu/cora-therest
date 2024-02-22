/*
 * Copyright 2015 Uppsala University Library
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

package se.uu.ub.cora.therest.testdata;

import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.therest.coradata.DataAtomicSpy;
import se.uu.ub.cora.therest.coradata.DataGroupSpy;

public final class DataCreator {

	public static DataGroup createRecordWithNameInDataAndIdAndTypeAndLinkedRecordId(
			String nameInData, String id, String recordType, String linkedRecordId) {
		DataGroup record = new DataGroupSpy(nameInData);
		DataGroup createRecordInfo = createRecordInfoWithIdAndTypeAndLinkedRecordId(id, recordType,
				linkedRecordId);
		record.addChild(createRecordInfo);
		return record;
	}

	public static DataGroup createRecordInfoWithIdAndTypeAndLinkedRecordId(String id,
			String recordType, String linkedRecordId) {
		DataGroup createRecordInfo = new DataGroupSpy("recordInfo");
		createRecordInfo.addChild(new DataAtomicSpy("id", id));
		DataGroup type = new DataGroupSpy("type");
		type.addChild(new DataAtomicSpy("linkedRecordType", "recordType"));
		type.addChild(new DataAtomicSpy("linkedRecordId", recordType));
		createRecordInfo.addChild(type);
		createRecordInfo.addChild(new DataAtomicSpy("createdBy", "12345"));

		DataGroup dataDivider = createDataDividerWithLinkedRecordId(linkedRecordId);
		createRecordInfo.addChild(dataDivider);
		return createRecordInfo;
	}

	public static DataGroup createDataDividerWithLinkedRecordId(String linkedRecordId) {
		DataGroup dataDivider = new DataGroupSpy("dataDivider");
		dataDivider.addChild(new DataAtomicSpy("linkedRecordType", "system"));
		dataDivider.addChild(new DataAtomicSpy("linkedRecordId", linkedRecordId));
		return dataDivider;
	}
}
