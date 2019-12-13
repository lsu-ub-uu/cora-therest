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

import se.uu.ub.cora.data.Action;
import se.uu.ub.cora.data.DataAtomic;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataResourceLink;
import se.uu.ub.cora.therest.data.DataAtomicSpy;
import se.uu.ub.cora.therest.data.DataGroupSpy;
import se.uu.ub.cora.therest.data.DataResourceLinkSpy;
import se.uu.ub.cora.therest.data.RestDataAtomic;
import se.uu.ub.cora.therest.data.RestDataGroup;
import se.uu.ub.cora.therest.record.DataRecordLinkCollectorSpy;

public final class DataCreator {
	private static final String SELF_PRESENTATION_VIEW_ID = "selfPresentationViewId";
	private static final String USER_SUPPLIED_ID = "userSuppliedId";
	private static final String SEARCH_PRESENTATION_FORM_ID = "searchPresentationFormId";
	private static final String SEARCH_METADATA_ID = "searchMetadataId";
	private static final String LIST_PRESENTATION_VIEW_ID = "listPresentationViewId";
	private static final String NEW_PRESENTATION_FORM_ID = "newPresentationFormId";
	private static final String PRESENTATION_FORM_ID = "presentationFormId";
	private static final String PRESENTATION_VIEW_ID = "presentationViewId";
	private static final String METADATA_ID = "metadataId";
	private static final String NEW_METADATA_ID = "newMetadataId";
	private static final String RECORD_TYPE = "recordType";

	public static DataGroup createRecordTypeWithIdAndUserSuppliedIdAndAbstract(String id,
			String userSuppliedId, String abstractValue) {
		return createRecordTypeWithIdAndUserSuppliedIdAndAbstractAndParentId(id, userSuppliedId,
				abstractValue, null);
	}

	private static DataGroup createRecordTypeWithIdAndUserSuppliedIdAndAbstractAndParentId(
			String id, String userSuppliedId, String abstractValue, String parentId) {
		String idWithCapitalFirst = id.substring(0, 1).toUpperCase() + id.substring(1);

		DataGroup dataGroup = new DataGroupSpy(RECORD_TYPE);
		dataGroup.addChild(createRecordInfoWithRecordTypeAndRecordId(RECORD_TYPE, id));
		dataGroup.addChild(
				createChildWithNameInDataLinkedTypeLinkedId(METADATA_ID, "metadataGroup", id));
		dataGroup.addChild(createChildWithNameInDataLinkedTypeLinkedId(PRESENTATION_VIEW_ID,
				"presentationGroup", "pg" + idWithCapitalFirst + "View"));
		dataGroup.addChild(createChildWithNameInDataLinkedTypeLinkedId(PRESENTATION_FORM_ID,
				"presentationGroup", "pg" + idWithCapitalFirst + "Form"));
		dataGroup.addChild(createChildWithNameInDataLinkedTypeLinkedId(NEW_METADATA_ID,
				"metadataGroup", id + "New"));
		dataGroup.addChild(createChildWithNameInDataLinkedTypeLinkedId(NEW_PRESENTATION_FORM_ID,
				"presentationGroup", "pg" + idWithCapitalFirst + "FormNew"));
		dataGroup.addChild(createChildWithNameInDataLinkedTypeLinkedId(LIST_PRESENTATION_VIEW_ID,
				"presentationGroup", "pg" + idWithCapitalFirst + "List"));
		dataGroup.addChild(new DataAtomicSpy(SEARCH_METADATA_ID, id + "Search"));
		dataGroup.addChild(new DataAtomicSpy(SEARCH_PRESENTATION_FORM_ID,
				"pg" + idWithCapitalFirst + "SearchForm"));

		dataGroup.addChild(new DataAtomicSpy(USER_SUPPLIED_ID, userSuppliedId));
		dataGroup.addChild(
				new DataAtomicSpy(SELF_PRESENTATION_VIEW_ID, "pg" + idWithCapitalFirst + "Self"));
		dataGroup.addChild(new DataAtomicSpy("abstract", abstractValue));
		if (null != parentId) {
			dataGroup.addChild(createChildWithNameInDataLinkedTypeLinkedId("parentId", "recordType",
					parentId));
			dataGroup.addChild(new DataAtomicSpy("parentId", parentId));
		}
		return dataGroup;
	}

	private static DataGroup createChildWithNameInDataLinkedTypeLinkedId(String nameInData,
			String linkedRecordType, String linkedRecordId) {
		DataGroup child = new DataGroupSpy(nameInData);
		child.addChild(new DataAtomicSpy("linkedRecordType", linkedRecordType));
		child.addChild(new DataAtomicSpy("linkedRecordId", linkedRecordId));
		return child;
	}

	public static DataGroup createRecordTypeWithIdAndUserSuppliedIdAndParentId(String id,
			String userSuppliedId, String parentId) {
		return createRecordTypeWithIdAndUserSuppliedIdAndAbstractAndParentId(id, userSuppliedId,
				"false", parentId);
	}

	public static DataGroup createRecordInfoWithRecordTypeAndRecordId(String recordType,
			String recordId) {
		DataGroup recordInfo = new DataGroupSpy("recordInfo");
		DataGroup type = new DataGroupSpy("type");
		type.addChild(new DataAtomicSpy("linkedRecordType", "recordType"));
		type.addChild(new DataAtomicSpy("linkedRecordId", recordType));
		recordInfo.addChild(type);

		recordInfo.addChild(new DataAtomicSpy("id", recordId));
		return recordInfo;
	}

	public static DataGroup createRecordWithNameInDataAndIdAndLinkedRecordId(String nameInData,
			String id, String linkedRecordId) {
		DataGroup record = new DataGroupSpy(nameInData);
		DataGroup createRecordInfo = createRecordInfoWithIdAndLinkedRecordId(id, linkedRecordId);
		record.addChild(createRecordInfo);
		return record;
	}

	public static DataGroup createRecordWithNameInDataAndIdAndTypeAndLinkedRecordId(
			String nameInData, String id, String recordType, String linkedRecordId) {
		DataGroup record = new DataGroupSpy(nameInData);
		DataGroup createRecordInfo = createRecordInfoWithIdAndTypeAndLinkedRecordId(id, recordType,
				linkedRecordId);
		record.addChild(createRecordInfo);
		return record;
	}

	public static DataGroup createRecordInfoWithLinkedRecordId(String linkedRecordId) {
		DataGroup createRecordInfo = new DataGroupSpy("recordInfo");
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

	public static DataGroup createRecordWithNameInDataAndLinkedRecordId(String nameInData,
			String linkedRecordId) {
		DataGroup record = new DataGroupSpy(nameInData);
		DataGroup createRecordInfo = createRecordInfoWithLinkedRecordId(linkedRecordId);
		record.addChild(createRecordInfo);
		return record;
	}

	public static DataGroup createRecordInfoWithIdAndLinkedRecordId(String id,
			String linkedRecordId) {
		DataGroup createRecordInfo = new DataGroupSpy("recordInfo");
		createRecordInfo.addChild(new DataAtomicSpy("id", id));
		DataGroup dataDivider = createDataDividerWithLinkedRecordId(linkedRecordId);
		createRecordInfo.addChild(dataDivider);
		return createRecordInfo;
	}

	public static DataGroup createMetadataGroupWithOneChild() {
		DataGroup dataGroup = new DataGroupSpy("metadata");
		DataGroup recordInfo = new DataGroupSpy("recordInfo");
		recordInfo.addChild(new DataAtomicSpy("id", "testNewGroup"));
		DataGroup type = new DataGroupSpy("type");
		type.addChild(new DataAtomicSpy("linkedRecordType", "recordType"));
		type.addChild(new DataAtomicSpy("linkedRecordId", "metadataGroup"));
		recordInfo.addChild(type);

		recordInfo.addChild(createDataDividerWithLinkedRecordId("test"));

		dataGroup.addChild(recordInfo);

		dataGroup.addChild(createChildReference());

		return dataGroup;
	}

	private static DataGroup createChildReference() {
		DataGroup childReferences = new DataGroupSpy("childReferences");

		childReferences.addChild(createChildReference("childOne", "1", "1"));

		return childReferences;
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

	public static DataGroup createMetadataGroupWithTwoChildren() {
		DataGroup dataGroup = new DataGroupSpy("metadata");
		DataGroup recordInfo = new DataGroupSpy("recordInfo");
		recordInfo.addChild(new DataAtomicSpy("id", "testNewGroup"));
		DataGroup type = new DataGroupSpy("type");
		type.addChild(new DataAtomicSpy("linkedRecordType", "recordType"));
		type.addChild(new DataAtomicSpy("linkedRecordId", "metadataGroup"));
		recordInfo.addChild(type);

		recordInfo.addChild(createDataDividerWithLinkedRecordId("test"));

		dataGroup.addChild(recordInfo);

		dataGroup.addChild(createChildReferences());

		return dataGroup;
	}

	private static DataGroup createChildReferences() {
		DataGroup childReferences = new DataGroupSpy("childReferences");

		childReferences.addChild(createChildReference("childOne", "1", "1"));
		childReferences.addChild(createChildReference("childTwo", "0", "2"));

		return childReferences;
	}

	private static DataGroup createChildReference(String ref, String repeatMin, String repeatMax) {
		DataGroup childReference = new DataGroupSpy("childReference");

		DataGroup refGroup = new DataGroupSpy("ref");
		refGroup.addAttributeByIdWithValue("type", "group");
		refGroup.addChild(new DataAtomicSpy("linkedRecordType", "metadataGroup"));
		refGroup.addChild(new DataAtomicSpy("linkedRecordId", ref));
		childReference.addChild(refGroup);

		DataAtomic repeatMinAtomic = new DataAtomicSpy("ref", repeatMin);
		childReference.addChild(repeatMinAtomic);

		DataAtomic repeatMaxAtomic = new DataAtomicSpy("ref", repeatMax);
		childReference.addChild(repeatMaxAtomic);

		return childReference;
	}

	public static DataGroup createMetadataGroupWithThreeChildren() {
		DataGroup dataGroup = createMetadataGroupWithTwoChildren();
		DataGroup childReferences = (DataGroup) dataGroup
				.getFirstChildWithNameInData("childReferences");
		childReferences.addChild(createChildReference("childThree", "1", "1"));

		return dataGroup;
	}

	public static DataRecordLinkCollectorSpy getDataRecordLinkCollectorSpyWithCollectedLinkAdded() {
		DataGroup recordToRecordLink = createDataForRecordToRecordLink();

		DataRecordLinkCollectorSpy linkCollector = new DataRecordLinkCollectorSpy();
		linkCollector.collectedDataLinks.addChild(recordToRecordLink);
		return linkCollector;
	}

	public static DataGroup createDataForRecordToRecordLink() {
		DataGroup recordToRecordLink = new DataGroupSpy("recordToRecordLink");

		DataGroup from = new DataGroupSpy("from");
		from.addChild(new DataAtomicSpy("linkedRecordType", "dataWithLinks"));
		from.addChild(new DataAtomicSpy("linkedRecordId", "someId"));

		recordToRecordLink.addChild(from);

		DataGroup to = new DataGroupSpy("to");
		to.addChild(new DataAtomicSpy("linkedRecordType", "toRecordType"));
		to.addChild(new DataAtomicSpy("linkedRecordId", "toRecordId"));
		to.addChild(to);

		recordToRecordLink.addChild(to);
		return recordToRecordLink;
	}

	public static DataGroup createMetadataGroupWithCollectionVariableAsChild() {
		DataGroup dataGroup = new DataGroupSpy("metadata");
		DataGroup recordInfo = new DataGroupSpy("recordInfo");
		recordInfo.addChild(new DataAtomicSpy("id", "testCollectionVar"));
		DataGroup type = new DataGroupSpy("type");
		type.addChild(new DataAtomicSpy("linkedRecordType", "recordType"));
		type.addChild(new DataAtomicSpy("linkedRecordId", "collectionVariable"));
		recordInfo.addChild(type);

		recordInfo.addChild(createDataDividerWithLinkedRecordId("test"));
		dataGroup.addChild(recordInfo);

		DataGroup refCollection = new DataGroupSpy("refCollection");
		refCollection.addChild(new DataAtomicSpy("linkedRecordType", "metadataItemCollection"));
		refCollection.addChild(new DataAtomicSpy("linkedRecordId", "testItemCollection"));
		dataGroup.addChild(refCollection);

		return dataGroup;
	}

	public static DataGroup createMetadataGroupWithRecordLinkAsChild() {
		DataGroup dataGroup = new DataGroupSpy("metadata");
		DataGroup recordInfo = new DataGroupSpy("recordInfo");
		recordInfo.addChild(new DataAtomicSpy("id", "testRecordLink"));
		DataGroup type = new DataGroupSpy("type");
		type.addChild(new DataAtomicSpy("linkedRecordType", "recordType"));
		type.addChild(new DataAtomicSpy("linkedRecordId", "recordLink"));
		recordInfo.addChild(type);

		recordInfo.addChild(createDataDividerWithLinkedRecordId("test"));
		dataGroup.addChild(recordInfo);

		return dataGroup;
	}

	public static DataResourceLink createResourceLinkMaster() {
		DataResourceLink master = new DataResourceLinkSpy("master");
		master.addChild(new DataAtomicSpy("streamId", "aStreamId"));
		master.addChild(new DataAtomicSpy("filename", "aFilename"));
		master.addChild(new DataAtomicSpy("filesize", "1234"));
		master.addChild(new DataAtomicSpy("mimeType", "application/tiff"));
		master.addAction(Action.READ);
		return master;
	}

	public static RestDataGroup createWorkOrder() {
		RestDataGroup workOrder = RestDataGroup.withNameInData("workOrder");

		RestDataGroup recordTypeLink = RestDataGroup.withNameInData("recordType");
		recordTypeLink
				.addChild(RestDataAtomic.withNameInDataAndValue("linkedRecordType", "recordType"));
		recordTypeLink.addChild(RestDataAtomic.withNameInDataAndValue("linkedRecordId", "person"));
		workOrder.addChild(recordTypeLink);

		workOrder.addChild(RestDataAtomic.withNameInDataAndValue("recordId", "personOne"));
		workOrder.addChild(RestDataAtomic.withNameInDataAndValue("type", "index"));
		return workOrder;
	}
}
